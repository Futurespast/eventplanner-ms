package com.eventplanner.events.domainclientlayer.Venue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class VenueModel {
     String venueId;
     String name;
     List<LocalDate> availableDates;
}
