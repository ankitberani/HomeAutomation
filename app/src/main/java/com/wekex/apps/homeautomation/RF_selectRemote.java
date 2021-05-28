package com.wekex.apps.homeautomation;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class RF_selectRemote extends AppCompatActivity {
    String TAG = "RF_selectRemote";
    RecyclerView remoteHolder;
    private String r_type;
    String deviceArray[] = {"TV", "Arf Conditioner", "STP", "DVD_Player", "Home_Theatre"};
    public static ArrayList<String> apiString;
    String urlForParams;
    String dnoValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rf_select_remote);

        apiString = new ArrayList<>();
        remoteHolder = findViewById(R.id.remoteHolder);
        remoteHolder.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        remoteHolder.setLayoutManager(mLayoutManager);

        dnoValue = getIntent().getStringExtra("dno");
        r_type = getIntent().getStringExtra("r_type");

        getRemote();
    }

    public void getRemote() {
        findViewById(R.id.manual).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Loading Remotes", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    URL url = new URL("http://209.58.164.151:88/api/Get/getRFdb");
                    URL url = new URL(APIClient.BASE_URL + "/api/Get/getRFdb");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    BufferedReader br;
                    if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String output;
                    StringBuilder sb = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Log.d(TAG, "run: " + sb.toString());
                    String InvalidJSON = sb.toString().replace("\\", "").trim();
                    JSONArray remo;
                    if (InvalidJSON.startsWith("\""))
                        remo = new JSONArray(InvalidJSON.substring(1, InvalidJSON.length() - 1));
                    else
                        remo = new JSONArray(InvalidJSON);

                    Log.d(TAG, "run: " + remo.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView remoteHolder2 = findViewById(R.id.remoteHolder);
                            remoteHolder2.setHasFixedSize(true);
                            GridLayoutManager mLayoutManager = new GridLayoutManager(RF_selectRemote.this, 2);
                            remoteHolder2.setLayoutManager(mLayoutManager);
                            RF_IconViewAdapter mAdapter = new RF_IconViewAdapter(RF_selectRemote.this, remo);
                            remoteHolder2.setAdapter(mAdapter);
                        }
                    });

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void getGetDevice() {
        findViewById(R.id.manual).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = APIClient.BASE_URL + "/api/Get/getRFdb";
        Log.d(TAG, "getGetDevice: Response" + url);
// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    // diplay response
                    Log.d("Response", response.toString());
                    // if (!response.has("keys")){
                    //     callRemote(response);
                    //}else
                    try {
                        Log.d("Response tried", response.toString());
                        JSONArray jsonArray = new JSONArray(response.toString());
                    } catch (JSONException e) {
                        Log.d("Response error", response.toString());
                        e.printStackTrace();
                    }
                    // populateToRecycler(response.toString());
                },
                error -> Log.d("Error.Response", "Some Error" + error.getMessage() + error.toString())
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void callRemote(JSONObject response) {

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        View power_button_dialog = LayoutInflater.from(this).inflate(R.layout.dia_remote_button_test, null, false);
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(power_button_dialog);

        String value = "";
        try {
            value = response.getJSONArray("keys").getJSONObject(0).getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout power = power_button_dialog.findViewById(R.id.power);
        Button yes = power_button_dialog.findViewById(R.id.yes);
        Button no = power_button_dialog.findViewById(R.id.no);
        LinearLayout testButton = power_button_dialog.findViewById(R.id.testButton);
        LinearLayout manual = power_button_dialog.findViewById(R.id.manual);
        //manual.setVisibility(View.GONE);

        power.getResources().getDrawable(R.drawable.remote_button_click_2);
        power.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));

        String finalValue = value;
        power.setOnClickListener(v -> {
            DtypeViews.PowerClick(RF_selectRemote.this, response.toString(), finalValue, dnoValue, "rfs");
            power_button_dialog.findViewById(R.id.worked).setVisibility(View.VISIBLE);
        });

        yes.setOnClickListener(v -> {


            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = APIClient.BASE_URL + "/api/Get/UpdateRemote?device=" + dnoValue + "&channel=rf";

            JSONObject data = new JSONObject();
            try {
                data.put("remotedata", response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, data + " saveRemote: " + url);
// prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(POST, url, data,
                    upresponse -> {
                        Log.d("Response", "response " + upresponse);
                        // display response
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        try {
                            dialog.dismiss();

                            JSONObject toUpdateJSOn = new JSONObject();
                            toUpdateJSOn.put("dno", dnoValue);
                            toUpdateJSOn.put("rf", response);

                            String jsonData = udpdateJSON(RF_selectRemote.this, toUpdateJSOn.toString());

                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RF_selectRemote.this, RemoteMenu.class);
                            intent.putExtra("remotes", jsonData);
                            intent.putExtra("dno", dnoValue);
                            startActivity(intent);
                            finish();

                            /*    Intent returnIntent = new Intent();
                            returnIntent.putExtra("result",jsonData.toString());
                            Log.d(TAG, "callRemote: "+jsonData);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Log.d("Error.Response", "error" + error.getMessage())
            );

// add it to the RequestQueue
            queue.add(getRequest);
            Log.d(TAG, "onClick: ");
            /*  */
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual.setVisibility(View.VISIBLE);
                testButton.setVisibility(View.GONE);
                // dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void populateToRecyclerIcon(JSONArray list) {
        //
        RecyclerView remoteHolder2 = findViewById(R.id.remoteHolder);
        remoteHolder2.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        remoteHolder2.setLayoutManager(mLayoutManager);
        RF_IconViewAdapter mAdapter = new RF_IconViewAdapter(RF_selectRemote.this, list);
        remoteHolder2.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + apiString.toString());
        /*if (!apiString.isEmpty()){
            apiString.remove(apiString.size()-1);
            int length = apiString.size();
            if (length>0){
                String aprfequest = "";
                for (String values: apiString) {
                    aprfequest += values+"&";
                }
                Log.d(TAG, "onClick: "+aprfequest);
                getGetDevice(aprfequest);
            }}else {
            finish();
        }*/

        finish();
    }

    public void startRemoteManual(View view) {
        Intent intent = new Intent(this, rf_remote.class);

        intent.putExtra("dno", dnoValue);
        intent.putExtra("type", "rf");
        intent.putExtra("mode", "0");// "1" for working remote
        startActivity(intent);
    }

}
