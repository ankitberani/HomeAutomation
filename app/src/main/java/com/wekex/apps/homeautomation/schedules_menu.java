package com.wekex.apps.homeautomation;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wekex.apps.homeautomation.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wekex.apps.homeautomation.utils.Constants.getDeviceById;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.DtypeViews.dno;

public class schedules_menu extends AppCompatActivity {
    String TAG = "schedules_menu";
    LinearLayout schedulesHolder;
    String DeviceInfos;
    String RoomId;
    String DeviceType;
    int schedulesResuyltCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_menu);

        schedulesHolder = findViewById(R.id.schedulesHolder);
        DeviceInfos = getIntent().getStringExtra("DeviceInfos");
        String deviceId = Constants.jsonObjectreader(DeviceInfos,dno);
        Log.d(TAG, "onCreate: "+DeviceInfos);
        JSONObject deviceData  = getDeviceById(deviceId,this);
        if (deviceData!=null){
            listSedules(deviceData.toString() );
        }
    }


    private void listSedules(String data) {

        schedulesHolder.removeAllViews();
            try {
                JSONObject deviceData  = stringToJsonObject(data);
                DeviceType = jsonObjectreader(data,"dtype");
                if(deviceData.has("schedule")){
                    JSONArray jsonArray = deviceData.getJSONArray("schedule");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        View dli = LayoutInflater.from(this).inflate(R.layout.schedules_list_item, null, false);
                        LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);
                        TextView schedules_name = dli.findViewById(R.id.schedules_name);
                        TextView dli_room = dli.findViewById(R.id.dli_room);
                        schedules_name.setText("Schedules " + i);

                        dli_parent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(schedules_menu.this, schedules.class);
                                intent.putExtra("DeviceInfos", DeviceInfos);
                                startActivityForResult(intent, schedulesResuyltCode);
                            }
                        });
                        schedulesHolder.addView(dli);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }


    public void close(View view) {
        finish();
    }

    public void addschedules(View view) {
        Intent intent = new Intent(this,schedules.class);
        intent.putExtra("DeviceInfos",DeviceInfos);
        startActivityForResult(intent,schedulesResuyltCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == schedulesResuyltCode) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                listSedules(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No Schedules Added", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
