package com.eventplanner.venues.dataacesslayer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="venues")
@Data
@NoArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private VenueIdentifier venueIdentifier;

    @Embedded
    private Location location;

    private String name;

    private Integer capacity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "venue_available_dates", joinColumns = @JoinColumn(name="venue_id"))
   private List<LocalDate> availableDates;

    public Venue(@NotNull Location location, @NotNull String name, @NotNull Integer capacity, @NotNull List<LocalDate> availableDates) {
        this.venueIdentifier = new VenueIdentifier();
        this.location = location;
        this.name = name;
        this.capacity = capacity;
        this.availableDates = availableDates;
    }
}
