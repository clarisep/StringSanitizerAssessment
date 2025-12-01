package za.co.flash.demo.sanitize.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.exception.DuplicateRecordException;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.entity.SqlReservedWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ActiveProfiles("test")
class SanitizerServiceImplTest {

    private SanitizerService sanitizerService;

    @Mock
    private SqlReservedWordRepository repository;

    @BeforeEach
    public void setup() {
        // Create a mock repository and inject into the service before each test
        repository = Mockito.mock(SqlReservedWordRepository.class);
        sanitizerService = new SanitizerServiceImpl(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USING", "ALLSCHEMAS", "DROP"})
    void testSanitize_OuputForDifferentReservedWords(String word) {
        // Arrange: mock repository to return a single reserved word
        SqlReservedWord reserved = new SqlReservedWord();
        reserved.setWord(word);
        when(repository.findAll()).thenReturn(List.of(reserved));

        // Act: sanitize the word
        String sanitized = sanitizerService.sanitizeWord(word);

        // Assert: result should be masked with asterisks
        assertNotNull(sanitized);
        assertEquals("*".repeat(word.length()), sanitized);
    }

    @Test
    void testSanitize_OutputWhenListOfReservedWordsAreGivenInAString() {
        // Arrange: mock repository to return multiple reserved phrases
        List<SqlReservedWord> listOfReservedWords = new ArrayList<>();

        SqlReservedWord reserved1 = new SqlReservedWord();
        String phrase1 = "SELECT * FROM";
        reserved1.setWord(phrase1);
        listOfReservedWords.add(reserved1);

        SqlReservedWord reserved2 = new SqlReservedWord();
        String phrase2 = "CURRENT_PATH";
        reserved2.setWord(phrase2);
        listOfReservedWords.add(reserved2);

        when(repository.findAll()).thenReturn(listOfReservedWords);

        String input = "I want to select * from this place and check the CURRENT_PATH";

        // Act: sanitize the input string
        String sanitized = sanitizerService.sanitizeWord(input);

        // Assert: both reserved phrases should be masked
        assertNotNull(sanitized);
        String output = "I want to " + "*".repeat(phrase1.length()) +
                " this place and check the " + "*".repeat(phrase2.length());
        log.info(output);
        assertEquals(output.toUpperCase(), sanitized);
    }

    @Test
    void testFindAllWords_ReturnsList() {
        // Arrange: mock repository to return a list with one word
        SqlReservedWord word = new SqlReservedWord();
        word.setId(1L);
        word.setWord("SELECT");
        when(repository.findAll()).thenReturn(List.of(word));

        // Act: call the service
        List<SqlReservedWordDto> result = sanitizerService.findAllWords();

        // Assert: result should contain the mapped DTO
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SELECT", result.get(0).getWord());
        assertEquals(1L, result.get(0).getId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindAllWords_ThrowsExceptionWhenEmpty() {
        // Arrange: mock repository to return empty list
        when(repository.findAll()).thenReturn(List.of());

        // Act & Assert: should throw RecordNotFoundException
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.findAllWords()
        );

        assertTrue(ex.getMessage().contains("No reserved words"));
        verify(repository, times(1)).findAll();
    }

    @Test
    void testAddWord_NewRecordAddedSuccessfully() {
        // Arrange: mock repository to return a saved entity
        SqlReservedWord entity = new SqlReservedWord();
        when(repository.save(any(SqlReservedWord.class))).thenReturn(entity);

        // Act: add a new word
        sanitizerService.addWord("ABC");

        // Assert: repository.save should be called once
        verify(repository, times(1)).save(any(SqlReservedWord.class));
    }

    @Test
    void testAddWord_DuplicateRecord_ThrowsException() {
        // Arrange: mock repository to throw DataIntegrityViolationException
        when(repository.save(any(SqlReservedWord.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        // Act & Assert: adding a duplicate should throw DuplicateRecordException
        DuplicateRecordException ex = assertThrows(
                DuplicateRecordException.class,
                () -> sanitizerService.addWord("SELECT"));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(repository, times(1)).save(any(SqlReservedWord.class));
    }

    @Test
    void testFindByWord_WordExists() {
        // Arrange: mock repository to find an entity
        SqlReservedWord entity = new SqlReservedWord();
        when(repository.findByWord(any())).thenReturn(Optional.of(entity));

        // Act: find a word
        SqlReservedWordDto dto = sanitizerService.findByWord("ABC");
        assertNotNull(dto);

        // Assert: repository.findByWord should be called once
        verify(repository, times(1)).findByWord(anyString());
    }

    @Test
    void testFindByWord_WordDoesNotExist() {
        // Arrange: mock repository to find an entity
        when(repository.findByWord(any())).thenReturn(Optional.empty());

        // Act & Assert: adding a duplicate should throw DuplicateRecordException
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.findByWord("SELECT"));

        assertTrue(ex.getMessage().contains("does not exist"));
        // Assert: repository.findByWord should be called once
        verify(repository, times(1)).findByWord(anyString());
    }

    @Test
    void testDeleteWordById_RecordExists() {
        // Arrange: mock repository to return a word by ID
        Long id = 1L;
        SqlReservedWord word = new SqlReservedWord();
        word.setId(id);
        word.setWord("SELECT");
        when(repository.findById(id)).thenReturn(Optional.of(word));

        // Act: delete the word
        boolean result = sanitizerService.deleteWordById(id);

        // Assert: deletion should succeed and repository.delete should be called
        assertTrue(result);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).delete(word);
    }

    @Test
    void testDeleteWordById_RecordDoesNotExist() {
        // Arrange: mock repository to return empty when searching by ID
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert: deleting a non-existent word should throw RecordNotFoundException
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.deleteWordById(id));

        assertTrue(ex.getMessage().contains("does not exist"));
        verify(repository, times(1)).findById(id);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateWord_Success() {
        // Arrange: old word exists, new word does not exist
        Long id = 1L;
        String newWord = "UPDATE";
        SqlReservedWord oldWord = new SqlReservedWord();
        oldWord.setId(id);
        oldWord.setWord("SELECT");

        when(repository.findByWord(oldWord.getWord())).thenReturn(Optional.of(oldWord));
        when(repository.findByWord(newWord)).thenReturn(Optional.empty());
        when(repository.save(any(SqlReservedWord.class))).thenReturn(oldWord);

        // Act: update the word
        SqlReservedWordDto result = sanitizerService.updateWord(oldWord.getWord(), newWord);

        // Assert: update should succeed and word should be changed
        assertNotNull(result);
        assertEquals(newWord, result.getWord());
        verify(repository, times(1)).findByWord(newWord);
        verify(repository, times(1)).save(oldWord);
    }

    @Test
    void testUpdateWord_OldWordDoesNotExist_ThrowsException() {
        // Arrange: old word does not exist
        String newWord = "UPDATE";
        String oldWord = "SELECT";
        when(repository.findByWord(oldWord)).thenReturn(Optional.empty());

        // Act & Assert: updating a non-existent word should throw RecordNotFoundException
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.updateWord(oldWord, newWord));

        assertTrue(ex.getMessage().contains("does not exist"));
        verify(repository, times(1)).findByWord(oldWord);
    }

    @Test
    void testUpdateWord_NewWordAlreadyExists_ThrowsException() {
        // Arrange: old word exists, new word already exists
        SqlReservedWord oldWord = new SqlReservedWord();
        oldWord.setWord("SELECT");

        SqlReservedWord newWord = new SqlReservedWord();
        newWord.setWord("UPDATE");

        when(repository.findByWord(oldWord.getWord())).thenReturn(Optional.of(oldWord));
        when(repository.findByWord(newWord.getWord())).thenReturn(Optional.of(newWord));

        // Act & Assert: updating to a duplicate word should throw DuplicateRecordException
        DuplicateRecordException ex = assertThrows(
                DuplicateRecordException.class,
                () -> sanitizerService.updateWord(oldWord.getWord(), newWord.getWord()));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(repository, times(2)).findByWord(any());
    }
}