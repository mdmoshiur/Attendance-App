package com.example.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Student_adapter extends ArrayAdapter<DataUser> {

    private int layout_resource;
    private Context context;

    public Student_adapter(@NonNull Context context, int resource, @NonNull List<DataUser> objects) {
        super(context, resource, objects);
        layout_resource = resource;
        this.context = context;
    }

    private final class ViewHolder {
        TextView roll;
        TextView marks;
        TextView pofattenance;
        TextView recent;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout_resource, null);
            viewHolder = new ViewHolder();
            viewHolder.roll = convertView.findViewById(R.id.roll_id);
            viewHolder.marks = convertView.findViewById(R.id.marks_id);
            viewHolder.pofattenance = convertView.findViewById(R.id.pofattendance_id);
            viewHolder.recent = convertView.findViewById(R.id.recent_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        DataUser current = getItem(position);
        viewHolder.roll.setText(current.getRoll());
        viewHolder.marks.setText(current.getMarks());
        viewHolder.pofattenance.setText(current.getPofattendance());
        viewHolder.recent.setText(current.getRecent());

        return convertView;

    }
}
