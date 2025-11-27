package za.co.flash.demo.sanitize.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "The request containing the string to be sanitized")
@Data
@AllArgsConstructor
public class SensitiveWordRequestDTO {

    @NotBlank(message = "Input may not be blank")
    @Schema(description = "The input string that may contain sensitive words")
    private final String input;
}

