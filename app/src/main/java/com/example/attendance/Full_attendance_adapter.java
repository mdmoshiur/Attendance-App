package com.example.attendance;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Full_attendance_adapter extends ArrayAdapter<Single_student_data> {

    //private List<Single_student_data> student_data = new ArrayList<>(0);
    private int layout_res;
    private Context context;

    public Full_attendance_adapter(@NonNull Context context, int resource, @NonNull List<Single_student_data> objects) {
        super(context, resource, objects);
        this.layout_res = resource;
        this.context = context;
    }

    private final class ViewHolder {
        TextView cycle_day_view;
        TextView date_view;
        TextView presence_view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout_res, null);
            viewHolder = new ViewHolder();
            viewHolder.cycle_day_view = convertView.findViewById(R.id.full_cycle_day_id);
            viewHolder.date_view = convertView.findViewById(R.id.full_date_id);
            viewHolder.presence_view = convertView.findViewById(R.id.full_presence_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Single_student_data current_data = getItem(position);
        viewHolder.cycle_day_view.setText(current_data.getCycle_day());
        String date = current_data.getDate().replaceAll("_","/");
        viewHolder.date_view.setText(date);
        viewHolder.presence_view.setText(current_data.getPresence());
        if(current_data.getPresence().equals("Present")){
            viewHolder.presence_view.setTextColor(Color.GREEN);
        } else {
            viewHolder.presence_view.setTextColor(Color.RED);
        }
        return convertView;
    }
}
