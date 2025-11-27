package za.co.flash.demo.sanitize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.flash.demo.sanitize.dto.SensitiveWordResponseDTO;
import za.co.flash.demo.sanitize.service.SanitizerService;

@RestController
@RequestMapping("/sanitize")
@RequiredArgsConstructor
public class WordSanitizerController {
    private final SanitizerService sanitizerService;

    @Operation(summary = "Sanitize input string")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sanitized string returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(consumes = "text/plain")
    public ResponseEntity<SensitiveWordResponseDTO> sanitizeString(@Valid @RequestBody final String input) {
        SensitiveWordResponseDTO sensitiveWordResponseDTO = sanitizerService.sanitize(input);
        return ResponseEntity.ok(sensitiveWordResponseDTO);
    }


    @Operation(summary = "Get list of sensitive strings string")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sensitive words  returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping
    public ResponseEntity<SensitiveWordResponseDTO> getAllSensitveWords() {
        SensitiveWordResponseDTO sensitiveWordResponseDTO = sanitizerService.findAllWords();
        return ResponseEntity.ok(sensitiveWordResponseDTO);
    }

}






