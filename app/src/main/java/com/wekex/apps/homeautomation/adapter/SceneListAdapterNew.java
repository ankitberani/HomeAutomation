package com.wekex.apps.homeautomation.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.data;

import org.jetbrains.annotations.NotNull;

public class SceneListAdapterNew extends RecyclerView.Adapter<SceneListAdapterNew.myViewHolder> {

    Activity activity;
    AllDataResponseModel _deviceList;
    View.OnClickListener _listener;
    boolean isFromAccesories;

    public SceneListAdapterNew(Activity activity, AllDataResponseModel _deviceList, View.OnClickListener _listener, boolean isFromAccesories) {
        this.activity = activity;
        this._deviceList = _deviceList;
        this._listener = _listener;
        this.isFromAccesories = isFromAccesories;
    }

    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene, parent, false);
        return new SceneListAdapterNew.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myViewHolder holder, int position) {

        data obj = _deviceList.getObjData().get(position);

        if (obj != null && obj.objd1 != null)
            holder.tv_deviceName.setText(obj.objd1.getName());
        else
            holder.tv_deviceName.setText(obj.getName());

        if (obj.isChecked())
            holder.rb_device.setChecked(true);
        else
            holder.rb_device.setChecked(false);

        if (obj.getDrawable() != 0) {
            holder.iv_device_icon.setImageResource(obj.getDrawable());
        }
    }

    @Override
    public int getItemCount() {
        return _deviceList.getObjData().size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tv_deviceName;
        RadioButton rb_device;
        ImageView iv_device_icon;
        LinearLayout ll_main_item;

        public myViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_deviceName = itemView.findViewById(R.id.tv_device_name);
            rb_device = itemView.findViewById(R.id.radiobtn);
            iv_device_icon = itemView.findViewById(R.id.iv_device_icon);
            ll_main_item = itemView.findViewById(R.id.ll_main_item);
            ll_main_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setTag(getAdapterPosition());
                    _listener.onClick(view);
                }
            });
            if (!isFromAccesories)
                rb_device.setVisibility(View.GONE);
        }
    }

}