package com.wekex.apps.homeautomation;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.MqttServiceConstants;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;
import com.wekex.apps.homeautomation.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    String TAG = "SplashActivity";
    TextView try_again;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.try_again = findViewById(R.id.try_again);

        File logFile = new File("/data/data/com.wekex.apps.homeautomation/smarty_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                System.out.println("File created!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File already created!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        check();
        startgps();
    }

    public void check() {
        Utility _utility = new Utility(SplashActivity.this);
        if (!_utility.getString("selected_domain").isEmpty() && _utility.getString("selected_domain") != null) {
            APIClient.BASE_URL = _utility.getString("selected_domain");
        } else
            APIClient.BASE_URL = "http://209.58.164.151:88/";
        if (!isDataAvailable()) {
            this.try_again.setVisibility(View.VISIBLE);
            findViewById(R.id.cti).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            //retrieveDevices();
        } else if (Constants.savetoShared(this).getString(Constants.USER_ID, "NA").equals("NA")) {
            startActivity(new Intent(this, LoginReg.class));
            finish();
        } else {
            retrieveDevices();
            Intent intent = new Intent(new Intent(SplashActivity.this, HomeActivity.class));
            //intent.putExtra(Utils.DATA, response);
            startActivity(intent);
            finish();
        }
    }

    private void retrieveDevices() {
//        RequestQueue queue = Volley.newRequestQueue(this);
        PreferencesHelper.clearData(this);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        StringBuilder sb = new StringBuilder();
//        sb.append("http://209.58.164.151:88/api/Get/getDevice?UID=" + user);
        sb.append(APIClient.BASE_URL).append("/api/Get/getDevice?UID=").append(user);
        APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);
        String _url = APIClient.BASE_URL + "/api/Get/getDevice?UID=" + user;
        Log.e("TAG", "URL of GetDevice " + _url);
        Call<String> _call = _apiService.getDeviceOfRoom(_url);

        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                Log.e("TAGGG", "OnResponse of device " + response.body());
//                lambda$retrieveDevices$0(SplashActivity.this, response.body());

                String str = SplashActivity.this.TAG;
                String sb = "retrieveDevices: " + response;
                Log.d(str, sb);

                //Create UID based offline data
                File logFile = new File("/data/data/com.wekex.apps.homeautomation/" + user + ".txt");
                if (!logFile.exists()) {
                    try {
                        logFile.createNewFile();
                        System.out.println("Offline JSON File created!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("JSON file already created!");
                }

                Intent intent = new Intent(new Intent(SplashActivity.this, HomeActivity.class));
                intent.putExtra(Utils.DATA, response.body());
                PreferencesHelper.setAllDevices(SplashActivity.this, response.body());
                PreferencesHelper.setUserId(SplashActivity.this, user);

                if (logFile.length() == 0) {
                    pushOfflineData(response.body(), user);
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAGGG", "OnResponse onFailure " + t.getMessage());
            }
        });
    }

    public  /* synthetic */ void lambda$retrieveDevices$0(SplashActivity splashActivity, String response) {
        String str = splashActivity.TAG;
        String sb = "retrieveDevices: " + response;
        Log.d(str, sb);

        Intent intent = new Intent(new Intent(splashActivity, HomeActivity.class));
        intent.putExtra(Utils.DATA, response);
        PreferencesHelper.setAllDevices(this, response);
//        splashActivity.startActivity(intent);
//        splashActivity.finish();
    }

    public /* synthetic */ void lambda$retrieveDevices$1(SplashActivity splashActivity, VolleyError error) {
        StringBuilder sb = new StringBuilder();
        sb.append(MqttServiceConstants.TRACE_ERROR);
        sb.append(error.getMessage());
        Log.d("Error.Response", sb.toString());
        Toast.makeText(splashActivity, "Unable to Data", Toast.LENGTH_LONG).show();
        splashActivity.findViewById(R.id.progressBar).setVisibility(View.GONE);

    }

    private boolean isDataAvailable() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void tryAgain(View view) {
        check();
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    public void pushOfflineData(String data, String UID) {
        File logFile = new File("/data/data/com.wekex.apps.homeautomation/" + UID + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.write(data);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
