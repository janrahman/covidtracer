package de.hhu.covidtracer.exceptions;

public class IndexPersonException extends RuntimeException {
    public IndexPersonException(String message) {
        super(message);
    }

    public IndexPersonException(String message, Throwable cause) {
        super(message, cause);
    }
}
