package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.color_item;
import com.wekex.apps.homeautomation.model.data;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

public class FillDeviceAdapter extends RecyclerView.Adapter<FillDeviceAdapter.ViewHolder> {


    AllDataResponseModel _objectMain;
    Context context;
    boolean isFromCreateScene = false;
    View.OnClickListener _listener;

    public FillDeviceAdapter(AllDataResponseModel _list, Context context, boolean isFromCreateScene, View.OnClickListener _listener) {
        this._objectMain = _list;
        this.context = context;
        this.isFromCreateScene = isFromCreateScene;
        this._listener = _listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            data obj = _objectMain.getObjData().get(position);
            if (obj != null && obj.getObjd1() != null && obj.getObjd1().getName() != null) {
                holder.tv_name.setText(obj.getObjd1().getName());
                Log.e("TAG", " name " + obj.getObjd1().getName() + " status " + obj.isOnline());
            } else
                holder.tv_name.setText("N/A");

            if (obj != null && obj.isOnline()) {
                holder.iv_device_status.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_device_status.setImageResource(R.drawable.device_offline_icon);
            }

            holder.iv_icon.setImageResource(obj.getDrawable());
            if (obj.getDtype() == 2) {
                int color = android.graphics.Color.argb(255, obj.objd1.getR(), obj.objd1.getG(), obj.objd1.getB());
                holder.iv_icon.setColorFilter(color);
            } else {
                holder.iv_icon.setColorFilter(null);
            }

            holder.checkBox.setChecked(obj.isChecked());
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return _objectMain.getObjData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_icon, iv_device_status;
        public TextView tv_name;
        public AppCompatCheckBox checkBox;
        LinearLayout ll_selectDevice;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.cb_1);
            iv_icon = itemView.findViewById(R.id.device_icon);
            tv_name = itemView.findViewById(R.id.devicename);
            ll_selectDevice = itemView.findViewById(R.id.ll_selectDevice);
            iv_device_status = itemView.findViewById(R.id.iv_device_status);

            ll_selectDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFromCreateScene && _listener != null) {
                        v.setTag(getAdapterPosition());
                        _listener.onClick(v);
                    }
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _objectMain.getObjData().get(getAdapterPosition()).setChecked(!_objectMain.getObjData().get(getAdapterPosition()).isChecked());
                }
            });

            if (isFromCreateScene)
                checkBox.setVisibility(View.GONE);
            else
                checkBox.setVisibility(View.VISIBLE);
        }
    }
}
