package za.co.flash.demo.sanitize.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.flash.demo.sanitize.entity.SqlReservedWord;

import java.util.Optional;

@Repository
public interface SqlReservedWordRepository extends JpaRepository<SqlReservedWord, Long> {
    Optional<SqlReservedWord> findByWord(String word);

}
