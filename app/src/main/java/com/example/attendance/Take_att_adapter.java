package com.example.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class Take_att_adapter extends ArrayAdapter<Take_att_data_node> {
    private int layout_res;
    private Context context;
    public Take_att_adapter(@NonNull Context context, int resource, @NonNull List<Take_att_data_node> objects) {
        super(context, resource, objects);
        this.context=  context;
        this.layout_res = resource;
    }

    private final class ViewHolder {
        TextView roll;
        TextView p_att;
        CheckBox checkBox;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout_res, null);
            viewHolder = new ViewHolder();
            viewHolder.roll = convertView.findViewById(R.id.sample_roll_id);
            viewHolder.p_att = convertView.findViewById(R.id.sample_pofattendance_id);
            viewHolder.checkBox = convertView.findViewById(R.id.checkbox_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.checkBox.setOnCheckedChangeListener(null);
        }

        final Take_att_data_node current_node = getItem(position);
        viewHolder.roll.setText(current_node.getRoll());
        viewHolder.p_att.setText(current_node.getPofa());
        viewHolder.checkBox.setChecked(current_node.getCheckValue());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    current_node.setCheckValue(1);
                } else {
                    current_node.setCheckValue(0);
                }
            }
        });

        return convertView;
    }
}
