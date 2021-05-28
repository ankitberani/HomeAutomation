package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.wekex.apps.homeautomation.HomeActivity;
import com.wekex.apps.homeautomation.Interfaces.SchedulerOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.schedule_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.wekex.apps.homeautomation.utils.Utils.HEADING;
import static com.wekex.apps.homeautomation.utils.Utils.ICON;
import static com.wekex.apps.homeautomation.utils.Utils.SCENEID;
import static com.wekex.apps.homeautomation.utils.Utils.SUBHEADING;


/**
 * Created by Nirmal on 22/04/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private schedule_model _schedules;
    private Context context;
    ArrayList<schedule_model.schedule> _list;

    public rgb_color_interface _objInterface;
    public SchedulerOperation _obj_other_type;

    public ScheduleAdapter(Context contxt, schedule_model list, rgb_color_interface _objInterface, SchedulerOperation _obj_other_type) {
        context = contxt;
        _schedules = list;
        _list = _schedules.get_lst_schedule();
        this._objInterface = _objInterface;
        this._obj_other_type = _obj_other_type;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        schedule_model.schedule _object = _list.get(position);
        try {

            if (_object != null) {
                holder.tv_time.setText(_object.getTime_H() + " : " + _object.getTime_M());
                holder.tv_date.setText(_object.getDate());

                holder.tv_on_off.setText(_object.isState() ? "On" : "Off");
                holder.switch_on_off.setChecked(_object.isState() ? true : false);

                holder.tv_days.setText(getDays(_object.getDays()));
                holder.iv_watch_icon.setImageResource(_object.isState() ? R.drawable.watch_icon_big : R.drawable.watch_icon_big_off);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //handling item click event

    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_on_off, tv_time, tv_days, tv_date;
        ImageView iv_edit, iv_delete, iv_watch_icon;
        SwitchCompat switch_on_off;

        LinearLayout parent;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_on_off = (TextView) itemView.findViewById(R.id.tv_on_off);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_days = (TextView) itemView.findViewById(R.id.tv_days);

            iv_watch_icon = (ImageView) itemView.findViewById(R.id.iv_watch_icon);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            switch_on_off = (SwitchCompat) itemView.findViewById(R.id.switch_1);

            iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_objInterface != null)
                        _objInterface.editSchedule(getAdapterPosition());
                    else if (_obj_other_type != null)
                        _obj_other_type.editSchedule(getAdapterPosition());
                }
            });
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_objInterface != null)
                        _objInterface.deleteSchedule(getAdapterPosition());
                    else if (_obj_other_type != null)
                        _obj_other_type.deleteSchedule(getAdapterPosition());
                }
            });

            switch_on_off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_objInterface != null)
                        _objInterface.updateSchedule(getAdapterPosition());
                    else if (_obj_other_type != null)
                        _obj_other_type.updateSchedule(getAdapterPosition());
                }
            });
        }
    }

    public String getDays(String days) {
        String dayInWord = "";
        if (days.contains("1")) {
            if (days.length() == 7) {
                StringBuilder _builder = new StringBuilder();
                if (days.charAt(0) == '1')
                    _builder.append("Mon, ");
                if (days.charAt(1) == '1')
                    _builder.append("Tue, ");
                if (days.charAt(2) == '1')
                    _builder.append("wed, ");
                if (days.charAt(3) == '1')
                    _builder.append("Thur, ");
                if (days.charAt(4) == '1')
                    _builder.append("Fri, ");
                if (days.charAt(5) == '1')
                    _builder.append("Sat, ");
                if (days.charAt(6) == '1')
                    _builder.append("Sun");

                dayInWord = _builder.toString();
            }

        } else {
            dayInWord = "Once";
        }
        return dayInWord;
    }

}
