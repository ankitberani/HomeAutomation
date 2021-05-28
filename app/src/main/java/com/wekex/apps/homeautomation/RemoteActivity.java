package com.wekex.apps.homeautomation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.Constants.jsonobjectSTringJSON;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class RemoteActivity extends AppCompatActivity {
    private String mode;
    private Dialog number_pad;
    View numberpad;
    private String keys;
    private RelativeLayout RemoteHolder;
    JSONArray RemotesKeys;
    JSONObject editRemoteData;
    String TAG = "RemoteActivity";
    JSONArray configure_mode;
    String ButtonClicked;
    String type_brand;
    String remoteData;
    String index = "na";


    String dnoValue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_remote);
        configure_mode = new JSONArray();
        String type = getIntent().getStringExtra("type");
        mode = getIntent().getStringExtra("mode");
        dnoValue = getIntent().getStringExtra("dno");
        Log.d(TAG, type + " onCreate: " + mode);
        type = "ac";
        switch (type) {
            case "ac":
                setContentView(R.layout.ac_remote);
                break;
            case "tv":
                setContentView(R.layout.activity_remote);
                break;
        }

        RemoteHolder = findViewById(R.id.RemoteView);
        numberpad = LayoutInflater.from(this).inflate(R.layout.remote_number_pad, null, false);
        number_pad = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

        if (mode.equals("1")) {
            keys = getIntent().getStringExtra("keys");
            Log.d(TAG, "onCreate: " + keys);
           /* try {
               // keys = Constants.stringToJsonObject(keys).getString("keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            if (!keys.equals("")) {
                //String dno = getIntent().getStringExtra("dno");
                Log.d(TAG, " onCreate: " + keys);

                try {
                    RemotesKeys = new JSONArray(keys);
                    for (int i = 0; i < RemotesKeys.length(); i++) {
                        JSONObject jsonObject = RemotesKeys.getJSONObject(i);
                        String value = jsonObject.getString("value");
                        String key = jsonObject.getString("key");
                        if (key.contains("DIGIT")) {
                            View view = numberpad.findViewWithTag(key);
                            view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                                    // DtypeViews.RemotClick(RemoteActivity.this,dno,value);
                                }
                            });
                        } else {
                            View view = RemoteHolder.findViewWithTag(key);
                            //view.getResources().getDrawable(R.drawable.remote_button_click_2);
                            view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                            Log.d(TAG, "onCreate: " + key);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                                    DtypeViews.RemotClick(RemoteActivity.this, dnoValue, value, "irs");
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (mode.equals("0")) {
            type_brand = getIntent().getStringExtra("uri");
            findViewById(R.id.saveRemote).setVisibility(VISIBLE);
        } else if (mode.equals("2")) {
            remoteData = getIntent().getStringExtra("remoteData");
            index = getIntent().getStringExtra("index");
            findViewById(R.id.saveRemote).setVisibility(VISIBLE);
            try {
                editRemoteData = new JSONObject(remoteData);
                RemotesKeys = editRemoteData.getJSONArray("keys");
                configure_mode = RemotesKeys;
                for (int i = 0; i < RemotesKeys.length(); i++) {
                    JSONObject jsonObject = RemotesKeys.getJSONObject(i);
                    String value = jsonObject.getString("value");
                    String key = jsonObject.getString("key");
                    if (key.contains("DIGIT")) {
                        View view = numberpad.findViewWithTag(key);
                        view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                reconfigure(v);

                                return false;
                            }
                        });
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                                // DtypeViews.RemotClick(RemoteActivity.this,dno,value);
                                DtypeViews.RemotClick(RemoteActivity.this, dnoValue, value, "irs");
                            }
                        });
                    } else {
                        View view = RemoteHolder.findViewWithTag(key);
                        view.getResources().getDrawable(R.drawable.remote_button_click_2);
                        view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                        Log.d(TAG, "onCreate: " + key);
                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                reconfigure(v);
                                return false;
                            }
                        });
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                                DtypeViews.RemotClick(RemoteActivity.this, dnoValue, value, "irs");
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    Boolean isUp = false;



    public void remoteClick(View v) {
        View view;
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        switch (v.getId()) {
            case R.id.close:
                number_pad.dismiss();
                break;
            case R.id.shownum:
                view = findViewById(R.id.remote_numbers);
                if (number_pad.isShowing()) {
                    number_pad.dismiss();
                } else {
                    dialog_umber_pad(view);
                }
                isUp = !isUp;
                break;
            default:
                if (mode.equals("1"))
                    Log.d(TAG, "remoteClick: ");
                    //Toast.makeText(this, "You have Pressed "+v.getTag().toString(), Toast.LENGTH_SHORT).show();
                else if (mode.equals("0")) {
                    progressDialog = ProgressDialog.show(RemoteActivity.this, "",
                            "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
                    DtypeViews.RemotClickConfigure(RemoteActivity.this, dnoValue, "irl");
                } else if (mode.equals("2")) {
                    progressDialog = ProgressDialog.show(RemoteActivity.this, "",
                            "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
                    progressDialog.setCanceledOnTouchOutside(true);
                    ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
                    DtypeViews.RemotClickConfigure(RemoteActivity.this, dnoValue, "irl");
                }

                break;
        }
    }

    public void dialog_umber_pad(View view) {
        number_pad.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = number_pad.getWindow();
        number_pad.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        number_pad.setContentView(numberpad);
        number_pad.show();
    }

    public void close(View view) {
        finish();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {

        String Rdata = intent.getStringExtra("datafromService");
        if (Rdata.contains("rawData")) {
            if (!ButtonClicked.equals(""))
                new setInRemote().execute(Rdata);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //DtypeViews.getGetDevice(this);
        String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        //addViews(jsonString);
        Log.d(TAG, "onResume: ");
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void saveRemote(View view) {


        Dialog dia_save = new Dialog(RemoteActivity.this);
        View view1 = LayoutInflater.from(RemoteActivity.this).inflate(R.layout.dialog_remote_save, null, false);
        dia_save.setContentView(view1);
        dia_save.show();

        ImageView close = view1.findViewById(R.id.close);
        Button dia_add = view1.findViewById(R.id.dia_add);
        Button dia_update = view1.findViewById(R.id.dia_update);

        EditText name = view1.findViewById(R.id.dia_name);
        EditText model = view1.findViewById(R.id.dia_model);
        EditText typeET = view1.findViewById(R.id.r_type);
        EditText brandNameET = view1.findViewById(R.id.brandName);


        if (!index.equals("na")) {
            typeET.setText(jsonobjectSTringJSON(editRemoteData, "R_Type"));
            brandNameET.setText(jsonobjectSTringJSON(editRemoteData, "R_Brand"));
            name.setText(jsonobjectSTringJSON(editRemoteData, "Name"));

            dia_add.setVisibility(GONE);
            dia_update.setVisibility(VISIBLE);
        } else {
            Uri uri = Uri.parse(type_brand);
            String type = uri.getQueryParameter("type");
            String brand = uri.getQueryParameter("brand");
            brandNameET.setText(brand);
            typeET.setText(type);
        }

        close.setOnClickListener(v -> {
            dia_save.dismiss();
        });

        dia_add.setOnClickListener(v -> {

            Log.d(TAG, "saveRemote: " + type_brand);


            JSONObject saveRemote = new JSONObject();
            try {
                saveRemote.put("R_Type", typeET.getText().toString());
                saveRemote.put("R_Brand", brandNameET.getText().toString());
                saveRemote.put("Format", 31);
                saveRemote.put("Name", name.getText().toString());
                saveRemote.put("keys", configure_mode);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "saveRemote: " + saveRemote);
            findViewById(R.id.progressBar).setVisibility(VISIBLE);
            Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
            RequestQueue queue = Volley.newRequestQueue(this);
//            final String url = "http://209.58.164.151:88/api/Get/UpdateRemote?device=" + dnoValue + "&channel=ir";
            final String url = APIClient.BASE_URL + "/api/Get/UpdateRemote?device=" + dnoValue + "&channel=ir";

            JSONObject data = new JSONObject();
            try {
                data.put("remotedata", saveRemote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, saveRemote + " saveRemote: " + url);
// prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(POST, url, data,
                    response -> {
                        Log.d("Response", "response " + response);
                        // display response
                        findViewById(R.id.progressBar).setVisibility(GONE);
                        try {
                            dia_save.dismiss();

                            JSONObject toUpdateJSOn = new JSONObject();
                            toUpdateJSOn.put("dno", dnoValue);
                            toUpdateJSOn.put("ir", saveRemote);

                            String jsonData = udpdateJSON(RemoteActivity.this, toUpdateJSOn.toString());

                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RemoteActivity.this, RemoteMenu.class);
                            intent.putExtra("remotes", jsonData);
                            intent.putExtra("dno", dnoValue);

                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Log.d("Error.Response", "error" + error.getMessage())
            );

// add it to the RequestQueue
            queue.add(getRequest);


        });

        dia_update.setOnClickListener(v -> {
            Log.d(TAG, "saveRemote: " + type_brand);

            JSONObject saveRemote = new JSONObject();
            try {


                saveRemote.put("R_Type", typeET.getText().toString());
                saveRemote.put("R_Brand", brandNameET.getText().toString());
                saveRemote.put("Format", 31);
                saveRemote.put("Name", name.getText().toString());
                saveRemote.put("keys", configure_mode);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "saveRemote: " + saveRemote);
            findViewById(R.id.progressBar).setVisibility(VISIBLE);
            Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
            RequestQueue queue = Volley.newRequestQueue(this);
//            final String url = "http://209.58.164.151:88/api/Get/EditRemote?index=" + index + "&device=" + dnoValue + "&channel=ir";
            final String url = APIClient.BASE_URL + "/api/Get/EditRemote?index=" + index + "&device=" + dnoValue + "&channel=ir";

            JSONObject data = new JSONObject();
            try {
                data.put("remotedata", saveRemote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, saveRemote + " saveRemote: " + url);
// prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(POST, url, data,
                    response -> {
                        Log.d("Response", "response " + response);
                        // display response
                        findViewById(R.id.progressBar).setVisibility(GONE);
                        try {
                            dia_save.dismiss();
                            saveRemote.put("index", index);
                            saveRemote.put("oprtype", "update");

                            JSONObject toUpdateJSOn = new JSONObject();
                            toUpdateJSOn.put("dno", dnoValue);
                            toUpdateJSOn.put("ir", saveRemote);


                            String jsonData = udpdateJSON(RemoteActivity.this, toUpdateJSOn.toString());

                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RemoteActivity.this, RemoteMenu.class);
                            intent.putExtra("remotes", jsonData);
                            intent.putExtra("dno", dnoValue);

                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Log.d("Error.Response", "error" + error.getMessage())
            );

// add it to the RequestQueue
            queue.add(getRequest);


        });


        Log.d(TAG, "onClick: ");

    }

    public void reconfigure(View v) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Reconfigure")
                .setMessage("Would like to reconfigure ? \n " + v.getTag().toString().replaceAll("_", " ").toUpperCase())
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = ProgressDialog.show(RemoteActivity.this, "",
                                "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
                        progressDialog.setCanceledOnTouchOutside(true);
                        ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
                        DtypeViews.RemotClickConfigure(RemoteActivity.this, dnoValue, "irs");
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private class setInRemote extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... strings) {
            String Rdata = strings[0];
            progressDialog.dismiss();

            Log.d(TAG, "updateUI: rawData " + Rdata);
            try {
                String value1 = Rdata.split("=")[1].trim();
                value1 = "38000," + value1.split("//")[0].trim()
                        .replace("{", "")
                        .replace("};", "");


                JSONObject keys_config = new JSONObject();
                keys_config.put("key", ButtonClicked);
                keys_config.put("value", value1);
                int arrayLength = configure_mode.length();
                if (arrayLength != 0) {
                    for (int i = 0; i < arrayLength; i++) {
                        if (configure_mode.getJSONObject(i).toString().toLowerCase().contains(ButtonClicked.toLowerCase()))
                            configure_mode.put(keys_config);
                        else
                            configure_mode.put(keys_config);
                    }
                } else
                    configure_mode.put(keys_config);

                if (ButtonClicked.contains("DIGIT")) {
                    View view = numberpad.findViewWithTag(ButtonClicked);
                    view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                    String finalValue1 = value1;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                            DtypeViews.RemotClick(RemoteActivity.this, dnoValue, finalValue1, "irs");
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            reconfigure(v);
                            return false;
                        }
                    });
                } else {
                    View view = RemoteHolder.findViewWithTag(ButtonClicked);
                    view.getResources().getDrawable(R.drawable.remote_button_click_2);
                    view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                    Log.d(TAG, "onCreate: " + ButtonClicked);
                    String finalValue = value1;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(RemoteActivity.this, dno, Toast.LENGTH_SHORT).show();
                            DtypeViews.RemotClick(RemoteActivity.this, dnoValue, finalValue, "irs");
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            reconfigure(v);
                            return false;
                        }
                    });
                }

                Log.d(TAG, "updateUI: " + configure_mode.toString());
                Log.d(TAG, "updated: " + configure_mode.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {

            super.onPostExecute(aLong);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
