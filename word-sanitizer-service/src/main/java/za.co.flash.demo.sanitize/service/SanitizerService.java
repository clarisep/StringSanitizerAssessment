package za.co.flash.demo.sanitize.service;

import za.co.flash.demo.sanitize.dto.SqlReservedWordsResponseDto;
import za.co.flash.demo.sanitize.model.SqlReservedWord;

public interface SanitizerService {

    SqlReservedWordsResponseDto sanitizeWord(String input);

    SqlReservedWordsResponseDto findAllWords();

    SqlReservedWord addWord(String newWord);

    boolean deleteWordById(Long id);

    boolean updateWord(Long id, String newWord);
}
