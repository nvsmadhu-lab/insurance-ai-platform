package com.insurance.party_service.Dto;

import com.insurance.party_service.entity.PartyType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyDTO {

    private Long id;
    private String partyCode;

    @Enumerated(EnumType.STRING)
    private PartyType partyType;

    // Individual fields
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;

    // Corporate fields
    private String companyName;
    private String registrationNumber;

    // Common fields
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
