package com.eventplanner.apigateway.presentationlayer.venues;

import com.eventplanner.apigateway.domainclientlayer.venues.Location;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantResponseModel;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VenueControllerIntegrationTest {

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

    private final String VENUE_SERVICE_BASE_URL = "http://localhost:7002/api/v1/venues";

    private final String BASE_VENUE_URL = "/api/v1/venues";

    Location location = new Location("address","city","province","country","postalCode");

    List<LocalDate> availableDates = List.of(LocalDate.of(2021, 12, 12), LocalDate.of(2021, 12, 13));

    VenueResponseModel venueResponseModel = new VenueResponseModel("1", location, "name",1000, availableDates);

    VenueRequestModel venueRequestModel = new VenueRequestModel(location, "name",1000, availableDates);

    @Test
    public void GetAllVenues() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(VENUE_SERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(List.of(venueResponseModel)), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_VENUE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VenueResponseModel.class).value(lists -> {
                    assertNotNull(lists);
                    assertEquals(1,lists.size());
                });
    }

    @Test
    public void GetVenueById() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(VENUE_SERVICE_BASE_URL+"/1")))
                .andRespond(withSuccess(mapper.writeValueAsString(venueResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_VENUE_URL+"/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(VenueResponseModel.class).value(venue -> {
                    assertNotNull(venue);
                    assertEquals("1",venue.getVenueId());
                    assertEquals("name",venue.getName());
                    assertEquals(1000,venue.getCapacity());
                });
    }

    @Test
    public void WhenInvalidVenueId_ReturnNotFound(){
        mockRestServiceServer.expect(requestTo(VENUE_SERVICE_BASE_URL + "/1"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_VENUE_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void AddVenue() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(),requestTo(new URI(VENUE_SERVICE_BASE_URL)))
                .andRespond(withSuccess(mapper.writeValueAsString(venueResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.post().uri(BASE_VENUE_URL)
                .bodyValue(venueRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VenueResponseModel.class).value(venue -> {
                    assertNotNull(venue);
                    assertEquals("1",venue.getVenueId());
                    assertEquals("name",venue.getName());
                    assertEquals(1000,venue.getCapacity());
                });
    }

    @Test
    public void WhenInvalidVenueIdForAddVenue_ThrowNotFoundException() throws URISyntaxException {
        mockRestServiceServer.expect(requestTo(VENUE_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.post().uri(BASE_VENUE_URL)
                .bodyValue(venueRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void UpdateVenue() throws URISyntaxException, JsonProcessingException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(VENUE_SERVICE_BASE_URL+ "/1")))
                .andExpect(method(HttpMethod.PUT)).andRespond(withSuccess(mapper.writeValueAsString(venueResponseModel), MediaType.APPLICATION_JSON));
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(VENUE_SERVICE_BASE_URL + "/1"))).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mapper.writeValueAsString(venueResponseModel), MediaType.APPLICATION_JSON));
        webTestClient.put().uri(BASE_VENUE_URL + "/1").bodyValue(venueRequestModel).exchange().expectStatus().isOk().expectBody(VenueResponseModel.class).value(venue -> {
            assertNotNull(venue);
            assertEquals("1",venue.getVenueId());
            assertEquals("name",venue.getName());
            assertEquals(1000,venue.getCapacity());
        });
    }

   @Test
   public void WhenInvalidVenueIdForUpdate_ThrowNotFoundException() throws URISyntaxException {
       mockRestServiceServer.expect(requestTo(VENUE_SERVICE_BASE_URL + "/1"))
               .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
       webTestClient.put().uri(BASE_VENUE_URL + "/1").bodyValue(venueRequestModel).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
   }

    @Test
    public void DeleteVenue() throws URISyntaxException {
        mockRestServiceServer.expect(ExpectedCount.once(), requestTo(new URI(VENUE_SERVICE_BASE_URL + "/1")))
                .andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        webTestClient.delete().uri(BASE_VENUE_URL + "/1").exchange().expectStatus().isNoContent();
    }

    @Test
    public void WhenInvalidVenueIdForDelete_ThrowNotFoundException() throws URISyntaxException {
        mockRestServiceServer.expect(requestTo(VENUE_SERVICE_BASE_URL + "/1"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.delete().uri(BASE_VENUE_URL + "/1").exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void WhenGetAllHasAnError_ThrowException() throws URISyntaxException {
        mockRestServiceServer.expect(requestTo(VENUE_SERVICE_BASE_URL))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON));
        webTestClient.get().uri(BASE_VENUE_URL).exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}