package za.co.flash.demo.sanitize.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import za.co.flash.demo.sanitize.model.SensitiveWord;

import java.util.List;

@Schema(description = "The response containing the masked data")
@Data
public class SensitiveWordResponseDTO {

    @Schema(description = "The sanitized string containing the masked sensitive data")
    private String sanitizedString;

    @Schema(description = "The list of sensitive words in the db")
    private List<SensitiveWord> sanitizedList;


    public SensitiveWordResponseDTO(final String sanitizedString) {

        this.sanitizedString = sanitizedString;
    }

    public SensitiveWordResponseDTO() {
    }

    public SensitiveWordResponseDTO(final List<SensitiveWord> words) {
        this.sanitizedList = words;
    }
}
