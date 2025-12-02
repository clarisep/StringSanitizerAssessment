package za.co.flash.demo.sanitize.service;

import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;

import java.util.List;

public interface SanitizerService {

    String sanitizeWord(String input);

    List<SqlReservedWordDto> findAllWords();

    SqlReservedWordDto addWord(String newWord);

    SqlReservedWordDto findById(Long id);

    SqlReservedWordDto findByWord(String word);

    boolean deleteWordById(Long id);

    boolean deleteWordByValue(String input);

    SqlReservedWordDto updateWord(String oldWord, String newWord);

}
