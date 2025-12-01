package za.co.flash.demo.sanitize.dto;

import lombok.Data;

@Data
public class SqlReservedWordDto {

    private Long id;
    private String word;

    public SqlReservedWordDto(final Long id, final String word) {
        this.id = id;
        this.word = word;
    }
    public SqlReservedWordDto(final String word) {
        this.word = word;
    }



}