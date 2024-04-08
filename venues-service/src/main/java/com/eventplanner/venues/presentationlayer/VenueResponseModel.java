package com.eventplanner.venues.presentationlayer;


import com.eventplanner.venues.dataacesslayer.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenueResponseModel{
    private String venueId;
    private Location location;
    private String name;
    private Integer capacity;
    private List<LocalDate> availableDates;
}
