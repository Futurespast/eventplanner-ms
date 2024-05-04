package com.eventplanner.apigateway.presentationlayer.events;

import com.eventplanner.apigateway.domainclientlayer.events.EventDate;
import com.eventplanner.apigateway.domainclientlayer.events.ParticipantModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
import static org.springframework.aot.hint.TypeReference.listOf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerIntergrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void init(){
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);

    }

  private final String EVENT_SERVICE_BASE_URL = "http://localhost:7004/api/v1/customers/1/events";

  private final String BASE_EVENT_URL = "/api/v1/customers/1/events";

    List<String> participantIds = new ArrayList<>();
    List<ParticipantModel> participantModels = new ArrayList<>();

    LocalDate date = LocalDate.of(2024, 12, 12);
    LocalDate date2 = LocalDate.of(2024, 12, 13);

    EventDate eventDate = new EventDate(date,date2);
  EventResponseModel eventResponseModel = new EventResponseModel("1","1","1",participantModels,eventDate,"PLANNED","name","description","venueName","firstName","lastName");

  EventRequestModel eventRequestModel = new EventRequestModel("1","1",eventDate,"PLANNED","name","description",participantIds);


  @Test
    public void GetALlEvents() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL))
                .andRespond(withSuccess(mapper.writeValueAsString(List.of(eventResponseModel)), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_EVENT_URL).exchange().expectStatus().isOk().expectBodyList(EventResponseModel.class).value(eventResponseModels -> {
            assertNotNull(eventResponseModels);
            assertEquals(1,eventResponseModels.size());
        });
  }

  @Test
    public void WhenInvalidCustomerIdForGetAllEvents_ThrowInvalidInputException() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_EVENT_URL).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @Test
    public void GetEventById() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL + "/1"))
                .andRespond(withSuccess(mapper.writeValueAsString(eventResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_EVENT_URL + "/1").exchange().expectStatus().isOk().expectBody(EventResponseModel.class).value(eventResponseModel -> {
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
        });
  }

  @Test
    public void WhenInvalidEventIdForGetEventById_ThrowInvalidInputException() throws JsonProcessingException {
      mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL + "/1"))
              .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
      webTestClient.get().uri(BASE_EVENT_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
  }

    @Test
        public void AddEvent() throws JsonProcessingException {
            mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL))
                    .andRespond(withSuccess(mapper.writeValueAsString(eventResponseModel), MediaType.APPLICATION_JSON));
            webTestClient.post().uri(BASE_EVENT_URL).bodyValue(eventRequestModel).exchange().expectStatus().isCreated().expectBody(EventResponseModel.class).value(eventResponseModel -> {
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
            });
    }

    @Test
    public void WhenInvalidEventIdForAddEvent_ThrowInvalidInputException() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(EVENT_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.post().uri(BASE_EVENT_URL).bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void UpdateEvent() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(EVENT_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(mapper.writeValueAsString(eventResponseModel), MediaType.APPLICATION_JSON));
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(EVENT_SERVICE_BASE_URL + "/1"))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(eventResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.put().uri(BASE_EVENT_URL + "/1").bodyValue(eventRequestModel).exchange().expectStatus().isOk().expectBody(EventResponseModel.class).value(eventResponseModel -> {
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
        });
        }

    @Test
    public void WhenInvalidEventIdForUpdateEvent_ThrowInvalidInputException() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(EVENT_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.put().uri(BASE_EVENT_URL + "/1").bodyValue(eventRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void DeleteEvent() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(EVENT_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        webTestClient.delete().uri(BASE_EVENT_URL + "/1").exchange().expectStatus().isNoContent();
    }

    @Test
    public void WhenInvalidEventIdForDeleteEvent_ThrowInvalidInputException() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(EVENT_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.delete().uri(BASE_EVENT_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}