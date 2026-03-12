package com.parking.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    private int ticketId;
    private String vehicleNo;
    private int slotId;
    private int floorNo;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private double amount;

    public Ticket(int ticketId, String vehicleNo, int slotId, int floorNo, Timestamp entryTime) {
        this.ticketId = ticketId;
        this.vehicleNo = vehicleNo;
        this.slotId = slotId;
        this.floorNo = floorNo;
        this.entryTime = entryTime;
    }

    public void setExit(Timestamp exitTime, double amount) {
        this.exitTime = exitTime;
        this.amount = amount;
    }

    public int getTicketId() { return ticketId; }
    public String getVehicleNo() { return vehicleNo; }
    public int getSlotId() { return slotId; }
    public int getFloorNo() { return floorNo; }
    public Timestamp getEntryTime() { return entryTime; }
    public Timestamp getExitTime() { return exitTime; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " | Vehicle=" + vehicleNo +
                " | Slot=" + slotId + " | Floor=" + floorNo +
                " | Amount=" + amount;
    }
}
