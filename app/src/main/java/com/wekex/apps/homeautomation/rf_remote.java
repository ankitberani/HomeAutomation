package com.wekex.apps.homeautomation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.Constants.getDrawableByName;
import static com.wekex.apps.homeautomation.utils.Constants.jsonobjectSTringJSON;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class rf_remote extends AppCompatActivity {

    String rf_icons[] = {"power", "play", "pause", "up", "down", "stop", "stop2", "next", "previous", "last", "first"};
    LinearLayout buttonHolder;
    private String TAG = "RF_REmote";
    ArrayList<String> remoteButtonsArray;
    String dnoValue = "84F3EBB3B945";
    ProgressDialog progressDialog;
    String ButtonClicked;
    String mode = "0";
    LinearLayout RemoteHolder;
    JSONArray configure_mode;
    private String keys = "";
    JSONArray RemotesKeys;
    String remoteData;
    String index = "na";
    JSONObject editRemoteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rf_remote);
        configure_mode = new JSONArray();
        buttonHolder = findViewById(R.id.buttonHolder);
        RemoteHolder = findViewById(R.id.remoteHolder);
        remoteButtonsArray = new ArrayList<>();
        showRemote();
        for (String buttonName : rf_icons) {
            View view = getLayoutInflater().inflate(R.layout.rf_buttom_image_holder, null, false);
            ImageView imageView = view.findViewById(R.id.image);
            String imageName = "rf_" + buttonName;
            imageView.setTag(buttonName.toUpperCase());
            Log.d(TAG, "onCreate: " + imageName);
            imageView.setImageDrawable(getDrawableByName(imageName, this));
            imageView.setOnClickListener(v -> {
                remoteButtonsArray.add(buttonName);
                addButton();
            });
            buttonHolder.addView(view);
        }
    }

    private void showRemote() {
        String type = getIntent().getStringExtra("type");
        mode = getIntent().getStringExtra("mode");
        dnoValue = getIntent().getStringExtra("dno");
//        Log.d(TAG, "onCreate: " + mode);


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
                    LinearLayout buttonHolderRow = new LinearLayout(this);
                    RemoteHolder.removeAllViews();
                    buttonHolderRow.removeAllViews();
                    int no_of_button = 1;
                    int row_no = 0;

                    for (int i = 0; i < RemotesKeys.length(); i++) {
                        JSONObject jsonObject = RemotesKeys.getJSONObject(i);
                        String value = jsonObject.getString("value");
                        String key = jsonObject.getString("key");

                        View view = getLayoutInflater().inflate(R.layout.rf_buttom_image_holder, null, false);
                        ImageView imageView = view.findViewById(R.id.image);
                        String imageName = "rf_" + key;
                        imageView.setTag(key.toUpperCase());
                        imageView.setEnabled(true);
                        Log.d(TAG, "onCreate: " + imageName.toLowerCase());

                        imageView.setImageDrawable(getDrawableByName(imageName.toLowerCase(), this));
                        imageView.setOnClickListener(v -> {
                            // remoteButtonsArray.add(buttonName);
                            // addButton();
                            DtypeViews.RemotClick(rf_remote.this, dnoValue, value, "rf");
                        });
                        buttonHolderRow.addView(view);
                        if (no_of_button == 3) {
                            RemoteHolder.removeViewAt(row_no);
                            RemoteHolder.addView(buttonHolderRow);
                            buttonHolderRow = new LinearLayout(this);
                            no_of_button = 1;
                            row_no++;
                        } else {
                            if (no_of_button != 1)
                                RemoteHolder.removeViewAt(row_no);
                            RemoteHolder.addView(buttonHolderRow);
                            no_of_button++;
                        }
                    }//For loop End
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else if (mode.equals("0")) {
            findViewById(R.id.saveRemote).setVisibility(View.VISIBLE);
            findViewById(R.id.hscrll).setVisibility(View.VISIBLE);
        } else if (mode.equals("2")) {
            findViewById(R.id.saveRemote).setVisibility(View.VISIBLE);
            findViewById(R.id.hscrll).setVisibility(View.VISIBLE);

            remoteData = getIntent().getStringExtra("remoteData");
            Log.d(TAG, "showRemote: " + remoteData);
            index = getIntent().getStringExtra("index");
            findViewById(R.id.saveRemote).setVisibility(VISIBLE);
            try {
                editRemoteData = new JSONObject(remoteData);

                if (editRemoteData.has("keys"))
                    RemotesKeys = editRemoteData.getJSONArray("keys");
                else if (editRemoteData.has("Keys"))
                    RemotesKeys = editRemoteData.getJSONArray("Keys");


                configure_mode = RemotesKeys;
                LinearLayout buttonHolderRow = new LinearLayout(this);
                RemoteHolder.removeAllViews();
                buttonHolderRow.removeAllViews();
                int no_of_button = 1;
                int row_no = 0;

                for (int i = 0; i < RemotesKeys.length(); i++) {
                    JSONObject jsonObject = RemotesKeys.getJSONObject(i);
                    String value = jsonObject.getString("value");
                    String key = jsonObject.getString("key");

                    View view = getLayoutInflater().inflate(R.layout.rf_buttom_image_holder, null, false);
                    ImageView imageView = view.findViewById(R.id.image);
                    String imageName = "rf_" + key;
                    imageView.setTag(key.toUpperCase());
                    imageView.setEnabled(true);
                    remoteButtonsArray.add(key.toLowerCase());
                    Log.d(TAG, "onCreate: " + imageName.toLowerCase());

                    imageView.setImageDrawable(getDrawableByName(imageName.toLowerCase(), this));
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            reconfigure(v);
                            return false;
                        }
                    });
                    imageView.setOnClickListener(v -> {
                        // remoteButtonsArray.add(buttonName);
                        // addButton();
                        DtypeViews.RemotClick(rf_remote.this, dnoValue, value, "rf");
                    });
                    buttonHolderRow.addView(view);
                    if (no_of_button == 3) {
                        RemoteHolder.removeViewAt(row_no);
                        RemoteHolder.addView(buttonHolderRow);
                        buttonHolderRow = new LinearLayout(this);
                        no_of_button = 1;
                        row_no++;
                    } else {
                        if (no_of_button != 1)
                            RemoteHolder.removeViewAt(row_no);
                        RemoteHolder.addView(buttonHolderRow);
                        no_of_button++;
                    }

                }//For loop End
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void addButton() {

        LinearLayout buttonHolderRow = new LinearLayout(this);
        RemoteHolder.removeAllViews();
        buttonHolderRow.removeAllViews();
        int no_of_button = 1;
        int row_no = 0;
        for (String buttonName : remoteButtonsArray) {
            View view = getLayoutInflater().inflate(R.layout.rf_buttom_image_holder, null, false);
            ImageView imageView = view.findViewById(R.id.image);
            String imageName = "rf_" + buttonName;
            imageView.setTag(buttonName.toUpperCase());
            imageView.setEnabled(true);
            Log.d(TAG, "onCreate: " + imageName);

            imageView.setImageDrawable(getDrawableByName(imageName, this));

            imageView.setOnClickListener(v -> {
                // remoteButtonsArray.add(buttonName);
                // addButton();
                remoteClick(v);
            });
            imageView.setOnLongClickListener(v -> {
                // remoteButtonsArray.add(buttonName);
                // addButton();
                reconfigure(v);
                return false;
            });

            buttonHolderRow.addView(view);
            if (no_of_button == 3) {
                RemoteHolder.removeViewAt(row_no);
                RemoteHolder.addView(buttonHolderRow);
                buttonHolderRow = new LinearLayout(this);
                no_of_button = 1;
                row_no++;
            } else {
                if (no_of_button != 1)
                    RemoteHolder.removeViewAt(row_no);
                RemoteHolder.addView(buttonHolderRow);
                no_of_button++;
            }
        }
    }

    public void remoteClick(View v) {
        View view;
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        if (mode.equals("1"))
            Log.d(TAG, "remoteClick: ");
            //Toast.makeText(this, "You have Pressed "+v.getTag().toString(), Toast.LENGTH_SHORT).show();
        else if (mode.equals("0")) {
            progressDialog = ProgressDialog.show(rf_remote.this, "",
                    "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
            progressDialog.setCanceledOnTouchOutside(true);
            ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
            DtypeViews.RemotClickConfigure(rf_remote.this, dnoValue, "rf");
        } else if (mode.equals("2")) {
            progressDialog = ProgressDialog.show(rf_remote.this, "",
                    "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
            progressDialog.setCanceledOnTouchOutside(true);
            ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
            DtypeViews.RemotClickConfigure(rf_remote.this, dnoValue, "rf");
        }


    }

    private void updateUI(Intent intent) {
        String Rdata = intent.getStringExtra("datafromService");
        Log.d(TAG, "updateUI: " + Rdata);
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    public void saveRemote(View view) {


        Dialog dia_save = new Dialog(rf_remote.this);
        View view1 = LayoutInflater.from(rf_remote.this).inflate(R.layout.dialog_remote_save, null, false);
        dia_save.setContentView(view1);
        dia_save.show();

        ImageView close = view1.findViewById(R.id.close);
        Button dia_add = view1.findViewById(R.id.dia_add);
        Button dia_update = view1.findViewById(R.id.dia_update);

        EditText name = view1.findViewById(R.id.dia_name);
        EditText model = view1.findViewById(R.id.dia_model);
        EditText typeET = view1.findViewById(R.id.r_type);
        EditText brandNameET = view1.findViewById(R.id.brandName);
        //brandNameET.setText(brand);
        //typeET.setText(type);
        if (!index.equals("na")) {
            typeET.setText(jsonobjectSTringJSON(editRemoteData, "R_Type"));
            brandNameET.setText(jsonobjectSTringJSON(editRemoteData, "R_Brand"));
            name.setText(jsonobjectSTringJSON(editRemoteData, "Name"));

            dia_add.setVisibility(GONE);
            dia_update.setVisibility(VISIBLE);
        }
        close.setOnClickListener(v -> {
            dia_save.dismiss();
        });

        dia_add.setOnClickListener(v -> {
            Log.d(TAG, "saveRemote: ");


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
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = APIClient.BASE_URL + "/api/Get/UpdateRemote?device=" + dnoValue + "&channel=rf";

            Log.e("TAG", "URL to add Remote " + url);
            JSONObject data = new JSONObject();
            try {
                data.put("remotedata", saveRemote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "saveRemote: " + url);
// prepare the Request
            JsonObjectRequest getRequest = new JsonObjectRequest(POST, url, data,
                    response -> {
                        Log.d("Response", "response " + response);
                        // display response
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        try {
                            dia_save.dismiss();
                            JSONObject toUpdateJSOn = new JSONObject();
                            toUpdateJSOn.put("dno", dnoValue);
                            toUpdateJSOn.put("rf", saveRemote);

                            String jsonData = udpdateJSON(rf_remote.this, toUpdateJSOn.toString());
                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            finish();
                            /*
                            Intent intent = new Intent(rf_remote.this, RemoteMenu.class);
                            intent.putExtra("remotes", jsonData);
                            intent.putExtra("dno", dnoValue);

                            startActivity(intent);
                           */
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
            final String url = APIClient.BASE_URL + "/api/Get/EditRemote?index=" + index + "&device=" + dnoValue + "&channel=rf";

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
                            toUpdateJSOn.put("rf", saveRemote);


                            String jsonData = udpdateJSON(rf_remote.this, toUpdateJSOn.toString());

                            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(rf_remote.this, RemoteMenu.class);
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
                        progressDialog = ProgressDialog.show(rf_remote.this, "",
                                "Confuring \"" + v.getTag().toString().replaceAll("_", " ").toUpperCase() + "\" Please wait...", true);
                        progressDialog.setCanceledOnTouchOutside(true);
                        ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
                        DtypeViews.RemotClickConfigure(rf_remote.this, dnoValue, "rfl");
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public void onClick(DialogInterface dialog, int which) {
                        ButtonClicked = v.getTag().toString().replaceAll("_", " ").toUpperCase();
                        remoteButtonsArray.remove(ButtonClicked.toLowerCase());

                        try {


                            int arrayLength = configure_mode.length();
                            if (arrayLength != 0) {
                                for (int i = 0; i < arrayLength; i++) {
                                    if (configure_mode.getJSONObject(i).toString().toLowerCase().contains(ButtonClicked.toLowerCase()))
                                        configure_mode.remove(i);
                                    else
                                        configure_mode.remove(i);
                                }
                            }
                        } catch (JSONException e) {
                            e.getStackTrace();
                            Log.d(TAG, "onClick: Delete" + e.getMessage());
                        }

                        addButton();
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void close(View view) {
        finish();
    }

    private class setInRemote extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... strings) {
            String Rdata = strings[0];
            progressDialog.dismiss();
            Log.d(TAG, "updateUI: rawData " + Rdata);
            try {
                String value1 = Rdata.split("=")[1].trim();
                value1 = value1.split("//")[0].trim()
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
                    /*View view = numberpad.findViewWithTag(ButtonClicked);
                    view.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));
                    String finalValue1 = value1;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Toast.makeText(rf_remote.this, dno, Toast.LENGTH_SHORT).show();
                            DtypeViews.RemotClick(rf_remote.this,dnoValue, finalValue1,"rfs");
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            reconfigure(v);

                            return false;
                        }
                    });*/
                } else {
                    Log.d(TAG, "onCreate: " + ButtonClicked);
                    View view = RemoteHolder.findViewWithTag(ButtonClicked);
                    view.setEnabled(true);
                    Log.d(TAG, "onCreate: " + ButtonClicked);
                    String finalValue = value1;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(rf_remote.this, dno, Toast.LENGTH_SHORT).show();
                            DtypeViews.RemotClick(rf_remote.this, dnoValue, finalValue, "rfs");
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
            ButtonClicked = "";
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {

            super.onPostExecute(aLong);
        }
    }

}
