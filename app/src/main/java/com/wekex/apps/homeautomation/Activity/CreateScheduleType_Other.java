package com.wekex.apps.homeautomation.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Sqlite.Scheduler;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.schedule_model;
import com.wekex.apps.homeautomation.model.schedule_model_type_other;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.wekex.apps.homeautomation.utils.Constants.createtime;

public class CreateScheduleType_Other extends AppCompatActivity implements View.OnClickListener {

    String dno1;
    int type;
    WheelView Wv_month, Wv_day, Wv_hour, Wv_mint;
    private char[] Days = "0000000".toCharArray();
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    LinearLayout daysLayout;

    int currentYear;
    int currentMonth;
    int currentDay;
    int hr;
    int mn;

    ImageView iv_back;
    TextView tv_on, tv_off, tv_cancel, tv_save;
    TextView tv_repeate_once;

    LinearLayout ll_repeat, ll_timer_main;

    int repeat = 0;
    int selected_mnth;
    int selected_date;
    int selected_hour;
    int selected_min;
    private int Arm = 1;

    CheckBox arm, sunrise, sunset;

    String TAG = "CreateSchedule";
    boolean isFromEditSchedule = false;
    schedule_model_type_other _schedule_model;
    Gson _gson = new Gson();
    int sr = 0;
    RadioGroup rg_repeat_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule_type__other);

        sunrise = findViewById(R.id.rb_sunrise);
        sunset = findViewById(R.id.rb_sunset);
        rg_repeat_state = findViewById(R.id.rg_repeat_state);
        daysLayout = findViewById(R.id.daysLayout);
        if (getIntent().hasExtra("dno")) {
            dno1 = getIntent().getStringExtra("dno");
        }

        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
        if (getIntent().hasExtra("SrNo")) {
            sr = getIntent().getIntExtra("SrNo", 0);
        } else
            sr = 1;

        arm = findViewById(R.id.arm);
        arm.setOnClickListener(v -> Arm = arm.isChecked() ? 1 : 0);



        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        selected_mnth = currentMonth;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        selected_date = currentDay;
        hr = calendar.get(Calendar.HOUR);
        selected_hour = hr;
        mn = calendar.get(Calendar.MINUTE);
        selected_min = mn;

        Wv_month = findViewById(R.id.Wv_month);
        Wv_month.setWheelAdapter(new ArrayWheelAdapter(this));
        Wv_month.setSkin(WheelView.Skin.Holo);
        Wv_month.setWheelData(createdate(currentMonth, 12));
        Wv_month.setOnWheelItemSelectedListener((position, o) -> {
//                selected_mnth = position + 1;
            selected_mnth = currentMonth + position;
            Log.e("TAGG", "selected_mnth " + selected_mnth);
        });


        Wv_hour = findViewById(R.id.Wv_hour);
        Wv_hour.setWheelAdapter(new ArrayWheelAdapter(this));
        Wv_hour.setSkin(WheelView.Skin.Holo);
        Wv_hour.setWheelData(createtime(24));
        Wv_hour.setSelection(hr);
        Wv_hour.setOnWheelItemSelectedListener((position, o) -> selected_hour = position);

        Wv_mint = findViewById(R.id.Wv_mnt);
        Wv_mint.setWheelAdapter(new ArrayWheelAdapter(this));
        Wv_mint.setSkin(WheelView.Skin.Holo);
        Wv_mint.setWheelData(createtime(60));
        Wv_mint.setSelection(mn);
        Wv_mint.setOnWheelItemSelectedListener((position, o) -> selected_min = position);

        Wv_day = findViewById(R.id.Wv_day);
        Wv_day.setWheelAdapter(new ArrayWheelAdapter(this));
        Wv_day.setSkin(WheelView.Skin.Holo);
        Wv_day.setWheelData(createdate(currentDay, 31));

        Wv_day.setOnWheelItemSelectedListener((position, o) -> selected_date = currentDay + position);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        tv_on = findViewById(R.id.tv_on);
        tv_off = findViewById(R.id.tv_off);

        tv_on.setOnClickListener(this);
        tv_off.setOnClickListener(this);

        tv_on.setTag(1);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_save = findViewById(R.id.tv_save);

        tv_repeate_once = findViewById(R.id.tv_repeate_once);
        ll_repeat = findViewById(R.id.ll_repeat);

        ll_timer_main = findViewById(R.id.ll_timer_main);

        tv_cancel.setOnClickListener(this::onClick);
        tv_save.setOnClickListener(this::onClick);

        ll_repeat.setOnClickListener(this::onClick);
        tv_repeate_once.setOnClickListener(v -> ll_repeat.performClick());
        AddDays();
        daysLayout.setEnabled(false);
        daysLayout.setClickable(false);

        if (getIntent().hasExtra("_list")) {
            String _data = getIntent().getStringExtra("_list");
            _schedule_model = _gson.fromJson(_data, schedule_model_type_other.class);

            if (getIntent().hasExtra("isFromEditSchedule")) {
                isFromEditSchedule = true;

                type = _schedule_model.get_lst_schedule().get(sr).getOutput();
                Wv_hour.setSelection(_schedule_model.get_lst_schedule().get(sr).getTime_H());
                Wv_mint.setSelection(_schedule_model.get_lst_schedule().get(sr).getTime_M());

                selected_hour = _schedule_model.get_lst_schedule().get(sr).getTime_H();
                selected_min = _schedule_model.get_lst_schedule().get(sr).getTime_M();

                try {
                    String _selected_date = _schedule_model.get_lst_schedule().get(sr).getDate();
                    int mnth, dd, yer = 0;
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Date d = sdf.parse(_selected_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    mnth = checkDigit(cal.get(Calendar.MONTH) + 1);
                    dd = checkDigit(cal.get(Calendar.DATE));
                    yer = checkDigit(cal.get(Calendar.YEAR));

                    Wv_month.setSelection(mnth - currentMonth);

                    int index = 0;
                    for (int i = currentDay; i <= 31; i++) {
                        if (i == dd) {
                            Wv_day.setSelection(index);
                        }
                        index++;
                    }

//                    currentYear = yer;

                    repeat = _schedule_model.get_lst_schedule().get(sr).getRepeat();
                    Arm = _schedule_model.get_lst_schedule().get(sr).getArm();
                    arm.setChecked(Arm == 1);
                    if (repeat == 0) {
                        tv_repeate_once.setText("Only Once");
                    } else {
                        Wv_month.setEnabled(false);
                        Wv_day.setEnabled(false);

                        tv_repeate_once.setText("Repeat");
                        AddDays();
                    }

                    if (_schedule_model.get_lst_schedule().get(sr).isState()) {
                        tv_on.setTextColor(getResources().getColor(R.color.colorAccent));
                        tv_off.setTextColor(getResources().getColor(R.color.gray600));
                    } else {
                        tv_on.setTextColor(getResources().getColor(R.color.gray600));
                        tv_off.setTextColor(getResources().getColor(R.color.colorAccent));
                    }

                    int h = _schedule_model.get_lst_schedule().get(sr).getTime_H();
                    int m = _schedule_model.get_lst_schedule().get(sr).getTime_M();

                    Wv_hour.setSelection(h);
                    Wv_mint.setSelection(m);
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception " + e.getMessage(), e);
                }
            }
        }

        rg_repeat_state.setOnCheckedChangeListener((radioGroup, id) -> {
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_once) {
                repeat = 0;
                daysLayout.setEnabled(false);
                daysLayout.setClickable(false);
                AddDays();
                Days = "0000000".toCharArray();
                ll_timer_main.setEnabled(true);
                ll_timer_main.setClickable(true);
                Wv_month.setEnabled(true);
                Wv_day.setEnabled(true);
            } else {
                Wv_month.setEnabled(false);
                Wv_day.setEnabled(false);
                ll_timer_main.setEnabled(false);
                ll_timer_main.setClickable(false);
                daysLayout.setEnabled(true);
                daysLayout.setClickable(true);
                repeat = 1;
            }
        });

        sunrise.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sunset.setChecked(false);
                Wv_hour.setEnabled(false);
                Wv_mint.setEnabled(false);
            } else {
                Wv_hour.setEnabled(true);
                Wv_mint.setEnabled(true);
            }
        });

        sunset.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sunrise.setChecked(false);
                Wv_hour.setEnabled(false);
                Wv_mint.setEnabled(false);
            } else {
                Wv_hour.setEnabled(true);
                Wv_mint.setEnabled(true);
            }
        });
    }

    public int checkDigit(int number) {
        return number <= 9 ? 0 + number : number;
    }

    public ArrayList<String> createdate(int start, int end) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
            case R.id.tv_cancel:
                finish();
                break;

            case R.id.tv_on:
                tv_on.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_off.setTextColor(getResources().getColor(R.color.gray600));
                tv_on.setTag(1);
                break;

            case R.id.tv_off:
                tv_on.setTextColor(getResources().getColor(R.color.gray600));
                tv_off.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_on.setTag(0);
                break;

            case R.id.tv_save:
                addSchedule();
                break;

            case R.id.ll_repeat: {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(CreateScheduleType_Other.this);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreateScheduleType_Other.this, android.R.layout.simple_list_item_1);
                arrayAdapter.add("Only Once");
                arrayAdapter.add("Repeat");

                builderSingle.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());

                builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {

                    if (which == 0) {
                        repeat = 0;
                        daysLayout.setEnabled(false);
                        daysLayout.setClickable(false);
                        AddDays();
                        Days = "0000000".toCharArray();
                        ll_timer_main.setEnabled(true);
                        ll_timer_main.setClickable(true);

                        Wv_month.setEnabled(true);
                        Wv_day.setEnabled(true);


                    } else {
                        Wv_month.setEnabled(false);
                        Wv_day.setEnabled(false);


                        ll_timer_main.setEnabled(false);
                        ll_timer_main.setClickable(false);
                        daysLayout.setEnabled(true);
                        daysLayout.setClickable(true);
                        repeat = 1;
                    }
                    String strName = arrayAdapter.getItem(which);
                    tv_repeate_once.setText(strName);
                });
                builderSingle.show();
            }
            break;
        }
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
            daystextView.setOnClickListener(v -> {
                if (repeat == 0)
                    return;

                if (daystextView.isSelected()) {
                    daystextView.setSelected(false);
                    Days[(int) daystextView.getTag()] = '0';
                } else {
                    daystextView.setSelected(true);
                    Days[finalDaysno] = '1';
                }
                Log.e("TAGG", "onClick: " + String.valueOf(Days));
            });

            if (isFromEditSchedule && repeat == 1) {
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

    void addSchedule() {
        try {
            if (isFromEditSchedule) {

                if (repeat == 1) {
                    if (!String.valueOf(Days).contains("1")) {
                        Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String _data = _gson.toJson(_schedule_model);
                Log.e("TAGGG", "Data Before " + _data);
                _schedule_model.get_lst_schedule().get(sr).setTime_M(selected_min);
                _schedule_model.get_lst_schedule().get(sr).setTime_H(selected_hour);
                _schedule_model.get_lst_schedule().get(sr).setRepeat(repeat);
                _schedule_model.get_lst_schedule().get(sr).setOutput(type);
                String date = selected_date + "";
                if (selected_date <= 9) {
                    date = "0" + selected_date;
                }

                String month = selected_mnth + "";
                if (selected_mnth <= 9) {
                    month = "0" + selected_mnth;
                }

                _schedule_model.get_lst_schedule().get(sr).setState(((int) tv_on.getTag()) == 1);
                _schedule_model.get_lst_schedule().get(sr).setDays(String.valueOf(Days));
                if (repeat == 0)
                    _schedule_model.get_lst_schedule().get(sr).setDate(date + "-" + month + "-" + currentYear);
                else
                    _schedule_model.get_lst_schedule().get(sr).setDate("00-00-0000");

                _schedule_model.get_lst_schedule().get(sr).setArm(Arm);

                _data = _gson.toJson(_schedule_model);
                Log.e("TAGGG", "Data Before Remove " + _data);
                showDialog(_data, null);
            } else {
                if (sr == 1) {
                    if (repeat == 1) {
                        if (!String.valueOf(Days).contains("1")) {
                            Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    JSONObject _json = new JSONObject();
                    _json.put("sn", 1);
                    _json.put("Time_H", selected_hour);
                    _json.put("Time_M", selected_min);
                    _json.put("Repeat", repeat);
                    _json.put("Output", type);
                    _json.put("state", ((int) tv_on.getTag()) == 1);
                    _json.put("Arm", Arm);

                    String date = selected_date + "";
                    if (selected_date <= 9) {
                        date = "0" + selected_date;
                    }

                    String month = selected_mnth + "";
                    if (selected_mnth <= 9) {
                        month = "0" + selected_mnth;
                    }

                    if (repeat == 0)
                        _json.put("Date", date + "-" + month + "-" + currentYear);
                    else
                        _json.put("Date", "00-00-0000");
                    _json.put("Days", String.valueOf(Days));
                    JSONObject _main = new JSONObject();
                    JSONArray _arr = new JSONArray();
                    _arr.put(_json);
                    _main.put("schedule", _arr);
                    Log.e("TAGGG", "Final Json Of CreateSchedule " + _main.toString());

                    String schedule_date = "";
                    String day = "";
                    if (repeat == 1) {
                        for (int i = 0; i < String.valueOf(Days).length(); i++) {
                            if (String.valueOf(Days).charAt(i) == '1') {
                                day = i + "";
                                break;
                            }
                        }
                        if (day.equalsIgnoreCase("0")) {
                            schedule_date = "Schedule Monday at " + selected_hour + ":" + selected_min;
                        } else if (day.equalsIgnoreCase("1"))
                            schedule_date = "Schedule Tuesday at " + selected_hour + ":" + selected_min;
                        else if (day.equalsIgnoreCase("2"))
                            schedule_date = "Schedule Wednesday at " + selected_hour + ":" + selected_min;
                        else if (day.equalsIgnoreCase("3"))
                            schedule_date = "Schedule Thursday at " + selected_hour + ":" + selected_min;
                        if (day.equalsIgnoreCase("4"))
                            schedule_date = "Next Schedule Friday at " + selected_hour + ":" + selected_min;
                        else if (day.equalsIgnoreCase("5"))
                            schedule_date = "Next Schedule Saturday at " + selected_hour + ":" + selected_min;
                        else if (day.equalsIgnoreCase("6"))
                            schedule_date = "Next Schedule Sunday at " + selected_hour + ":" + selected_min;
                    } else {
                        schedule_date = "Next Schedule " + _json.getString("Date") + " at " + selected_hour + ":" + selected_min;
                    }

                    Toast.makeText(this, schedule_date + "", Toast.LENGTH_SHORT).show();


                    Scheduler _obj = new Scheduler();
                    _obj.setDno(dno1);
                    _obj.setType(type + "");
                    _obj.setScheduler(schedule_date);
                    showDialog(_main.toString(), _obj);
                    Log.e("TAGG", "Data Before Add First Schedule " + _main.toString());
                } else {
                    if (repeat == 1) {
                        if (!String.valueOf(Days).contains("1")) {
                            Toast.makeText(this, "Select Atleast One Day.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    schedule_model_type_other.schedule _obj = new schedule_model_type_other.schedule();
                    _obj.setSerialNumber(sr);
                    _obj.setTime_H(selected_hour);
                    _obj.setTime_M(selected_min);
                    _obj.setRepeat(repeat);
                    _obj.setOutput(type);
                    _obj.setState(((int) tv_on.getTag()) == 1);
                    _obj.setDays(String.valueOf(Days));
//                _obj.setDate(syear + "-" + smonth + "-" + sdate);

                    String date = selected_date + "";
                    if (selected_date <= 9) {
                        date = "0" + selected_date;
                    }

                    String month = selected_mnth + "";
                    if (selected_mnth <= 9) {
                        month = "0" + selected_mnth;
                    }

                    if (repeat == 0)
                        _obj.setDate(date + "-" + month + "-" + currentYear);
                    else
                        _obj.setDate("00-00-0000");
                    _obj.setArm(Arm);
                    _schedule_model.get_lst_schedule().add(_obj);
                    String newList = _gson.toJson(_schedule_model);
                    Log.e("TAGGG", "New List " + newList);

                    String schedule_date = "";
                    int day = 0;

                    ArrayList<String> _date = new ArrayList<>();
                    for (int i = 0; i < _schedule_model.get_lst_schedule().size(); i++) {
                        if (_schedule_model.get_lst_schedule().get(i).getRepeat() == 1) {
                            for (int j = 0; j < _schedule_model.get_lst_schedule().get(i).getDays().length(); j++) {
                                if (_schedule_model.get_lst_schedule().get(i).getDays().charAt(j) == '1') {
                                    day = (j + 1);
                                    break;
                                }
                            }

                          /*  Calendar c = Calendar.getInstance(TimeZone.getDefault());
                            int diff = c.DAY_OF_WEEK - day;

                            c.add(Calendar.DATE, diff);
                            int mnth = checkDigit(c.get(Calendar.MONTH) + 1);
                            int dd = checkDigit(c.get(Calendar.DATE));
                            int yer = checkDigit(c.get(Calendar.YEAR));
                            _date.add(yer + "-" + mnth + "-" + dd);*/
                        } else {
                            _date.add(_schedule_model.get_lst_schedule().get(i).getDate());
                        }
                    }

                    Scheduler _object = new Scheduler();
                    _object.setDno(dno1);
                    _object.setType(type + "");
                    _object.setScheduler(schedule_date);

                    for (int i = 0; i < _date.size(); i++) {
                        Log.e("TAGG", "Date From List :- " + _date.get(i));
                    }

                    showDialog(newList, _object);
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at e " + e.getMessage());
        }
    }

    public void postSchedules(String infos) {
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + dno1 + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    void showDialog(String _object, Scheduler _obj) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateScheduleType_Other.this);

        if (isFromEditSchedule) {
            dialog.setTitle("Update Schedule");
            dialog.setMessage("Do you want to proceed now ?");
        } else {
            dialog.setTitle("New Schedule");
            dialog.setMessage("Do you want to proceed for new schedule ?");
        }

        dialog.setPositiveButton("Yes", (dialogInterface, is) -> {
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
                    if (_model_convert.get_lst_schedule().get(j).getOutput() == type) {

                        if (_model_convert.get_lst_schedule().get(j).getRepeat() == 1) {
                            c.setTime(sdf.parse(dt));

                            int selectedDay = (_model_convert.get_lst_schedule().get(j).getDays().indexOf("1")) + 1;
                            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

                            int diff = ((7 - dayOfWeek) + selectedDay);
                            c.add(Calendar.DATE, diff);
                            Log.e("TAG", "Both Day dayOfWeek " + dayOfWeek + " selectedDay " + selectedDay);
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
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
                }

                for (_model_next_schedule _object1 : _lst_dates) {
                    Log.e("TAG", "From List Before sort" + _object1.get_date());
                }

                Collections.sort(_lst_dates, (o1, o2) -> o1.get_date().compareTo(o2.get_date()));
                Log.e("TAG", "*************SORTING*****************");
                for (_model_next_schedule _object1 : _lst_dates) {
                    Log.e("TAG", "From List After sort >> " + _object1.get_date() + " " + _object1.get_time());
                }
                Constants.savetoShared(CreateScheduleType_Other.this).edit().putString(dno1 + "_" + type, (getResources().getString(R.string.work_next) + _lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
            } catch (Exception e) {
                Log.e("TAG", "Exception at add schedule " + e.getMessage());
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("newList", _object);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    class _model_next_schedule {
        public String _date;
        public String _time;
        public String _type;

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

}

