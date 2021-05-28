package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.color_item;
import com.wekex.apps.homeautomation.model.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAllLogsAdapter extends RecyclerView.Adapter<ViewAllLogsAdapter.ViewHolder> {


    ArrayList<data> _list;
    Context context;
    rgb_color_interface objInterface;

    public ViewAllLogsAdapter(ArrayList<data> _list, Context context) {
        this._list = _list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logs_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        data object = _list.get(position);

        if (object != null) {
            Log.e("TAG", "Time " + object.getTime() + " isOnline " + object.isOnline());
            holder.tv_date_time.setText(object.getTime() + "");
            holder.tv_status.setText(object.isOnline() ? "on" : "off");
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date_time, tv_status;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_date_time = itemView.findViewById(R.id.tv_time);
            tv_status = itemView.findViewById(R.id.tv_status);
        }
    }

    private String getDateTimeToHexa(long time) {
        Calendar mCalendar = Calendar.getInstance();
        TimeZone gmtTime =  TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName());
        mCalendar.setTimeZone(gmtTime);
        final Date date = mCalendar.getTime();
        return Long.toHexString(time/1000);
    }
}
