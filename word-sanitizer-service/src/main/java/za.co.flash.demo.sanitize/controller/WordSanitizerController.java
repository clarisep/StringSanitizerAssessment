package za.co.flash.demo.sanitize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import za.co.flash.demo.sanitize.exception.ErrorResponse;
import za.co.flash.demo.sanitize.model.SanitizeRequest;
import za.co.flash.demo.sanitize.model.UpdateWordRequest;
import za.co.flash.demo.sanitize.service.SanitizerService;

import java.util.List;
import java.util.Map;

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
            @ApiResponse(responseCode = "200", description = "Sanitized string returned",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sanitizeString(@Valid @RequestBody final SanitizeRequest request) {
        return ResponseEntity.ok(sanitizerService.sanitizeWord(request.getInput()));
    }


    // ---------------------------
    // GET: List all words
    // ---------------------------
    @Operation(summary = "Get the list of SQL reserved words")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of SQL reserved words returned"),
            @ApiResponse(responseCode = "404", description = "No reserved words found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SqlReservedWordDto>> getAllReservedWords() {
        List<SqlReservedWordDto> responseDto = sanitizerService.findAllWords();
        return ResponseEntity.ok(responseDto);
    }

    // ----------------------------
    // GET : Find word by value
    //-----------------------------
    @Operation(summary = "Find reserved word by value",
            description = "Fetches a reserved SQL word from the database by its value. Returns 404 if the word does not exist.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserved word found"),
            @ApiResponse(responseCode = "404", description = "Reserved word not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(path = "/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SqlReservedWordDto> findByWord(
            @NotBlank(message = "Input must not be blank")
            @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
                    message = "Input must start with a letter or underscore, and may only contain letters, digits, underscores, spaces, or asterisks")
            @PathVariable("word") final String word) {
        SqlReservedWordDto dto = sanitizerService.findByWord(word);
        return ResponseEntity.ok(dto);
    }

    // ---------------------------
    // PUT: Update an old word
    // ---------------------------
    @Operation(summary = "Update existing record with a new value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/update-by-word", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SqlReservedWordDto> updateWord(@Valid @RequestBody final UpdateWordRequest request) {
        SqlReservedWordDto dto = sanitizerService.updateWord(request.getOldWord(), request.getNewWord());
        return ResponseEntity.ok(dto);
    }

    // ---------------------------
    // DELETE: Delete an input by ID
    // ---------------------------
    @Operation(summary = "Delete record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/delete-by-id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteWordById(
            @PathVariable("id") @Min(value = 1, message = "ID must be greater than 0") final Long id) {
        sanitizerService.deleteWordById(id);
        return ResponseEntity.ok("Record deleted successfully.");
    }

    // ---------------------------
    // DELETE: Delete a input by value
    // ---------------------------
    @Operation(summary = "Delete a record by value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/delete-by-word/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteWordByValue(
            @NotBlank(message = "Input must not be blank")
            @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
                    message = "Input must start with a letter or underscore, and may only contain letters, digits, underscores, spaces, or asterisks")
            @PathVariable("word") final String word) {
        sanitizerService.deleteWordByValue(word);
        return ResponseEntity.ok("Record deleted successfully.");
    }
}