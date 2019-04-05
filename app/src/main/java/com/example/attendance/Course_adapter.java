package com.example.attendance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Course_adapter extends RecyclerView.Adapter<Course_adapter.Course_view_holder>{

    private ArrayList<Course_card> courses;

    public static class Course_view_holder extends RecyclerView.ViewHolder {

        public TextView course_name;
        public TextView series_dept;
        public TextView roll;

        public Course_view_holder(View itemView) {
            super(itemView);

            course_name = itemView.findViewById(R.id.course_name_id);
            series_dept = itemView.findViewById(R.id.series_section_id);
            roll = itemView.findViewById(R.id.section_roll_id);

        }
    }

    public Course_adapter(ArrayList<Course_card> passing_courses){
        courses = passing_courses;
    }

    @Override
    public Course_view_holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        Course_view_holder cvh = new Course_view_holder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder( Course_view_holder course_view_holder, int i) {
        Course_card current_course = courses.get(i);

        course_view_holder.course_name.setText(current_course.get_course_name());
        course_view_holder.series_dept.setText(current_course.get_series_dept());
        course_view_holder.roll.setText(current_course.get_roll());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
