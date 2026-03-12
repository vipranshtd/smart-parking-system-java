package com.parking.service;

import com.parking.exceptions.SlotNotAvailableException;
import com.parking.exceptions.VehicleNotFoundException;
import com.parking.model.*;
import com.parking.util.FeeUtils;
import com.parking.util.TimeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ParkingService {

    private List<ParkingSlot> slots;
    private Map<String, Vehicle> activeVehicles;
    private Map<String, Ticket> activeTickets;
    private List<Ticket> ticketHistory;

    private PriorityQueue<ParkingSlot> pq4W;
    private PriorityQueue<ParkingSlot> pq2W;
    private PriorityQueue<ParkingSlot> pqEV;
    private PriorityQueue<ParkingSlot> pqVIP;

    private Map<Integer, List<ParkingSlot>> floorMap = new TreeMap<>();

    private AtomicInteger ticketCounter = new AtomicInteger(1000);

    public ParkingService() {

        Comparator<ParkingSlot> cmp =
                Comparator.comparingInt(ParkingSlot::getFloorNo)
                        .thenComparingInt(ParkingSlot::getSlotId);

        pq4W = new PriorityQueue<>(cmp);
        pq2W = new PriorityQueue<>(cmp);
        pqEV = new PriorityQueue<>(cmp);
        pqVIP = new PriorityQueue<>(cmp);

        PersistenceService.State state = PersistenceService.loadState();

        if (state != null) {
            System.out.println("Loaded previous state.");

            this.slots = state.slots;
            this.activeVehicles = state.activeVehicles;
            this.activeTickets = state.activeTickets;
            this.ticketHistory = state.ticketHistory;

            rebuildIndexes();
        } else {
            this.slots = new ArrayList<>();
            this.activeVehicles = new HashMap<>();
            this.activeTickets = new HashMap<>();
            this.ticketHistory = new ArrayList<>();

            seedSlots();
        }
    }

    private void rebuildIndexes() {
        pq4W.clear();
        pq2W.clear();
        pqEV.clear();
        pqVIP.clear();
        floorMap.clear();

        for (ParkingSlot s : slots) {
            floorMap.computeIfAbsent(s.getFloorNo(), k -> new ArrayList<>()).add(s);

            if (!s.isOccupied()) {
                switch (s.getSlotType()) {
                    case "VIP": pqVIP.add(s); break;
                    case "EV": pqEV.add(s); break;
                    case "FOUR_WHEELER": pq4W.add(s); break;
                    default: pq2W.add(s);
                }
            }
        }

        int max = ticketHistory.stream().mapToInt(Ticket::getTicketId).max().orElse(1000);
        ticketCounter.set(max + 1);
    }

    private void seedSlots() {
        int id = 1;

        for (int i = 0; i < 10; i++, id++) addSlot(id, 0, "VIP");

        for (int f = 1; f <= 2; f++)
            for (int i = 0; i < 40; i++, id++)
                addSlot(id, f, (i % 10 == 0) ? "EV" : "FOUR_WHEELER");

        for (int f = 3; f <= 4; f++)
            for (int i = 0; i < 40; i++, id++)
                addSlot(id, f, (i % 12 == 0) ? "EV" : "TWO_WHEELER");
    }

    private void addSlot(int id, int floor, String type) {
        ParkingSlot s = new ParkingSlot(id, floor, type);
        slots.add(s);
        floorMap.computeIfAbsent(floor, k -> new ArrayList<>()).add(s);

        switch (type) {
            case "VIP": pqVIP.add(s); break;
            case "EV": pqEV.add(s); break;
            case "FOUR_WHEELER": pq4W.add(s); break;
            default: pq2W.add(s);
        }
    }

    private ParkingSlot allocateSlot(String vType, String category)
            throws SlotNotAvailableException {

        if (category.equals("VIP")) {
            ParkingSlot s = pqVIP.poll();
            if (s != null) return s;
        }

        if (category.equals("EV")) {
            ParkingSlot s = pqEV.poll();
            if (s != null) return s;
        }

        if (vType.equals("FOUR_WHEELER")) {
            ParkingSlot s = pq4W.poll();
            if (s != null) return s;
            ParkingSlot e = pqEV.poll();
            if (e != null) return e;
        } else {
            ParkingSlot s = pq2W.poll();
            if (s != null) return s;
            ParkingSlot e = pqEV.poll();
            if (e != null) return e;
        }

        throw new SlotNotAvailableException("No available slots.");
    }

    private void returnSlot(ParkingSlot s) {
        if (s == null) return;

        switch (s.getSlotType()) {
            case "VIP": pqVIP.add(s); break;
            case "EV": pqEV.add(s); break;
            case "FOUR_WHEELER": pq4W.add(s); break;
            default: pq2W.add(s);
        }
    }

    public String vehicleEntry() throws SlotNotAvailableException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter vehicle number: ");
        String vNo = sc.nextLine().trim().toUpperCase();

        System.out.print("Owner name: ");
        String owner = sc.nextLine().trim();

        System.out.print("Vehicle type (2W/4W): ");
        String typ = sc.nextLine().trim();
        String vType = typ.equalsIgnoreCase("2W") ? "TWO_WHEELER" : "FOUR_WHEELER";

        System.out.print("Category (NORMAL/EV/VIP): ");
        String cat = sc.nextLine().trim().toUpperCase();
        if (!cat.equals("NORMAL") && !cat.equals("EV") && !cat.equals("VIP"))
            cat = "NORMAL";

        Timestamp entry = new Timestamp(System.currentTimeMillis());

        Vehicle v = vType.equals("TWO_WHEELER")
                ? new TwoWheeler(vNo, owner, cat, entry)
                : new FourWheeler(vNo, owner, cat, entry);

        ParkingSlot slot = allocateSlot(vType, cat);
        slot.occupy(vNo);

        activeVehicles.put(vNo, v);

        Ticket t = new Ticket(ticketCounter.incrementAndGet(),
                vNo, slot.getSlotId(), slot.getFloorNo(), entry);

        activeTickets.put(vNo, t);

        PersistenceService.saveState(slots, activeVehicles, activeTickets, ticketHistory);

        System.out.println("Allocated → Floor " + slot.getFloorNo() +
                ", Slot " + slot.getSlotId());
        System.out.println("Ticket ID: " + t.getTicketId());

        return String.valueOf(t.getTicketId());
    }

    public double vehicleExit() throws VehicleNotFoundException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter vehicle number: ");
        String vNo = sc.nextLine().trim().toUpperCase();

        if (!activeVehicles.containsKey(vNo))
            throw new VehicleNotFoundException("Vehicle not found.");

        Vehicle v = activeVehicles.get(vNo);
        Ticket t = activeTickets.get(vNo);

        Timestamp exit = new Timestamp(System.currentTimeMillis());
        long mins = TimeUtils.minutesBetween(t.getEntryTime(), exit);

        double amt = FeeUtils.calculateFee(v, mins);

        t.setExit(exit, amt);
        ticketHistory.add(t);

        ParkingSlot free = null;
        for (ParkingSlot s : slots) {
            if (s.getSlotId() == t.getSlotId()) {
                s.free();
                free = s;
                break;
            }
        }

        returnSlot(free);

        activeVehicles.remove(vNo);
        activeTickets.remove(vNo);

        writeReceipt(t);

        PersistenceService.saveState(slots, activeVehicles, activeTickets, ticketHistory);

        printBill(
        t.getTicketId(),
        vNo,
        v.getVehicleType(),
        v.getCategory(),
        t.getFloorNo(),
        t.getSlotId(),
        t.getEntryTime().toString(),
        exit.toString(),
        mins,
        v.getRatePerHour(),
        amt
);

return amt;

    }

    private void writeReceipt(Ticket t) {
        try {
            File dir = new File("receipts");
            if (!dir.exists()) dir.mkdirs();

            String fname = "receipts/ticket_" + t.getTicketId() + ".txt";

            PrintWriter pw = new PrintWriter(new FileWriter(fname));
            pw.println("SMART PARKING RECEIPT");
            pw.println("Ticket ID: " + t.getTicketId());
            pw.println("Vehicle: " + t.getVehicleNo());
            pw.println("Floor: " + t.getFloorNo());
            pw.println("Slot: " + t.getSlotId());
            pw.println("Entry: " + t.getEntryTime());
            pw.println("Exit: " + t.getExitTime());
            pw.println("Amount Paid: ₹" + t.getAmount()); 
            pw.close();

        } catch (Exception e) {
            System.out.println("Receipt write error: " + e.getMessage());
        }
    }

    public Map<Integer, List<ParkingSlot>> getFloorMap() {
        return floorMap;
    }

    public Ticket searchByVehicle(String vNo) {
        return activeTickets.get(vNo.toUpperCase());
    }

    public List<ParkingSlot> searchSlotsByType(String type) {
        return slots.stream()
                .filter(s -> s.getSlotType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<ParkingSlot> searchSlotsByFloor(int floor) {
        return floorMap.getOrDefault(floor, Collections.emptyList());
    }

    public void showRevenueSummary() {
        double total = ticketHistory.stream().mapToDouble(Ticket::getAmount).sum();
        System.out.println("Total Revenue: ₹" + total);
    }

    public void showEVUtilization() {
        long total = slots.stream().filter(s -> s.getSlotType().equals("EV")).count();
        long occ = slots.stream().filter(s -> s.getSlotType().equals("EV") && s.isOccupied()).count();
        double p = (total == 0) ? 0 : (occ * 100.0 / total);

        System.out.printf("EV Utilization: %.2f%% (%d/%d)\n", p, occ, total);
    }

    public void listActive() {
        if (activeVehicles.isEmpty()) {
            System.out.println("No active vehicles.");
            return;
        }

        activeVehicles.forEach((k, v) -> {
            Ticket t = activeTickets.get(k);
            System.out.printf("%s | %s | %s | Cat:%s | Slot:%d\n",
                    k, v.getOwnerName(), v.getVehicleType(),
                    v.getCategory(), t.getSlotId());
        });
    }

    public void saveStateToDisk() {
        PersistenceService.saveState(slots, activeVehicles, activeTickets, ticketHistory);
    }
    private void printBill(
        int ticketId,
        String vehicleNo,
        String vType,
        String category,
        int floor,
        int slot,
        String entryTime,
        String exitTime,
        long minutes,
        double rate,
        double amount
) {
    System.out.println("============================================");
    System.out.println("             Smart Parking System           ");
    System.out.println("============================================");

    System.out.println("--------------- BILL / RECEIPT -------------");
    System.out.println("Ticket ID       : " + ticketId);
    System.out.println("Vehicle Number  : " + vehicleNo);
    System.out.println("Vehicle Type    : " + vType);
    System.out.println("Category        : " + category);

    System.out.println("--------------------------------------------");

    System.out.println("Floor           : " + floor);
    System.out.println("Slot            : " + slot);
    System.out.println("Entry Time      : " + entryTime);
    System.out.println("Exit Time       : " + exitTime);
    System.out.println("Duration        : " + minutes + " minutes");

    System.out.println("--------------------------------------------");

    System.out.println("Rate Per Hour   : Rs" + rate);
    System.out.println("Total Amount    : Rs" + amount);

    System.out.println("============================================");
    System.out.println("       Thank you for using our service!     ");
    System.out.println("============================================");
}

}
