package com.eventplanner.events.datamapperlayer;


import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.dataacesslayer.EventIdentifier;
import com.eventplanner.events.presentationlayer.EventRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {
    @Mapping(target = "id", ignore = true)
    Event eventRequestModelToEntity(EventRequestModel eventRequestModel, EventIdentifier eventIdentifier, VenueIdentifier venueIdentifier, CustomerIdentifier customerIdentifier);
}
