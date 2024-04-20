package com.eventplanner.events.businesslayer;

import com.example.eventplanningws.EventSubDomain.PresentationLayer.EventRequestModel;
import com.example.eventplanningws.EventSubDomain.PresentationLayer.EventResponseModel;

import java.util.List;

public interface EventService {
    List<EventResponseModel> getEvents();
    EventResponseModel getEventById(String eventId);
    EventResponseModel addEvent(EventRequestModel eventRequestModel);
    EventResponseModel updateEvent(EventRequestModel eventRequestModel, String eventId);
    void deleteEvent(String eventId);
}
