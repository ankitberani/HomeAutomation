package com.wekex.apps.homeautomation.utils;

import android.app.Activity;
import android.content.Context;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Switch;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.RoomActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.DtypeViews.SIGNAL;
import static com.wekex.apps.homeautomation.utils.DtypeViews.isOnline;
import static com.wekex.apps.homeautomation.utils.DtypeViews.pubit_multiButton;
import static com.wekex.apps.homeautomation.utils.DtypeViews.setSignal;
import static com.wekex.apps.homeautomation.utils.DtypeViews.setTagAndBackground;

public class RoomControl {
    private static String TAG = "roomcontrol";

    public static void Dtype1(LinearLayout deviceHolder, String type1, String Rdata, Activity context) {

        String dno1 = jsonObjectreader(Rdata, "dno");
        String state = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "state");

        if (Constants.stringToJsonObject(Rdata).has("name")) {
            TextView devicename = deviceHolder.findViewWithTag(dno1 + "devicename"); //Device name
            devicename.setText(Constants.jsonObjectreader(Rdata, DtypeViews.pname));
        }

        if (Constants.stringToJsonObject(Rdata).has("duser")) {
            String jsonString = Constants.savetoShared(context).getString(Constants.ROOMS, "null");
            ((RoomActivity) context).addViews(jsonString);
        }


        if (Constants.stringToJsonObject(Rdata).has(isOnline)) {
            online = jsonObjectreader(Rdata, isOnline);
            isonline(deviceHolder, online, context, dno1);
        }
        JSONObject jsonObject = stringToJsonObject(Rdata);
        if (jsonObject.has(SIGNAL)) {
            String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
            ImageView imageView = deviceHolder.findViewWithTag(dno1 + "online_status");
            setSignal(Integer.parseInt(signal), imageView, context);
        }


        LinearLayout parent = deviceHolder.findViewWithTag(dno1 + "parent");


        Switch view = deviceHolder.findViewWithTag(dno1 + "switch1");
        view.setOnCheckedChangeListener(null);
        view.setChecked(Boolean.parseBoolean(state));
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                DtypeViews.pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
                /* */
            }
        });
        Log.d(TAG, "updateUI: " + dno1);
    }

    private static String online;

    public static void Dtype2(LinearLayout deviceHolder, String type1, String Rdata, Activity context) {

        try {
            String dno1 = jsonObjectreader(Rdata, "dno");
            String state = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "state");
            Log.d(TAG, "Dtype2: hghg " + Constants.jsonobjectByName(Rdata, "d1"));
            if (Constants.jsonobjectByName(Rdata, "d1").has("r")) {
                Log.d(TAG, "Dtype2: hghg");
                String red = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "r");
                String green = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "g");
                String blue = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "b");
                int color = android.graphics.Color.argb(255, Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
                LinearLayout parent = deviceHolder.findViewWithTag(dno1 + "parent");
//                ImageView device_icon = deviceHolder.findViewWithTag(dno1 + "device_icon");
//                device_icon.setColorFilter(color);
                //  parent.setBackgroundColor(color);
            }


            if (Constants.stringToJsonObject(Rdata).has(isOnline)) {
                online = jsonObjectreader(Rdata, isOnline);
                isonline(deviceHolder, online, context, dno1);
            }
            JSONObject jsonObject = stringToJsonObject(Rdata);
            if (jsonObject.has(SIGNAL)) {
                String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
                ImageView imageView = deviceHolder.findViewWithTag(dno1 + "online_status");
                setSignal(Integer.parseInt(signal), imageView, context);
            }
            TextView currentColor = deviceHolder.findViewById(R.id.currentColor);
            Log.e(TAG, "Dtype2: " + dno1 + "switch1");

            Switch view = deviceHolder.findViewWithTag(dno1 + "switch1");
            view.setOnCheckedChangeListener(null);
            view.setChecked(Boolean.parseBoolean(state));
            if (Boolean.parseBoolean(online)) {
                view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.d(TAG, "onCheckedChanged: " + isChecked);
                        DtypeViews.pubit(context, buttonView.getTag().toString().replace("switch1", ""), isChecked);
                        /* */
                    }
                });
            } else {
                view.setEnabled(false);
                view.setClickable(false);
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at roomcontrol " + e.getMessage());
        }
    }

    public static void Dtype5(LinearLayout deviceHolder, String type1, String Rdata, Activity context) {
        String dno1 = jsonObjectreader(Rdata, "dno");
        String state1 = savetoShared(context).getString(dno1 + "d1", "false");
        String state2 = savetoShared(context).getString(dno1 + "d2", "false");

        JSONObject jsonObject = stringToJsonObject(Rdata);

        if (Constants.stringToJsonObject(Rdata).has(isOnline)) {
            online = jsonObjectreader(Rdata, isOnline);
            isonline(deviceHolder, online, context, dno1);
        }

        if (jsonObject.has(SIGNAL)) {
            String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
            ImageView imageView = deviceHolder.findViewWithTag(dno1 + "online_status");
            setSignal(Integer.parseInt(signal), imageView, context);
        }

        LinearLayout parent = deviceHolder.findViewWithTag(dno1 + "parent");

        CardView switch2Card = deviceHolder.findViewWithTag(dno1 + "switch2Card");
        CardView switch1Card = deviceHolder.findViewWithTag(dno1 + "switch1Card");

        Switch aSwitch = deviceHolder.findViewWithTag(dno1 + "switch1");

        LabeledSwitch labeledSwitch1 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch1");
        LabeledSwitch labeledSwitch2 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch2");

        ImageView subswitch1IV = deviceHolder.findViewWithTag(dno1 + "subswitch1IV");
        ImageView subswitch2IV = deviceHolder.findViewWithTag(dno1 + "subswitch2IV");

        TextView subswitch1TV = deviceHolder.findViewWithTag(dno1 + "subswitch1TV");

        TextView subswitch2TV = deviceHolder.findViewWithTag(dno1 + "subswitch2TV");

        labeledSwitch1.setOnToggledListener(null);
        labeledSwitch2.setOnToggledListener(null);
        if (jsonObject.has("d1")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d1"));
            if (jsonObjectname.has("name"))
                subswitch1TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d1"), "name"));

            if (jsonObjectname.has("state")) {
                state1 = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "state");
                labeledSwitch1.setOn(Boolean.parseBoolean(state1));
                setTagAndBackground(Boolean.parseBoolean(state1), (LinearLayout) switch1Card.getChildAt(0), subswitch1IV);
                savetoShared(context).edit().putString(dno1 + "d1", state1).apply();
            }
        }

        if (jsonObject.has("d2")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d2"));
            if (jsonObjectname.has("name"))
                subswitch2TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d2"), "name"));
            //  Log.d(TAG, jsonObjectname.toString()+" Dtype5: "+jsonObjectname.has("name")+jsonObjectreader(jsonObjectreader(Rdata, "d2"), "name"));
            if (jsonObjectname.has("state")) {
                state2 = jsonObjectreader(jsonObjectreader(Rdata, "d2"), "state");
                labeledSwitch2.setOn(Boolean.parseBoolean(state2));
                setTagAndBackground(Boolean.parseBoolean(state2), (LinearLayout) switch2Card.getChildAt(0), subswitch2IV);
                savetoShared(context).edit().putString(dno1 + "d2", state2).apply();
            }
        }


        labeledSwitch1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isOn, "d1");
            }
        });

        labeledSwitch2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                Log.d(TAG, labeledSwitch2.getTag().toString() + "pub ");
                pubit_multiButton(context, labeledSwitch2.getTag().toString().replace("labeledSwitch2", ""), isOn, "d2");
            }
        });

        aSwitch.setOnCheckedChangeListener(null);
        if (Boolean.parseBoolean(state1) || Boolean.parseBoolean(state2)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                //DtypeViews.pubit(context,buttonView.getTag().toString().replace("switch1",""),isChecked);

                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isChecked, "d1");
                pubit_multiButton(context, labeledSwitch2.getTag().toString().replace("labeledSwitch2", ""), isChecked, "d2");

                /* */
            }
        });
        Log.d(TAG, "updateUI: " + dno1);
    }

    public static void Dtype6(LinearLayout deviceHolder, String type1, String Rdata, Activity context) {
        String dno1 = jsonObjectreader(Rdata, "dno");
        String state1 = savetoShared(context).getString(dno1 + "d1", "false");
        String state2 = savetoShared(context).getString(dno1 + "d2", "false");
        String state3 = savetoShared(context).getString(dno1 + "d3", "false");
        String state4 = savetoShared(context).getString(dno1 + "d4", "false");

        JSONObject jsonObject = stringToJsonObject(Rdata);

        if (Constants.stringToJsonObject(Rdata).has(isOnline)) {
            online = jsonObjectreader(Rdata, isOnline);
            isonline(deviceHolder, online, context, dno1);
        }

        if (jsonObject.has(SIGNAL)) {
            String signal = Constants.jsonObjectreader(jsonObject.toString(), SIGNAL);
            ImageView imageView = deviceHolder.findViewWithTag(dno1 + "online_status");
            setSignal(Integer.parseInt(signal), imageView, context);
        }

        LinearLayout parent = deviceHolder.findViewWithTag(dno1 + "parent");

        CardView switch1Card = deviceHolder.findViewById(R.id.switch1Card);
        CardView switch2Card = deviceHolder.findViewById(R.id.switch2Card);


//        CardView switch3Card = deviceHolder.findViewById(R.id.switch3Card);
//        CardView switch4Card = deviceHolder.findViewById(R.id.switch4Card);

        Switch aSwitch = deviceHolder.findViewWithTag(dno1 + "switch1");

        LabeledSwitch labeledSwitch1 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch1");
        LabeledSwitch labeledSwitch2 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch2");

        LabeledSwitch labeledSwitch3 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch2");
        LabeledSwitch labeledSwitch4 = deviceHolder.findViewWithTag(dno1 + "labeledSwitch4");

        ImageView subswitch1IV = deviceHolder.findViewWithTag(dno1 + "subswitch1IV");
        ImageView subswitch2IV = deviceHolder.findViewWithTag(dno1 + "subswitch2IV");

        ImageView subswitch3IV = deviceHolder.findViewWithTag(dno1 + "subswitch3IV");
        ImageView subswitch4IV = deviceHolder.findViewWithTag(dno1 + "subswitch4IV");

        TextView subswitch1TV = deviceHolder.findViewWithTag(dno1 + "subswitch1TV");
        TextView subswitch2TV = deviceHolder.findViewWithTag(dno1 + "subswitch2TV");

        TextView subswitch3TV = deviceHolder.findViewWithTag(dno1 + "subswitch3TV");
        TextView subswitch4TV = deviceHolder.findViewWithTag(dno1 + "subswitch4TV");

        labeledSwitch1.setOnToggledListener(null);
        labeledSwitch2.setOnToggledListener(null);
        labeledSwitch3.setOnToggledListener(null);
        labeledSwitch4.setOnToggledListener(null);

        if (jsonObject.has("d1")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d1"));
            if (jsonObjectname.has("name"))
                subswitch1TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d1"), "name"));

            if (jsonObjectname.has("state")) {
                state1 = jsonObjectreader(jsonObjectreader(Rdata, "d1"), "state");
                labeledSwitch1.setOn(Boolean.parseBoolean(state1));
                setTagAndBackground(Boolean.parseBoolean(state1), (LinearLayout) switch1Card.getChildAt(0), subswitch1IV);
                savetoShared(context).edit().putString(dno1 + "d1", state1).apply();
            }
        }

        if (jsonObject.has("d2")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d2"));
            if (jsonObjectname.has("name"))
                subswitch2TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d2"), "name"));
            //  Log.d(TAG, jsonObjectname.toString()+" Dtype5: "+jsonObjectname.has("name")+jsonObjectreader(jsonObjectreader(Rdata, "d2"), "name"));
            if (jsonObjectname.has("state")) {
                state2 = jsonObjectreader(jsonObjectreader(Rdata, "d2"), "state");
                labeledSwitch2.setOn(Boolean.parseBoolean(state2));
                setTagAndBackground(Boolean.parseBoolean(state2), (LinearLayout) switch2Card.getChildAt(0), subswitch2IV);
                savetoShared(context).edit().putString(dno1 + "d2", state2).apply();
            }
        }

        if (jsonObject.has("d3")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d3"));
            if (jsonObjectname.has("name"))
                subswitch3TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d3"), "name"));
            //  Log.d(TAG, jsonObjectname.toString()+" Dtype5: "+jsonObjectname.has("name")+jsonObjectreader(jsonObjectreader(Rdata, "d3"), "name"));
            if (jsonObjectname.has("state")) {
                state3 = jsonObjectreader(jsonObjectreader(Rdata, "d3"), "state");
                labeledSwitch3.setOn(Boolean.parseBoolean(state3));
//                setTagAndBackground(Boolean.parseBoolean(state3), (LinearLayout) switch3Card.getChildAt(0), subswitch3IV);
                savetoShared(context).edit().putString(dno1 + "d3", state3).apply();
            }
        }

        if (jsonObject.has("d4")) {
            JSONObject jsonObjectname = stringToJsonObject(jsonObjectreader(Rdata, "d4"));
            if (jsonObjectname.has("name"))
                subswitch4TV.setText(jsonObjectreader(jsonObjectreader(Rdata, "d4"), "name"));
            //  Log.d(TAG, jsonObjectname.toString()+" Dtype5: "+jsonObjectname.has("name")+jsonObjectreader(jsonObjectreader(Rdata, "d4"), "name"));
            if (jsonObjectname.has("state")) {
                state4 = jsonObjectreader(jsonObjectreader(Rdata, "d4"), "state");
                labeledSwitch4.setOn(Boolean.parseBoolean(state4));
//                setTagAndBackground(Boolean.parseBoolean(state4), (LinearLayout) switch4Card.getChildAt(0), subswitch4IV);
                savetoShared(context).edit().putString(dno1 + "d4", state4).apply();
            }
        }

        labeledSwitch1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                pubit_multiButton(context, labeledSwitch1.getTag().toString().replace("labeledSwitch1", ""), isOn, "d1");
            }
        });

        labeledSwitch2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                Log.d(TAG, labeledSwitch2.getTag().toString() + "pub ");
                pubit_multiButton(context, labeledSwitch2.getTag().toString().replace("labeledSwitch2", ""), isOn, "d2");
            }
        });

        labeledSwitch3.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                Log.d(TAG, labeledSwitch3.getTag().toString() + "pub ");
                pubit_multiButton(context, labeledSwitch3.getTag().toString().replace("labeledSwitch3", ""), isOn, "d3");
            }
        });

        labeledSwitch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                Log.d(TAG, labeledSwitch4.getTag().toString() + "pub ");
                pubit_multiButton(context, labeledSwitch4.getTag().toString().replace("labeledSwitch4", ""), isOn, "d4");
            }
        });

        aSwitch.setOnCheckedChangeListener(null);

        if (Boolean.parseBoolean(state1) || Boolean.parseBoolean(state4) || Boolean.parseBoolean(state3) || Boolean.parseBoolean(state4)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                //DtypeViews.pubit(context,buttonView.getTag().toString().replace("switch1",""),isChecked);

                pubit_multiButton(context, labeledSwitch3.getTag().toString().replace("labeledSwitch3", ""), isChecked, "d3");
                pubit_multiButton(context, labeledSwitch4.getTag().toString().replace("labeledSwitch4", ""), isChecked, "d4");
                pubit_multiButton(context, labeledSwitch3.getTag().toString().replace("labeledSwitch3", ""), isChecked, "d3");
                pubit_multiButton(context, labeledSwitch4.getTag().toString().replace("labeledSwitch4", ""), isChecked, "d4");

                /* */
            }
        });
        Log.d(TAG, "updateUI: " + dno1);
    }

    public static void Dtype7(LinearLayout deviceHolder, String type1, String rdata, RoomActivity roomActivity) {
    }

    public static void Dtype10(LinearLayout deviceHolder, String type1, String rdata, RoomActivity roomActivity) {
    }

    public static void Dtype11(LinearLayout deviceHolder, String type1, String rdata, RoomActivity roomActivity) {
    }

    public static String udpdateJSON(Activity context, String rdata) {
        String forReturn = "";
        String dno = jsonObjectreader(rdata, "dno");
        Log.d(TAG, "udpdateJSON:3 " + rdata);
        String jsonString = Constants.savetoShared(context).getString(Constants.ROOMS, "null");
        Log.d(TAG, "udpdateJSON1: getDevice" + jsonString);

        try {
            JSONObject device = new JSONObject(rdata);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayCurrent = jsonArray.getJSONObject(i);
                if (jsonArrayCurrent.toString().contains(dno)) {
                    Log.d(TAG, "udpdateJSON: " + jsonArrayCurrent);
                    if (device.has("name"))
                        jsonArrayCurrent.put("name", jsonObjectreader(rdata, "name"));

                    if (device.has("room"))
                        jsonArrayCurrent.put("room", jsonObjectreader(rdata, "room"));

                    if (device.has("duser"))
                        jsonArrayCurrent.put("duser", jsonObjectreader(rdata, "duser"));

                    if (device.has("isOnline"))
                        jsonArrayCurrent.put("isOnline", jsonObjectreader(rdata, "isOnline"));

                    if (device.has("ir")) {
                        JSONArray ir = jsonArrayCurrent.getJSONArray("ir");
                        if (ir == null)
                            ir = new JSONArray();

                        JSONObject DeviceData = device.getJSONObject("ir");
                        Log.d(TAG, "MyudpdateJSON: " + DeviceData.toString());
                        if (DeviceData.has("index")) {
                            Log.d(TAG, DeviceData.getString("oprtype") + " udpdateJSON: Index " + DeviceData.getString("index"));
                            JSONArray newArray = new JSONArray();
                            int index = Integer.parseInt(DeviceData.getString("index"));

                            if (DeviceData.getString("oprtype").equals("delete")) {
                                for (int ind = 0; ind < ir.length(); ind++) {
                                    if (ind != index)
                                        newArray.put(ir.get(ind));
                                }
                                Log.d(TAG, "Deleted udpdateJSON: Index" + DeviceData.getString("index"));
                            } else if (DeviceData.getString("oprtype").equals("update")) {
                                for (int ind = 0; ind < ir.length(); ind++) {
                                    if (ind != index)
                                        newArray.put(ir.get(ind));
                                    else {
                                        Log.d(TAG, "Updated udpdateJSON: Index" + DeviceData.getString("index"));
                                        DeviceData.remove("index");
                                        DeviceData.remove("oprtype");
                                        newArray.put(DeviceData.toString());
                                    }
                                }

                            }

                            ir = newArray;
                        } else {
                            ir.put(ir.length(), DeviceData.toString());
                        }
                        jsonArrayCurrent.put("ir", ir);
                        forReturn = jsonArrayCurrent.toString();
                    }

                    if (device.has("rf")) {


                        JSONArray rf = jsonArrayCurrent.getJSONArray("rf");
                        if (rf == null)
                            rf = new JSONArray();

                        JSONObject DeviceData = device.getJSONObject("rf");
                        Log.d(TAG, "MyudpdateJSON: " + DeviceData.toString());
                        if (DeviceData.has("index")) {
                            JSONArray newArray = new JSONArray();
                            int index = Integer.parseInt(DeviceData.getString("index"));

                            if (DeviceData.getString("oprtype").equals("delete")) {
                                for (int ind = 0; ind < rf.length(); ind++) {
                                    if (ind != index)
                                        newArray.put(rf.get(ind));
                                }
                            } else if (DeviceData.getString("oprtype").equals("update")) {
                                for (int ind = 0; ind < rf.length(); ind++) {
                                    if (ind != index)
                                        newArray.put(rf.get(ind));
                                    else {
                                        DeviceData.remove("index");
                                        DeviceData.remove("oprtype");
                                        newArray.put(DeviceData.toString());
                                    }
                                }
                            }

                            rf = newArray;
                        } else {
                            rf.put(rf.length(), device.getJSONObject("rf").toString());
                        }
                        jsonArrayCurrent.put("rf", rf);
                        forReturn = jsonArrayCurrent.toString();


                    }

                    if (device.has("schedule")) {
                        if (jsonArrayCurrent.has("schedule")) {
                            JSONArray schedule = jsonArrayCurrent.getJSONArray("schedule");
                            if (schedule == null)
                                schedule = new JSONArray();
                            schedule.put(schedule.length(), device.getJSONObject("schedule"));
                            jsonArrayCurrent.put("schedule", schedule);
                            forReturn = jsonArrayCurrent.toString();
                        } else {
                            JSONArray schedule = new JSONArray();
                            schedule.put(schedule.length(), device.getJSONObject("schedule"));
                            jsonArrayCurrent.put("schedule", schedule);
                            forReturn = jsonArrayCurrent.toString();
                        }
                    }

                    if (device.has("d1")) {
                        JSONObject mainJSONObject = jsonArrayCurrent.getJSONObject("d1");
                        JSONObject deviceJSONonject = device.getJSONObject("d1");
                        Log.d(TAG, mainJSONObject + " udpdateJSON:4 " + deviceJSONonject.toString());
                        if (deviceJSONonject.has("state"))
                            mainJSONObject.put("state", deviceJSONonject.getBoolean("state"));
                        if (deviceJSONonject.has("r")) {
                            Log.d(TAG, mainJSONObject.getInt("r") + " udpdateJSON: Updates " + deviceJSONonject.getInt("r"));
                            mainJSONObject.put("r", deviceJSONonject.getInt("r"));
                        }
                        if (deviceJSONonject.has("g")) {
                            Log.d(TAG, mainJSONObject.getInt("g") + " udpdateJSON: Updates " + deviceJSONonject.getInt("g"));
                            mainJSONObject.put("g", deviceJSONonject.getInt("g"));
                            Log.d(TAG, mainJSONObject.getInt("g") + " udpdateJSON: Updates " + deviceJSONonject.getInt("g"));
                        }
                        if (deviceJSONonject.has("b")) {
                            mainJSONObject.put("b", deviceJSONonject.getInt("b"));
                        }
                        if (deviceJSONonject.has("w"))
                            mainJSONObject.put("w", deviceJSONonject.getInt("w"));
                        if (deviceJSONonject.has("ww"))
                            mainJSONObject.put("ww", deviceJSONonject.getInt("ww"));
                        if (deviceJSONonject.has("br"))
                            mainJSONObject.put("br", deviceJSONonject.getInt("br"));
                        if (deviceJSONonject.has("name"))
                            mainJSONObject.put("name", deviceJSONonject.getString("name"));
                        //  Log.d(TAG, "udpdateJSON:5 "+mainJSONObject.toString());
                        jsonArrayCurrent.put("d1", mainJSONObject);
                    }
                    if (device.has("d2")) {
                        JSONObject mainJSONObject = jsonArrayCurrent.getJSONObject("d2");
                        JSONObject deviceJSONonject = device.getJSONObject("d2");
                        if (deviceJSONonject.has("state"))
                            mainJSONObject.put("state", deviceJSONonject.getBoolean("state"));
                        if (deviceJSONonject.has("r"))
                            mainJSONObject.put("r", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("g"))
                            mainJSONObject.put("g", deviceJSONonject.getInt("g"));
                        if (deviceJSONonject.has("b"))
                            mainJSONObject.put("b", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("w"))
                            mainJSONObject.put("w", deviceJSONonject.getInt("w"));
                        if (deviceJSONonject.has("ww"))
                            mainJSONObject.put("ww", deviceJSONonject.getInt("ww"));
                        if (deviceJSONonject.has("br"))
                            mainJSONObject.put("br", deviceJSONonject.getInt("br"));
                        if (deviceJSONonject.has("name"))
                            mainJSONObject.put("name", deviceJSONonject.getString("name"));
                        jsonArrayCurrent.put("d2", mainJSONObject);
                    }

                    if (device.has("d3")) {
                        JSONObject mainJSONObject = jsonArrayCurrent.getJSONObject("d3");
                        JSONObject deviceJSONonject = device.getJSONObject("d3");
                        if (deviceJSONonject.has("state"))
                            mainJSONObject.put("state", deviceJSONonject.getBoolean("state"));
                        if (deviceJSONonject.has("r"))
                            mainJSONObject.put("r", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("g"))
                            mainJSONObject.put("g", deviceJSONonject.getInt("g"));
                        if (deviceJSONonject.has("b"))
                            mainJSONObject.put("b", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("w"))
                            mainJSONObject.put("w", deviceJSONonject.getInt("w"));
                        if (deviceJSONonject.has("ww"))
                            mainJSONObject.put("ww", deviceJSONonject.getInt("ww"));
                        if (deviceJSONonject.has("br"))
                            mainJSONObject.put("br", deviceJSONonject.getInt("br"));
                        if (deviceJSONonject.has("name"))
                            mainJSONObject.put("name", deviceJSONonject.getString("name"));
                        jsonArrayCurrent.put("d3", mainJSONObject);
                    }

                    if (device.has("d4")) {
                        JSONObject mainJSONObject = jsonArrayCurrent.getJSONObject("d4");
                        JSONObject deviceJSONonject = device.getJSONObject("d4");
                        if (deviceJSONonject.has("state"))
                            mainJSONObject.put("state", deviceJSONonject.getBoolean("state"));
                        if (deviceJSONonject.has("r"))
                            mainJSONObject.put("r", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("g"))
                            mainJSONObject.put("g", deviceJSONonject.getInt("g"));
                        if (deviceJSONonject.has("b"))
                            mainJSONObject.put("b", deviceJSONonject.getInt("r"));
                        if (deviceJSONonject.has("w"))
                            mainJSONObject.put("w", deviceJSONonject.getInt("w"));
                        if (deviceJSONonject.has("ww"))
                            mainJSONObject.put("ww", deviceJSONonject.getInt("ww"));
                        if (deviceJSONonject.has("br"))
                            mainJSONObject.put("br", deviceJSONonject.getInt("br"));
                        if (deviceJSONonject.has("name"))
                            mainJSONObject.put("name", deviceJSONonject.getString("name"));
                        jsonArrayCurrent.put("d4", mainJSONObject);
                    }
                    jsonArray.put(i, jsonArrayCurrent);
                }
                //   {  "dno": "84F3EB8280C3",  "key": "1323036",  "dtype": "5",  "d2":{    "state": true}}


            }

            jsonObject.put("data", jsonArray);
            Log.d(TAG, "udpdateJSON2: getDevice" + jsonObject.toString());
            Constants.savetoShared(context).edit().putString(Constants.ROOMS, jsonObject.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return forReturn;
    }

    public static void isonline(LinearLayout view, String isOnline1, Context context, String dno1) {
        ImageView imageView = view.findViewWithTag(dno1 + "online_status");
        ImageView imageView_device = view.findViewWithTag(dno1 + "device_icon");
        ImageView showMenu = view.findViewById(R.id.showMenu);
        if (!Boolean.parseBoolean(isOnline1)) {
            TextView power = view.findViewWithTag(dno1 + "power");
            if (power != null)
                power.setVisibility(view.GONE);
        }
        LinearLayout parent = view.findViewWithTag(dno1 + "parent");
        parent.setEnabled(Boolean.parseBoolean(isOnline1));
        showMenu.setEnabled(Boolean.parseBoolean(isOnline1));
        if (Boolean.parseBoolean(isOnline1)) {
            parent.setBackgroundColor(context.getResources().getColor(R.color.white));
            imageView.setContentDescription("online");
            imageView.setVisibility(View.VISIBLE);
        } else {
            parent.setBackgroundColor(context.getResources().getColor(R.color.grey));
            imageView_device.setImageDrawable(context.getResources().getDrawable(R.drawable.device_offline_icon));
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.signal_zero));
            imageView.setContentDescription("offline");
            imageView.setVisibility(View.GONE);
        }
    }


}
