package com.insurance.party_service.service;

import com.insurance.party_service.dto.PartyDTO;
import com.insurance.party_service.entity.PartyType;

import java.util.List;

public interface PartyService {

    PartyDTO createParty(PartyDTO dto);

    PartyDTO getPartyById(Long id);

    PartyDTO getPartyByCode(String partyCode);

    PartyDTO getPartyByEmail(String email);

    List<PartyDTO> getAllParties();

    List<PartyDTO> getPartiesByType(PartyType partyType);

    PartyDTO updateParty(Long id, PartyDTO dto);

    PartyDTO deactivateParty(Long id);

    PartyDTO activateParty(Long id);

    void deleteParty(Long id);
}
