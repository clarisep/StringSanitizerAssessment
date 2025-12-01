package za.co.flash.demo.sanitize.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.service.SanitizerService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
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
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("SELECT"))
                .andExpect(status().isOk())
                .andExpect(content().string("SAFE"));
    }

    @Test
    void testGetAllReservedWords() throws Exception {
        String word = "SELECT";
        SqlReservedWordDto dto = new SqlReservedWordDto(word);

        // Arrange: mock service to return a list containing one DTO
        when(sanitizerService.findAllWords()).thenReturn(List.of(dto));

        // Act & Assert: perform GET request and expect JSON response with the word
        mockMvc.perform(get("/sanitize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words[0].word").value("SELECT"));
    }

    @Test
    void testUpdateWordByWord() throws Exception {
        // Arrange: mock service to return true when updating "SELECT" to "UPDATE"
        when(sanitizerService.updateWord(eq("SELECT"), eq("UPDATE"))).thenReturn(true);

        // Act & Assert: perform PUT request with JSON body and expect success message
        mockMvc.perform(put("/sanitize/update/by-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldWord\":\"SELECT\",\"newWord\":\"UPDATE\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word updated successfully."));
    }

    @Test
    void testDeleteWordById() throws Exception {
        // Arrange: mock service to return true when deleting word with ID 1
        when(sanitizerService.deleteWordById(1L)).thenReturn(true);

        // Act & Assert: perform DELETE request and expect success message
        mockMvc.perform(delete("/sanitize/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word deleted successfully."));
    }

    @Test
    void testDeleteWordByValue() throws Exception {
        // Arrange: mock service to return true when deleting word "SELECT"
        when(sanitizerService.deleteWordByValue("SELECT")).thenReturn(true);

        // Act & Assert: perform DELETE request and expect success message
        mockMvc.perform(delete("/sanitize/value/SELECT"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word deleted successfully."));
    }
}