package com.insurance.party_service.repository;

import com.insurance.party_service.entity.Party;
import com.insurance.party_service.entity.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {

    Optional<Party> findByPartyCode(String partyCode);

    Optional<Party> findByEmail(String email);

    List<Party> findByPartyType(PartyType partyType);

    List<Party> findByActive(Boolean active);

    List<Party> findByCity(String city);

    boolean existsByPartyCode(String partyCode);

    boolean existsByEmail(String email);

}
