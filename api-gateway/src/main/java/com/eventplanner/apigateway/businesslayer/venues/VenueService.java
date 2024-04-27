package com.eventplanner.apigateway.businesslayer.venues;

import com.eventplanner.apigateway.presentationlayer.venues.VenueRequestModel;
import com.eventplanner.apigateway.presentationlayer.venues.VenueResponseModel;

import java.util.List;

public interface VenueService {
    List<VenueResponseModel> getAllVenues();
    VenueResponseModel getVenueById(String venueId);

    VenueResponseModel addVenue(VenueRequestModel venueRequestModel);

    void updateVenue(VenueRequestModel venueRequestModel, String venueId);

    void deleteVenue(String venueId);
}
