package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CampusDataRepository {

    private static final Map<String, Room> roomStore = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensorStore = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readingStore = new ConcurrentHashMap<>();

    static {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-102", "Computer Lab", 30);
        roomStore.put(r1.getId(), r1);
        roomStore.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 412.0, "LAB-102");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LIB-301");
        sensorStore.put(s1.getId(), s1);
        sensorStore.put(s2.getId(), s2);
        sensorStore.put(s3.getId(), s3);

        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s3.getId());
        r2.getSensorIds().add(s2.getId());
    }

    public static Map<String, Room> getAllRooms() { return roomStore; }

    public static Room findRoom(String id) { return roomStore.get(id); }

    public static void saveRoom(Room room) { roomStore.put(room.getId(), room); }

    public static boolean removeRoom(String id) { return roomStore.remove(id) != null; }

    public static Map<String, Sensor> getAllSensors() { return sensorStore; }

    public static Sensor findSensor(String id) { return sensorStore.get(id); }

    public static void saveSensor(Sensor sensor) { sensorStore.put(sensor.getId(), sensor); }

    public static boolean removeSensor(String id) { return sensorStore.remove(id) != null; }

    public static List<SensorReading> getReadingsBySensor(String sensorId) {
        return readingStore.getOrDefault(sensorId, new ArrayList<>());
    }

    public static void saveReading(String sensorId, SensorReading reading) {
        readingStore.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
