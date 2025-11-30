package za.co.flash.demo.sanitize.exception;

public class EntitySaveException extends RuntimeException {

    public EntitySaveException() {
    }

    public EntitySaveException(final String message) {
        super(message);
    }

    public EntitySaveException(final String message, final Throwable ex) {
        super(message, ex);
    }
}