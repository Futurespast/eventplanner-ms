package com.eventplanner.events.businesslayer;

import com.eventplanner.events.presentationlayer.EventRequestModel;
import com.eventplanner.events.presentationlayer.EventResponseModel;


import java.util.List;

public interface EventService {
    List<EventResponseModel> getEvents(String customerId);
    EventResponseModel getEventById(String customerId,String eventId);
    EventResponseModel addEvent(String customerId,EventRequestModel eventRequestModel);
    EventResponseModel updateEvent(String customerId, String eventId, EventRequestModel eventRequestModel);
    void deleteEvent(String customerId,String eventId);
}
