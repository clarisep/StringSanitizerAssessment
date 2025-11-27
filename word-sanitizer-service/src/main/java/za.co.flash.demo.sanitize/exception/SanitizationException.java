package za.co.flash.demo.sanitize.exception;

public class SanitizationException extends RuntimeException {

    public SanitizationException(final String message, final Throwable ex) {
        super(message, ex);
    }
}
