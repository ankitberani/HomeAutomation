package com.wekex.apps.homeautomation;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;

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
import com.wekex.apps.homeautomation.adapter.AddremoteApi;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class selectRemote extends AppCompatActivity {
    RecyclerView remoteHolder;
    RecyclerView iconView;
    private String TAG = "selectRemote";
    private String r_type;
    //    String deviceArray[] = {"TV", "Air Conditioner", "STP", "DVD_Player", "Home_Theatre"};
    String deviceArray[] = {"TV", "Air Conditioner", "SETUP BOX"};
    public static ArrayList<String> apiString;
    String urlForParams;
    String dnoValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_remote);
        apiString = new ArrayList<>();

        dnoValue = getIntent().getStringExtra("dno");
        r_type = getIntent().getStringExtra("r_type");


        if (r_type.equals("ir")) {
            ArrayList<String> devices = new ArrayList<>(Arrays.asList(deviceArray));
            populateToRecyclerIcon(devices);
        } else if (r_type.equals("rf")) {
            ArrayList<String> devices = new ArrayList<>(Arrays.asList(deviceArray));
            populateToRecyclerIcon(devices);

        }


    }

    public void getGetDevice(String what) {

        findViewById(R.id.manual).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
//        final String url = "http://209.58.164.151:88/api/Get/Remote?" + what;
        final String url = APIClient.BASE_URL + "/api/Get/Remote?" + what;
        urlForParams = url;
        Log.d(TAG, "getGetDevice: Response" + url);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    // display response
                    Log.d("Response", response.toString());
                    if (response.has("keys")) {
                        callRemote(response);
                    } else
                        populateToRecycler(response.toString());
                },
                error -> Log.d("Error.Response", "Some Error" + error.getMessage() + error.toString())
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    private void callRemote(JSONObject response) {
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
            DtypeViews.PowerClick(selectRemote.this, response.toString(), finalValue, dnoValue, "irs");
            power_button_dialog.findViewById(R.id.worked).setVisibility(View.VISIBLE);
        });

        yes.setOnClickListener(v -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = APIClient.BASE_URL+"/api/Get/UpdateRemote?device=" + dnoValue + "&channel=ir";

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
                            toUpdateJSOn.put("ir", response);

                            String jsonData = udpdateJSON(selectRemote.this, toUpdateJSOn.toString());

                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(selectRemote.this, RemoteMenu.class);
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

    public void populateToRecycler(String list) {
        RecyclerView remoteHolder = findViewById(R.id.remoteHolder);
        remoteHolder.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        remoteHolder.setLayoutManager(mLayoutManager);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        AddremoteApi mAdapter = new AddremoteApi(selectRemote.this, list);
        remoteHolder.setAdapter(mAdapter);
    }

    public void populateToRecyclerIcon(ArrayList<String> list) {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        RecyclerView remoteHolder2 = findViewById(R.id.remoteHolder);
        remoteHolder2.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        remoteHolder2.setLayoutManager(mLayoutManager);
        IconViewAdapter mAdapter = new IconViewAdapter(selectRemote.this, list);
        remoteHolder2.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + apiString.toString());
        if (!apiString.isEmpty()) {
            apiString.remove(apiString.size() - 1);
            int length = apiString.size();
            if (length > 0) {
                String apirequest = "";
                for (String values : apiString) {
                    apirequest += values + "&";
                }
                Log.d(TAG, "onClick: " + apirequest);
                getGetDevice(apirequest);
            }
        } else {
            finish();
        }
    }

    public void startRemoteManual(View view) {
        Uri uri = Uri.parse(urlForParams);
        String type = uri.getQueryParameter("type");
        Log.d(TAG, "startRemoteManual: " + type);
        Intent intent = new Intent(selectRemote.this, RemoteActivity.class);
        intent.putExtra("dno", dnoValue);
        intent.putExtra("uri", urlForParams);
        intent.putExtra("type", type.toLowerCase());
        intent.putExtra("mode", "0");// "0" for configure remote
        startActivity(intent);
    }
}
