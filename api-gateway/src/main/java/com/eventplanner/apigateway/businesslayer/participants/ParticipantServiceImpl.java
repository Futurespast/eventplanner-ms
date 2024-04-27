package com.eventplanner.apigateway.businesslayer.participants;

import com.eventplanner.apigateway.datamapperlayer.participants.ParticipantResponseMapper;
import com.eventplanner.apigateway.domainclientlayer.participants.ParticipantsServiceClient;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantRequestModel;
import com.eventplanner.apigateway.presentationlayer.participants.ParticipantResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ParticipantServiceImpl implements ParticipantService{

    private final ParticipantsServiceClient participantsServiceClient;
    private final ParticipantResponseMapper participantResponseMapper;

    public ParticipantServiceImpl(ParticipantsServiceClient participantsServiceClient, ParticipantResponseMapper participantResponseMapper) {
        this.participantsServiceClient = participantsServiceClient;
        this.participantResponseMapper = participantResponseMapper;
    }

    @Override
    public List<ParticipantResponseModel> getAllParticipants() {
        return participantResponseMapper.responseModelListToResponseModelList(participantsServiceClient.getAllParticipants());
    }

    @Override
    public ParticipantResponseModel getParticipantById(String participantId) {
        return participantResponseMapper.responseModelToResponseModel(participantsServiceClient.getParticipantById(participantId));
    }

    @Override
    public ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel) {
        return participantResponseMapper.responseModelToResponseModel(participantsServiceClient.addParticipant(participantRequestModel));
    }

    @Override
    public void updateParticipant(ParticipantRequestModel participantRequestModel, String participantId) {
      participantsServiceClient.updateParticipant(participantRequestModel,participantId);
    }

    @Override
    public void deleteParticipant(String participantId) {
        participantsServiceClient.deleteParticipant(participantId);
    }
}
