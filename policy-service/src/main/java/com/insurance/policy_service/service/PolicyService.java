package com.insurance.policy_service.service;

import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.Policy;
import com.insurance.policy_service.entity.PolicyStatus;

import java.util.List;

public interface PolicyService {

    PolicyDTO createPolicy(PolicyDTO dto);

    PolicyDTO getPolicyById(Long id);

    PolicyDTO getPolicyByNumber(String policyNumber);

    List<PolicyDTO> getAllPolicies();

    List<PolicyDTO> getPoliciesByStatus(PolicyStatus status);

    PolicyDTO updatePolicy(Long id, PolicyDTO dto);

    PolicyDTO updatePolicyStatus(Long id, PolicyStatus status);

    void deletePolicy(Long id);

    List<PolicyDTO> getPoliciesByPartyCode(String partyCode);
}
