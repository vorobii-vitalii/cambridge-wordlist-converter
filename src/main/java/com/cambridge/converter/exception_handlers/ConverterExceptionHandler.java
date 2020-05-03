package com.cambridge.converter.exception_handlers;

import com.cambridge.converter.exceptions.WrongFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ConverterExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = WrongFormatException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    protected String handleWrongFormat( WrongFormatException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(value = IOException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    protected String handleIOError( IOException ex) {
        return "Error while reading file";
    }

}
