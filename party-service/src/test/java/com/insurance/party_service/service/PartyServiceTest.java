package com.insurance.party_service.service;

import com.insurance.party_service.dto.PartyDTO;
import com.insurance.party_service.entity.Party;
import com.insurance.party_service.entity.PartyType;
import com.insurance.party_service.exception.PartyAlreadyExistsException;
import com.insurance.party_service.exception.PartyNotFoundException;
import com.insurance.party_service.repository.PartyRepository;
import com.insurance.party_service.service.impl.PartyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Party Service Unit Tests")
class PartyServiceTest {

    @Mock
    private PartyRepository partyRepository;

    @InjectMocks
    private PartyServiceImpl partyService;

    private Party party;
    private PartyDTO partyDTO;

    @BeforeEach
    void setUp() {
        party = Party.builder()
                .id(1L)
                .partyCode("PTY-2026-XYZ")
                .partyType(PartyType.INDIVIDUAL)
                .firstName("Madhumitra")
                .lastName("NVS")
                .email("madhu@example.com")
                .phoneNumber("9876543210")
                .city("Bengaluru")
                .state("Karnataka")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        partyDTO = PartyDTO.builder()
                .partyType(PartyType.INDIVIDUAL)
                .firstName("Madhumitra")
                .lastName("NVS")
                .email("madhu@example.com")
                .phoneNumber("9876543210")
                .city("Bengaluru")
                .state("Karnataka")
                .build();
    }

    // ✅ Test 1 — Create party successfully
    @Test
    @DisplayName("Should create party successfully")
    void shouldCreatePartySuccessfully() {
        when(partyRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(partyRepository.existsByPartyCode(anyString()))
                .thenReturn(false);
        when(partyRepository.save(any(Party.class)))
                .thenReturn(party);

        PartyDTO result = partyService.createParty(partyDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Madhumitra");
        assertThat(result.getEmail()).isEqualTo("madhu@example.com");
        verify(partyRepository, times(1)).save(any(Party.class));
    }

    // ✅ Test 2 — Create party with duplicate email
    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        when(partyRepository.existsByEmail("madhu@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> partyService.createParty(partyDTO))
                .isInstanceOf(PartyAlreadyExistsException.class)
                .hasMessageContaining("madhu@example.com");

        verify(partyRepository, never()).save(any());
    }

    // ✅ Test 3 — Get party by ID
    @Test
    @DisplayName("Should return party by ID")
    void shouldReturnPartyById() {
        when(partyRepository.findById(1L))
                .thenReturn(Optional.of(party));

        PartyDTO result = partyService.getPartyById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPartyCode()).isEqualTo("PTY-2026-XYZ");
    }

    // ✅ Test 4 — Get party not found
    @Test
    @DisplayName("Should throw PartyNotFoundException when not found")
    void shouldThrowExceptionWhenPartyNotFound() {
        when(partyRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> partyService.getPartyById(99L))
                .isInstanceOf(PartyNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ✅ Test 5 — Deactivate party
    @Test
    @DisplayName("Should deactivate party successfully")
    void shouldDeactivateParty() {
        when(partyRepository.findById(1L))
                .thenReturn(Optional.of(party));
        when(partyRepository.save(any(Party.class)))
                .thenReturn(party);

        PartyDTO result = partyService.deactivateParty(1L);

        assertThat(result).isNotNull();
        verify(partyRepository, times(1)).save(any(Party.class));
    }

    // ✅ Test 6 — Get all parties
    @Test
    @DisplayName("Should return all parties")
    void shouldReturnAllParties() {
        when(partyRepository.findAll())
                .thenReturn(List.of(party));

        List<PartyDTO> results = partyService.getAllParties();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmail())
                .isEqualTo("madhu@example.com");
    }
}