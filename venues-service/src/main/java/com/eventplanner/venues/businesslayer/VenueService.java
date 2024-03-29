package com.eventplanner.venues.businesslayer;



import com.eventplanner.venues.presentationlayer.VenueRequestModel;
import com.eventplanner.venues.presentationlayer.VenueResponseModel;

import java.util.List;

public interface VenueService {
    List<VenueResponseModel> getVenues();

    VenueResponseModel getVenueById(String venueId);

    VenueResponseModel addVenue(VenueRequestModel venueRequestModel);

    VenueResponseModel updateVenue(VenueRequestModel venueRequestModel, String venueId);

    void deleteVenue(String venueId);

}
