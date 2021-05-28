package com.wekex.apps.homeautomation.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.Interfaces.SchedulerOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Sqlite.Scheduler;
import com.wekex.apps.homeautomation.adapter.ScheduleAdapterTypeOther;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.schedule_model;
import com.wekex.apps.homeautomation.model.schedule_model_type_other;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleList extends AppCompatActivity implements SchedulerOperation {

    Button btn_add_new_schedule;
    Toolbar toolbar;

    String dno1;
    int type;
    public static SchedulerOperation obj_interface;
    Gson _gson = new Gson();

    ProgressDialog _p_dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Schedule");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _p_dialog = new ProgressDialog(ScheduleList.this);
        _p_dialog.setCancelable(true);
        _p_dialog.setCanceledOnTouchOutside(true);
        obj_interface = this;
        rv_schedule_list = findViewById(R.id.rv_schedule_list);
        rv_schedule_list.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("dno")) {
            dno1 = getIntent().getStringExtra("dno");
        }

        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }


        btn_add_new_schedule = (Button) findViewById(R.id.btn_add_new_schedule);
        btn_add_new_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sr = 0;

                if (schedule_data_filtered != null && schedule_data_filtered.get_lst_schedule() != null && schedule_data_filtered.get_lst_schedule().size() >= 4) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ScheduleList.this, android.R.style.Theme_Material_Dialog_Alert);
                    dialog.setTitle("Limit exceeded");
                    dialog.setMessage("You can add only 4 schedule, please delete existing schedule to add new schedule.");
                    dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.show();
                    return;
                }

                if (schedule_data != null) {
                    if (schedule_data.get_lst_schedule() != null && schedule_data.get_lst_schedule().size() != 0) {
                        sr = schedule_data.get_lst_schedule().size() + 1;
                    } else
                        sr = 1;
                } else
                    sr = 1;

                Intent _intent = new Intent(ScheduleList.this, CreateScheduleType_Other.class);
                String _list = _gson.toJson(schedule_data);
                if (schedule_data != null && schedule_data.get_lst_schedule() != null && schedule_data.get_lst_schedule().size() > 0)
                    _intent.putExtra("_list", _list);
                _intent.putExtra("dno", dno1);
                _intent.putExtra("type", type);
                _intent.putExtra("SrNo", sr);
                startActivityForResult(_intent, 100);
            }
        });

        showDialog();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGGG", "BroadCastReceiver Called ");
            String Rdata = intent.getStringExtra("datafromService");
//            Toast.makeText(context, Rdata + "", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //DtypeViews.getGetDevice(this);
        Log.e("TAG", "onResume: ");
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));

        getSchedules();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getSchedules() {
        try {
            Log.e("TAGG", "getSchedule Called dno1 " + dno1);
            JSONObject _obj = new JSONObject();
            _obj.put("getSch", 1);
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    _obj.toString(),
                    1,
                    "d/" + dno1 + "/sub");
        } catch (Exception e) {
            Log.e("TAGG", "Exception at getSchedule " + e.getMessage(), e);
        }
    }

    @Override
    public void getSchedules(String scheduleData) {
        Log.e("TAG", "getSchedule called scheduleData " + scheduleData);
        if (_schedule_adapter == null)
            fillScheduleData(scheduleData);
    }

    @Override
    public void deleteSchedule(int pos) {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(this);
        }
        builder.setTitle("Remove Schedule")
                .setMessage("Are you sure you want to Remove this schedule ?")
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        schedule_data_filtered.get_lst_schedule().remove(pos);
//                        String newList = _gson.toJson(schedule_data_filtered);
//                        postSchedules(newList);
                        schedule_model_type_other.schedule _obj = schedule_data_filtered.get_lst_schedule().get(pos);
                        for (int i = 0; i < schedule_data.get_lst_schedule().size(); i++) {

                            if (schedule_data.get_lst_schedule().get(i).getDays().equalsIgnoreCase(_obj.getDays())) {
                                if (schedule_data.get_lst_schedule().get(i).getDate().equalsIgnoreCase(_obj.getDate()))
                                    if (schedule_data.get_lst_schedule().get(i).getTime_H() == _obj.getTime_H())
                                        if (schedule_data.get_lst_schedule().get(i).getTime_M() == _obj.getTime_M()) {
                                            schedule_data.get_lst_schedule().remove(i);
                                            String newList = _gson.toJson(schedule_data);
                                            postSchedules(newList);
                                            fillScheduleData(newList);

                                            break;
                                        }
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void editSchedule(int pos) {

        schedule_model_type_other.schedule _obj = schedule_data_filtered.get_lst_schedule().get(pos);
        for (int i = 0; i < schedule_data.get_lst_schedule().size(); i++) {

            if (schedule_data.get_lst_schedule().get(i).getDays().equalsIgnoreCase(_obj.getDays())) {
                if (schedule_data.get_lst_schedule().get(i).getDate().equalsIgnoreCase(_obj.getDate()))
                    if (schedule_data.get_lst_schedule().get(i).getTime_H() == _obj.getTime_H())
                        if (schedule_data.get_lst_schedule().get(i).getTime_M() == _obj.getTime_M()) {

                            Intent _intent = new Intent(ScheduleList.this, CreateScheduleType_Other.class);
                            Gson gson = new Gson();
                            String _list = gson.toJson(schedule_data);
                            _intent.putExtra("_list", _list);
                            _intent.putExtra("dno", dno1);
                            _intent.putExtra("SrNo", i);
                            _intent.putExtra("isFromEditSchedule", true);
                            startActivityForResult(_intent, 100);

                            break;
                        }
            }
        }


    }

    @Override
    public void updateSchedule(int pos) {
//        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                schedule_model_type_other.schedule _obj = schedule_data_filtered.get_lst_schedule().get(pos);
                for (int i = 0; i < schedule_data.get_lst_schedule().size(); i++) {
                    if (schedule_data.get_lst_schedule().get(i).getDays().equalsIgnoreCase(_obj.getDays())) {
                        if (schedule_data.get_lst_schedule().get(i).getDate().equalsIgnoreCase(_obj.getDate()))
                            if (schedule_data.get_lst_schedule().get(i).getTime_H() == _obj.getTime_H())
                                if (schedule_data.get_lst_schedule().get(i).getTime_M() == _obj.getTime_M()) {
                                    schedule_data.get_lst_schedule().get(i).setState(!schedule_data.get_lst_schedule().get(i).isState());
                                    String newList = _gson.toJson(schedule_data);
                                    postSchedules(newList);
                                    _schedule_adapter.notifyDataSetChanged();
                                    Log.e("TAG", "New List " + newList);
                                    break;
                                }
                    }
                }
            }
        }, 500);
    }

    schedule_model_type_other schedule_data_filtered = new schedule_model_type_other();
    ScheduleAdapterTypeOther _schedule_adapter;
    RecyclerView rv_schedule_list;
    schedule_model_type_other schedule_data;

    void fillScheduleData(String _scheduleData) {
        try {
            hideDialog();
            schedule_data = _gson.fromJson(_scheduleData, schedule_model_type_other.class);
            ArrayList<schedule_model_type_other.schedule> _lst = new ArrayList<schedule_model_type_other.schedule>();

            if (schedule_data == null)
                return;

            for (int i = 0; i < schedule_data.get_lst_schedule().size(); i++) {
                if (schedule_data.get_lst_schedule().get(i).getOutput() == type) {
                    _lst.add(schedule_data.get_lst_schedule().get(i));
                }
            }

            latestSchedule(_lst);

            schedule_data_filtered.set_lst_schedule(_lst);
            _schedule_adapter = new ScheduleAdapterTypeOther(ScheduleList.this, schedule_data_filtered, this);
            rv_schedule_list.setAdapter(_schedule_adapter);

            if (_lst.size() == 0) {
                Constants.savetoShared(ScheduleList.this).edit().putString(dno1 + "_" + type, "").apply();
                Toast.makeText(this, "Scheduler not found!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                //setup latest scheduler
            }

        } catch (Exception e) {
            Log.e("TAGG", "Exception at fill schedule " + e.getMessage(), e);
            hideDialog();
        }
    }

    public void postSchedules(String infos) {
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + dno1 + "/sub");
        } catch (MqttException e) {
            Log.e("TAG", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("TAG", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        obj_interface = null;
    }

    void showDialog() {
        _p_dialog.setMessage("Please Wait..");
        _p_dialog.show();
    }

    void hideDialog() {
        try {
            if (_p_dialog.isShowing())
                _p_dialog.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String lst = data.getStringExtra("newList");
            fillScheduleData(lst);
        }
    }

    void latestSchedule(ArrayList<schedule_model_type_other.schedule> _lst) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String dt = df.format(c.getTime());
        // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
//            schedule_model _model_convert = _gson.fromJson(_object, schedule_model.class);
            Log.e("TAG", "Schedule size " + _lst.size());
            ArrayList<_model_next_schedule> _lst_dates = new ArrayList<>();
            for (int j = 0; j < _lst.size(); j++) {
                if (_lst.get(j).getRepeat() == 1) {
                    c.setTime(sdf.parse(dt));

                    /*int k = (_model_convert.get_lst_schedule().get(j).getDays().indexOf("1")) + 1;
                    int day = c.get(Calendar.DAY_OF_WEEK) - 1;
*/
                    int selectedDay = (_lst.get(j).getDays().indexOf("1")) + 1;
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

                    int diff = ((7 - dayOfWeek) + selectedDay);
                    c.add(Calendar.DATE, diff);
                    Log.e("TAG", "Both Day dayOfWeek " + dayOfWeek + " selectedDay " + selectedDay);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                    String output = sdf1.format(c.getTime());
                    Log.e("TAG", "Both Day dayOfWeek Date " + output);
                    _model_next_schedule obj = new _model_next_schedule(output, _lst.get(j).getTime_H() + ":" + _lst.get(j).getTime_M(), (_lst.get(j).getRepeat()) + "");
                    _lst_dates.add(obj);

                } else {
                    _model_next_schedule obj = new _model_next_schedule(_lst.get(j).getDate(), _lst.get(j).getTime_H() + ":" + _lst.get(j).getTime_M(), (_lst.get(j).getRepeat()) + "");
                    _lst_dates.add(obj);
                    Log.e("TAG", "Schedule Added in list " + _lst.get(j).getDate());
                }
            }

            for (_model_next_schedule _object : _lst_dates) {
                Log.e("TAG", "From List Before sort <> " + _object.get_date());
            }

            Collections.sort(_lst_dates, new Comparator<_model_next_schedule>() {
                public int compare(_model_next_schedule o1, _model_next_schedule o2) {
                    return o1.get_date().compareTo(o2.get_date());
                }
            });

            Log.e("TAG", "*************SORTING*****************");

            for (_model_next_schedule _object_schedule : _lst_dates) {
                Log.e("TAG", "From List After sort <> " + _object_schedule.get_date());
            }

            if (_lst_dates.size() == 0)
                Constants.savetoShared(ScheduleList.this).edit().putString(dno1 + "_" + type, "").apply();
            else {
//                Constants.savetoShared(ScheduleList.this).edit().putString(dno1 + "_" + type, (_lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
                Constants.savetoShared(ScheduleList.this).edit().putString(dno1 + "_" + type, (getResources().getString(R.string.work_next) + _lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at add schedule " + e.getMessage());
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }
}
