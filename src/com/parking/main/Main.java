package com.parking.main;

import com.parking.exceptions.SlotNotAvailableException;
import com.parking.exceptions.VehicleNotFoundException;
import com.parking.service.AdminService;
import com.parking.service.ParkingService;
import com.parking.util.DisplayUtils;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ParkingService service = new ParkingService();
        AdminService admin = new AdminService(service);

        Scanner sc = new Scanner(System.in);
        boolean run = true;

        while (run) {
            DisplayUtils.header("SMART PARKING SYSTEM");
            System.out.println("1. Vehicle Entry");
            System.out.println("2. Vehicle Exit");
            System.out.println("3. View Free Slots");
            System.out.println("4. View Occupancy");
            System.out.println("5. Admin: Full Reports");
            System.out.println("6. Admin: EV Utilization");
            System.out.println("7. Admin: Search Vehicle Ticket");
            System.out.println("8. Admin: Search Slots By Type");
            System.out.println("9. Admin: Search Slots By Floor");
            System.out.println("10. Admin: Active Vehicles");
            System.out.println("11. Save State Now");
            System.out.println("12. Exit (Auto Save)");
            DisplayUtils.line();

            System.out.print("Choose option: ");
            String c = sc.nextLine().trim();

            switch (c) {

                case "1":
                    try {
                        service.vehicleEntry();
                    } catch (SlotNotAvailableException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case "2":
                    try {
                        service.vehicleExit();
                    } catch (VehicleNotFoundException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;

                case "3":
                    service.getFloorMap().forEach((floor, list) -> {
                        long free = list.stream().filter(s -> !s.isOccupied()).count();
                        System.out.printf("Floor %d: Free %d / Total %d%n",
                                floor, free, list.size());
                    });
                    break;

                case "4":
                    service.getFloorMap().forEach((f, list) -> {
                        long occ = list.stream().filter(s -> s.isOccupied()).count();
                        double percent = (occ * 100.0) / list.size();
                        System.out.printf("Floor %d: %.2f%% Occupied%n", f, percent);
                    });
                    break;

                case "5":
                    admin.showFullReports();
                    break;

                case "6":
                    admin.showEVUsage();
                    break;

                case "7":
                    System.out.print("Enter vehicle number: ");
                    String vno = sc.nextLine().trim();
                    var t = admin.searchByVehicle(vno);
                    if (t == null) System.out.println("Not found.");
                    else System.out.println(t);
                    break;

                case "8":
                    System.out.print("Enter slot type (TWO_WHEELER/FOUR_WHEELER/EV/VIP): ");
                    String type = sc.nextLine().trim();
                    List<?> typeSlots = admin.searchSlotsByType(type);
                    typeSlots.forEach(System.out::println);
                    break;

                case "9":
                    System.out.print("Enter floor number: ");
                    int f = Integer.parseInt(sc.nextLine().trim());
                    List<?> floorSlots = admin.searchSlotsByFloor(f);
                    floorSlots.forEach(System.out::println);
                    break;

                case "10":
                    admin.showActiveVehicles();
                    break;

                case "11":
                    service.saveStateToDisk();
                    break;

                case "12":
                    service.saveStateToDisk();
                    run = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            DisplayUtils.line();
        }

        System.out.println("System shutting down...");
    }
}
