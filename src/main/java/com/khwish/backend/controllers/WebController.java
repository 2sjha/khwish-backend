package com.khwish.backend.controllers;

import com.khwish.backend.instamojo.InstamojoService;
import com.khwish.backend.requests.instamojo.CreatePaymentRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.EventService;
import com.khwish.backend.services.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/web")
@CrossOrigin("https://khwish-web.herokuapp.com")
public class WebController {

    private EventService eventService;
    private InstamojoService instamojoService;
    private GoalService goalService;

    @Autowired
    public WebController(EventService eventService, InstamojoService instamojoService, GoalService goalService) {
        this.eventService = eventService;
        this.instamojoService = instamojoService;
        this.goalService = goalService;
    }

    @GetMapping("/event-details")
    private BaseResponse eventDetails(@RequestParam("event-id") UUID eventId, HttpServletResponse response) {
        BaseResponse result = eventService.getEventDetails(eventId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/payment-request")
    private BaseResponse createPaymentRequest(@RequestBody CreatePaymentRequest createPaymentRequest, HttpServletResponse response) {
        BaseResponse result = instamojoService.createPaymentRequest(createPaymentRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @GetMapping("/thanks")
    private BaseResponse thanks(@RequestParam("goal-id") UUID goalId, HttpServletResponse response) {
        BaseResponse result = goalService.getGoalThanks(goalId);
        response.setStatus(result.getStatusCode());
        return result;
    }
}
