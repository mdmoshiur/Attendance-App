package com.example.attendance;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Course_adapter extends RecyclerView.Adapter<Course_adapter.Course_view_holder>{

    private ArrayList<Course_data> courses;
    //listener var
    private MyOnClickListener myOnClickListener;

    public Course_adapter(ArrayList<Course_data> passing_courses){
        this.courses = passing_courses;
        //this.myOnClickListener = myOnClickListener;
    }

    public void setMyOnClickListener(MyOnClickListener myOnClickListener){
        this.myOnClickListener = myOnClickListener;
    }

    @Override
    public Course_view_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        Course_view_holder cvh = new Course_view_holder(v, myOnClickListener);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull Course_view_holder course_view_holder, int i) {
        Course_data current_course = courses.get(i);

        course_view_holder.course_name.setText(current_course.get_course_name());
        course_view_holder.series_dept.setText(current_course.get_series_dept());
        course_view_holder.roll.setText(current_course.get_roll());

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class Course_view_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView course_name;
        public TextView series_dept;
        public TextView roll;

        //for click event
        MyOnClickListener myOnClickListener;

        public Course_view_holder(View itemView, MyOnClickListener myOnClickListener) {
            super(itemView);

            course_name = itemView.findViewById(R.id.course_name_id);
            series_dept = itemView.findViewById(R.id.series_section_id);
            roll = itemView.findViewById(R.id.section_roll_id);

            //click event
            this.myOnClickListener = myOnClickListener;
            //set listener
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myOnClickListener.onClick(v, getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            myOnClickListener.onLongClick(v, getAdapterPosition());
            return true;
        }

    }
    //start for onclicklistener
    public interface MyOnClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
