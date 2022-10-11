package ru.practicum.shareit.error;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException(String message) {
        super(message);
    }
}