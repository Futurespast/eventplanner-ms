package com.eventplanner.apigateway.businesslayer.venues;

import com.eventplanner.apigateway.datamapperlayer.venues.VenueResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.venues.VenuesServiceClient;
import com.eventplanner.apigateway.presentationlayer.venues.VenueRequestModel;
import com.eventplanner.apigateway.presentationlayer.venues.VenueResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {
    private final VenueResponseMapper venueResponseMapper;
    private final VenuesServiceClient venuesServiceClient;

    public VenueServiceImpl(VenueResponseMapper venueResponseMapper, VenuesServiceClient venuesServiceClient) {
        this.venueResponseMapper = venueResponseMapper;
        this.venuesServiceClient = venuesServiceClient;
    }

    @Override
    public List<VenueResponseModel> getAllVenues() {
        return venueResponseMapper.responseModelListToResponseList(venuesServiceClient.getVenues());
    }

    @Override
    public VenueResponseModel getVenueById(String venueId) {
        return venueResponseMapper.responseModelToVenueResponseModel(venuesServiceClient.getVenueByVenueId(venueId));
    }

    @Override
    public VenueResponseModel addVenue(VenueRequestModel venueRequestModel) {
        return venueResponseMapper.responseModelToVenueResponseModel(venuesServiceClient.addVenue(venueRequestModel));
    }

    @Override
    public void updateVenue(VenueRequestModel venueRequestModel, String venueId) {
        venuesServiceClient.updateVenue(venueRequestModel,venueId);
    }

    @Override
    public void deleteVenue(String venueId) {
        venuesServiceClient.deleteVenue(venueId);
    }
}
