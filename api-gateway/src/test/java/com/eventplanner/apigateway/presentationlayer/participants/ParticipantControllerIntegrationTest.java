package com.eventplanner.apigateway.presentationlayer.participants;


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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ParticipantControllerIntegrationTest {
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

    private final String PARTICIPANT_SERVICE_BASE_URL = "http://localhost:7003/api/v1/participants";

    private final String BASE_PARTICIPANT_URL = "/api/v1/participants";

    ParticipantResponseModel participantResponseModel = new ParticipantResponseModel("1","firstName","lastName","email","note");

    ParticipantRequestModel participantRequestModel = new ParticipantRequestModel("firstName","lastName","email","note");

    @Test
    public void GetAllParticipants() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(List.of(participantResponseModel)), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_PARTICIPANT_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ParticipantResponseModel.class).value(lists -> {
                    assertNotNull(lists);
                    assertEquals(1,lists.size());
                });
    }

    @Test
    public void GetParticipantById() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL+"/1")))
                .andRespond(withSuccess(mapper.writeValueAsString(participantResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_PARTICIPANT_URL+"/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParticipantResponseModel.class).value(participant -> {
                    assertNotNull(participant);
                    assertEquals("1",participant.getParticipantId());
                    assertEquals("firstName",participant.getFirstName());
                    assertEquals("lastName",participant.getLastName());
                    assertEquals("email",participant.getEmailAddress());
                    assertEquals("note",participant.getSpecialNote());
                });
    }

    @Test
    public void WhenInvalidParticipantId_ReturnNotFound(){
        mockRestServiceServer.expect(requestTo(PARTICIPANT_SERVICE_BASE_URL + "/1"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_PARTICIPANT_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void AddParticipant() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(participantResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.post().uri(BASE_PARTICIPANT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(participantRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ParticipantResponseModel.class).value(participant -> {
                    assertNotNull(participant);
                    assertEquals("1",participant.getParticipantId());
                    assertEquals("firstName",participant.getFirstName());
                    assertEquals("lastName",participant.getLastName());
                    assertEquals("email",participant.getEmailAddress());
                    assertEquals("note",participant.getSpecialNote());
                });
    }

    @Test
    public void WhenInvalidParticipantIdForAddParticipant_ThrowInvalidInputException() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(PARTICIPANT_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.post().uri(BASE_PARTICIPANT_URL).bodyValue(participantRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void UpdateParticipant() throws JsonProcessingException, URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL+ "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(mapper.writeValueAsString(participantResponseModel), MediaType.APPLICATION_JSON));
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL + "/1"))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(participantResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.put().uri(BASE_PARTICIPANT_URL + "/1").bodyValue(participantRequestModel).exchange().expectStatus().isOk().expectBody(ParticipantResponseModel.class).value(participant -> {
            assertNotNull(participant);
            assertEquals("1",participant.getParticipantId());
            assertEquals("firstName",participant.getFirstName());
            assertEquals("lastName",participant.getLastName());
            assertEquals("email",participant.getEmailAddress());
            assertEquals("note",participant.getSpecialNote());
        });
        }

    @Test
    public void WhenInvalidParticipantIdForUpdateParticipant_ThrowInvalidInputException() throws JsonProcessingException {
        mockRestServiceServer.expect(requestTo(PARTICIPANT_SERVICE_BASE_URL + "/1"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.put().uri(BASE_PARTICIPANT_URL + "/1").bodyValue(participantRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void DeleteParticipant() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(PARTICIPANT_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        webTestClient.delete().uri(BASE_PARTICIPANT_URL + "/1").exchange().expectStatus().isNoContent();
    }

    @Test
    public void WhenInvalidParticipantIdForDeleteParticipant_ThrowInvalidInputException() throws URISyntaxException {
        mockRestServiceServer.expect(requestTo(PARTICIPANT_SERVICE_BASE_URL + "/1"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.delete().uri(BASE_PARTICIPANT_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void WhenGetAllHasAnError_ThrowException() throws URISyntaxException {
        mockRestServiceServer.expect(requestTo(PARTICIPANT_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_PARTICIPANT_URL).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}