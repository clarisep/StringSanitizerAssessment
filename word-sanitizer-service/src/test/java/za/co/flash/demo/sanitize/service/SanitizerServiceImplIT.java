package za.co.flash.demo.sanitize.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import za.co.flash.demo.sanitize.model.SqlReservedWord;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class SanitizerServiceImplIT {

    @Autowired
    private SanitizerService sanitizerService;

    @Test
    @Order(1)
    void testAddWord() {
        // Add a new word
        SqlReservedWord word = sanitizerService.addWord("SELECT");
        assertNotNull(word.getId());
        assertEquals("SELECT", word.getWord());
    }

    @Test
    @Order(2)
    void testAddAnotherWord() {
        // Add another word
        SqlReservedWord word = sanitizerService.addWord("UPDATE");
        assertNotNull(word.getId());
        assertEquals("UPDATE", word.getWord());
    }

    @Test
    @Order(3)
    void testSanitizeWord() {
        // Programmatically add the word to ensure it exists
        sanitizerService.addWord("DELETE");

        String input = "SELECT DELETE UPDATE";
        String sanitized = sanitizerService.sanitizeWord(input);

        assertEquals("****** ****** ******", sanitized);
    }

    @Test
    @Order(4)
    void testFindAllWords() {
        // Insert words to query
        sanitizerService.addWord("INSERT");
        sanitizerService.addWord("DROP");

        var response = sanitizerService.findAllWords();
        assertTrue(response.getWords().contains("INSERT"));
        assertTrue(response.getWords().contains("DROP"));
    }

    @Test
    @Order(5)
    @Transactional
    void testUpdateWord() {
        sanitizerService.addWord("ALTER");
        boolean updated = sanitizerService.updateWord("ALTER", "ALTERED");
        assertTrue(updated);
    }

    @Test
    @Order(6)
    void testDeleteWordByValue() {
        sanitizerService.addWord("TRUNCATE");
        boolean deleted = sanitizerService.deleteWordByValue("TRUNCATE");
        assertTrue(deleted);
    }

    @Test
    @Order(7)
    void testDeleteWordById() {
        SqlReservedWord word = sanitizerService.addWord("CREATE");
        boolean deleted = sanitizerService.deleteWordById(word.getId());
        assertTrue(deleted);
    }
}
