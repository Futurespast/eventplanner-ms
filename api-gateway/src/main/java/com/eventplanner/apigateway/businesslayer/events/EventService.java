package com.eventplanner.apigateway.businesslayer.events;

import com.eventplanner.apigateway.presentationlayer.events.EventRequestModel;
import com.eventplanner.apigateway.presentationlayer.events.EventResponseModel;

import java.util.List;

public interface EventService {
    List<EventResponseModel> getAllEvents(String customerId);
    EventResponseModel getEventById(String customerId, String eventId);
    EventResponseModel addEvent(EventRequestModel eventRequestModel, String customerId);

    void updateEvent(EventRequestModel eventRequestModel, String customerId, String eventId);

    void deleteEvent(String customerId, String eventId);

}
