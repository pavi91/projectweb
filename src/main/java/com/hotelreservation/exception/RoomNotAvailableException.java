package com.hotelreservation.exception;

/**
 * Exception thrown when a room is not available for the requested dates
 */
public class RoomNotAvailableException extends HotelSystemException {
    public RoomNotAvailableException(String message) {
        super(message, "ROOM_NOT_AVAILABLE", 409);
    }

    public RoomNotAvailableException(String message, String roomNumber) {
        super("Room " + roomNumber + " is not available: " + message, "ROOM_NOT_AVAILABLE", 409);
    }
}

