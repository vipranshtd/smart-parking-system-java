package com.parking.model;

import java.io.Serializable;
import java.sql.Timestamp;

public abstract class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String vehicleNo;
    protected String ownerName;
    protected String vehicleType;  // TWO_WHEELER / FOUR_WHEELER
    protected String category;     // NORMAL / EV / VIP
    protected Timestamp entryTime;

    public Vehicle(String vehicleNo, String ownerName, String vehicleType, String category, Timestamp entryTime) {
        this.vehicleNo = vehicleNo;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.category = category;
        this.entryTime = entryTime;
    }

    public String getVehicleNo() { return vehicleNo; }
    public String getOwnerName() { return ownerName; }
    public String getVehicleType() { return vehicleType; }
    public String getCategory() { return category; }
    public Timestamp getEntryTime() { return entryTime; }

    public void setEntryTime(Timestamp entryTime) { this.entryTime = entryTime; }

    public abstract double getRatePerHour();

    @Override
    public String toString() {
        return vehicleNo + " | " + ownerName + " | " + vehicleType +
                " | " + category + " | Entry: " + entryTime;
    }
}
