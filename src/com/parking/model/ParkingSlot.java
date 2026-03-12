package com.parking.model;

import java.io.Serializable;

public class ParkingSlot implements Comparable<ParkingSlot>, Serializable {
    private static final long serialVersionUID = 1L;

    private int slotId;
    private int floorNo;
    private String slotType;   // TWO_WHEELER, FOUR_WHEELER, EV, VIP
    private boolean occupied;
    private String vehicleNo;

    public ParkingSlot(int slotId, int floorNo, String slotType) {
        this.slotId = slotId;
        this.floorNo = floorNo;
        this.slotType = slotType;
        this.occupied = false;
        this.vehicleNo = null;
    }

    public int getSlotId() { return slotId; }
    public int getFloorNo() { return floorNo; }
    public String getSlotType() { return slotType; }
    public boolean isOccupied() { return occupied; }
    public String getVehicleNo() { return vehicleNo; }

    public void occupy(String vNo) {
        occupied = true;
        vehicleNo = vNo;
    }

    public void free() {
        occupied = false;
        vehicleNo = null;
    }

    @Override
    public int compareTo(ParkingSlot o) {
        if (this.floorNo != o.floorNo)
            return Integer.compare(this.floorNo, o.floorNo);
        return Integer.compare(this.slotId, o.slotId);
    }

    @Override
    public String toString() {
        return "Slot[" + slotId + ", Floor=" + floorNo + ", Type=" + slotType +
                ", Occupied=" + occupied + ", Vehicle=" + vehicleNo + "]";
    }
}
