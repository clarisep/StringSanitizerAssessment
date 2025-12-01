package za.co.flash.demo.sanitize.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SanitizeRequest {

    @NotBlank(message = "Input must not be blank")
    @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
            message = "Input must start with a letter or underscore, and may only contain letters, digits, underscores, spaces, or asterisks"
    )
    @Schema(example = "Enter the input here")
    private String input;

}