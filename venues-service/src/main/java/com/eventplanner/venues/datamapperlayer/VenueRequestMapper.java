package com.eventplanner.venues.datamapperlayer;


import com.eventplanner.venues.dataacesslayer.Venue;
import com.eventplanner.venues.dataacesslayer.VenueIdentifier;
import com.eventplanner.venues.presentationlayer.VenueRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueRequestMapper {

    @Mapping(target = "id", ignore = true)
    Venue venueRequestModelToEntity(VenueRequestModel venueRequestModel, VenueIdentifier venueIdentifier);


}
