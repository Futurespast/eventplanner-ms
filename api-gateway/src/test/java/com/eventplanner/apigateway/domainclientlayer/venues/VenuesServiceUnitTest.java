package com.eventplanner.apigateway.domainclientlayer.venues;

import com.eventplanner.apigateway.businesslayer.venues.VenueService;
import com.eventplanner.apigateway.datamapperlayer.venues.VenueResponseMapper;
import com.eventplanner.apigateway.presentationlayer.venues.VenueRequestModel;
import com.eventplanner.apigateway.presentationlayer.venues.VenueResponseModel;
import com.eventplanner.apigateway.utils.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class VenuesServiceUnitTest {

    @Autowired
    VenueService venueService;

    @MockBean
    VenuesServiceClient venuesServiceClient;

    @SpyBean
    VenueResponseMapper venueResponseMapper;

    Location location = new Location("address","city","province","country","postalCode");

    List<LocalDate> availableDates = List.of(LocalDate.of(2021, 12, 12), LocalDate.of(2021, 12, 13));

    VenueResponseModel venueResponseModel = new VenueResponseModel("1", location, "name",1000, availableDates);

    VenueRequestModel venueRequestModel = new VenueRequestModel(location, "name",1000, availableDates);

    @Test
    public void GetAllVenues(){
        when(venuesServiceClient.getVenues()).thenReturn(List.of(venueResponseModel));
        List<VenueResponseModel> venueResponseModels = venueService.getAllVenues();
        assertNotNull(venueResponseModels);
        assertEquals(1,venueResponseModels.size());
    }

    @Test
    public void GetVenueById(){
        when(venuesServiceClient.getVenueByVenueId("1")).thenReturn(venueResponseModel);
        VenueResponseModel venueResponseModel = venueService.getVenueById("1");
        assertNotNull(venueResponseModel);
        assertEquals("1",venueResponseModel.getVenueId());
        assertEquals("name",venueResponseModel.getName());
        assertEquals(1000,venueResponseModel.getCapacity());
        assertEquals(location,venueResponseModel.getLocation());
        assertEquals(availableDates,venueResponseModel.getAvailableDates());
    }

    @Test
    public void WhenInvalidIdForGet(){
        when(venuesServiceClient.getVenueByVenueId("1")).thenThrow(new NotFoundException("Venue not found"));
        assertThrows(NotFoundException.class, () -> venueService.getVenueById("1"));
    }

    @Test
    public void AddVenue() {
        when(venuesServiceClient.addVenue(venueRequestModel)).thenReturn(venueResponseModel);
        VenueResponseModel venueResponseModel = venueService.addVenue(venueRequestModel);
        assertNotNull(venueResponseModel);
        assertEquals("1", venueResponseModel.getVenueId());
        assertEquals("name", venueResponseModel.getName());
        assertEquals(1000, venueResponseModel.getCapacity());
        assertEquals(location, venueResponseModel.getLocation());
    }

    @Test
    public void WhenInvalidIdForAdd(){
        when(venuesServiceClient.addVenue(venueRequestModel)).thenThrow(new NotFoundException("Venue not found"));
        assertThrows(NotFoundException.class, () -> venueService.addVenue(venueRequestModel));
    }

    @Test
    public void UpdateVenue() {
        doNothing().when(venuesServiceClient).updateVenue(venueRequestModel, "1");
        venueService.updateVenue(venueRequestModel, "1");
    }

    @Test
    public void WhenInvalidIdForUpdate(){
        doThrow(new NotFoundException("Venue not found")).when(venuesServiceClient).updateVenue(venueRequestModel, "1");
        assertThrows(NotFoundException.class, () -> venueService.updateVenue(venueRequestModel, "1"));
    }

    @Test
    public void DeleteVenue() {
        doNothing().when(venuesServiceClient).deleteVenue("1");
        venueService.deleteVenue("1");
    }

    @Test
    public void WhenInvalidIdForDelete(){
      doThrow(new NotFoundException("Venue not found")).when(venuesServiceClient).deleteVenue("1");
        assertThrows(NotFoundException.class, () -> venueService.deleteVenue("1"));
    }

}