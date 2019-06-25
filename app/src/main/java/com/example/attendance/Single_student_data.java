package com.example.attendance;

public class Single_student_data {
    private String cycle_day;
    private String date;
    private String presence;
    private String col_name;

    public Single_student_data(String cycle_day, String date, String presence, String col_name) {
        this.cycle_day = cycle_day;
        this.date = date;
        this.presence = presence;
        this.col_name = col_name;
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

    public String getCol_name(){
        return this.col_name;
    }

    public void setPresence(String p){
        this.presence = p;
    }
}
