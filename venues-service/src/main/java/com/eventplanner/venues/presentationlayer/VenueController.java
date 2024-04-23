package com.eventplanner.venues.presentationlayer;


import com.eventplanner.venues.businesslayer.VenueService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<VenueResponseModel>> getVenues() {
        return ResponseEntity.ok().body(venueService.getVenues());
    }

    @GetMapping(value = "/{venueId}", produces = "application/json")
    public ResponseEntity<VenueResponseModel> getVenuebyId(@PathVariable String venueId) {
        return ResponseEntity.ok().body(venueService.getVenueById(venueId));
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<VenueResponseModel> addVenue(@RequestBody VenueRequestModel venueRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.addVenue(venueRequestModel));
    }

    @PutMapping(value= "/{venueId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<VenueResponseModel> updateVenue(@RequestBody VenueRequestModel venueRequestModel, @PathVariable String venueId) {
        return ResponseEntity.ok().body(venueService.updateVenue(venueRequestModel,venueId));
    }

    @PatchMapping(value= "/{venueId}/{startDate}/{endDate}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<VenueResponseModel> updateAvailableVenueDates(@PathVariable String venueId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate){
        return  ResponseEntity.ok().body(venueService.changeVenueDates(venueId,startDate,endDate));
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable String venueId) {
        venueService.deleteVenue(venueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
