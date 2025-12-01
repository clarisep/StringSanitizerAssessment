package za.co.flash.demo.sanitize.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.service.SanitizerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WordSanitizerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SanitizerService sanitizerService;

    /**
     * Test configuration that provides a mocked SanitizerService bean.
     * This ensures the controller can be tested independently of the actual service implementation.
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public SanitizerService sanitizerService() {
            return Mockito.mock(SanitizerService.class);
        }
    }


    @Test
    void testSanitizeString() throws Exception {
        // Arrange: mock service to return "SAFE" when sanitizing "SELECT"
        when(sanitizerService.sanitizeWord("SELECT")).thenReturn("SAFE");

        // Act & Assert: perform POST request and expect sanitized output
        mockMvc.perform(post("/sanitize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"input\":\"SELECT\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("SAFE")); // Jackson wraps plain string in quotes
    }

    @Test
    void testGetAllReservedWords() throws Exception {
        String word = "SELECT";
        SqlReservedWordDto dto = new SqlReservedWordDto(word);

        // Arrange: mock service to return a list containing one DTO
        when(sanitizerService.findAllWords()).thenReturn(List.of(dto));

        // Act & Assert: perform GET request and expect JSON response with the word
        mockMvc.perform(get("/sanitize/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("SELECT"));
    }

    @Test
    void testFindWord_WordExists() throws Exception {
        // Arrange
        // Arrange: mock service response
        SqlReservedWordDto dto = new SqlReservedWordDto(1L, "SELECT");
        when(sanitizerService.findByWord("SELECT")).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/sanitize/SELECT") // endpoint path
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.word").value("SELECT"));
    }

    @Test
    void testFindWord_WordDoesNotExist() throws Exception {
        // Arrange
        when(sanitizerService.findByWord("DROP"))
                .thenThrow(new RecordNotFoundException("The input 'DROP' does not exist"));

        // Act & Assert
        mockMvc.perform(get("/sanitize/DROP")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWordByWord() throws Exception {
        // Arrange: mock service to return an entity when updating
        SqlReservedWordDto dto = new SqlReservedWordDto(1L, "UPDATE");
        when(sanitizerService.updateWord(eq("SELECT"), eq("UPDATE"))).thenReturn(dto);

        // Act & Assert: perform PUT request with JSON body and expect success message
        mockMvc.perform(put("/sanitize/update-by-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldWord\":\"SELECT\",\"newWord\":\"UPDATE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.word").value("UPDATE"));
    }

    @Test
    void testDeleteWordById() throws Exception {
        // Arrange: mock service to return true when deleting by ID
        when(sanitizerService.deleteWordById(1L)).thenReturn(true);

        // Act & Assert: perform DELETE request and expect success message
        mockMvc.perform(delete("/sanitize/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Record deleted successfully."));
    }

    @Test
    void testDeleteWordByValue() throws Exception {
        // Arrange: mock service to return true when deleting by value
        when(sanitizerService.deleteWordByValue("SELECT")).thenReturn(true);

        // Act & Assert: perform DELETE request and expect success message
        mockMvc.perform(delete("/sanitize/delete-by-word/SELECT"))
                .andExpect(status().isOk())
                .andExpect(content().string("Record deleted successfully."));
    }
}