package com.wekex.apps.homeautomation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.wekex.apps.homeautomation.RoomActivity.deviceTypeHolder;

public class Constants {


    public static boolean FIRSTROOM;
    private static final String TAG = "ConstantsCLass";
    public static String room_Id;
    public static String backcolor;
    public static final String ROOMS = "rooms";
    public static final String EMPTY = "empty";
    public static final String PASSWORD = "pass";
    public static final String USER_ID = "UID";
    public static final String FNAME = "fname";
    public static final String LNAME = "lname";
    public static final String PHONE = "mobile";
    public static final String DOB = "dob";
    public static final String EMAIL = "email";
    public static final String BANNER = "banner";
    public static final String PROFILEPIC = "profilepic";
    public static final int IMAGE_GALLERY_REQUEST = 9;

    public static final String TIMEZONE = "timeZone";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String ENERGYCOST = "energyCost";

    public static final String DATAINFO = "datainfo";
    public static int showDeviceRype = 0;

    public static Drawable dia_backdrawable;
    public static String gradename;
    public static String PowerDnoR = "";
    public static RelativeLayout dia_cardRoom;

    //public static String cardColorup = "#E8E8E8";
    public static int cardColorup;

    public static final String DEVICETYPES = "devicetypes";
    public static SharedPreferences sharedPreferences;
    public static final String GET_DEVICE = "getdevices";
    public static final String SHARED_PREFERENCES_NAME = "HomeAutomation";
    public static final String MQTT_BROKER_URL = "tcp://209.58.164.151:1883";
    public static final String PUBLISH_TOPIC = "u/c14ecd7a-7869-4c5f-bdae-c93f107b5edf/pub";
    public static final String SUSCRIBE_TOPIC = "u/c14ecd7a-7869-4c5f-bdae-c93f107b5edf/sub";
    public static final String CLIENT_ID = "c14ecd7a-7869-4c5f-bdae-c93f107b5edf";
    //    public static final String BASEURL = "http://209.58.164.151:88/api/";
    public static final String BASEURL = APIClient.BASE_URL + "/api/";
    public static final String clientId = UUID.randomUUID().toString();
    public static MqttAndroidClient GeneralpahoMqttClient;
    public static String mobNo;

    //color picker
    public static int blue;
    public static int green;
    public static int red;
    public static int brightness;
    public static float BRIGHTNESS;
    public static int warm_white;
    public static int white;
    public static final int colorpickerActivity = 2;
    public static double latitude;
    public static double longitude;

    //
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static final String SMARTCONFIGPASSWORD = "smartpassword";

    public static SharedPreferences savetoShared(Context baseContext) {
        return baseContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static String createDeviceJson(String name, String dno1, String key1, String type1) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(DtypeViews.dno, dno1);
            obj.put(DtypeViews.key, key1);
            obj.put(DtypeViews.dtype, type1);
            obj.put(DtypeViews.d1pname, name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("kuch", "createJson: " + obj.toString());
        return obj.toString();
    }

    public static String createPublishJson(String dno1, String key1, String type1, String name, String roomID) {

        JSONObject obj = new JSONObject();
        try {
            obj.put(DtypeViews.dno, dno1);
            obj.put(DtypeViews.key, key1);
            obj.put(DtypeViews.dtype, type1);
            obj.put(DtypeViews.d1pname, name);
            obj.put(DtypeViews.room, roomID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("kuch", "createJson: " + obj.toString());
        return obj.toString();
    }

    public static MqttAndroidClient getClientConnection(Context context) {
        String user = savetoShared(context).getString(Constants.USER_ID, "0");
        String pass = savetoShared(context).getString(Constants.PASSWORD, "0");
        return new PahoMqttClient().getMqttClientAuthenticate(context, Constants.MQTT_BROKER_URL, user, pass);
    }

    public static String jsonDeviceAdder(Context context, String devices, LinearLayout deviceHolder) {
        String TAG = "ContantsClass";
        try {
            JSONArray jsonArray = new JSONArray(devices);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d(TAG, "jsonDeviceAdder: " + jsonObject.toString());
                deviceHolder.addView(DtypeViews.Dtype1(context, jsonObject));
            }
            return "done";
        } catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public static String jsonObjectreader(String json, String name) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(name))
                return jsonObject.get(name).toString();
            else
                return "NA";

        } catch (JSONException e) {
            e.printStackTrace();
//            return "error";
            return "NA";
        }
    }

    public static JSONObject jsonobject(String json, String name) {
        try {

            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("contsnt", "jsonobject: " + e.getMessage());
            return null;
        }

    }

    public static JSONObject jsonobjectByName(String json, String name) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("contsnt", "jsonobject: " + e.getMessage());
            return null;
        }

    }

    public static String jsonobjectSTringJSON(JSONObject jsonObject, String name) {
        try {

            return jsonObject.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("contsnt", "jsonobject: " + e.getMessage());
            return null;
        }

    }

    public static JSONArray getJsonArray(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getJsonArray: " + e.getMessage());
        }
        return null;
    }

    public static void jsonDeviceAdderByRoom(Activity context, String data, LinearLayout deviceHolder, String room_id) {
        String TAG = "ConstantClass";
        Log.wtf("++inside jsondeviceAddweby", data);
        Log.wtf("++inside room_id", room_id);
        room_id = "e72f54e0-d052-45dc-963d-c75c1ff9f33c";
        try {
            JSONObject jsonOb = new JSONObject(data);
            JSONArray jsonArray = jsonOb.getJSONArray("rooms");
            Log.wtf("JSON_DATA_CONSTANTS", data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject json = new JSONObject(jsonArray.getString(i));
                if (Constants.FIRSTROOM) {
                    if (json.has("ID")) {

                        if (json.getString("ID").equals(room_id)) {
                            Log.e("TAGGG", "Room Id at add " + json.getString("ID") + " room_id " + room_id);
                            addByDeviceType(context, jsonObject, deviceHolder);
                        }
                        if (!Constants.jsonObjectreader(Constants.savetoShared(context).getString(Constants.ROOMS, Constants.EMPTY), "rooms").contains(json.getString("ID"))) {
                            addByDeviceType(context, jsonObject, deviceHolder);
                        }
                    } else {
                        addByDeviceType(context, jsonObject, deviceHolder);
                    }

                } else {
                    if (json.has("ID")) {
                        Log.d(TAG, Constants.jsonObjectreader(json.toString(), "ID") + " jsonDeviceAdder1: " + json.toString());
                        String dno = Constants.jsonObjectreader(jsonObject.toString(), "dno");
                        if (dno.equalsIgnoreCase("84f3ebfb696a")) {
                            Log.e("TAGGG", "Record id is " + dno + " post " + i);
                        }
                        Log.e("TAGG", "room_id " + room_id + " from pref " + json.getString("ID") + " COND >> " + json.getString("ID").equals(room_id));
                        if (json.getString("ID").equals(room_id)) {
                            addByDeviceType(context, jsonObject, deviceHolder);
                        }
                    }
                }
                /*
                if (jsonObject.has("room")) {

                if (jsonObject.has("ID")) {

                    Log.d(TAG, jsonObject.getString("ID") + "jsonDeviceAdder1: " + jsonObject.toString());
                    if (jsonObject.getString("ID").equals(room_id)) {
                        Log.d(TAG, "jsonDeviceAdder: " + jsonObject.toString());
                        switch (jsonObject.getInt("dtype")) {
                            case 1: deviceHolder.addView(DtypeViews.Dtype1(context, jsonObject)); break;
                            case 2: deviceHolder.addView(DtypeViews.Dtype2(context, jsonObject)); break;
                        }
                    }
                }*/

            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.getMessage();
        }
    }

    public static boolean isValidDtype2(JSONObject json) {
        try {
            if (json.has("d1")) {
                JSONObject deviceJSONonject = json.getJSONObject("d1");
                if (deviceJSONonject.has("state") &&
                        deviceJSONonject.has("r") &&
                        deviceJSONonject.has("g") &&
                        deviceJSONonject.has("b") &&
                        deviceJSONonject.has("w") &&
                        deviceJSONonject.has("ww") &&
                        deviceJSONonject.has("br")
                    // && deviceJSONonject.has("name")
                ) {
                    return true;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean isValidDtype1(JSONObject json) {
        try {
            if (json.has("d1")) {
                JSONObject deviceJSONonject = json.getJSONObject("d1");
                if (deviceJSONonject.has("state")
                    // &&deviceJSONonject.has("name")
                ) {
                    return true;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean isValidDtype5(JSONObject json) {
        try {
            if (json.has("d1")) {
                JSONObject deviceJSONonject = json.getJSONObject("d1");
                if (deviceJSONonject.has("state")
                    // &&deviceJSONonject.has("name")
                ) {
                    return true;
                }
            }
            if (json.has("d2")) {
                JSONObject deviceJSONonject = json.getJSONObject("d1");
                if (deviceJSONonject.has("state")
                    // &&deviceJSONonject.has("name")
                ) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    public static void addByDeviceType(Activity context, JSONObject jsonObject, LinearLayout deviceHolder) throws JSONException {
        int dtv = jsonObject.getInt("dtype");
//        int dtv = 2;

        if (showDeviceRype == dtv || showDeviceRype == 0) {
            mTDTV(dtv);
            switch (dtv) {
                case 1:
                    if (isValidDtype1(jsonObject))
                        deviceHolder.addView(DtypeViews.Dtype1(context, jsonObject));
                    break;
                case 2:
                    if (isValidDtype2(jsonObject)) {
                        deviceHolder.addView(DtypeViews.Dtype2(context, jsonObject));
                        Log.e("TAGG", "Added Object " + jsonObject);
                    } else
                        Log.e("TAGG", "Object Rejected " + jsonObject);
                    break;
                case 3: // empty case
                case 4: // empty case
//                    if (isValidDtype2(jsonObject))
//                        deviceHolder.addView(DtypeViews.Dtype2(context, jsonObject));
//                    break;
                case 5:/*if (isValidDtype5(jsonObject)) */
                    deviceHolder.addView(DtypeViews.Dtype5(context, jsonObject));
                    break;
                case 6:/*if (isValidDtype5(jsonObject)) */
                    deviceHolder.addView(DtypeViews.Dtype6(context, jsonObject));
                    break;
                case 7:/*if (isValidDtype5(jsonObject)) */
                    deviceHolder.addView(DtypeViews.Dtype7(context, jsonObject));
                    break;
                case 10:/*if (isValidDtype5(jsonObject)) */
                    deviceHolder.addView(DtypeViews.Dtype10(context, jsonObject));
                    break;
                case 11:/*if (isValidDtype5(jsonObject)) */
                    deviceHolder.addView(DtypeViews.Dtype11(context, jsonObject));
                    break;
            }
        }
    }

    public static void /*makeTabDevicetypeVisible*/mTDTV(int dtv) {
        try {
            if (deviceTypeHolder.findViewWithTag("myTag" + dtv) != null) {
                Log.d(TAG, "myTag" + dtv);
                deviceTypeHolder.findViewWithTag("myTag" + dtv).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable getDrawableByName(String gradename, Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(gradename, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);
    }

    public static String getDrawableString(String gradename, Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(gradename, "drawable",
                context.getPackageName());

        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    public static void getProgressBar(ProgressBar progressBar) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public static ArrayList<String> createdate(int start, int end) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }

    public static ArrayList<String> createtime(int no) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < no; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }

    public static ArrayList<String> createMinutes() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add("" + i);
            }
        }
        return list;
    }

    public static JSONObject stringToJsonObject(String msg) {
        try {
            return new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void jsonObjectPut(JSONObject jsonObject1, String key, String value) {
        try {
            jsonObject1.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void jsonObjectPutObject(JSONObject jsonObject1, String key, JSONObject value) {
        try {
            jsonObject1.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void jsonObjectPutInt(JSONObject jsonObject1, String key, int value) {
        try {
            jsonObject1.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getDeviceById(String deviceID, Context context) {
        String jsonRoom = Constants.savetoShared(context).getString(Constants.ROOMS, "null");
        try {
            JSONObject jsonObject = new JSONObject(jsonRoom);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonArrayCurrent = jsonArray.getJSONObject(i);
                if (jsonArrayCurrent.toString().contains(deviceID)) {
                    return jsonArrayCurrent;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setIMagetoView(String path, ImageView img, Context context) {

        try {
            Log.e("TAG", "Path of the remote icon " + path);
            InputStream is = context.getAssets().open(path + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            img.setImageBitmap(bitmap);
        } catch (IOException e) {

            img.setImageResource(R.drawable.tv_svg);
            e.printStackTrace();
        }

    }

    static void myvisibility(View view, boolean visible) {
        Log.d(TAG, "visibility: " + view);
        if (visible)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }
}