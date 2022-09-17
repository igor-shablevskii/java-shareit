package ru.practicum.shareit.error;

public class UserNotValidException extends RuntimeException{

    public UserNotValidException(String message) {
        super(message);
    }
}