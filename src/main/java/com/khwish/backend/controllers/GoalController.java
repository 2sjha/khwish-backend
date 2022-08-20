package com.khwish.backend.controllers;

import com.khwish.backend.requests.goals.AddGoalRequest;
import com.khwish.backend.requests.goals.CloseGoalRequest;
import com.khwish.backend.requests.goals.ModifyGoalRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/goals")
public class GoalController {

    GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/details")
    private BaseResponse goalDetails(@RequestAttribute("user-id") UUID userId, @RequestParam("goal-id") UUID goalId,
                                      HttpServletResponse response) {
        BaseResponse result = goalService.getGoalDetails(goalId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/add")
    private BaseResponse addGoal(@RequestAttribute("user-id") UUID userId, @RequestBody AddGoalRequest newGoalRequest,
                                  HttpServletResponse response) {
        BaseResponse result = goalService.addNewGoal(newGoalRequest, userId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/modify")
    private BaseResponse modifyGoal(@RequestAttribute("user-id") UUID userId, @RequestBody ModifyGoalRequest modifyGoalRequest,
                                     HttpServletResponse response) {
        BaseResponse result = goalService.modifyGoal(modifyGoalRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/close")
    private BaseResponse closeGoal(@RequestAttribute("user-id") UUID userId, @RequestBody CloseGoalRequest closeGoalRequest,
                                    HttpServletResponse response) {
        BaseResponse result = goalService.closeGoal(closeGoalRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }
}