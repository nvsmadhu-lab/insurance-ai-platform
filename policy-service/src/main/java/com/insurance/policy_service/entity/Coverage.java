package com.insurance.policy_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coverages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageType coverageType;

    @Column(nullable = false, precision = 15, scale = 2 )
    private BigDecimal limitAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal deductible;

    @ManyToOne(fetch  = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

}
