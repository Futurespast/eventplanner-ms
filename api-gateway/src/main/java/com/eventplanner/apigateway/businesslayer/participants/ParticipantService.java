package com.eventplanner.apigateway.businesslayer.participants;

import com.eventplanner.apigateway.presentationlayer.participants.ParticipantRequestModel;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantResponseModel;

import java.util.List;

public interface ParticipantService {
    List<ParticipantResponseModel> getAllParticipants();
    ParticipantResponseModel getParticipantById(String participantId);

    ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel);

    void updateParticipant(ParticipantRequestModel participantRequestModel, String participantId);

    void deleteParticipant(String participantId);
}
