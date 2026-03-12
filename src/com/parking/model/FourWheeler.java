package com.parking.model;

import java.sql.Timestamp;

public class FourWheeler extends Vehicle {
    private static final long serialVersionUID = 1L;

    public FourWheeler(String vNo, String owner, String category, Timestamp entryTime) {
        super(vNo, owner, "FOUR_WHEELER", category, entryTime);
    }

    @Override
    public double getRatePerHour() {
        if (category.equalsIgnoreCase("EV")) return 30;
        if (category.equalsIgnoreCase("VIP")) return 100;
        return 60;
    }
}
