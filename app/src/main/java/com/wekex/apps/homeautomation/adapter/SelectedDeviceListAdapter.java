package com.wekex.apps.homeautomation.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wekex.apps.homeautomation.Bubble_seekbar.BubbleSeekBar;
import com.wekex.apps.homeautomation.Graphview;
import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.RoomModel;
import com.wekex.apps.homeautomation.model.data;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;
import com.wekex.apps.homeautomation.utils.SelectableDeviceList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eo.view.batterymeter.BatteryMeterView;

public class SelectedDeviceListAdapter extends RecyclerView.Adapter<SelectedDeviceListAdapter.ViewHolder> {

    AllDataResponseModel allData;
    Context context;
    RoomOperation objInterface;

    public SelectedDeviceListAdapter(AllDataResponseModel allData, Context context, RoomOperation objInterface) {
        this.allData = allData;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        data obj = allData.getObjData().get(position);
        holder.device_icon_d1.setColorFilter(null);
        if (obj.getDtype() == 2) {
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj != null && obj.getObjd1() != null && obj.getObjd1().getName() != null) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            }

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.view_rgb_clr.setVisibility(View.VISIBLE);
            int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
            holder.view_rgb_clr.setBackgroundColor(color);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);

            holder.switch_d1.setVisibility(View.VISIBLE);
            holder.ll_type_16.setVisibility(View.GONE);

        } else if (obj.getDtype() == 8) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            Log.e("TAG", "RoomDeviceAdapter Type 8 Br " + obj.getObjd1().getBr() + " Name " + obj.getObjd1().getName());
            holder.tv_brightness.setVisibility(View.VISIBLE);
            holder.tv_brightness.setText(context.getResources().getString(R.string.brightness));
            holder._seekbar_brightness.setVisibility(View.VISIBLE);

            holder._seekbar_brightness.setProgress((float) (obj.getObjd1().getBr() * 100));
            if (obj != null && obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            } else {
                holder.tv_device_1.setText(obj.getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            }
            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                    holder.device_icon_d1.setColorFilter(R.color.gray600);
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            }
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 15) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            Log.e("TAG", "RoomDeviceAdapter Type 15 Br " + obj.getObjd1().getBr() + " Name " + obj.getObjd1().getName());
            holder.tv_brightness.setVisibility(View.VISIBLE);
            holder.tv_brightness.setText(context.getResources().getString(R.string.fan_speed));
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.VISIBLE);

            int speed = 25;
            double _br = (obj.getObjd1().getBr());
            if (_br <= 0.25) {
                speed = 25;
                holder._seekbar_brightness15.setProgress(1);
            } else if (_br > 0.25 && _br <= 0.50) {
                holder._seekbar_brightness15.setProgress(2);
                speed = 50;
            } else if (_br > 0.50 && _br <= 0.75) {
                holder._seekbar_brightness15.setProgress(3);
                speed = 75;
            } else {
                holder._seekbar_brightness15.setProgress(4);
                speed = 100;
            }
            holder.tv_brightness.setText(context.getResources().getString(R.string.fan_speed) + " " + speed);

            if (obj != null && obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            } else {
                holder.tv_device_1.setText("");
                holder.tv_deviceType_1.setText("");
            }
            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                    holder.device_icon_d1.setColorFilter(R.color.gray600);
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            }

            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 6) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d2.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d3.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d4.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d2.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d3.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d4.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));


                holder.tv_brightness.setVisibility(View.GONE);
                holder._seekbar_brightness.setVisibility(View.GONE);
                holder._seekbar_brightness15.setVisibility(View.GONE);
                if (obj.getObjd1() != null) {
                    holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                    holder.cardView_d1.setVisibility(View.VISIBLE);
                    holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                    holder.tv_deviceType_1.setText(obj.getD_typeName());
                    holder.switch_d1.setChecked(obj.getObjd1().isState());

                } else {
                    holder.switch_d1.setChecked(false);
                    holder.cardView_d1.setVisibility(View.GONE);
                }

                if (obj.getObjd2() != null) {
                    holder.device_icon_d2.setImageResource(R.drawable.bulb_type_6);
                    holder.cardView_d2.setVisibility(View.VISIBLE);
                    holder.tv_device_2.setText(obj.getObjd2() != null ? obj.getObjd2().getName() : "N/A");
                    holder.tv_deviceType_2.setText(obj.getD_typeName());
                    holder.switch_d2.setChecked(obj.getObjd2().isState());
                } else
                    holder.cardView_d2.setVisibility(View.GONE);

                if (obj.getObjd3() != null) {
                    holder.device_icon_d3.setImageResource(R.drawable.bulb_type_6);
                    holder.cardView_d3.setVisibility(View.VISIBLE);
                    holder.tv_device_3.setText(obj.getObjd3() != null ? obj.getObjd3().getName() : "N/A");
                    holder.tv_deviceType_3.setText(obj.getD_typeName());
                    holder.switch_d3.setChecked(obj.getObjd3().isState());
                } else
                    holder.cardView_d3.setVisibility(View.GONE);

                if (obj.getObjd4() != null) {
                    holder.device_icon_d4.setImageResource(R.drawable.bulb_type_6);
                    holder.cardView_d4.setVisibility(View.VISIBLE);
                    holder.tv_device_4.setText(obj.getObjd4() != null ? obj.getObjd4().getName() : "N/A");
                    holder.tv_deviceType_4.setText(obj.getD_typeName());
                    holder.switch_d4.setChecked(obj.getObjd4().isState());
                } else
                    holder.cardView_d4.setVisibility(View.GONE);

                if (obj != null) {
                    holder.device_icon_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                    holder.device_icon_d2.setImageResource((obj.getObjd2() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                    holder.device_icon_d3.setImageResource((obj.getObjd3() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                    holder.device_icon_d4.setImageResource((obj.getObjd4() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);

                    holder.iv_signal_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                    holder.iv_signal_d2.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                    holder.iv_signal_d3.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                    holder.iv_signal_d4.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                }
                holder.ll_type_16.setVisibility(View.GONE);

            }
        } else if (obj.getDtype() == 20) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d2.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d3.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d4.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d2.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d3.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d4.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            }

            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            if (obj.getObjd1() != null) {
                holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                holder.tv_deviceType_1.setText(obj.getD_typeName());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.switch_d1.setChecked(false);
                holder.cardView_d1.setVisibility(View.GONE);
            }

            if (obj.getObjd2() != null) {
                holder.device_icon_d2.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d2.setVisibility(View.VISIBLE);
                holder.tv_device_2.setText(obj.getObjd2() != null ? obj.getObjd2().getName() : "N/A");
                holder.tv_deviceType_2.setText(obj.getD_typeName());
                holder.switch_d2.setChecked(obj.getObjd2().isState());
            } else
                holder.cardView_d2.setVisibility(View.GONE);

            if (obj.getObjd3() != null) {
                holder.device_icon_d3.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d3.setVisibility(View.VISIBLE);
                holder.tv_device_3.setText(obj.getObjd3() != null ? obj.getObjd3().getName() : "N/A");
                holder.tv_deviceType_3.setText(obj.getD_typeName());
                holder.switch_d3.setChecked(obj.getObjd3().isState());
            } else
                holder.cardView_d3.setVisibility(View.GONE);

            if (obj.getObjd4() != null) {
                holder.device_icon_d4.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d4.setVisibility(View.VISIBLE);
                holder.tv_device_4.setText(obj.getObjd4() != null ? obj.getObjd4().getName() : "N/A");
                holder.tv_deviceType_4.setText(obj.getD_typeName());
                holder.switch_d4.setChecked(obj.getObjd4().isState());
            } else
                holder.cardView_d4.setVisibility(View.GONE);

            if (obj != null) {
                holder.device_icon_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                holder.device_icon_d2.setImageResource((obj.getObjd2() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                holder.device_icon_d3.setImageResource((obj.getObjd3() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                holder.device_icon_d4.setImageResource((obj.getObjd4() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);

                holder.iv_signal_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                holder.iv_signal_d2.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                holder.iv_signal_d3.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
                holder.iv_signal_d4.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.online_wifi : R.drawable.offline_wifi);
            }
            holder.ll_type_16.setVisibility(View.GONE);

        } else if (obj.getDtype() == 5) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d2.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d2.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            }

            if (obj.getObjd1() != null) {
                holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                holder.tv_deviceType_1.setText(obj.getD_typeName());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else
                holder.cardView_d1.setVisibility(View.GONE);

            if (obj.getObjd2() != null) {
                holder.device_icon_d2.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d2.setVisibility(View.VISIBLE);
                holder.tv_device_2.setText(obj.getObjd2() != null ? obj.getObjd2().getName() : "N/A");
                holder.tv_deviceType_2.setText(obj.getD_typeName());
                holder.switch_d2.setChecked(obj.getObjd2().isState());
            } else
                holder.cardView_d2.setVisibility(View.GONE);

            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            if (obj != null) {
                holder.device_icon_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                holder.device_icon_d2.setImageResource((obj.getObjd2() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
            }


            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 19) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            if (obj.isOnline()) {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.iv_signal_d2.setImageResource(obj.getSignalDrawable());
            } else {
                holder.iv_signal_d2.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            }

            if (obj.getObjd1() != null) {
                holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                holder.tv_deviceType_1.setText(obj.getD_typeName());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else
                holder.cardView_d1.setVisibility(View.GONE);

            if (obj.getObjd2() != null) {
                holder.device_icon_d2.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d2.setVisibility(View.VISIBLE);
                holder.tv_device_2.setText(obj.getObjd2() != null ? obj.getObjd2().getName() : "N/A");
                holder.tv_deviceType_2.setText(obj.getD_typeName());
                holder.switch_d2.setChecked(obj.getObjd2().isState());
            } else
                holder.cardView_d2.setVisibility(View.GONE);

            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            if (obj != null) {
                holder.device_icon_d1.setImageResource((obj.getObjd1() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
                holder.device_icon_d2.setImageResource((obj.getObjd2() != null && obj.isOnline()) ? R.drawable.bulb_type_6 : R.drawable.device_offline_icon);
            }

            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 9) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_device_1.setText(obj.getObjd1().getName());
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
        } else if (obj.getDtype() == 18) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_device_1.setText(obj.getObjd1().getName());
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);

            holder.ll_type_16.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
        } else if (obj.getDtype() == 14) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else {
                holder.tv_device_1.setText("");
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);

        } else if (obj.getDtype() == 1) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);

            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else {
                holder.tv_device_1.setText("");
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                if (obj.getDno().equals(obj.getdNum())) {
                    Log.wtf("MATCH", "CONDITION_TRUE");
                    Log.wtf("POWER_DATA", obj.getDno() + " = " + obj.getdNum() + " > " + obj.getPowerPuff());

                } else {
                    Log.wtf("MATCH", obj.getDno() + " = " + obj.getdNum() + " > " + obj.getPowerPuff());
                    //holder.tv_power.setVisibility(View.GONE);
                }

                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                Log.wtf("DRAWABLE_URL", obj.getDrawable() + "");
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }


                holder.switch_d1.setVisibility(View.VISIBLE);
                holder._seekbar_brightness.setVisibility(View.GONE);
                holder._seekbar_brightness15.setVisibility(View.GONE);
                holder.tv_brightness.setVisibility(View.GONE);

                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.cardView_d2.setVisibility(View.GONE);
                holder.cardView_d3.setVisibility(View.GONE);
                holder.cardView_d4.setVisibility(View.GONE);
                holder.ll_type_16.setVisibility(View.GONE);
            }
        } else if (obj.getDtype() == 10) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.getName() != null) {
                holder.tv_device_1.setText(obj.getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            }

            if (obj.isEnable()) {
                holder.cardView_d1.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.cardView_d1.setBackgroundColor(context.getResources().getColor(R.color.window));
            }

            if (!obj.isOnline()) {
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
            } else {
                holder.device_icon_d1.setImageResource(R.drawable.d_10_icon);
            }
            holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 11) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else if (!obj.getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getName());
            } else {
                holder.tv_device_1.setText("");
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());

                if (obj.getObjd1().isDoor())
                    holder.device_icon_d1.setImageResource(R.drawable.open_door);
                else
                    holder.device_icon_d1.setImageResource(R.drawable.door_closed);
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
            }

            holder.switch_d1.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.VISIBLE);


            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 16) {
            holder.ll_type_16.setVisibility(View.VISIBLE);
            holder.view_rgb_clr.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else if (!obj.getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getName());
            } else {
                holder.tv_device_1.setText("");
            }

            if (obj.getObjd1() != null) {
                holder.tv_value_t.setText(obj.getObjd1().getValue_t());
                holder.tv_value_h.setText(obj.getObjd1().getValue_h());
                holder.tv_value_hi.setText(obj.getObjd1().getValue_hi());
                holder.tv_value_v.setText(obj.getObjd1().getValue_v());
            } else {
                holder.tv_value_t.setText("-");
                holder.tv_value_h.setText("-");
                holder.tv_value_hi.setText("-");
                holder.tv_value_v.setText("-");
            }

            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());

                if (obj.getObjd1().isDoor())
                    holder.device_icon_d1.setImageResource(R.drawable.open_door);
                else
                    holder.device_icon_d1.setImageResource(R.drawable.door_closed);
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }

            holder.switch_d1.setVisibility(View.VISIBLE);


            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

        } else if (obj.getDtype() == 17) {
            holder.view_rgb_clr.setVisibility(View.GONE);
            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else if (!obj.getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getName());
            } else {
                holder.tv_device_1.setText("");
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(obj.getDrawable());

                if (obj.getObjd1().getMotion().equals("true"))
                    holder.device_icon_d1.setImageResource(R.drawable.open_door);
                else
                    holder.device_icon_d1.setImageResource(R.drawable.door_closed);
                int color = Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
            }

            holder.switch_d1.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.VISIBLE);


            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            holder.ll_type_16.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return allData.getObjData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //        ImageView iv_tower_icon;
        LinearLayout ll_parent;

        CardView cardView_d1, cardView_d2, cardView_d3, cardView_d4;

        TextView tv_device_1, tv_device_2, tv_device_3, tv_device_4;
        TextView tv_deviceType_1, tv_deviceType_2, tv_deviceType_3, tv_deviceType_4;
        Switch switch_d1, switch_d2, switch_d3, switch_d4;
        BatteryMeterView battery_indicator;

        View view_rgb_clr;

        ImageView device_icon_d1, device_icon_d2, device_icon_d3, device_icon_d4, iv_delete_device;

        ImageView iv_signal_d1, iv_signal_d2, iv_signal_d3, iv_signal_d4;


        LinearLayout ll_brightness;
        TextView tv_brightness;
        BubbleSeekBar _seekbar_brightness, _seekbar_brightness15;


        LinearLayout ll_switch_1, ll_switch_2, ll_switch_3, ll_switch_4;

        CardView ll_type_16;

        TextView tv_value_t, tv_value_h, tv_value_hi, tv_value_v;

        public ViewHolder(View itemView) {
            super(itemView);

            view_rgb_clr = itemView.findViewById(R.id.view_rgb_clr);
            ll_parent = itemView.findViewById(R.id.parent);
            ll_brightness = itemView.findViewById(R.id.ll_brightness);

            ll_type_16 = itemView.findViewById(R.id.card_type_16);

            tv_brightness = itemView.findViewById(R.id.tv_brightness);
            _seekbar_brightness = itemView.findViewById(R.id.seekbar_type8);
            _seekbar_brightness15 = itemView.findViewById(R.id.seekbar_type15);
            _seekbar_brightness.setProgress(10);
            _seekbar_brightness.setMin(10);
            tv_brightness.setText(context.getResources().getString(R.string.brightness) + " 10%");

            cardView_d1 = itemView.findViewById(R.id.card_view_d1);
            cardView_d2 = itemView.findViewById(R.id.card_view_d2);
            cardView_d3 = itemView.findViewById(R.id.card_view_d3);
            cardView_d4 = itemView.findViewById(R.id.card_view_d4);


            tv_value_t = itemView.findViewById(R.id.tv_t_value);
            tv_value_h = itemView.findViewById(R.id.tv_h_value);
            tv_value_hi = itemView.findViewById(R.id.tv_hi_value);
            tv_value_v = itemView.findViewById(R.id.tv_v_value);

            ll_switch_1 = itemView.findViewById(R.id.ll_switch_1);
            ll_switch_2 = itemView.findViewById(R.id.ll_switch_2);
            ll_switch_3 = itemView.findViewById(R.id.ll_switch_3);
            ll_switch_4 = itemView.findViewById(R.id.ll_switch_4);


            ll_switch_1.setOnClickListener(this);
            ll_switch_2.setOnClickListener(this);
            ll_switch_3.setOnClickListener(this);
            ll_switch_4.setOnClickListener(this);

            battery_indicator = itemView.findViewById(R.id.battery_indicator);

            ll_parent.setOnClickListener(v -> {
                objInterface.click_device(getAdapterPosition(), allData.getObjData().get(getAdapterPosition()).getDtype());
            });

            tv_device_1 = itemView.findViewById(R.id.devicename_d1);
            tv_device_2 = itemView.findViewById(R.id.devicename_d2);
            tv_device_3 = itemView.findViewById(R.id.devicename_d3);
            tv_device_4 = itemView.findViewById(R.id.devicename_d4);

            tv_deviceType_1 = itemView.findViewById(R.id.devicetypename_d1);
            tv_deviceType_2 = itemView.findViewById(R.id.devicetypename_d2);
            tv_deviceType_3 = itemView.findViewById(R.id.devicetypename_d3);
            tv_deviceType_4 = itemView.findViewById(R.id.devicetypename_d4);

            switch_d1 = itemView.findViewById(R.id.switch1_d1);
            switch_d2 = itemView.findViewById(R.id.switch1_d2);
            switch_d3 = itemView.findViewById(R.id.switch1_d3);
            switch_d4 = itemView.findViewById(R.id.switch1_d4);


            device_icon_d1 = itemView.findViewById(R.id.device_icon_d1);
            device_icon_d2 = itemView.findViewById(R.id.device_icon_d2);
            device_icon_d3 = itemView.findViewById(R.id.device_icon_d3);
            device_icon_d4 = itemView.findViewById(R.id.device_icon_d4);


            iv_signal_d1 = itemView.findViewById(R.id.iv_signal_d1);
            iv_signal_d2 = itemView.findViewById(R.id.iv_signal_d2);
            iv_signal_d3 = itemView.findViewById(R.id.iv_signal_d3);
            iv_signal_d4 = itemView.findViewById(R.id.iv_signal_d4);

            switch_d1.setOnClickListener(this);
            switch_d2.setOnClickListener(this);
            switch_d3.setOnClickListener(this);
            switch_d3.setOnClickListener(this);

            iv_delete_device = itemView.findViewById(R.id.iv_delete_device);
            iv_delete_device.setOnClickListener(this);

            device_icon_d1.setOnClickListener(this);

            _seekbar_brightness.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                    if (progress < 10) {
                        _seekbar_brightness.setProgress(10);
                    }
                    tv_brightness.setText(context.getResources().getString(R.string.brightness) + " " + _seekbar_brightness.getProgress() + "%");
                }

                @Override
                public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
//                    objInterface.publishSeekbar(getAdapterPosition(), progress);
                    allData.getObjData().get(getAdapterPosition()).getObjd1().setBr((double) progress / 100);
                }

                @Override
                public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                }

            });

            _seekbar_brightness15.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

                    Log.e("TAG", "Progrss of type 15 " + progress);
                    if (progress < 1) {
                        _seekbar_brightness.setProgress(1);
                    }
                    int speed = 25;
                    if (progress <= 1) {
                        speed = 25;
                    } else if (progress > 1 && progress <= 2) {
                        speed = 50;
                    } else if (progress > 2 && progress <= 3) {
                        speed = 75;
                    } else {
                        speed = 100;
                    }
                    tv_brightness.setText(context.getResources().getString(R.string.fan_speed) + " " + speed);

                    /* tv_brightness.setText(context.getResources().getString(R.string.fan_speed) + " " + progress);*/
                }

                @Override
                public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                    if (getAdapterPosition() != -1) {
                        double speed = 0.25;
                        if (progress <= 1) {
                            speed = 0.25;
                        } else if (progress > 1 && progress <= 2) {
                            speed = 0.50;
                        } else if (progress > 2 && progress <= 3) {
                            speed = 0.75;
                        } else {
                            speed = 1;
                        }
                        allData.getObjData().get(getAdapterPosition()).getObjd1().setBr(speed);
//                        objInterface.publishSeekbarType16(getAdapterPosition(), speed);
                    }
                }

                @Override
                public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.switch1_d1:
                    if (allData.getObjData().get(getAdapterPosition()).isOnline())
                        ll_parent.performClick();
                    else
                        switch_d1.setChecked(allData.getObjData().get(getAdapterPosition()).isOnline());
                    break;

                case R.id.device_icon_d1: {
                    try {
                        Intent intent = new Intent(context, Graphview.class);
                        intent.putExtra("code", allData.getObjData().get(getAdapterPosition()).getDno());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at OnClick " + e.getMessage());
                    }
                }
                break;
                case R.id.card_view_d1: {
                    if (allData.getObjData().get(getAdapterPosition()).getDtype() == 10) {
//                        Intent intent = new Intent(context, RemoteMenu.class);
//                        intent.putExtra("dno", allData.getObjData().get(getAdapterPosition()).getDno());
//                        intent.putExtra("r_type", "rf");
//                        intent.putExtra("remotes", "rf");
//                        intent.putExtra("mode", "0");// "1" for working remote
//                        context.startActivity(intent);
                        Intent intent = new Intent(context, SelectableDeviceList.class);
                        intent.putExtra("RoomId", "e72f54e0-d052-45dc-963d-c75c1ff9f33c");
                        intent.putExtra("DeviceType", "10");
                        context.startActivity(intent);
                    }
                }
                break;
                case R.id.ll_switch_1: {
                    try {
                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d1", !allData.getObjData().get(getAdapterPosition()).getObjd1().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd1().setState(!allData.getObjData().get(getAdapterPosition()).getObjd1().isState());
                        switch_d1.setChecked(allData.getObjData().get(getAdapterPosition()).getObjd1().isState());
                    } catch (Exception e) {
                        Log.e("TAG", "Exception " + e.toString());
                    }
                    //                    objInterface.TurnOnOffDevice(getAdapterPosition(), "d1", !allData.getObjData().get(getAdapterPosition()).getObjd1().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                   /* TurnOnOffDevice(allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), "d1", switch_d1.isChecked(), allData.getObjData().get(getAdapterPosition()).getDtype());
                    switch_d1.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd1().isState());*/
                }
                break;
                case R.id.ll_switch_2: {
                    try {
//                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d2", !allData.getObjData().get(getAdapterPosition()).getObjd2().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd2().setState(!allData.getObjData().get(getAdapterPosition()).getObjd2().isState());
                        switch_d2.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd2().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case R.id.ll_switch_3: {
                    try {
//                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d3", !allData.getObjData().get(getAdapterPosition()).getObjd3().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd3().setState(!allData.getObjData().get(getAdapterPosition()).getObjd3().isState());
                        switch_d3.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd3().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.ll_switch_4: {
                    try {
//                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d4", !allData.getObjData().get(getAdapterPosition()).getObjd4().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd4().setState(!allData.getObjData().get(getAdapterPosition()).getObjd4().isState());
                        switch_d4.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd4().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.iv_delete_device: {
                    new AlertDialog.Builder(context).setTitle(context.getString(R.string.delete)).setMessage(R.string.are_you_sure).setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            objInterface.DeleteGroup(getAdapterPosition());
                        }
                    }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
                break;
            }
        }
    }

    public AllDataResponseModel getLatestData() {
        return allData;
    }
}
