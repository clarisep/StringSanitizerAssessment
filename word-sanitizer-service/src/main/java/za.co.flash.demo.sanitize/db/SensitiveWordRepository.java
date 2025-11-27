package za.co.flash.demo.sanitize.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.flash.demo.sanitize.model.SensitiveWord;

import java.util.Optional;

@Repository
public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {
    Optional<SensitiveWord> findByWord(String word);

}
