package com.example.attendance;

public class DataUser {
    private String row_id,roll, marks, pofattendance, recent;

    public DataUser(String id, String roll, String marks, String pofattendance, String recent) {
        this.row_id = id;
        this.roll = roll;
        this.marks = marks;
        this.pofattendance = pofattendance;
        this.recent = recent;
    }

    public String getRowId(){
        return row_id;
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
