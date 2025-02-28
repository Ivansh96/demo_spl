package ru.test.demo.spl.demo_spl.validation;

public class EventDoesNotExistException extends Exception {
    public EventDoesNotExistException (String message) {
        super(message);
    }
}
