package com.khwish.backend.services;

import com.khwish.backend.auth.AuthConstants;
import com.khwish.backend.constants.Constants;
import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.google.FirebaseService;
import com.khwish.backend.google.GoogleSheetsService;
import com.khwish.backend.models.*;
import com.khwish.backend.notifications.SlackMessage;
import com.khwish.backend.repositories.*;
import com.khwish.backend.requests.users.*;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.responses.events.EventDetailsResponse;
import com.khwish.backend.responses.users.HomePageResponse;
import com.khwish.backend.responses.users.LoginResponse;
import com.khwish.backend.responses.users.UserProfileResponse;
import com.khwish.backend.responses.users.WalletActivitiesResponse;
import com.khwish.backend.utils.AuthUtil;
import com.khwish.backend.utils.DateTimeUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger LOGGER = LogManager.getLogger(this.getClass());
    private final String classTag = this.getClass().getSimpleName();
    private final UserRepository userRepo;
    private final AuthTokenRepository authRepo;
    private final EventRepository eventRepo;
    private final UserBankDetailsRepository userBankDetailsRepo;
    private final WithdrawalRepository withdrawalRepo;
    private final ContributionRepository contributionRepo;
    private final GoalRepository goalRepo;
    private final WalletService walletService;
    private final EventService eventService;

    @Autowired
    public UserService(UserRepository userRepo, AuthTokenRepository authRepo, EventRepository eventRepo,
                       UserBankDetailsRepository userBankDetailsRepo, WithdrawalRepository withdrawalRepo,
                       ContributionRepository contributionRepo, GoalRepository goalRepo,
                       WalletService walletService, EventService eventService) {
        this.userRepo = userRepo;
        this.authRepo = authRepo;
        this.eventRepo = eventRepo;
        this.userBankDetailsRepo = userBankDetailsRepo;
        this.withdrawalRepo = withdrawalRepo;
        this.contributionRepo = contributionRepo;
        this.goalRepo = goalRepo;
        this.walletService = walletService;
        this.eventService = eventService;
    }

    public BaseResponse login(UserLoginOAuthRequest userLoginRequest) {
        String uid = FirebaseService.verifyTokenAndGetUid(userLoginRequest.getIdToken());

        if (uid == null) {
            return new BaseResponse(false, ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            boolean searchByPhone;
            boolean searchByEmail;
            User foundUserByEmail = null;
            User foundUserByPhone = null;

            if (userLoginRequest.getPhoneNumber() == null && userLoginRequest.getEmail() == null) {
                return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
            } else if (userLoginRequest.getPhoneNumber() != null && userLoginRequest.getEmail() == null) {
                searchByPhone = true;
                searchByEmail = false;
            } else if (userLoginRequest.getPhoneNumber() == null && userLoginRequest.getEmail() != null) {
                searchByPhone = false;
                searchByEmail = true;
            } else {
                searchByEmail = true;
                searchByPhone = true;
            }

            // Search for User Params within existing users
            if (searchByPhone) {
                foundUserByPhone = userRepo.findByPhoneNumber(userLoginRequest.getPhoneNumber());
                if (foundUserByPhone != null) {
                    String token = AuthUtil.generateNewToken();
                    Long authExpiry = DateTimeUtil.getCurrentEpoch() + AuthConstants.AUTH_VALIDITY_TIME;
                    saveAccessToken(foundUserByPhone.getId(), token, authExpiry);

                    LOGGER.log(Level.INFO,
                            "[" + classTag + "][login] User logged in. User found found by phone number. " + foundUserByPhone
                                    .toString());
                    return new LoginResponse(true, ResponseStrings.USER_LOGGED_IN, HttpServletResponse.SC_OK,
                            foundUserByPhone.getId(), foundUserByPhone.getName(), false, token, authExpiry);
                }
            }

            if (searchByEmail) {
                foundUserByEmail = userRepo.findByEmail(userLoginRequest.getEmail());
                if (foundUserByEmail != null) {
                    String token = AuthUtil.generateNewToken();
                    Long authExpiry = DateTimeUtil.getCurrentEpoch() + AuthConstants.AUTH_VALIDITY_TIME;
                    saveAccessToken(foundUserByEmail.getId(), token, authExpiry);

                    LOGGER.log(Level.INFO,
                            "[" + classTag + "][login] User logged in. User found by email. " + foundUserByEmail.toString());
                    return new LoginResponse(true, ResponseStrings.USER_LOGGED_IN, HttpServletResponse.SC_OK,
                            foundUserByEmail.getId(), foundUserByEmail.getName(), false, token, authExpiry);
                }
            }


            // User not found, Create new User
            if ((searchByEmail || searchByPhone) && (foundUserByEmail == null && foundUserByPhone == null)) {
                String registeredVia;
                if (userLoginRequest.getPhoneNumber() != null) {
                    registeredVia = Constants.REGISTERED_VIA_PHONE_NUMBER;
                } else {
                    registeredVia = Constants.REGISTERED_VIA_EMAIL;
                }

                User newUser = new User(userLoginRequest.getName(), userLoginRequest.getPhoneNumber(),
                        userLoginRequest.getEmail(), registeredVia);
                userRepo.save(newUser);

                String token = AuthUtil.generateNewToken();
                Long authExpiry = DateTimeUtil.getCurrentEpoch() + AuthConstants.AUTH_VALIDITY_TIME;
                saveAccessToken(newUser.getId(), token, authExpiry);

                LOGGER.log(Level.INFO, "[" + classTag + "][login] New User logged in. " + newUser.toString());
                return new LoginResponse(true, ResponseStrings.USER_LOGGED_IN, HttpServletResponse.SC_OK,
                        newUser.getId(), newUser.getName(), true, token, authExpiry);
            }

            return new BaseResponse(false, ResponseStrings.OOPS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public BaseResponse onBoardUser(UserOnBoardingRequest userOnBoardingRequest) {
        Optional<User> foundUser = userRepo.findById(userOnBoardingRequest.getUserId());
        if (foundUser.isPresent()) {
            User user = foundUser.get();

            if (!StringUtils.isEmpty(userOnBoardingRequest.getName()) && (StringUtils.isEmpty(user.getName())
                    || (!StringUtils.isEmpty(user.getName()) && !user.getName().equals(userOnBoardingRequest.getName())))) {
                user.setName(userOnBoardingRequest.getName());
            }

            if (!Constants.REGISTERED_VIA_EMAIL.equals(user.getRegisteredVia())
                    // To Edit, User should not be registered vai this method
                    && !StringUtils.isEmpty(userOnBoardingRequest.getEmail()) // Input request data should obviously not be empty
                    && (StringUtils.isEmpty(user.getEmail()) // User email is empty
                    || (!StringUtils.isEmpty(user.getEmail()) && !user.getEmail().equals(userOnBoardingRequest
                    .getEmail())))) { // User email is not empty but there's a difference in input and current data
                user.setEmail(userOnBoardingRequest.getEmail());
            }
            if (!Constants.REGISTERED_VIA_PHONE_NUMBER.equals(user.getRegisteredVia()) && !StringUtils
                    .isEmpty(userOnBoardingRequest.getPhoneNumber())
                    && (StringUtils.isEmpty(user.getPhoneNumber())
                    || (!StringUtils.isEmpty(user.getPhoneNumber()) && !user.getPhoneNumber()
                                                                            .equals(userOnBoardingRequest.getPhoneNumber())))) {
                user.setPhoneNumber(userOnBoardingRequest.getPhoneNumber());
            }
            if (!StringUtils.isEmpty(userOnBoardingRequest.getDateOfBirth()) && (StringUtils.isEmpty(user.getDateOfBirth())
                    || (!StringUtils.isEmpty(user.getDateOfBirth()) && !user.getDateOfBirth()
                                                                            .equals(userOnBoardingRequest.getDateOfBirth())))) {
                user.setDateOfBirth(userOnBoardingRequest.getDateOfBirth());
            }
            userRepo.save(user);

            if (userOnBoardingRequest.getBankDetails() != null) {
                UserBankDetails userBankDetails = userBankDetailsRepo.findByUserId(user.getId());
                UserOnBoardingRequest.BankDetails onBoardingBankDetails = userOnBoardingRequest.getBankDetails();
                if (userBankDetails == null) {
                    userBankDetails = new UserBankDetails(user.getId(), onBoardingBankDetails.getUpiVpa(),
                            onBoardingBankDetails.getAccountNumber(), onBoardingBankDetails.getIfscCode(),
                            onBoardingBankDetails.getAccountHoldersName());
                } else {
                    if (onBoardingBankDetails.getUpiVpa() != null) {
                        if (StringUtils.isEmpty(userBankDetails.getUpiVpa()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getUpiVpa())
                                && Pattern.compile(Constants.UPI_REGEX_PATTERN).matcher(onBoardingBankDetails.getUpiVpa())
                                          .matches()) {
                            userBankDetails.setUpiVpa(onBoardingBankDetails.getUpiVpa());
                        } else if (!StringUtils.isEmpty(userBankDetails.getUpiVpa()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getUpiVpa())
                                && !userBankDetails.getUpiVpa().equals(onBoardingBankDetails.getUpiVpa())
                                && Pattern.compile(Constants.UPI_REGEX_PATTERN).matcher(onBoardingBankDetails.getUpiVpa())
                                          .matches()) {
                            userBankDetails.setUpiVpa(onBoardingBankDetails.getUpiVpa());
                        }
                    }

                    if (onBoardingBankDetails.getAccountNumber() != null) {
                        if (StringUtils.isEmpty(userBankDetails.getAccountNumber()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getAccountNumber())) {
                            userBankDetails.setAccountNumber(onBoardingBankDetails.getAccountNumber());
                        } else if (!StringUtils.isEmpty(userBankDetails.getAccountNumber()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getAccountNumber())
                                && !userBankDetails.getAccountNumber().equals(onBoardingBankDetails.getAccountNumber())) {
                            userBankDetails.setAccountNumber(onBoardingBankDetails.getAccountNumber());
                        }

                        if (StringUtils.isEmpty(userBankDetails.getIfscCode()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getIfscCode())) {
                            userBankDetails.setIfscCode(onBoardingBankDetails.getIfscCode());
                        } else if (!StringUtils.isEmpty(userBankDetails.getIfscCode()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getIfscCode())
                                && !userBankDetails.getIfscCode().equals(onBoardingBankDetails.getIfscCode())) {
                            userBankDetails.setIfscCode(onBoardingBankDetails.getIfscCode());
                        }

                        if (StringUtils.isEmpty(userBankDetails.getAccountHoldersName()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getAccountHoldersName())) {
                            userBankDetails.setAccountHoldersName(onBoardingBankDetails.getAccountHoldersName());
                        } else if (!StringUtils.isEmpty(userBankDetails.getAccountHoldersName()) && !StringUtils
                                .isEmpty(onBoardingBankDetails.getAccountHoldersName())
                                && !userBankDetails.getAccountHoldersName()
                                                   .equals(onBoardingBankDetails.getAccountHoldersName())) {
                            userBankDetails.setAccountHoldersName(onBoardingBankDetails.getAccountHoldersName());
                        }
                    }
                }
                userBankDetailsRepo.save(userBankDetails);
            }

            walletService.createWallet(userOnBoardingRequest.getUserId());
            return new BaseResponse(true, ResponseStrings.USER_ON_BOARDED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(false, ResponseStrings.USER_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public BaseResponse getHomePage(UUID userId) {
        HomePageResponse homePageResponse;
        List<Event> userEvents = eventRepo.findAllByUserId(userId);
        if (userEvents == null || userEvents.isEmpty()) {
            homePageResponse = new HomePageResponse(true, ResponseStrings.NO_EVENTS_FOUND, HttpServletResponse.SC_OK);
        } else {
            ArrayList<EventDetailsResponse> events = new ArrayList<>();

            for (Event event : userEvents) {
                if (!event.isClosed()) {
                    EventDetailsResponse eventDetailsResponse = eventService.getEventDetailsResponseFromEvent(event);
                    events.add(eventDetailsResponse);
                }
            }

            homePageResponse = new HomePageResponse(true, ResponseStrings.HOME_PAGE_FETCHED, HttpServletResponse.SC_OK);
            homePageResponse.setEvents(events);
        }
        homePageResponse.setWalletAmount(walletService.getWalletAmount(userId));
        return homePageResponse;
    }

    public BaseResponse getProfile(UUID userId) {
        Optional<User> foundUser = userRepo.findById(userId);
        if (!foundUser.isPresent()) {
            return new BaseResponse(true, ResponseStrings.USER_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        } else {
            User user = foundUser.get();
            UserProfileResponse.UserDetails userDetails = new UserProfileResponse.UserDetails(user.getName(), user.getEmail(),
                    user.getPhoneNumber(), user.getDateOfBirth(), user.getRegisteredVia());

            UserBankDetails userBankDetails = userBankDetailsRepo.findByUserId(userId);
            UserProfileResponse.BankDetails bankDetails = null;
            if (userBankDetails != null) {
                bankDetails = new UserProfileResponse.BankDetails(userBankDetails.getUpiVpa(), userBankDetails.getAccountNumber(),
                        userBankDetails.getIfscCode(), userBankDetails.getAccountHoldersName());
            }
            return new UserProfileResponse(true, ResponseStrings.USER_PROFILE_FETCHED, HttpServletResponse.SC_OK, userDetails,
                    bankDetails);
        }
    }

    public BaseResponse withdraw(UUID userId, WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest != null && withdrawalRequest.getAmount() > 0) {
            UserBankDetails userBankDetails = userBankDetailsRepo.findByUserId(userId);
            String userBankDetailsValidationText = validateUserBankDetailsForWithdrawal(userBankDetails,
                    withdrawalRequest.getMethod());
            if (userBankDetailsValidationText != null) {
                return new BaseResponse(false, userBankDetailsValidationText, HttpServletResponse.SC_NOT_FOUND);
            }

            boolean success = walletService.deductFromWallet(userId, withdrawalRequest.getAmount());
            if (success) {
                Withdrawal newWithdrawal = new Withdrawal(userId, withdrawalRequest.getAmount(), withdrawalRequest.getMethod());
                withdrawalRepo.save(newWithdrawal);

                Optional<User> foundUser = userRepo.findById(userId);
                foundUser.ifPresent(user -> {
                    SlackMessage
                            .sendWithdrawalRequestMessage(getWithdrawalSlackMessage(user.getName(), withdrawalRequest.getAmount(),
                                    withdrawalRequest.getMethod(), userBankDetails));
                    try {
                        GoogleSheetsService.makeWithdrawalRequestEntry(user.getName(), withdrawalRequest.getAmount(),
                                withdrawalRequest.getMethod(), userBankDetails.getUpiVpa(),
                                String.valueOf(userBankDetails.getAccountNumber()), userBankDetails.getIfscCode(),
                                userBankDetails.getAccountHoldersName());
                    } catch (IOException | GeneralSecurityException e) {
                        LOGGER.log(Level.ERROR, "[" + classTag + "][withdraw] " + e.toString(), e);
                    }
                });
                return new BaseResponse(true, ResponseStrings.WITHDRAWAL_SUCCESS, HttpServletResponse.SC_OK);
            } else {
                return new BaseResponse(false, ResponseStrings.WITHDRAWAL_FAILURE, HttpServletResponse.SC_CONFLICT);
            }
        } else {
            return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String validateUserBankDetailsForWithdrawal(UserBankDetails userBankDetails, String withdrawalMethod) {
        if (userBankDetails == null) {
            return ResponseStrings.BANK_DETAILS_NOT_PRESENT;
        }

        if (Constants.WITHDRAWAL_METHOD_UPI.equals(withdrawalMethod)) {
            if (userBankDetails.getUpiVpa() == null) {
                return ResponseStrings.WRONG_WITHDRAWAL_METHOD_CHOSEN;
            } else if (!Pattern.compile(Constants.UPI_REGEX_PATTERN).matcher(userBankDetails.getUpiVpa()).matches()) {
                return ResponseStrings.INVALID_BANK_DETAILS_UPI;
            } else {
                return null;
            }
        } else if (Constants.WITHDRAWAL_METHOD_BANK.equals(withdrawalMethod)) {
            if (userBankDetails.getAccountHoldersName() == null && userBankDetails.getAccountNumber() == null
                    && userBankDetails.getIfscCode() == null) {
                return ResponseStrings.WRONG_WITHDRAWAL_METHOD_CHOSEN;
            } else if (StringUtils.isEmpty(userBankDetails.getAccountNumber())
                    || String.valueOf(userBankDetails.getAccountNumber()).length() < 10) {
                return ResponseStrings.INVALID_BANK_DETAILS_ACCOUNT_NUMBER;
            } else if (StringUtils.isEmpty(userBankDetails.getAccountHoldersName())) {
                return ResponseStrings.INVALID_BANK_DETAILS_ACCOUNT_HOLDERS_NAME;
            } else if (StringUtils.isEmpty(userBankDetails.getIfscCode()) || userBankDetails.getIfscCode().length() < 10) {
                return ResponseStrings.INVALID_BANK_DETAILS_ACCOUNT_IFSC;
            } else {
                return null;
            }
        } else {
            return ResponseStrings.BAD_REQUEST;
        }
    }

    public BaseResponse getWalletActivities(UUID userId) {
        List<Withdrawal> withdrawals = withdrawalRepo.findAllByUserId(userId);
        List<Goal> goals = goalRepo.findAllByUserId(userId);
        ArrayList<WalletActivitiesResponse.WalletActivity> activities = new ArrayList<>();

        if (withdrawals != null && !withdrawals.isEmpty()) {
            for (Withdrawal withdrawal : withdrawals) {
                activities.add(new WalletActivitiesResponse.WalletActivity(Constants.NEGATIVE, withdrawal.getAmount(),
                        DateTimeUtil.getEpochFromDate(withdrawal.getCreatedAt()), withdrawal.getMethod()));
            }
        }

        if (goals != null && !goals.isEmpty()) {
            List<UUID> goalIds = goals.stream().map(Goal::getId).collect(Collectors.toList());
            List<Contribution> contributions = contributionRepo.findAllByGoalIdIn(goalIds);

            if (contributions != null && !contributions.isEmpty()) {
                Map<UUID, String> goalsIdNameMap = goals.stream().collect(Collectors.toMap(Goal::getId, Goal::getName));

                for (Contribution contribution : contributions) {
                    activities.add(new WalletActivitiesResponse.WalletActivity(Constants.POSITIVE, contribution.getAmount(),
                            DateTimeUtil.getEpochFromDate(contribution.getCreatedAt()),
                            goalsIdNameMap.get(contribution.getGoalId())));
                }
            }
        }

        return new WalletActivitiesResponse(true, ResponseStrings.WALLET_ACTIVITIES_FETCHED, HttpServletResponse.SC_OK,
                walletService.getWalletAmount(userId), activities);
    }

    public BaseResponse logout(UserLogoutRequest userLogoutRequest) {
        Optional<User> foundUser = userRepo.findById(userLogoutRequest.getUserId());

        foundUser.ifPresent(user -> authRepo.deleteByUserId(user.getId()));

        return new BaseResponse(true, ResponseStrings.USER_LOGGED_OUT, HttpServletResponse.SC_OK);
    }

    private void saveAccessToken(UUID userId, String token, Long authExpiry) {
        AuthToken authToken = authRepo.findByUserId(userId);
        if (authToken != null) {
            authToken.setToken(token);
            authToken.setExpiresAt(authExpiry);
        } else {
            authToken = new AuthToken(userId, token, authExpiry);
        }
        authRepo.save(authToken);
    }

    public BaseResponse saveUserNotificationToken(UUID userId, UserNotificationTokenRequest
            userNotificationTokenRequest) {

        if (userNotificationTokenRequest == null || StringUtils.isEmpty(userNotificationTokenRequest.getNotificationToken())) {
            return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        }

        String token = userNotificationTokenRequest.getNotificationToken();
        Optional<User> foundUser = userRepo.findById(userId);
        if (foundUser.isPresent()) {
            User user = foundUser.get();

            user.setNotificationToken(token);
            userRepo.save(user);
            return new BaseResponse(true, ResponseStrings.TOKEN_RECEIVED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(true, ResponseStrings.USER_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String getWithdrawalSlackMessage(String userName, double amount, String transferMethod,
                                             UserBankDetails userBankDetails) {
        String withdrawalMsg = userName + " request a withdrawal of amount ₹" + amount + " via " + transferMethod + ".\n";

        if (Constants.WITHDRAWAL_METHOD_UPI.equals(transferMethod)) {
            withdrawalMsg += "UPI Id: " + userBankDetails.getUpiVpa();
        } else {
            withdrawalMsg += "Account No. " + userBankDetails.getAccountNumber() + ", IFSC Code: " + userBankDetails.getIfscCode()
                    + ", Account Holder's Name: " + userBankDetails.getAccountHoldersName();
        }
        return withdrawalMsg;
    }
}