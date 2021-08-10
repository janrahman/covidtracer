package de.hhu.covidtracer.exceptions;

public class ReportNotFoundException extends ReportException {
    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
