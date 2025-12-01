package za.co.flash.demo.sanitize.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.exception.EntitySaveException;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.exception.SanitizationException;
import za.co.flash.demo.sanitize.exception.DuplicateRecordException;
import za.co.flash.demo.sanitize.entity.SqlReservedWord;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SanitizerServiceImpl implements SanitizerService {

    private final SqlReservedWordRepository repository;

    @Override
    public String sanitizeWord(final String input) {
        log.info("Sanitizing input {} ", input);
        List<String> sensitiveWords;
        try {
            // find all the words in the db table
            sensitiveWords = repository.findAll()
                    .stream()
                    .map(SqlReservedWord::getWord)
                    .map(String::toUpperCase)
                    .toList();
        } catch (Exception e) {
            throw new SanitizationException("Unexpected error while fetching sensitive words", e);
        }

        String sanitized = input.toUpperCase();
        for (String word : sensitiveWords) {
            if (!word.isEmpty()) {
                sanitized = sanitized.replaceAll("(?i)" + java.util.regex.Pattern.quote(word),
                        "*".repeat(word.length()));
            }
        }

        return sanitized;
    }

    @Cacheable("reservedWords")
    @Override
    public List<SqlReservedWordDto> findAllWords() {
        log.info("Finding all words in the table");
        List<SqlReservedWord> words = repository.findAll();
        if (words.isEmpty()) {
            throw new RecordNotFoundException("No reserved words found in the database. Table is empty");
        }
        return words.stream()
                .map(e -> new SqlReservedWordDto(e.getId(), e.getWord()))
                .toList();
    }

    @Override
    @CacheEvict(value = "reservedWordsList", allEntries = true) // clear list cache
    @CachePut(value = "reservedWordByValue", key = "#newWord")  // update single-word cache
    public SqlReservedWordDto addWord(final String newWord) {
        log.info("Attempting to add a new word {}", newWord);
        SqlReservedWord entity = new SqlReservedWord();
        entity.setWord(newWord);

        try {
            SqlReservedWord saved = repository.save(entity);
            return new SqlReservedWordDto(saved.getId(), saved.getWord());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("The input '" + newWord + "' already exists.", e);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save the input '" + newWord + "'.", e);
        }
    }

    @Override
    @CacheEvict("reservedWords")
    public boolean deleteWordById(final Long id) {
        log.info("Deleting word by id {}", id);
        SqlReservedWord entity = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("The id " + id + " does not exist"));
        repository.delete(entity);
        return true;
    }

    @Override
    @CacheEvict(value = "reservedWords", allEntries = true)
    public boolean deleteWordByValue(final String input) {
        log.info("Deleting word by value {}", input);
        SqlReservedWord entity = repository.findByWord(input)
                .orElseThrow(() -> new RecordNotFoundException("The input " + input + " does not exist"));
        repository.delete(entity);
        return true;
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservedWordsList", allEntries = true) // clear list cache
    @CachePut(value = "reservedWordByValue", key = "#newWord")  // update with newword
    public boolean updateWord(final String oldWord, final String newWord) {
        log.info("Update word {} with new word {}", oldWord, newWord);
        SqlReservedWord entity = repository.findByWord(oldWord)
                .orElseThrow(() -> new RecordNotFoundException(
                        "The old input '" + oldWord + "' does not exist"));

        if (repository.findByWord(newWord).isPresent()) {
            throw new DuplicateRecordException("The input to be added '" + newWord + "' already exists.");
        }

        entity.setWord(newWord);
        repository.save(entity);
        return true;
    }
}