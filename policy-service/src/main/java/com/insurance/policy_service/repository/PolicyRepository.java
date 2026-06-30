package com.insurance.policy_service.repository;

import com.insurance.policy_service.entity.Policy;
import com.insurance.policy_service.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy,Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByStatus(PolicyStatus status);

    List<Policy> findByHolderEmail(String email);

    List<Policy> findByPolicyType(String policyType);

    boolean existsByPolicyNumber(String policyNumber);

}
