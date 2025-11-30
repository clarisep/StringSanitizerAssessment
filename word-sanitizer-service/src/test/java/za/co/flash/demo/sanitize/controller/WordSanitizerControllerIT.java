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
import za.co.flash.demo.sanitize.dto.SqlReservedWordsResponseDto;
import za.co.flash.demo.sanitize.model.UpdateWordRequest;
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
    private SanitizerService sanitizerService; // injected from test config

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SanitizerService sanitizerService() {
            return Mockito.mock(SanitizerService.class);
        }
    }



    @Test
    void testSanitizeString() throws Exception {
        when(sanitizerService.sanitizeWord("SELECT")).thenReturn("SAFE");

        mockMvc.perform(post("/sanitize")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("SELECT"))
                .andExpect(status().isOk())
                .andExpect(content().string("SAFE"));
    }

    @Test
    void testGetAllReservedWords() throws Exception {
       String word = "SELECT";

        SqlReservedWordsResponseDto dto = new SqlReservedWordsResponseDto(List.of(word));
        when(sanitizerService.findAllWords()).thenReturn(dto);

        mockMvc.perform(get("/sanitize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.words[0].word").value("SELECT"));
    }

    @Test
    void testUpdateWordByWord() throws Exception {
        UpdateWordRequest request = new UpdateWordRequest("SELECT", "UPDATE");
        when(sanitizerService.updateWord(eq("SELECT"), eq("UPDATE"))).thenReturn(true);

        mockMvc.perform(put("/sanitize/update/by-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldWord\":\"SELECT\",\"newWord\":\"UPDATE\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word updated successfully."));
    }

    @Test
    void testDeleteWordById() throws Exception {
        when(sanitizerService.deleteWordById(1L)).thenReturn(true);

        mockMvc.perform(delete("/sanitize/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word deleted successfully."));
    }

    @Test
    void testDeleteWordByValue() throws Exception {
        when(sanitizerService.deleteWordByValue("SELECT")).thenReturn(true);

        mockMvc.perform(delete("/sanitize/value/SELECT"))
                .andExpect(status().isOk())
                .andExpect(content().string("Word deleted successfully."));
    }
}