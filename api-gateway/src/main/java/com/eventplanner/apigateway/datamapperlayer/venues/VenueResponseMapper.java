package com.eventplanner.apigateway.datamapperlayer.venues;




import com.eventplanner.apigateway.presentationlayer.venues.VenueController;
import com.eventplanner.apigateway.presentationlayer.venues.VenueResponseModel;
import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface VenueResponseMapper {

    VenueResponseModel responseModelToVenueResponseModel(VenueResponseModel venueResponseModel);

    List<VenueResponseModel> responseModelListToResponseList(List<VenueResponseModel> venueResponseModels);


    @AfterMapping
    default void addLinks(@MappingTarget VenueResponseModel model) {

        Link selfLink = linkTo(methodOn(VenueController.class)
                .getVenuebyId(model.getVenueId()))
                .withSelfRel();
        model.add(selfLink);

        Link venuesLink = linkTo(methodOn(VenueController.class).getVenues())
                .withRel("venues");
        model.add(venuesLink);
    }


}
