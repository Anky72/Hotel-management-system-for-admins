package com.hotel;

import java.io.Serializable;

public class Room implements Serializable {
    private int roomNumber;
    private String type;
    private double pricePerDay;
    private boolean available;

    public Room(int roomNumber, String type, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public int getRoomNumber()          { return roomNumber; }
    public String getType()             { return type; }
    public double getPricePerDay()      { return pricePerDay; }
    public boolean isAvailable()        { return available; }
    public void setAvailable(boolean v) { this.available = v; }
    public void setType(String t)       { this.type = t; }
    public void setPricePerDay(double p){ this.pricePerDay = p; }

    @Override
    public String toString() {
        return roomNumber + "," + type + "," + pricePerDay + "," + available;
    }

    public static Room fromString(String line) {
        String[] p = line.split(",");
        Room r = new Room(Integer.parseInt(p[0].trim()), p[1].trim(), Double.parseDouble(p[2].trim()));
        r.setAvailable(Boolean.parseBoolean(p[3].trim()));
        return r;
    }
}