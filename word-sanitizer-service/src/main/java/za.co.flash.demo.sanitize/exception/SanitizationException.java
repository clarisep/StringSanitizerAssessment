package za.co.flash.demo.sanitize.exception;

public class SanitizationException extends RuntimeException {

    public SanitizationException() {

    }

    public SanitizationException(final String message) {
        super(message);
    }

    public SanitizationException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
