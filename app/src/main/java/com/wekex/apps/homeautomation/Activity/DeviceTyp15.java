package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DeviceTyp15 extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView tv_progress, tv1, tv2, tv3, tv4;
    Croller croller;
    double br = 0;

    String _dno = "", type = "15", key = "", state = "";

    Switch switch1_d1;
    ImageView _iv_fan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_typ15);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("dname"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _iv_fan = (ImageView) findViewById(R.id.iv_fan);

        br = getIntent().getDoubleExtra("br", 0);

        if (getIntent().hasExtra("dno"))
            _dno = getIntent().getStringExtra("dno");

        if (getIntent().hasExtra("key"))
            key = getIntent().getStringExtra("key");

        if (getIntent().hasExtra("state"))
            state = getIntent().getStringExtra("state");

        switch1_d1 = findViewById(R.id.switch1);
        if (state.equalsIgnoreCase("true")) {
            Glide.with(this)
                    .load(R.drawable.animated_fan)
                    .into(_iv_fan);
            switch1_d1.setChecked(true);
        } else {
            _iv_fan.setImageResource(R.drawable.animated_fan);
            switch1_d1.setChecked(false);
        }

        switch1_d1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("TAG", "Switch State " + b);

                TurnOnOffDevice();

            }
        });

        croller = (Croller) findViewById(R.id.croller);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        tv3 = (TextView) findViewById(R.id.tv_3);
        tv4 = (TextView) findViewById(R.id.tv_4);

        tv4.setOnClickListener(this::onClick);
        tv3.setOnClickListener(this::onClick);
        tv2.setOnClickListener(this::onClick);
        tv1.setOnClickListener(this::onClick);

        int progress = 0;
        if (br == 1) {
            progress = 4;
        } else if (br == 0.75) {
            progress = 3;
        } else if (br == 0.50) {
            progress = 2;
        } else
            progress = 1;

        croller.setProgress(progress);
        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                // use the progress
                tv_progress.setText((progress) + "");
                Log.e("TAG", "onProgressChanged progress " + progress);
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                // tracking stopped
                publishSeekbar();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_1:
                croller.setProgress(1);
                publishSeekbar();
                break;
            case R.id.tv_2:
                croller.setProgress(2);
                publishSeekbar();
                break;
            case R.id.tv_3:
                croller.setProgress(3);
                publishSeekbar();
                break;
            case R.id.tv_4:
                croller.setProgress(4);
                publishSeekbar();
                break;

        }
    }

    void publishSeekbar() {
        JSONObject jsonObjectD1 = new JSONObject();
        JSONObject jsonObjectMain = new JSONObject();
        String _json = "";
        try {
            jsonObjectD1.put("state", state);
            jsonObjectD1.put("br", getProgress());
            jsonObjectMain.put("dno", _dno);
            jsonObjectMain.put("key", key);
            jsonObjectMain.put("dtype", type);
            jsonObjectMain.put("d1", jsonObjectD1);
            _json = jsonObjectMain.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    _json,
                    1,
                    "d/" + _dno + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.e("TAG", e.getMessage());
            e.printStackTrace();
        }
    }

    double getProgress() {
        if (croller.getProgress() == 4) {
            return 1;
        } else if (croller.getProgress() == 3) {
            return 0.75;
        } else if (croller.getProgress() == 2) {
            return 0.50;
        } else if (croller.getProgress() == 1) {
            return 0.25;
        }
        return 0.25;
    }

    public void TurnOnOffDevice() {

        //showProgressDialog(getResources().getString(R.string.please_wait));
        try {
            if (state.equalsIgnoreCase("false"))
                state = "true";
            else
                state = "false";

            JsonObject _obj = new JsonObject();
            _obj.addProperty("dno", _dno);
            _obj.addProperty("key", key);
            _obj.addProperty("dtype", "15");
            JsonObject object = new JsonObject();
            object.addProperty("state", state);
            _obj.add("d1", object);

            /*filteredList.getObjData().get(pos).getObjd1().setState(state);
            _device_adapter.notifyItemChanged(pos);*/
            Gson gson = new Gson();
            String json = gson.toJson(_obj);
            Log.e("TURN_ON_OFF_DEVICE", "Object in String " + json);

            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    json,
                    1,
                    "d/" + _dno + "/sub");


            if (state.equalsIgnoreCase("true")) {
                Glide.with(this)
                        .load(R.drawable.animated_fan)
                        .into(_iv_fan);
                switch1_d1.setChecked(true);
            } else {
                _iv_fan.setImageResource(R.drawable.animated_fan);
                switch1_d1.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAGGG", "Exception at TurnOnOffDevice Devices " + e.getMessage(), e);
        }
    }
}