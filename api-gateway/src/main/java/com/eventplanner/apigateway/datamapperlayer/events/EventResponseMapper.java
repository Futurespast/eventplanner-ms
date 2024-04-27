package com.eventplanner.apigateway.datamapperlayer.events;



import com.eventplanner.apigateway.presentationlayer.events.EventController;
import com.eventplanner.apigateway.presentationlayer.events.EventResponseModel;

import org.mapstruct.*;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EventResponseMapper {

    EventResponseModel entityToEventResponseModel(EventResponseModel eventResponseModel);

    List<EventResponseModel> entityToEventResponseList(List<EventResponseModel> events);

    @AfterMapping
    default void addLinks(@MappingTarget EventResponseModel model) {

        Link selfLink = linkTo(methodOn(EventController.class)
                .getEventById(model.getCustomerId(),model.getEventId()))
                .withSelfRel();
        model.add(selfLink);

        Link venuesLink = linkTo(methodOn(EventController.class).getEvents(model.getCustomerId()))
                .withRel("events");
        model.add(venuesLink);
    }


}
