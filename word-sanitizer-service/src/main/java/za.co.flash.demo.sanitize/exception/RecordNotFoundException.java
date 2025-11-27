package za.co.flash.demo.sanitize.exception;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException() {
    }

    public RecordNotFoundException(final String message) {
        super(message);
    }

    public RecordNotFoundException(final String message, final Throwable ex) {
        super(message, ex);
    }
}