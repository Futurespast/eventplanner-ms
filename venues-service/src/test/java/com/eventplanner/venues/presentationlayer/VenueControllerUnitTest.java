package com.eventplanner.venues.presentationlayer;

import com.eventplanner.venues.businesslayer.VenueService;
import com.eventplanner.venues.dataacesslayer.Location;
import com.eventplanner.venues.dataacesslayer.VenueIdentifier;
import com.eventplanner.venues.utils.NotFoundException;
import com.eventplanner.venues.utils.PastAvailableDateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = VenueController.class)
class VenueControllerUnitTest {
    private final String FOUND_VENUE_ID = "8d996257-e535-4614-98f6-4596be2a3626";
    private final String NOT_FOUND_VENUE_ID = "c3540a89-cb47-4c96-888e-ff96708db4d9";
    private final String INVALID_VENUE_ID = "23djwsdjw-a";

    @Autowired
    VenueController venueController;

    @MockBean
    private VenueService venueService;

    @Test
    public void whenNoVenuesExists_ThenReturnEmptyList(){
        //arrange
        when(venueService.getVenues()).thenReturn(Collections.EMPTY_LIST);

        ResponseEntity<List<VenueResponseModel>> responseEntity = venueController.getVenues();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());
        verify(venueService, times(1)).getVenues();
    }

    @Test
    public void whenVenueExists_thenReturnVenue(){
       VenueRequestModel venueRequestModel = buildVenueRequestModel();
        VenueResponseModel venueResponseModel = buildVenueResponseModel();

        when(venueService.addVenue(venueRequestModel)).thenReturn(venueResponseModel);

        ResponseEntity<VenueResponseModel> responseEntity = venueController.addVenue(venueRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(venueResponseModel, responseEntity.getBody());
        verify(venueService, times(1)).addVenue(venueRequestModel);
    }

    @Test
    public void whenVenueDoesNotExist_thenRespondWithNotFound() {
        // Given
        String nonExistentVenueId = UUID.randomUUID().toString();
        doThrow(new NotFoundException("venueId does not exist "+ nonExistentVenueId))
                .when(venueService).getVenueById(nonExistentVenueId);

        // When
        ResponseEntity<VenueResponseModel> responseEntity = null;
        NotFoundException thrownException = null;
        try {
            responseEntity = venueController.getVenuebyId(nonExistentVenueId);
        } catch (NotFoundException e) {
            thrownException = e;
        }

        // Then
        assertNotNull(thrownException);
        assertNull(responseEntity);
        verify(venueService, times(1)).getVenueById(nonExistentVenueId);
    }



    @Test
    public void postVenueTest(){
        VenueRequestModel venueRequestModel = buildVenueRequestModel();
        VenueResponseModel venueResponseModel = buildVenueResponseModel();

        when(venueService.addVenue(venueRequestModel)).thenReturn(venueResponseModel);

        ResponseEntity<VenueResponseModel> responseEntity = venueController.addVenue(venueRequestModel);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(venueResponseModel, responseEntity.getBody());
        verify(venueService, times(1)).addVenue(venueRequestModel);
    }

    @Test
    public void postCustomerTest_InvalidDateException() {
        // Given
        VenueRequestModel venueRequestModel = buildVenueRequestModel();
        LocalDate date = LocalDate.of(2020, 1, 8);
       List<LocalDate> localDates = new ArrayList<>();
       localDates.add(date);
        venueRequestModel.setAvailableDates(localDates);


        when(venueService.addVenue(any(VenueRequestModel.class)))
                .thenThrow(new PastAvailableDateException(date));

        // When
        Exception exception = assertThrows(PastAvailableDateException.class, () -> {
            venueController.addVenue(venueRequestModel);
        });

        // Then
        assertTrue(exception.getMessage().contains("The provided date " + date + " is in the past and cannot be used as an available date."));
        verify(venueService, times(1)).addVenue(venueRequestModel);
    }


    @Test
    public void updateVenueTest_Positive() {
        String venueId = "8d996257-e535-4614-98f6-4596be2a3626";
        VenueRequestModel venueRequestModel = buildVenueRequestModel();
        VenueResponseModel expectedResponse = buildVenueResponseModel();

        when(venueService.updateVenue(venueRequestModel, venueId)).thenReturn(expectedResponse);

        ResponseEntity<VenueResponseModel> responseEntity = venueController.updateVenue(venueRequestModel, venueId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedResponse, responseEntity.getBody());
        verify(venueService, times(1)).updateVenue(venueRequestModel, venueId);
    }

    @Test
    public void updateCustomerTest_Negative() {
        String venueId = UUID.randomUUID().toString();
        VenueRequestModel venueRequestModel = buildVenueRequestModel();

        when(venueService.updateVenue(venueRequestModel, venueId))
                .thenThrow(new NotFoundException("Venue id does not exist:" + venueId));

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            venueController.updateVenue(venueRequestModel, venueId);
        });

        assertTrue(thrownException.getMessage().contains("Venue id does not exist:"+venueId));
        verify(venueService, times(1)).updateVenue(venueRequestModel, venueId);
    }

    @Test
    public void deleteVenueTest_Positive() {
        VenueIdentifier venueIdentifier = new VenueIdentifier("8d996257-e535-4614-98f6-4596be2a3626");


        doNothing().when(venueService).deleteVenue(venueIdentifier.getVenueId());

        ResponseEntity<Void> responseEntity = venueController.deleteVenue(venueIdentifier.getVenueId());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(venueService, times(1)).deleteVenue(venueIdentifier.getVenueId());
    }

    @Test
    public void deleteCustomerTest_Negative() {
        String venueId = UUID.randomUUID().toString();

        doThrow(new NotFoundException("Venue id does not exist:" + venueId))
                .when(venueService).deleteVenue(venueId);

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            venueController.deleteVenue(venueId);
        });

        assertTrue(thrownException.getMessage().contains("Venue id does not exist:"+ venueId));
        verify(venueService, times(1)).deleteVenue(venueId);
    }


    private VenueRequestModel buildVenueRequestModel(){
        List<LocalDate> dates = new ArrayList<>();
        Location location = new Location("1 street", "City", "Province", "Canada", "aaaaa");
        dates.add(LocalDate.now());

        return VenueRequestModel.builder()
                .location(location)
                .name("venue1000")
                .capacity(1000)
                .availableDates(dates)
                .build();
    }

    private VenueResponseModel buildVenueResponseModel(){
        List<LocalDate> dates = new ArrayList<>();
        Location location = new Location("1 street", "City", "Province", "Canada", "aaaaa");
        dates.add(LocalDate.now());


        return VenueResponseModel.builder()
                .venueId("8d996257-e535-4614-98f6-4596be2a3626")
                .location(location)
                .name("venue1000")
                .capacity(1000)
                .availableDates(dates)
                .build();
    }

}