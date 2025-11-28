package za.co.flash.demo.sanitize.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.dto.SqlReservedWordsResponseDto;
import za.co.flash.demo.sanitize.exception.RecordNotFoundException;
import za.co.flash.demo.sanitize.exception.SanitizationException;
import za.co.flash.demo.sanitize.model.SqlReservedWord;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanitizerServiceImpl implements SanitizerService {

    private final SqlReservedWordRepository repository;

    @Override
    public SqlReservedWordsResponseDto sanitizeWord(final String input) {
        SqlReservedWordsResponseDto sqlReservedWordsResponseDto = new SqlReservedWordsResponseDto();
        List<String> sensitiveWords;
        try {
            sensitiveWords = repository.findAll()
                    .stream()
                    .map(SqlReservedWord::getWord)
                    .toList();
        } catch (Exception e) {
            throw new SanitizationException("Unexpected error while fetching sensitive words", e);
        }

        String sanitized = input;
        for (String word : sensitiveWords) {
            if (!word.isEmpty()) {
                sanitized = sanitized.replaceAll("(?i)" + java.util.regex.Pattern.quote(word),
                        "*".repeat(word.length()));
            }
        }
        sqlReservedWordsResponseDto.setSanitizedList(List.of(sanitized));
        return sqlReservedWordsResponseDto;
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
        SqlReservedWordsResponseDto sanitizedWord = sanitizeWord(newWord);
        SqlReservedWord entity = new SqlReservedWord();
        entity.setWord(sanitizedWord.getSanitizedList().get(0));
        return repository.save(entity);
    }


    @Override
    public boolean deleteWordById(final Long id) {
        Optional<SqlReservedWord> word = repository.findById(id);
        if (word.isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            throw new RecordNotFoundException("The id " + id + " does not exist");
        }
    }

    @Override
    public boolean updateWord(Long id, String newWord) {
        Optional<SqlReservedWord> oldWord = repository.findById(id);
        if (oldWord.isPresent()) {
            SqlReservedWord word = oldWord.get();
            word.setWord(newWord);
            repository.save(word);
            return true;
        } else {
            throw new RecordNotFoundException("The id " + id + " does not exist");
        }

    }


}


