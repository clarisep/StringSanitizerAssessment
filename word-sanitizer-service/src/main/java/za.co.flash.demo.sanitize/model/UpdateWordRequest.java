package za.co.flash.demo.sanitize.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateWordRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
            message = "Word must start with a letter or underscore, and may contain letters, digits, underscores, or spaces")
    private String oldWord;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z_][A-Za-z0-9_ *]*$",
            message = "Word must start with a letter or underscore, and may contain letters, digits, underscores, or spaces")
    private String newWord;

    public UpdateWordRequest(final String oldWord, final String newWord) {
        this.oldWord = oldWord;
        this.newWord = newWord;
    }
}
