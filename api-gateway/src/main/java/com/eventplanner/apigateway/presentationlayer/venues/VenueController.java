package com.eventplanner.apigateway.presentationlayer.venues;



import com.eventplanner.apigateway.businesslayer.venues.VenueService;
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
        return ResponseEntity.ok().body(venueService.getAllVenues());
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
        venueService.updateVenue(venueRequestModel,venueId);
        VenueResponseModel venueResponseModel = venueService.getVenueById(venueId);
        return ResponseEntity.ok().body(venueResponseModel);
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable String venueId) {
        venueService.deleteVenue(venueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
