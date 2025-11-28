package za.co.flash.demo.sanitize.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "The response containing the masked data")
@Data
public class SqlReservedWordsResponseDto {

    @Schema(description = "The list of sensitive words in the db")
    private List<String> sanitizedList;

    public SqlReservedWordsResponseDto() {
    }

    public SqlReservedWordsResponseDto(final List<String> words) {
        this.sanitizedList = words;
    }
}
