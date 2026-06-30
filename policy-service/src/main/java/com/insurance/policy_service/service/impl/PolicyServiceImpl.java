package com.insurance.policy_service.service.impl;

import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.Policy;
import com.insurance.policy_service.entity.PolicyStatus;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.repository.PolicyRepository;
import com.insurance.policy_service.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyDTO createPolicy(PolicyDTO dto) {
        Policy policy = mapToEntity(dto);
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setStatus(PolicyStatus.PENDING);
        Policy saved = policyRepository.save(policy);
        return mapToDTO(saved);
    }

    public PolicyDTO getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found with id: " + id));
        return mapToDTO(policy);
    }

    public PolicyDTO getPolicyByNumber(String policyNumber) {
        Policy policy = policyRepository
                .findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found: " + policyNumber));
        return mapToDTO(policy);
    }

    public List<PolicyDTO> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PolicyDTO> getPoliciesByStatus(PolicyStatus status) {
        return policyRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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

    public PolicyDTO updatePolicyStatus(Long id, PolicyStatus status) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(
                        "Policy not found with id: " + id));
        policy.setStatus(status);
        return mapToDTO(policyRepository.save(policy));
    }

    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new PolicyNotFoundException(
                    "Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    private String generatePolicyNumber() {
        String number;
        do {
            number = "POL-" + java.time.Year.now().getValue()
                    + "-" + UUID.randomUUID()
                    .toString().substring(0, 6).toUpperCase();
        } while (policyRepository.existsByPolicyNumber(number));
        return number;
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
                .build();
    }
}
