package com.insurance.policy_service.controller;

import com.insurance.policy_service.dto.PolicyDTO;
import com.insurance.policy_service.entity.PolicyStatus;
import com.insurance.policy_service.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyDTO> createPolicy(
            @Valid @RequestBody PolicyDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyService.createPolicy(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @GetMapping("/number/{policyNumber}")
    public ResponseEntity<PolicyDTO> getByNumber(@PathVariable String policyNumber){
        return ResponseEntity.ok(policyService.getPolicyByNumber(policyNumber));
    }

    @GetMapping
    public ResponseEntity<List<PolicyDTO>> getAll(){
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PolicyDTO>> getByStatus(
            @PathVariable PolicyStatus status) {
        return ResponseEntity.ok(
                policyService.getPoliciesByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PolicyDTO dto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PolicyDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam PolicyStatus status) {
        return ResponseEntity.ok(
                policyService.updatePolicyStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/party/{partyCode}")
    public ResponseEntity<List<PolicyDTO>> getByPartyCode(
            @PathVariable String partyCode) {
        return ResponseEntity.ok(
                policyService.getPoliciesByPartyCode(partyCode));
    }
}
