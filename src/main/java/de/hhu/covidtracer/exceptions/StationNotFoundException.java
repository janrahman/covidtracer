package de.hhu.covidtracer.exceptions;

public class StationNotFoundException extends StationException{
    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
