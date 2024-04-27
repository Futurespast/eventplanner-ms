package com.eventplanner.apigateway.presentationlayer.venues;



import com.eventplanner.apigateway.domainclientlayer.venues.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestModel {
private Location location;
private String name;
private Integer capacity;
private List<LocalDate> availableDates;
}
