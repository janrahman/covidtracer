package de.hhu.covidtracer.exceptions;

public class ContactPersonNotFoundException extends ContactPersonException {
    public ContactPersonNotFoundException(String message) {
        super(message);
    }

    public ContactPersonNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
