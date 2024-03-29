package com.eventplanner.participants.dataacesslayer;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="participants")
@Data
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private ParticipantIdentifier participantIdentifier;

  /*  @Embedded
    private EventIdentifier eventIdentifier;

   */

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String specialNote;

    public Participant(@NotNull String firstName, @NotNull String lastName, @NotNull String emailAddress, String specialNote) {
        this.participantIdentifier = new ParticipantIdentifier();
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.specialNote = specialNote;
    }
}
