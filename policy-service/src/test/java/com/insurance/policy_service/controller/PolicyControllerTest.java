package com.insurance.policy_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.PolicyStatus;
import com.insurance.policy_service.exception.PolicyNotFoundException;
import com.insurance.policy_service.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
@DisplayName("Policy Controller Tests")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Create manually instead of @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PolicyService policyService;

    private PolicyDTO policyDTO;

    @BeforeEach
    void setUp() {
        policyDTO = PolicyDTO.builder()
                .id(1L)
                .policyNumber("POL-2026-ABC123")
                .holderName("Madhumitra NVS")
                .holderEmail("madhu@example.com")
                .premiumAmount(12000.00)
                .coverageAmount(500000.00)
                .policyType("HEALTH")
                .partyCode("PTY-2026-XYZ")
                .status(PolicyStatus.PENDING)
                .build();
    }

    // ✅ Test 1 — POST /api/policies
    @Test
    @DisplayName("Should create policy and return 201")
    void shouldCreatePolicy() throws Exception {
        when(policyService.createPolicy(any(PolicyDTO.class)))
                .thenReturn(policyDTO);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber")
                        .value("POL-2026-ABC123"))
                .andExpect(jsonPath("$.holderName")
                        .value("Madhumitra NVS"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));
    }

    // ✅ Test 2 — GET /api/policies/{id}
    @Test
    @DisplayName("Should return policy by ID")
    void shouldReturnPolicyById() throws Exception {
        when(policyService.getPolicyById(1L))
                .thenReturn(policyDTO);

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.policyNumber")
                        .value("POL-2026-ABC123"));
    }

    // ✅ Test 3 — GET /api/policies/{id} not found
    @Test
    @DisplayName("Should return 404 when policy not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(policyService.getPolicyById(99L))
                .thenThrow(new PolicyNotFoundException(
                        "Policy not found with id: 99"));

        mockMvc.perform(get("/api/policies/99"))
                .andExpect(status().isNotFound());
    }

    // ✅ Test 4 — GET /api/policies
    @Test
    @DisplayName("Should return all policies")
    void shouldReturnAllPolicies() throws Exception {
        when(policyService.getAllPolicies())
                .thenReturn(List.of(policyDTO));

        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].policyNumber")
                        .value("POL-2026-ABC123"));
    }

    // ✅ Test 5 — PATCH /api/policies/{id}/status
    @Test
    @DisplayName("Should update policy status")
    void shouldUpdatePolicyStatus() throws Exception {
        policyDTO.setStatus(PolicyStatus.ACTIVE);
        when(policyService.updatePolicyStatus(1L, PolicyStatus.ACTIVE))
                .thenReturn(policyDTO);

        mockMvc.perform(patch("/api/policies/1/status")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    // ✅ Test 6 — DELETE /api/policies/{id}
    @Test
    @DisplayName("Should delete policy and return 204")
    void shouldDeletePolicy() throws Exception {
        doNothing().when(policyService).deletePolicy(1L);

        mockMvc.perform(delete("/api/policies/1"))
                .andExpect(status().isNoContent());
    }

    // ✅ Test 7 — POST validation failure
    @Test
    @DisplayName("Should return 400 when required fields missing")
    void shouldReturn400WhenValidationFails() throws Exception {
        PolicyDTO invalidDTO = PolicyDTO.builder()
                .holderName("")  // blank — should fail validation
                .build();

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}