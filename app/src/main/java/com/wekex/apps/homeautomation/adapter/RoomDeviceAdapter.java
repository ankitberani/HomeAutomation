package com.wekex.apps.homeautomation.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.Bubble_seekbar.BubbleSeekBar;
import com.wekex.apps.homeautomation.Graphview;
import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.RoomModel;
import com.wekex.apps.homeautomation.model.data;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;
import com.wekex.apps.homeautomation.utils.SelectableDeviceList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eo.view.batterymeter.BatteryMeterView;

public class RoomDeviceAdapter extends RecyclerView.Adapter<RoomDeviceAdapter.ViewHolder> {

    AllDataResponseModel allData;
    Context context;
    RoomOperation objInterface;
    Gson gson = new Gson();

    private ArrayList<RoomModel> listItems = new ArrayList<>();
    private SingleAdapter adapter;

    public RoomDeviceAdapter(AllDataResponseModel allData, Context context, RoomOperation objInterface) {
        this.allData = allData;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        data obj = allData.getObjData().get(position);
        holder.device_icon_d1.setColorFilter(null);
        holder.device_icon_d1.setEnabled(false);
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
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
//                Log.e("TAG", "New Valued of RGB In Adapter " + obj.getObjd1().getR() + " " + obj.getObjd1().getB() + " " + obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
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
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());
            holder.switch_d1.setVisibility(View.VISIBLE);
            String date_next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (date_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(date_next_schedule);
            }

            holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);

        } else if (obj.getDtype() == 8) {

           /* holder._seekbar_brightness.setMax(100);
            holder._seekbar_brightness.setMin(10);*/
            holder.battery_indicator.setVisibility(View.GONE);
            Log.e("TAG", "RoomDeviceAdapter Type 8 Br " + obj.getObjd1().getBr() + " Name " + obj.getObjd1().getName());
            holder.tv_brightness.setVisibility(View.VISIBLE);
            holder.tv_brightness.setText(context.getResources().getString(R.string.brightness));
            holder._seekbar_brightness.setVisibility(View.VISIBLE);
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());
            holder._seekbar_brightness.setProgress((float) (obj.getObjd1().getBr() * 100));
            if (obj != null && obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            } else {
                holder.tv_device_1.setText("");
                holder.tv_deviceType_1.setText("");
            }
//            holder._seekbar_brightness.setEnabled(obj.isOnline());
            if (!obj.isOnline()) {
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.device_icon_d1.setImageResource(obj.getDrawable());
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                    holder.device_icon_d1.setColorFilter(R.color.gray600);
//                    holder.switch_d1.setChecked(false);
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

            String date_next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (date_next_schedule.isEmpty())
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(date_next_schedule);
            }

            holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 15) {

           /* holder._seekbar_brightness.setMax(4);
            holder._seekbar_brightness.setMin(1);*/
            holder.battery_indicator.setVisibility(View.GONE);
            Log.e("TAG", "RoomDeviceAdapter Type 15 Br " + obj.getObjd1().getBr() + " Name " + obj.getObjd1().getName());
            holder.tv_brightness.setVisibility(View.VISIBLE);
            holder.tv_brightness.setText(context.getResources().getString(R.string.fan_speed));
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.VISIBLE);
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                    holder.device_icon_d1.setColorFilter(R.color.gray600);
//                    holder.switch_d1.setChecked(false);
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

            String date_next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (date_next_schedule.isEmpty())
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(date_next_schedule);
            }

            holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 6) {

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

            String _next_schedule_d1 = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule_d1.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule_d1);
            }

            String _next_schedule_d2 = Constants.savetoShared(context).getString(obj.getDno() + "_2", "");
            if (_next_schedule_d2.isEmpty()) {
                holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d2.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d2.setText(_next_schedule_d2);
            }

            String _next_schedule_d3 = Constants.savetoShared(context).getString(obj.getDno() + "_3", "");
            if (_next_schedule_d3.isEmpty()) {
                holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d3.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d3.setText(_next_schedule_d3);
            }


            String _next_schedule_d4 = Constants.savetoShared(context).getString(obj.getDno() + "_4", "");
            if (_next_schedule_d4.isEmpty()) {
                holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d4.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d4.setText(_next_schedule_d4);
            }

            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            if (obj.getObjd1() != null) {
                holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                holder.tv_deviceType_1.setText(obj.getD_typeName());
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
//                holder.switch_d1.setChecked(false);
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

            holder.ll_switch_1.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_2.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_3.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_4.setClickable(allData.getObjData().get(position).isOnline());

            holder.switch_d1.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d2.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d3.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d4.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_type_16.setVisibility(View.GONE);

        } else if (obj.getDtype() == 20) {

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

            String _next_schedule_d1 = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule_d1.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule_d1);
            }

            String _next_schedule_d2 = Constants.savetoShared(context).getString(obj.getDno() + "_2", "");
            if (_next_schedule_d2.isEmpty()) {
                holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d2.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d2.setText(_next_schedule_d2);
            }

            String _next_schedule_d3 = Constants.savetoShared(context).getString(obj.getDno() + "_3", "");
            if (_next_schedule_d3.isEmpty()) {
                holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d3.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d3.setText(_next_schedule_d3);
            }


            String _next_schedule_d4 = Constants.savetoShared(context).getString(obj.getDno() + "_4", "");
            if (_next_schedule_d4.isEmpty()) {
                holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d4.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d4.setText(_next_schedule_d4);
            }

            holder.tv_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            if (obj.getObjd1() != null) {
                holder.device_icon_d1.setImageResource(R.drawable.bulb_type_6);
                holder.cardView_d1.setVisibility(View.VISIBLE);
                holder.tv_device_1.setText(obj.getObjd1() != null ? obj.getObjd1().getName() : "N/A");
                holder.tv_deviceType_1.setText(obj.getD_typeName());
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
//                holder.switch_d1.setChecked(false);
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

            holder.ll_switch_1.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_2.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_3.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_4.setClickable(allData.getObjData().get(position).isOnline());

            holder.switch_d1.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d2.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d3.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d4.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_type_16.setVisibility(View.GONE);

        } else if (obj.getDtype() == 5) {

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
            holder.ll_switch_1.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_2.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d1.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d2.setClickable(allData.getObjData().get(position).isOnline());

            String _next_schedule_d1 = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule_d1.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule_d1);
            }

            String _next_schedule_d2 = Constants.savetoShared(context).getString(obj.getDno() + "_2", "");
            if (_next_schedule_d2.isEmpty()) {
                holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d2.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d2.setText(_next_schedule_d2);
            }
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 19) {

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
            holder.ll_switch_1.setClickable(allData.getObjData().get(position).isOnline());
            holder.ll_switch_2.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d1.setClickable(allData.getObjData().get(position).isOnline());
            holder.switch_d2.setClickable(allData.getObjData().get(position).isOnline());

            String _next_schedule_d1 = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule_d1.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule_d1);
            }

            String _next_schedule_d2 = Constants.savetoShared(context).getString(obj.getDno() + "_2", "");
            if (_next_schedule_d2.isEmpty()) {
                holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d2.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d2.setText(_next_schedule_d2);
            }
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 9) {

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
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
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty())
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }

            holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
        } else if (obj.getDtype() == 18) {

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
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
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty())
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }

            holder.tv_next_scheudule_d2.setVisibility(View.GONE);
            holder.tv_next_scheudule_d3.setVisibility(View.GONE);
            holder.tv_next_scheudule_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);
            holder.switch_d1.setVisibility(View.VISIBLE);
        } else if (obj.getDtype() == 14) {

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
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
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());
            holder.switch_d1.setVisibility(View.VISIBLE);


            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }
        } else if (obj.getDtype() == 1) {

            holder.battery_indicator.setVisibility(View.GONE);

            if (obj.getObjd1() != null && obj.getObjd1().getName() != null && !obj.getObjd1().getName().isEmpty()) {
                holder.tv_device_1.setText(obj.getObjd1().getName());
            } else {
                holder.tv_device_1.setText("");
            }
            holder.tv_deviceType_1.setText(obj.getD_typeName());

            if (!obj.isOnline()) {
                holder.tv_power.setVisibility(View.VISIBLE);
                holder.tv_power.setText("Power: " + "00.00 W");
                holder.tv_power.setTextColor(Color.GRAY);
                holder.device_icon_d1.setColorFilter(null);
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
                holder.iv_signal_d1.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                if (obj.getDno().equals(obj.getdNum())) {
                    Log.wtf("MATCH", "CONDITION_TRUE");
                    Log.wtf("POWER_DATA", obj.getDno() + " = " + obj.getdNum() + " > " + obj.getPowerPuff());
                    holder.tv_power.setVisibility(View.VISIBLE);
                    holder.tv_power.setText(obj.getPowerPuff().equals("") ? "Power: 0.00 W" : "Power: " + obj.getPowerPuff() + "0 W");
                    holder.tv_power.setTextColor(Color.GREEN);

                    holder.tv_power.setOnClickListener(v -> {
                        Intent intent = new Intent(context, Graphview.class);
                        intent.putExtra("code", allData.getObjData().get(position).getDno());
                        context.startActivity(intent);
                    });
                } else {
                    Log.wtf("MATCH", obj.getDno() + " = " + obj.getdNum() + " > " + obj.getPowerPuff());
                    //holder.tv_power.setVisibility(View.GONE);
                }

                holder.iv_signal_d1.setImageResource(obj.getSignalDrawable());

                holder.device_icon_d1.setImageResource(2131231176);
                Log.wtf("DRAWABLE_URL", obj.getDrawable() + "");
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }

            holder.device_icon_d1.setEnabled(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());
            holder.switch_d1.setClickable(obj.isOnline());
            holder.switch_d1.setVisibility(View.VISIBLE);

            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);
            holder.ll_type_16.setVisibility(View.GONE);

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }
        } else if (obj.getDtype() == 10) {
            holder.battery_indicator.setVisibility(View.GONE);
            if (obj.getName() != null) {
                holder.tv_device_1.setText(obj.getName());
                holder.tv_deviceType_1.setText(obj.getD_typeName());
            }

            if (obj.isEnable()) {
                holder.cardView_d1.setEnabled(true);
                holder.cardView_d1.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.cardView_d1.setEnabled(false);
                holder.cardView_d1.setBackgroundColor(context.getResources().getColor(R.color.window));
            }

            if (!obj.isOnline()) {
                holder.device_icon_d1.setImageResource(R.drawable.device_offline_icon);
//                holder.switch_d1.setChecked(obj.getObjd1().isState());
            } else {
                holder.device_icon_d1.setImageResource(R.drawable.d_10_icon);
//                holder.device_icon_d1.setImageResource(obj.getDrawable());
//                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
//                holder.device_icon_d1.setColorFilter(color);
//                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
//                } else {
//                    holder.switch_d1.setChecked(false);
//                }
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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
//                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
//                } else {
//                    holder.switch_d1.setChecked(false);
//                }
            }

            holder.switch_d1.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.VISIBLE);
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }
            holder.ll_type_16.setVisibility(View.GONE);
        } else if (obj.getDtype() == 16) {

            holder.ll_type_16.setVisibility(View.VISIBLE);

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
                if (obj.getObjd1() != null) {
                    holder.switch_d1.setChecked(obj.getObjd1().isState());
                } else {
                    holder.switch_d1.setChecked(false);
                }
            }

            holder.switch_d1.setVisibility(View.VISIBLE);
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }

        } else if (obj.getDtype() == 17) {

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
                int color = android.graphics.Color.argb(255, obj.getObjd1().getR(), obj.getObjd1().getG(), obj.getObjd1().getB());
                holder.device_icon_d1.setColorFilter(color);
//                if (obj.getObjd1() != null) {
//                    holder.switch_d1.setChecked(obj.getObjd1().isState());
//                } else {
//                    holder.switch_d1.setChecked(false);
//                }
            }

            holder.switch_d1.setVisibility(View.GONE);
            holder.battery_indicator.setVisibility(View.VISIBLE);
            holder.switch_d1.setClickable(obj.isOnline());
            holder.ll_switch_1.setClickable(obj.isOnline());

            holder._seekbar_brightness.setVisibility(View.GONE);
            holder._seekbar_brightness15.setVisibility(View.GONE);
            holder.tv_brightness.setVisibility(View.GONE);

            holder.cardView_d1.setVisibility(View.VISIBLE);
            holder.cardView_d2.setVisibility(View.GONE);
            holder.cardView_d3.setVisibility(View.GONE);
            holder.cardView_d4.setVisibility(View.GONE);

            String _next_schedule = Constants.savetoShared(context).getString(obj.getDno() + "_1", "");
            if (_next_schedule.isEmpty()) {
                holder.tv_next_scheudule_d1.setVisibility(View.GONE);
            } else {
                holder.tv_next_scheudule_d1.setVisibility(View.VISIBLE);
                holder.tv_next_scheudule_d1.setText(_next_schedule);
            }
            holder.ll_type_16.setVisibility(View.GONE);
        }


        /*holder.ll_parent.setClickable(true);
        if (obj.isOnline()) {
            holder.ll_parent.setClickable(true);
        } else {
            holder.ll_parent.setClickable(false);
        }*/

       /* holder.tv_next_scheudule_d1.setVisibility(View.GONE);
        holder.tv_next_scheudule_d2.setVisibility(View.GONE);
        holder.tv_next_scheudule_d3.setVisibility(View.GONE);
        holder.tv_next_scheudule_d4.setVisibility(View.GONE);*/
        //notifyDataSetChanged();
        //notifyItemChanged(position);
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

        ImageView device_icon_d1, device_icon_d2, device_icon_d3, device_icon_d4;
        ImageView iv_showMenu_d1, iv_showMenu_d2, iv_showMenu_d3, iv_showMenu_d4;

        ImageView iv_signal_d1, iv_signal_d2, iv_signal_d3, iv_signal_d4;
        ImageView deviceInfo_d1, deviceInfo_d2, deviceInfo_d3, deviceInfo_d4;

        LinearLayout ll_brightness;
        TextView tv_brightness, tv_power;
        BubbleSeekBar _seekbar_brightness, _seekbar_brightness15;

        TextView tv_next_scheudule_d1, tv_next_scheudule_d2, tv_next_scheudule_d3, tv_next_scheudule_d4;

        LinearLayout ll_switch_1, ll_switch_2, ll_switch_3, ll_switch_4;

        CardView ll_type_16;

        TextView tv_value_t, tv_value_h, tv_value_hi, tv_value_v;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_power = itemView.findViewById(R.id.tv_power);

            ll_parent = itemView.findViewById(R.id.parent);
            ll_brightness = itemView.findViewById(R.id.ll_brightness);

            ll_type_16 = itemView.findViewById(R.id.card_type_16);

            tv_brightness = itemView.findViewById(R.id.tv_brightness);
            _seekbar_brightness = itemView.findViewById(R.id.seekbar_type8);
            _seekbar_brightness15 = itemView.findViewById(R.id.seekbar_type15);
            _seekbar_brightness.setProgress(10);
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

            deviceInfo_d1 = itemView.findViewById(R.id.deviceInfo_d1);
            deviceInfo_d2 = itemView.findViewById(R.id.deviceInfo_d2);
            deviceInfo_d3 = itemView.findViewById(R.id.deviceInfo_d3);
            deviceInfo_d4 = itemView.findViewById(R.id.deviceInfo_d4);

            deviceInfo_d1.setOnClickListener(this);
            deviceInfo_d2.setOnClickListener(this);
            deviceInfo_d3.setOnClickListener(this);
            deviceInfo_d4.setOnClickListener(this);

            tv_next_scheudule_d1 = itemView.findViewById(R.id.tv_next_schedule_d1);
            tv_next_scheudule_d2 = itemView.findViewById(R.id.tv_next_schedule_d2);
            tv_next_scheudule_d3 = itemView.findViewById(R.id.tv_next_schedule_d3);
            tv_next_scheudule_d4 = itemView.findViewById(R.id.tv_next_schedule_d4);

            battery_indicator = itemView.findViewById(R.id.battery_indicator);

            tv_next_scheudule_d1.setVisibility(View.GONE);
            tv_next_scheudule_d2.setVisibility(View.GONE);
            tv_next_scheudule_d3.setVisibility(View.GONE);
            tv_next_scheudule_d4.setVisibility(View.GONE);

            ll_parent.setOnClickListener(v -> {
                if (allData.getObjData().get(getAdapterPosition()).isOnline())
                    objInterface.click_device(getAdapterPosition(), allData.getObjData().get(getAdapterPosition()).getDtype());
                else
                    Toast.makeText(context, "Device offline", Toast.LENGTH_SHORT).show();
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

            iv_showMenu_d1 = itemView.findViewById(R.id.showMenu_d1);
            iv_showMenu_d2 = itemView.findViewById(R.id.showMenu_d2);
            iv_showMenu_d3 = itemView.findViewById(R.id.showMenu_d3);
            iv_showMenu_d4 = itemView.findViewById(R.id.showMenu_d4);


            iv_signal_d1 = itemView.findViewById(R.id.iv_signal_d1);
            iv_signal_d2 = itemView.findViewById(R.id.iv_signal_d2);
            iv_signal_d3 = itemView.findViewById(R.id.iv_signal_d3);
            iv_signal_d4 = itemView.findViewById(R.id.iv_signal_d4);


            iv_showMenu_d1.setOnClickListener(this);
            iv_showMenu_d2.setOnClickListener(this);
            iv_showMenu_d3.setOnClickListener(this);
            iv_showMenu_d4.setOnClickListener(this);

            switch_d1.setOnClickListener(this);
            switch_d2.setOnClickListener(this);
            switch_d3.setOnClickListener(this);
            switch_d4.setOnClickListener(this);

            /*switch_d1.setEnabled(false);
            switch_d2.setEnabled(false);
            switch_d3.setEnabled(false);
            switch_d4.setEnabled(false);*/


            device_icon_d1.setOnClickListener(this);

            _seekbar_brightness.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
//                    Log.e("TAG", "OnProgress Changes Called " + bubbleSeekBar.getProgress() + " Type " + (allData.getObjData().get(getAdapterPosition()).getDtype()));
                    if (progress < 10) {
                        _seekbar_brightness.setProgress(10);
                    }
                    tv_brightness.setText(context.getResources().getString(R.string.brightness) + " " + _seekbar_brightness.getProgress() + "%");
                }

                @Override
                public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                    objInterface.publishSeekbar(getAdapterPosition(), progress);
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
                        objInterface.publishSeekbarType16(getAdapterPosition(), speed);
                    }
                }

                @Override
                public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

                }
            });

/*            switch_d1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    allData.getObjData().get(getAdapterPosition()).getObjd1().setState(isChecked);
                    TurnOnOffDevice(allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), "d1", isChecked, allData.getObjData().get(getAdapterPosition()).getDtype());
                    objInterface.TurnOnOffDevice(getAdapterPosition(), "d1", isChecked, allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                }
            });*/
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.switch1_d1:
                    if (allData.getObjData().get(getAdapterPosition()).isOnline())
                        ll_parent.performClick();
                    else {
                        switch_d1.setChecked(allData.getObjData().get(getAdapterPosition()).isOnline());
                        //switch_d1.setEnabled(false);
                        Toast.makeText(context, "Device offline", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.device_icon_d1: {
//                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
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
                        switch_d1.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd1().isState());
                        Log.e("TAG", "Device Checked At Switch " + allData.getObjData().get(getAdapterPosition()).getObjd1().getName());
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
                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d2", !allData.getObjData().get(getAdapterPosition()).getObjd2().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd2().setState(!allData.getObjData().get(getAdapterPosition()).getObjd2().isState());
                        switch_d2.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd2().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case R.id.ll_switch_3: {
                    try {
                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d3", !allData.getObjData().get(getAdapterPosition()).getObjd3().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd3().setState(!allData.getObjData().get(getAdapterPosition()).getObjd3().isState());
                        switch_d3.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd3().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.ll_switch_4: {
                    try {
                        objInterface.TurnOnOffDevice(getAdapterPosition(), "d4", !allData.getObjData().get(getAdapterPosition()).getObjd4().isState(), allData.getObjData().get(getAdapterPosition()).getDno(), allData.getObjData().get(getAdapterPosition()).getKey(), allData.getObjData().get(getAdapterPosition()).getDtype() + "");
                        allData.getObjData().get(getAdapterPosition()).getObjd4().setState(!allData.getObjData().get(getAdapterPosition()).getObjd4().isState());
                        switch_d4.setChecked(!allData.getObjData().get(getAdapterPosition()).getObjd4().isState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case R.id.deviceInfo_d1:
                case R.id.deviceInfo_d2:
                case R.id.deviceInfo_d4:
                case R.id.deviceInfo_d3: {
                    showInfoDialog(allData.getObjData().get(getAdapterPosition()).getIp(), allData.getObjData().get(getAdapterPosition()).getVersion());
                }
                break;

                case R.id.showMenu_d1: {
                    showMenu(v, allData.getObjData().get(getAdapterPosition()).isOnline(), getAdapterPosition(), 1);
                }
                break;

                case R.id.showMenu_d2: {
                    showMenu(v, allData.getObjData().get(getAdapterPosition()).isOnline(), getAdapterPosition(), 2);
                }
                break;


                case R.id.showMenu_d3: {
                    showMenu(v, allData.getObjData().get(getAdapterPosition()).isOnline(), getAdapterPosition(), 3);
                }
                break;


                case R.id.showMenu_d4: {
                    showMenu(v, allData.getObjData().get(getAdapterPosition()).isOnline(), getAdapterPosition(), 4);
                }
                break;
            }
        }
    }

    public void TurnOnOffDevice(String dno, String key, String dtype, boolean state, int type) {

        try {
            JsonObject _obj = new JsonObject();
            _obj.addProperty("dno", dno);
            _obj.addProperty("key", key);
            _obj.addProperty("dtype", type);
/*
            _obj.addProperty("dno", dno);
            _obj.addProperty("key", key);
            _obj.addProperty("dtype", dtype);*/

            JsonObject object = new JsonObject();
            object.addProperty("state", state);
            _obj.add(dtype, object);

            /*all_data_model.getObjData().get(pos).getObjd1().setState(state);
            _device_adapter.notifyItemChanged(pos);*/
            String json = gson.toJson(_obj);
            Log.e("TAGGG", "Object in String at TurnOnOffDevice " + json);
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    json,
                    1,
                    "d/" + dno + "/sub");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAGGG", "Exception at TurnOnOffDevice Devices " + e.getMessage(), e);
        }
    }


    void showMenu(View view, boolean isOffline, int pos, int type) {
        PopupMenu popup = new PopupMenu(context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.device_setting_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener


       /* Intent intent = new Intent(context, rf_remote.class);

        intent.putExtra("dno","84F3EBFBA20C");
        intent.putExtra("type","rf");
        intent.putExtra("mode","0");// "1" for working remote
        context.startActivity(intent);*/

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_d:
//                        delete(view);
//                        String dno1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
//                        remove(_id);
                    break;
                case R.id.rename_d:
//                        rename(view);
                    objInterface.RenameDevice(pos, "d" + type);
                    break;
                case R.id.move_d:
//                        changeroom(view);
                    if (allData.getObjData().get(pos).isOnline) {
                        LayoutInflater factory = LayoutInflater.from(context);
                        View dialogView = factory.inflate(R.layout.dialog_move_to, null);
                        AlertDialog dialog = new AlertDialog.Builder(context).create();
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                        dialog.setView(dialogView);

                        createList(allData.getObjData().get(pos).getRoom());

                        RecyclerView recyclerCities = dialogView.findViewById(R.id.recycler_bottom_cities);
                        Button btnSubmit = dialogView.findViewById(R.id.btn_loc_okay);

                        assert recyclerCities != null;
                        recyclerCities.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new SingleAdapter(context, listItems);
                        recyclerCities.setAdapter(adapter);

                        btnSubmit.setOnClickListener(v -> {
                            if (adapter.getSelected() != null) {
                                objInterface.MoveTo(adapter.getSelected().getId(), allData.getObjData().get(pos).getDno());
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Make at least one selection!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                    } else {
                        Toast.makeText(context, "Device is Offline!", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduled_d:
                    objInterface.scheduleDevice(pos, type);
                    break;

                case R.id.menu_logs:
                    objInterface.view_logs(pos, allData.getObjData().get(pos).getDno());
                    break;

//                case R.id.menu_graph:
//                    try {
//                        Intent intent = new Intent(context, Graphview.class);
//                        intent.putExtra("code", allData.getObjData().get(pos).getDno());
//                        context.startActivity(intent);
//                    } catch (Exception e) {
//                        Log.e("TAG", "Exception at OnClick " + e.getMessage());
//                    }
//                    break;
            }
            return true;
        });
        /*if (!isOffline) {
            popup.getMenu().removeItem(R.id.move_d);
            popup.getMenu().removeItem(R.id.rename_d);
            popup.getMenu().removeItem(R.id.scheduled_d);
        }
        popup.getMenu().removeItem(R.id.edit_grp);*/
        popup.show();
    }

    public void showInfoDialog(String ip, double version) {

        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.device_info_dialog, null, false);
        dialog.setContentView(view);
        TextView tv_ip = view.findViewById(R.id.tv_ip_address);
        TextView tv_version = view.findViewById(R.id.tv_version);
        tv_ip.setText(ip);
        tv_version.setText(version + "");
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void notifyManually(AllDataResponseModel allDeviceData) {
        this.allData = allDeviceData;
        notifyDataSetChanged();
    }

    private void createList(String roomID) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://209.58.164.151:88/api/Get/getAppHome?UID=" + PreferencesHelper.getUserId(context),
                null, response -> {
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                JSONArray jsonArray = jsonObject.getJSONArray("rooms");

                listItems.clear();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject listItem = jsonArray.getJSONObject(i);
                    RoomModel roomModel = new RoomModel();

                    roomModel.setId(listItem.getString("ID"));
                    roomModel.setName(listItem.getString("name"));

                    if (!listItem.getString("ID").equals(roomID)) {
                        listItems.add(roomModel);
                    }
                }

                adapter.setEmployees(listItems);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        requestQueue.add(jsonObjectRequest);
    }
}
