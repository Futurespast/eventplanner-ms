package com.eventplanner.venues.businesslayer;



import com.eventplanner.venues.presentationlayer.VenueRequestModel;
import com.eventplanner.venues.presentationlayer.VenueResponseModel;

import java.time.LocalDate;
import java.util.List;

public interface VenueService {
    List<VenueResponseModel> getVenues();

    VenueResponseModel getVenueById(String venueId);

    VenueResponseModel addVenue(VenueRequestModel venueRequestModel);

    VenueResponseModel updateVenue(VenueRequestModel venueRequestModel, String venueId);

    VenueResponseModel patchForPostVenueDates(String venueId, LocalDate start, LocalDate end);

    VenueResponseModel patchForPutVenueDates(String venueId, LocalDate addStart, LocalDate AddEnd, LocalDate removeStart, LocalDate removeEnd);

    void deleteVenue(String venueId);

}
