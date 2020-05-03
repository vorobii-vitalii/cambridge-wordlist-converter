package com.cambridge.converter.exceptions;

public class WrongFormatException extends Exception {

    public WrongFormatException(String msg, Throwable t) {
        super(msg, t);
    }

    public WrongFormatException(String msg) {
        super(msg);
    }

}
