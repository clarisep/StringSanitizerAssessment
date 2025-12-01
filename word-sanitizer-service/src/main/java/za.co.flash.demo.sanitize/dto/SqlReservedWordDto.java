package za.co.flash.demo.sanitize.dto;


import lombok.Data;

import java.util.List;

@Data
public class SqlReservedWordDto {

    private Long id;
    private String word;


    // Constructors
    public SqlReservedWordDto() {}

    public SqlReservedWordDto(final Long id, final String word) {
        this.id = id;
        this.word = word;
    }
    public SqlReservedWordDto(final String word) {
        this.word = word;
    }



}