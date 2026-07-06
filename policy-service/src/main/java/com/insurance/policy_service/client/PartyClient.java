package com.insurance.policy_service.client;

import com.insurance.policy_service.dto.PartyResponse;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PathVariable;


public interface PartyClient {

    // Check if party exists by partyCode
    @RequestLine("GET /api/parties/code/{partyCode}")
    PartyResponse getPartyByCode(@Param String partyCode);

    // Get party by email
    @RequestLine("GET /api/parties/email/{email}")
    PartyResponse getPartyByEmail(@Param String email);

}
