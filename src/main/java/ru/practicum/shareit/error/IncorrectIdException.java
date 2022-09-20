package ru.practicum.shareit.error;

public class IncorrectIdException extends RuntimeException {

    public IncorrectIdException(String message) {
        super(message);
    }
}