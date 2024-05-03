package com.eventplanner.apigateway.businesslayer.events;


import com.eventplanner.apigateway.datamapperlayer.events.EventResponseMapper;

import com.eventplanner.apigateway.domainclientlayer.events.EventDate;
import com.eventplanner.apigateway.domainclientlayer.events.EventServiceClient;
import com.eventplanner.apigateway.domainclientlayer.events.ParticipantModel;
import com.eventplanner.apigateway.presentationlayer.events.EventRequestModel;
import com.eventplanner.apigateway.presentationlayer.events.EventResponseModel;
import com.eventplanner.apigateway.utils.InvalidInputException;
import com.eventplanner.apigateway.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class EventServiceUnitTest {

    @Autowired
    EventService eventService;

    @MockBean
    EventServiceClient eventServiceClient;

    @SpyBean
    EventResponseMapper eventResponseMapper;

    List<String> participantIds = new ArrayList<>();

    List<ParticipantModel> participantModels = new ArrayList<>();

    EventResponseModel eventResponseModel = new EventResponseModel("1","1","1",participantModels,new EventDate(),"PLANNED","name","description","venueName","firstName","lastName");

    EventRequestModel eventRequestModel = new EventRequestModel("1","1",new EventDate(),"PLANNED","name","description",participantIds);

    @Test
    public void GetALlEvents() {
    when(eventServiceClient.getEvents(any())).thenReturn(List.of(eventResponseModel));
    List<EventResponseModel> eventResponseModels = eventService.getAllEvents("1");
    assertNotNull(eventResponseModels);
    assertEquals(1,eventResponseModels.size());
    }

    @Test
    public void GetEventById() {
    when(eventServiceClient.getEventByEventId(any(),any())).thenReturn(eventResponseModel);
    EventResponseModel eventResponseModel = eventService.getEventById("1","1");
    assertNotNull(eventResponseModel);
    assertEquals("1",eventResponseModel.getEventId());
    assertEquals("1",eventResponseModel.getVenueId());
    assertEquals("1",eventResponseModel.getCustomerId());
    assertEquals("PLANNED",eventResponseModel.getEventStatus());
    assertEquals("name",eventResponseModel.getEventName());
    assertEquals("description",eventResponseModel.getDescription());
    assertEquals("venueName",eventResponseModel.getVenueName());
    assertEquals("firstName",eventResponseModel.getCustomerFirstName());
    assertEquals("lastName",eventResponseModel.getCustomerLastName());
    }

    @Test
    public void AddEvent() {
    when(eventServiceClient.addEvent(any(),any())).thenReturn(eventResponseModel);
    EventResponseModel eventResponseModel = eventService.addEvent(eventRequestModel,"1");
    assertNotNull(eventResponseModel);
    assertEquals("1",eventResponseModel.getEventId());
    assertEquals("1",eventResponseModel.getVenueId());
    assertEquals("1",eventResponseModel.getCustomerId());
    assertEquals("PLANNED",eventResponseModel.getEventStatus());
    assertEquals("name",eventResponseModel.getEventName());
    assertEquals("description",eventResponseModel.getDescription());
    assertEquals("venueName",eventResponseModel.getVenueName());
    assertEquals("firstName",eventResponseModel.getCustomerFirstName());
    assertEquals("lastName",eventResponseModel.getCustomerLastName());
    }

    @Test
    public void WhenEventIdInvalidForGet(){
    when(eventServiceClient.getEventByEventId(any(),any())).thenThrow(new InvalidInputException("Eventid provided is invalid"));
    assertThrows(InvalidInputException.class,()->eventService.getEventById("1","1"));
    }

    @Test
    public void WhenEventIdInvalidForAdd(){
    when(eventServiceClient.addEvent(any(),any())).thenThrow(new InvalidInputException("Eventid provided is invalid"));
    assertThrows(InvalidInputException.class,()->eventService.addEvent(eventRequestModel,"1"));
    }

    @Test
    public void UpdateEvent() {
       doNothing().when(eventServiceClient).updateEvent(any(),any(),any());
         eventService.updateEvent(eventRequestModel,"1","1");
    }

    @Test
    public void WhenEventIdInvalidForUpdate(){
    doThrow(new InvalidInputException("Eventid provided is invalid")).when(eventServiceClient).updateEvent(any(),any(),any());
    assertThrows(InvalidInputException.class,()->eventService.updateEvent(eventRequestModel,"1","1"));
    }

    @Test
    public void DeleteEvent() {
    doNothing().when(eventServiceClient).deleteEvent(any(),any());
    eventService.deleteEvent("1","1");
    }

    @Test
    public void WhenEventIdInvalidForDelete(){
  doThrow(new InvalidInputException("Eventid provided is invalid")).when(eventServiceClient).deleteEvent(any(),any());
    assertThrows(InvalidInputException.class,()->eventService.deleteEvent("1","1"));
    }
}