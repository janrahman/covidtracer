package de.hhu.covidtracer.exceptions;

public class DataCollectionException extends RuntimeException {
    public DataCollectionException(String message) {
        super(message);
    }

    public DataCollectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
