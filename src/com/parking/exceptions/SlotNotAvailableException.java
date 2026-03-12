package com.parking.exceptions;

public class SlotNotAvailableException extends Exception {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}
