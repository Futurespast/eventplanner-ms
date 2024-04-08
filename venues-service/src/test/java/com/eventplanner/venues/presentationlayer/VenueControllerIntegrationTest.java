package com.eventplanner.venues.presentationlayer;

import com.eventplanner.venues.dataacesslayer.Location;
import com.eventplanner.venues.dataacesslayer.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("h2")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VenueControllerIntegrationTest {
    private final String BASE_URI_VENUE = "/api/v1/venues";
    private final String FOUND_VENUE_ID = "8d996257-e535-4614-98f6-4596be2a3626";
    private final String FOUND_VENUE_NAME = "Venue 1";
    private final Location FOUND_VENUE_LOCATION = new Location("123 Maple St","CityA","ProvinceA","CountryA","A1A 1A1");
    private final int FOUND_VENUE_CAPACITY = 100;
    private final String NOT_FOUND_VENUE_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";




    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenGetVenues_thenReturnALLVenues(){
        //arrange
        long sizeDB = venueRepository.count();

        //act and assert
        webTestClient.get().uri(BASE_URI_VENUE).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(VenueResponseModel.class).value((list)->{
            assertNotNull(list);
            assertTrue(list.size() == sizeDB);
        });

    }

    @Test
    public void whenVenueDoesNotExist_thenReturnNotFound(){
        //act + assert
        webTestClient.get().uri(BASE_URI_VENUE +"/"+ NOT_FOUND_VENUE_ID).accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("venueId does not exist "+ NOT_FOUND_VENUE_ID);
    }

    @Test
    public void whenValidVenue_thenCreateVenue() {
        //arrange
        long sizeDB = venueRepository.count();
        LocalDate date = LocalDate.of(2025, 1, 8);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        VenueRequestModel venueRequestModel = new VenueRequestModel(FOUND_VENUE_LOCATION,FOUND_VENUE_NAME,FOUND_VENUE_CAPACITY,localDates);

        webTestClient.post().uri(BASE_URI_VENUE).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .bodyValue(venueRequestModel).exchange().expectStatus().isCreated().expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(VenueResponseModel.class).value((venueResponseModel -> {
                    assertNotNull(venueResponseModel);
                    assertEquals(FOUND_VENUE_LOCATION.getStreetAddress(), venueResponseModel.getLocation().getStreetAddress());
                    assertEquals(FOUND_VENUE_LOCATION.getCity(), venueResponseModel.getLocation().getCity());
                    assertEquals(FOUND_VENUE_LOCATION.getProvince(), venueResponseModel.getLocation().getProvince());
                    assertEquals(FOUND_VENUE_LOCATION.getCountry(), venueResponseModel.getLocation().getCountry());
                    assertEquals(FOUND_VENUE_LOCATION.getPostalCode(), venueResponseModel.getLocation().getPostalCode());
                    assertEquals(venueRequestModel.getName(), venueResponseModel.getName());
                    assertEquals(venueRequestModel.getCapacity(),venueResponseModel.getCapacity());
                    assertEquals(venueRequestModel.getAvailableDates(), venueResponseModel.getAvailableDates());
                }));
        long sizeDBAfter = venueRepository.count();
        assertEquals(sizeDB + 1, sizeDBAfter );
    }

    @Test
    public void whenVenueExists_thenReturnVenueDetails() {
        // Arrange
        String foundVenueId = FOUND_VENUE_ID;
        LocalDate date = LocalDate.of(2024, 10, 01);
        LocalDate date2 = LocalDate.of(2024, 10, 02);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        localDates.add(date2);

        // Act + Assert
        webTestClient.get().uri(BASE_URI_VENUE + "/" + foundVenueId).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(VenueResponseModel.class).value((venueResponseModel -> {

                    assertNotNull(venueResponseModel);
                    assertEquals(FOUND_VENUE_NAME, venueResponseModel.getName());
                    assertEquals(FOUND_VENUE_LOCATION.getStreetAddress(), venueResponseModel.getLocation().getStreetAddress());
                    assertEquals(FOUND_VENUE_LOCATION.getCity(), venueResponseModel.getLocation().getCity());
                    assertEquals(FOUND_VENUE_LOCATION.getProvince(), venueResponseModel.getLocation().getProvince());
                    assertEquals(FOUND_VENUE_LOCATION.getCountry(), venueResponseModel.getLocation().getCountry());
                    assertEquals(FOUND_VENUE_LOCATION.getPostalCode(), venueResponseModel.getLocation().getPostalCode());
                    assertEquals(FOUND_VENUE_CAPACITY,venueResponseModel.getCapacity());
                    assertEquals(localDates,venueResponseModel.getAvailableDates());

                }));
    }

    @Test
    public void WhenUpdateCustomerValid_thenReturnUpdatedCustomer() {
        // Arrange
        String foundVenueId = FOUND_VENUE_ID;
        LocalDate date = LocalDate.of(2024, 10, 01);
        LocalDate date2 = LocalDate.of(2024, 10, 02);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        localDates.add(date2);
        VenueRequestModel updateRequest = new VenueRequestModel(FOUND_VENUE_LOCATION, "Ven",10000,localDates);

        // Act & Assert
        webTestClient.put().uri(BASE_URI_VENUE + "/" + foundVenueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VenueResponseModel.class).value((venueResponseModel -> {

                    assertNotNull(venueResponseModel);
                    assertEquals(FOUND_VENUE_LOCATION.getStreetAddress(), venueResponseModel.getLocation().getStreetAddress());
                    assertEquals(FOUND_VENUE_LOCATION.getCity(), venueResponseModel.getLocation().getCity());
                    assertEquals(FOUND_VENUE_LOCATION.getProvince(), venueResponseModel.getLocation().getProvince());
                    assertEquals(FOUND_VENUE_LOCATION.getCountry(), venueResponseModel.getLocation().getCountry());
                    assertEquals(FOUND_VENUE_LOCATION.getPostalCode(), venueResponseModel.getLocation().getPostalCode());
                    assertEquals(updateRequest.getCapacity(),venueResponseModel.getCapacity());
                    assertEquals(updateRequest.getAvailableDates(),venueResponseModel.getAvailableDates());
                    assertEquals(updateRequest.getName(),venueResponseModel.getName());
                }));
    }

    @Test
    public void updateVenueDoesNotExist_ThrowNotFound() {
        // Arrange
        String nonExistentVenueId = "112432dhxf-24";
        LocalDate date = LocalDate.of(2024, 10, 01);
        LocalDate date2 = LocalDate.of(2024, 10, 02);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        localDates.add(date2);
        VenueRequestModel updateRequest = new VenueRequestModel(FOUND_VENUE_LOCATION, "Ven",10000,localDates);


        // Act & Assert
        webTestClient.put().uri(BASE_URI_VENUE + "/" + nonExistentVenueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("venueId does not exist " + nonExistentVenueId);
    }

    @Test
    public void updateVenue_InvalidDate() {
        // Arrange
        String venueId = FOUND_VENUE_ID;
        LocalDate date = LocalDate.of(2023, 10, 01);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        VenueRequestModel updateRequest = new VenueRequestModel(FOUND_VENUE_LOCATION, "Ven",10000,localDates);


        // Act & Assert
        webTestClient.put().uri(BASE_URI_VENUE + "/" + venueId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("The provided date " + date + " is in the past and cannot be used as an available date.");
    }


    @Test
    public void whenValidCustomerButInvalidDate_thenThrowUnprocessableEntity() {
        // Arrange
        long sizeDB = venueRepository.count();
        LocalDate date = LocalDate.of(2023, 10, 01);
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(date);
        VenueRequestModel venueRequestModel = new VenueRequestModel(FOUND_VENUE_LOCATION, "Ven",10000,localDates);

        // Act & Assert
        webTestClient.post().uri(BASE_URI_VENUE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(venueRequestModel)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("UNPROCESSABLE_ENTITY")
                .jsonPath("$.message").isEqualTo("The provided date " + date + " is in the past and cannot be used as an available date.");

        long sizeDBAfter = venueRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
    }

    @Test
    public void deleteVenueThatExist_ReturnNoContent() {
        // Arrange
        String foundVenueId = FOUND_VENUE_ID;
        long sizeDB = venueRepository.count();

        // Act & Assert
        webTestClient.delete().uri(BASE_URI_VENUE + "/" + foundVenueId)
                .exchange()
                .expectStatus().isNoContent();

        long sizeDBAfter = venueRepository.count();
        assertEquals(sizeDB - 1, sizeDBAfter );

    }

    @Test
    public void deleteVenueThatDoesNotExist_ReturnNotFound() {
        // Arrange
        String nonExistentVenueId = "q13232shd2";
        long sizeDB = venueRepository.count();
        // Act & Assert
        webTestClient.delete().uri(BASE_URI_VENUE + "/" + nonExistentVenueId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.httpStatus").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("venueId does not exist " + nonExistentVenueId);

        long sizeDBAfter = venueRepository.count();
        assertEquals(sizeDB, sizeDBAfter );
    }

}