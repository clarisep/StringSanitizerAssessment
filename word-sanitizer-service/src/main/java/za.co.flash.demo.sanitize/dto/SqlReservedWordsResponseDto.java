package za.co.flash.demo.sanitize.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The response containing the masked data")
public class SqlReservedWordsResponseDto {

    @Schema(description = "The sanitized output for a single input string")
    private String sanitized;

    @Schema(description = "The list of sensitive words in the database or multiple sanitized results")
    private List<String> words;

    public SqlReservedWordsResponseDto() {}

    public SqlReservedWordsResponseDto(final String sanitized) {
        this.sanitized = sanitized;
    }

    public SqlReservedWordsResponseDto(final List<String> words) {
        this.words = words;
    }
}
