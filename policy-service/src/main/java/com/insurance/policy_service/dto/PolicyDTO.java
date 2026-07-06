package com.insurance.policy_service.dto;

import com.insurance.policy_service.entity.PolicyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PolicyDTO {

    private Long id;
    private String policyNumber;

    @NotBlank(message = "Holder name is required")
    private String holderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String holderEmail;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium must be positive")
    private Double premiumAmount;

    @NotNull(message = "Coverage amount is required")
    private Double coverageAmount;

    @NotBlank(message = "Policy type is required")
    private String policyType;

    private PolicyStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String partyCode;

}
