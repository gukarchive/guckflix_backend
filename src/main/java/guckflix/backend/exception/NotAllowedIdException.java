package guckflix.backend.exception;

public class NotAllowedIdException extends RuntimeException {
    public NotAllowedIdException() {
    }

    public NotAllowedIdException(String message) {
        super(message);
    }

    public NotAllowedIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAllowedIdException(Throwable cause) {
        super(cause);
    }

    public NotAllowedIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
