package com.eventplanner.apigateway.presentationlayer.participants;



import com.eventplanner.apigateway.businesslayer.participants.ParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("api/v1/events/{eventId}/participants")
@RequestMapping("api/v1/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ParticipantResponseModel>> getParticipants(){
        List<ParticipantResponseModel> participantResponseModels = participantService.getAllParticipants();
        return ResponseEntity.ok().body(participantResponseModels);
    }

    @GetMapping(value = "{participantId}", produces = "application/json")
    public ResponseEntity<ParticipantResponseModel> getParticipantById(@PathVariable String participantId){
        ParticipantResponseModel participantResponseModel = participantService.getParticipantById(participantId);
        return ResponseEntity.ok().body(participantResponseModel);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<ParticipantResponseModel> addParticipant(@RequestBody ParticipantRequestModel participantRequestModel){
        ParticipantResponseModel participantResponseModel = participantService.addParticipant(participantRequestModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(participantResponseModel);
    }

    @PutMapping(value = "{participantId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ParticipantResponseModel> updateParticipant(@PathVariable String participantId, @RequestBody ParticipantRequestModel participantRequestModel){
        participantService.updateParticipant(participantRequestModel,participantId);
        ParticipantResponseModel participantResponseModel =  participantService.getParticipantById(participantId);
        return ResponseEntity.ok().body(participantResponseModel);
    }
    @DeleteMapping("{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable String participantId){
        participantService.deleteParticipant(participantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
