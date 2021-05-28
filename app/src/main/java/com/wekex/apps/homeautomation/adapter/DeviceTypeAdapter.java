package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.DeviceType;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceTypeAdapter extends RecyclerView.Adapter<DeviceTypeAdapter.ViewHolder> {


    ArrayList<DeviceType> _list;
    Context context;
    String data;
    RoomOperation _interface;

    public DeviceTypeAdapter(ArrayList<DeviceType> _list, Context context, RoomOperation _interface) {
        this._list = _list;
        this.context = context;
        this._interface = _interface;
        Log.e("TAGGG", "Data " + data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_type_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_type.setText(_list.get(position).getName());
        holder.type_icon.setImageResource(_list.get(position).get_icon());
        if (_list.get(position).isSelected()) {
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.warmwhite));
        } else {
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.gray600));
        }


    }

    @Override
    public int getItemCount() {
        return (_list.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;
        ImageView type_icon;
        LinearLayout ll_main;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.deviceTypeId);
            type_icon = itemView.findViewById(R.id.type_icon);
            ll_main = itemView.findViewById(R.id.ll_main);

            ll_main.setOnClickListener(v -> {
                _interface.selectType(_list.get(getAdapterPosition()).getID());
                notifyItem(getAdapterPosition());
            });
        }
    }

    void notifyItem(int pos) {
        for (int i = 0; i < _list.size(); i++) {
            _list.get(i).setSelected(false);
        }
        _list.get(pos).setSelected(true);
        notifyItemChanged(pos);
        notifyDataSetChanged();
    }
}