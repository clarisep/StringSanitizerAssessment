package za.co.flash.demo.sanitize.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.exception.DuplicateRecordException;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.model.SqlReservedWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class SanitizerServiceImplTest {

    private SanitizerService sanitizerService;
    @Mock
    private SqlReservedWordRepository repository;

    @BeforeEach
    public void setup() {
        repository = Mockito.mock(SqlReservedWordRepository.class);
        sanitizerService = new SanitizerServiceImpl(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USING", "ALLSCHEMAS", "DROP"})
    void testSanitize_OuputForDifferentReservedWords(String word) {
        SqlReservedWord reserved = new SqlReservedWord();
        reserved.setWord(word);
        when(repository.findAll()).thenReturn(List.of(reserved));
        String sanitized = sanitizerService.sanitizeWord(word);
        assertNotNull(sanitized);
        assertEquals(sanitized, "*".repeat(word.length()));

    }

    @Test
    void testSanitize_OutputWhenListOfReservedWordsAreGivenInAString() {
        // Arrange
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

        String input = "I want to select * from this table and check the CURRENT_PATH";
        // Act
        String sanitized = sanitizerService.sanitizeWord(input);
        // Assert
        assertNotNull(sanitized);
        String output = "I want to " + "*".repeat(phrase1.length()) + " this table and check the " + "*".repeat(phrase2.length());
        log.info(output);
        assertEquals(output.toUpperCase(), sanitized);
    }


    @Test
    void findAllWords() {
    }

    @Test
    void testAddWord_NewRecordAddedSuccessfully() {
        SqlReservedWord entity = new SqlReservedWord();
        when(repository.save(any(SqlReservedWord.class))).thenReturn(entity);
        sanitizerService.addWord("ABC");
        verify(repository, times(1)).save(any(SqlReservedWord.class));
    }

    @Test
    void testAddWord_DuplicateRecord_ThrowsException() {
        when(repository.save(any(SqlReservedWord.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        DuplicateRecordException ex = assertThrows(
                DuplicateRecordException.class,
                () -> sanitizerService.addWord("SELECT"));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(repository, times(1)).save(any(SqlReservedWord.class));
    }

    @Test
    void testDeleteWordById_RecordExists() {
        Long id = 1L;
        SqlReservedWord word = new SqlReservedWord();
        word.setId(id);
        word.setWord("SELECT");

        when(repository.findById(id)).thenReturn(Optional.of(word));
        boolean result = sanitizerService.deleteWordById(id);
        assertTrue(result);
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
    }


    @Test
    void testDeleteWordById_RecordDoesNotExist() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.deleteWordById(id));

        assertTrue(ex.getMessage().contains("does not exist"));
        verify(repository, times(1)).findById(id);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateWord_Success() {
        Long id = 1L;
        String newWord = "UPDATE";
        SqlReservedWord oldWord = new SqlReservedWord();
        oldWord.setId(id);
        oldWord.setWord("SELECT");

        // Arrange: record exists, new word does not exist
        when(repository.findByWord(oldWord.getWord())).thenReturn(Optional.of(oldWord));
        when(repository.save(any(SqlReservedWord.class))).thenReturn(oldWord);
        when(repository.findByWord(newWord)).thenReturn(Optional.empty());
        when(repository.save(any(SqlReservedWord.class))).thenReturn(oldWord);

        // Act
        boolean result = sanitizerService.updateWord(oldWord.getWord(), newWord);

        // Assert
        assertTrue(result, "Expected updateWord to return true");
        assertEquals(newWord, oldWord.getWord(), "Word should be updated");

        verify(repository, times(1)).findByWord(newWord);
        verify(repository, times(1)).save(oldWord);
    }

    @Test
    void testUpdateWord_OldWordDoesNotExist_ThrowsException() {
        String newWord = "UPDATE";
        String oldWord = "SELECT";

        // Arrange: old word does not exist
        when(repository.findByWord(oldWord)).thenReturn(Optional.empty());
        RecordNotFoundException ex = assertThrows(
                RecordNotFoundException.class,
                () -> sanitizerService.updateWord(oldWord, newWord));
        assertTrue(ex.getMessage().contains("does not exist"));

        verify(repository, times(1)).findByWord(oldWord);

    }

    @Test
    void testUpdateWord_NewWordAlreadyExists_ThrowsException() {
        SqlReservedWord oldWord = new SqlReservedWord();
        oldWord.setWord("SELECT");

        SqlReservedWord newWord = new SqlReservedWord();
        newWord.setWord("UPDATE");
        // Arrange: old word does not exist
        when(repository.findByWord(oldWord.getWord())).thenReturn(Optional.of(oldWord));
        when(repository.findByWord(newWord.getWord())).thenReturn(Optional.of(newWord));
        DuplicateRecordException ex = assertThrows(
                DuplicateRecordException.class,
                () -> sanitizerService.updateWord(oldWord.getWord(), newWord.getWord()));
        assertTrue(ex.getMessage().contains("already exists"));

        verify(repository, times(2)).findByWord(any());

    }
}