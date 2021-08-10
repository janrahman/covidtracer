package de.hhu.covidtracer.exceptions;

public class ContactPersonException extends RuntimeException {
    public ContactPersonException(String message) {
        super(message);
    }

    public ContactPersonException(String message, Throwable cause) {
        super(message, cause);
    }
}
