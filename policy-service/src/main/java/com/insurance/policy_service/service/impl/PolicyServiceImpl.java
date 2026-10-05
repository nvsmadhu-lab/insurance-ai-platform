package com.insurance.policy_service.service.impl;

import com.insurance.policy_service.client.PartyClient;
import com.insurance.policy_service.dto.CoverageDetail;
import com.insurance.policy_service.dto.CoverageSummaryResponse;
import com.insurance.policy_service.dto.PartyResponse;
import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.Policy;
import com.insurance.policy_service.entity.PolicyStatus;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.repository.PolicyRepository;
import com.insurance.policy_service.service.PolicyService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PartyClient partyClient;

    @Override
    public PolicyDTO createPolicy(PolicyDTO dto) {
        System.out.println(">>> partyCode received: " + dto.getPartyCode());
        if(dto.getPartyCode()!=null){
            try{
                PartyResponse party = partyClient.getPartyByCode(dto.getPartyCode());
                if(!party.getActive()){
                    throw new IllegalArgumentException(
                            "cannot create policy for Inactive Party : "
                            +dto.getPartyCode()
                    );
                }
            } catch (FeignException.NotFound e) {
                throw new IllegalArgumentException(
                        "Party not found: " + dto.getPartyCode());
            }
        }

        Policy policy = mapToEntity(dto);
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setStatus(PolicyStatus.PENDING);
        Policy saved = policyRepository.save(policy);
        return mapToDTO(saved);
    }

    @Override
    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found with id: " + id));
        return mapToDTO(policy);
    }

    @Override
    public PolicyDTO getPolicyByNumber(String policyNumber) {
        Policy policy = policyRepository
                .findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found: " + policyNumber));
        return mapToDTO(policy);
    }

    @Override
    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PolicyDTO> getPoliciesByStatus(PolicyStatus status) {
        return policyRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyDTO updatePolicy(Long id, PolicyDTO dto) {
        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found with id: " + id));

        existing.setHolderName(dto.getHolderName());
        existing.setHolderEmail(dto.getHolderEmail());
        existing.setPremiumAmount(dto.getPremiumAmount());
        existing.setCoverageAmount(dto.getCoverageAmount());
        existing.setPolicyType(dto.getPolicyType());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());

        return mapToDTO(policyRepository.save(existing));
    }

    @Override
    public PolicyDTO updatePolicyStatus(Long id, PolicyStatus status) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found with id: " + id));
        policy.setStatus(status);
        return mapToDTO(policyRepository.save(policy));
    }

    @Override
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new PolicyNotFoundException(
                    "Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    @Override
    public String generatePolicyNumber() {
        String number;
        do {
            number = "POL-" + java.time.Year.now().getValue()
                    + "-" + UUID.randomUUID()
                    .toString().substring(0, 6).toUpperCase();
        } while (policyRepository.existsByPolicyNumber(number));
        return number;
    }

    @Override
    public List<PolicyDTO> getPoliciesByPartyCode(String partyCode) {

        // First verify party exists
        try {
            partyClient.getPartyByCode(partyCode);
        } catch (FeignException.NotFound e) {
            throw new PolicyNotFoundException(
                    "Party not found: " + partyCode);
        }

        return policyRepository.findByPartyCode(partyCode)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PolicyDTO> getPoliciesByHolderName(String holderName){
        if(!policyRepository.existsByHolderName(holderName)){
            throw new PolicyNotFoundException(
                    "policy not found with HolderName : " + holderName);
        }
        return policyRepository.findByHolderName(holderName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CoverageSummaryResponse getCoverageByPolicyNumber(String policyNumber) {

        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy is not found by the given policy number : "+policyNumber
                ));


        List<CoverageDetail> coverageDetail = policy.getCoverages()
                .stream().map(
                        c -> new CoverageDetail(
                                c.getCoverageType(),c.getLimitAmount(),c.getDeductible()
                        )).sorted(Comparator.comparing(CoverageDetail::limit).reversed())
                .toList();

        BigDecimal total = coverageDetail.stream()
                .map(CoverageDetail::limit).reduce(BigDecimal.ZERO,BigDecimal::add);

        LocalDate today = LocalDate.now();

        boolean valid = policy.getStatus() == PolicyStatus.ACTIVE
                && policy.getStartDate() != null && policy.getEndDate() != null
                && !today.isBefore(policy.getStartDate())
                && !today.isAfter(policy.getEndDate());

        long dayRemaining = valid
                ? ChronoUnit.DAYS.between(today, policy.getEndDate())
                : 0;

        return new CoverageSummaryResponse(
                policy.getPolicyNumber(),
                policy.getStatus(),
                policy.getStartDate(),
                policy.getEndDate(),
                valid,
                dayRemaining,
                total,
                coverageDetail);

    }

    private PolicyDTO mapToDTO(Policy policy) {
        return PolicyDTO.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .holderName(policy.getHolderName())
                .holderEmail(policy.getHolderEmail())
                .premiumAmount(policy.getPremiumAmount())
                .coverageAmount(policy.getCoverageAmount())
                .policyType(policy.getPolicyType())
                .status(policy.getStatus())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .partyCode(policy.getPartyCode())
                .build();
    }

    private Policy mapToEntity(PolicyDTO dto) {
        return Policy.builder()
                .holderName(dto.getHolderName())
                .holderEmail(dto.getHolderEmail())
                .premiumAmount(dto.getPremiumAmount())
                .coverageAmount(dto.getCoverageAmount())
                .policyType(dto.getPolicyType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .partyCode(dto.getPartyCode())
                .build();
    }
}
