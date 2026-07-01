package com.insurance.party_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="parties", schema="party")
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String partyCode;

    @Enumerated(EnumType.STRING)
    private PartyType partyType;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;

    // Corporate fields
    private String companyName;
    private String registrationNumber;

    // Common fields
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private Boolean active;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) active = true;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
