package za.co.flash.demo.sanitize.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import za.co.flash.demo.sanitize.db.SqlReservedWordRepository;
import za.co.flash.demo.sanitize.dto.SqlReservedWordDto;
import za.co.flash.demo.sanitize.entity.SqlReservedWord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class SanitizerServiceCachingIT {

    @Autowired
    private SanitizerService sanitizerService;

    @MockBean
    private SqlReservedWordRepository repository;

    @Test
    @Order(1)
    void testFindAllWordsCaching() {
        // Arrange: mock repository to return one word
        SqlReservedWord word = new SqlReservedWord();
        word.setId(1L);
        word.setWord("SELECT");

        when(repository.findAll()).thenReturn(List.of(word));

        // Act: first call -> should hit repository
        List<SqlReservedWordDto> firstCall = sanitizerService.findAllWords();
        assertEquals(1, firstCall.size());
        assertEquals("SELECT", firstCall.get(0).getWord());

        // Act: second call -> should return cached result, repository not called again
        List<SqlReservedWordDto> secondCall = sanitizerService.findAllWords();
        assertEquals(1, secondCall.size());

        // Verify repository.findAll() was called only once
        verify(repository, times(1)).findAll();
    }

    @Test
    @Order(2)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void testCacheEvictOnAddWord() {
        // Arrange: mock repository to return one word
        SqlReservedWord word = new SqlReservedWord();
        word.setId(1L);
        word.setWord("SELECT");

        when(repository.findAll()).thenReturn(List.of(word));

        // First call caches result
        sanitizerService.findAllWords();
        verify(repository, times(1)).findAll();

        // Mock save for new word
        SqlReservedWord newWord = new SqlReservedWord();
        newWord.setId(2L);
        newWord.setWord("DROP");
        when(repository.save(any(SqlReservedWord.class))).thenReturn(newWord);

        // Act: add a new word -> should evict cache
        sanitizerService.addWord("DROP");

        // After eviction, calling findAllWords should hit repository again
        sanitizerService.findAllWords();
        verify(repository, times(2)).findAll();
    }
}
