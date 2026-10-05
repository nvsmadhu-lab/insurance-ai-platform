package com.insurance.policy_service.service;

import com.insurance.policy_service.client.PartyClient;
import com.insurance.policy_service.dto.CoverageDetail;
import com.insurance.policy_service.dto.CoverageSummaryResponse;
import com.insurance.policy_service.dto.PartyResponse;
import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.Coverage;
import com.insurance.policy_service.entity.CoverageType;
import com.insurance.policy_service.entity.Policy;
import com.insurance.policy_service.entity.PolicyStatus;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.repository.PolicyRepository;
import com.insurance.policy_service.service.impl.PolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Policy Service Unit Tests")
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PartyClient partyClient;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy policy;
    private PolicyDTO policyDTO;
    private PartyResponse partyResponse;

    @BeforeEach
    void setUp() {
        policy = Policy.builder()
                .id(1L)
                .policyNumber("POL-2026-ABC123")
                .holderName("Madhumitra NVS")
                .holderEmail("madhu@example.com")
                .premiumAmount(12000.00)
                .coverageAmount(500000.00)
                .policyType("HEALTH")
                .partyCode("PTY-2026-XYZ")
                .status(PolicyStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        policyDTO = PolicyDTO.builder()
                .holderName("Madhumitra NVS")
                .holderEmail("madhu@example.com")
                .premiumAmount(12000.00)
                .coverageAmount(500000.00)
                .policyType("HEALTH")
                .partyCode("PTY-2026-XYZ")
                .build();

        partyResponse = PartyResponse.builder()
                .id(1L)
                .partyCode("PTY-2026-XYZ")
                .partyType("INDIVIDUAL")
                .firstName("Madhumitra")
                .lastName("NVS")
                .email("madhu@example.com")
                .active(true)
                .build();
    }

    // ✅ Test 1 — Create policy successfully
    @Test
    @DisplayName("Should create policy successfully when party is active")
    void shouldCreatePolicySuccessfully() {
        when(partyClient.getPartyByCode("PTY-2026-XYZ"))
                .thenReturn(partyResponse);
        when(policyRepository.existsByPolicyNumber(anyString()))
                .thenReturn(false);
        when(policyRepository.save(any(Policy.class)))
                .thenReturn(policy);

        PolicyDTO result = policyService.createPolicy(policyDTO);

        assertThat(result).isNotNull();
        assertThat(result.getHolderName()).isEqualTo("Madhumitra NVS");
        assertThat(result.getPolicyType()).isEqualTo("HEALTH");
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    // ✅ Test 2 — Create policy with inactive party
    @Test
    @DisplayName("Should throw exception when party is inactive")
    void shouldThrowExceptionWhenPartyIsInactive() {
        partyResponse.setActive(false);
        when(partyClient.getPartyByCode("PTY-2026-XYZ"))
                .thenReturn(partyResponse);

        assertThatThrownBy(() -> policyService.createPolicy(policyDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot create policy for Inactive Party");

        verify(policyRepository, never()).save(any());
    }

    // ✅ Test 3 — Get policy by ID successfully
    @Test
    @DisplayName("Should return policy when found by ID")
    void shouldReturnPolicyById() {
        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        PolicyDTO result = policyService.getPolicyById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPolicyNumber())
                .isEqualTo("POL-2026-ABC123");
        assertThat(result.getHolderEmail())
                .isEqualTo("madhu@example.com");
    }

    // ✅ Test 4 — Get policy by ID not found
    @Test
    @DisplayName("Should throw PolicyNotFoundException when ID not found")
    void shouldThrowExceptionWhenPolicyNotFound() {
        when(policyRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.getPolicyById(99L))
                .isInstanceOf(PolicyNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ✅ Test 5 — Get all policies
    @Test
    @DisplayName("Should return all policies")
    void shouldReturnAllPolicies() {
        when(policyRepository.findAll())
                .thenReturn(List.of(policy));

        List<PolicyDTO> results = policyService.getAllPolicies();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPolicyNumber())
                .isEqualTo("POL-2026-ABC123");
    }

    // ✅ Test 6 — Update policy status
    @Test
    @DisplayName("Should update policy status successfully")
    void shouldUpdatePolicyStatus() {
        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class)))
                .thenReturn(policy);

        PolicyDTO result = policyService
                .updatePolicyStatus(1L, PolicyStatus.ACTIVE);

        assertThat(result).isNotNull();
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    // ✅ Test 7 — Delete policy
    @Test
    @DisplayName("Should delete policy successfully")
    void shouldDeletePolicy() {
        when(policyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(policyRepository).deleteById(1L);

        assertThatCode(() -> policyService.deletePolicy(1L))
                .doesNotThrowAnyException();

        verify(policyRepository, times(1)).deleteById(1L);
    }

    // ✅ Test 8 — Delete policy not found
    @Test
    @DisplayName("Should throw exception when deleting non-existent policy")
    void shouldThrowExceptionWhenDeletingNonExistentPolicy() {
        when(policyRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> policyService.deletePolicy(99L))
                .isInstanceOf(PolicyNotFoundException.class);

        verify(policyRepository, never()).deleteById(any());
    }

    // A small helper so each test can add coverages in one line.
// It sets coverage.policy AND adds to policy.coverages, so both sides of the
// two-way relationship agree, the same way JPA would see real data.
    private void addCoverage(CoverageType type, String limit, String deductible) {
        Coverage c = Coverage.builder()
                .coverageType(type)
                .limitAmount(new BigDecimal(limit))      // String constructor: exact value.
                .deductible(new BigDecimal(deductible))  // new BigDecimal(0.1) would bring back the Double error
                .policy(policy)
                .build();
        policy.getCoverages().add(c);  // works only because of @Builder.Default; otherwise the list is null
    }

    @Test
    @DisplayName("Should return coverage summary for an active policy")
    void shouldReturnCoverageSummaryForActivePolicy() {
        // Dates relative to today, not hard-coded, so the test still passes next year
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now().minusMonths(1));
        policy.setEndDate(LocalDate.now().plusDays(30));
        addCoverage(CoverageType.CRITICAL_ILLNESS, "300000.00", "0.00");
        addCoverage(CoverageType.HOSPITALIZATION, "500000.00", "5000.00");

        // Mock only the repository. We're testing the service's logic, not the database
        when(policyRepository.findByPolicyNumber("POL-2026-ABC123"))
                .thenReturn(Optional.of(policy));

        CoverageSummaryResponse result =
                policyService.getCoverageByPolicyNumber("POL-2026-ABC123");

        // Records use field-name accessors: currentlyValid(), not getCurrentlyValid()
        assertThat(result.currentlyValid()).isTrue();
        assertThat(result.daysRemaining()).isEqualTo(30);

        // isEqualByComparingTo, NOT isEqualTo: BigDecimal.equals also compares scale,
        // so 800000.00 and 800000 are "not equal" by equals()
        assertThat(result.totalCoverageLimit())
                .isEqualByComparingTo(new BigDecimal("800000"));

        // Added in the order CRITICAL_ILLNESS, HOSPITALIZATION, but the service sorts by
        // limit, largest first. This checks the Comparator actually ran.
        assertThat(result.coverages())
                .extracting(CoverageDetail::type)
                .containsExactly(CoverageType.HOSPITALIZATION, CoverageType.CRITICAL_ILLNESS);
    }

    @Test
    @DisplayName("Should mark expired policy as not valid")
    void shouldMarkExpiredPolicyAsNotValid() {
        // Edge case: the policy exists and has coverages, but its dates are in the past.
        // A claims system must not treat this policy as covering anything today.
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now().minusYears(2));
        policy.setEndDate(LocalDate.now().minusYears(1));
        addCoverage(CoverageType.HOSPITALIZATION, "500000.00", "5000.00");

        when(policyRepository.findByPolicyNumber("POL-2026-ABC123"))
                .thenReturn(Optional.of(policy));

        CoverageSummaryResponse result =
                policyService.getCoverageByPolicyNumber("POL-2026-ABC123");

        assertThat(result.currentlyValid()).isFalse();
        assertThat(result.daysRemaining()).isZero();
    }

    @Test
    @DisplayName("Should throw when policy number does not exist")
    void shouldThrowWhenPolicyNotFound() {
        // Optional.empty() simulates "no row found", which triggers orElseThrow in the service
        when(policyRepository.findByPolicyNumber("POL-UNKNOWN"))
                .thenReturn(Optional.empty());

        // assertThatThrownBy runs the lambda and checks that it throws the expected exception
        assertThatThrownBy(() -> policyService.getCoverageByPolicyNumber("POL-UNKNOWN"))
                .isInstanceOf(PolicyNotFoundException.class)
                .hasMessageContaining("POL-UNKNOWN");
    }
}