package com.wekex.apps.homeautomation;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wekex.apps.homeautomation.utils.DtypeViews.dno;
import static com.wekex.apps.homeautomation.utils.DtypeViews.dtype;
import static com.wekex.apps.homeautomation.utils.DtypeViews.key;
import static com.wekex.apps.homeautomation.utils.DtypeViews.pname;
import static com.wekex.apps.homeautomation.utils.DtypeViews.room;

public class DeviceList extends AppCompatActivity {
    String TAG = "DeviceLIstActivity";
    LinearLayout deviceHolder;
    String RoomId = "";
    String deviceType = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        deviceHolder = findViewById(R.id.deviceHolder);

        RoomId = getIntent().getStringExtra("RoomId");
        deviceType = getIntent().getStringExtra("DeviceType");
        if (deviceType == null)
            deviceType = "0";

        DtypeViews.getGetDevice(this);

        String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        Log.d(TAG, "onCreate: " + jsonString);
        updateUI(jsonString);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateUI(String Rdata) {
        //String Rdata = intent.getStringExtra("datafromService");
        String data = Constants.jsonObjectreader(Rdata, "data");
        Log.d(TAG, "updateUI: " + data);
        // String jsonc = jsonObjectreader(response,"data");
        if (data.equals("error")) {
           /* if (deviceHolder!=null) {
                Log.d(TAG, "updateUI2: "+Rdata);
                String dno1 = Constants.jsonObjectreader(Rdata, "dno");
                String type1 = Constants.jsonObjectreader(Rdata, "dtype");
                String state = Constants.jsonObjectreader(Constants.jsonObjectreader(Rdata, "d1"), "state");
                Switch view = deviceHolder.findViewWithTag(dno1);
                view.setChecked(Boolean.parseBoolean(state));
              Log.d(TAG, "updateUI: " + dno1);
            }*/
        } else {

            //84f3ebfb696a
            deviceHolder.removeAllViews();
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

//                    Log.e("TAGGGG", "Devices dno " + String.format("Bulb : %s", Constants.jsonObjectreader(jsonObject.toString(), dno)));
//                    Log.e("TAGGG", "Both Room Id " + RoomId + " From Json " + Constants.jsonObjectreader(jsonObject.toString(), "room"));

                    String dno = Constants.jsonObjectreader(jsonObject.toString(), "dno");
//                    if (dno.equalsIgnoreCase("84f3ebfb696a")) {
//                        Log.e("TAGGG", "Record id is " + dno + " post " + i);
//                    }
                    if (!RoomId.equals(Constants.jsonObjectreader(jsonObject.toString(), "room")))
                        continue;

                    Log.e("TAGGG", "Device Type  deviceType " + deviceType + " From Json " + Constants.jsonObjectreader(jsonObject.toString(), "dtype"));
                    if (!deviceType.equals(Constants.jsonObjectreader(jsonObject.toString(), "dtype")) && !deviceType.equals("0"))
                        continue;

                    Log.d(TAG, "jsonDeviceAdder: " + jsonObject.toString());
                    View dli = LayoutInflater.from(this).inflate(R.layout.device_list_item, null, false);
                    LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);
                    TextView dli_name = dli.findViewById(R.id.dli_name);
                    TextView dli_room = dli.findViewById(R.id.dli_room);
                    dli_name.setText(String.format("Device Name : %s", Constants.jsonObjectreader(jsonObject.toString(), pname)));
//                    dli_name.setText(String.format("Bulb : %s", Constants.jsonObjectreader(jsonObject.toString(), dno)));
                    if (Constants.jsonObjectreader(jsonObject.toString(), "room").equals("")) {
                        dli_room.setText("Room : Not Assigned");
                    } else {
                        dli_room.setText("Room : " + Constants.savetoShared(this).getString(Constants.jsonObjectreader(jsonObject.toString(), room), "Null"));
                    }


//                    Log.e("TAGGGG", dno);
                    //Constants.savetoShared(this).getString(Constants.jsonObjectreader(jsonObject.toString(),"d1"),"Null");
                    String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
                    String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
                    String dtype1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
                    dli_parent.setOnClickListener(v -> {
                        /*if (dli_room.getText().equals("Room : Not Assigned")){
                        DtypeViews.addDevicetoRoom(DeviceList.this,key1,RoomId);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","Added");
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();*/

                        DtypeViews.chageDevicefromRoom(DeviceList.this, key1, RoomId, dno1, dtype1);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "Added");
                        returnIntent.putExtra("deviceInfo", jsonObject.toString());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });
                    deviceHolder.addView(dli);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
