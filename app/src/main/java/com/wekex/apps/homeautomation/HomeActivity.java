package com.wekex.apps.homeautomation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wekex.apps.homeautomation.Activity.AddAccessories;
import com.wekex.apps.homeautomation.Activity.AddNewScene;
import com.wekex.apps.homeautomation.Activity.AddRulesActivity;
import com.wekex.apps.homeautomation.Activity.CreateScene;
import com.wekex.apps.homeautomation.Activity.EditScene;
import com.wekex.apps.homeautomation.Activity.NewSceneTempletActivity;
import com.wekex.apps.homeautomation.Activity.RuleListActivity;
import com.wekex.apps.homeautomation.Activity.SceneListForShortcut;
import com.wekex.apps.homeautomation.Interfaces.HomeScreenOperation;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.ProfilesAdapter;
import com.wekex.apps.homeautomation.adapter.RoomAdapter;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.GetAppHomeModel;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;
import com.wekex.apps.homeautomation.utils.ActivityProfile;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;
import com.wekex.apps.homeautomation.utils.GlobalLogging;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;
import com.wekex.apps.homeautomation.utils.ScenesEditor;
import com.wekex.apps.homeautomation.utils.Utils;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.volley.Request.Method.GET;
import static com.wekex.apps.homeautomation.utils.Constants.BASEURL;
import static com.wekex.apps.homeautomation.utils.Constants.DEVICETYPES;
import static com.wekex.apps.homeautomation.utils.Constants.FNAME;
import static com.wekex.apps.homeautomation.utils.Constants.IMAGE_GALLERY_REQUEST;
import static com.wekex.apps.homeautomation.utils.Constants.USER_ID;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader;
import static com.wekex.apps.homeautomation.utils.Constants.jsonobjectSTringJSON;
import static com.wekex.apps.homeautomation.utils.Constants.latitude;
import static com.wekex.apps.homeautomation.utils.Constants.longitude;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.DtypeViews.getGetDevice;
import static com.wekex.apps.homeautomation.utils.Utils.HEADING;
import static com.wekex.apps.homeautomation.utils.Utils.SCENEID;
import static com.wekex.apps.homeautomation.utils.Utils.addhomescene;
import static com.wekex.apps.homeautomation.utils.Utils.getprofiles;
import static com.wekex.apps.homeautomation.utils.Utils.removehomescene;

import com.onesignal.OSPermissionState;
import com.onesignal.OneSignal;

public class HomeActivity extends BaseActivity implements HomeScreenOperation, View.OnClickListener {

    String TAG = "HomeActivity";
    //    private LinearLayout roomholder;
    private LinearLayout quickAccessHolder;
    ArrayList<String> AssetsImages;
    ArrayList<JSONObject> profileJson;
    ImageView dia_ImageView;
    JSONArray sceneSavedList = new JSONArray();

    public final int REQUEST_EXTERNAL_STORAGE = 101;
    public final int REQUEST_FROM_CAMERA = 102;

    HomeScreenOperation _interface;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            GetAllData(true);
//            Log.e("TAG", "OnReceive Called Home Activity" + intent.getStringExtra("datafromService"));
        }
    };
    LinearLayout Lweather;
    RecyclerView rv_rooms;

    RoomAdapter _adapter;
    GetAppHomeModel main_object = new GetAppHomeModel();

    ImageView iv_refresh;
    Utility _utility;
    Gson _gson = new Gson();

    LinearLayout ll_rules;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        _interface = this;
//        getGetDevice(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileJson = new ArrayList<>();
//        roomholder = findViewById(R.id.roomholder);
        quickAccessHolder = findViewById(R.id.quickAccessHolder);

        TextView homepageUser = findViewById(R.id.homepageUser);
        homepageUser.setText(savetoShared(HomeActivity.this).getString(FNAME, "name"));

        _utility = new Utility(HomeActivity.this);
        iv_refresh = findViewById(R.id.iv_refresh);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());


        rv_rooms = findViewById(R.id.rv_room);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_rooms.setLayoutManager(mLayoutManager);

        try {
            String[] images = getAssets().list("icons");
            AssetsImages = new ArrayList<>(Arrays.asList(images));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Constants.savetoShared(this).edit().putString(Constants.PASSWORD, "12345").apply();
        Constants.GeneralpahoMqttClient = new PahoMqttClient().getHomeMqttClientAuthenticate(this, Constants.MQTT_BROKER_URL);
        startService(new Intent(this, MqttMessageService.class));

        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        Log.d(TAG, "onCreate: " + user);
        sendOneSignalUserID(user);
        Lweather = findViewById(R.id.weather);
        getWeather(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(this, 60 * 60 * 1000);
            }
        }, 60 * 1000);

       /* swipe_refresh_room.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh_room.setRefreshing(true);
                getWeather(true);
            }
        });*/

        iv_refresh.setOnClickListener(view -> {
            showProgressDialog("Fetching Data");
            getWeather(false);
        });

        ll_rules = findViewById(R.id.ll_rules);
        ll_rules.setOnClickListener(this);
    }

    public void getWeather(boolean isFromRefresh) {
        /*String lat = savetoShared(this).getString(LAT, "");
        String lon = savetoShared(this).getString(LON, "");*/
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=c5cdf5c47c8dd7639c29385bcdb062ab";
        Log.d(TAG, "getWeather: " + url);
        StringRequest stringRequest = new StringRequest(GET, url, response -> {
            try {
                JSONObject js = new JSONObject(response);
                View view = getLayoutInflater().inflate(R.layout.weather_home, null, false);
                TextView weather, humidiy, temprature, windspeed, city, pressure, weatherdate;
                ImageView weatherimg;
                weather = view.findViewById(R.id.weather);
                humidiy = view.findViewById(R.id.humidity);
                windspeed = view.findViewById(R.id.windspeed);
                temprature = view.findViewById(R.id.temp);
                weatherimg = view.findViewById(R.id.imageView);
                pressure = view.findViewById(R.id.pressure);
                city = view.findViewById(R.id.city);
                weatherdate = view.findViewById(R.id.weatherdate);

                String ic = js.getJSONArray("weather").getJSONObject(0).getString("icon");
                String IconUrl = "http://openweathermap.org/img/wn/" + ic + "@2x.png";

                Glide.with(this).load(IconUrl).into(weatherimg);
                weather.setText(js.getJSONArray("weather").getJSONObject(0).getString("main"));
                humidiy.setText(getString(R.string.humid, js.getJSONObject("main").getString("humidity")));
                windspeed.setText(getString(R.string.windkm, js.getJSONObject("wind").getString("speed")));
                temprature.setText(getString(R.string.degtemp, js.getJSONObject("main").getString("temp")));
                pressure.setText((getString(R.string.pressureunit, js.getJSONObject("main").getString("pressure"))));
                city.setText(js.getString("name"));
//                String result = String.format("%02d:%02d", js.getInt("dt") / 100, js.getInt("dt") % 100);

                weatherdate.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                //Glide.with(this).load(IconUrl).into(weatherimg);
                Lweather.removeAllViews();
                Lweather.addView(view);

                getDeviceTypes(isFromRefresh);

            } catch (JSONException e) {
                e.printStackTrace();
                getDeviceTypes(isFromRefresh);
                Log.e("TAG", "Exception at getWeather " + e.getMessage());
            }
            hideProgressDialog();

        }, error -> {
            Log.e("TAG", "OnError Called at get weather " + error.toString());
            hideProgressDialog();
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void sendOneSignalUserID(String user) {
        /*OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String osid = status.getSubscriptionStatus().getUserId();
*/
        String osid = OneSignal.getDeviceState().getUserId();
        String url = BASEURL + "Get/AddMobile?UID=" + user + "&pID=" + osid;
        Log.e("TAG", "sendOneSignalUserID Received " + url);
        RequestQueue queue = Volley.newRequestQueue(this);
        Log.e(TAG, "sendOneSignalUserID: " + url);
        StringRequest stringRequest = new StringRequest(GET, url, response -> {
            Log.d(TAG, "sendOneSignalUserID: " + response);
        },
                error -> {
                    error.getStackTrace();
                    Log.d(TAG, "sendOneSignalUserID: " + error.getCause());
                });
        queue.add(stringRequest);
    }

    private void getDeviceTypes(boolean isFromRefresh) {

        RequestQueue queue = Volley.newRequestQueue(this);
//        final String url = "http://209.58.164.151:88/api/Get/DType";
        final String url = APIClient.BASE_URL + "/api/Get/DType";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "getDeviceTypes: " + response.toString());
                    savetoShared(this).edit().putString(DEVICETYPES, jsonObjectreader(response.toString(), "Types")).apply();

                    GetAllData(isFromRefresh);
                },
                error -> Log.d("Error.Response", "Some Error" + error.getMessage() + error.toString())
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    public void addRoom() {

        final Dialog dialog = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_addroom, null, false);
        cardRoom_dialog = view1.findViewById(R.id.cardRoom);
        EditText editText = view1.findViewById(R.id.diaEditName);
        final TextView textView = view1.findViewById(R.id.roomName);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
//                dia_name[0] = s.toString();
            }
        });
        Button dia_add = view1.findViewById(R.id.dia_add);
        dia_add.setText("ADD");
        dia_add.setOnClickListener(v -> {

            if (isNameExist(editText.getText().toString().trim(), -1)) {
                return;
            }

            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("Required!");
                return;
            }

            if (editText.getText().toString().trim().equals("null")) {
                Toast.makeText(HomeActivity.this, "Invalid Name!", Toast.LENGTH_SHORT).show();
                return;
            }

            newly_added_room_name = editText.getText().toString().trim();
            showProgressDialog("Adding Room");
            View view2 = LayoutInflater.from(HomeActivity.this).inflate(R.layout.card_rooms, null, false);
            TextView textView1 = view2.findViewById(R.id.roomName);
            RelativeLayout dia_cardRoom = view2.findViewById(R.id.cardRoom);
            textView1.setText(editText.getText().toString().trim());
            DtypeViews.addRoom(HomeActivity.this, editText.getText().toString().trim());
            Constants.savetoShared(HomeActivity.this).edit().putString(editText.getText().toString().trim(), Constants.gradename).apply();
            dia_cardRoom.setBackground(Constants.dia_backdrawable);
            dialog.dismiss();
        });
        dialog.setContentView(view1);
        dialog.show();
    }


    RelativeLayout cardRoom_dialog;
    String _drawable_nm = "";
    String newly_added_room_name = "";

    public void getDradient(View view) {
        try {
            Constants.gradename = view.getTag().toString();
            Log.d(TAG, "getDradient: " + Constants.gradename);
            Constants.dia_backdrawable = Constants.getDrawableByName(Constants.gradename, this);
            cardRoom_dialog.setBackground(Constants.dia_backdrawable);
            cardRoom_dialog.setTag(Constants.dia_backdrawable);
            _drawable_nm = Constants.gradename;
        } catch (Exception e) {
            Log.e("TAG", "Exception e " + e.getMessage());
        }
    }

    public void addProfiles(String name, String sceneId, int pos) {

        final Dialog dialog = new Dialog(this);
        final String[] headText = new String[1];
        final String[] SubHeadText = new String[1];
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_addprofiles, null, false);
        Constants.dia_cardRoom = view1.findViewById(R.id.cardRoom);
        EditText head = view1.findViewById(R.id.dia_head_edit);
        head.setText(name);
        final EditText subhead = view1.findViewById(R.id.dia_subhead_edit);
        final TextView dia_prof_head = view1.findViewById(R.id.prof_heading);
        final TextView dia_prof_subhead = view1.findViewById(R.id.prof_subheading);
        dia_ImageView = view1.findViewById(R.id.prof_img);
        dia_prof_subhead.setText(name);
        dia_ImageView.setTag("icons/na");
        view1.findViewById(R.id.addprofilepic).setVisibility(View.VISIBLE);

        head.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dia_prof_head.setText(s.toString());
                headText[0] = s.toString();
            }
        });

        subhead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                dia_prof_subhead.setText(s.toString());
                SubHeadText[0] = s.toString();
            }
        });
        LinearLayout dia_qa_icon = view1.findViewById(R.id.dia_qa_icon);
        for (String path : AssetsImages) {
            Log.d(TAG, "addProfiles: " + path);
            loadImages(dia_qa_icon, path);
        }
        Button dia_add = view1.findViewById(R.id.dia_add);
        dia_add.setOnClickListener(v -> {
            try {
                if (jsonArray_global != null)
                    for (int i = 0; i < jsonArray_global.length(); i++) {
                        JSONObject obj = jsonArray_global.getJSONObject(i);
                        Log.e("TAGGG", "Scene from array " + obj.getString("heading") + " From Edit " + head.getText().toString().trim() + " COND " + obj.getString("heading").equalsIgnoreCase(head.getText().toString().trim()));
                        if (obj.has("heading") && obj.getString("heading").equalsIgnoreCase(head.getText().toString().trim())) {
                            Toast.makeText(HomeActivity.this, "Name Already Exist!", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                            return;
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONArray jsonArray = addhomescene(HomeActivity.this, sceneId, head.getText().toString(), subhead.getText().toString(), dia_ImageView.getTag().toString(), pos);
            Log.d(TAG, "onClick: " + jsonArray.toString());

            callProfAdapter(jsonArray);
            dialog.dismiss();
        });
        dialog.setContentView(view1);
        dialog.show();
    }

    public void createScene(View View) {
       /* Intent intent = new Intent(this, ScenesEditor.class);
        intent.putExtra("Devices", "new");
        startActivity(intent);*/
        if (sceneSavedList.length() <= 10)
            showRoomLists(false, "", "");
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
            dialog.setTitle("Scene limit exceeded");
            dialog.setMessage("You can add only 10 scene, please delete existing scene to add.");
            dialog.setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        }
    }

    public void showRoomLists(boolean isFromEditScene, String roomName, String _json_scenedata) {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.scene_picker, null, false);
        dialog.setContentView(view);
        LinearLayout sceneHolder = view.findViewById(R.id.picker_list);
        sceneHolder.removeAllViews();
        View inflate = LayoutInflater.from(this).inflate(R.layout.scene_picker_item, null, false);
        TextView headings = inflate.findViewById(R.id.schedules_head);
        if (isFromEditScene)
            headings.setText(getResources().getString(R.string.edit_scene_room_selection_msg));
        else
            headings.setText(getResources().getString(R.string.select_room_msg));
        headings.setVisibility(View.VISIBLE);
        headings.setTextSize(16);
        headings.setTextColor(getResources().getColor(R.color.black));

        TextView schedules_name_ = inflate.findViewById(R.id.schedules_name);
        schedules_name_.setVisibility(View.GONE);

        sceneHolder.addView(inflate);
        for (int i = 0; i < main_object._lst_rooms.size() - 1; i++) {
            View dli = LayoutInflater.from(this).inflate(R.layout.scene_picker_item, null, false);
            LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);

            TextView schedules_name = dli.findViewById(R.id.schedules_name);
            schedules_name.setText(main_object.get_lst_rooms().get(i).getName());
            schedules_name.setTag(main_object.get_lst_rooms().get(i).getID());
            dli_parent.setOnClickListener(v -> {
                if (!isFromEditScene) {
                    Intent intent = new Intent(HomeActivity.this, NewSceneTempletActivity.class);
                    intent.putExtra("Devices", "new");
                    intent.putExtra("roomID", (String) schedules_name.getTag());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, AddNewScene.class);
                    intent.putExtra("Devices", "update");
                    intent.putExtra("roomID", (String) schedules_name.getTag());
                    intent.putExtra("sceneJsonData", _json_scenedata);
                    intent.putExtra("name", roomName);
                    startActivity(intent);
                }
                dialog.dismiss();
            });
            sceneHolder.addView(dli);
        }
        dialog.show();
    }

    public void triggerscene(String id) {
        if (id.equals("add")) {
//            showRoomLists();
            Intent _intent = new Intent(this, SceneListForShortcut.class);
            startActivity(_intent);
        } else {
            showProgressDialog("Trigering...");
            String url = BASEURL + "Get/triggerScene?ID=" + id;
            Log.d(TAG, "triggerscene: " + url);
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(GET, url, response -> {
                Log.d(TAG, "triggerscene: " + response);
                hideProgressDialog();
            }, error -> {
                Log.d(TAG, "triggerscene: " + error.getCause());
                hideProgressDialog();
            });
            queue.add(request);
        }
    }

    JSONArray jsonArray_global;
    ProfilesAdapter _adapter_scene;

    private void callProfAdapter(JSONArray jsonArray) {
        JSONArray _arr = new JSONArray();
        try {
            String _json = PreferencesHelper.getString(HomeActivity.this, Constants.ShortcutList);
            ArrayList<String> _selectedList = (ArrayList<String>) _gson.fromJson(_json,
                    new TypeToken<ArrayList<String>>() {
                    }.getType());

            if (_selectedList != null && _selectedList.size() != 0)
                for (int i = 0; i < jsonArray.length(); i++) {
                    for (int j = 0; j < _selectedList.size(); j++) {
                        try {
                            JSONObject _object = jsonArray.getJSONObject(i);
                            if (_object.getString("ID").equalsIgnoreCase("-1") || _object.getString("ID").equalsIgnoreCase(_selectedList.get(j))) {
                                _arr.put(_object);
                                break;
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            else {
                JSONObject _myObject = new JSONObject();
                _myObject.put("Name", "add");
                _myObject.put("ID", "-1");
                _arr.put(_myObject);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at Home " + e.toString());
        }
//        sceneSavedList = jsonArray;
        sceneSavedList = _arr;
        if (sceneSavedList.length() > 1)
            ((LinearLayout) findViewById(R.id.dummyscenes)).removeAllViews();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profileRecycler);
        recyclerView.setHasFixedSize(true);

        _adapter_scene = new ProfilesAdapter(this, sceneSavedList);
        recyclerView.setAdapter(_adapter_scene);
        jsonArray_global = sceneSavedList;
    }

    public void Addimages(View view) {

        PopupMenu popup = new PopupMenu(this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_images, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.fromCamera:
                    if (verifyStoragePermissions(true)) {
                        photoCameraIntent();
                    }
                    break;
                case R.id.fromGallery:
                    if (verifyStoragePermissions(false)) {
                        photoGalleryIntent();
                    }
                    break;
            }
            return true;
        });
        popup.setOnDismissListener(menu -> view.setVisibility(View.VISIBLE));
        // popup.getMenu().removeItem(R.id.scheduled_d);
        /*MenuPopupHelper menuHelper = new MenuPopupHelper(this;, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);*/
        popup.show();


    }

    int CAMERA_REQUEST = 1;
    File photoFile = null;

    private void photoCameraIntent() {
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoFile != null) {
           /* Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoURI);*/
            startActivityForResult(pictureIntent,
                    CAMERA_REQUEST);
        }
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Acrticv", "onActivityResult: ");
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d("Acrticv", "onActivityResult: fragment");
            Uri fileUri = data.getData();


            File myfile = new File(Utils.getPath(fileUri, HomeActivity.this));
            if (myfile.exists()) {
                dia_ImageView.setTag(myfile.getAbsolutePath());
                Glide.with(this)
                        .load(myfile) // Uri of the picture
                        .into(dia_ImageView);
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            dia_ImageView.setImageBitmap(photo);
            if (photoFile != null)
                dia_ImageView.setTag(photoFile.getAbsolutePath());
            else
                Toast.makeText(this, "Photofile null", Toast.LENGTH_LONG).show();
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public boolean verifyStoragePermissions(boolean isFromCamera) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            /*ActivityCompat.requestPermissions(
                    this,
                    ,
                    REQUEST_EXTERNAL_STORAGE
            );*/
            if (Build.VERSION.SDK_INT >= 23) {
                if (!isFromCamera)
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_EXTERNAL_STORAGE);
                else
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_FROM_CAMERA);
            }
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photoGalleryIntent();
                } else
                    verifyStoragePermissions(false);
                break;
            case REQUEST_FROM_CAMERA:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photoCameraIntent();
                } else
                    verifyStoragePermissions(true);
                break;
        }
    }

    public void addQuickAccess(View view) {
        final Dialog dialog = new Dialog(this);
        final String[] dia_name = new String[1];
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_add_quickaccess, null, false);
        Constants.dia_cardRoom = view1.findViewById(R.id.cardRoom);

        EditText editText = view1.findViewById(R.id.diaEditName);
        final TextView textView = view1.findViewById(R.id.qa_text);
        dia_ImageView = view1.findViewById(R.id.qa_img);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
                dia_name[0] = s.toString();
            }
        });
        LinearLayout dia_qa_icon = view1.findViewById(R.id.dia_qa_icon);
        for (String path : AssetsImages) {
            Log.d(TAG, "addProfiles: " + path);
            loadImages(dia_qa_icon, path);
        }
        Button dia_add = view1.findViewById(R.id.dia_add);
        dia_add.setOnClickListener(v -> {
            View view2 = LayoutInflater.from(HomeActivity.this).inflate(R.layout.quick_access, null, false);
            TextView textView1 = view2.findViewById(R.id.qa_text);
            ImageView qa_img = view2.findViewById(R.id.qa_img);
            setIMagetoView(dia_ImageView.getTag().toString(), qa_img);
            textView1.setText(dia_name[0]);
            quickAccessHolder.addView(view2);
            dialog.dismiss();
        });
        dialog.setContentView(view1);
        dialog.show();
    }

    public void loadImages(LinearLayout linearLayout, String path) {
        String imgePath = "icons/" + path;
        InputStream is = null;
        try {

            ImageView imageView = new ImageView(HomeActivity.this);
            imageView.setTag(imgePath);
            imageView.setOnClickListener(v -> DiasetImage(v));

            is = getAssets().open(imgePath);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            linearLayout.addView(imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void DiasetImage(View view) {
        try {
            InputStream is = getAssets().open(view.getTag().toString());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            dia_ImageView.setImageBitmap(bitmap);
            dia_ImageView.setTag(view.getTag().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIMagetoView(String path, ImageView img) {
        Bitmap bitmap;
        try {
            if (!path.contains("storage")) {
                InputStream is = getAssets().open(path);
                bitmap = BitmapFactory.decodeStream(is);
            } else {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(path, bmOptions);
//                bitmap = compressedBitmap(this, path);
            }

            img.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //DtypeViews.getList(this);
        String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
//        addViews(jsonString);
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));

        getAllScenes();
    }

    @Override
    protected void onRestart() {
        //DtypeViews.getList(this);
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }


    public void menu(View view) {
        PopupMenu popup = new PopupMenu(HomeActivity.this, view);
        popup.getMenuInflater()
                .inflate(R.menu.home_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.about:
                    startActivity(new Intent(HomeActivity.this, AboutAndChange.class)
                            .putExtra("what", "about"));
                    break;

                case R.id.logout:
                    logout(view);
                    break;

                case R.id.profile:
                    profile(view);
                    break;

                case R.id.change_log:
                    startActivity(new Intent(HomeActivity.this, AboutAndChange.class)
                            .putExtra("what", "Change_log"));
                    break;

                case R.id.logs:
                    startActivity(new Intent(this, GlobalLogging.class));
                    break;
            }

            return true;
        });
        popup.show();
    }

    public void logout(View view) {
        savetoShared(HomeActivity.this).edit().putString(USER_ID, "NA").apply();
        Utility.setIsFresh(this, true);
        startActivity(new Intent(this, LoginReg.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void addDeviceHome(View view) {
        Dialog dia_addDevice = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_add_device_home, null, false);
        dia_addDevice.setContentView(view1);
        LinearLayout linearLayout = view1.findViewById(R.id.Dia_roomholder);
        linearLayout.removeAllViews();
        String jstr = Constants.savetoShared(HomeActivity.this).getString(Constants.ROOMS, "null");
        if (!jstr.equals("null")) {
            try {
                JSONObject jsonObject = new JSONObject(jstr);
                JSONArray rooms = Constants.getJsonArray(jsonObject, "rooms");
                for (int i = 0; i < rooms.length(); i++) {
                    View viewDiaRooms = LayoutInflater.from(HomeActivity.this).inflate(R.layout.add_device_home_dialog_room_holder, null, false);
                    TextView Dia_RoomsTV = viewDiaRooms.findViewById(R.id.dia_room);
                    Dia_RoomsTV.setTag(rooms.getJSONObject(i).get("ID").toString());
                    // Log.d(TAG, "addRooms: "+rooms.getJSONObject(i).get("name"));
                    String roomName = rooms.getJSONObject(i).get("name").toString();
                    Dia_RoomsTV.setText(roomName);
                    Dia_RoomsTV.setOnClickListener(v -> {
//                            Toast.makeText(HomeActivity.this, "hey " + v.getTag(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this, SmartConfig.class);
                        intent.putExtra("RoomId", v.getTag().toString());
                        startActivityForResult(intent, 123);
                        dia_addDevice.dismiss();
                    });
                    linearLayout.addView(viewDiaRooms);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dia_addDevice.show();
//        dia_addDevice.show();
    }

    public void callWidget(View view) {
        startActivity(new Intent(this, widget.class));
    }

    public void schedules(View view) {
        Intent intent = new Intent(this, schedules.class);
        intent.putExtra("DeviceInfos", "");
        startActivity(intent);
    }

    public void startRemote(View view) {
        Intent intent = new Intent(this, rf_remote.class);
        intent.putExtra("remotes", "");
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void scene_edit_menu(JSONObject sceneObj, int pos) {
        View view = LayoutInflater.from(this).inflate(R.layout.home_scene_menu, null, false);
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view);

        String heading, sceneid;

        TextView editScene, removeScene, cancel;
        editScene = view.findViewById(R.id.editScene);
        removeScene = view.findViewById(R.id.remoscene);
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());
        try {
            heading = sceneObj.getString("Name");
            sceneid = sceneObj.getString("ID");
            if (sceneid.equals("add"))
                dialog.dismiss();
            editScene.setOnClickListener(v -> {
                showRoomLists(true, heading, sceneObj.toString());
                dialog.dismiss();
            });

            removeScene.setOnClickListener(v -> {
                try {
                    delScene(sceneid, pos);
                } catch (Exception e) {
                    Log.e("TAG", "Exception at delete " + e.toString());
                }
                dialog.dismiss();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
    }


    public void profile(View view) {
        startActivity(new Intent(HomeActivity.this, ActivityProfile.class));
    }

    public void refreshWeather(View view) {
        if (!startgps().equals("NA")) {
            getWeather(false);
            showProgressDialog("Refreshing...");
        }
    }

    @Override
    public void selectRoom(String Id, String name) {
        if (!Id.equalsIgnoreCase("-1")) {
            Intent intent = new Intent(HomeActivity.this, RoomActivity.class);
            intent.putExtra("room_id", Id);
            intent.putExtra("room_name", name);
            startActivity(intent);
        } else {
            addRoom();
        }
    }

    int _position_dialog = 0;

    @Override
    public void updateRoom(String Id, String roomName, int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(HomeActivity.this);

        _position_dialog = position;
        builder.setTitle("Attention !")
                .setMessage("Do you want to Edit or Remove " + roomName + " Room ?")
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    String _data = _utility.getString(Id);
                    Gson gson = new Gson();
                    AllDataResponseModel _all_data = gson.fromJson(_data, AllDataResponseModel.class);
                    if (_data.isEmpty()) {
                        Toast.makeText(HomeActivity.this, "Operation failed!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (_all_data.getObjData().size() == 0) {
                        showProgressDialog("Deleting..");
                        removeRoom(HomeActivity.this, Id);
                    } else {
                        new AlertDialog.Builder(HomeActivity.this).setTitle("Delete Failed").setMessage(_all_data.getObjData().size() + " Devices in " + roomName).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }
                })
                .setNegativeButton(R.string.editRoom, (dialog, which) -> {
                    editRoom(Id, roomName, position);
                    dialog.dismiss();
                })
                .setNeutralButton("cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    _position_dialog = 0;
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void updateRoomName(String _id, String name, int position, Drawable drawable, String _drawable_nm) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = "http://smartyhome.in" +"/api/Get/updRoom?UID=" + user + "&roomID=" + _id + "&RoomName=" + name;
        Log.e("TAGG", "URL of the UpdateRoom" + url + " position " + position + " Room size " + main_object.get_lst_rooms().size() + " _drawable_nm " + _drawable_nm);
        Observable<SuccessResponse> observable = apiInterface.updateRoom(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SuccessResponse successResponse) {
                hideProgressDialog();
                if (successResponse.getSuccess() && main_object.get_lst_rooms().size() > 0) {
                    main_object.get_lst_rooms().get(position).setName(name);
                    main_object.get_lst_rooms().get(position).setRoomBackground(drawable);
                    main_object.get_lst_rooms().get(position).set_drawable_name(_drawable_nm);

                    _adapter.notifyItemChanged(position);
                    _utility.putString(main_object.get_lst_rooms().get(position).getID() + "_clr", _drawable_nm);
                    Toast.makeText(HomeActivity.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "OnSuccess ID " + main_object.get_lst_rooms().get(position).getID() + "_clr" + " _drawable_nm " + _drawable_nm);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                Log.e("TAGGG", "Exception at update Room " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void editRoom(String roomId, String roomName, int position) {
        final Dialog dialog = new Dialog(HomeActivity.this);
        final String[] dia_name = new String[1];
        View view1 = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_addroom, null, false);
        cardRoom_dialog = view1.findViewById(R.id.cardRoom);
        cardRoom_dialog.setTag(main_object.get_lst_rooms().get(position).getRoomBackground());
        if (main_object.get_lst_rooms().get(position).getRoomBackground() == null)
            cardRoom_dialog.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradtwo));
        else
            cardRoom_dialog.setBackgroundDrawable(main_object.get_lst_rooms().get(position).getRoomBackground());
        _drawable_nm = "";
        EditText editText = view1.findViewById(R.id.diaEditName);
        editText.setText(roomName);
        dia_name[0] = roomName;
        final TextView textView = view1.findViewById(R.id.roomName);
        textView.setText(roomName);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
                dia_name[0] = s.toString();
            }
        });
        Button dia_add = view1.findViewById(R.id.dia_add);
        dia_add.setOnClickListener(v -> {
            if (!isNameExist(editText.getText().toString().trim(), position)) {
                showProgressDialog("Updating...");
                updateRoomName(roomId, editText.getText().toString().trim(), position, (Drawable) cardRoom_dialog.getTag(), _drawable_nm);
                Log.e("TAG", "Edit Room RoomID " + roomId + " _drawable_nm " + _drawable_nm);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view1);
        dialog.show();
    }

    public boolean isNameExist(String roomName, int position) {
        for (int i = 0; i < main_object.get_lst_rooms().size(); i++) {
            if (position != i && main_object.get_lst_rooms().get(i).getName().equalsIgnoreCase(roomName)) {
                Toast.makeText(this, "Name Already Exist!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void removeRoom(Context context, String RoomId) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        Log.d(TAG, user + "removeDeviceFromRoom: " + RoomId);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, "{ \"method\" : \"deleteRoom\" , \"data\" : {\"room\":\"" + RoomId + "\"}}", 1, "u/" + user + "/pub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, "getGetDevice" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void GetAllData(boolean isFromRefresh) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = APIClient.BASE_URL + "/api/Get/getAppHome?UID=" + user;
        Log.e("TAG", "GetAllData URL " + url);

        Observable<GetAppHomeModel> observable = apiInterface.getAllData(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GetAppHomeModel>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(GetAppHomeModel _object) {
                //hideProgressDialog();
                try {
                    if (_object != null) {
                        main_object = _object;
                        Gson gson = new Gson();
                        String str = gson.toJson(main_object);
                        GetAppHomeModel.rooms _obj_rooms = new GetAppHomeModel.rooms();
                        _obj_rooms.setName("AddNew");
                        _obj_rooms.setID("-1");

                        main_object.get_lst_rooms().add(_obj_rooms);
                        Constants.savetoShared(HomeActivity.this).edit().putString(Constants.ROOMS, str).apply();

                        for (int i = 0; i < main_object.get_lst_rooms().size(); i++) {
                            String _back_name = _utility.getString(main_object.get_lst_rooms().get(i).getID() + "_clr");
                            Log.e("TAG", "OnSuccess ID RoomID " + main_object.get_lst_rooms().get(i).getID() + "_clr" + " _back_name " + _back_name);
                            if (_back_name != null && !_back_name.isEmpty()) {
                                Drawable drawable = Constants.getDrawableByName(_back_name, HomeActivity.this);
                                main_object.get_lst_rooms().get(i).set_drawable_name(_back_name);
                                main_object.get_lst_rooms().get(i).setRoomBackground(drawable);
                            } else {
                                if (newly_added_room_name.equalsIgnoreCase(main_object.get_lst_rooms().get(i).getName()) && _drawable_nm != null && !_drawable_nm.isEmpty()) {
                                    newly_added_room_name = "";
                                    Drawable drawable = Constants.getDrawableByName(_drawable_nm, HomeActivity.this);
                                    main_object.get_lst_rooms().get(i).set_drawable_name(_drawable_nm);
                                    main_object.get_lst_rooms().get(i).setRoomBackground(drawable);
                                }
                            }
                        }
                        _adapter = new RoomAdapter(main_object, HomeActivity.this, _interface, str);
                        rv_rooms.setAdapter(_adapter);

                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at parse" + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
//                swipe_refresh_room.setRefreshing(false);
                Log.e("TAGGG", "Exception at update Room " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.wtf("DESTROY_EVENT", "CALLED");
    }

    void getAllScenes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        final String url = APIClient.BASE_URL + "/api/Get/getScene?UID=" + user;
        StringRequest getRequest = new StringRequest(GET, url,
                response -> {
                    JSONArray remo;
                    String InvalidJSON = response.replace("\\\"", "\"").trim();
                    try {
                        JSONObject mainJsop = stringToJsonObject(response);
                        Log.e("TAG", "response of getAllScenes " + response);
                        remo = mainJsop.getJSONArray("Scene");
                        JSONArray _array = new JSONArray();
                        JSONObject _myObject = new JSONObject();
                        _myObject.put("Name", "add");
                        _myObject.put("ID", "-1");
                        _array.put(_myObject);
                        for (int i = 0; i < remo.length(); i++) {
                            JSONObject jsonObject = remo.getJSONObject(i);
                            JSONArray devices = jsonObject.getJSONArray("Devices");
                            String sceneName = jsonobjectSTringJSON(jsonObject, "Name");
                            String id = jsonObject.getString("ID");
                            _myObject = new JSONObject();
                            _myObject.put("Name", sceneName);
                            _myObject.put("ID", id);
                            _myObject.put("Devices", devices);
                            _array.put(_myObject);
                        }
                        callProfAdapter(_array);
                        hideProgressDialog();
                        Log.e(TAG, "Get All scene size  " + remo.length());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        hideProgressDialog();
                    }
                },
                error -> {
                    Log.d("Error.Response", "error" + error.getMessage());
                    Toast.makeText(this, "Unable to loadScenes", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    public void delScene(String id, int pos) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/delScene?ID=" + id;
        Log.e("TAG", "Delete Scenes <>" + url);
        showProgressDialog(getResources().getString(R.string.please_wait));
        Observable<SuccessResponse> observable = apiInterface.delScene(url);
        try {
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(SuccessResponse successResponse) {
                    Toast.makeText(HomeActivity.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                    if (successResponse.getSuccess()) {
                        getAllScenes();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAGG", "OnError " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {
                    hideProgressDialog();
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at e " + e.getMessage(), e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_rules:
                startActivity(new Intent(HomeActivity.this, RuleListActivity.class));
                break;
        }
    }
}
