package com.eventplanner.apigateway.datamapperlayer.participants;


import com.eventplanner.apigateway.presentationlayer.participants.ParticipantController;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantResponseModel;

import org.mapstruct.*;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ParticipantResponseMapper {
ParticipantResponseModel responseModelToResponseModel(ParticipantResponseModel participantResponseModel);
List<ParticipantResponseModel> responseModelListToResponseModelList(List<ParticipantResponseModel> responseModels);
    @AfterMapping
    default void addLinks(@MappingTarget ParticipantResponseModel model) {

        Link selfLink = linkTo(methodOn(ParticipantController.class)
                .getParticipantById(model.getParticipantId()))
                .withSelfRel();
        model.add(selfLink);

        Link participantsLink = linkTo(methodOn(ParticipantController.class).getParticipants())
                .withRel("participants");
        model.add(participantsLink);
    }




}
