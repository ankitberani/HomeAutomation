package com.wekex.apps.homeautomation;


import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

import static com.wekex.apps.homeautomation.utils.Constants.createdate;
import static com.wekex.apps.homeautomation.utils.Constants.createtime;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPutInt;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.DtypeViews.postSchedules;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class schedules extends AppCompatActivity {
    private String Time;
    private char[] Days = "-------".toCharArray();
    private int Repeat = 0;
    private int Output = 0;
    private int Action = 1;
    private int Arm = 1;
    private int Mode = 1;
    private int Enable_timer = 1;
    ColorPickerView colorPickerView;
    private String TAG = "schedulesActvity";
    String DeviceInfos;
    LinearLayout daysLayout;
    private Toast toast;
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private WheelView hour, min, date, month, year;
    private String shour, smin, sdate, smonth, syear;
    String deviceType;
    Spinner window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);
        DeviceInfos = getIntent().getStringExtra("DeviceInfos");
        daysLayout = findViewById(R.id.daysLayout);
        AddDays();
        initWheel1();
        initForm();
    }

    private void initForm() {
        colorPickerView = findViewById(R.id.colorPicker);
        colorPickerView.setInitialColor(0x7F313C93);
        colorPickerView.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {

            }
        });
        window = findViewById(R.id.window);
        deviceType = jsonObjectreader(DeviceInfos, "dtype");
        switch (deviceType) {
            case "2":
                window.setVisibility(View.VISIBLE);
                break;

        }

        CheckBox repeat = findViewById(R.id.repeat);
        CheckBox arm = findViewById(R.id.arm);
        CheckBox output = findViewById(R.id.output);
        RadioGroup action = findViewById(R.id.action);
        RadioGroup based_on = findViewById(R.id.basedon);

        repeat.setOnClickListener(v -> {
            Repeat = repeat.isChecked() ? 1 : 0;
        });
        arm.setOnClickListener(v -> {
            Arm = arm.isChecked() ? 1 : 0;
        });
        output.setOnClickListener(v -> { ///removed
            Output = output.isChecked() ? 1 : 0;
        });

        action.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.on)
                Action = 1;
            else
                Action = 0;
        });

        based_on.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.time)
                Mode = 0;
            else if (checkedId == R.id.sunrise)
                Mode = 1;
            else
                Mode = 2;

        });

    }

    private void initWheel1() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hr = calendar.get(Calendar.HOUR);
        int mn = calendar.get(Calendar.MINUTE);


        hour = findViewById(R.id.hour);
        hour.setWheelAdapter(new ArrayWheelAdapter(this));
        hour.setSkin(WheelView.Skin.Holo);
        hour.setWheelData(createtime(24));
        hour.setSelection(hr);

        min = findViewById(R.id.min);
        min.setWheelAdapter(new ArrayWheelAdapter(this));
        min.setSkin(WheelView.Skin.Holo);
        min.setWheelData(createtime(60));
        min.setOnWheelItemSelectedListener(new myselectionListener());
        min.setSelection(mn);

        month = findViewById(R.id.month);
        month.setWheelAdapter(new ArrayWheelAdapter(this));
        month.setSkin(WheelView.Skin.Holo);
        month.setWheelData(createdate(currentMonth, 12));

        date = findViewById(R.id.date);
        date.setWheelAdapter(new ArrayWheelAdapter(this));
        date.setSkin(WheelView.Skin.Holo);
        date.setWheelData(createdate(currentDay, 31));

        year = findViewById(R.id.year);
        year.setWheelAdapter(new ArrayWheelAdapter(this));
        year.setSkin(WheelView.Skin.Holo);
        year.setWheelData(createdate(currentYear, 2050));


        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        hour.setStyle(style);
        min.setStyle(style);
        month.setStyle(style);
        date.setStyle(style);
        year.setStyle(style);

    }

    public String confirm() {

        shour = hour.getSelectionItem().toString();
        smin = min.getSelectionItem().toString();
        smonth = month.getSelectionItem().toString();
        sdate = date.getSelectionItem().toString();
        syear = date.getSelectionItem().toString();
        return shour + ":" + smonth;

    }

    private void AddDays() {
        daysLayout.removeAllViews();
        int daysno = 0;
        for (String day : days) {
            View view = LayoutInflater.from(this).inflate(R.layout.round_days, null, false);
            TextView daystextView = view.findViewById(R.id.daystextView);
            daystextView.setTag(daysno);
            daystextView.setText(day);

            int finalDaysno = daysno;
            daystextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (daystextView.isSelected()) {
                        daystextView.setSelected(false);
                        Days[(int) daystextView.getTag()] = '-';
                    } else {
                        daystextView.setSelected(true);
                        Days[finalDaysno] = daystextView.getText().charAt(0);
                    }
                    // Toast.makeText(schedules.this, " "+String.valueOf(Days), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: " + String.valueOf(Days));
                }
            });
            daysLayout.addView(view);
            daysno++;
        }
    }

    private List<String> createMainDatas() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add("item " + i);
        }
        return list;
    }

    public void finish(View view) {
        finish();
    }

    public void done(View view) throws JSONException {
        Time = confirm();
        JSONObject jsonObject = new JSONObject();
        jsonObjectPut(jsonObject, "Time", Time);
        jsonObjectPutInt(jsonObject, "Repeat", Repeat);
        jsonObjectPutInt(jsonObject, "Action", Action);
        jsonObjectPutInt(jsonObject, "Arm", Arm);
        jsonObjectPutInt(jsonObject, "Mode", Mode);
        jsonObjectPut(jsonObject, "Days", String.valueOf(Days));
        jsonObjectPutInt(jsonObject, "window", window.getSelectedItemPosition() + 1);

        JSONObject device = stringToJsonObject(DeviceInfos);
        device.put("schedule", jsonObject);
        String updated = udpdateJSON(this, device.toString());
        Log.e("TAGGG", "schedule_interface Data " + updated);
//        postSchedules(this, updated);
//
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result", updated);
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
    }


    private class myselectionListener implements WheelView.OnWheelItemSelectedListener {
        @Override
        public void onItemSelected(int position, Object o) {
            Log.d(TAG, position + "onItemSelected: " + o.toString());
        }
    }
}
