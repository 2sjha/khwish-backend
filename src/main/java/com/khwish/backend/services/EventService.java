package com.khwish.backend.services;

import com.khwish.backend.constants.Constants;
import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.models.Contribution;
import com.khwish.backend.models.Event;
import com.khwish.backend.models.Goal;
import com.khwish.backend.repositories.ContributionRepository;
import com.khwish.backend.repositories.EventRepository;
import com.khwish.backend.repositories.GoalRepository;
import com.khwish.backend.requests.events.AddEventRequest;
import com.khwish.backend.requests.events.CloseEventRequest;
import com.khwish.backend.requests.events.ModifyEventRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.responses.events.EventContributorsResponse;
import com.khwish.backend.responses.events.EventDetailsResponse;
import com.khwish.backend.responses.goals.GoalDetailsResponse;
import com.khwish.backend.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepo;
    private final GoalRepository goalRepo;
    private final ContributionRepository contributionRepo;

    @Autowired
    public EventService(EventRepository eventRepo, GoalRepository goalRepo, ContributionRepository contributionRepo) {
        this.eventRepo = eventRepo;
        this.goalRepo = goalRepo;
        this.contributionRepo = contributionRepo;
    }

    public BaseResponse getEventDetails(UUID eventId) {
        Optional<Event> existingEvent = eventRepo.findById(eventId);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            return getEventDetailsResponseFromEvent(event);
        } else {
            return new BaseResponse(false, ResponseStrings.EVENT_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    EventDetailsResponse getEventDetailsResponseFromEvent(Event event) {
        List<Goal> goals = goalRepo.findAllByEventId(event.getId());
        ArrayList<GoalDetailsResponse> goalsDetails = new ArrayList<>();
        int eventCollectedTotal = 0;
        int eventTotal = 0;

        if (goals != null) {
            for (Goal goal : goals) {
                if (!goal.isClosed()) {
                    GoalDetailsResponse goalDetail = new GoalDetailsResponse(goal.getId(), goal.getName(), goal.getDescription(),
                            goal.getCollectedAmount(), goal.getTotalAmount(), DateTimeUtil.getEpochFromDate(goal.getCreatedAt()));
                    goalsDetails.add(goalDetail);

                    eventCollectedTotal += goal.getCollectedAmount();
                    eventTotal += goal.getTotalAmount();
                }
            }
        }

        return new EventDetailsResponse(true, ResponseStrings.EVENT_DETAILS_FETCHED, HttpServletResponse.SC_OK,
                event.getId(), event.getName(), event.getDescription(), event.getEventDate(),
                event.getLocationLat(), event.getLocationLong(), eventCollectedTotal, eventTotal,
                Constants.getEventInviteMessage(event), DateTimeUtil.getEpochFromDate(event.getCreatedAt()), goalsDetails);
    }

    public BaseResponse getEventContributors(UUID eventId) {
        List<Goal> goalsByEventId = goalRepo.findAllByEventId(eventId);

        if (goalsByEventId != null && !goalsByEventId.isEmpty()) {
            List<UUID> goalIds = goalsByEventId.stream().map(Goal::getId).collect(Collectors.toList());
            Map<UUID, String> goalsIdNameMap = goalsByEventId.stream().collect(Collectors.toMap(Goal::getId, Goal::getName));
            List<Contribution> eventContributions = contributionRepo.findAllByGoalIdIn(goalIds);

            if (eventContributions != null && !eventContributions.isEmpty()) {
                ArrayList<EventContributorsResponse.ContributionResponse> contributionResponses = new ArrayList<>();
                for (Contribution contribution : eventContributions) {
                    contributionResponses.add(new EventContributorsResponse.ContributionResponse(contribution.getContributorName(),
                            contribution.getAmount(), goalsIdNameMap.get(contribution.getGoalId()),
                            DateTimeUtil.getEpochFromDate(contribution.getCreatedAt())));
                }
                return new EventContributorsResponse(true, ResponseStrings.EVENT_CONTRIBUTIONS_FETCHED, HttpServletResponse.SC_OK, contributionResponses);
            } else {
                return new EventContributorsResponse(true, ResponseStrings.NO_CONTRIBUTIONS_YET, HttpServletResponse.SC_OK, null);
            }
        } else {
            return new BaseResponse(true, ResponseStrings.NO_GOALS_FOUND, HttpServletResponse.SC_OK);
        }
    }

    public EventDetailsResponse addNewEvent(AddEventRequest newEventRequest, UUID userId) {
        Event newEvent = new Event(userId, newEventRequest.getName(), newEventRequest.getDescription(),
                newEventRequest.getLocationLat(), newEventRequest.getLocationLong(), newEventRequest.getEventDate());
        eventRepo.save(newEvent);
        return new EventDetailsResponse(true, ResponseStrings.EVENT_CREATED, HttpServletResponse.SC_OK,
                newEvent.getId(), newEvent.getName(), newEvent.getDescription(), newEvent.getEventDate(),
                newEvent.getLocationLat(), newEvent.getLocationLong(), 0, 0,
                Constants.getEventInviteMessage(newEvent), DateTimeUtil.getEpochFromDate(newEvent.getCreatedAt()), null);
    }

    public BaseResponse modifyEvent(ModifyEventRequest changedEventRequest) {
        UUID eventId = changedEventRequest.getEventId();

        Optional<Event> existingEvent = eventRepo.findById(eventId);
        if (existingEvent.isPresent()) {
            if (changedEventRequest.getName() != null) {
                existingEvent.get().setName(changedEventRequest.getName());
            }
            if (changedEventRequest.getDescription() != null) {
                existingEvent.get().setDescription(changedEventRequest.getDescription());
            }
            if (changedEventRequest.getEventDate() != null) {
                existingEvent.get().setEventDate(changedEventRequest.getEventDate());
            }
            if (changedEventRequest.getLocationLat() != null || changedEventRequest.getLocationLong() != null) {
                existingEvent.get().setLocationLat(changedEventRequest.getLocationLat());
                existingEvent.get().setLocationLong(changedEventRequest.getLocationLong());
            }
            eventRepo.save(existingEvent.get());
            return new BaseResponse(true, ResponseStrings.EVENT_MODIFIED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(false, ResponseStrings.EVENT_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public BaseResponse closeEvent(CloseEventRequest closeEventRequest) {
        UUID eventId = closeEventRequest.getEventId();

        Optional<Event> toCloseEvent = eventRepo.findById(eventId);
        if (toCloseEvent.isPresent()) {
            toCloseEvent.get().setClosed(true);
            List<Goal> toCloseGoals = goalRepo.findAllByEventId(eventId);
            for (Goal goal : toCloseGoals) {
                goal.setClosed(true);
            }
            eventRepo.save(toCloseEvent.get());
            goalRepo.saveAll(toCloseGoals);
            return new BaseResponse(true, ResponseStrings.EVENT_CLOSED, HttpServletResponse.SC_OK);
        } else {
            return new BaseResponse(false, ResponseStrings.EVENT_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
        }
    }
}