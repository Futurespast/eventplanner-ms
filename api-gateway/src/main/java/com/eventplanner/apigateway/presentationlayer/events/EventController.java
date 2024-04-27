package com.eventplanner.apigateway.presentationlayer.events;

import com.eventplanner.apigateway.businesslayer.events.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers/{customerId}/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<EventResponseModel>> getEvents(@PathVariable String customerId){
        List<EventResponseModel> eventResponseModels = eventService.getAllEvents(customerId);
        return ResponseEntity.ok().body(eventResponseModels);
    }
    @GetMapping(value = "{eventId}", produces = "application/json")
    public ResponseEntity<EventResponseModel> getEventById(@PathVariable String customerId, @PathVariable String eventId){
        EventResponseModel eventResponseModel = eventService.getEventById(customerId,eventId);
        return ResponseEntity.ok().body(eventResponseModel);
    }
    @PostMapping(produces = "application/json",consumes = "application/json")
    public ResponseEntity<EventResponseModel> addEvent(@RequestBody EventRequestModel eventRequestModel, @PathVariable String customerId){
        EventResponseModel eventResponseModel = eventService.addEvent(eventRequestModel,customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponseModel);
    }

    @PutMapping(value = "{eventId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EventResponseModel> updateEvent(@PathVariable String customerId,@PathVariable String eventId, @RequestBody EventRequestModel eventRequestModel){
        eventService.updateEvent(eventRequestModel,customerId,eventId);
        EventResponseModel eventResponseModel = eventService.getEventById(customerId, eventId);
        return ResponseEntity.ok().body(eventResponseModel);
    }
    @DeleteMapping(value = "{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String customerId,@PathVariable String eventId){
        eventService.deleteEvent(customerId,eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
