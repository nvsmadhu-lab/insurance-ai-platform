package com.insurance.party_service.contoller;

import com.insurance.party_service.Dto.PartyDTO;
import com.insurance.party_service.entity.Party;
import com.insurance.party_service.entity.PartyType;
import com.insurance.party_service.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<PartyDTO> createParty(@Valid @RequestBody PartyDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partyService.createParty(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(partyService.getPartyById(id));
    }

        @GetMapping("/code/{partyCode}")
    public ResponseEntity<PartyDTO> getByCode(@PathVariable String partyCode){
        return ResponseEntity.ok(partyService.getPartyByCode(partyCode));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PartyDTO> getByEmail(@PathVariable String email){
        return ResponseEntity.ok(partyService.getPartyByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<PartyDTO>> getAll(){
        return ResponseEntity.ok(partyService.getAllParties());
    }

    @GetMapping("/type/{partyType}")
    public ResponseEntity<List<PartyDTO>> getByType(@PathVariable PartyType partyType){
        return ResponseEntity.ok(partyService.getPartiesByType(partyType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartyDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PartyDTO partyDTO){
        return ResponseEntity.ok(partyService.updateParty(id, partyDTO));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PartyDTO> deactivateParty(@PathVariable Long id){
        return ResponseEntity.ok(partyService.deactivateParty(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<PartyDTO> activateParty(@PathVariable Long id){
        return ResponseEntity.ok(partyService.activateParty(id));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteParty(@PathVariable Long id){
        partyService.deleteParty(id);
        return ResponseEntity.noContent().build();

    }
}
