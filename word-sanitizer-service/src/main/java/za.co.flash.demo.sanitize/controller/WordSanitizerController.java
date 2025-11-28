package za.co.flash.demo.sanitize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.flash.demo.sanitize.dto.SqlReservedWordsResponseDto;
import za.co.flash.demo.sanitize.service.SanitizerService;

@RestController
@RequestMapping("/sanitize")
@RequiredArgsConstructor
public class WordSanitizerController {

    private final SanitizerService sanitizerService;

    // ---------------------------
    // POST: Sanitize input string
    // ---------------------------
    @Operation(summary = "Sanitize input string")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sanitized string returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<SqlReservedWordsResponseDto> sanitizeString(@Valid @RequestBody final String input) {
        SqlReservedWordsResponseDto responseDto = sanitizerService.sanitizeWord(input);
        return ResponseEntity.ok(responseDto);
    }

    // ---------------------------
    // GET: List all words
    // ---------------------------
    @Operation(summary = "Get the list of SQL reserved words")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of SQL reserved words returned"),
            @ApiResponse(responseCode = "404", description = "No reserved words found")
    })
    @GetMapping
    public ResponseEntity<?> getAllReservedWords() {
        SqlReservedWordsResponseDto responseDto = sanitizerService.findAllWords();
        return ResponseEntity.ok(responseDto);
    }

    // ---------------------------
    // PUT: Update a word by ID
    // ---------------------------
    @Operation(summary = "Update a sensitive word by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Word updated successfully"),
            @ApiResponse(responseCode = "404", description = "Word not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWord(@PathVariable final Long id, @RequestBody final String newWord) {
        sanitizerService.updateWord(id, newWord);
        return ResponseEntity.ok("Word updated successfully.");
    }

    // ---------------------------
    // DELETE: Delete a word by ID
    // ---------------------------
    @Operation(summary = "Delete a sensitive word by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Word deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Word not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable final  Long id) {
        sanitizerService.deleteWordById(id);
        return ResponseEntity.ok("Word deleted successfully.");
    }
}
