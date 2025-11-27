package za.co.flash.demo.sanitize.utils;

public final class WordValidator {

    private WordValidator() {

    }

    public static void validateWord(final String word) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("Word cannot be empty");
        }
        if (!word.matches("^[A-Za-z_][A-Za-z0-9_ *]*$")) {
            throw new IllegalArgumentException("Word contains invalid characters");
        }
    }

}
