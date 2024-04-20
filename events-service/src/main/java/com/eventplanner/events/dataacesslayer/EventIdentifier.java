package com.eventplanner.events.dataacesslayer;


import lombok.Getter;

import java.util.UUID;

@Getter
public class EventIdentifier {
    private String eventId;

    public EventIdentifier(){this.eventId= UUID.randomUUID().toString();}
    public EventIdentifier(String eventId){this.eventId=eventId;}
}
