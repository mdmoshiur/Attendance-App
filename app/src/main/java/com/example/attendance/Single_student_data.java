package com.example.attendance;

public class Single_student_data {
    private String cycle_day;
    private String date;
    private String presence;

    public Single_student_data(String cycle_day, String date, String presence) {
        this.cycle_day = cycle_day;
        this.date = date;
        this.presence = presence;
    }

    public String getCycle_day() {
        return cycle_day;
    }

    public String getDate() {
        return date;
    }

    public String getPresence() {
        return presence;
    }
}
