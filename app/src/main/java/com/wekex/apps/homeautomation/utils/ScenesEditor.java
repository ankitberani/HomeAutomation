package com.wekex.apps.homeautomation.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScenesEditor extends AppCompatActivity {
    int GET_DEVICE = 1;

    /* renamed from: ID */
    String f195ID;
    String TAG = "ScenesEditor";
    String UID;
    LinearLayout configHolder;
    JSONObject configedJSON;
    LinearLayout deviceConfig;
    private LinearLayout deviceHolder;
    int deviceNo = 0;
    TextView devicename;
    JSONArray devices;
    TextView done;
    EditText sceneName;
    String sceneUrl;
    String scenee;
    String roomID = "";

    RelativeLayout rl_main;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenes_editor);

        rl_main = findViewById(R.id.rl_main);
        this.deviceHolder = findViewById(R.id.deviceHolder);
        this.sceneName = findViewById(R.id.sceneName);
        this.devices = new JSONArray();
        initView();

        this.scenee = getIntent().getStringExtra("Devices");
        Log.wtf("SCENEE_INTENT_EXTRA", this.scenee);

        roomID = getIntent().getStringExtra("roomID");

        if (this.scenee.equals("new")) {
            Log.wtf(TAG, "CONDITION NEW");
            this.f195ID = UUID.randomUUID().toString();
            this.UID = PreferencesHelper.getUserId(this);
//            this.sceneUrl = "http://209.58.164.151:88/api/Get/AddScene";
            this.sceneUrl = APIClient.BASE_URL + "/api/Get/AddScene";
            Log.wtf(TAG + " :SCENE_URL_ADD", this.sceneUrl);
        } else {
            try {
                Log.wtf(TAG, "CONDITION OLD");
//            this.sceneUrl = " http://209.58.164.151:88/api/Get/EditScene";
                this.sceneUrl = APIClient.BASE_URL + "/api/Get/EditScene";
                JSONObject jsonObject = new JSONObject(this.scenee);
                this.sceneName.setText(jsonObject.getString("Name"));
                this.devices = jsonObject.getJSONArray("Devices");
                this.f195ID = jsonObject.getString("ID");
                this.UID = jsonObject.getString(Constants.USER_ID);
                Log.wtf(TAG + " :SCENE_URL_ADD", this.sceneUrl);
                addToLIst();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String str = this.TAG;
        String sb = "onCreate: " +
                this.scenee;
        Log.d(str, sb);
    }

    /* access modifiers changed from: private */
    public void addToLIst() {
        this.deviceHolder.removeAllViews();
        String str = this.TAG;
        String sb = "addToLIst: " +
                this.devices;
        Log.d(str, sb);
        int i = 0;
        while (i < this.devices.length()) {
            try {
                String str2 = this.TAG;
                String sb2 = "onCreate: " +
                        this.devices.get(i).toString();
                Log.d(str2, sb2);
                final JSONObject deviceInfo = Constants.getDeviceById(Constants.stringToJsonObject(this.devices.get(i).toString()).getString(DtypeViews.dno), this);
                String str3 = this.TAG;
                String sb3 = "onCreate: " + deviceInfo.toString();
                Log.d(str3, sb3);
                View dli = LayoutInflater.from(this).inflate(R.layout.schedules_list_item, null, false);
                LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);
                TextView schedules_name = dli.findViewById(R.id.schedules_name);
                TextView dli_room = dli.findViewById(R.id.dli_room);
                String sb4 = "Room : " +
                        Constants.savetoShared(this).getString(Constants.jsonObjectreader(deviceInfo.toString(), DtypeViews.room), "Null");
                dli_room.setText(sb4);
                schedules_name.setText(Constants.jsonobjectSTringJSON(deviceInfo, "name"));
                final int finalI = i;
                dli_parent.setOnClickListener(v -> {
                    ScenesEditor.this.sceneConfiguration(deviceInfo.toString(), finalI);
                    ScenesEditor.this.deviceNo = finalI;
                });
                this.deviceHolder.addView(dli);
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    ProgressBar progressBar;

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
    }

    public void getDevice(View view) {
        Intent intent = new Intent(this, SelectableDeviceList.class);
        intent.putExtra("RoomId", roomID);
        startActivityForResult(intent, this.GET_DEVICE);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.GET_DEVICE) {
            if (resultCode == -1) {
                try {
                    assert data != null;
                    String result = data.getStringExtra("deviceInfo");

                    assert result != null;
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("Devices");
                    this.devices = jsonObject.getJSONArray("Devices");

                    //sceneConfiguration(result, jsonArray.length());
                    String str = this.TAG;
                    String sb = "onActivityResult: " + result;
                    Log.e(str, sb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == 0) {
                Toast.makeText(this, "No Schedules Added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sceneConfiguration(String result, final int finalI) {
        try {
            Log.wtf("++++++++++SCENE_RESPONSE_DATA**********", result + "\n" + finalI);
            Log.wtf("+++---SCENE_RESPONSE_DEVICES", this.devices.toString());

            View view = LayoutInflater.from(this).inflate(R.layout.scene_configuration, null, false);
            final Dialog dialog = new Dialog(this);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = dialog.getWindow();
            dialog.setCanceledOnTouchOutside(true);
            window.setGravity(80);
            window.setLayout(-1, -2);
            dialog.setContentView(view);
            this.deviceConfig = view.findViewById(R.id.deviceConfig);
            this.configHolder = view.findViewById(R.id.configHolder);
            this.devicename = view.findViewById(R.id.devicename);
            this.done = view.findViewById(R.id.done);
//        String Sdeviceanme = Constants.jsonObjectreader(result, "Name");
//        String jsonObjectreader = Constants.jsonObjectreader(result, DtypeViews.dno);
//        String Sdevictype = Constants.jsonObjectreader(result, DtypeViews.dtype);

            JSONObject jsonObject = new JSONObject(result);
            String Sdeviceanme = jsonObject.getString("Name");
            String Sdevictype = "d1";

            //Log.wtf("++++++++++SCENE_GET_DATA**********", Sdeviceanme + "\n" + jsonObjectreader + "\n" + Sdevictype);

            String str = this.TAG;
            String sb = "scenceConfiguration: " + Sdevictype;
            Log.d(str, sb);
            String str2 = this.TAG;
            String sb2 = "scenceConfiguration: " + result;
            Log.d(str2, sb2);
            this.devicename.setText(Sdeviceanme);
            typeOne(this.configHolder, result);
            this.done.setOnClickListener(v -> {
                try {
                    ScenesEditor.this.devices.put(finalI, ScenesEditor.this.configedJSON.toString());
                    ScenesEditor.this.addToLIst();
                    String str1 = ScenesEditor.this.TAG;
                    String sb1 = "onClick: " + ScenesEditor.this.configedJSON.toString();
                    Log.d(str1, sb1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            });
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void typeOne(LinearLayout configHolder2, String result) {
        String dno = null;
        boolean state = Boolean.TRUE;
        try {
            JSONObject device = new JSONObject(result);
            state = device.getJSONArray("Devices")
                    .getJSONObject(0)
                    .getJSONObject("d1")
                    .getBoolean("status");
            String str = this.TAG;
            String sb = "typeOne: " + state;
            Log.d(str, sb);
            dno = device.getJSONArray("Devices").getJSONObject(0).getString(DtypeViews.dno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        View view = LayoutInflater.from(this).inflate(R.layout.scene_config, null, false);
        ((TextView) view.findViewById(R.id.device3)).setText("Device No. 1");
        Switch aSwitch = view.findViewById(R.id.switch1);
        final String dno2 = dno;


        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                ScenesEditor.this.configedJSON = new JSONObject();
                JSONObject jsonInternal = new JSONObject();
                ScenesEditor.this.configedJSON.put(DtypeViews.dno, dno2);
                jsonInternal.put(NotificationCompat.CATEGORY_STATUS, isChecked);
                ScenesEditor.this.configedJSON.put("d1", jsonInternal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String str = ScenesEditor.this.TAG;
            String sb = "onCheckedChanged: " + ScenesEditor.this.configedJSON.toString();
            Log.d(str, sb);
        });

        if (!this.scenee.equals("new")) {
            try {
                JSONObject d = Constants.stringToJsonObject(this.devices.get(this.deviceNo).toString());
                String str2 = this.TAG;
                assert d != null;
                String sb2 = d.toString() + " typeOne: aa ";
                Log.d(str2, sb2);
                state = d.getJSONObject("d1").getBoolean(NotificationCompat.CATEGORY_STATUS);
                String str3 = this.TAG;
                String sb3 = d.toString() + " typeOne: aa " + state;
                Log.d(str3, sb3);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        aSwitch.setChecked(state);
        configHolder2.addView(view);
    }

    public void done(View view) {
        String sn = this.sceneName.getText().toString();
        if (!sn.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ID", UUID.randomUUID());
                jsonObject.put(Constants.USER_ID, PreferencesHelper.getUserId(this));
                jsonObject.put("Name", sn);
                jsonObject.put("Devices", this.devices);
//                String str = this.TAG;
//                String sb = "done: " + jsonObject.toString();
//                Log.d(str, sb);

                Log.wtf("SCENE_EDITOR_ON_DONE", jsonObject.toString());

                uploadScene(jsonObject.toString());
//                uploadScene("{\"UID\":\"d0690697-f044-465f-8152-79b3d7f2137d\",\"Name\":\"justest\",\"Devices\":[\"{\\\"dno\\\":\\\"18FE34A402A0\\\",\\\"d1\\\":{\\\"r\\\":0,\\\"g\\\":0,\\\"b\\\":0,\\\"w\\\":129,\\\"ww\\\":126,\\\"state\\\":true,\\\"br\\\":0.85}}\"]}");
            } catch (JSONException e) {
                String str2 = this.TAG;
                String sb2 = "done: " +
                        e.getMessage();
                Log.d(str2, sb2);
            }
        }
    }

    public void close(View view) {
        finish();
    }

//    public void uploadScene(final String data) {
//        progressBar.setVisibility(View.VISIBLE);
//        String str = this.TAG;
//        String sb = this.sceneUrl + " uploadScene: " + data;
//        Log.d(str, sb);
//        new Thread(() -> {
//            BufferedReader br;
//            try {
//                HttpURLConnection conn = (HttpURLConnection) new URL(ScenesEditor.this.sceneUrl).openConnection();
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
//                conn.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
//                conn.setDoOutput(true);
//                conn.setDoInput(true);
//                Log.i("JSON", data);
//                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                os.writeBytes(data);
//                os.flush();
//                os.close();
//                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
//                Log.i("MSG1", conn.getResponseMessage());
//                if (200 > conn.getResponseCode() || conn.getResponseCode() > 299) {
//                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                } else {
//                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                }
//                StringBuilder sb1 = new StringBuilder();
//                while (true) {
//                    String readLine = br.readLine();
//                    if (readLine != null) {
//                        sb1.append(readLine);
//                    } else {
//                        Log.i("JSON", sb1.toString());
//                        JSONObject object = new JSONObject(sb1.toString());
//                        if (object.has("success") && object.getBoolean("success")) {
//                            showSnackBar("Update successfully!");
//                        } else
//                            showSnackBar("Failed!");
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(this, "Scene Added Successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//                        ScenesEditor.this.responseData(sb1.toString());
//                        conn.disconnect();
//                        return;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

    public void uploadScene(String data) throws JSONException {
        progressBar.setVisibility(View.VISIBLE);
        String str = this.TAG;
        String sb = this.sceneUrl + " uploadScene: " + data;
        Log.d(str, sb);

        JSONObject jsonObject = new JSONObject(data);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ScenesEditor.this.sceneUrl,
                jsonObject, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject json = new JSONObject(String.valueOf(response));
                Log.wtf(this.TAG + " responseee ", json.toString());

                if (json.has("success") && json.getBoolean("success")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Scene Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    onPause();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        requestQueue.add(jsonObjectRequest);
    }

    /* access modifiers changed from: private */
    public void responseData(String toString) {
        String str = this.TAG;
        String sb = "responseData: " +
                toString;
        Log.d(str, sb);
    }

    void showSnackBar(String msg) {
        Snackbar.make(rl_main, msg, Snackbar.LENGTH_LONG).show();
    }

}
