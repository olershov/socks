package ru.backspark.testing.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.backspark.testing.exception.InvalidDataException;
import ru.backspark.testing.exception.ObjectNotFoundException;
import ru.backspark.testing.model.dto.ErrorDto;

import java.util.ArrayList;

@RestControllerAdvice(basePackages = "ru.backspark.testing.controller")
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final var errors = new ArrayList<String>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(objectError ->
                errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

        final var error = new ErrorDto("The argument(s) have not been validated", errors);
        LOGGER.error(error.toString());
        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorDto> handleException(InvalidDataException e) {
        LOGGER.error(e.getMessage());
        return getErrorDTOResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorDto> handleException(ObjectNotFoundException e) {
        LOGGER.error(e.getMessage());
        return getErrorDTOResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorDto> handleException(OptimisticLockingFailureException e) {
        var message = "Conflict of update the data, please try again";
        LOGGER.error(message);
        return getErrorDTOResponseEntity(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        var message = "Server Error";
        LOGGER.error(message);
        return getErrorDTOResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<ErrorDto> getErrorDTOResponseEntity(HttpStatus httpStatus, String message) {
        final var response = new ErrorDto(message, httpStatus.value() + "");
        return new ResponseEntity<>(response, httpStatus);
    }
}
