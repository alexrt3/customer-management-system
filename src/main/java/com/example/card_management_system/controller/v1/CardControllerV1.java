package com.example.card_management_system.controller.v1;

import com.example.card_management_system.dto.CardResponseDTO;
import com.example.card_management_system.dto.CardUpdateDTO;
import com.example.card_management_system.dto.CreateCardRequestDTO;
import com.example.card_management_system.dto.v1.CardResponseV1DTO;
import com.example.card_management_system.dto.v1.CreateCardRequestV1DTO;
import com.example.card_management_system.entity.Card;
import com.example.card_management_system.mapper.CardMapper;
import com.example.card_management_system.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//TODO: MOVE TO CARD CONTROLLER AND MOVE V1 TO METHOD LEVEL MAPPING
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/cards")
public class CardControllerV1 {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping("/create")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardResponseV1DTO> createCard(@Valid @RequestBody CreateCardRequestV1DTO request) {
        log.info("POST /cards - creating card for customer with customerId={}", request.getCustomerId());

        Card newCard = cardMapper.requestV1DtoToCard(request);

        Card created = cardService.createNewCard(newCard);

        CardResponseV1DTO cardResponseV1DTO = cardMapper.cardToResponseV1Dto(created);

        log.info("POST /cards - successfully created card for customer={}", created.getCardHolderName());
        return ResponseEntity.status(201).body(cardResponseV1DTO);
    }

    @GetMapping("/find/{accountId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardResponseV1DTO> getCardById(@PathVariable String accountId) {
        log.info("GET /cards/{}- retrieving card", accountId);

        Card card = cardService.getCardById(accountId);

        CardResponseV1DTO retrieved = cardMapper.cardToResponseV1Dto(card);

        log.info("GET /cards/{}- successfully retrieved card", accountId);
        return ResponseEntity.ok(retrieved);
    }

    @PutMapping("/update/{accountId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardResponseDTO> updateCardById(@PathVariable String accountId, @Valid @RequestBody CardUpdateDTO update) {
        log.info("PUT /cards/{}- updating card", accountId);

        CardResponseDTO updated = cardService.updateCard(accountId, update);

        log.info("PUT /cards/{}- successfully updated card", accountId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/remove/{accountId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCardById(@PathVariable String accountId) {
        log.info("DELETE /cards/{}- deleting card", accountId);

        cardService.deleteCard(accountId); //404

        log.info("DELETE /cards/{}- successfully deleted card   ", accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/all")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CardResponseDTO>> getAllCardsByCustomerId(@PathVariable String customerId) {
        log.info("GET /cards/{}/cards- retrieving all cards", customerId);

        List<CardResponseDTO> retrievedCards = cardService.getAllCardsByCustomerId(customerId);

        log.info("GET /cards/{}/cards- successfully retrieved all cards", customerId);
        return ResponseEntity.ok(retrievedCards);
    }

}
