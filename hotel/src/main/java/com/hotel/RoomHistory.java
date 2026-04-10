package com.hotel;

import java.time.LocalDate; // ✅ ADD THIS

public class RoomHistory {

    private int roomNumber;
    private String action;
    private LocalDate date;

    public RoomHistory(int roomNumber, String action, LocalDate date) {
        this.roomNumber = roomNumber;
        this.action = action;
        this.date = date;
    }
}