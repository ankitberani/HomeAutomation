package com.wekex.apps.homeautomation.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.CustomRemoteKeyAdapter;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.ir_remotes;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;

public class RemoteKeyActivity extends AppCompatActivity {


    String _dno;
    String _type = "";
    String brand = "";
    String model = "";
    Toolbar toolbar;

    String ButtonClicked;
    String mode = "0";

    String TAG = "RemoteActivity";
    ProgressDialog progressDialog;
    ir_remotes receivedObject = new ir_remotes();

    Gson _gson = new Gson();

    LinearLayout ll_exit, ll_menu, ll_shownum, ll_record, ll_channel_up, ll_channel_down, ll_vol_up, ll_vol_down, ll_more;

    TextView tv_cursor_left, tv_cursor_down, tv_cursor_up;
    TextView tv_cursor_enter, tv_cursor_right;

    private Dialog number_pad;
    View numberpad;

    TextView tv_learn;

    int pos = -1;
    Utility _utility;

    ImageView iv_power, iv_exit, iv_menu, iv_rec, iv_ch_up, iv_ch_down, iv_vol_up, iv_vol_down, iv_more, iv_mute;

    TextView tv_dig_0, tv_dig_1, tv_dig_2, tv_dig_3, tv_dig_4, tv_dig_5, tv_dig_6, tv_dig_7, tv_dig_8, tv_dig_9, tv_dig_dash;

    String remote_data = "";

    String remote_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_key);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        _utility = new Utility(RemoteKeyActivity.this);

        if (getIntent() != null) {
            if (getIntent().hasExtra("_dno"))
                _dno = getIntent().getStringExtra("_dno");
            if (getIntent().hasExtra("pos"))
                pos = getIntent().getIntExtra("pos", -1);
            if (getIntent().hasExtra("name")) {
                toolbar.setTitle(getIntent().getStringExtra("name"));
                remote_name = getIntent().getStringExtra("name");
                receivedObject.setName(remote_name);
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.e("TAG", "Activity OnCreate Called");
        initialize();

        if (getIntent() != null && getIntent().hasExtra("data"))
            remote_data = getIntent().getStringExtra("data");

        tv_learn = findViewById(R.id.tv_learn);
        tv_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(RemoteKeyActivity.this, LearnRemoteKeyActivity.class);
                _intent.putExtra("_name", getIntent().getStringExtra("name"));
                _intent.putExtra("remote_data", remote_data);
                _intent.putExtra("_dno", _dno);
                _intent.putExtra("pos", pos);
                startActivity(_intent);
            }
        });

        if (remote_data != null && !remote_data.isEmpty()) {
            receivedObject = _gson.fromJson(remote_data, ir_remotes.class);
            if (receivedObject != null) {
                _type = receivedObject.getR_Type();
                model = receivedObject.getR_MOdel();
                brand = receivedObject.getR_Brand();
                remote_name = receivedObject.getName();
            }

            for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
                unableButton(receivedObject.get_lst_key().get(j).getKey());
            }
        }

        ll_shownum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_umber_pad();
            }
        });


    }

    void initialize() {
        iv_power = findViewById(R.id.iv_power);
        iv_exit = findViewById(R.id.iv_exit);

        tv_cursor_enter = findViewById(R.id.tv_cursor_enter);
        tv_cursor_right = findViewById(R.id.tv_cursor_right);
        iv_menu = findViewById(R.id.iv_menu);
        iv_rec = findViewById(R.id.iv_rec);
        iv_ch_up = findViewById(R.id.iv_ch_up);
        iv_ch_down = findViewById(R.id.iv_ch_down);
        iv_vol_up = findViewById(R.id.iv_vol_up);
        iv_vol_down = findViewById(R.id.iv_vol_down);
        iv_mute = findViewById(R.id.iv_mute);
        iv_more = findViewById(R.id.iv_more);

        numberpad = LayoutInflater.from(this).inflate(R.layout.remote_number_pad, null, false);
        number_pad = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);


        tv_cursor_up = findViewById(R.id.tv_cursor_up);
        tv_cursor_left = findViewById(R.id.tv_cursor_left);
        tv_cursor_down = findViewById(R.id.tv_cursor_down);


        ll_vol_up = findViewById(R.id.ll_vol_up);
        ll_vol_down = findViewById(R.id.ll_vol_down);
        ll_exit = findViewById(R.id.ll_exit);
        ll_menu = findViewById(R.id.ll_menu);
        ll_shownum = findViewById(R.id.ll_shownum);
        ll_record = findViewById(R.id.ll_record);
        ll_channel_up = findViewById(R.id.ll_channel_up);
        ll_channel_down = findViewById(R.id.ll_channel_down);
        ll_more = findViewById(R.id.ll_more);

    }

    public void dialog_umber_pad() {
        number_pad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = number_pad.getWindow();
        number_pad.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        number_pad.setContentView(numberpad);
        number_pad.show();

        tv_dig_0 = number_pad.findViewById(R.id.tv_0);
        tv_dig_1 = number_pad.findViewById(R.id.tv_1);
        tv_dig_2 = number_pad.findViewById(R.id.tv_2);
        tv_dig_3 = number_pad.findViewById(R.id.tv_3);
        tv_dig_4 = number_pad.findViewById(R.id.tv_4);
        tv_dig_5 = number_pad.findViewById(R.id.tv_5);
        tv_dig_6 = number_pad.findViewById(R.id.tv_6);
        tv_dig_7 = number_pad.findViewById(R.id.tv_7);
        tv_dig_8 = number_pad.findViewById(R.id.tv_8);
        tv_dig_9 = number_pad.findViewById(R.id.tv_9);
        tv_dig_dash = number_pad.findViewById(R.id.tv_dash);

        for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
            enableDigKey(receivedObject.get_lst_key().get(j).getKey());
        }

    }

    void enableDigKey(String _key) {
        switch (_key) {
            case "DIGIT 0": {
                tv_dig_0.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 1": {
                tv_dig_1.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 2": {
                tv_dig_2.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 3": {
                tv_dig_3.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 4": {
                tv_dig_4.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 5": {
                tv_dig_5.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 6": {
                tv_dig_6.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 7": {
                tv_dig_7.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 8": {
                tv_dig_8.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "DIGIT 9": {
                tv_dig_9.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
            case "num_dash": {
                tv_dig_dash.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            break;
        }
    }

    public void remoteNumbClick(View v) {
        if (v.getTag().toString().equals("close")) {
            number_pad.dismiss();
            return;
        }
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
        Log.e("TAG", "Button Clicked " + ButtonClicked);
        for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
            if (receivedObject.get_lst_key().get(j).getKey().equalsIgnoreCase(v.getTag().toString())) {
                String value = receivedObject.get_lst_key().get(j).getValue();
                RemotClickConfigure(receivedObject.getFormat(), _dno, "irs", value);
                return;
            }
        }

//        sendKey();
        showLearnDialog();
    }

    public void remoteClick(View v) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        if (mode.equals("1"))
            Log.d(TAG, "remoteClick: ");
            //Toast.makeText(this, "You have Pressed "+v.getTag().toString(), Toast.LENGTH_SHORT).show();
        else if (mode.equals("0")) {
            ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
            Log.e("TAG", "Button Clicked " + ButtonClicked);
            try {
                for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
                    if (receivedObject.get_lst_key().get(j).getKey().equalsIgnoreCase(v.getTag().toString())) {
                        String value = receivedObject.get_lst_key().get(j).getValue();
                        RemotClickConfigure(receivedObject.getFormat(), _dno, "irs", value);
                        return;
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "Exception at RemoteKeyAct " + e.getMessage());
            }
            showLearnDialog();
        } else if (mode.equals("2")) {
            ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
            Log.e("TAG", "Button Clicked " + ButtonClicked);

            for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
                if (receivedObject.get_lst_key().get(j).getKey().equalsIgnoreCase(v.getTag().toString())) {
                    String value = receivedObject.get_lst_key().get(j).getValue();
                    RemotClickConfigure(receivedObject.getFormat(), _dno, "irs", value);
                    return;
                }
            }

            showLearnDialog();
        }
    }

    void showLearnDialog() {
        new AlertDialog.Builder(RemoteKeyActivity.this).setTitle("Code Not Found").setMessage("Click on Learn(Top Right) for better experience.").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));

        if (!_utility.getString(receivedObject.getName()).isEmpty()) {
            TypeToken<ArrayList<Model_UserRemoteList.keys>> token = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
            };
            ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(receivedObject.getName()), token.getType());
            for (int i = 0; i < _remote_key_data.size(); i++) {
                if (!isAdded(_remote_key_data.get(i).getKey())) {
                    receivedObject.get_lst_key().add(_remote_key_data.get(i));
                    unableButton(_remote_key_data.get(i).getKey());
                    Log.e("TAG", "Unable Button " + _remote_key_data.get(i).getKey());
                } else {
                    for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
                        Log.e("TAG", "Compare Value " + _remote_key_data.get(i).getKey() + " <> " + receivedObject.get_lst_key().get(j).getKey());
                        if (_remote_key_data.get(i).getKey().equalsIgnoreCase(receivedObject.get_lst_key().get(j).getKey())) {
                            receivedObject.get_lst_key().get(j).setValue(receivedObject.get_lst_key().get(j).getValue());
                            Log.e("TAG", "Key found and Set " + receivedObject.get_lst_key().get(j).getKey() + " " + receivedObject.get_lst_key().get(j).getValue());
                            break;
                        }
                    }
                }
            }
        }
        setupCustomKey();
    }

    void unableButton(String KeyName) {
        Log.e("TAG", "Unable Button " + KeyName);
        switch (KeyName) {
            case "POWER":
                iv_power.setImageResource(R.drawable.power_btn_active);
                break;
            case "EXIT":
                iv_exit.setColorFilter(R.color.colorAccent);
                break;
            case "MENU":
                iv_menu.setImageResource(R.drawable.remote_menu_active);
                break;
            case "REC":
                iv_menu.setImageResource(R.drawable.record_icon_active);
                break;
            case "CHANNEL UP":
                iv_ch_up.setImageResource(R.drawable.remote_up_active);
                break;
            case "CHANNEL DOWN":
                iv_ch_down.setImageResource(R.drawable.remote_down_active);
                break;
            case "VOLUME UP":
                iv_vol_up.setImageResource(R.drawable.remote_add_active);
                break;
            case "VOLUME DOWN":
                iv_vol_down.setImageResource(R.drawable.remote_minus_active);
                break;
            case "MORE":
                iv_more.setImageResource(R.drawable.remote_more_active);
                break;
            case "MUTE":
                iv_mute.setImageResource(R.drawable.remote_mute_active);
                break;

            case "CURSOR UP":
                tv_cursor_up.setBackgroundResource(R.drawable.key_up_active);
                break;

            case "CURSOR DOWN":
                tv_cursor_down.setBackgroundResource(R.drawable.key_down_active);
                break;

            case "CURSOR LEFT":
                tv_cursor_left.setBackgroundResource(R.drawable.key_left_active);
                break;

            case "CURSOR RIGHT":
                tv_cursor_right.setBackgroundResource(R.drawable.key_right_active);
                break;

        }
    }

    public boolean isAdded(String keyName) {
        for (int j = 0; j < receivedObject.get_lst_key().size(); j++) {
            if (keyName.equalsIgnoreCase(receivedObject.get_lst_key().get(j).getKey()))
                return true;
        }
        return false;
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void RemotClickConfigure(String format, String dnoValue, String irl, String Value) {

        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "format", format);
        jsonObjectPut(jsonObject1, "channel", irl);
        jsonObjectPut(jsonObject1, "data", Value);
        Log.e("TAG", "RemotClickConfigure dno " + dnoValue + " key " + ButtonClicked + " channel " + irl + " topic " + "d/" + _dno + "/sub");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, jsonObject1.toString(),
                    1,
                    "d/" + dnoValue + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    public void setupCustomKey() {
        try {

            RecyclerView rv_custom_key = (RecyclerView) findViewById(R.id.rv_custome_remote);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rv_custom_key.setLayoutManager(mLayoutManager);
            TextView tv_head_custom_key = findViewById(R.id.tv_head_custom_key);
            TypeToken<ArrayList<Model_UserRemoteList.keys>> token_key = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
            };
            ArrayList<Model_UserRemoteList.keys> lst_remote_key = _gson.fromJson(_utility.getString(remote_name + "_custom"), token_key.getType());
            Log.e("TAG", "Button Name at RemoteKey " + remote_name + "_custom" + " data " + _utility.getString(remote_name + "_custom"));
            if (lst_remote_key != null && lst_remote_key.size() > 0) {
                CustomRemoteKeyAdapter _adapter = new CustomRemoteKeyAdapter(lst_remote_key, this, null, _listeners);
                rv_custom_key.setAdapter(_adapter);
                tv_head_custom_key.setVisibility(View.VISIBLE);
                rv_custom_key.setVisibility(View.VISIBLE);
            } else {
                tv_head_custom_key.setVisibility(View.GONE);
                rv_custom_key.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at setup Custom key " + e.getMessage());
        }
    }

    View.OnClickListener _listeners = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Model_UserRemoteList.keys _key = (Model_UserRemoteList.keys) view.getTag();
//            remoteClick(view);
            try {
                if (_key != null && _key.getValue() != null && !_key.getValue().isEmpty())
                    RemotClickConfigure("30", _dno, "irs", _key.getValue());
                else
                    showLearnDialog();
            } catch (Exception e) {
                Log.e("TAG", "Exception at RemoteKeyAct " + e.getMessage());
            }
        }
    };
}
