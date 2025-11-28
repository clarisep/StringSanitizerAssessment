package za.co.flash.demo.sanitize.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sensitive_words")
@Data
public class SqlReservedWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String word;
}
