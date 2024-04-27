package com.eventplanner.events.presentationlayer;

import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.dataacesslayer.EventRepository;
import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
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
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
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

    private ObjectMapper mapper = new ObjectMapper();

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
        Event event = events.stream().filter(e -> e.getEventIdentifier().getEventId().equals("499075b0-7761-4684-9bbc-16b9a0079837")).findFirst().get();
        assertNotNull(event);
        String url = EVENT_BASE_URI + "/" + customerModel.getCustomerId()+ "/events/499075b0-7761-4684-9bbc-16b9a0079837";
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


}