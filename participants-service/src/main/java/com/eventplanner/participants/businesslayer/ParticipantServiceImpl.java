package com.eventplanner.participants.businesslayer;

import com.eventplanner.participants.dataacesslayer.Participant;
import com.eventplanner.participants.dataacesslayer.ParticipantIdentifier;
import com.eventplanner.participants.utils.*;
import com.eventplanner.participants.dataacesslayer.ParticipantRepository;
import com.eventplanner.participants.datamapperlayer.ParticipantRequestMapper;
import com.eventplanner.participants.datamapperlayer.ParticipantResponseMapper;
import com.eventplanner.participants.presentationlayer.ParticipantRequestModel;
import com.eventplanner.participants.presentationlayer.ParticipantResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantResponseMapper participantResponseMapper;
    private final ParticipantRequestMapper participantRequestMapper;

    public ParticipantServiceImpl(ParticipantRepository participantRepository, ParticipantResponseMapper participantResponseMapper, ParticipantRequestMapper participantRequestMapper) {
        this.participantRepository = participantRepository;
        this.participantResponseMapper = participantResponseMapper;
        this.participantRequestMapper = participantRequestMapper;
    }

    @Override
    public List<ParticipantResponseModel> getParticipants() {
        return participantResponseMapper.entityListToParticipationResponseModelList(participantRepository.findAll());
    }

    @Override
    public ParticipantResponseModel getParticipantById(String participantId) {
        Participant participant = participantRepository.findParticipantByParticipantIdentifier_ParticipantId(participantId);
        if (participant == null){
            throw  new NotFoundException("Participant id does not exist:"+participantId);
        }
        return participantResponseMapper.entityToParticipantResponseModel(participant);
    }

    @Override
    public ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel) {
        if (!isValidEmail(participantRequestModel.getEmailAddress())) {
            throw new InvalidEmailAddressException(participantRequestModel.getEmailAddress());
        }
        Participant participant = participantRequestMapper.participantRequestModelToEntity(participantRequestModel, new ParticipantIdentifier());
        Participant savedParticipant = participantRepository.save(participant);
        return participantResponseMapper.entityToParticipantResponseModel(savedParticipant);
    }

    @Override
    public ParticipantResponseModel updateParticipant(ParticipantRequestModel participantRequestModel, String participantId) {
        Participant oldParticipant = participantRepository.findParticipantByParticipantIdentifier_ParticipantId(participantId);
        if(oldParticipant == null){
            throw new NotFoundException("Participant id does not exist:"+participantId);
        }
        if (!isValidEmail(participantRequestModel.getEmailAddress())) {
            throw new InvalidEmailAddressException(participantRequestModel.getEmailAddress());
        }
        Participant newParticipant = participantRequestMapper.participantRequestModelToEntity(participantRequestModel,new ParticipantIdentifier(participantId));
        newParticipant.setId(oldParticipant.getId());
        Participant participant = participantRepository.save(newParticipant);
        return participantResponseMapper.entityToParticipantResponseModel(participant);
    }

    @Override
    public void deleteParticipant(String participantId) {
        Participant participant = participantRepository.findParticipantByParticipantIdentifier_ParticipantId(participantId);
        if (participant == null){
            throw new NotFoundException("Participant id does not exist:"+participantId);
        }
        participantRepository.delete(participant);
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
  /*  @Override
    public List<ParticipantResponseModel> getParticipants(String eventId) {
        return participantResponseMapper.entityListToParticipationResponseModelList(participantRepository.findAllByEventIdentifier_EventId(eventId));
    }

    @Override
    public ParticipantResponseModel getParticipantById(String eventId,String participantId) {
       Participant participant = participantRepository.findParticipantByEventIdentifier_EventIdAndParticipantIdentifier_ParticipantId(eventId, participantId);
       if (participant == null){
           throw  new NotFoundException("Participant id does not exist:"+participantId);
       }
        return participantResponseMapper.entityToParticipantResponseModel(participant);
    }

    @Override
    public ParticipantResponseModel addParticipant(ParticipantRequestModel participantRequestModel, String eventId) {
        Participant participant = participantRequestMapper.participantRequestModelToEntity(participantRequestModel, new ParticipantIdentifier(), new EventIdentifier(eventId));
        Participant savedParticipant = participantRepository.save(participant);
        return participantResponseMapper.entityToParticipantResponseModel(savedParticipant);

    }

    @Override
    public ParticipantResponseModel updateParticipant(ParticipantRequestModel participantRequestModel, String eventId,String participantId) {
        Participant oldParticipant = participantRepository.findParticipantByEventIdentifier_EventIdAndParticipantIdentifier_ParticipantId(eventId, participantId);
        if(oldParticipant == null){
            throw new NotFoundException("Participant id does not exist:"+participantId);
        }
        Participant newParticipant = participantRequestMapper.participantRequestModelToEntity(participantRequestModel,new ParticipantIdentifier(participantId), new EventIdentifier(eventId));
        newParticipant.setId(oldParticipant.getId());
        Participant participant = participantRepository.save(newParticipant);
        return participantResponseMapper.entityToParticipantResponseModel(participant);
    }

    @Override
    public void deleteParticipant(String eventId,String participantId) {
        Participant participant = participantRepository.findParticipantByEventIdentifier_EventIdAndParticipantIdentifier_ParticipantId(eventId,participantId);
        if (participant == null){
            throw new NotFoundException("Participant id does not exist:"+participantId);
        }
        participantRepository.delete(participant);
    }

   */
}
