package com.example.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Student_adapter extends ArrayAdapter<DataUser> {
    private List<DataUser> dataUserList = new ArrayList<>(0);
    private int layout_resource;
    private Context context;

    public Student_adapter(@NonNull Context context, int resource, @NonNull List<DataUser> objects) {
        super(context, resource, objects);
        layout_resource = resource;
        this.context = context;
        this.dataUserList.addAll(objects);
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

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DataUser> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataUserList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (DataUser item : dataUserList) {
                    if (item.getRoll().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
