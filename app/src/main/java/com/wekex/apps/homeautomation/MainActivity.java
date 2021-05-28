package com.wekex.apps.homeautomation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;
    private Intent intent;
    private EditText textMessage, subscribeTopic, unSubscribeTopic;
    private Button publishMessage, subscribe, unSubscribe, getDevices;
    String dno;
    private Switch switch1;
    private String user;
    private String pass;
    private String devices;
    String isOnline;
    LinearLayout linearLayout;
    TextView log;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log = findViewById(R.id.log);
        user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        pass = Constants.savetoShared(this).getString(Constants.PASSWORD, "0");
        if (user.equals("0") || pass.equals("0")) {
            Log.d(TAG, "onCreate: Password or Username is not registered in shared preferences");
            finish();
        }
        pahoMqttClient = new PahoMqttClient();
        linearLayout = findViewById(R.id.linearLayout);


        textMessage = (EditText) findViewById(R.id.textMessage);
        publishMessage = (Button) findViewById(R.id.publishMessage);

        subscribe = (Button) findViewById(R.id.subscribe);
        unSubscribe = (Button) findViewById(R.id.unSubscribe);

        subscribeTopic = (EditText) findViewById(R.id.subscribeTopic);
        unSubscribeTopic = (EditText) findViewById(R.id.unSubscribeTopic);
        getDevices = findViewById(R.id.getDevices);
        // client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        client = pahoMqttClient.getMqttClientAuthenticate(getApplicationContext(), Constants.MQTT_BROKER_URL, user, pass);
        Constants.GeneralpahoMqttClient = client;
        startService(new Intent(this, MqttMessageService.class));
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                // setLog(s+" connextion "+b);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                // setLog("Lost "+throwable);
            }


            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.d(TAG, s + "messageArrived: " + new String(mqttMessage.getPayload()));
                String response = new String(mqttMessage.getPayload());
                setLog(response);


                String jsonc = jsonObjectreader(response, "data");
                if (jsonc.equals("error")) {
                    devices = new String(mqttMessage.getPayload());
                    setLog(devices);
                    dno = jsonObjectreader(devices, "dno");
                } else {
                    setLog(jsonc);
                    devices = jsonArrayreader(jsonc, 0);
                    setLog(devices);
                    dno = jsonObjectreader(devices, "dno");
                    isOnline = jsonObjectreader(devices, "isOnline");
                    setLog("Suscribe to " + "d/" + dno + "/#");
                    // if(Constants.savetoShared(getBaseContext()).getBoolean(dno,true)) {
                    //  pahoMqttClient.subscribe(client, "d/" + dno + "/#", 1);
                    Constants.savetoShared(getBaseContext()).edit().putBoolean(dno, false).apply();
                    // }
                }
                init();


                //setLog(new String(mqttMessage.getPayload()));
                //init();
                //setMessageNotification(s, new String(mqttMessage.getPayload()));
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textMessage.getText().toString().trim();
                if (!msg.isEmpty()) {
                    try {
                        pahoMqttClient.publishMessage(client, msg, 1, Constants.PUBLISH_TOPIC);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pahoMqttClient.subscribe(client, "u/" + user + "/sub", 1);
                    getDevices.setVisibility(View.VISIBLE);
                    findViewById(R.id.deciveList).setVisibility(View.VISIBLE);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                String topic = subscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {

                }
            }
        });
        unSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pahoMqttClient.unSubscribe(client, Constants.SUSCRIBE_TOPIC);
                } catch (MqttException e) {
                    Log.d(TAG, "onClick: " + e.getMessage());
                    e.printStackTrace();
                }
                String topic = unSubscribeTopic.getText().toString().trim();
                if (!topic.isEmpty()) {

                }
            }
        });

        getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pahoMqttClient.publishMessage(client, "{ \"method\" : \"getDevice\" }", 1, "u/" + user + "/pub");
                    setLog("{ \"method\" : \"getDevice\" }");
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        switch1 = findViewById(R.id.switch1);

        /*switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: "+isChecked);
                try {
                    pahoMqttClient.publishMessage(client, "{  \"dno\": \"996688\",  \"key\": \"996688\",  \"dtype\": \"1\",  \"d1\":{    \"state\": "+isChecked+"}}", 1, "d/996688/sub");
                } catch (MqttException e) {
                    setLog(e.getMessage());
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    setLog(e.getMessage());
                    e.printStackTrace();
                }
            }
        });*/

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);
    }

    private void init() {
        linearLayout.setVisibility(View.VISIBLE);
        String status = jsonObjectreader(devices, "d1");
        setLog("D1 State " + jsonObjectreader(status, "state"));
        TextView on = findViewById(R.id.online);
        on.setText(isOnline + " D1 " + jsonObjectreader(status, "state"));
        TextView tv = findViewById(R.id.devicename);

        tv.setText(jsonObjectreader(status, "name"));


        switch1.setOnCheckedChangeListener(null);
        switch1.setChecked(Boolean.parseBoolean(jsonObjectreader(status, "state")));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);

                try {
                    setLog("publishing " + "{  \"dno\": \"" + dno + "\",  \"key\": \"" + dno + "\",  \"dtype\": \"1\",  \"d1\":{    \"state\": " + isChecked + "}} qos 0" + "d/" + dno + "/sub");
                    pahoMqttClient.publishMessage(client, "{  \"dno\": \"" + dno + "\",  \"key\": \"" + dno + "\",  \"dtype\": \"1\",  \"d1\":{    \"state\": " + isChecked + "}}", 1, "d/" + dno + "/sub");
                } catch (MqttException e) {
                    setLog(e.getMessage());
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    setLog(e.getMessage());
                    e.printStackTrace();
                }

            }
        });
    }

    private String jsonObjectreader(String devices, String d1) {
        try {
            JSONObject jsonObject = new JSONObject(devices);
            return jsonObject.get(d1).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }

    }

    private String jsonArrayreader(String devices, int no) {
        try {
            JSONArray jsonArray = new JSONArray(devices);
            return jsonArray.get(no).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public void pub(MqttAndroidClient client1, String msg) {
        try {
            pahoMqttClient.publishMessage(client1, msg, 1, "d/996688/sub");
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(Intent intent) {

        String state = intent.getStringExtra("toggle");
        Log.d(TAG, "updateUI: " + state);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        if (dno != null) {
            Constants.savetoShared(getBaseContext()).edit().putBoolean(dno, true).apply();
        }

        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dno != null) {
            Constants.savetoShared(getBaseContext()).edit().putBoolean(dno, true).apply();
        }

    }

    public void setLog(String message) {
        String text = log.getText().toString();
        log.setText(message + "\n" + text);
    }

    public void smartConfig(View view) {
        startActivity(new Intent(MainActivity.this, SmartConfig.class));

    }

    public void clear(View view) {
        log.setText("");
    }

    public void devices(View view) {
        startActivity(new Intent(this, DeviceList.class));
    }
}
