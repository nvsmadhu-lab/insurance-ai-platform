package com.insurance.policy_service.dto;

import com.insurance.policy_service.entity.CoverageType;

import java.math.BigDecimal;

public record CoverageDetail(
        CoverageType type,
        BigDecimal limit,
        BigDecimal deductible
) {
}
