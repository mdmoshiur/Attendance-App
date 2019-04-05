package com.example.attendance;

public class Course_card {
    private String course_name, series_dept, roll;

    public Course_card(String c_name, String ser_dept, String r) {
        course_name = c_name;
        series_dept = ser_dept;
        roll = r;
    }

    public String get_course_name() {
        return course_name;
    }

    public String get_series_dept() {
        return series_dept;
    }

    public String get_roll() {
        return roll;
    }
}
