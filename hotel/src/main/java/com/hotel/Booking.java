package com.hotel;

import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Serializable {
    private static int idCounter = 1;
    private int bookingId;
    private String customerName;
    private String phone;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalBill;
    private String status; // ACTIVE / COMPLETED

    
    


    public Booking(String customerName, String phone, int roomNumber,
                   LocalDate checkIn, LocalDate checkOut, double pricePerDay) {
        this.bookingId    = idCounter++;
        this.customerName = customerName;
        this.phone        = phone;
        this.roomNumber   = roomNumber;
        this.checkIn      = checkIn;
        this.checkOut     = checkOut;
        long days         = checkOut.toEpochDay() - checkIn.toEpochDay();
        this.totalBill    = days * pricePerDay;
         this.status = "ACTIVE";
        
    }

    // Constructor used when loading from file
    public Booking(int id, String customerName, String phone, int roomNumber,
                   LocalDate checkIn, LocalDate checkOut, double totalBill) {
        this.bookingId    = id;
        this.customerName = customerName;
        this.phone        = phone;
        this.roomNumber   = roomNumber;
        this.checkIn      = checkIn;
        this.checkOut     = checkOut;
        this.totalBill    = totalBill;
        this.status = "ACTIVE";
        if (id >= idCounter) idCounter = id + 1;
    }

    public int       getBookingId()    { return bookingId; }
    public String    getCustomerName() { return customerName; }
    public String    getPhone()        { return phone; }
    public int       getRoomNumber()   { return roomNumber; }
    public LocalDate getCheckIn()      { return checkIn; }
    public LocalDate getCheckOut()     { return checkOut; }
    public double    getTotalBill()    { return totalBill; }
    public long      getDays()         { return checkOut.toEpochDay() - checkIn.toEpochDay(); }
    public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}
    @Override
    public String toString() {
        return bookingId + "," + customerName + "," + phone + "," + roomNumber
             + "," + checkIn + "," + checkOut + "," + totalBill+ "," + status;
    }

    public static Booking fromString(String line) {
    String[] p = line.split(",");

    Booking b = new Booking(
        Integer.parseInt(p[0].trim()),
        p[1].trim(),
        p[2].trim(),
        Integer.parseInt(p[3].trim()),
        LocalDate.parse(p[4].trim()),
        LocalDate.parse(p[5].trim()),
        Double.parseDouble(p[6].trim())
    );

    // ✅ ADD THIS
    if (p.length > 7) {
        b.setStatus(p[7].trim());
    } else {
        b.setStatus("ACTIVE");
    }

    return b;

    }
}