package com.insurance.party_service.service;

import com.insurance.party_service.Dto.PartyDTO;
import com.insurance.party_service.entity.Party;
import com.insurance.party_service.entity.PartyType;
import com.insurance.party_service.exception.PartyAlreadyExistsException;
import com.insurance.party_service.exception.PartyNotFoundException;
import com.insurance.party_service.repository.PartyRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService{

    private final PartyRepository partyRepository;

    public PartyDTO createParty(PartyDTO dto){
        if (partyRepository.existsByEmail(dto.getEmail())) {
            throw new PartyAlreadyExistsException(
                    "Party already exists with email: " + dto.getEmail());
        }
        Party party = mapToEntity(dto);
        party.setPartyCode(generatePartyCode());
        return mapToDTO(partyRepository.save(party));
    }

    public PartyDTO getPartyById(Long id) {
        return mapToDTO(partyRepository.findById(id)
                .orElseThrow(() -> new PartyNotFoundException(
                        "Party not found with id: " + id)));
    }

    public PartyDTO getPartyByCode(String partyCode) {
        return mapToDTO(partyRepository.findByPartyCode(partyCode)
                .orElseThrow(() -> new PartyNotFoundException(
                        "Party not found: " + partyCode)));
    }

    public PartyDTO getPartyByEmail(String email) {
        return mapToDTO(partyRepository.findByEmail(email)
                .orElseThrow(() -> new PartyNotFoundException(
                        "Party not found with email: " + email)));
    }

    public List<PartyDTO> getAllParties() {
        return partyRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PartyDTO> getPartiesByType(PartyType partyType) {
        return partyRepository.findByPartyType(partyType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PartyDTO updateParty(Long id, PartyDTO dto) {
        Party existing = partyRepository.findById(id)
                .orElseThrow(() -> new PartyNotFoundException(
                        "Party not found with id: " + id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setCompanyName(dto.getCompanyName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPincode(dto.getPincode());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setGender(dto.getGender());

        return mapToDTO(partyRepository.save(existing));
    }

    public PartyDTO deactivateParty(Long id) {
        Party party = partyRepository.findById(id)
                .orElseThrow(() -> new PartyNotFoundException(
                        "Party not found with id: " + id));
        party.setActive(false);
        return mapToDTO(partyRepository.save(party));
    }

    public void deleteParty(Long id) {
        if (!partyRepository.existsById(id)) {
            throw new PartyNotFoundException(
                    "Party not found with id: " + id);
        }
        partyRepository.deleteById(id);
    }


    private String generatePartyCode() {
        String code;
        do {
            code = "PTY-" + java.time.Year.now().getValue()
                    + "-" + UUID.randomUUID()
                    .toString().substring(0, 6).toUpperCase();
        } while (partyRepository.existsByPartyCode(code));
        return code;
    }

    private PartyDTO mapToDTO(Party party) {
        return PartyDTO.builder()
                .id(party.getId())
                .partyCode(party.getPartyCode())
                .partyType(party.getPartyType())
                .firstName(party.getFirstName())
                .lastName(party.getLastName())
                .dateOfBirth(party.getDateOfBirth())
                .gender(party.getGender())
                .companyName(party.getCompanyName())
                .registrationNumber(party.getRegistrationNumber())
                .email(party.getEmail())
                .phoneNumber(party.getPhoneNumber())
                .address(party.getAddress())
                .city(party.getCity())
                .state(party.getState())
                .pincode(party.getPincode())
                .active(party.getActive())
                .createdAt(party.getCreatedAt())
                .updatedAt(party.getUpdatedAt())
                .build();
    }

    private Party mapToEntity(PartyDTO dto) {
        return Party.builder()
                .partyType(dto.getPartyType())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .companyName(dto.getCompanyName())
                .registrationNumber(dto.getRegistrationNumber())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .build();
    }

    }
