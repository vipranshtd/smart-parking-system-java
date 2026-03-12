package com.parking.model;

import java.sql.Timestamp;

public class TwoWheeler extends Vehicle {
    private static final long serialVersionUID = 1L;

    public TwoWheeler(String vNo, String owner, String category, Timestamp entryTime) {
        super(vNo, owner, "TWO_WHEELER", category, entryTime);
    }

    @Override
    public double getRatePerHour() {
        if (category.equalsIgnoreCase("EV")) return 20;
        if (category.equalsIgnoreCase("VIP")) return 80;
        return 50;
    }
}
