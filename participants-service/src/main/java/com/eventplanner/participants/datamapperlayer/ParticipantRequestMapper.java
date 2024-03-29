package com.eventplanner.participants.datamapperlayer;


import com.eventplanner.participants.dataacesslayer.Participant;
import com.eventplanner.participants.dataacesslayer.ParticipantIdentifier;
import com.eventplanner.participants.presentationlayer.ParticipantRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipantRequestMapper {
    @Mapping(target = "id", ignore = true)
    Participant participantRequestModelToEntity(ParticipantRequestModel participantRequestModel, ParticipantIdentifier participantIdentifier);
}
