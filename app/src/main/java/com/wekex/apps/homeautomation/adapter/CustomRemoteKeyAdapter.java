package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.DeviceType;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRemoteKeyAdapter extends RecyclerView.Adapter<CustomRemoteKeyAdapter.ViewHolder> {


    ArrayList<Model_UserRemoteList.keys> _list;
    Context context;
    String data;
    RoomOperation _interface;
    View.OnClickListener _listeners;

    public CustomRemoteKeyAdapter(ArrayList<Model_UserRemoteList.keys> _list, Context context, RoomOperation _interface, View.OnClickListener _listeners) {
        this._list = _list;
        this.context = context;
        this._interface = _interface;
        this._listeners = _listeners;
        Log.e("TAGGG", "Data " + data);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_remote_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_type.setText(_list.get(position).getKey());
        if (_list.get(position).getValue().isEmpty()) {
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.gray600));
        } else {
            holder.tv_type.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return (_list.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_type = (TextView) itemView.findViewById(R.id.tv_key_name);

            tv_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    _interface.selectType(_list.get(getAdapterPosition()).getID());3
                    v.setTag(_list.get(getAdapterPosition()));
                    _listeners.onClick(v);
                }
            });
        }
    }
}