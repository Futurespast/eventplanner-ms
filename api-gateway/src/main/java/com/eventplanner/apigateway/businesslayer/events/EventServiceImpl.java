package com.eventplanner.apigateway.businesslayer.events;

import com.eventplanner.apigateway.datamapperlayer.events.EventResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.events.EventServiceClient;
import com.eventplanner.apigateway.presentationlayer.events.EventRequestModel;
import com.eventplanner.apigateway.presentationlayer.events.EventResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventResponseMapper eventResponseMapper;
    private final EventServiceClient eventServiceClient;

    public EventServiceImpl(EventResponseMapper eventResponseMapper, EventServiceClient eventServiceClient) {
        this.eventResponseMapper = eventResponseMapper;
        this.eventServiceClient = eventServiceClient;
    }

    @Override
    public List<EventResponseModel> getAllEvents(String customerId) {
        return eventResponseMapper.entityToEventResponseList(eventServiceClient.getEvents(customerId));
    }

    @Override
    public EventResponseModel getEventById(String customerId, String eventId) {
        return eventResponseMapper.entityToEventResponseModel(eventServiceClient.getEventByEventId(customerId,eventId));
    }

    @Override
    public EventResponseModel addEvent(EventRequestModel eventRequestModel, String customerId) {
        return eventResponseMapper.entityToEventResponseModel(eventServiceClient.addEvent(eventRequestModel, customerId));
    }

    @Override
    public void updateEvent(EventRequestModel eventRequestModel, String customerId, String eventId) {
            eventServiceClient.updateEvent(eventRequestModel, customerId, eventId);
    }

    @Override
    public void deleteEvent(String customerId, String eventId) {
        eventServiceClient.deleteEvent(customerId, eventId);
    }
}
