package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wekex.apps.homeautomation.Activity.RemoteCatList;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.device_model;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder> {
    ArrayList<device_model> _lst_device_list;
    Context context;
    String _dno = "";
    View.OnClickListener _listener;

    public DeviceListAdapter(Context _context, ArrayList<device_model> _lst_device_list, String _dno, View.OnClickListener _listener) {
        context = _context;
        this._lst_device_list = _lst_device_list;
        this._dno = _dno;
        this._listener = _listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_layout_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_device_name.setText(_lst_device_list.get(position).getDevice_name());
        if (_lst_device_list.get(position).isTypeCustome()) {
            File imgFile = new File(_lst_device_list.get(position).getIconpath());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.iv_device_icon.setImageBitmap(myBitmap);
            }
        } else {
            holder.iv_device_icon.setImageResource(_lst_device_list.get(position).getDevice_drawble());
        }
    }

    @Override
    public int getItemCount() {
        return _lst_device_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_device_name;
        ImageView iv_device_icon;
        CardView _card_device;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tv_device_name = view.findViewById(R.id.tv_device_name);
            iv_device_icon = view.findViewById(R.id.iv_device_icon);
            _card_device = view.findViewById(R.id.card);
            _card_device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, _lst_device_list.get(getAdapterPosition()).getDevice_name() + "", Toast.LENGTH_SHORT).show();
                    v.setTag(_lst_device_list.get(getAdapterPosition()).getDevice_name());
                    _listener.onClick(v);
                }
            });
        }
    }

}
