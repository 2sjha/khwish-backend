package com.khwish.backend.services;

import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.models.Event;
import com.khwish.backend.models.User;
import com.khwish.backend.models.Goal;
import com.khwish.backend.repositories.EventRepository;
import com.khwish.backend.repositories.UserRepository;
import com.khwish.backend.repositories.GoalRepository;
import com.khwish.backend.requests.goals.AddGoalRequest;
import com.khwish.backend.requests.goals.CloseGoalRequest;
import com.khwish.backend.requests.goals.ModifyGoalRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.responses.goals.GoalDetailsResponse;
import com.khwish.backend.responses.web.GoalThanksResponse;
import com.khwish.backend.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoalService {

    private GoalRepository goalRepo;
    private EventRepository eventRepo;
    private UserRepository userRepo;

    @Autowired
    public GoalService(GoalRepository goalRepo, EventRepository eventRepo, UserRepository userRepo) {
        this.goalRepo = goalRepo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    public BaseResponse addNewGoal(AddGoalRequest newGoalRequest, UUID userId) {
        if (newGoalRequest.getEventId() == null || StringUtils.isEmpty(newGoalRequest.getName())) {
            return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        } else if (newGoalRequest.getCollectedAmount() == null) {
            newGoalRequest.setCollectedAmount(0);
        } else if (newGoalRequest.getTotalAmount() == null) {
            newGoalRequest.setCollectedAmount(0);
        }
        Optional<Event> event = eventRepo.findById(newGoalRequest.getEventId());
        if (event.isPresent()) {
            Goal newGoal = new Goal(userId, newGoalRequest.getEventId(), newGoalRequest.getName(), newGoalRequest.getDescription(),
                    newGoalRequest.getCollectedAmount(), newGoalRequest.getTotalAmount());
            goalRepo.save(newGoal);
            return new BaseResponse(true, ResponseStrings.GOAL_CREATED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(true, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public BaseResponse getGoalDetails(UUID goalId) {
        Optional<Goal> existingGoal = goalRepo.findById(goalId);
        if (existingGoal.isPresent()) {
            Goal goal = existingGoal.get();
            return new GoalDetailsResponse(goal.getId(), goal.getName(), goal.getDescription(), goal.getCollectedAmount(), goal.getTotalAmount(),
                    DateTimeUtil.getEpochFromDate(goal.getCreatedAt()));
        } else {
            return new BaseResponse(false, ResponseStrings.GOAL_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public BaseResponse modifyGoal(ModifyGoalRequest changedGoalRequest) {
        UUID goalId = changedGoalRequest.getGoalId();

        Optional<Goal> existingGoal = goalRepo.findById(goalId);
        if (existingGoal.isPresent()) {
            if (changedGoalRequest.getName() != null) {
                existingGoal.get().setName(changedGoalRequest.getName());
            }
            if (changedGoalRequest.getDescription() != null) {
                existingGoal.get().setDescription(changedGoalRequest.getDescription());
            }
            if (changedGoalRequest.getCollectedAmount() != null) {
                existingGoal.get().setCollectedAmount(changedGoalRequest.getCollectedAmount());
            }
            if (changedGoalRequest.getTotalAmount() != null) {
                existingGoal.get().setTotalAmount(changedGoalRequest.getTotalAmount());
            }
            goalRepo.save(existingGoal.get());
            return new BaseResponse(true, ResponseStrings.GOAL_MODIFIED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(false, ResponseStrings.GOAL_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public BaseResponse closeGoal(CloseGoalRequest closeGoalRequest) {
        UUID goalId = closeGoalRequest.getGoalId();

        Optional<Goal> toCloseGoal = goalRepo.findById(goalId);
        if (toCloseGoal.isPresent()) {
            toCloseGoal.get().setClosed(true);
            goalRepo.save(toCloseGoal.get());
            return new BaseResponse(true, ResponseStrings.GOAL_CLOSED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(false, ResponseStrings.GOAL_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public BaseResponse getGoalThanks(UUID goalId) {
        Optional<Goal> existingGoal = goalRepo.findById(goalId);
        if (existingGoal.isPresent()) {
            Goal goal = existingGoal.get();
            UUID userId = goal.getUserId();
            Optional<User> foundUser = userRepo.findById(userId);
            if (foundUser.isPresent()) {
                return new GoalThanksResponse(true, ResponseStrings.GOAL_DETAILS_FETCHED, HttpServletResponse.SC_OK,
                        goal.getName(), goal.getEventId(), foundUser.get().getName());
            } else {
                return new BaseResponse(false, ResponseStrings.OOPS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            return new BaseResponse(false, ResponseStrings.GOAL_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }
}