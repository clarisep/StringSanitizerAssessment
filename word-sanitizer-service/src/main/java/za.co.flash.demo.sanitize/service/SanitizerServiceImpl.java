package za.co.flash.demo.sanitize.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import za.co.flash.demo.sanitize.db.SensitiveWordRepository;
import za.co.flash.demo.sanitize.dto.SensitiveWordResponseDTO;
import za.co.flash.demo.sanitize.exception.CustomDataAccessException;
import za.co.flash.demo.sanitize.exception.SanitizationException;
import za.co.flash.demo.sanitize.model.SensitiveWord;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SanitizerServiceImpl implements  SanitizerService{

    private final SensitiveWordRepository repository;

    @Override
    public SensitiveWordResponseDTO sanitize(final String input) {
        SensitiveWordResponseDTO sensitiveWordResponseDTO = new SensitiveWordResponseDTO();
        List<String> sensitiveWords;
        try {
            sensitiveWords = repository.findAll()
                    .stream()
                    .map(SensitiveWord::getWord)
                    .toList();
        }  catch (DataAccessException dae) {
            throw new CustomDataAccessException("Database access error while fetching sensitive words", dae);
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
        sensitiveWordResponseDTO.setSanitizedString(sanitized);
        return sensitiveWordResponseDTO;
    }

    @Override
    public SensitiveWordResponseDTO findAllWords() {
        List<SensitiveWord> dbList = repository.findAll();
        return new SensitiveWordResponseDTO(dbList);
    }


}
