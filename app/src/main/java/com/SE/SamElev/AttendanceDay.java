package com.SE.SamElev;

public class AttendanceDay {
    private String name; // Employee name
    private boolean isPresent; // Attendance status

    public AttendanceDay(String name, boolean isPresent) {
        this.name = name;
        this.isPresent = isPresent;
    }

    public String getDate() {
        return name;
    }

    public boolean isPresent() {
        return isPresent;
    }
}
