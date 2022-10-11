package ru.practicum.shareit.error;

public class NoAccessToSeeBookingException extends RuntimeException {

    public NoAccessToSeeBookingException(String message) {
        super(message);
    }
}
