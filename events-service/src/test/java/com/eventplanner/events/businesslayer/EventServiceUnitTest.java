package com.eventplanner.events.businesslayer;

import com.eventplanner.events.dataacesslayer.*;
import com.eventplanner.events.datamapperlayer.EventResponseMapper;
import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Customer.CustomersServiceClient;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantsServiceClient;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import com.eventplanner.events.domainclientlayer.Venue.VenuesServiceClient;
import com.eventplanner.events.presentationlayer.EventRequestModel;
import com.eventplanner.events.presentationlayer.EventResponseModel;
import com.eventplanner.events.utils.InvalidEventDateException;
import com.eventplanner.events.utils.InvalidInputException;
import com.eventplanner.events.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.autoconfigure.exclude= org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration")
class EventServiceUnitTest {

    @Autowired
    EventService eventService;

    @MockBean
    CustomersServiceClient customersServiceClient;

    @MockBean
    VenuesServiceClient venuesServiceClient;

    @MockBean
    ParticipantsServiceClient participantsServiceClient;

    @MockBean
    EventRepository eventRepository;

    @SpyBean
    EventResponseMapper eventResponseMapper;


    @Test
    public void WhenNewEventIsValid_AddEvent(){
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
        EventDate eventDate = new EventDate(date,date2);
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
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("PLANNED")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(participantsServiceClient.getParticipantById("25a249e0-52c1-4911-91e2-b50fffef55e6")).thenReturn(participant1);
        when(participantsServiceClient.getParticipantById("10a9dc8f-6259-4c0e-997f-38cc8773596a")).thenReturn(participant2);
        when(venuesServiceClient.patchForPostVenueDates("8d996257-e535-4614-98f6-4596be2a3626",date,date2)).thenReturn(updatedVenue);
        when(eventRepository.save(any())).thenReturn(event);

        EventResponseModel eventResponseModel = eventService.addEvent("c3540a89-cb47-4c96-888e-ff96708db4d8",eventRequestModel);

        assertNotNull(eventResponseModel);
        assertEquals(eventRequestModel.getEventName(),eventResponseModel.getEventName());
        assertEquals(eventRequestModel.getDescription(),eventResponseModel.getDescription());
        assertEquals(eventRequestModel.getEventStatus(),eventResponseModel.getEventStatus());
        assertEquals(eventRequestModel.getVenueId(),eventResponseModel.getVenueId());
        assertEquals(venueModel.getName(),eventResponseModel.getVenueName());
        assertEquals(customerModel.getCustomerId(),eventResponseModel.getCustomerId());
        assertEquals(customerModel.getFirstName(),eventResponseModel.getCustomerFirstName());
        assertEquals(customerModel.getLastName(),eventResponseModel.getCustomerLastName());

        verify(eventResponseMapper,times(1)).entityToEventResponseModel(event);
    }

    @Test
    public void WhenCustomerIdIsInvalidForPost_ThrowException(){
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888")
                .firstName("John")
                .lastName("Doe")
                .build();
        when(customersServiceClient.getCustomerByCustomerId(customerModel.getCustomerId())).thenReturn(null);
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(new EventDate(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2)))
                .description("Concert performed by DJ Sam")
                .eventStatus("PLANNED")
                .build();
      assertThrows(NotFoundException.class, () -> {
            eventService.addEvent("c3540a89-cb47-4c96-888", eventRequestModel);
        });
    }

    @Test
    public void WhenVenueIdIsInvalidForPost_ThrowException(){
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(null);
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(new EventDate(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2)))
                .description("Concert performed by DJ Sam")
                .eventStatus("PLANNED")
                .build();
        assertThrows(InvalidInputException.class, () -> {
            eventService.addEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventRequestModel);
        });
    }

    @Test
    public void WhenParticipantIdIsInvalidForPost_ThrowException(){
            List<ParticipantModel> participantModelList = new ArrayList<>();
            ParticipantModel participant1 = ParticipantModel.builder()
                    .participantId("25a249e0-52c1-49")
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
            EventDate eventDate = new EventDate(date,date2);
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
            EventRequestModel eventRequestModel = EventRequestModel.builder()
                    .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                    .eventName("Concert")
                    .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6","10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                    .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                    .eventDate(eventDate)
                    .description("Concert performed by DJ Sam")
                    .eventStatus("PLANNED")
                    .build();
            VenueModel updatedVenue = VenueModel.builder()
                    .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                    .name("Venue 1")
                    .availableDates(new ArrayList<>())
                    .build();
            when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
            when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
            when(participantsServiceClient.getParticipantById("25a249e0-52c1-4911-91e2-b50fffef55e6")).thenReturn(null);
            assertThrows(InvalidInputException.class, () -> {
                eventService.addEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventRequestModel);
            });
    }

    @Test
    public void WhenEventDateInvalid_ThrowException() {
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
        EventDate eventDate = new EventDate(date, date2);
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
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(new EventDate(LocalDate.of(2024, 10, 3), LocalDate.of(2024, 10, 4)))
                .description("Concert performed by DJ Sam")
                .eventStatus("PLANNED")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();

        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        Exception exception = assertThrows(InvalidEventDateException.class, () -> {
            eventService.addEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventRequestModel);
        });
    }

    @Test
    public void WhenEventIsValid_UpdateEvent(){
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
        EventDate eventDate = new EventDate(date, date2);
        Event oldevent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        Event updatedEvent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.ONGOING)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(participantsServiceClient.getParticipantById("25a249e0-52c1-4911-91e2-b50fffef55e6")).thenReturn(participant1);
        when(participantsServiceClient.getParticipantById("10a9dc8f-6259-4c0e-997f-38cc8773596a")).thenReturn(participant2);
        when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId())).thenReturn(oldevent);
        when(eventRepository.save(any())).thenReturn(updatedEvent);
       EventResponseModel eventResponseModel = eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId(),eventRequestModel);

        assertNotNull(eventResponseModel);
        assertEquals(eventRequestModel.getEventName(),eventResponseModel.getEventName());
        assertEquals(eventRequestModel.getDescription(),eventResponseModel.getDescription());
        assertEquals(eventRequestModel.getEventStatus(),eventResponseModel.getEventStatus());
        assertEquals(eventRequestModel.getVenueId(),eventResponseModel.getVenueId());
        assertEquals(venueModel.getName(),eventResponseModel.getVenueName());
        assertEquals(customerModel.getCustomerId(),eventResponseModel.getCustomerId());
        assertEquals(customerModel.getFirstName(),eventResponseModel.getCustomerFirstName());
        assertEquals(customerModel.getLastName(),eventResponseModel.getCustomerLastName());

        verify(eventResponseMapper,times(1)).entityToEventResponseModel(updatedEvent);

    }

    @Test
    public void WhenCustomerIdIsInvalidForPut_ThrowException(){
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2));
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();

        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708d")).thenReturn(null);
        assertThrows(NotFoundException.class, () -> {
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId(),eventRequestModel);
        });
    }

    @Test
    public void WhenVenueIdIsInvalidForPut_ThrowException(){

        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be26")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2));
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(null);
        assertThrows(InvalidInputException.class, () -> {
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId(),eventRequestModel);
        });
    }

    @Test
    public void WhenParticipantIdIsInvalidForPut_ThrowException() {
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50f")
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
        EventDate eventDate = new EventDate(date, date2);
        Event oldevent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        Event updatedEvent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.ONGOING)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(participantsServiceClient.getParticipantById(participant1.getParticipantId())).thenReturn(null);
        assertThrows(InvalidInputException.class, () -> {
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventIdentifier.getEventId(), eventRequestModel);
        });
    }

    @Test
    public void WhenEventValidButDifferentDate_UpdateEvent(){
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
            LocalDate date3 = LocalDate.of(2024, 10, 3);
            LocalDate date4 = LocalDate.of(2024, 10, 4);
            List<LocalDate> ogDates = new ArrayList<>();
            ogDates.add(date);
            ogDates.add(date2);
            ogDates.add(date3);
            ogDates.add(date4);
            localDates.add(date);
            localDates.add(date2);
            VenueModel venueModel = VenueModel.builder()
                    .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                    .name("Venue 1")
                    .availableDates(ogDates)
                    .build();
            CustomerModel customerModel = CustomerModel.builder()
                    .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                    .firstName("John")
                    .lastName("Doe")
                    .build();
            EventIdentifier eventIdentifier = new EventIdentifier();
            EventDate eventDate = new EventDate(date, date2);
            Event oldevent = Event.builder()
                    .eventIdentifier(eventIdentifier)
                    .eventStatus(EventStatus.PLANNED)
                    .eventDate(eventDate)
                    .eventName("Concert")
                    .description("Concert performed by DJ Sam")
                    .customerModel(customerModel)
                    .participantModels(participantModelList)
                    .venueModel(venueModel)
                    .build();

            EventRequestModel eventRequestModel = EventRequestModel.builder()
                    .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                    .eventName("Concert")
                    .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                    .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                    .eventDate(new EventDate(date3, date4))
                    .description("Concert performed by DJ Sam")
                    .eventStatus("ONGOING")
                    .build();
            VenueModel updatedVenue = VenueModel.builder()
                    .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                    .name("Venue 1")
                    .availableDates(localDates)
                    .build();
            Event updatedEvent = Event.builder()
                    .eventIdentifier(eventIdentifier)
                    .eventStatus(EventStatus.ONGOING)
                    .eventDate(new EventDate(date3,date4))
                    .eventName("Concert")
                    .description("Concert performed by DJ Sam")
                    .customerModel(customerModel)
                    .participantModels(participantModelList)
                    .venueModel(venueModel)
                    .build();
            when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
            when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
            when(participantsServiceClient.getParticipantById("25a249e0-52c1-4911-91e2-b50fffef55e6")).thenReturn(participant1);
            when(participantsServiceClient.getParticipantById("10a9dc8f-6259-4c0e-997f-38cc8773596a")).thenReturn(participant2);
            when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId())).thenReturn(oldevent);
            when(eventRepository.save(any())).thenReturn(updatedEvent);
            when(venuesServiceClient.patchForPutVenueDates("8d996257-e535-4614-98f6-4596be2a3626",date,date2,date3,date4)).thenReturn(updatedVenue);
            EventResponseModel eventResponseModel = eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId(),eventRequestModel);

            assertNotNull(eventResponseModel);
            assertEquals(eventRequestModel.getEventName(),eventResponseModel.getEventName());
            assertEquals(eventRequestModel.getDescription(),eventResponseModel.getDescription());
            assertEquals(eventRequestModel.getEventStatus(),eventResponseModel.getEventStatus());
            assertEquals(eventRequestModel.getVenueId(),eventResponseModel.getVenueId());
            assertEquals(venueModel.getName(),eventResponseModel.getVenueName());
            assertEquals(customerModel.getCustomerId(),eventResponseModel.getCustomerId());
            assertEquals(customerModel.getFirstName(),eventResponseModel.getCustomerFirstName());
            assertEquals(customerModel.getLastName(),eventResponseModel.getCustomerLastName());

            verify(eventResponseMapper,times(1)).entityToEventResponseModel(updatedEvent);
    }

    @Test
    public void WhenParticipantIdIsInvalidAndDateIsDifferent_ThrowException(){
        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2024, 10, 1);
        LocalDate date2 = LocalDate.of(2024, 10, 2);
        LocalDate date3 = LocalDate.of(2024, 10, 3);
        LocalDate date4 = LocalDate.of(2024, 10, 4);
        List<LocalDate> ogDates = new ArrayList<>();
        ogDates.add(date);
        ogDates.add(date2);
        ogDates.add(date3);
        ogDates.add(date4);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(ogDates)
                .build();
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        EventIdentifier eventIdentifier = new EventIdentifier();
        EventDate eventDate = new EventDate(date, date2);
        Event oldevent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(new EventDate(date3, date4))
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        Event updatedEvent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.ONGOING)
                .eventDate(new EventDate(date3,date4))
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(participantsServiceClient.getParticipantById(participant1.getParticipantId())).thenReturn(null);
        assertThrows(InvalidInputException.class, () -> {
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventIdentifier.getEventId(), eventRequestModel);
        });
    }

    @Test
    public void WhenEventIdInvalidForPut_ThrowException(){
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
        EventDate eventDate = new EventDate(date, date2);
        Event oldevent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(eventDate)
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        Event updatedEvent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.ONGOING)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(participantsServiceClient.getParticipantById("25a249e0-52c1-4911-91e2-b50fffef55e6")).thenReturn(participant1);
        when(participantsServiceClient.getParticipantById("10a9dc8f-6259-4c0e-997f-38cc8773596a")).thenReturn(participant2);
        when(venuesServiceClient.patchForPostVenueDates("8d996257-e535-4614-98f6-4596be2a3626",date,date2)).thenReturn(updatedVenue);
        when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8","123232323")).thenThrow(new InvalidInputException("eventId provided is invalid 123232323"));
        assertThrows(InvalidInputException.class,()->{
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8","123232323",eventRequestModel);
        });
    }

    @Test
    public void WhenDateInvalidForPut_ThrowException() {
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
        EventDate eventDate = new EventDate(date, date2);
        Event oldevent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.PLANNED)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .eventName("Concert")
                .participantIds(List.of("25a249e0-52c1-4911-91e2-b50fffef55e6", "10a9dc8f-6259-4c0e-997f-38cc8773596a"))
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .eventDate(new EventDate(LocalDate.of(2024, 10, 3), LocalDate.of(2024, 10, 4)))
                .description("Concert performed by DJ Sam")
                .eventStatus("ONGOING")
                .build();
        VenueModel updatedVenue = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        Event updatedEvent = Event.builder()
                .eventIdentifier(eventIdentifier)
                .eventStatus(EventStatus.ONGOING)
                .eventDate(eventDate)
                .eventName("Concert")
                .description("Concert performed by DJ Sam")
                .customerModel(customerModel)
                .participantModels(participantModelList)
                .venueModel(venueModel)
                .build();

        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(venuesServiceClient.getVenueByVenueId("8d996257-e535-4614-98f6-4596be2a3626")).thenReturn(venueModel);
        when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId())).thenReturn(oldevent);
        Exception exception = assertThrows(InvalidEventDateException.class, () -> {
            eventService.updateEvent("c3540a89-cb47-4c96-888e-ff96708db4d8", eventIdentifier.getEventId(), eventRequestModel);
        });
    }

    @Test
    public void WhenValidCustomerId_ReturnALL(){
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
        EventDate eventDate = new EventDate(date, date2);
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
        List<Event> events = new ArrayList<>();
        events.add(event);
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(eventRepository.getAllByCustomerModel_CustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(events);
        eventService.getEvents("c3540a89-cb47-4c96-888e-ff96708db4d8");
        verify(eventResponseMapper,times(1)).entityToEventResponseList(events);
        assertEquals(1,events.size());
    }
    @Test
    public void WhenInvalidCustomerIdOnGetALL_ThrowException(){
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96")).thenReturn(null);
        assertThrows(NotFoundException.class,()->{
            eventService.getEvents("c3540a89-cb47-4c96-888e-ff96");
        });
    }

    @Test
    public void WhenValidCustomerIdAndEventId_ReturnEvent() {
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
        EventDate eventDate = new EventDate(date, date2);
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
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId())).thenReturn(event);
      EventResponseModel eventResponseModel =  eventService.getEventById("c3540a89-cb47-4c96-888e-ff96708db4d8",eventIdentifier.getEventId());
        verify(eventResponseMapper,times(1)).entityToEventResponseModel(event);
        assertNotNull(eventResponseModel);
        assertEquals(event.getEventName(),eventResponseModel.getEventName());
        assertEquals(event.getDescription(),eventResponseModel.getDescription());
        assertEquals(event.getEventStatus().toString(),eventResponseModel.getEventStatus());
        assertEquals(event.getVenueModel().getVenueId(),eventResponseModel.getVenueId());
        assertEquals(event.getVenueModel().getName(),eventResponseModel.getVenueName());
        assertEquals(event.getCustomerModel().getCustomerId(),eventResponseModel.getCustomerId());
        assertEquals(event.getCustomerModel().getFirstName(),eventResponseModel.getCustomerFirstName());
        assertEquals(event.getCustomerModel().getLastName(),eventResponseModel.getCustomerLastName());
    }

    @Test
    public void WhenCustomerIdIsInvalidOnGet_ThrowException(){
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96")).thenReturn(null);
        assertThrows(NotFoundException.class,()->{
            eventService.getEventById("c3540a89-cb47-4c96-888e-ff96","123232323");
        });
    }

    @Test
    public void WhenEventIdInvalidOnGet_ThrowException(){
        CustomerModel customerModel = CustomerModel.builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96708db4d8")).thenReturn(customerModel);
        when(eventRepository.getEventByCustomerModel_CustomerIdAndEventIdentifier_EventId("c3540a89-cb47-4c96-888e-ff96708db4d8","123232323")).thenReturn(null);
        assertThrows(InvalidInputException.class,()->{
            eventService.getEventById("c3540a89-cb47-4c96-888e-ff96708db4d8","123232323");
        });
    }

    @Test
 public void WhenCustomerIdInvalidOnDelete_ThrowException(){
        when(customersServiceClient.getCustomerByCustomerId("c3540a89-cb47-4c96-888e-ff96")).thenReturn(null);
        assertThrows(NotFoundException.class,()->{
            eventService.deleteEvent("c3540a89-cb47-4c96-888e-ff96","123232323");
        });
    }
}
