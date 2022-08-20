package com.khwish.backend.services;

import com.khwish.backend.constants.Constants;
import com.khwish.backend.notifications.EmailSender;
import com.khwish.backend.google.FirebaseService;
import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.models.Contribution;
import com.khwish.backend.models.Event;
import com.khwish.backend.models.User;
import com.khwish.backend.models.Goal;
import com.khwish.backend.repositories.ContributionRepository;
import com.khwish.backend.repositories.EventRepository;
import com.khwish.backend.repositories.UserRepository;
import com.khwish.backend.repositories.GoalRepository;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.impl.ConcurrentExecutorServiceImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class GiftService {

    private final GoalRepository goalRepo;
    private final EventRepository eventRepo;
    private final ContributionRepository contributionRepo;
    private final UserRepository userRepo;
    private final WalletService walletService;
    private final EmailSender emailSender;
    private final ConcurrentExecutorServiceImpl concurrentExecutorService;

    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private final String classTag = this.getClass().getSimpleName();

    @Autowired
    public GiftService(GoalRepository goalRepo, EventRepository eventRepo, ContributionRepository contributionRepo,
                       UserRepository userRepo, WalletService walletService, EmailSender emailSender,
                       ConcurrentExecutorServiceImpl concurrentExecutorService) {
        this.goalRepo = goalRepo;
        this.eventRepo = eventRepo;
        this.contributionRepo = contributionRepo;
        this.userRepo = userRepo;
        this.walletService = walletService;
        this.emailSender = emailSender;
        this.concurrentExecutorService = concurrentExecutorService;
    }

    public BaseResponse registerWebHook(Double amount, String contributorEmail, String contributorName,
                                        String contributorPhoneNumber, String currency, Double fees, String longUrl, String mac,
                                        String paymentId, String paymentRequestId, String purpose, String shortUrl,
                                        String status) {
        LOGGER.log(Level.INFO, "Web-hook received, purpose:  " + purpose + ", amount: " + amount + ", contributorEmail: "
                + contributorEmail + ", contributorName: " + contributorName + ", contributorPhoneNumber: "
                + contributorPhoneNumber + ", currency: " + currency + ", fees: " + fees + ", longUrl: " + longUrl + ", mac: "
                + mac + ", paymentId: " + paymentId + ", paymentRequestId: " + paymentRequestId + ", shortUrl: " + shortUrl
                + ", status: " + status);

        if (Constants.INSTAMOJO_PAYMENT_SUCCESS_STATUS.equals(status)) {
            UUID goalId = getGoalIdFromPurpose(purpose);
            if (goalId != null) {
                if (amount <= 0 || contributorName == null) {
                    return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
                }

                Optional<Goal> existingGoal = goalRepo.findById(goalId);
                if (!existingGoal.isPresent()) {
                    return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    Goal goal = existingGoal.get();
                    double amountTillNow = goal.getCollectedAmount();
                    goal.setCollectedAmount(amount + amountTillNow);
                    goalRepo.save(goal);
                    Contribution contribution = new Contribution(contributorName, goalId, amount);
                    contributionRepo.save(contribution);
                    UUID userId = goal.getUserId();
                    walletService.addToWallet(userId, amount);

                    concurrentExecutorService.execute(() -> {
                        Optional<User> foundUser = userRepo.findById(userId);
                        if (foundUser.isPresent()) {
                            User user = foundUser.get();

                            sendNotificationToUser(user, contributorName, amount, goal);
                            sendEmailToContributor(user, contributorName, contributorEmail, amount, goal);
                        } else {
                            LOGGER.log(Level.ERROR, "[" + classTag + "][registerWebHook] User not found for userId: " + userId);
                        }
                    });
                }
            }
        } else {
            LOGGER.log(Level.INFO, "[" + classTag + "][registerWebHook] Webhook Payment Status: " + status
                    + ", Thus webhook processing rejected.");
        }
        return new BaseResponse(true, ResponseStrings.WEBHOOK_RECEIVED, HttpServletResponse.SC_OK);
    }

    private void sendEmailToContributor(User user, String contributorName, String contributorEmail, Double amount, Goal goal) {
        Optional<Event> foundEvent = eventRepo.findById(goal.getEventId());
        if (foundEvent.isPresent()) {
            String subject = user.getName() + " thanks you for your contribution";
            String body = "Hi " + contributorName + ",\n" +
                    user.getName() + " thanks you for your contribution of " + amount
                    + " to their goal " + goal.getName() + " in the event " + foundEvent.get().getName() + "."
                    + "\n\n\n\n\n\n\n\n" + "Use Khwish App to create your own event, and start sharing to collect money.";
            emailSender.sendEmail(contributorEmail, subject, body);
        }
    }

    private void sendNotificationToUser(User user, String contributorName, Double amount, Goal goal) {
        String notificationTitle = "Contribution received";
        String notificationText = "Contribution received of ₹" + amount + " from " + contributorName + " for " + goal.getName();

        String notificationToken = user.getNotificationToken();
        if (notificationToken != null) {
            FirebaseService.sendNotificationToUser(notificationToken, notificationTitle, notificationText);
        }
    }

    private UUID getGoalIdFromPurpose(String purpose) {
        if (purpose != null && purpose.contains("(")) {
            int startIndex = purpose.indexOf("(");
            int endIndex = purpose.indexOf(")");
            String goalIDStr = purpose.substring(startIndex + 1, endIndex);
            try {
                return UUID.fromString(goalIDStr);
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][getGoalIdFromPurpose] Error occurred: " + e.toString());
                return null;
            }
        } else {
            return null;
        }
    }
}