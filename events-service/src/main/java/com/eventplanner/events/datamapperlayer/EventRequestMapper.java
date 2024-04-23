package com.eventplanner.events.datamapperlayer;


import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.dataacesslayer.EventIdentifier;
import com.eventplanner.events.domainclientlayer.Customer.CustomerModel;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.domainclientlayer.Venue.VenueModel;
import com.eventplanner.events.presentationlayer.EventRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participantModels", ignore = true)
    Event eventRequestModelToEntity(EventRequestModel eventRequestModel, EventIdentifier eventIdentifier, VenueModel venueModel, CustomerModel customerModel);
}
