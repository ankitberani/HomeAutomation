package com.wekex.apps.homeautomation.helperclass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.wekex.apps.homeautomation.Activity.ScheduleList;
import com.wekex.apps.homeautomation.Graphview;
import com.wekex.apps.homeautomation.Interfaces.MyListener;
import com.wekex.apps.homeautomation.MainActivity;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.RoomActivity;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MqttMessageService extends Service {
    public static final String BROADCAST_ACTION = "com.wekex.apps.homeautomation.displayevent";
    private static final String TAG = "MqttMessageService";
    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;
    Intent intent;

    public MqttMessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        Log.d(TAG, "onCreate");

        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String pass = Constants.savetoShared(this).getString(Constants.PASSWORD, "0");

        Log.e("TAGGG", "MQTT Cred user " + user + " pass " + pass);
        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClientAuthenticate(getApplicationContext(), Constants.MQTT_BROKER_URL, user, pass);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.e("TAGGG", "MQTT Cred user connect complete");
                pushLog("MQTT Connected Successfully ");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                pushLog("MQTT Disconnected! ");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //setMessageNotification(s, new String(mqttMessage.getPayload()));
                String msg = new String(mqttMessage.getPayload());
                // if (!msg.contains("power"))
                Log.e(TAG, "MQTT Cred user messageArrived " + s);
                Log.e(TAG, "messageArrived: at MqttMessageService " + msg);

                DisplayLoggingInfo(msg, s.startsWith("d"));
                Graphview.myListener.listener(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }

        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        DisplayLoggingInfo(msg, false);
        Intent intent = new Intent(this, MainActivity.class);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fd634dgdft5";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(topic)
                .setContentText(msg)
                // .setLargeIcon(emailObject.getSenderAvatar())

                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void DisplayLoggingInfo(String msg, boolean isTopicDevice) {

        Log.e("TAGGG", "DisplayLoggingInfo called isTopicDevice " + isTopicDevice);
        Object json = null;
        try {
            json = new JSONTokener(msg).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            Log.d(TAG, "DisplayLoggingInfo: JSONArray");
        } else if (json instanceof JSONObject) {
            Log.d(TAG, "DisplayLoggingInfo: JSONObject");
            JSONObject jsonObject = Constants.stringToJsonObject(msg);
            if (jsonObject != null) {
                if (jsonObject.has("power")) {
                    String dno = Constants.jsonObjectreader(jsonObject.toString(), "dno");
                    if (!Constants.PowerDnoR.contains(dno)) {
                        Constants.PowerDnoR += dno + ",";
                        Constants.savetoShared(this).edit().remove(dno + "powerR").apply();
                    }
                    String storePower = Constants.savetoShared(this).getString(dno + "powerR", "NA");
                    if (storePower.equals("NA")) {
                        JSONArray jsonArray = new JSONArray();
                        try {
                            jsonArray.put(jsonObject.get("power"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Constants.jsonObjectPut(jsonObject1,String.valueOf(Calendar.getInstance().getTime()),Constants.jsonObjectreader(jsonObject.toString(),"power"));
                        Constants.savetoShared(this).edit().putString(dno + "powerR", jsonArray.toString()).apply();
                    } else {
                        //JSONObject jsonObject1 = Constants.stringToJsonObject(storePower);
                        try {
                            JSONArray jsonArray = new JSONArray(storePower);
                            jsonArray.put(jsonObject.get("power"));
                            Constants.savetoShared(this).edit().putString(dno + "powerR", jsonArray.toString()).apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Constants.jsonObjectPut(jsonObject1,String.valueOf(Calendar.getInstance().getTime()),Constants.jsonObjectreader(jsonObject.toString(),"power"));
                    }
                }
            }
        }

        try {
            Log.e("TAG", "MQTT Service Logs at 202 " + msg);
            if (msg.startsWith("uint16_t") || msg.contains("uint16_t")) {
                intent.putExtra("datafromService", msg);
                sendBroadcast(intent);
            } else {
                JSONObject _object = new JSONObject(msg);
                Log.e("TAG", "Has Data Method " + _object.has("data_method"));
                if (_object.has("schedule")) {
                    Log.e("TAGG", "Track Data getAllSchedule Called");
                    if (rgb_controls.objInterface != null)
                        rgb_controls.objInterface.getSchedules(msg);
                    else if (ScheduleList.obj_interface != null) {
                        ScheduleList.obj_interface.getSchedules(msg);
                        Log.e("TAGG", "ScheduleList.obj_interface called");
                    }
                } else if (isTopicDevice) {
                    RoomActivity._grp_interface.updateStatus(msg);
                } else if (_object.has("data_method")) { //To overcome received data after while
                    Log.e("TAG", "BroadCast Send datafromService ");
                    intent.putExtra("datafromService", msg);
                    sendBroadcast(intent);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at send msg" + e.getMessage());
        }
    }

    public void pushLog(String data) {
        File logFile = new File("/data/data/com.wekex.apps.homeautomation/smarty_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {

            Time t = new Time(Time.getCurrentTimezone());
            t.setToNow();
            String date1 = t.format("%Y-%m-%d");

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String var = dateFormat.format(date);
            String fetch = date1 + "|" + var;

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(fetch).append(" >> ").append(data).append("\n");
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}