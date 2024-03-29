package com.eventplanner.participants.businesslayer;



import com.eventplanner.participants.presentationlayer.ParticipantRequestModel;
import com.eventplanner.participants.presentationlayer.ParticipantResponseModel;

import java.util.List;

public interface ParticipantService {

    //List<ParticipantResponseModel> getParticipants(String eventId);

  //  ParticipantResponseModel getParticipantById( String eventId,String participantId);

   // ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel, String eventId);

   // ParticipantResponseModel updateParticipant(ParticipantRequestModel participantRequestModel, String eventId,String participantId);

   // void deleteParticipant(String eventId, String participantId);

    List<ParticipantResponseModel> getParticipants();
    ParticipantResponseModel getParticipantById(String participantId);

    ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel);

    ParticipantResponseModel updateParticipant(ParticipantRequestModel participantRequestModel,String participantId);

    void deleteParticipant(String participantId);


}
