package com.parking.util;

import com.parking.model.Vehicle;

public class FeeUtils {

    public static double calculateFee(Vehicle v, long minutes) {

    
    int hours = (int) Math.ceil(minutes / 60.0);

    if (hours < 1) {
        hours = 1;
    }

    double rate = v.getRatePerHour();
    double amount = hours * rate;

    return amount;
}

}
