package de.hhu.covidtracer.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.format.DateTimeParseException;

@ControllerAdvice("de.hhu.covidtracer")
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(UploadException.class)
    public String uploadError(
            UploadException ex,
            RedirectAttributes redirectAttributes) {
        String errorMsg = "UploadException: " + ex.getMessage();

        log.error(errorMsg);
        redirectAttributes.addFlashAttribute(
                "messageDanger", errorMsg);

        return "redirect:/";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IndexPersonNotFoundException.class)
    public void indexNotFoundError(IndexPersonNotFoundException ex) {
        log.error("IndexNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AppointmentNotFoundException.class)
    public void appointmentNotFound(AppointmentNotFoundException ex) {
        log.error("AppointmentNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ContactPersonNotFoundException.class)
    public void contactNotFound(ContactPersonNotFoundException ex) {
        log.error("ContactPersonNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IndexContactNotFoundException.class)
    public void indexContactNotFound(IndexContactNotFoundException ex) {
        log.error("IndexContactNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReportNotFoundException.class)
    public void reportNotFound(ReportNotFoundException ex) {
        log.error("ReportNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StationNotFoundException.class)
    public void stationNotFound(StationNotFoundException ex) {
        log.error("StationNotFoundException: " + ex.getMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReportException.class)
    public void reportExceptionError(ReportException ex) {
        log.error("ReportException - Catching: " + ex
                .getClass()
                .getSimpleName());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StationException.class)
    public void stationExceptionError(StationException ex) {
        log.error("StationException - Catching: " + ex
                .getClass()
                .getSimpleName());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndexPersonException.class)
    public void indexPersonExceptionError(IndexPersonException ex) {
        log.error("IndexPersonException - Catching: " + ex
                .getClass()
                .getSimpleName());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public void ioExceptionError(IOException ex) {
        log.error("IOException - Catching: " + ex
                .getClass()
                .getSimpleName());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public void constraintViolationError(
            ConstraintViolationException ex) {
        ex.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation
                        .getPropertyPath()
                        .toString() +
                        ": " +
                        constraintViolation
                                .getMessage())
                .forEach(log::error);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public void dateTimeParseError(DateTimeParseException ex) {
        log.error(
                "DateTimeParseException - Catching:" + ex
                        .getClass()
                        .getSimpleName());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public void handleAll(Exception e) {
        log.error("Unhandled exception occurred", e);
    }
}
