package com.wekex.apps.homeautomation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.GET;
import static com.wekex.apps.homeautomation.utils.Constants.backcolor;
import static com.wekex.apps.homeautomation.utils.Constants.getJsonArray;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPutObject;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;
import static com.wekex.apps.homeautomation.utils.Constants.room_Id;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.Constants.setIMagetoView;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class RemoteMenu extends AppCompatActivity {
    GridLayout remoteHolder;
    private String TAG = "RemoteActivity";
    private int remoteSelected = 1;
    String dnoValue;
    boolean isfinish;
    Spinner window;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isfinish = true;
        setContentView(R.layout.activity_remote_menu);
        remoteHolder = findViewById(R.id.remoteHolder);

        remoteHolder.setColumnCount(2);

        dnoValue = getIntent().getStringExtra("dno");
        String remotes = getIntent().getStringExtra("remotes");
        Log.d(TAG, "onCreate: " + remotes);

        if (remotes.contains("ir")) {
            JSONObject jsonObject = stringToJsonObject(remotes);
            // JSONObject IRremotes = jsonobjectByName(jsonObject.toString(),"ir");
            //  JSONObject RFremotes = jsonobjectByName(jsonObject.toString(),"ir");

            JSONArray jsonArray = getJsonArray(jsonObject, "ir");
            if (jsonArray != null) {
//                 Log.d(TAG, jsonArray.toString()+" onCreate: 222"+jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {

                    View view = LayoutInflater.from(this).inflate(R.layout.remote_list_item, null, false);
                    LinearLayout remoteItem = view.findViewById(R.id.remoteItem);

                    TextView brandName = view.findViewById(R.id.brandName);
                    ImageView setting = view.findViewById(R.id.setting);
                    ImageView rmicon = view.findViewById(R.id.rmicon);

                    String imgTag = "IR_" + i;
                    setting.setTag(imgTag);

                    try {
                        Log.d(TAG, "onCreate: String using Replace  " + jsonArray.getString(i).replace("\\", ""));
                        String InvalidJSON = jsonArray.getString(i).replace("\\", "").trim();
                        JSONObject remo;
                        if (InvalidJSON.startsWith("\""))
                            remo = new JSONObject(InvalidJSON.substring(1, InvalidJSON.length() - 1));
                        else
                            remo = new JSONObject(InvalidJSON);

                        //   JSONObject remo = new JSONObject("\""+jsonArray.getString(i).replace("\\","")+"\"");
                        savetoShared(this).edit().putString(imgTag, remo.toString()).apply();
                        brandName.setText(remo.getString("Name"));
                        String R_Type = remo.getString("R_Type");
                        String icon = "icons/" + R_Type.toLowerCase();
                        setIMagetoView(icon, rmicon, RemoteMenu.this);
                        String keys = "";
                        if (remo.has("keys"))
                            keys = remo.getString("keys");
                        else if (remo.has("Keys"))
                            keys = remo.getString("Keys");

                        Log.d(TAG, "onCreate: ");
                        String keysAssigned = keys;
                        remoteItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (R_Type) {
                                    case "TV":
                                        tv_remote(v, keysAssigned);
                                        break;
                                    case "AC":
                                        ac_remote(v, keysAssigned);
                                        break;
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    GridLayout.LayoutParams param = new GridLayout.LayoutParams();

                    param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);

                    param.width = 0;

                    view.setLayoutParams(param);

                    remoteHolder.addView(view);
                }
            }
        }
        try {

            if (remotes.contains("rf")) {
                JSONObject jsonObject = stringToJsonObject(remotes);
                // JSONObject IRremotes = jsonobjectByName(jsonObject.toString(),"ir");
                //  JSONObject RFremotes = jsonobjectByName(jsonObject.toString(),"ir");

                JSONArray jsonArray = getJsonArray(jsonObject, "rf");
                if (jsonArray != null) {
//                 Log.d(TAG, jsonArray.toString()+" onCreate: 222"+jsonArray.length());
                    int remoteCount = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {

                        View view = LayoutInflater.from(this).inflate(R.layout.remote_list_item, null, false);
                        LinearLayout remoteItem = view.findViewById(R.id.remoteItem);

                        TextView brandName = view.findViewById(R.id.brandName);
                        ImageView setting = view.findViewById(R.id.setting);
                        ImageView rmicon = view.findViewById(R.id.rmicon);

                        try {
                            String imgTag = "RF_" + remoteCount;
                            setting.setTag(imgTag);
                            Log.d(TAG, imgTag + " onCreate: String using Replace  " + jsonArray.getString(i).replace("\\", ""));
                            String InvalidJSON = jsonArray.getString(i).replace("\\", "").trim();
                            JSONObject remo;
                            if (InvalidJSON.startsWith("\""))
                                remo = new JSONObject(InvalidJSON.substring(1, InvalidJSON.length() - 1));
                            else
                                remo = new JSONObject(InvalidJSON);
                            savetoShared(this).edit().putString(imgTag, remo.toString()).apply();
                            //   JSONObject remo = new JSONObject("\""+jsonArray.getString(i).replace("\\","")+"\"");
                            brandName.setText(remo.getString("Name"));
                            String R_Type = remo.getString("R_Type");
                            String icon = "icons/" + R_Type.toLowerCase();
                            setIMagetoView(icon, rmicon, RemoteMenu.this);
                            String keys = "";
                            if (remo.has("keys"))
                                keys = remo.getString("keys");
                            else if (remo.has("Keys"))
                                keys = remo.getString("Keys");

                            Log.d(TAG, "onCreate: ");
                            String keysAssigned = keys;
                            remoteItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rf_remote("1", keysAssigned);

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GridLayout.LayoutParams param = new GridLayout.LayoutParams();

                        param.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f);

                        param.width = 0;

                        view.setLayoutParams(param);

                        remoteHolder.addView(view);
                        remoteCount++;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("TAG", "Exception at remote menu " + ex.getMessage());
        }

    }

    public void device_menu_list(View view) {
        String ir_index = view.getTag().toString();
        PopupMenu popup = new PopupMenu(RemoteMenu.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.remote_setting, popup.getMenu());

        //registering popup with OnMenuItemClickListener

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        Log.d(TAG, "onMenuItemClick: " + ir_index);
                        delete(ir_index);
                        break;
                    case R.id.edit:
                        edit(ir_index);
                        break;
                }

                return true;
            }
        });
        popup.show();
    }

    private void delete(String ir_index) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        String[] irRfIN = ir_index.split("_");
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = APIClient.BASE_URL + "/api/Get/DeleteRemote?dNo=" + dnoValue + "&irRF=" + irRfIN[0].toLowerCase() + "&index=" + irRfIN[1];
        Log.d(TAG, "delete: " + url);
        StringRequest getRequest = new StringRequest(GET, url,
                response -> {
                    Log.d("Response", "response " + response);
                    // display response
                    JSONObject delete = new JSONObject();
                    jsonObjectPut(delete, "index", irRfIN[1]);
                    jsonObjectPut(delete, "oprtype", "delete");

                    JSONObject toUpdateJSOn = new JSONObject();
                    jsonObjectPut(toUpdateJSOn, "dno", dnoValue);

                    jsonObjectPutObject(toUpdateJSOn, irRfIN[0].toLowerCase(), delete);

                    String jsonData = udpdateJSON(RemoteMenu.this, toUpdateJSOn.toString());

                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RemoteMenu.this, RemoteMenu.class);
                    intent.putExtra("remotes", jsonData);
                    intent.putExtra("dno", dnoValue);

                    startActivity(intent);
                    finish();

                },
                error -> {
                    Log.d("Error.Response", "error" + error.getMessage());
                    Toast.makeText(this, "Unable to Delete", Toast.LENGTH_SHORT).show();
                }
        );
// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void edit(String ir_index) {
        Log.d(TAG, "edit: " + ir_index);

        String[] irRfIN = ir_index.split("_");
        String remo = savetoShared(this).getString(ir_index, "na");

        if (!remo.equals("na")) {
            Intent intent;
            if (irRfIN[0].equals("IR")) {
                intent = new Intent(RemoteMenu.this, RemoteActivity.class);
            } else {
                intent = new Intent(RemoteMenu.this, rf_remote.class);
            }
            intent.putExtra("index", irRfIN[1]);
            intent.putExtra("dno", dnoValue);
            intent.putExtra("remoteData", remo);
            intent.putExtra("type", jsonObjectreader(remo, "R_Type").toLowerCase());
            intent.putExtra("mode", "2");// "0" for editing remote
            startActivity(intent);
        }
        JSONObject remoObject = stringToJsonObject(remo);




        /*
        String url = "http://demo/api/Get/Remote?type="+jsonobjectByName(remo,"R_Type")+"&brand="+jsonobjectByName(remo,"R_Brand")+"&model="+jsonobjectByName(remo,"NA");
        Uri uri = Uri.parse(R_Model);

        String type = uri.getQueryParameter("type");
        Log.d(TAG, "startRemoteManual: "+type);
        Intent intent = new Intent(RemoteMenu.this,RemoteActivity.class);
        intent.putExtra("index",)
        intent.putExtra("dno",dnoValue);
        intent.putExtra("uri",url);
        intent.putExtra("type",type.toLowerCase());
        intent.putExtra("mode","0");// "0" for configure remote
        startActivity(intent);*/
    }

    Intent intent;

    public void ac_remote(View view, String keys) {
        Intent intent = new Intent(RemoteMenu.this, RemoteActivity.class);
        intent.putExtra("keys", keys);
        intent.putExtra("type", "ac");
        intent.putExtra("mode", "1");// "1" to working as remote
        startActivity(intent);
    }

    public void tv_remote(View view, String keys) {
        Intent intent = new Intent(RemoteMenu.this, RemoteActivity.class);
        intent.putExtra("keys", keys);
        intent.putExtra("dno", dnoValue);
        intent.putExtra("type", "tv");
        intent.putExtra("mode", "1");// "1" for working remote
        startActivity(intent);
    }

    public void rf_remote(String mode, String keysAssigned) {
        Intent intent = new Intent(this, rf_remote.class);
        if (!mode.equals("0"))
            intent.putExtra("keys", keysAssigned);
        intent.putExtra("dno", dnoValue);
        intent.putExtra("type", "rf");
        intent.putExtra("mode", mode);// "1" for working remote
        startActivity(intent);
    }

    public void chooseRemote(String r_type) {
        Intent intent;
        if (r_type.equals("ir"))
            intent = new Intent(this, selectRemote.class);
        else
            intent = new Intent(this, RF_selectRemote.class);

        intent.putExtra("dno", dnoValue);
        intent.putExtra("r_type", r_type);
        startActivityForResult(intent, remoteSelected);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == remoteSelected) {
            if (resultCode == Activity.RESULT_OK) {
                DtypeViews.getGetDevice(this);
                String result = data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: " + result);
                addRemotetoList(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No Remote Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addRemotetoList(String result) {

        View view = LayoutInflater.from(this).inflate(R.layout.remote_selectlist_item, null, false);
        // LinearLayout remoteItem = view.findViewById(R.id.remoteItem);

        TextView brandName = view.findViewById(R.id.brandName);
        TextView r_type = view.findViewById(R.id.r_type);
        LinearLayout remoteCard = view.findViewById(R.id.remoteCard);
        try {
            String keys = result;
            intent = new Intent(RemoteMenu.this, RemoteActivity.class);
            JSONObject remo = Constants.stringToJsonObject(result);
            if (remo.has("R_Brand"))
                brandName.setText(remo.getString("R_Brand"));
            String R_Type = remo.getString("R_type");
            r_type.setText(R_Type);
            // String R_Brand = remo.getString("R_Brand");
            String Format = remo.getString("Format");
            remoteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (R_Type) {
                        case "TV":
                            tv_remote(v, keys);
                            break;
                        case "AC":
                            ac_remote(v, keys);
                            break;
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "updateUI: ");
        }


        remoteHolder.addView(view);
    }

    @Override
    public void onBackPressed() {
        if (isfinish) {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.putExtra("room_id", room_Id);
            intent.putExtra("backcolor", backcolor);
            startActivity(intent);
        }

    }


    public void addRemote(View view) {
        View power_button_dialog = LayoutInflater.from(this).inflate(R.layout.dia_rf_ir, null, false);
        Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(power_button_dialog);
        CardView rfRemote = power_button_dialog.findViewById(R.id.rfRemote);
        CardView irRemote = power_button_dialog.findViewById(R.id.irRemote);
        //manual.setVisibility(View.GONE);


        rfRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseRemote("rf");
                dialog.dismiss();
            }
        });
        irRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseRemote("ir");
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}