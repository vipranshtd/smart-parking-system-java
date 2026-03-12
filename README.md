# Smart Parking System (Java)

A console-based Smart Parking System implemented in Java using object-oriented design.  
The system manages vehicle entry and exit, allocates parking slots efficiently, calculates parking fees, and generates parking receipts.

--------------------------------------------

FEATURES

- Vehicle entry and exit management
- Automatic parking slot allocation
- Support for two-wheelers and four-wheelers
- Special categories like EV and VIP
- Parking fee calculation based on duration
- Admin reports and vehicle search
- Parking ticket generation
- Receipt generation
- Data persistence using file storage

--------------------------------------------

TECHNOLOGIES USED

Language
- Java

Concepts
- Object Oriented Programming
- Exception Handling
- Collections Framework
- File Handling
- Serialization

--------------------------------------------

PROJECT STRUCTURE

smart-parking-system-java
|
|-- src
|   |-- com
|       |-- parking
|           |-- main
|           |   |-- Main.java
|           |
|           |-- model
|           |   |-- Vehicle.java
|           |   |-- TwoWheeler.java
|           |   |-- FourWheeler.java
|           |   |-- ParkingSlot.java
|           |   |-- Ticket.java
|           |
|           |-- service
|           |   |-- ParkingService.java
|           |   |-- AdminService.java
|           |   |-- PersistenceService.java
|           |
|           |-- util
|           |   |-- DisplayUtils.java
|           |   |-- FeeUtils.java
|           |   |-- TimeUtils.java
|           |
|           |-- exceptions
|               |-- InvalidVehicleTypeException.java
|               |-- SlotNotAvailableException.java
|               |-- VehicleNotFoundException.java
|
|-- data
|   |-- state.ser
|
|-- receipts
|   |-- ticket_1015.txt
|
|-- README.md

--------------------------------------------

HOW IT WORKS

1. Vehicles enter the parking system and a suitable parking slot is allocated.
2. A parking ticket is generated for the vehicle.
3. When the vehicle exits, the system calculates the parking fee based on the duration.
4. A receipt is generated and stored.
5. The parking slot becomes available again.

--------------------------------------------

RUNNING THE PROGRAM

Compile the project:

javac src/com/parking/main/Main.java

Run the program:

java com.parking.main.Main

--------------------------------------------

FUTURE IMPROVEMENTS

- Graphical user interface
- Online booking system
- Integration with payment gateway
- Real-time parking availability dashboard
