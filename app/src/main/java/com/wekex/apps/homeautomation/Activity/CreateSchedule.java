package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferObserver;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Sqlite.DatabaseClient;
import com.wekex.apps.homeautomation.Sqlite.Scheduler;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.schedule_model;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.wekex.apps.homeautomation.utils.Constants.createdate;
import static com.wekex.apps.homeautomation.utils.Constants.createtime;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;

public class CreateSchedule extends AppCompatActivity {

    private String Time;
    private char[] Days = "0000000".toCharArray();

    private int Action = 1;
    private int Arm = 1;
    private int Mode = 1;
    private int Enable_timer = 1;
    ColorPicker colorPickerView;
    private String TAG = "schedulesActvity";
    String DeviceInfos;
    LinearLayout daysLayout;
    private Toast toast;
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private WheelView hour, min, date, month, year;
    private String sdate, smonth, syear;
    String deviceType;
    Spinner window;
    String Dno = "";

    int shour, smin;

    TextView tv_brightness;
    SeekBar _seekbar_br;
    int sr = 0;

    schedule_model _schedule_model;
    Gson _gson = new Gson();

    boolean isFromEditSchedule;

    RadioButton rb_on, rb_off;

    CheckBox repeat;
    CheckBox arm;
    CheckBox output;

    LinearLayout ll_date, ll_time;

    TextView tv_date, tv_time;
    int c_hour; // return the hour in 24 hrs format (ranging from 0-23)
    int c_min;
    RadioGroup action;
    RadioGroup based_on;
    CheckBox sunrise, sunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Log.e("TAGG", "Track Data OnCreate CreateSchedule Called");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New schedule");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_brightness = findViewById(R.id.tv_brightness);
        tv_brightness.setText(getResources().getString(R.string.brightness) + " 10%");
        _seekbar_br = findViewById(R.id.brightness);

        _seekbar_br.incrementProgressBy(1);
        _seekbar_br.setMax(100);

        colorPickerView = findViewById(R.id.colorPicker);
        colorPickerView.setColor(0x7F313C93);
        colorPickerView.setShowOldCenterColor(false);
        window = findViewById(R.id.window);

        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);

        repeat = findViewById(R.id.repeat);
        arm = findViewById(R.id.arm);
        output = findViewById(R.id.output);

        action = findViewById(R.id.action);
        based_on = findViewById(R.id.basedon);

        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);

        ll_date = findViewById(R.id.ll_date);
        ll_time = findViewById(R.id.ll_time);


        tv_date.setTextColor(getResources().getColor(R.color.gray600));
        tv_time.setTextColor(getResources().getColor(R.color.gray600));

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time

        Calendar rightNow = Calendar.getInstance();
        c_hour = rightNow.get(Calendar.HOUR_OF_DAY);

        c_min = rightNow.get(Calendar.MINUTE);

//        tv_date.setText(formattedDate);
//        tv_time.setText(c_hour + ":" + c_min);

        tv_time.setText("00:00");
        tv_date.setText("00-00-0000");

        shour = 00;
        smin = 00;

        sunrise.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sunset.setChecked(false);
                ll_time.setClickable(false);
            } else ll_time.setClickable(true);
        });

        sunset.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sunrise.setChecked(false);
                ll_time.setClickable(false);
            } else ll_time.setClickable(true);
        });

        rb_on = findViewById(R.id.on);
        rb_off = findViewById(R.id.rb_off);

        if (getIntent().hasExtra("Dno")) {
            Dno = getIntent().getStringExtra("Dno");
        }
        if (getIntent().hasExtra("DeviceInfos"))
            DeviceInfos = getIntent().getStringExtra("DeviceInfos");


        if (getIntent().hasExtra("_list")) {
            String _data = getIntent().getStringExtra("_list");
            _schedule_model = _gson.fromJson(_data, schedule_model.class);
        }

        if (getIntent().hasExtra("SrNo")) {
            sr = getIntent().getIntExtra("SrNo", 0);
        }


        _seekbar_br.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    progress = 1;
                Constants.brightness = progress;
                tv_brightness.setText(getResources().getString(R.string.brightness) + " " + progress + "%");
                if (progress < 10) {
                    _seekbar_br.setProgress(10);
                }
                Log.d(TAG, "onProgressChanged: bb " + Constants.brightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        if (getIntent().hasExtra("isFromEditSchedule")) {
            isFromEditSchedule = true;
            toolbar.setTitle("Update schedule");
            double br = (_schedule_model.get_lst_schedule().get(sr).getBr() * 100);
            _seekbar_br.setProgress((int) br);

            int r = _schedule_model.get_lst_schedule().get(sr).getR();
            int g = _schedule_model.get_lst_schedule().get(sr).getG();
            int b = _schedule_model.get_lst_schedule().get(sr).getB();

            int color = Color.rgb(r, g, b);
            colorPickerView.setColor(color);
        }
        daysLayout = findViewById(R.id.daysLayout);
//        AddDays();
//        initWheel1();
//        initForm();

        if (isFromEditSchedule)
            setData(_schedule_model.get_lst_schedule().get(sr));


        ll_date.setOnClickListener(v -> new DatePickerDialog(CreateSchedule.this, datePicker, rightNow
                .get(Calendar.YEAR), rightNow.get(Calendar.MONTH),
                rightNow.get(Calendar.DAY_OF_MONTH)).show());

        ll_time.setOnClickListener(v -> {
            final Calendar c1 = Calendar.getInstance();
            c_hour = c1.get(Calendar.HOUR_OF_DAY);
            c_min = c1.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog;

            timePickerDialog = new TimePickerDialog(CreateSchedule.this,
                    (view, hourOfDay, minute) -> {
                        if (minute <= 9) {
                            smin = Integer.parseInt("0" + minute);
                        } else
                            smin = minute;

                        if (hourOfDay <= 9) {
                            shour = Integer.parseInt("0" + hourOfDay);
                        } else
                            shour = hourOfDay;
                        tv_time.setText(hourOfDay + ":" + minute);

                    }, c_hour, c_min, false);
            timePickerDialog.show();

        });

        if (isFromEditSchedule) {
            if (_schedule_model.get_lst_schedule().get(sr).getRepeat() == 1) {
                ll_date.setClickable(false);

                Log.e("TAGGG", "Repeat ser from 275");
                repeat.setChecked(true);
                tv_date.setTextColor(getResources().getColor(R.color.gray600));
                tv_time.setTextColor(getResources().getColor(R.color.gray600));

            } else {
                output.setChecked(true);
                ll_date.setClickable(true);

                tv_date.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_time.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        } else {
            ll_date.setClickable(false);
        }
        AddDays();
        initWheel1();
        initForm();
    }

    void setData(schedule_model.schedule _objSchedule) {

        if (_objSchedule.isState()) {
            rb_off.setChecked(false);
            rb_on.setChecked(true);
        } else {
            rb_on.setChecked(false);
            rb_off.setChecked(true);
        }
        if (_objSchedule.getRepeat() == 1)
            repeat.setChecked(true);
        else
            repeat.setChecked(false);

        if (_objSchedule.getOutput() == 2)
            output.setChecked(true);
        else
            output.setChecked(false);

        if (_objSchedule.getArm() == 1)
            arm.setChecked(true);
        else
            arm.setChecked(false);

        tv_date.setText(_objSchedule.getDate());
        tv_time.setText(_objSchedule.getTime_H() + ":" + _objSchedule.getTime_M());

        shour = _objSchedule.getTime_H();
        smin = _objSchedule.getTime_M();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initForm() {

        if (DeviceInfos != null && !DeviceInfos.isEmpty()) {
            deviceType = jsonObjectreader(DeviceInfos, "dtype");
            if ("2".equals(deviceType)) {
                window.setVisibility(View.VISIBLE);
            }
        }


        repeat.setOnClickListener(v -> {

            Log.e("TAGGG", "Repeat ser from 356");
            if (repeat.isChecked()) {
                output.setChecked(false);
                date.setClickable(false);
                date.setBackgroundColor(getResources().getColor(R.color.grey));

                ll_date.setClickable(false);

                tv_date.setTextColor(getResources().getColor(R.color.gray600));
                tv_time.setTextColor(getResources().getColor(R.color.gray600));

                daysLayout.setEnabled(true);
                daysLayout.setClickable(true);

                tv_time.setText("00:00");
                tv_date.setText("00-00-0000");
                shour = 00;
                smin = 00;
            } else {
                output.setChecked(true);
                ll_date.setClickable(true);

                tv_date.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_time.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                daysLayout.setEnabled(false);
                daysLayout.setClickable(false);
                Days = "0000000".toCharArray();
                AddDays();

            }
        });
        arm.setOnClickListener(v -> {
            Arm = arm.isChecked() ? 1 : 0;
        });
        output.setOnClickListener(v -> { ///removed

            if (output.isChecked()) {

                Log.e("TAGGG", "Repeat ser from 397");
                repeat.setChecked(false);
                date.setClickable(true);
                date.setBackgroundColor(getResources().getColor(R.color.white));
                ll_date.setClickable(true);

                tv_date.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_time.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                daysLayout.setEnabled(false);
                daysLayout.setClickable(false);
                Days = "0000000".toCharArray();
                AddDays();
            } else {
                Log.e("TAGGG", "Repeat ser from 412");
                repeat.setChecked(true);

                ll_date.setClickable(false);

                tv_date.setTextColor(getResources().getColor(R.color.gray600));
                tv_time.setTextColor(getResources().getColor(R.color.gray600));

                daysLayout.setEnabled(true);
                daysLayout.setClickable(true);

                tv_time.setText("00:00");
                tv_date.setText("00-00-0000");
                shour = 00;
                smin = 00;
            }
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


        if (isFromEditSchedule) {
            hour.setSelection(_schedule_model.get_lst_schedule().get(sr).getTime_H());
            min.setSelection(_schedule_model.get_lst_schedule().get(sr).getTime_M());

            String dob = _schedule_model.get_lst_schedule().get(sr).getDate();
            int mnth = 0, dd = 0, yer = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date d = sdf.parse(dob);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                mnth = checkDigit(cal.get(Calendar.MONTH) + 1);
                dd = checkDigit(cal.get(Calendar.DATE));
                yer = checkDigit(cal.get(Calendar.YEAR));


                month.setSelection(mnth - 1);

                int index = 0;
                for (int i = currentDay; i <= 31; i++) {
                    if (i == dd) {
                        date.setSelection(index);
                    }
                    index++;
                }

                index = 0;
                for (int i = currentYear; i <= 2050; i++) {
                    if (i == yer) {
                        year.setSelection(index);
                    }
                    index++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int checkDigit(int number) {
        return number <= 9 ? 0 + number : number;
    }


    private void AddDays() {
        daysLayout.removeAllViews();
        int daysno = 0;
        String days_from_model = "";
        if (isFromEditSchedule) {
            days_from_model = _schedule_model.get_lst_schedule().get(sr).getDays();
        }
        int index = 0;
        for (String day : days) {
            View view = LayoutInflater.from(this).inflate(R.layout.round_days, null, false);
            TextView daystextView = view.findViewById(R.id.daystextView);
            daystextView.setTag(daysno);
            daystextView.setText(day);

            int finalDaysno = daysno;
            daystextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (output.isChecked())
                        return;

                    if (daystextView.isSelected()) {
                        daystextView.setSelected(false);
                        Days[(int) daystextView.getTag()] = '0';
                    } else {
                        daystextView.setSelected(true);
//                        Days[finalDaysno] = daystextView.getText().charAt(0);
                        Days[finalDaysno] = '1';
                    }
                    // Toast.makeText(schedules.this, " "+String.valueOf(Days), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: " + String.valueOf(Days));
                }
            });


            if (isFromEditSchedule && repeat.isChecked()) {
                if (days_from_model.charAt(index) == '1') {
                    daystextView.setSelected(true);
                    Days[finalDaysno] = '1';
                }
            }
            index++;
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
//        Time = confirm();

        int colorpic = colorPickerView.getColor();
        int red = Color.red(colorpic);
        int green = Color.green(colorpic);
        int blue = Color.blue(colorpic);
        double br = (double) _seekbar_br.getProgress() / 100;

        if (isFromEditSchedule) {

            if (repeat.isChecked()) {
                if (!String.valueOf(Days).contains("1")) {
                    Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (shour == 0 || tv_date.getText().toString().startsWith("0000")) {
                    Toast.makeText(this, "Please Select Date & Time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String _data = _gson.toJson(_schedule_model);
            Log.e("TAGGG", "Data Before " + _data);
            _schedule_model.get_lst_schedule().get(sr).setTime_M(smin);
            _schedule_model.get_lst_schedule().get(sr).setTime_H(shour);
            _schedule_model.get_lst_schedule().get(sr).setRepeat(repeat.isChecked() ? 1 : 0);
            _schedule_model.get_lst_schedule().get(sr).setOutput(output.isChecked() ? 1 : 0);
            _schedule_model.get_lst_schedule().get(sr).setState((Action == 1));
            _schedule_model.get_lst_schedule().get(sr).setDays(String.valueOf(Days));
            _schedule_model.get_lst_schedule().get(sr).setDate(tv_date.getText().toString().trim());
            _schedule_model.get_lst_schedule().get(sr).setArm(Arm);
            _schedule_model.get_lst_schedule().get(sr).setR(red);
            _schedule_model.get_lst_schedule().get(sr).setG(green);
            _schedule_model.get_lst_schedule().get(sr).setB(blue);
            _schedule_model.get_lst_schedule().get(sr).setBr(br);
            _schedule_model.get_lst_schedule().get(sr).setW(0);
            _schedule_model.get_lst_schedule().get(sr).setWw(0);
            _data = _gson.toJson(_schedule_model);
            Log.e("TAGGG", "Data Before Remove " + _data);
            showDialog(_data);

        } else {
            if (sr == 1) {

                if (repeat.isChecked()) {
                    if (!String.valueOf(Days).contains("1")) {
                        Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (shour == 0 || tv_date.getText().toString().startsWith("0000")) {
                        Toast.makeText(this, "Please Select Date & Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                JSONObject _json = new JSONObject();
                _json.put("sn", sr);
                _json.put("Time_H", shour);
                _json.put("Time_M", smin);
                _json.put("Repeat", repeat.isChecked() ? 1 : 0);
                _json.put("Output", output.isChecked() ? 1 : 0);
                _json.put("state", (Action == 1));
                _json.put("Days", String.valueOf(Days));
//                _json.put("Date", syear + "-" + smonth + "-" + sdate);
                _json.put("Date", tv_date.getText().toString().trim());
                _json.put("Arm", Arm);
                _json.put("r", red);
                _json.put("g", green);
                _json.put("b", blue);
                _json.put("br", br);
                _json.put("w", 0);
                _json.put("ww", 0);

                JSONObject _main = new JSONObject();
                JSONArray _arr = new JSONArray();
                _arr.put(_json);
                _main.put("schedule", _arr);
                Log.e("TAGGG", "Final Json Of CreateSchedule " + _main.toString());
                showDialog(_main.toString());
            } else {

                if (repeat.isChecked()) {
                    if (!String.valueOf(Days).contains("1")) {
                        Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (shour == 0 || tv_date.getText().toString().startsWith("0000")) {
                        Toast.makeText(this, "Please Select Date & Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                schedule_model.schedule _obj = new schedule_model.schedule();
                _obj.setSerialNumber(sr);
                _obj.setTime_H(shour);
                _obj.setTime_M(smin);
                _obj.setRepeat(repeat.isChecked() ? 1 : 0);
                _obj.setOutput(output.isChecked() ? 1 : 0);
                _obj.setState((Action == 1));
                _obj.setDays(String.valueOf(Days));
//                _obj.setDate(syear + "-" + smonth + "-" + sdate);
                _obj.setDate(tv_date.getText().toString().trim());
                _obj.setArm(Arm);
                _obj.setR(red);
                _obj.setG(green);
                _obj.setB(blue);
                _obj.setW(0);
                _obj.setWw(0);
                _obj.setBr(br);
                _schedule_model.get_lst_schedule().add(_obj);

                String newList = _gson.toJson(_schedule_model);
                Log.e("TAGGG", "New List " + newList);

                showDialog(newList);
            }
        }

    }

    public void postSchedules(String infos) {
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + Dno + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private class myselectionListener implements WheelView.OnWheelItemSelectedListener {
        @Override
        public void onItemSelected(int position, Object o) {
            Log.d(TAG, position + "onItemSelected: " + o.toString());
        }
    }

    void showDialog(String _object) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateSchedule.this);
        if (isFromEditSchedule) {
            dialog.setTitle("Update CreateSchedule");
            dialog.setMessage("Do you want to proceed now ?");
        } else {
            dialog.setTitle("New Schedule");
            dialog.setMessage("Do you want to proceed to new schedule ?");
        }
        dialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            postSchedules(_object);


            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String dt = df.format(c.getTime());
            // Start date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            try {
                schedule_model _model_convert = _gson.fromJson(_object, schedule_model.class);
                Log.e("TAG", "Schedule logs " + _object);
                ArrayList<_model_next_schedule> _lst_dates = new ArrayList<>();
                for (int j = 0; j < _model_convert.get_lst_schedule().size(); j++) {

                    if (_model_convert.get_lst_schedule().get(j).getRepeat() == 1) {
                        c.setTime(sdf.parse(dt));

                        int selectedDay = (_model_convert.get_lst_schedule().get(j).getDays().indexOf("1")) + 1;
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

                        int diff = ((7 - dayOfWeek) + selectedDay);
                        c.add(Calendar.DATE, diff);
                        Log.e("TAG", "Both Day dayOfWeek " + dayOfWeek + " selectedDay " + selectedDay);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String output = sdf1.format(c.getTime());
                        Log.e("TAG", "Both Day dayOfWeek Date " + output);
                        _model_next_schedule obj = new _model_next_schedule(output, _model_convert.get_lst_schedule().get(j).getTime_H() + ":" + _model_convert.get_lst_schedule().get(j).getTime_M(), (_model_convert.get_lst_schedule().get(j).getRepeat()) + "");
                        _lst_dates.add(obj);
                    } else {
//                            _str_date.add(_model_convert.get_lst_schedule().get(j).getDate());
                        _model_next_schedule obj = new _model_next_schedule(_model_convert.get_lst_schedule().get(j).getDate(), _model_convert.get_lst_schedule().get(j).getTime_H() + ":" + _model_convert.get_lst_schedule().get(j).getTime_M(), (_model_convert.get_lst_schedule().get(j).getRepeat()) + "");
                        _lst_dates.add(obj);
                        Log.e("TAG", "Schedule Added in list " + _model_convert.get_lst_schedule().get(j).getDate());
                    }
                }

                for (_model_next_schedule _object1 : _lst_dates) {
                    Log.e("TAG", "From List Before sort" + _object1.get_date());
                }

                Collections.sort(_lst_dates, new Comparator<_model_next_schedule>() {
                    public int compare(_model_next_schedule o1, _model_next_schedule o2) {
                        return o1.get_date().compareTo(o2.get_date());
                    }
                });
                Log.e("TAG", "*************SORTING*****************");
                for (_model_next_schedule _object1 : _lst_dates) {
                    Log.e("TAG", "From List After sort >> " + _object1.get_date() + " " + _object1.get_time());
                }
                Constants.savetoShared(CreateSchedule.this).edit().putString(Dno + "_1", (getResources().getString(R.string.work_next) + _lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
            } catch (Exception e) {
                Log.e("TAG", "Exception at add schedule " + e.getMessage());
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", _object);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });


        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }


    class _model_next_schedule {
        public String _date = "";
        public String _time = "";
        public String _type = "";

        public _model_next_schedule(String _date, String _time, String _type) {
            this._date = _date;
            this._time = _time;
            this._type = _type;
        }

        public String get_date() {
            return _date;
        }

        public void set_date(String _date) {
            this._date = _date;
        }

        public String get_time() {
            return _time;
        }

        public void set_time(String _time) {
            this._time = _time;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            String _month = "";
            if ((monthOfYear + 1) <= 9) {
                _month = "0" + (monthOfYear + 1);
            } else
                _month = (monthOfYear + 1) + "";

            String day = dayOfMonth + "";
            if (dayOfMonth <= 9) {
                day = "0" + dayOfMonth;
            }

            tv_date.setText(day + "-" + _month + "-" + year);
        }
    };


}
