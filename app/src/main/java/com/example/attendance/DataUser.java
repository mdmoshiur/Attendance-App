package com.example.attendance;

public class DataUser {
    private String roll, marks, pofattendance, recent;

    public DataUser(String roll, String marks, String pofattendance, String recent) {
        this.roll = roll;
        this.marks = marks;
        this.pofattendance = pofattendance;
        this.recent = recent;
    }

    public String getRoll() {
        return roll;
    }

    public String getMarks() {
        return marks;
    }

    public String getPofattendance() {
        return pofattendance;
    }

    public String getRecent() {
        return recent;
    }
}
