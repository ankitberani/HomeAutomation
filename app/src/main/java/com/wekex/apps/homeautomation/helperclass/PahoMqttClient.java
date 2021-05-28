package com.wekex.apps.homeautomation.helperclass;


import android.content.Context;

import androidx.annotation.NonNull;

import android.util.Log;

import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.DtypeViews.userSuscribe;

/**
 * Created by brijesh on 20/4/17.
 */

public class PahoMqttClient {

    private static final String TAG = "PahoMqttClient";

    private MqttAndroidClient mqttAndroidClient;
    private String username;
    private String password;

    /*public MqttAndroidClient getMqttClient(Context context, String brokerUrl, String clientId) {

        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        try {
            IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    Log.d(TAG, "Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return mqttAndroidClient;
    }*/

    public MqttAndroidClient getMqttClientAuthenticate(Context context, String brokerUrl, String UserId, String pass) {
        username = UserId;
        password = pass;
        String clientId = Constants.clientId;
        Log.d(TAG, "getMqttClientAuthenticate: " + clientId);
        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        try {
            final IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOptionAthenticate());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    Log.d(TAG, "Success " + token.toString());

//                    setLog("Success "+token.toString());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            Log.d(TAG, "Failure1 " + e.toString());
            e.printStackTrace();
        }
        return mqttAndroidClient;
    }

    public MqttAndroidClient getHomeMqttClientAuthenticate(Context context, String brokerUrl) {
        username = savetoShared(context).getString(Constants.USER_ID, "0");
        password = savetoShared(context).getString(Constants.PASSWORD, "0");
        String clientId = Constants.clientId;
        Log.d(TAG, "getMqttClientAuthenticate: " + clientId);
        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        try {
            final IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOptionAthenticate());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttAndroidClient.setBufferOpts(getDisconnectedBufferOptions());
                    Log.d(TAG, "Success " + token.toString());
                    userSuscribe(context);
                    DtypeViews.getGetDevice(context);
//                    setLog("Success "+token.toString());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failure " + exception.toString());
                }
            });
        } catch (MqttException e) {
            Log.d(TAG, "Failure1 " + e.toString());
            e.printStackTrace();
        }
        return mqttAndroidClient;
    }

    public void disconnect(@NonNull MqttAndroidClient client) throws MqttException {
        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Successfully disconnected");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(TAG, "Failed to disconnected " + throwable.toString());
            }
        });
    }

    @NonNull
    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }

    @NonNull
    private MqttConnectOptions getMqttConnectionOptionAthenticate() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setWill(Constants.PUBLISH_TOPIC, "I am going offline".getBytes(), 1, true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        return mqttConnectOptions;
    }

    @NonNull
    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setWill(Constants.PUBLISH_TOPIC, "I am going offline".getBytes(), 1, true);
        //mqttConnectOptions.setUserName("user");
        //mqttConnectOptions.setPassword("WtjhZKl3OPoK".toCharArray());
        return mqttConnectOptions;
    }


    public void publishMessage(@NonNull MqttAndroidClient client, @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload;
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setId(320);
        message.setRetained(false);
        message.setQos(qos);
        client.publish(topic, message);
        Log.d(TAG, "publishMessage: " + topic + " " + message);
    }

    public void subscribe(@NonNull MqttAndroidClient client, @NonNull final String topic, int qos) throws MqttException {
        try {

            final String[] ret = new String[1];
            IMqttToken token = client.subscribe(topic, qos);
            if (token == null) {
                Log.e("TAGG", "Subscribe token null");
                return;
            }
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.e(TAG, "Subscribe Successfully " + topic);
//                setLog("Subscribe Successfully " + topic);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e(TAG, "Subscribe onFailure " + topic);
                }
            });
        } catch (Exception e) {
            Log.e("TAGG", "Subscribe Exception " + e.getMessage());
        }
    }

    public void unSubscribe(@NonNull MqttAndroidClient client, @NonNull final String topic) throws MqttException {

        IMqttToken token = client.unsubscribe(topic);

        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "UnSubscribe Successfully " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "UnSubscribe Failed " + topic);
            }
        });
    }

    public void setLog(String msg) {
    }

}