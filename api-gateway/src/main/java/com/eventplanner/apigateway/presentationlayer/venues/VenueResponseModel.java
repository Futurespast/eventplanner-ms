package com.eventplanner.apigateway.presentationlayer.venues;


import com.eventplanner.apigateway.domainclientlayer.venues.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenueResponseModel extends RepresentationModel<VenueResponseModel> {
    private String venueId;
    private Location location;
    private String name;
    private Integer capacity;
    private List<LocalDate> availableDates;
}
