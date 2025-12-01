package za.co.flash.demo.sanitize.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import java.util.regex.Pattern;

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
            sensitiveWords = repository.findAll()
                    .stream()
                    .map(SqlReservedWord::getWord)
                    .toList(); // keep original case
        } catch (Exception e) {
            throw new SanitizationException("Unexpected error while fetching sensitive words", e);
        }

        String sanitized = input;
        for (String word : sensitiveWords) {
            if (!word.isEmpty()) {
                String regex = "(?i)" + Pattern.quote(word);
                sanitized = sanitized.replaceAll(regex, "*".repeat(word.length()));
            }
        }

        return sanitized.toUpperCase();

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
    @Transactional
    @CacheEvict(value = "reservedWords", allEntries = true) // clear list cache
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
    @Cacheable(value = "reservedWordByValue", key = "#word")
    public SqlReservedWordDto findByWord(final String word) {
        log.info("Finding reserved word by value {}", word);

        SqlReservedWord entity = repository.findByWord(word)
                .orElseThrow(() -> new RecordNotFoundException("The input '" + word + "' does not exist"));

        return new SqlReservedWordDto(entity.getId(), entity.getWord());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reservedWords", allEntries = true),
            @CacheEvict(value = "reservedWordByValue", allEntries = true) // clear all single-word cache entries
    })

    public boolean deleteWordById(final Long id) {
        log.info("Deleting word by id {}", id);
        SqlReservedWord entity = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("The id " + id + " does not exist"));
        repository.delete(entity);
        return true;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reservedWords", allEntries = true),
            @CacheEvict(value = "reservedWordByValue", key = "#word")
    })
    public boolean deleteWordByValue(final String word) {
        log.info("Deleting word by value {}", word);
        SqlReservedWord entity = repository.findByWord(word)
                .orElseThrow(() -> new RecordNotFoundException("The word " + word + " does not exist"));
        repository.delete(entity);
        return true;
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "reservedWords", allEntries = true), // clear list cache
            @CacheEvict(value = "reservedWordByValue", key = "#oldWord") // clear old word cache
    }, put = {
            @CachePut(value = "reservedWordByValue", key = "#newWord") // update with new word
    })
    public SqlReservedWordDto updateWord(final String oldWord, final String newWord) {
        log.info("Update word {} with new word {}", oldWord, newWord);
        SqlReservedWord entity = repository.findByWord(oldWord)
                .orElseThrow(() -> new RecordNotFoundException(
                        "The old input '" + oldWord + "' does not exist"));

        if (repository.findByWord(newWord).isPresent()) {
            throw new DuplicateRecordException("The input to be added '" + newWord + "' already exists.");
        }

        entity.setWord(newWord);
        repository.save(entity);
        return new SqlReservedWordDto(entity.getId(), entity.getWord());
    }
}