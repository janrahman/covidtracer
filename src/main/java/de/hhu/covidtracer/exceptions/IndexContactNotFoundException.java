package de.hhu.covidtracer.exceptions;

public class IndexContactNotFoundException extends RuntimeException {
    public IndexContactNotFoundException(String message) {
        super(message);
    }

    public IndexContactNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
