package com.parking.service;

import com.parking.model.ParkingSlot;
import com.parking.model.Ticket;
import com.parking.model.Vehicle;

import java.io.*;
import java.util.List;
import java.util.Map;

public class PersistenceService {

    private static final String STATE_FILE = "data/state.ser";

    // Save entire system state to disk
    public static void saveState(List<ParkingSlot> slots,
                                 Map<String, Vehicle> activeVehicles,
                                 Map<String, Ticket> activeTickets,
                                 List<Ticket> ticketHistory) {

        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE));
            oos.writeObject(slots);
            oos.writeObject(activeVehicles);
            oos.writeObject(activeTickets);
            oos.writeObject(ticketHistory);
            oos.close();

            System.out.println("State saved to disk.");
        }
        catch (Exception e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    // Load entire system state
    @SuppressWarnings("unchecked")
    public static State loadState() {
        try {
            File file = new File(STATE_FILE);
            if (!file.exists()) return null;

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<ParkingSlot> slots = (List<ParkingSlot>) ois.readObject();
            Map<String, Vehicle> activeVehicles = (Map<String, Vehicle>) ois.readObject();
            Map<String, Ticket> activeTickets = (Map<String, Ticket>) ois.readObject();
            List<Ticket> ticketHistory = (List<Ticket>) ois.readObject();
            ois.close();

            return new State(slots, activeVehicles, activeTickets, ticketHistory);
        }
        catch (Exception e) {
            System.err.println("Error loading state: " + e.getMessage());
            return null;
        }
    }

    // Helper class to package loaded objects
    public static class State {
        public List<ParkingSlot> slots;
        public Map<String, Vehicle> activeVehicles;
        public Map<String, Ticket> activeTickets;
        public List<Ticket> ticketHistory;

        public State(List<ParkingSlot> slots,
                     Map<String, Vehicle> activeVehicles,
                     Map<String, Ticket> activeTickets,
                     List<Ticket> ticketHistory) {
            this.slots = slots;
            this.activeVehicles = activeVehicles;
            this.activeTickets = activeTickets;
            this.ticketHistory = ticketHistory;
        }
    }
}
