package com.eventplanner.events.utils;

import com.eventplanner.events.dataacesslayer.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoaderService implements CommandLineRunner {
    @Autowired
    EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception{

    }
}
