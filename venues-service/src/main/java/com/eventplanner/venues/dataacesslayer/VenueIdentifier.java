package com.eventplanner.venues.dataacesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class VenueIdentifier {
    private String venueId;

    public VenueIdentifier(){
        this.venueId= UUID.randomUUID().toString();
    }

    public VenueIdentifier(String venueId){
        this.venueId=venueId;
    }

}
