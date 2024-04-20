package com.eventplanner.events.datamapperlayer;


import com.eventplanner.events.dataacesslayer.Event;
import com.eventplanner.events.presentationlayer.EventResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface EventResponseMapper {
    @Mapping(expression = "java(event.getEventIdentifier().getEventId())", target = "eventId")
    @Mapping(expression = "java(event.getCustomerIdentifier().getCustomerId())", target = "customerId")
    @Mapping(expression = "java(event.getVenueIdentifier().getVenueId())", target = "venueId")
    @Mapping(expression = "java(customer.getFirstName())",target = "customerFirstName")
    @Mapping(expression = "java(customer.getLastName())",target = "customerLastName")
    @Mapping(expression = "java(venue.getName())",target = "venueName")
    @Mapping(expression = "java(event.getName())", target = "name")
    EventResponseModel entityToEventResponseModel(Event event, Customer customer, Venue venue);


}
