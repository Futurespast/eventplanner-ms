package com.eventplanner.venues.datamapperlayer;



import com.eventplanner.venues.dataacesslayer.Venue;
import com.eventplanner.venues.presentationlayer.VenueResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
//import org.springframework.hateoas.Link;

import java.util.List;

//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface VenueResponseMapper {
    @Mapping(expression = "java(venue.getVenueIdentifier().getVenueId())", target = "venueId")
    VenueResponseModel entityToVenueResponseModel(Venue venue);

    List<VenueResponseModel> entityListToVenueResponseModel(List<Venue> venues);

    /*
    @AfterMapping
    default void addLinks(@MappingTarget VenueResponseModel model, Venue venue) {

        Link selfLink = linkTo(methodOn(VenueController.class)
                .getVenuebyId(model.getVenueId()))
                .withSelfRel();
        model.add(selfLink);

        Link venuesLink = linkTo(methodOn(VenueController.class).getVenues())
                .withRel("venues");
        model.add(venuesLink);
    }

     */
}
