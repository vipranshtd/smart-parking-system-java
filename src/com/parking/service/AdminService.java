package com.parking.service;

import com.parking.model.ParkingSlot;
import com.parking.model.Ticket;

import java.util.List;

public class AdminService {

    private ParkingService service;

    public AdminService(ParkingService service) {
        this.service = service;
    }

    public void showFullReports() {
        System.out.println("\n--- ADMIN REPORTS ---");
        service.showRevenueSummary();
        service.showEVUtilization();
    }

    public void showEVUsage() {
        service.showEVUtilization();
    }

    public void showActiveVehicles() {
        service.listActive();
    }

    public Ticket searchByVehicle(String vNo) {
        return service.searchByVehicle(vNo);
    }

    public List<ParkingSlot> searchSlotsByType(String type) {
        return service.searchSlotsByType(type);
    }

    public List<ParkingSlot> searchSlotsByFloor(int floor) {
        return service.searchSlotsByFloor(floor);
    }
}
