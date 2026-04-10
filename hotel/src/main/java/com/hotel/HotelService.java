package com.hotel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HotelService {
    private List<Room>    rooms;
    private List<Booking> bookings;

    public HotelService() {
        rooms    = DataManager.loadRooms();
        bookings = DataManager.loadBookings();

        // ── Sync room availability from booking STATUS (not just dates) ──────
        // First, mark all rooms as available
        rooms.forEach(r -> r.setAvailable(true));

        // Then mark occupied only those rooms that have an ACTIVE booking
        bookings.stream()
            .filter(b -> "ACTIVE".equals(b.getStatus()))
            .forEach(b -> findRoom(b.getRoomNumber())
                .ifPresent(r -> r.setAvailable(false)));
    }

    // ── Rooms ──────────────────────────────────────────────────────────────
    public List<Room> getAllRooms()        { return rooms; }
    public List<Room> getAvailableRooms() {
        return rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
    }
    public Optional<Room> findRoom(int number) {
        return rooms.stream().filter(r -> r.getRoomNumber() == number).findFirst();
    }

    public boolean addRoom(int number, String type, double price) {
        if (findRoom(number).isPresent()) return false;
        rooms.add(new Room(number, type, price));
        DataManager.saveRooms(rooms);
        return true;
    }

    public boolean deleteRoom(int number) {
        Optional<Room> r = findRoom(number);
        if (r.isEmpty() || !r.get().isAvailable()) return false;
        rooms.remove(r.get());
        DataManager.saveRooms(rooms);
        return true;
    }

    // ── Bookings ───────────────────────────────────────────────────────────
    public List<Booking> getAllBookings()    { return bookings; }
    public List<Booking> getActiveBookings() {
        return bookings.stream()
            .filter(b -> "ACTIVE".equals(b.getStatus()))
            .collect(Collectors.toList());
    }

    public Booking bookRoom(String name, String phone, int roomNumber,
                            LocalDate checkIn, LocalDate checkOut) {
        Room room = findRoom(roomNumber)
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomNumber));
        if (!room.isAvailable())
            throw new IllegalStateException("Room " + roomNumber + " is not available.");
        if (!checkOut.isAfter(checkIn))
            throw new IllegalArgumentException("Check-out must be after check-in.");

        Booking b = new Booking(name, phone, roomNumber, checkIn, checkOut, room.getPricePerDay());
        bookings.add(b);
        room.setAvailable(false);
        DataManager.saveBookings(bookings);
        DataManager.saveRooms(rooms);
        return b;
    }

    public Booking cancelBooking(int bookingId) {
        Booking booking = bookings.stream()
            .filter(b -> b.getBookingId() == bookingId && "ACTIVE".equals(b.getStatus()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active booking found with ID " + bookingId));

        booking.setStatus("CANCELLED");
        findRoom(booking.getRoomNumber()).ifPresent(r -> r.setAvailable(true));
        DataManager.saveRooms(rooms);
        DataManager.saveBookings(bookings);
        return booking;
    }

    public Booking checkout(int roomNumber) {
        Room room = findRoom(roomNumber)
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomNumber));
        if (room.isAvailable())
            throw new IllegalStateException("Room " + roomNumber + " is not currently booked.");

        Booking booking = bookings.stream()
            .filter(b -> b.getRoomNumber() == roomNumber && "ACTIVE".equals(b.getStatus()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active booking for room " + roomNumber));

        booking.setStatus("COMPLETED");
        room.setAvailable(true);
        DataManager.saveRooms(rooms);
        DataManager.saveBookings(bookings);
        return booking;
    }

    // ── Stats ──────────────────────────────────────────────────────────────
    public int    totalRooms()     { return rooms.size(); }
    public int    availableRooms() { return (int) rooms.stream().filter(Room::isAvailable).count(); }
    public int    occupiedRooms()  { return totalRooms() - availableRooms(); }
    public double totalRevenue()   { return bookings.stream().filter(b -> !"CANCELLED".equals(b.getStatus())).mapToDouble(Booking::getTotalBill).sum(); }
}