package com.eventplanner.events.presentationlayer;

import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.dataacesslayer.EventDate;
import com.eventplanner.events.dataacesslayer.EventRepository;
import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    RestTemplate restTemplate;

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private ObjectMapper mapper;

    private final String CUSTOMER_BASE_URI = "http://localhost:7001/api/v1/customers";

    private final String EVENT_BASE_URI = "/api/v1/customers";

    @BeforeEach
    public void init(){
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

    }

    @Test
    public void whenGetEventById_thenReturnEvent() throws URISyntaxException, JsonProcessingException {
        //arrange
        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

        List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
        Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
        assertNotNull(event);
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
        //act & assert
        webTestClient.get().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(EventResponseModel.class).value((response)->
        {
            assertNotNull(response);
            assertEquals(event.getEventIdentifier().getEventId(),response.getEventId());
            assertEquals(event.getCustomerModel().getCustomerId(),response.getCustomerId());
            assertEquals(event.getVenueModel().getVenueId(),response.getVenueId());
            assertEquals(event.getVenueModel().getName(),response.getVenueName());
            assertEquals(event.getEventName(),response.getEventName());
            assertEquals(event.getDescription(),response.getDescription());
        });
    }

    @Test
    public void WhenGetByInvalidEventId_ReturnInvalidException() throws URISyntaxException, JsonProcessingException {
        //arrange
        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();
        String eventId = "499075b0-7761-4684-9bbc-16b9a007911";
        String errormessage = "eventId provided is invalid " + eventId;
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/499075b0-7761-4684-9bbc-16b9a007911";
        //act & assert
        webTestClient.get().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo(errormessage);
    }

    @Test
    public void WhenGetByInvalidCustomerId_ReturnInvalidException() throws URISyntaxException, JsonProcessingException {
        //arrange
        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96")
                .firstName("John")
                .lastName("Doe")
                .build();
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/"+customerModel.getCustomerId())))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/499075b0-7761-4684-9bbc-16b9a007911";
        //act & assert
        webTestClient.get().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");

    }


    @Test
    public void whenGetAll_ReturnEvents() throws URISyntaxException, JsonProcessingException {
        long sizeDB = eventRepository.count();

        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";


        webTestClient.get().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(EventResponseModel.class).value((list)->{
            assertNotNull(list);
            assertTrue(list.size() == sizeDB);
        });
    }

    @Test
    public void whenValidEvent_AddEvent() throws URISyntaxException, JsonProcessingException{
        long sizeDB = eventRepository.count();

        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";

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
         VenueModel changed = VenueModel.builder()
                 .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                 .name("Venue 1")
                 .availableDates(new ArrayList<>())
                 .build();


         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-10-01/2024-10-02")))
                         .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(changed)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/25a249e0-52c1-4911-91e2-b50fffef55e6")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant1)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/10a9dc8f-6259-4c0e-997f-38cc8773596a")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant2)));

            EventRequestModel eventRequestModel = EventRequestModel.builder()
                    .customerId(customerModel.getCustomerId())
                    .eventName("Concert1")
                    .eventStatus("PLANNED")
                    .description("Concert performed by DJ Sam")
                    .venueId(venueModel.getVenueId())
                    .eventDate(new EventDate(date,date2))
                    .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                 .build();
         webTestClient.post().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(EventResponseModel.class).value((response)->
         {
             assertNotNull(response);
             assertEquals(eventRequestModel.getCustomerId(),response.getCustomerId());
             assertEquals(eventRequestModel.getVenueId(),response.getVenueId());
             assertEquals(eventRequestModel.getEventName(),response.getEventName());
             assertEquals(eventRequestModel.getDescription(),response.getDescription());
             assertEquals(eventRequestModel.getEventDate().getStartDate(),response.getEventDate().getStartDate());
             assertEquals(eventRequestModel.getEventDate().getEndDate(),response.getEventDate().getEndDate());
             assertEquals(eventRequestModel.getEventStatus(),response.getEventStatus());
             assertEquals(participantModelList.size(),response.getParticipants().size());
             assertEquals(sizeDB+1,eventRepository.count());
         });

     }

    @Test
    public void whenInvalidParticipantForAddEvent() throws URISyntaxException, JsonProcessingException{
        long sizeDB = eventRepository.count();

        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";

        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2")
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
        VenueModel changed = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-10-01/2024-10-02")))
                .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(changed)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/25a249e0-52c1-4911-91e2")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant1)));


        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId(customerModel.getCustomerId())
                .eventName("Concert1")
                .eventStatus("PLANNED")
                .description("Concert performed by DJ Sam")
                .venueId(venueModel.getVenueId())
                .eventDate(new EventDate(date,date2))
                .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                .build();
        webTestClient.post().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");

    }

    @Test
    public void whenInvalidVenueIdForAddEvent() throws URISyntaxException, JsonProcessingException{
        long sizeDB = eventRepository.count();

        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";

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
                .venueId("8d996257-e535-4614-98f6-4596")
                .name("Venue 1")
                .availableDates(localDates)
                .build();
        VenueModel changed = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));


        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId(customerModel.getCustomerId())
                .eventName("Concert1")
                .eventStatus("PLANNED")
                .description("Concert performed by DJ Sam")
                .venueId(venueModel.getVenueId())
                .eventDate(new EventDate(date,date2))
                .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                .build();
        webTestClient.post().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");


    }

     @Test
    public void WhenEventDateInvalid_ReturnInvalidDateException() throws JsonProcessingException, URISyntaxException {
         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";

         List<ParticipantModel> participantModelList = new ArrayList<>();
         ParticipantModel participant1 = ParticipantModel.builder()
                 .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                 .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
         ParticipantModel participant2 = ParticipantModel.builder()
                 .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
         participantModelList.add(participant1);
         participantModelList.add(participant2);
         List<LocalDate> localDates = new ArrayList<>();
         LocalDate date = LocalDate.of(2025, 10, 1);
         LocalDate date2 = LocalDate.of(2025, 10, 2);
         localDates.add(date);
         localDates.add(date2);
         VenueModel venueModel = VenueModel.builder()
                 .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                 .name("Venue 1")
                 .availableDates(localDates)
                 .build();

         EventRequestModel eventRequestModel = EventRequestModel.builder()
                 .customerId(customerModel.getCustomerId())
                 .eventName("Concert1")
                 .eventStatus("PLANNED")
                 .description("Concert performed by DJ Sam")
                 .venueId(venueModel.getVenueId())
                 .eventDate(new EventDate(LocalDate.of(2024,1,1),LocalDate.of(2024,1,2)))
                 .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                 .build();


         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));

         webTestClient.post().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                 .expectBody()
                 .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                 .jsonPath("$.message").isEqualTo("Event date is not available with the venue");

     }

    @Test
    public void WhenEventDateInvalid_ReturnInvalidDateExceptionForServiceClient() throws JsonProcessingException, URISyntaxException {
        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events";

        List<ParticipantModel> participantModelList = new ArrayList<>();
        ParticipantModel participant1 = ParticipantModel.builder()
                .participantId("25a249e0-52c1-4911-91e2-b50fffef55e6")
                .firstName("John").lastName("Doe").emailAddress("john.doe@example.com").specialNote("Vegetarian").build();
        ParticipantModel participant2 = ParticipantModel.builder()
                .participantId("10a9dc8f-6259-4c0e-997f-38cc8773596a").firstName("Jane").lastName("Smith").emailAddress("jane.smith@example.com").specialNote("Allergic to nuts").build();
        participantModelList.add(participant1);
        participantModelList.add(participant2);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate date = LocalDate.of(2025, 10, 1);
        LocalDate date2 = LocalDate.of(2025, 10, 2);
        localDates.add(date);
        localDates.add(date2);
        VenueModel venueModel = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();

        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId(customerModel.getCustomerId())
                .eventName("Concert1")
                .eventStatus("PLANNED")
                .description("Concert performed by DJ Sam")
                .venueId(venueModel.getVenueId())
                .eventDate(new EventDate(LocalDate.of(2024,1,1),LocalDate.of(2024,1,2)))
                .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-01-01/2024-01-02")))
                .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString("Event date is not available with the venue")));
        webTestClient.post().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("Event date is not available with the venue");

    }


     @Test
    public void whenValidEvent_UpdateEvent() throws JsonProcessingException, URISyntaxException {
         long sizeDB = eventRepository.count();

         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

         List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
         Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
         assertNotNull(event);
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
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
         VenueModel changed = VenueModel.builder()
                 .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                 .name("Venue 1")
                 .availableDates(new ArrayList<>())
                 .build();


         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/25a249e0-52c1-4911-91e2-b50fffef55e6")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant1)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/10a9dc8f-6259-4c0e-997f-38cc8773596a")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant2)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-10-01/2024-10-02")))
                 .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(changed)));

         EventRequestModel eventRequestModel = EventRequestModel.builder()
                 .customerId(customerModel.getCustomerId())
                 .eventName("Concert1")
                 .eventStatus("PLANNED")
                 .description("Concert performed by DJ Sam")
                 .venueId(venueModel.getVenueId())
                 .eventDate(new EventDate(date,date2))
                 .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                 .build();
         webTestClient.put().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(EventResponseModel.class).value((response)->
         {
             assertNotNull(response);
             assertEquals(eventRequestModel.getCustomerId(),response.getCustomerId());
             assertEquals(eventRequestModel.getVenueId(),response.getVenueId());
             assertEquals(eventRequestModel.getEventName(),response.getEventName());
             assertEquals(eventRequestModel.getDescription(),response.getDescription());
             assertEquals(eventRequestModel.getEventDate().getStartDate(),response.getEventDate().getStartDate());
             assertEquals(eventRequestModel.getEventDate().getEndDate(),response.getEventDate().getEndDate());
             assertEquals(eventRequestModel.getEventStatus(),response.getEventStatus());
             assertEquals(participantModelList.size(),response.getParticipants().size());
             assertEquals(sizeDB,eventRepository.count());
         });
     }

     @Test
    public void WhenEventIdInvalid_ReturnInvalidInput() throws URISyntaxException, JsonProcessingException {
         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

        String eventId = "1111111112e323213";
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+eventId;
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
         VenueModel changed = VenueModel.builder()
                 .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                 .name("Venue 1")
                 .availableDates(new ArrayList<>())
                 .build();


         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/25a249e0-52c1-4911-91e2-b50fffef55e6")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant1)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/10a9dc8f-6259-4c0e-997f-38cc8773596a")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant2)));
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-10-01/2024-10-02")))
                 .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(changed)));

         EventRequestModel eventRequestModel = EventRequestModel.builder()
                 .customerId(customerModel.getCustomerId())
                 .eventName("Concert1")
                 .eventStatus("PLANNED")
                 .description("Concert performed by DJ Sam")
                 .venueId(venueModel.getVenueId())
                 .eventDate(new EventDate(date,date2))
                 .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                 .build();
         webTestClient.put().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                 .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                 .jsonPath("$.message").isEqualTo("eventId provided is invalid "+eventId);
     }

     @Test
    public void whenInvalidDate_ReturnInvalidDateException() throws URISyntaxException, JsonProcessingException {
         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

         List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
         Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
         assertNotNull(event);
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
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
         VenueModel changed = VenueModel.builder()
                 .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                 .name("Venue 1")
                 .availableDates(new ArrayList<>())
                 .build();
         EventRequestModel eventRequestModel = EventRequestModel.builder()
                 .customerId(customerModel.getCustomerId())
                 .eventName("Concert1")
                 .eventStatus("PLANNED")
                 .description("Concert performed by DJ Sam")
                 .venueId(venueModel.getVenueId())
                 .eventDate(new EventDate(LocalDate.of(2024,1,1),LocalDate.of(2024,1,2)))
                 .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
         webTestClient.put().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                 .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                 .jsonPath("$.message").isEqualTo("Event date is not available with the venue");
     }

    @Test
    public void whenInvalidDate_ReturnInvalidDateExceptionForServiceClient() throws URISyntaxException, JsonProcessingException {
        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

        List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
        Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
        assertNotNull(event);
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
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
        VenueModel changed = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(new ArrayList<>())
                .build();
        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId(customerModel.getCustomerId())
                .eventName("Concert1")
                .eventStatus("PLANNED")
                .description("Concert performed by DJ Sam")
                .venueId(venueModel.getVenueId())
                .eventDate(new EventDate(LocalDate.of(2024,1,1),LocalDate.of(2024,1,2)))
                .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
        webTestClient.put().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY");
    }

     @Test
    public void WhenValidEvent_Delete() throws URISyntaxException, JsonProcessingException{
         long sizeDB = eventRepository.count();

         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();

         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

         List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
         Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
         assertNotNull(event);
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
         webTestClient.delete().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNoContent();
         long sizeDBAfter = eventRepository.count();
         assertEquals(sizeDB -1, sizeDBAfter);
     }
     @Test
    public void WhenInvalidDelete_ReturnInvalidException() throws JsonProcessingException, URISyntaxException {
         CustomerModel customerModel = new CustomerModel().builder()
                 .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                 .firstName("John")
                 .lastName("Doe")
                 .build();
         String eventId= "12232423";
         mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                 .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));
         String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+eventId;
         webTestClient.delete().uri(url).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY).expectBody()
                 .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                 .jsonPath("$.message").isEqualTo("eventId provided is invalid "+eventId);
     }

    @Test
    public void whenValidEventButDateDifferent_UpdateEvent() throws JsonProcessingException, URISyntaxException {
        long sizeDB = eventRepository.count();

        CustomerModel customerModel = new CustomerModel().builder()
                .customerId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(CUSTOMER_BASE_URI+"/c3540a89-cb47-4c96-888e-ff96708db4d8")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(customerModel)));

        List<Event> events = eventRepository.getAllByCustomerModel_CustomerId(customerModel.getCustomerId());
        Event event = events.stream().filter(e -> e.getVenueModel().getVenueId().equals("8d996257-e535-4614-98f6-4596be2a3626")).findFirst().get();
        assertNotNull(event);
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/"+event.getEventIdentifier().getEventId();
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
        VenueModel changed = VenueModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .name("Venue 1")
                .availableDates(localDates)
                .build();


        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(venueModel)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7002/api/v1/venues/8d996257-e535-4614-98f6-4596be2a3626/2024-10-01/2024-10-02/2024-10-03/2024-10-04")))
                .andExpect(method(HttpMethod.PATCH)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(changed)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/25a249e0-52c1-4911-91e2-b50fffef55e6")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant1)));
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI("http://localhost:7003/api/v1/participants/10a9dc8f-6259-4c0e-997f-38cc8773596a")))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(participant2)));


        EventRequestModel eventRequestModel = EventRequestModel.builder()
                .customerId(customerModel.getCustomerId())
                .eventName("Concert1")
                .eventStatus("PLANNED")
                .description("Concert performed by DJ Sam")
                .venueId(venueModel.getVenueId())
                .eventDate(new EventDate(date3,date4))
                .participantIds(List.of(participant1.getParticipantId(),participant2.getParticipantId()))
                .build();
        webTestClient.put().uri(url).accept(MediaType.APPLICATION_JSON).bodyValue(eventRequestModel).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(EventResponseModel.class).value((response)->
        {
            assertNotNull(response);
            assertEquals(eventRequestModel.getCustomerId(),response.getCustomerId());
            assertEquals(eventRequestModel.getVenueId(),response.getVenueId());
            assertEquals(eventRequestModel.getEventName(),response.getEventName());
            assertEquals(eventRequestModel.getDescription(),response.getDescription());
            assertEquals(eventRequestModel.getEventDate().getStartDate(),response.getEventDate().getStartDate());
            assertEquals(eventRequestModel.getEventDate().getEndDate(),response.getEventDate().getEndDate());
            assertEquals(eventRequestModel.getEventStatus(),response.getEventStatus());
            assertEquals(participantModelList.size(),response.getParticipants().size());
            assertEquals(sizeDB,eventRepository.count());
        });
    }

}