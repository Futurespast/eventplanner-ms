package com.eventplanner.venues.businesslayer;


import com.eventplanner.venues.dataacesslayer.Venue;
import com.eventplanner.venues.dataacesslayer.VenueIdentifier;
import com.eventplanner.venues.dataacesslayer.VenueRepository;
import com.eventplanner.venues.datamapperlayer.VenueRequestMapper;
import com.eventplanner.venues.datamapperlayer.VenueResponseMapper;
import com.eventplanner.venues.presentationlayer.VenueRequestModel;
import com.eventplanner.venues.presentationlayer.VenueResponseModel;
import com.eventplanner.venues.utils.NotFoundException;
import com.eventplanner.venues.utils.PastAvailableDateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueRequestMapper venueRequestMapper;
    private final VenueResponseMapper venueResponseMapper;

    public VenueServiceImpl(VenueRepository venueRepository, VenueRequestMapper venueRequestMapper, VenueResponseMapper venueResponseMapper) {
        this.venueRepository = venueRepository;
        this.venueRequestMapper = venueRequestMapper;
        this.venueResponseMapper = venueResponseMapper;
    }

    @Override
    public List<VenueResponseModel> getVenues() {
        List<Venue> venues = venueRepository.findAll();
        return venueResponseMapper.entityListToVenueResponseModel(venues);
    }

    @Override
    public VenueResponseModel getVenueById(String venueId) {
        Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        if(venue == null){
            throw new NotFoundException("venueId does not exist "+venueId);
        }
        return venueResponseMapper.entityToVenueResponseModel(venue);
    }

    @Override
    public VenueResponseModel addVenue(VenueRequestModel venueRequestModel) {
        List<LocalDate> availabledates = venueRequestModel.getAvailableDates();
        LocalDate today = LocalDate.now();
        for (LocalDate date : availabledates ){
            if (date.isBefore(today)) {
                throw new PastAvailableDateException(date);
            }
        }
        Venue venue = venueRequestMapper.venueRequestModelToEntity(venueRequestModel,new VenueIdentifier());
        return venueResponseMapper.entityToVenueResponseModel(venueRepository.save(venue));
    }

    @Override
    public VenueResponseModel updateVenue(VenueRequestModel venueRequestModel, String venueId) {
        Venue existingVenue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        if(existingVenue == null){
            throw new NotFoundException("venueId does not exist "+venueId);
        }
        List<LocalDate> availabledates = venueRequestModel.getAvailableDates();
        LocalDate today = LocalDate.now();
        for (LocalDate date : availabledates ){
            if (date.isBefore(today)) {
                throw new PastAvailableDateException(date);
            }
        }
        Venue updatedVenue = venueRequestMapper.venueRequestModelToEntity(venueRequestModel,new VenueIdentifier(venueId));
        updatedVenue.setId(existingVenue.getId());
       Venue response =  venueRepository.save(updatedVenue);
        return venueResponseMapper.entityToVenueResponseModel(response);
    }

    @Override
    public void deleteVenue(String venueId) {
        Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        if(venue == null){
            throw new NotFoundException("venueId does not exist "+venueId);
        }
        venueRepository.delete(venue);
    }
}
