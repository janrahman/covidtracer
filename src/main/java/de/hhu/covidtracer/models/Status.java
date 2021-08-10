package de.hhu.covidtracer.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Status {
    STAFF(1, "Staff"),
    PATIENT(2, "Patient"),
    VISITOR(3, "Visitor");

    private final int id;
    private final String name;
    private static final Map<Integer, Status> ENUM_STATUS;

    Status(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }


    static {
        Map<Integer, Status> map = new HashMap<>();

        for (Status instance : Status.values()) {
            map.put(instance.getId(), instance);
        }

        ENUM_STATUS = Collections.unmodifiableMap(map);
    }


    public static Status getStatus(int id) {
        return ENUM_STATUS.get(id);
    }
}
