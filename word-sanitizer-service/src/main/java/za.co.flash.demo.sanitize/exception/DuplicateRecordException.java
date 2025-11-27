package za.co.flash.demo.sanitize.exception;

public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException() {
    }

    public DuplicateRecordException(final String message) {
        super(message);
    }

    public DuplicateRecordException(final String message, final Throwable ex) {
        super(message, ex);
    }
}