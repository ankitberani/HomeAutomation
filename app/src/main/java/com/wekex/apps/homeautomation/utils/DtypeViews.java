package com.wekex.apps.homeautomation.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.RemoteMenu;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import androidx.cardview.widget.CardView;

import static com.wekex.apps.homeautomation.utils.Constants.DEVICETYPES;
import static com.wekex.apps.homeautomation.utils.Constants.cardColorup;
import static com.wekex.apps.homeautomation.utils.Constants.colorpickerActivity;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPutInt;
import static com.wekex.apps.homeautomation.utils.Constants.myvisibility;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;

public class DtypeViews {

    public static final String dno = "dno";
    public static final String pname = "name";
    public static final String d1pname = "name";
    public static final String enable = "enable";
    public static final String isOnline = "isOnline";
    public static final String SIGNAL = "signal";
    public static final String key = "key";
    public static final String room = "room";
    public static final String dtype = "dtype";
    public static final String TAG = "DtypeViews";

    public static void jsonDeviceUpdater(Context context, String data) {
    }

    public static View Dtype1(final Context context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);


        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        String state = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "state");
        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype1_switchdevice, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        device_icon.setImageDrawable(drawable);

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        if (!Boolean.parseBoolean(isOnline1)) {
            power.setVisibility(view.INVISIBLE);//Gone to Replace
        }
        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));
        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        ImageView showMenu = view.findViewById(R.id.showMenu);

        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));
        aSwitch.setChecked(Boolean.parseBoolean(state));

        TextView devicename = view.findViewById(R.id.devicename); //Device name
        devicename.setTag(dno1 + "devicename");
//        devicename.setText(pname1);

        String name = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "name");
        devicename.setText(name);
        Log.e("TAGG", "Name SEted is " + pname1 + " dno " + dno1 + " name " + name);
        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {

            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");
        parent.setOnLongClickListener(v -> {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Remove Device")
                    .setMessage("Are you sure you want to Remove this Device ?")
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeDeviceFromRoom(context, aSwitch.getTag().toString().replace("switch1", ""));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return false;
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
                /* */
            }
        });
        //parent.setBackgroundColor(getMyDrawable(context));
        return view;
    }

    private static String getDeviceNamebyType(String i, Context context) {
        String deviceTypes = savetoShared(context).getString(DEVICETYPES, "NA");
        String dName = "";
        if (!deviceTypes.equals("NA")) {
            Log.d(TAG, i + " onCreate: " + deviceTypes);
            try {
                JSONArray jarray = new JSONArray(deviceTypes);
                JSONObject jsonObject = jarray.getJSONObject(Integer.parseInt(i));
                dName = jsonObject.getString("Name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dName;
    }

    public static View Dtype2(final Activity context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);

        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        JSONObject jsonObject1check = Constants.jsonobject(jsonObject.toString(), "d1");
        if (!jsonObject1check.has("state") || !jsonObject1check.has("r") || !jsonObject1check.has("g") || !jsonObject1check.has("b")) {

        }

        String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), d1pname);
        String state = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "state");
        String red = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "r");
        String green = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "g");
        String blue = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "b");
        JSONObject myD1 = Constants.jsonobject(jsonObject.toString(), "d1");
        //if (myD1.)
        Constants.brightness = Math.round(Float.parseFloat(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "br")));
        Constants.warm_white = Integer.parseInt(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "ww"));
        int color = android.graphics.Color.argb(255, Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));


        Log.d(TAG, "Dtype2: " + Constants.createPublishJson(dno1, key1, type1, d1pname1, room1));
        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, d1pname1, room1)).apply();
        Constants.savetoShared(context).edit().putString(dno1 + "hello", Constants.createPublishJson(dno1, key1, type1, d1pname1, room1)).apply();

        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype_rbg_normal, null, false);

        ImageView device_icon = view.findViewById(R.id.device_icon);
        device_icon.setColorFilter(color);
        device_icon.setTag(dno1 + "device_icon");

        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);
        showMenu.setTag(dno1 + "showMenu");
        //        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        TextView devicetypename = view.findViewById(R.id.devicetypename);
//        devicetypename.setText(getDeviceNamebyType(type1, context));


        TextView currentColor = view.findViewById(R.id.currentColor);
        currentColor.setBackgroundColor(color);
        currentColor.setTag(color);

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        Drawable drawable = HomePage.getDrawable(context, type1);
        device_icon.setImageDrawable(drawable);


        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));
        aSwitch.setChecked(Boolean.parseBoolean(state));
        //parent.setBackgroundColor(color);

        TextView devicename = view.findViewById(R.id.devicename); //Device name
//        devicename.setText(pname1);
        devicename.setTag(dno1 + "devicename");

        try {
            JSONObject obj = myD1.getJSONObject("d1");
            {
                if (obj.has("name")) {
                    Log.e("TAGG", "DEvice Name Seted " + obj.getString("name"));
                    devicename.setText(obj.getString("name"));
                } else {
                    Log.e("TAGG", "DEvice Name not found ");
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception at set Name " + e.getMessage());
        }

        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (Boolean.parseBoolean(isOnline1)) {

                //////////////////////////INtent for start ///////////////////
                Intent intent = new Intent(context, rgb_controls.class);
                intent.putExtra("jsonString", jsonObject.toString());
                context.startActivityForResult(intent, colorpickerActivity);

                Log.e("TAGGG", "jsonObject at click " + jsonObject.toString());
            /*
                }else {
                    Toast.makeText(context, "Device is Offline", Toast.LENGTH_SHORT).show();
                }*/

                //rgbdialog(parent,context,aSwitch);
            }
        });
        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Remove Device")
                        .setMessage("Are you sure you want to Remove this Device ?")
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDeviceFromRoom(context, aSwitch.getTag().toString());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                int color = Integer.parseInt(currentColor.getTag().toString());
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);
                publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), isChecked);
                /* */
            }
        });


        // parent.setBackgroundColor(getMyDrawable(context));
        return view;
    }

    public static View Dtype5(final Context context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);

        Log.e("TAGGG", "Json Object " + jsonObject);

        String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), d1pname);
        String d1pname2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), d1pname);

        Log.d(TAG, d1pname1 + " Dtype5: " + d1pname2);

        String state1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "state");
        String state2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), "state");

        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype5_dual_relay, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        // device_icon.setImageDrawable(drawable);//For Device Icon
        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        CardView switch1Card = view.findViewById(R.id.switch1Card);
        switch1Card.setTag(dno1 + "switch1Card");

        CardView switch2Card = view.findViewById(R.id.switch2Card);
        switch2Card.setTag(dno1 + "switch2Card");


        ImageView showSwitches = view.findViewById(R.id.showSwitches);
        showSwitches.setVisibility(View.VISIBLE);


        LabeledSwitch labeledSwitch1 = view.findViewById(R.id.subswitch1);
        labeledSwitch1.setTag(dno1 + "labeledSwitch1");

        LabeledSwitch labeledSwitch2 = view.findViewById(R.id.subswitch2);
        labeledSwitch2.setTag(dno1 + "labeledSwitch2");

        ImageView subswitch1IV = view.findViewById(R.id.subswitch1IV);
        subswitch1IV.setTag(dno1 + "subswitch1IV");

        ImageView subswitch2IV = view.findViewById(R.id.subswitch2IV);
        subswitch2IV.setTag(dno1 + "subswitch2IV");

        TextView subswitch1TV = view.findViewById(R.id.subswitch1TV);
        subswitch1TV.setTag(dno1 + "subswitch1TV");
        subswitch1TV.setText(d1pname1);

        TextView subswitch2TV = view.findViewById(R.id.subswitch2TV);
        subswitch2TV.setTag(dno1 + "subswitch2TV");
        subswitch2TV.setText(d1pname2);

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        if (!Boolean.parseBoolean(isOnline1)) {
            power.setVisibility(view.INVISIBLE);//Gone to Replace
        }


        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);

        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        LinearLayout switch1LL = view.findViewById(R.id.switch1LL);
        setTagAndBackground(Boolean.parseBoolean(state1), switch1LL, subswitch1IV);

        LinearLayout switch2LL = view.findViewById(R.id.switch2LL);
        setTagAndBackground(Boolean.parseBoolean(state2), switch2LL, subswitch2IV);

        switch1LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 1");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch1LL, subswitch1IV);
                pubit_multiButton(context, dno1, !state, "d1");
                // pubit(context,dno1,Boolean.parseBoolean(state));

            }
        });

        switch2LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 2");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch2LL, subswitch2IV);
                pubit_multiButton(context, dno1, !state, "d2");
            }
        });

        switch1LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d1", "5");
                return false;
            }


        });

        switch2LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d2", "5");
                return false;
            }
        });

        int bcolor = getMyDrawable(context);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subswitch2TV.setText(d1pname2);
                LinearLayout linearLayout = view.findViewById(R.id.dualholder);
                linearLayout.setBackgroundColor(bcolor);

                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    showSwitches.setImageResource(R.drawable.ic_up);
                } else {
                    linearLayout.setVisibility(view.GONE);//Gone to Replace
                    showSwitches.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        labeledSwitch1.setOnToggledListener(null);
        labeledSwitch2.setOnToggledListener(null);
        labeledSwitch1.setOn(Boolean.parseBoolean(state1));
        labeledSwitch2.setOn(Boolean.parseBoolean(state2));

        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));

        if (Boolean.parseBoolean(state1) || Boolean.parseBoolean(state2)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        TextView devicename = view.findViewById(R.id.devicename); //Device name
        devicename.setTag(dno1 + "devicename");
//        devicename.setText(pname1);
        devicename.setText(d1pname1);


        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");
        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Remove Device")
                        .setMessage("Are you sure you want to Remove this Device ?")
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDeviceFromRoom(context, aSwitch.getTag().toString().replace("switch1", ""));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
                /* */
            }
        });

        labeledSwitch1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isOn, "d1");
            }
        });

        labeledSwitch2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch2", ""), isOn, "d2");
            }
        });


        //parent.setBackgroundColor(bcolor);
        return view;
    }

    public static View Dtype6(final Context context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);


        String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), d1pname);
        String d1pname2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), d1pname);
        String d1pname3 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d3"), d1pname);
        String d1pname4 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d4"), d1pname);

        Log.d(TAG, d1pname1 + " Dtype5: " + d1pname2);

        String state1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "state");
        String state2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), "state");
        String state3 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d3"), "state");
        String state4 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d4"), "state");

        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype6_quad_relay, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        // device_icon.setImageDrawable(sdrawable);//For Device Icon

        CardView switch1Card = view.findViewById(R.id.switch1Card);
        switch1Card.setTag(dno1 + "switch1Card");
        CardView switch2Card = view.findViewById(R.id.switch2Card);
        switch2Card.setTag(dno1 + "switch2Card");
        CardView switch3Card = view.findViewById(R.id.switch3Card);
        switch3Card.setTag(dno1 + "switch3Card");
        CardView switch4Card = view.findViewById(R.id.switch4Card);
        switch4Card.setTag(dno1 + "switch4Card");


        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        ImageView showSwitches = view.findViewById(R.id.showSwitches);
        showSwitches.setVisibility(View.VISIBLE);

        //Labeled Switch
        LabeledSwitch labeledSwitch1 = view.findViewById(R.id.subswitch1);
        labeledSwitch1.setTag(dno1 + "labeledSwitch1");

        LabeledSwitch labeledSwitch2 = view.findViewById(R.id.subswitch2);
        labeledSwitch2.setTag(dno1 + "labeledSwitch2");

        LabeledSwitch labeledSwitch3 = view.findViewById(R.id.subswitch3);
        labeledSwitch3.setTag(dno1 + "labeledSwitch3");

        LabeledSwitch labeledSwitch4 = view.findViewById(R.id.subswitch4);
        labeledSwitch4.setTag(dno1 + "labeledSwitch4");

        //Labeled subswitch ImageView
        ImageView subswitch1IV = view.findViewById(R.id.subswitch1IV);
        subswitch1IV.setTag(dno1 + "subswitch1IV");

        ImageView subswitch2IV = view.findViewById(R.id.subswitch2IV);
        subswitch2IV.setTag(dno1 + "subswitch2IV");

        ImageView subswitch3IV = view.findViewById(R.id.subswitch3IV);
        subswitch3IV.setTag(dno1 + "subswitch3IV");

        ImageView subswitch4IV = view.findViewById(R.id.subswitch4IV);
        subswitch4IV.setTag(dno1 + "subswitch4IV");

        //Labeled subswitch TextView
        TextView subswitch1TV = view.findViewById(R.id.subswitch1TV);
        subswitch1TV.setTag(dno1 + "subswitch1TV");
        subswitch1TV.setText(d1pname1);

        TextView subswitch2TV = view.findViewById(R.id.subswitch2TV);
        subswitch2TV.setTag(dno1 + "subswitch2TV");
        subswitch2TV.setText(d1pname2);

        TextView subswitch3TV = view.findViewById(R.id.subswitch3TV);
        subswitch3TV.setTag(dno1 + "subswitch3TV");
        subswitch3TV.setText(d1pname3);

        TextView subswitch4TV = view.findViewById(R.id.subswitch4TV);
        subswitch4TV.setTag(dno1 + "subswitch4TV");
        subswitch4TV.setText(d1pname4);

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        if (!Boolean.parseBoolean(isOnline1)) {
            power.setVisibility(view.INVISIBLE);//Gone to Replace
        }
        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);
        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        LinearLayout switch1LL = view.findViewById(R.id.switch1LL);
        setTagAndBackground(Boolean.parseBoolean(state1), switch1LL, subswitch1IV);

        LinearLayout switch2LL = view.findViewById(R.id.switch2LL);
        setTagAndBackground(Boolean.parseBoolean(state2), switch2LL, subswitch2IV);

        LinearLayout switch3LL = view.findViewById(R.id.switch3LL);
        setTagAndBackground(Boolean.parseBoolean(state1), switch3LL, subswitch3IV);

        LinearLayout switch4LL = view.findViewById(R.id.switch4LL);
        setTagAndBackground(Boolean.parseBoolean(state2), switch4LL, subswitch4IV);

        switch1LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 1");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch1LL, subswitch1IV);
                pubit_multiButton(context, dno1, !state, "d1");
                // pubit(context,dno1,Boolean.parseBoolean(state));

            }
        });

        switch2LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 2");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch2LL, subswitch2IV);
                pubit_multiButton(context, dno1, !state, "d2");
            }
        });

        switch3LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 1");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch3LL, subswitch3IV);
                pubit_multiButton(context, dno1, !state, "d3");
                // pubit(context,dno1,Boolean.parseBoolean(state));

            }
        });

        switch4LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 4");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch4LL, subswitch4IV);
                pubit_multiButton(context, dno1, !state, "d4");
            }
        });

        switch1LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d1", "6");
                return false;
            }


        });

        switch2LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d2", "6");
                return false;
            }
        });

        switch3LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d3", "6");
                return false;
            }


        });

        switch4LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d4", "6");
                return false;
            }
        });

        int bcolor = getMyDrawable(context);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subswitch2TV.setText(d1pname2);
                LinearLayout linearLayout = view.findViewById(R.id.dualholder);
                linearLayout.setBackgroundColor(bcolor);

                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    showSwitches.setImageResource(R.drawable.ic_up);
                } else {
                    linearLayout.setVisibility(view.GONE);//Gone to Replace
                    showSwitches.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        labeledSwitch1.setOnToggledListener(null);
        labeledSwitch2.setOnToggledListener(null);
        labeledSwitch1.setOn(Boolean.parseBoolean(state1));
        labeledSwitch2.setOn(Boolean.parseBoolean(state2));

        labeledSwitch3.setOnToggledListener(null);
        labeledSwitch4.setOnToggledListener(null);
        labeledSwitch3.setOn(Boolean.parseBoolean(state3));
        labeledSwitch4.setOn(Boolean.parseBoolean(state4));

        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));

        if (Boolean.parseBoolean(state1) || Boolean.parseBoolean(state2) || Boolean.parseBoolean(state3) || Boolean.parseBoolean(state4)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        TextView devicename = view.findViewById(R.id.devicename); //Device name

        devicename.setTag(dno1 + "devicename");

        String name = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "name");
        devicename.setText(name);
//        devicename.setText(pname1);

        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");

        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Remove Device")
                        .setMessage("Are you sure you want to Remove this Device ?")
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeDeviceFromRoom(context, aSwitch.getTag().toString().replace("switch1", ""));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
                /* */
            }
        });

        labeledSwitch1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isOn, "d1");
            }
        });

        labeledSwitch2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch2", ""), isOn, "d2");
            }
        });

        labeledSwitch3.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch3", ""), isOn, "d3");
            }
        });

        labeledSwitch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch4", ""), isOn, "d4");
            }
        });

        //parent.setBackgroundColor(bcolor);
        return view;
    }

    public static View Dtype7(final Context context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);


        String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), d1pname);
        String d1pname2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), d1pname);

        Log.d(TAG, d1pname1 + " Dtype5: " + d1pname2);

        String state1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "state");
        String state2 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d2"), "state");

//        double br1 = Double.parseDouble(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),"br"));
        //      double br2 = Double.parseDouble(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d2"),"br"));

        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype7_dual_dimmer, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        // device_icon.setImageDrawable(drawable);//For Device Icon

        CardView switch1Card = view.findViewById(R.id.switch1Card);
        switch1Card.setTag(dno1 + "switch1Card");
        CardView switch2Card = view.findViewById(R.id.switch2Card);
        switch2Card.setTag(dno1 + "switch2Card");

        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        ImageView showSwitches = view.findViewById(R.id.showSwitches);
        showSwitches.setVisibility(View.VISIBLE);

        //Labeled Switch
        LabeledSwitch labeledSwitch1 = view.findViewById(R.id.subswitch1);
        labeledSwitch1.setTag(dno1 + "labeledSwitch1");

        LabeledSwitch labeledSwitch2 = view.findViewById(R.id.subswitch2);
        labeledSwitch2.setTag(dno1 + "labeledSwitch2");


        //Labeled subswitch ImageView
        ImageView subswitch1IV = view.findViewById(R.id.subswitch1IV);
        subswitch1IV.setTag(dno1 + "subswitch1IV");

        ImageView subswitch2IV = view.findViewById(R.id.subswitch2IV);
        subswitch2IV.setTag(dno1 + "subswitch2IV");

        //Labeled subswitch TextView
        TextView subswitch1TV = view.findViewById(R.id.subswitch1TV);
        subswitch1TV.setTag(dno1 + "subswitch1TV");
        subswitch1TV.setText(d1pname1);

        TextView subswitch2TV = view.findViewById(R.id.subswitch2TV);
        subswitch2TV.setTag(dno1 + "subswitch2TV");
        subswitch2TV.setText(d1pname2);

        //Seekbar
        SeekBar seekBar1 = view.findViewById(R.id.seek_bar1);

        SeekBar seekBar2 = view.findViewById(R.id.seek_bar2);
        // double br1,br2;

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onStopTrackingTouch: " + seekBar1.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: " + seekBar1.getProgress());
                pubit_slider(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), true, "d1", seekBar1.getProgress());
            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onStopTrackingTouch: " + seekBar2.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: " + seekBar2.getProgress());
                pubit_slider(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), true, "d1", seekBar2.getProgress());
            }
        });

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        if (!Boolean.parseBoolean(isOnline1)) {
            power.setVisibility(view.INVISIBLE);//Gone to Replace
        }
        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);
        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        LinearLayout switch1LL = view.findViewById(R.id.switch1LL);
        setTagAndBackground(Boolean.parseBoolean(state1), switch1LL, subswitch1IV);

        LinearLayout switch2LL = view.findViewById(R.id.switch2LL);
        setTagAndBackground(Boolean.parseBoolean(state2), switch2LL, subswitch2IV);


        switch1LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 1");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch1LL, subswitch1IV);
                pubit_multiButton(context, dno1, !state, "d1");
                myvisibility(seekBar1, !state);
                // pubit(context,dno1,Boolean.parseBoolean(state));

            }
        });

        switch2LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 2");
                Boolean state = (Boolean) v.getTag();
                setTagAndBackground(!state, switch2LL, subswitch2IV);
                pubit_multiButton(context, dno1, !state, "d2");
                myvisibility(seekBar2, !state);
            }
        });


        switch1LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d1", "6");
                return false;
            }


        });

        switch2LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                renameDTypes(dno1, context, key1, "d2", "6");
                return false;
            }
        });


        int bcolor = getMyDrawable(context);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subswitch2TV.setText(d1pname2);
                LinearLayout linearLayout = view.findViewById(R.id.dualholder);
                linearLayout.setBackgroundColor(bcolor);

                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    showSwitches.setImageResource(R.drawable.ic_up);
                } else {
                    linearLayout.setVisibility(view.GONE);//Gone to Replace
                    showSwitches.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        labeledSwitch1.setOnToggledListener(null);
        labeledSwitch2.setOnToggledListener(null);
        labeledSwitch1.setOn(Boolean.parseBoolean(state1));
        labeledSwitch2.setOn(Boolean.parseBoolean(state2));

        myvisibility(seekBar1, Boolean.parseBoolean(state1));
        myvisibility(seekBar2, Boolean.parseBoolean(state2));

        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));

        if (Boolean.parseBoolean(state1) || Boolean.parseBoolean(state2)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        TextView devicename = view.findViewById(R.id.devicename); //Device name

        devicename.setTag(dno1 + "devicename");

        String name = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "name");
        devicename.setText(name);
//        devicename.setText(pname1);

        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");

        parent.setOnLongClickListener(v -> {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Remove Device")
                    .setMessage("Are you sure you want to Remove this Device ?")
                    .setPositiveButton(R.string.remove, (dialog, which) -> removeDeviceFromRoom(context, aSwitch.getTag().toString().replace("switch1", "")))
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return false;
        });

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: " + isChecked);
            pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
            /* */
        });

        labeledSwitch1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_slider(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isOn, "d1", 1);

            }
        });

        labeledSwitch2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_slider(context, labeledSwitch1.getTag().toString().replace("labeledSwitch2", ""), isOn, "d2", 1);
                myvisibility(seekBar2, isOn);
            }
        });


        //parent.setBackgroundColor(bcolor);
        return view;
    }

    public static View Dtype10(Activity context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);


        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        Log.d(TAG, dno1 + " Dtype10: " + Constants.createPublishJson(dno1, key1, type1, pname1, room1));
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype10_remotes, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        device_icon.setImageDrawable(drawable);

        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");
        power.setVisibility(view.INVISIBLE);//Gone to Replace


        if (!Boolean.parseBoolean(isOnline1)) {
            power.setVisibility(view.INVISIBLE);//Gone to Replace
        }
        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);
        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        TextView devicename = view.findViewById(R.id.devicename); //Device name
        devicename.setTag(dno1 + "devicename");
        devicename.setText(pname1);


        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RemoteMenu.class);
                intent.putExtra("remotes", jsonObject.toString());
                intent.putExtra("dno", dno1);
                context.startActivity(intent);

            }
        });

        // parent.setBackgroundColor(getMyDrawable(context));
        return view;
    }

    public static View Dtype11(final Context context, JSONObject jsonObject) {

        String dno1 = Constants.jsonObjectreader(jsonObject.toString(), dno);
        String key1 = Constants.jsonObjectreader(jsonObject.toString(), key);
        String type1 = Constants.jsonObjectreader(jsonObject.toString(), dtype);
        String pname1 = Constants.jsonObjectreader(jsonObject.toString(), pname);
        // String enable1 = Constants.jsonObjectreader(jsonObject.toString(),enable);
        String isOnline1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);
        String room1 = Constants.jsonObjectreader(jsonObject.toString(), isOnline);


        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        //String d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(),"d1"),d1pname);
        String state = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonObject.toString(), "d1"), "door");
        Constants.savetoShared(context).edit().putString(dno1, Constants.createPublishJson(dno1, key1, type1, pname1, room1)).apply();
        subscribe(dno1);
        final View view = LayoutInflater.from(context).inflate(R.layout.dtype11_door, null, false);
        ImageView device_icon = view.findViewById(R.id.device_icon);
        Drawable drawable = HomePage.getDrawable(context, type1);
        device_icon.setImageDrawable(drawable);

        Switch aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setTag(dno1 + "switch1");

        TextView devicetypename = view.findViewById(R.id.devicetypename);
        devicetypename.setText(getDeviceNamebyType(type1, context));

        ImageView imageView = view.findViewById(R.id.online_status);
        imageView.setTag(dno1 + "online_status");

        LinearLayout parent = view.findViewById(R.id.parent);
        parent.setTag(dno1 + "parent");

        TextView power = view.findViewById(R.id.power);
        power.setTag(dno1 + "power");


        ImageView deviceInfo = view.findViewById(R.id.deviceInfo);
//        deviceInfo.setOnClickListener(new myclickListener(context,jsonObject));

        ImageView showMenu = view.findViewById(R.id.showMenu);
        showMenu.setTag(dno1 + "showMenu");

        showMenu.setTag(Constants.createPublishJson(dno1, key1, type1, pname1, room1));

        aSwitch.setOnCheckedChangeListener(null);
        aSwitch.setEnabled(Boolean.parseBoolean(isOnline1));
        aSwitch.setChecked(Boolean.parseBoolean(state));

        TextView devicename = view.findViewById(R.id.devicename); //Device name
        devicename.setTag(dno1 + "devicename");
        devicename.setText(pname1);


        String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
        if (Boolean.parseBoolean(isOnline1)) {
            if (!signal.equals("NA"))
                //setSignal(Integer.parseInt(signal),imageView,context);
                imageView.setContentDescription("online");
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
        }
        Log.d(TAG, "Dtype1: ");
        parent.setOnLongClickListener(v -> {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Remove Device")
                    .setMessage("Are you sure you want to Remove this Device ?")
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeDeviceFromRoom(context, aSwitch.getTag().toString().replace("switch1", ""));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return false;
        });
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: " + isChecked);
            pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
            /* */
        });
        //parent.setBackgroundColor(getMyDrawable(context));
        return view;
    }

    public static void renameDTypes(String dno, Context context, String key, String dees, String dtypes) {

        Dialog dialog = new Dialog(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.dia_change_device_name, null, false);
        dialog.setContentView(view1);
        EditText devicename = view1.findViewById(R.id.diaEditName);
        Button change = view1.findViewById(R.id.dia_change);

        change.setOnClickListener(v -> {
            //DtypeViews.renameDevice(context,devicename.getText().toString(),dno);
            try {
                new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                        "{  " +
                                "\"dno\": \"" + dno + "\",  " +
                                "\"key\": \"" + key + "\",  " +
                                "\"dtype\": \"" + dtypes + "\",  " +
                                "\"" + dees + "\":{    \"name\": \"" + devicename.getText().toString() + "\"}}",
                        1,
                        "d/" + dno + "/sub");
            } catch (MqttException | UnsupportedEncodingException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    public static void setTagAndBackground(Boolean state, LinearLayout switchLL, ImageView imageView) {
        switchLL.setTag(state);
        imageView.setSelected(state);
        if (state) {
            switchLL.setBackgroundResource(R.drawable.button_switch_on_border);
        } else {
            switchLL.setBackgroundResource(R.drawable.button_switch_off_border);
        }

    }

    public static void rgbdialog(LinearLayout currentColor, Context context, Switch aSwitch) {
        ColorDrawable viewColor = (ColorDrawable) currentColor.getBackground();
        int colorId = viewColor.getColor();
        final String[] dia_name = new String[1];
        AlertDialog.Builder colorPickAlert = new AlertDialog.Builder(context);

        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.rbg_control, null);

        colorPickAlert.setView(dialoglayout);

        ColorPicker picker = dialoglayout.findViewById(R.id.picker);
        TextView onoff_colorpicker = dialoglayout.findViewById(R.id.onoff_colorpicker);
        onoff_colorpicker.setTag(false); //Initialize value of Bulb i.e ON or OFF

        ImageView rbg_on_bulb = dialoglayout.findViewById(R.id.rbg_on_bulb);
        ImageView rbg_off_bulb = dialoglayout.findViewById(R.id.rbg_off_bulb);

        LinearLayout rbg_on_layout = dialoglayout.findViewById(R.id.rbg_on_layout);
        LinearLayout rbg_off_layout = dialoglayout.findViewById(R.id.rbg_off_layout);

        if (aSwitch.isChecked()) {
            rbg_off_layout.setVisibility(View.INVISIBLE);
            rbg_on_layout.setVisibility(View.VISIBLE);
        } else {
            rbg_on_layout.setVisibility(View.INVISIBLE);
            rbg_off_layout.setVisibility(View.VISIBLE);
        }

        rbg_on_bulb.setOnClickListener(v -> {
            onoff_colorpicker.setTag(false);
            onoff_colorpicker.setText("OFF");
            rbg_on_layout.setVisibility(View.INVISIBLE);
            rbg_off_layout.setVisibility(View.VISIBLE);
            publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), false);
        });
        rbg_off_bulb.setOnClickListener(v -> {
            onoff_colorpicker.setTag(true);
            onoff_colorpicker.setText("ON");
            rbg_off_layout.setVisibility(View.INVISIBLE);
            rbg_on_layout.setVisibility(View.VISIBLE);
            DtypeViews.publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), true);
        });

        picker.setOldCenterColor(colorId);
        picker.setShowOldCenterColor(false);
        SVBar svBar = (SVBar) dialoglayout.findViewById(R.id.svbar);

        SeekBar brightness = dialoglayout.findViewById(R.id.brightness);
        SeekBar warmwhite = dialoglayout.findViewById(R.id.warmwhite);
        brightness.setProgress(Constants.brightness * 255);
        warmwhite.setProgress(Constants.warm_white);

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Constants.brightness = (float)seekBar.getProgress()/255;
                DtypeViews.publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), (Boolean) onoff_colorpicker.getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        warmwhite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Constants.warm_white = seekBar.getProgress();
                DtypeViews.publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), (Boolean) onoff_colorpicker.getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        Button colorSelectedBtn = dialoglayout.findViewById(R.id.button1);

        final AlertDialog colorDialog = colorPickAlert.show();

        colorSelectedBtn.setOnClickListener(v -> {
            currentColor.setBackgroundColor(picker.getColor());
            currentColor.setTag(picker.getColor());
            colorDialog.dismiss();
        });


        picker.setOnColorChangedListener(color -> {
            currentColor.setBackgroundColor(picker.getColor());
            Constants.red = Color.red(color);
            Constants.green = Color.green(color);
            Constants.blue = Color.blue(color);

            Log.d(TAG, "onColorChanged: " + picker.getAlpha());
            DtypeViews.publishRBGcolor(context, aSwitch.getTag().toString().replace("switch1", ""), (Boolean) onoff_colorpicker.getTag());
        });
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static void pubit(Context context, String sname, boolean value) {
        Log.d(TAG, "pubit: " + Constants.savetoShared(context).getString(sname, "0"));
        String jsonObject = Constants.savetoShared(context).getString(sname, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    "{  " +
                            "\"dno\": \"" + Constants.jsonObjectreader(jsonObject, dno) + "\",  " +
                            "\"key\": \"" + Constants.jsonObjectreader(jsonObject, key) + "\",  " +
                            "\"dtype\": \"" + Constants.jsonObjectreader(jsonObject, dtype) + "\",  " +
                            "\"d1\":{    \"state\": " + value + "}}",
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void pubit_multiButton(Context context, String sname, boolean value, String dType) {
        Log.d(TAG, "pubit: " + Constants.savetoShared(context).getString(sname, "0"));
        String jsonObject = Constants.savetoShared(context).getString(sname, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    "{  " +
                            "\"dno\": \"" + Constants.jsonObjectreader(jsonObject, dno) + "\",  " +
                            "\"key\": \"" + Constants.jsonObjectreader(jsonObject, key) + "\",  " +
                            "\"dtype\": \"" + Constants.jsonObjectreader(jsonObject, dtype) + "\",  " +
                            "\"" + dType + "\":{    \"state\": " + value + "}}",
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void pubit_slider(Context context, String sname, boolean value, String dType, float br) {
        Log.d(TAG, br + " pubit_slider: " + Constants.savetoShared(context).getString(sname, "0"));
        String jsonObject = Constants.savetoShared(context).getString(sname, "0");
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObjectPut(jsonObject1, dno, Constants.jsonObjectreader(jsonObject, dno));
            jsonObjectPut(jsonObject1, key, Constants.jsonObjectreader(jsonObject, key));
            jsonObjectPut(jsonObject1, dtype, Constants.jsonObjectreader(jsonObject, dtype));

            JSONObject Dvalues = new JSONObject().put("state", value);
            Dvalues.put("state", value);
            Dvalues.put("br", (double) br / 100);
            jsonObject1.put(dType, Dvalues);

            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void getGetDevice(Context context) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ \"method\" : \"getDevice\" }", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void subscribe(String adno) {
        try {
            new PahoMqttClient().subscribe(Constants.GeneralpahoMqttClient, "d/" + adno + "/#", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void userSuscribe(Context context) {
        try {
            new PahoMqttClient().subscribe(Constants.GeneralpahoMqttClient, "u/" + Constants.savetoShared(context).getString(Constants.USER_ID, "0") + "/sub", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void addDevice(Context context, String device, String deviceKey) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ \"method\" : \"linkDevice\" , \"data\"  : {\"dno\":\"" + deviceKey + "\"}}", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
        subscribe(deviceKey);
    }

    public static void addRoom(Context context, String name) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ \"method\" : \"addRoom\" , \"data\"  : {\"name\":\"" + name + "\"}}", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void UpdateRoomName(Context context, String name, String roomID) {

        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ " +
                            "\"method\" : \"updRoom\" , " +
                            "\"data\"  : {" +
                            "\"room\":\"" + roomID + "\"," +
                            "\"name\":\"" + name + "\"" +
                            "}}",
                    1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addDevicetoRoom(Context context, String deviceKey, String roomID) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {

            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    "{ \"method\" : \"linkDevice\" , \"data\"  : {\"dno\":\"" + deviceKey + "\",\"room\":\"" + roomID + "\"}}",
                    1,
                    "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
        subscribe(deviceKey);
    }

    public static void chageDevicefromRoom(Context context, String deviceKey, String roomID, String hdno, String hdtype) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        try {

            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    "{\"dno\":\"" + hdno + "\"," +
                            "\"key\":\"" + deviceKey + "\"," +
                            "\"dtype\":\"" + hdtype + "\"," +
                            "\"room\":\"" + roomID + "\"}",
                    1,
                    "d/" + hdno + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
        getGetDevice(context);
    }

    public static void removeDeviceFromRoom(Context context, String string) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        String jsonObject = Constants.savetoShared(context).getString(string, "0");
        Log.d(TAG, user + "removeDeviceFromRoom: " + Constants.jsonObjectreader(jsonObject, dno));
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ " +
                    "\"method\" : \"ulinkDevice\" ," +
                    " \"data\"  : {\"dno\":\"" + Constants.jsonObjectreader(jsonObject, dno) + "\"}}", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void removeRoom(Context context, String RoomId) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        Log.d(TAG, user + "removeDeviceFromRoom: " + RoomId);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ \"method\" : \"deleteRoom\" , \"data\"  : {\"room\":\"" + RoomId + "\"}}", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getList(Context context) {
        Constants.savetoShared(context).edit().putString(Constants.PASSWORD, "12345").apply();
        Constants.GeneralpahoMqttClient = new PahoMqttClient().getHomeMqttClientAuthenticate(context, Constants.MQTT_BROKER_URL);
    }

    public static void publishRBGcolor(Context context, String jsonString, Boolean state) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        Log.d(TAG, "publishRBGcolor: " + jsonString);
        String jsonObject = Constants.savetoShared(context).getString(jsonString, "0");
        JSONObject jsonObjectD1 = new JSONObject();
        JSONObject jsonObjectMain = new JSONObject();
        try {

            jsonObjectD1.put("state", state);
            jsonObjectD1.put("r", Constants.red);
            jsonObjectD1.put("g", Constants.green);
            jsonObjectD1.put("b", Constants.blue);
            jsonObjectD1.put("w", Constants.white);
            jsonObjectD1.put("ww", Constants.warm_white);
            jsonObjectD1.put("br", (double) Constants.brightness / 100);


            jsonObjectMain.put("dno", Constants.jsonObjectreader(jsonObject, dno));
            jsonObjectMain.put("key", Constants.jsonObjectreader(jsonObject, key));
            jsonObjectMain.put("dtype", Constants.jsonObjectreader(jsonObject, dtype));
            jsonObjectMain.put("d1", jsonObjectD1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "publishRBGcolor: " + jsonObjectMain.toString());
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObjectMain.toString(),
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void renameDevice(Context context, String newname, String string) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        String jsonObject = Constants.savetoShared(context).getString(string, "0");
        try {
            JSONObject jsonObject1 = new JSONObject(jsonObject);
            String Ddno = jsonObject1.getString("dno");
            jsonObject1.remove("room");
            jsonObject1.put("name", newname);
            Log.e(TAG, "renameDevice: " + jsonObject1.toString());
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + Ddno + "/sub");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("TAGGG", "Exception at Rename Devices " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("TAGGG", "ExceptionUnSupport at Rename Devices " + e.getMessage(), e);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("TAGGG", "MqttException at Rename Devices " + e.getMessage(), e);
        }
        //getGetDevice(context);
    }

    public static void renameDeviceDtypes(Context context, String newname, String dno, String dType) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        String jsonObject = Constants.savetoShared(context).getString(dno, "0");
        try {
            JSONObject jsonObject1 = new JSONObject(jsonObject);
            String Ddno = jsonObject1.getString("dno");

            jsonObject1.remove("room");
            jsonObject1.put("name", newname);

            Log.d(TAG, "renameDevice: " + jsonObject1.toString());
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + Ddno + "/sub");
        } catch (JSONException | MqttException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //getGetDevice(context);
    }

    public static void RemotClick(Context context, String sname, String value, String irs) {
        Log.d(TAG, "pubit: " + Constants.savetoShared(context).getString(sname, "0"));
        String jsonObject = Constants.savetoShared(context).getString(sname, "0");

        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "dno", Constants.jsonObjectreader(jsonObject, dno));
        jsonObjectPut(jsonObject1, "key", Constants.jsonObjectreader(jsonObject, key));
        jsonObjectPutInt(jsonObject1, "format", 30);
        jsonObjectPut(jsonObject1, "dtype", Constants.jsonObjectreader(jsonObject, dtype));
        jsonObjectPut(jsonObject1, "data", value);
        jsonObjectPut(jsonObject1, "channel", irs);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void PowerClick(Context context, String sname, String value, String dnoValue, String irs) {
        Log.d(TAG, "pubit: " + Constants.savetoShared(context).getString(dnoValue, "0"));
        String jsonObject = Constants.savetoShared(context).getString(dnoValue, "0");

        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "dno", Constants.jsonObjectreader(jsonObject, dno));
        jsonObjectPut(jsonObject1, "key", Constants.jsonObjectreader(jsonObject, key));
        jsonObjectPutInt(jsonObject1, "format", 31);
        jsonObjectPutInt(jsonObject1, "dtype", 10);
        jsonObjectPut(jsonObject1, "data", value);
        jsonObjectPut(jsonObject1, "channel", irs);

        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + dnoValue + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void RemotClickConfigure(Activity remoteActivity, String dnoValue, String irl) {
        String jsonObject = Constants.savetoShared(remoteActivity).getString(dnoValue, "0");
        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "dno", Constants.jsonObjectreader(jsonObject, dnoValue));
        jsonObjectPut(jsonObject1, "key", Constants.jsonObjectreader(jsonObject, key));
        jsonObjectPut(jsonObject1, "channel", irl);
        Log.e("TAG", "RemotClickConfigure dno " + dnoValue + " key " + key + " channel " + irl + " topic " + "d/" + Constants.jsonObjectreader(jsonObject, dnoValue) + "/sub");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, jsonObject1.toString(),
                    1,
                    "d/" + Constants.jsonObjectreader(jsonObject, dnoValue) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void postSchedules(Activity remoteActivity, String infos) {

        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + Constants.jsonObjectreader(infos, dno) + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private static int getMyDrawable(Context context) {
        // TypedArray colors = context.getResources().obtainTypedArray(R.array.loading_colors);

        //  int index = (int) (Math.random() * colors .length());
        //  int color = colors.getColor(index, Color.BLACK);

      /*   int color = context.getResources().getColor(R.color.offwhite);

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        Log.d(TAG, "getMyDrawable: "+alpha);
        if (alpha > 255)
            alpha = 50;
        return Color.argb(alpha, red, green, blue);

        //Drawable mDrawable = context.getResources().getDrawable(R.drawable.ball);
        //mDrawable.setColorFilter(new                PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        //colors.recycle();
       // return color;
*/
        return cardColorup;
    }

    public static void setSignal(int strength, ImageView imageView, Context context) {
        int signalDawables = R.drawable.signal_zero;
        switch (strength) {

            case 0:
                signalDawables = R.drawable.signal_zero;
                break;
            case 1:
                signalDawables = R.drawable.signal_one;
                break;
            case 2:
                signalDawables = R.drawable.signal_two;
                break;
            case 3:
                signalDawables = R.drawable.signal_three;
                break;
            case 4:
                signalDawables = R.drawable.signal_four;
                break;
        }
        imageView.setImageDrawable(context.getResources().getDrawable(signalDawables));
    }
}
