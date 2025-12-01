package za.co.flash.demo.sanitize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.model.SanitizeRequest;
import za.co.flash.demo.sanitize.model.UpdateWordRequest;
import za.co.flash.demo.sanitize.service.SanitizerService;

import java.util.List;

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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sanitizeString(@Valid @RequestBody final SanitizeRequest request) {
        String responseDto = sanitizerService.sanitizeWord(request.getInput());
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SqlReservedWordDto>> getAllReservedWords() {
        List<SqlReservedWordDto> responseDto = sanitizerService.findAllWords();
        return ResponseEntity.ok(responseDto);
    }

    // ---------------------------
    // PUT: Update an old word
    // ---------------------------
    @Operation(summary = "Update existing record with a new value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWord(@RequestBody final UpdateWordRequest request) {
        sanitizerService.updateWord(request.getOldWord(), request.getNewWord());
        return ResponseEntity.ok("Record updated successfully.");
    }

    // ---------------------------
    // DELETE: Delete a input by ID
    // ---------------------------
    @Operation(summary = "Delete record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWordById( @PathVariable @Min(value = 1, message = "ID must be greater than 0") final Long id) {
        sanitizerService.deleteWordById(id);
        return ResponseEntity.ok("Record deleted successfully.");
    }

    // ---------------------------
    // DELETE: Delete a input by value
    // ---------------------------
    @Operation(summary = "Delete a record by value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found")
    })
    @DeleteMapping("/value/{word}")
    public ResponseEntity<?> deleteWordByValue(@PathVariable @NotBlank(message = "Input must not be blank")
                                                   @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
                                                           message = "Input must start with a letter or underscore, and may contain letters, digits, underscores, or spaces")
                                                   final String word) {
        sanitizerService.deleteWordByValue(word);
        return ResponseEntity.ok("Record deleted successfully.");
    }
}
