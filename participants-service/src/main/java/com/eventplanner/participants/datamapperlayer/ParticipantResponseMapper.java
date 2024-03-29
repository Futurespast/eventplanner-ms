package com.eventplanner.participants.datamapperlayer;


import com.eventplanner.participants.dataacesslayer.Participant;
import com.eventplanner.participants.presentationlayer.ParticipantResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
//import org.springframework.hateoas.Link;

import java.util.List;

//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface ParticipantResponseMapper {
    @Mapping(expression = "java(participant.getParticipantIdentifier().getParticipantId())", target = "participantId")
    //@Mapping(expression = "java(participant.getEventIdentifier().getEventId())", target = "eventId")
    ParticipantResponseModel entityToParticipantResponseModel(Participant participant);

    List<ParticipantResponseModel> entityListToParticipationResponseModelList(List<Participant> participants);

  /*  @AfterMapping
    default void addLinks(@MappingTarget ParticipantResponseModel model, Participant participant) {

        Link selfLink = linkTo(methodOn(ParticipantController.class)
                .getParticipantById(model.getEventId(),model.getParticipantId()))
                .withSelfRel();
        model.add(selfLink);

        Link participantsLink = linkTo(methodOn(ParticipantController.class).getParticipants(model.getEventId()))
                .withRel("participants");
        model.add(participantsLink);
    }


   */

}
