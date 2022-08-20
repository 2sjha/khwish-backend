package com.khwish.backend.controllers;

import com.khwish.backend.requests.events.AddEventRequest;
import com.khwish.backend.requests.events.CloseEventRequest;
import com.khwish.backend.requests.events.ModifyEventRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@CrossOrigin("https://khwish-web.herokuapp.com")
public class EventController {

    EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/details")
    private BaseResponse eventDetails(@RequestAttribute("user-id") UUID userId, @RequestParam("event-id") UUID eventId,
                                      HttpServletResponse response) {
        BaseResponse result = eventService.getEventDetails(eventId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @GetMapping("/contributors")
    private BaseResponse eventContributors(@RequestAttribute("user-id") UUID userId, @RequestParam("event-id") UUID eventId,
                                      HttpServletResponse response) {
        BaseResponse result = eventService.getEventContributors(eventId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/add")
    private BaseResponse addEvent(@RequestAttribute("user-id") UUID userId, @RequestBody AddEventRequest newEventRequest,
                                  HttpServletResponse response) {
        BaseResponse result = eventService.addNewEvent(newEventRequest, userId);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/modify")
    private BaseResponse modifyEvent(@RequestAttribute("user-id") UUID userId, @RequestBody ModifyEventRequest modifyEventRequest,
                                     HttpServletResponse response) {
        BaseResponse result = eventService.modifyEvent(modifyEventRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }

    @PostMapping("/close")
    private BaseResponse closeEvent(@RequestAttribute("user-id") UUID userId, @RequestBody CloseEventRequest closeEventRequest,
                                    HttpServletResponse response) {
        BaseResponse result = eventService.closeEvent(closeEventRequest);
        response.setStatus(result.getStatusCode());
        return result;
    }
}