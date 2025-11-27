package za.co.flash.demo.sanitize.exception;

public class CustomDataAccessException extends RuntimeException {

    public CustomDataAccessException() {

    }

    public CustomDataAccessException(final String message) {
        super(message);
    }

    public CustomDataAccessException(final String message, final Throwable ex) {
        super(message, ex);
    }
}