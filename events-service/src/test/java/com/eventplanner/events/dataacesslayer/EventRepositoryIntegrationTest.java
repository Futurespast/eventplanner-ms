package com.eventplanner.events.dataacesslayer;

import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataMongoTest
@ActiveProfiles("test")
class EventRepositoryIntegrationTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    public void WhenCustomerIdValid_ReturnLists(){
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 2);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(date,date);
        Event event = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        Event event2 = Event.builder()
                .eventIdentifier(new EventIdentifier())
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        eventRepository.save(event);
        eventRepository.save(event2);

        //act
    List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
    assertNotNull(events);
    assertEquals(2,events.size());
    }

    @Test
    public void WhenCustomerIdInvalid_ReturnNull(){
       String invalidCustomerId = UUID.randomUUID().toString();
       List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(invalidCustomerId);
      assertEquals(0,events.size());
    }


    @Test
    public void WhenEventIdValid_ReturnEvent(){
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 2);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(date,date);
        Event event = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        eventRepository.save(event);
        Event returnedEvent = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(customerModel.getCustomerId(),event.getEventIdentifier().getEventId());
        assertNotNull(returnedEvent);
        assertEquals(event.getEventName(), returnedEvent.getEventName());
        assertEquals(event.getEventIdentifier().getEventId(),returnedEvent.getEventIdentifier().getEventId());
        assertEquals(event.getEventStatus(),returnedEvent.getEventStatus());
        assertEquals(event.getDescription(),returnedEvent.getDescription());
    }

    @Test
    public void WhenCustomerIdInvalidForEvent_ReturnNull(){
        String invalidCustomerId = UUID.randomUUID().toString();
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 2);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(date,date);
        Event event = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        eventRepository.save(event);
       Event returnedEvent = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(invalidCustomerId, event.getEventIdentifier().getEventId());
       assertNull(returnedEvent);
    }
    @Test
    public void WhenInvalidEventId_ReturnNull() {
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 2);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(date, date);
        Event event = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
eventRepository.save(event);
        String invalidEventId = "222222";
        Event returnedEvent = eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId(customerModel.getCustomerId(),invalidEventId);
        assertNull(returnedEvent);
    }
}