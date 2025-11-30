package za.co.flash.demo.sanitize.model;

import lombok.Data;

@Data
public class UpdateWordRequest {
    private String oldWord;
    private String newWord;

    public UpdateWordRequest(final String oldWord, final String newWord) {
        this.oldWord = oldWord;
        this.newWord = newWord;
    }
}
