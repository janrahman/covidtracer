package de.hhu.covidtracer.exceptions;

public class IndexPersonNotFoundException extends IndexPersonException {
    public IndexPersonNotFoundException(String message) {
        super(message);
    }

    public IndexPersonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
