package com.insurance.policy_service.dto;

import com.insurance.policy_service.entity.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CoverageSummaryResponse(
        String policyNumber,
        PolicyStatus status,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyValid,
        long daysRemaining,
        BigDecimal totalCoverageLimit,
        List<CoverageDetail> coverages
) {
}
