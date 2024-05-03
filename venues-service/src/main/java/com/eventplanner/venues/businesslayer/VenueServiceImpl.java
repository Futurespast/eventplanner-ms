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
import java.util.ArrayList;
import java.util.Iterator;
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
    public VenueResponseModel patchForPostVenueDates(String venueId, LocalDate start, LocalDate end) {
        Venue existingVenue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        if(existingVenue == null){
            throw new NotFoundException("venueId does not exist "+venueId);
        }
        List<LocalDate> dates = new ArrayList<>(existingVenue.getAvailableDates());
        Iterator<LocalDate> dateIterator = dates.iterator();

        while (dateIterator.hasNext()) {
            LocalDate date = dateIterator.next();
            if ((date.isEqual(start) && start.isEqual(end)) || date.isEqual(start) || date.isEqual(end)) {
                dateIterator.remove();
            }
        }
        existingVenue.setAvailableDates(dates);
        return venueResponseMapper.entityToVenueResponseModel(venueRepository.save(existingVenue));
    }

    @Override
    public VenueResponseModel patchForPutVenueDates(String venueId, LocalDate addStart, LocalDate addEnd, LocalDate removeStart, LocalDate removeEnd) {
        Venue existingVenue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        if(existingVenue == null){
            throw new NotFoundException("venueId does not exist "+venueId);
        }

        List<LocalDate> currentDates = existingVenue.getAvailableDates();
        List<LocalDate> datesToRemove = new ArrayList<>();
        List<LocalDate> datesToAdd = new ArrayList<>();


        if (!removeStart.isEqual(removeEnd)) {
            for (LocalDate date : currentDates) {
                if (date.isEqual(removeStart) || date.isEqual(removeEnd)) {
                    datesToRemove.add(date);
                }
            }
        } else {
            for (LocalDate date : currentDates) {
                if (date.isEqual(removeStart)) {
                    datesToRemove.add(date);
                }
            }
        }


        if (!addStart.isEqual(addEnd)) {
            datesToAdd.add(addStart);
            datesToAdd.add(addEnd);
        } else {
            datesToAdd.add(addStart);
        }


        currentDates.removeAll(datesToRemove);


        currentDates.addAll(datesToAdd);

        existingVenue.setAvailableDates(currentDates);
        return venueResponseMapper.entityToVenueResponseModel(venueRepository.save(existingVenue));

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
