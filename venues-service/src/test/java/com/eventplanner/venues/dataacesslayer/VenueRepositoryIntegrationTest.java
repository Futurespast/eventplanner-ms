package com.eventplanner.venues.dataacesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class VenueRepositoryIntegrationTest {

    @Autowired
 private VenueRepository venueRepository;


    @BeforeEach
    public void setUpDb(){
       venueRepository.deleteAll();}

    @Test
    public void whenVenueExist_ReturnVenueByVenueId(){
        //arrange
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now());
        Venue venue = new Venue(new Location("1 street", "City", "Province", "Canada", "aaaaa"),"venue",100,dates);
        venueRepository.save(venue);
        //act
        Venue saved = venueRepository.findVenueByVenueIdentifier_VenueId(venue.getVenueIdentifier().getVenueId());
        //assert
        assertNotNull(venue);
        assertEquals(saved.getVenueIdentifier(), venue.getVenueIdentifier());
        assertEquals(saved.getLocation(), venue.getLocation());
        assertEquals(saved.getName(), venue.getName());
        assertEquals(saved.getAvailableDates(), venue.getAvailableDates());
    }
    @Test
    public void whenVenueDoesNotExist_ReturnNull(){
        //arrange
        String venueId = UUID.randomUUID().toString();
        //act
        Venue venue = venueRepository.findVenueByVenueIdentifier_VenueId(venueId);
        //assert
        assertNull(venue);
    }

}