package za.co.flash.demo.sanitize.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.dto.SqlReservedWordsResponseDto;
import za.co.flash.demo.sanitize.exception.EntitySaveException;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.exception.SanitizationException;
import za.co.flash.demo.sanitize.exception.DuplicateRecordException;
import za.co.flash.demo.sanitize.model.SqlReservedWord;
import za.co.flash.demo.sanitize.utils.WordValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanitizerServiceImpl implements SanitizerService {

    private final SqlReservedWordRepository repository;

    @Override
    public String sanitizeWord(final String input) {
        WordValidator.validateWord(input);
        List<String> sensitiveWords;
        try {
            //find all the words in the db table
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

    @Override
    public SqlReservedWordsResponseDto findAllWords() {
        List<SqlReservedWord> wordsList = repository.findAll();
        if (wordsList.isEmpty()) {
            throw new RecordNotFoundException("No reserved words found in the database.");
        }
        return new SqlReservedWordsResponseDto(wordsList.stream().map(SqlReservedWord::getWord).toList());
    }

    @Override
    public SqlReservedWord addWord(final String newWord) {
        WordValidator.validateWord(newWord);
        SqlReservedWord entity = new SqlReservedWord();
        entity.setWord(newWord);
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("The word '" + newWord + "' already exists.", e);
        } catch (Exception e) {
            throw new EntitySaveException("Failed to save the word '" + newWord + "'.", e);
        }
    }

    @Override
    public boolean deleteWordById(final Long id) {
        //check if the entity exists
        Optional<SqlReservedWord> word = repository.findById(id);
        if (word.isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            throw new RecordNotFoundException("The id " + id + " does not exist");
        }
    }

    @Override
    public boolean deleteWordByValue(final String input) {
        // check if the entity exists
        Optional<SqlReservedWord> word = repository.findByWord(input);
        if (word.isPresent()) {
            repository.delete(word.get());
            return true;
        } else {
            throw new RecordNotFoundException("The word " + input + " does not exist");
        }
    }

    @Override
    @Transactional
    public boolean updateWord(final String oldWord, final String newWord) {
        WordValidator.validateWord(oldWord);
        WordValidator.validateWord(newWord);
        // Fetch entity by oldWord (source must exist)
        SqlReservedWord entity = repository.findByWord(oldWord)
                .orElseThrow(() -> new RecordNotFoundException(
                        "The word '" + oldWord + "' does not exist"));

        // Check if the new word already exists (duplicate check)
        if (repository.findByWord(newWord).isPresent()) {
            throw new DuplicateRecordException(
                    "The word '" + newWord + "' already exists.");
        }

        //Update and save
        entity.setWord(newWord);
        repository.save(entity);
        return true;
    }

}


