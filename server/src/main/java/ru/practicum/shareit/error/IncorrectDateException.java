package ru.practicum.shareit.error;

public class IncorrectDateException extends RuntimeException {

    public IncorrectDateException(String message) {
        super(message);
    }
}