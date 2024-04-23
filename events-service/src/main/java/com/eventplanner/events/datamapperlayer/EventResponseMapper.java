package com.eventplanner.events.datamapperlayer;


import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.domainclientlayer.Participant.ParticipantModel;
import com.eventplanner.events.presentationlayer.EventResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface EventResponseMapper {
    @Mapping(expression = "java(event.getEventIdentifier().getEventId())", target = "eventId")
    @Mapping(expression = "java(event.getCustomerModel().getCustomerId())", target = "customerId")
    @Mapping(expression = "java(event.getVenueModel().getVenueId())", target = "venueId")
    @Mapping(expression = "java(event.getCustomerModel().getFirstName())", target = "customerFirstName")
    @Mapping(expression = "java(event.getCustomerModel().getLastName())", target = "customerLastName")
    @Mapping(expression = "java(event.getVenueModel().getName())",target = "venueName")
    @Mapping(expression = "java(event.getEventName())", target = "eventName")
    @Mapping(expression = "java(event.getParticipantModels())",target = "participants")
    EventResponseModel entityToEventResponseModel(Event event);

    List<EventResponseModel> entityToEventResponseList(List<Event> events);
}
