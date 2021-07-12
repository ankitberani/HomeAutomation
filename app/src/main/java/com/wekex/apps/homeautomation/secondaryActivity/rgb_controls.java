package com.wekex.apps.homeautomation.secondaryActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;
import com.triggertrap.seekarc.SeekArc;
import com.wekex.apps.homeautomation.Activity.CreateScene;
import com.wekex.apps.homeautomation.Activity.CreateSchedule;
import com.wekex.apps.homeautomation.Activity.CreateScheduleType_Other;
import com.wekex.apps.homeautomation.Activity.ScheduleList;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.ColorPickerAdapter;
import com.wekex.apps.homeautomation.adapter.SceneAdapter;
import com.wekex.apps.homeautomation.adapter.ScheduleAdapter;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.DeviceType;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.color_item;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.model.schedule_model;
import com.wekex.apps.homeautomation.model.schedule_model_type_other;
import com.wekex.apps.homeautomation.utils.BubbleFlag;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.GradienSeekBar;
import com.wekex.apps.homeautomation.utils.StringConstants;
import com.wekex.apps.homeautomation.utils.Utils;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wekex.apps.homeautomation.utils.Constants.DEVICETYPES;
import static com.wekex.apps.homeautomation.utils.Constants.IMAGE_GALLERY_REQUEST;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.DtypeViews.d1pname;
import static com.wekex.apps.homeautomation.utils.DtypeViews.dtype;
import static com.wekex.apps.homeautomation.utils.DtypeViews.key;
import static com.wekex.apps.homeautomation.utils.DtypeViews.pname;
import static com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON;

public class rgb_controls extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener, rgb_color_interface {

    String TAG = "rgb_controls_activity";
    private String dno1;
    String type1;
    private int colorpic;
    String state;
    LinearLayout rbg_on_layout;
    LinearLayout rbg_off_layout;
    TextView onoff_colorpicker;
    //    ColorPickerView picker;
    ColorPicker picker;
    SeekBar _brightness;
    // SeekBar bright;
    SeekBar warmwhite;
    GradienSeekBar alphaSlider;


    int EDIT_SCENE = 151;
    TextView tv_bright_perc;

    Utility utility;

    SeekBar seekbar_clr_picker;


    TextView tv_r, tv_g, tv_b;
    View view;

    LinearLayout ll_color_pallete;


    RecyclerView rv_scenelist;
    SceneAdapter _sceneAdapter;
    BottomNavigationBar bottomNavigationBar;
    ArrayList<scene_model.Scene> _mainList;
    LinearLayout ll_scene_main;
    String key1;

    TextView tv_scene_type;
    Toolbar toolbar;

    RecyclerView rv_schedule_list;
    LinearLayout rl_schedule_list;
    ImageView btn_new_schedule;

    ArrayList<DeviceType> _lst_device_type;
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_controls);
        utility = new Utility(this);
        tv_scene_type = findViewById(R.id.tv_show_scene_type);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);

        onoff_colorpicker = findViewById(R.id.onoff_colorpicker);

//        bottomNavigationBar.setBackgroundColor(getResources().getColor(R.color.white));
//        bottomNavigationBar
//                .addItem(new BottomNavigationItem(R.drawable.color_icon, getResources().getString(R.string.color)).setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .addItem(new BottomNavigationItem(R.drawable.ww_icon_bottom, "White").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .addItem(new BottomNavigationItem(R.drawable.color_pallete, "Color Pallete").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .addItem(new BottomNavigationItem(R.drawable.sceneicon, "Scene").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .addItem(new BottomNavigationItem(R.drawable.sound_icon, "Sound").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .addItem(new BottomNavigationItem(R.drawable.schedule, "Schedule").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
//                .initialise();


        jsonString = getIntent().getStringExtra("jsonString");
        Log.d(TAG, "onCreate: " + jsonString);
        try {
            JSONObject jObj = new JSONObject(jsonString);
            String dType = jObj.getString("dtype");
            if (dType.equals("4")) {
                bottomNavigationBar.setBackgroundColor(getResources().getColor(R.color.white));
                bottomNavigationBar
                        .addItem(new BottomNavigationItem(R.drawable.color_icon, getResources().getString(R.string.color)).setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.color_pallete, "Color Pallete").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.sceneicon, "Scene").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.sound_icon, "Sound").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.schedule, "Schedule").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .initialise();
            } else {
                bottomNavigationBar.setBackgroundColor(getResources().getColor(R.color.white));
                bottomNavigationBar
                        .addItem(new BottomNavigationItem(R.drawable.color_icon, getResources().getString(R.string.color)).setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.ww_icon_bottom, "White").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.color_pallete, "Color Pallete").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.sceneicon, "Scene").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.sound_icon, "Sound").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .addItem(new BottomNavigationItem(R.drawable.schedule, "Schedule").setActiveColorResource(R.color.white).setInActiveColor(R.color.colorAccent_CrashReporter))
                        .initialise();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String pname1 = Constants.jsonObjectreader(jsonString, pname);

        if (getIntent().hasExtra("_name")) {
            pname1 = getIntent().getStringExtra("_name");
        }

        if (getIntent().hasExtra("dno"))
            dno1 = getIntent().getStringExtra("dno");
        Log.wtf("TYPE_RGB", dno1);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(pname1);
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ll_color_pallete = findViewById(R.id.ll_color_pallete);


        tv_r = findViewById(R.id.tv_r);
        tv_g = findViewById(R.id.tv_g);
        tv_b = findViewById(R.id.tv_b);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        view = findViewById(R.id.view_1);

        tv_bright_perc = findViewById(R.id.tv_bright_perc);
        _brightness = findViewById(R.id.brightness);
        _brightness.incrementProgressBy(1);
        _brightness.setMax(100);

        alphaSlider = findViewById(R.id.alphaSlider);

        Constants.brightness = 100;

        initDiy();
        picker = findViewById(R.id.picker11_new);
        picker.setShowOldCenterColor(false);
        colorpic = picker.getColor();
        rgbdialog(jsonString, this);
        picker.setTouchAnywhereOnColorWheelEnabled(true);

        alphaSlider.setOnALphaChangeListener(new GradienSeekBar.OnAlphaChangeListener() {
            @Override
            public void onAlphaColorChnage(int color) {
                Log.e("TAGGG", "onAlphaColorChnage called");
                Log.d(TAG, "r " + Color.red(color) + " g " + Color.green(color) + " b " + Color.blue(color) + "onAlphaColorChnage: " + color);
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);

                Constants.warm_white = 0;
                Constants.white = 0;
                setRBG();
            }

            @Override
            public void onAlphaColorChnaged(boolean alpha) {
                publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
            }
        });

        iv_add_color = findViewById(R.id.iv_add_color);
        setupRecyclerView();

        rv_scenelist = findViewById(R.id.rv_scenelist);
        ll_scene_main = findViewById(R.id.ll_scene_main);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0: {
                        colorTab();
                        ll_color_pallete.setVisibility(View.GONE);
                        ll_scene_main.setVisibility(View.GONE);
                        rl_schedule_list.setVisibility(View.GONE);

                        btn_new_schedule.setVisibility(View.GONE);
                    }
                    break;
                    case 1: {
                        tempR = Constants.red;
                        tempG = Constants.green;
                        tempB = Constants.blue;
                        tempcolor = colorpic;
                        whiteTab();
                        ll_color_pallete.setVisibility(View.GONE);
                        ll_scene_main.setVisibility(View.GONE);

                        rl_schedule_list.setVisibility(View.GONE);
                        btn_new_schedule.setVisibility(View.GONE);
                    }
                    break;

                    case 2: {
//                        showPallet();
                        tempR = Constants.red;
                        tempG = Constants.green;
                        tempB = Constants.blue;
                        tempcolor = colorpic;

                        ll_color_pallete.setVisibility(View.VISIBLE);
                        findViewById(R.id.rgblayout).setVisibility(View.GONE);
                        findViewById(R.id.whitelayout).setVisibility(View.GONE);
                        ll_scene_main.setVisibility(View.GONE);
                        rl_schedule_list.setVisibility(View.GONE);
                        btn_new_schedule.setVisibility(View.GONE);
                    }
                    break;
                    case 3: {

                        tempR = Constants.red;
                        tempG = Constants.green;
                        tempB = Constants.blue;
                        tempcolor = colorpic;

                        Log.e("TAGGG", "Color Value at case 3 Red " + tempR + " Green " + tempG + " Blue " + tempB + " tempColor " + tempcolor);
                        ll_scene_main.setVisibility(View.VISIBLE);
                        ll_color_pallete.setVisibility(View.GONE);
                        findViewById(R.id.rgblayout).setVisibility(View.GONE);
                        findViewById(R.id.whitelayout).setVisibility(View.GONE);
                        rl_schedule_list.setVisibility(View.GONE);
                        btn_new_schedule.setVisibility(View.GONE);
                    }
                    break;

                    case 5: {
                        tempR = Constants.red;
                        tempG = Constants.green;
                        tempB = Constants.blue;
                        tempcolor = colorpic;

                        Log.e("TAGGG", "Color Value at case 3 Red " + tempR + " Green " + tempG + " Blue " + tempB + " tempColor " + tempcolor);
                        ll_scene_main.setVisibility(View.GONE);
                        ll_color_pallete.setVisibility(View.GONE);
                        findViewById(R.id.rgblayout).setVisibility(View.GONE);
                        findViewById(R.id.whitelayout).setVisibility(View.GONE);

                        rl_schedule_list.setVisibility(View.VISIBLE);
                        btn_new_schedule.setVisibility(View.VISIBLE);
                    }
                    break;
                }

            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }

        });
        setupSceneList();
        getSceneFromServer();
        tv_scene_type.setOnClickListener(view -> {

            if (getTotalScene() >= 10) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(rgb_controls.this);
                dialog.setTitle("Scene limit exceeded");
                dialog.setMessage("You can add only 10 scene, please delete existing scene to add.");
                dialog.setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss());
                dialog.show();
                return;
            }

            String[] colors = {"Color", "White"};
            AlertDialog.Builder builder = new AlertDialog.Builder(rgb_controls.this);
            builder.setTitle("Scene Type");
            builder.setItems(colors, (dialog, which) -> {
                // the user clicked on colors[which]
                Intent _intent = new Intent(rgb_controls.this, CreateScene.class);
                _intent.putExtra("type", which);
                _intent.putExtra("dno", dno1);
                _intent.putExtra("dtype", type1);
                Gson gson = new Gson();
                String list_all = gson.toJson(_mainList);
                _intent.putExtra("list", list_all);
                startActivityForResult(_intent, 105);

                dialog.dismiss();
            });
            builder.show();
        });

        seekbar_clr_picker = findViewById(R.id.seekbar_clr_picker);
        seekbar_clr_picker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setColor(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
            }
        });


        picker.setOnColorSelectedListener(color -> {
            Log.e("TAGG", "Color is setOnColorChangedListener " + color);
            try {
                colorpic = color;
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);

                alphaSlider.setColor(color);
                setRBG();

                Constants.white = 0;
                Constants.warm_white = 0;
                publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at on color " + e.getMessage(), e);
            }
        });

        Gson gson = new Gson();
        String deviceTypes = savetoShared(this).getString(DEVICETYPES, "NA");
        ArrayList<DeviceType> _list_device_type = gson.fromJson(deviceTypes, new com.google.common.reflect.TypeToken<ArrayList<DeviceType>>() {
        }.getType());
        DeviceType obj_all = new DeviceType();
        obj_all.setID(0);
        obj_all.setName("All Devices");
        obj_all.setSelected(true);
        _list_device_type.add(0, obj_all);

        String _json = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        AllDataResponseModel main_object = gson.fromJson(_json, AllDataResponseModel.class);

        _lst_device_type = new ArrayList<>();
        _lst_device_type.add(0, obj_all);
        for (int i = 0; i < _list_device_type.size(); i++) {
            int type = _list_device_type.get(i).getID();
            for (int j = 0; j < main_object.getObjData().size(); j++) {
                if (type == main_object.getObjData().get(j).getDtype()) {
                    _lst_device_type.add(_list_device_type.get(i));
                    break;
                }
            }
        }

        rv_schedule_list = findViewById(R.id.rv_schedule_list);
        rv_schedule_list.setLayoutManager(new LinearLayoutManager(this));

        rl_schedule_list = findViewById(R.id.rl_schedule_list);

        btn_new_schedule = findViewById(R.id.btn_add);

        if (getIntent().hasExtra("isFromSchedul")) {
            bottomNavigationBar.selectTab(5, true);
            bottomNavigationBar.setFirstSelectedPosition(5);
            rl_schedule_list.setVisibility(View.VISIBLE);
            btn_new_schedule.setVisibility(View.VISIBLE);
        }

        btn_new_schedule.setOnClickListener(v -> {


            if (schedule_data != null && schedule_data.get_lst_schedule().size() >= 4) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(rgb_controls.this, android.R.style.Theme_Material_Dialog_Alert);
                dialog.setTitle("Limit exceeded");
                dialog.setMessage("You can add only 4 schedule, please delete existing schedule to add new schedule.");
                dialog.setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss());
                dialog.show();
                return;
            }

            Intent _intent = new Intent(rgb_controls.this, CreateSchedule.class);
            int sr;

            if (schedule_data != null) {
                if (schedule_data.get_lst_schedule() != null && schedule_data.get_lst_schedule().size() != 0) {
                    sr = schedule_data.get_lst_schedule().size() + 1;
                } else
                    sr = 1;
            } else
                sr = 1;


            String _list = gson.toJson(schedule_data);
            _intent.putExtra("_list", _list);
            _intent.putExtra("Dno", dno1);
            _intent.putExtra("SrNo", sr);
            startActivity(_intent);
        });

    }


    @Override
    public void selectedColor(int color) {

        colorpic = color;
        Constants.red = Color.red(color);
        Constants.green = Color.green(color);
        Constants.blue = Color.blue(color);

        alphaSlider.setColor(color);
        setRBG();

        Constants.white = 0;
        Constants.warm_white = 0;
        publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
    }

    @Override
    public void selectedScene(int position) {
//        Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();

        Constants.red = _mainList.get(position).getValue_r();
        Constants.green = _mainList.get(position).getValue_g();
        Constants.blue = _mainList.get(position).getValue_b();
        setRBG();

        Constants.white = 0;
        Constants.warm_white = 0;
        publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
    }

    boolean isChanged = true;

    public void rgbdialog(String jsonstring, Context context) {

//        dno1 = Constants.jsonObjectreader(jsonstring, dno);

        key1 = Constants.jsonObjectreader(jsonstring, key);

        type1 = Constants.jsonObjectreader(jsonstring, dtype);
//        String pname1 = Constants.jsonObjectreader(jsonstring,pname);
        // String enable1 = Constants.jsonObjectreader(jsonstring,enable);
//        String isOnline1 = Constants.jsonObjectreader(jsonstring,isOnline);
//        String room1 = Constants.jsonObjectreader(jsonstring,isOnline);

//        if (Constants.jsonObjectreader(jsonstring, "d1").contains(d1pname))
//            d1pname1 = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), d1pname);
        state = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "state");
        String red = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "r");
        String green = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "g");
        String blue = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "b");

        Constants.brightness = Math.round(Float.parseFloat(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "br")) * 100);
        Constants.warm_white = Integer.parseInt(Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "ww"));

        if (Constants.brightness == 0) {
            Constants.brightness = 100;
            utility.putInteger(StringConstants._brightness, 100);
        }

        Log.d(TAG, "rgbdialog:  v " + Constants.brightness);
        Constants.red = Integer.parseInt(red);
        Constants.green = Integer.parseInt(green);
        Constants.blue = Integer.parseInt(blue);

        //Constants.savetoShared(context).edit().putString(dno1,Constants.createPublishJson(dno1,key1,type1,d1pname1,room1)).apply();

        int colorId = android.graphics.Color.argb(255, Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
        final String[] dia_name = new String[1];

        picker.setColor(colorId);

       /* picker.setColor(colorId);
        colorpic = picker.getColor();
        picker.setOnColorChangedListener(null);*/

        onoff_colorpicker.setTag(Boolean.parseBoolean(state)); //Initialize value of Bulb i.e ON or OFF

        ImageView rbg_on_bulb = findViewById(R.id.rbg_on_bulb);
        ImageView rbg_off_bulb = findViewById(R.id.rbg_off_bulb);


        rbg_on_layout = findViewById(R.id.rbg_on_layout);
        rbg_off_layout = findViewById(R.id.rbg_off_layout);

        if (Boolean.parseBoolean(state)) {
            rbg_off_layout.setVisibility(View.INVISIBLE);
            rbg_on_layout.setVisibility(View.VISIBLE);
        } else {
            rbg_on_layout.setVisibility(View.INVISIBLE);
            rbg_off_layout.setVisibility(View.VISIBLE);
        }


      /*  picker.setOldCenterColor(colorId);
        picker.setShowOldCenterColor(false);*/


        warmwhite = findViewById(R.id.warmwhite);
        warmwhite.setEnabled(false);
        Log.d(TAG, "rgbdialog: " + Constants.brightness * 100);

        TextView brigtnessTV = findViewById(R.id.brightnessTV);
//        TextView warmwhiteTV = findViewById(R.id.warmwhiteTV);

        _brightness.setOnSeekBarChangeListener(null);
        warmwhite.setOnSeekBarChangeListener(null);

        Constants.brightness = utility.getInteger(StringConstants._brightness);
//        _brightness.setProgress(utility.getInteger(StringConstants._brightness));
        _brightness.setProgress(Constants.brightness);
        warmwhite.setProgress(Constants.warm_white);

        brigtnessTV.setText("Brightness " + _brightness.getProgress() + "%");
//        warmwhiteTV.setText("Warm White " + Constants.warm_white * 100 / 255 + "%");

        _brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    progress = 1;
                Constants.brightness = progress;
                brigtnessTV.setText("Brightness " + progress + "%");
                if (progress < 10) {
                    _brightness.setProgress(10);
                }
                Log.d(TAG, "onProgressChanged: bb " + Constants.brightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Constants.red = 0;
//                Constants.green = 0;
//                Constants.blue = 0;
                Constants.white = 0;
                Constants.warm_white = 0;
                if (seekBar.getProgress() == 0) {
                    utility.putInteger(StringConstants._brightness, 10);
                } else
                    utility.putInteger(StringConstants._brightness, seekBar.getProgress());
                publishRBGcolor(context, (Boolean) onoff_colorpicker.getTag());
            }
        });


        warmwhite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //int per = (seekBar.getProgress()/255)*100;
                Log.d(TAG, "onProgressChanged: " + progress);
//                warmwhiteTV.setText("warmwhite " + decimalFormat.format(progress * 100 / 255) + "%");
//                Constants.warm_white = seekBar.getProgress();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                DtypeViews.publishRBGcolor(context,dno1,(Boolean)onoff_colorpicker.getTag());
            }
        });

        setRBG();
    }

    public void initDiy() {
        TextView diy1 = findViewById(R.id.diy1);
        TextView diy2 = findViewById(R.id.diy2);
        TextView diy3 = findViewById(R.id.diy3);
        TextView diy4 = findViewById(R.id.diy4);
        TextView diy5 = findViewById(R.id.diy5);

        Constants.savetoShared(this).edit().putString(dno1 + "diy1", getJsonStringWhite()).apply();
        diy2.setOnLongClickListener(this);
        diy3.setOnLongClickListener(this);
        diy4.setOnLongClickListener(this);
        diy5.setOnLongClickListener(this);

        diy1.setOnClickListener(this);
        diy2.setOnClickListener(this);
        diy3.setOnClickListener(this);
        diy4.setOnClickListener(this);
        diy5.setOnClickListener(this);

        diy1.setBackgroundColor(setColor(diy1));
        diy2.setBackgroundColor(setColor(diy2));
        diy3.setBackgroundColor(setColor(diy3));
        diy4.setBackgroundColor(setColor(diy4));
        diy5.setBackgroundColor(setColor(diy5));
    }

    public int setColor(View v) {
        ColorDrawable viewColor = (ColorDrawable) v.getBackground();
        int colorId = viewColor.getColor();
        int col = colorId;
        String jsonstring = Constants.savetoShared(rgb_controls.this).getString(dno1 + v.getTag(), "false");
        if (!jsonstring.equals("false")) {
            col = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "color"));
        } else {
            Constants.red = Color.red(col);
            Constants.green = Color.green(col);
            Constants.blue = Color.blue(col);
            /*Constants.brightness = brightness.getf;
            Constants.warm_white = 150;
            Constants.white = 100;*/
            colorpic = col;
            Constants.savetoShared(this).edit().putString(dno1 + v.getTag(), getJsonString()).apply();
        }
        return col;
    }

    @Override
    public boolean onLongClick(View v) {
        v.setBackgroundColor(colorpic);
        Constants.savetoShared(this).edit().putString(dno1 + v.getTag(), getJsonString()).apply();
        return false;
    }

    private String getJsonString() {
        JSONObject jsonObjectD1 = new JSONObject();
        try {
            // jsonObjectD1.put("state",state);
            jsonObjectD1.put("r", Constants.red);
            jsonObjectD1.put("g", Constants.green);
            jsonObjectD1.put("b", Constants.blue);
            jsonObjectD1.put("w", Constants.white);
            jsonObjectD1.put("ww", Constants.warm_white);
            jsonObjectD1.put("br", Constants.brightness);
            jsonObjectD1.put("color", colorpic);
        } catch (Exception e) {

        }
        return jsonObjectD1.toString();
    }

    private String getJsonStringWhite() {
        JSONObject jsonObjectD1 = new JSONObject();
        try {
            //jsonObjectD1.put("state",state);
            jsonObjectD1.put("r", 255);
            jsonObjectD1.put("g", 255);
            jsonObjectD1.put("b", 255);
            jsonObjectD1.put("w", 0);
            jsonObjectD1.put("ww", 255);
            jsonObjectD1.put("br", Constants.brightness);
            jsonObjectD1.put("color", 0);
        } catch (Exception e) {

        }
        return jsonObjectD1.toString();
    }

    @Override
    public void onClick(View v) {
        String jsonstring = Constants.savetoShared(rgb_controls.this).getString(dno1 + v.getTag(), "false");
        Log.d(TAG, "onClick: " + jsonstring);
        if (!jsonstring.equals("false")) {
            Log.e("TAGGG", "OnClick called in if ");
            // String state = Constants.jsonObjectreader(Constants.jsonObjectreader(jsonstring, "d1"), "state");
            Constants.red = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "r"));
            Constants.green = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "g"));
            Constants.blue = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "b"));
            Constants.brightness = Math.round(Float.parseFloat(Constants.jsonObjectreader(jsonstring, "br")));
            Constants.warm_white = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "ww"));

            Constants.white = 0;
            Constants.warm_white = 0;
            setRBG();
            picker.setColor(android.graphics.Color.argb(255, Constants.red, Constants.green, Constants.blue));
            publishRBGcolor(this, true);
        } else {
            Log.e("TAGGG", "OnClick called in elase ");
            ColorDrawable viewColor = (ColorDrawable) v.getBackground();
            int color = viewColor.getColor();
            //Log.d(TAG, "onClick: CI "+colorId);yu
            Log.d(TAG, "onColorSelected: ");
            colorpic = color;
            Constants.red = Color.red(color);
            Constants.green = Color.green(color);
            Constants.blue = Color.blue(color);
            Constants.white = 0;
            Constants.warm_white = 0;
            alphaSlider.setColor(color);
            picker.setColor(color);
            setRBG();
            publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
        }
    }

    public void setRBG() {
        TextView r, b, g;
        r = findViewById(R.id.red);
        b = findViewById(R.id.blue);
        g = findViewById(R.id.green);

        r.setText("R " + String.valueOf(Constants.red));
        b.setText("B " + String.valueOf(Constants.blue));
        g.setText("G " + String.valueOf(Constants.green));

        r.setBackgroundColor(android.graphics.Color.argb(255, Constants.red, 0, 0));
        b.setBackgroundColor(android.graphics.Color.argb(255, 0, 0, Constants.blue));
        g.setBackgroundColor(android.graphics.Color.argb(255, 0, Constants.green, 0));
    }

    public void power(View view) {
        Log.d(TAG, "onOptionsItemSelected: " + onoff_colorpicker.getTag());
        if (!(Boolean) onoff_colorpicker.getTag()) {
            onoff_colorpicker.setTag(true);
            onoff_colorpicker.setText("ON");
            rbg_off_layout.setVisibility(View.INVISIBLE);
            rbg_on_layout.setVisibility(View.VISIBLE);
            publishRBGcolor(rgb_controls.this, true);
        } else {
            onoff_colorpicker.setTag(false);
            onoff_colorpicker.setText("OFF");
            rbg_on_layout.setVisibility(View.INVISIBLE);
            rbg_off_layout.setVisibility(View.VISIBLE);
            publishRBGcolor(rgb_controls.this, false);
        }
    }

    public void finish(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Added");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAGGG", "BroadCastReceiver Called ");
            updateUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //DtypeViews.getGetDevice(this);
        Log.d(TAG, "onResume: ");

        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
        getSchedules();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void updateUI(Intent intent) {
//        findViewById(R.id.progressBar).setVisibility(View.GONE);

        String Rdata = intent.getStringExtra("datafromService");
        Log.e("TAGGG", "Update UI called " + Rdata);

        udpdateJSON(rgb_controls.this, Rdata);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Rdata);
            if (jsonObject.has("data")) {

            } else if (jsonObject.has("duser")) {
                //DtypeViews.getGetDevice(this);
            } else if (jsonObject.has("room")) {
                //DtypeViews.getGetDevice(this);
            } else if (jsonObject.has("name")) {

            } else if (jsonObject.has("power")) {

            } else {
                //Update Views
                String type1 = Constants.jsonObjectreader(Rdata, "dtype");
                //   String dno1 = Constants.jsonObjectreader(Rdata, "dno");
                Log.d(TAG, "updateUI2: " + Rdata);
                switch (type1) {
                    case "1":
                        break;
                    case "2":
                    case "3":
                    case "4":
                        if (Rdata.contains(dno1))
                            rgbdialog(Rdata, this);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void warmWhite(View view) {
//        Constants.red = 255;
//        Constants.green = 255;
//        Constants.blue = 255;
//        Constants.brightness = 100;
//        Constants.warm_white = 255;
//        Constants.white = 100;

        Constants.red = 0;
        Constants.green = 0;
        Constants.blue = 0;

//        Constants.brightness = 100;
        Constants.warm_white = 255;
        Constants.white = 0;
        //picker.setColor(android.graphics.Color.argb(255,Constants.red,Constants.green,Constants.blue));
        publishRBGcolor(this, true);
    }

    public void white(View view) {
//        Constants.red = 255;
//        Constants.green = 255;
//        Constants.blue = 255;
//
//        Constants.brightness = 100;
//        Constants.warm_white = 255;
//        Constants.white = 100;
//

        Constants.red = 0;
        Constants.green = 0;
        Constants.blue = 0;

//        Constants.brightness = 100;
        Constants.warm_white = 0;
        Constants.white = 255;


        //picker.setColor(android.graphics.Color.argb(255,Constants.red,Constants.green,Constants.blue));
        publishRBGcolor(this, true);
    }

    public void colorTab() {

        Log.e("TAGGG", "Color Value at colorTab Red " + tempR + " Green " + tempG + " Blue " + tempB + " tempColor " + tempcolor);

        //Backup condtiotions
        if (tempR == 0 && tempG == 0 && tempB == 0) {
            colorpic = -7409665;
            Constants.red = Color.red(colorpic);
            Constants.green = Color.green(colorpic);
            Constants.blue = Color.blue(colorpic);
        } else {
            Constants.red = tempR;
            Constants.blue = tempB;
            Constants.green = tempG;
            colorpic = tempcolor;
        }
        picker.setColor(colorpic);
        Log.d(TAG, "onProgressChanged: assigned At " + Constants.red + " " + Constants.green + " " + Constants.blue + " " + tempcolor);
        setRBG();
        findViewById(R.id.rgblayout).setVisibility(View.VISIBLE);
        findViewById(R.id.whitelayout).setVisibility(View.GONE);

    }

    int tempR, tempG, tempB, tempcolor;

    public void whiteTab() {
        findViewById(R.id.rgblayout).setVisibility(View.GONE);
        findViewById(R.id.whitelayout).setVisibility(View.VISIBLE);
        SeekArc seekArc = findViewById(R.id.seekArcWhite);
//        tv_bright_perc.setText("30%");
        seekArc.setProgress(utility.getInteger(StringConstants._brightness));
        tv_bright_perc.setText(seekArc.getProgress() + "%");

        SeekBar seekbar_ww = (SeekBar) findViewById(R.id.seekbar_ww);
        seekbar_ww.setProgress(utility.getInteger(StringConstants._ww_seekbar));

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                Log.d(TAG, "onProgressChanged: " + i + " At " + Constants.red + " " + Constants.green + " " + Constants.blue);

                Constants.red = 0;
                Constants.green = 0;
                Constants.blue = 0;
                Constants.white = i;
                Constants.warm_white = seekbar_ww.getProgress();

                Double per = ((double) i / 100) * 100;
                Log.e("TAGG", "Percentage " + (Double) (per / 100));
                Constants.brightness = i;
                tv_bright_perc.setText((String.format("%.0f", per) + "%"));
//                seekArc.setProgress(i);
                if (i < 10) {
                    seekArc.setProgress(10);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                if (seekArc.getProgress() == 0)
                    utility.putInteger(StringConstants._brightness, 10);
                else
                    utility.putInteger(StringConstants._brightness, seekArc.getProgress());

                if (Constants.brightness > 10)
                    publishRBGcolor(rgb_controls.this, (Boolean) onoff_colorpicker.getTag());
                else
                    seekArc.setProgress(10);
            }
        });

        seekbar_ww.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("TAGGG", "Seekbar Change " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.e("TAGGG", "ONStop WW " + seekBar.getProgress() + " W " + (255 - seekBar.getProgress()));

                utility.putInteger(StringConstants._ww_seekbar, seekBar.getProgress());

                Constants.red = 0;
                Constants.green = 0;
                Constants.blue = 0;

//        Constants.brightness = 100;
//                Constants.warm_white = seekbar_ww.getProgress();
//                Constants.white = (255 - seekBar.getProgress());

                if (seekbar_ww.getProgress() == 70) {
                    Constants.warm_white = 255;
                    Constants.white = 0;
                } else if (seekbar_ww.getProgress() == 0) {
                    Constants.warm_white = 0;
                    Constants.white = 255;
                } else if (seekbar_ww.getProgress() == 35) {
                    Constants.warm_white = 127;
                    Constants.white = 128;
                } else {
                    Constants.warm_white = ((seekbar_ww.getProgress() * 255) / 100);
                    Constants.white = (255 - Constants.warm_white);
                }
                Log.e("TAGGG", "OnStop tracking WW " + Constants.warm_white + " White " + Constants.white);

                //picker.setColor(android.graphics.Color.argb(255,Constants.red,Constants.green,Constants.blue));
                publishRBGcolor(rgb_controls.this, true);
            }
        });


        /*final Slidr slidr = (Slidr) findViewById(R.id.slideure_regions);
        slidr.setMax(3000);
        slidr.setRegionTextFormatter(new Slidr.RegionTextFormatter() {
            @Override
            public String format(int region, float value) {
                return String.format("region %d : %d", region, (int) value);
            }
        });
        slidr.addStep(new Slidr.Step("test", 1500, Color.parseColor("#007E90"), Color.parseColor("#111111")));
*/
    }

    RecyclerView rv_color;
    ColorPickerAdapter _adapter;
    ArrayList<color_item> _lst_clr_item;
    FloatingActionButton iv_add_color;

    public final int REQUEST_EXTERNAL_STORAGE = 101;
    public final int REQUEST_FROM_CAMERA = 102;
    public static rgb_color_interface objInterface;

    void setupRecyclerView() {
        objInterface = this;
        rv_color = findViewById(R.id.rv_color_boxes);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 6);
        rv_color.setLayoutManager(mLayoutManager);

        _lst_clr_item = new ArrayList<>();

        color_item obj1 = new color_item();
        obj1.setColor_code(-1672077);
        obj1.setSelected(false);
        _lst_clr_item.add(obj1);

        color_item obj2 = new color_item();
        obj2.setColor_code(-2818048);
        obj2.setSelected(false);
        _lst_clr_item.add(obj2);

        color_item obj3 = new color_item();
        obj3.setColor_code(-765666);
        obj3.setSelected(false);
        _lst_clr_item.add(obj3);

        color_item obj4 = new color_item();
        obj4.setColor_code(-606426);
        obj4.setSelected(false);
        _lst_clr_item.add(obj4);

        color_item obj5 = new color_item();
        obj5.setColor_code(-16023485);
        obj5.setSelected(false);
        _lst_clr_item.add(obj5);

        color_item obj6 = new color_item();
        obj6.setColor_code(-13388167);
        obj6.setSelected(false);
        _lst_clr_item.add(obj6);

        color_item obj7 = new color_item();
        obj7.setColor_code(-7461718);
        obj7.setSelected(false);
        _lst_clr_item.add(obj7);

        color_item obj8 = new color_item();
        obj8.setColor_code(-8812853);
        obj8.setSelected(false);
        _lst_clr_item.add(obj8);

        color_item obj9 = new color_item();
        obj9.setColor_code(-12627531);
        obj9.setSelected(false);
        _lst_clr_item.add(obj9);

        color_item obj10 = new color_item();
        obj10.setColor_code(-16540699);
        obj10.setSelected(false);
        _lst_clr_item.add(obj10);

        color_item obj11 = new color_item();
        obj11.setColor_code(-10395295);
        obj11.setSelected(false);
        _lst_clr_item.add(obj11);


        _adapter = new ColorPickerAdapter(_lst_clr_item, rgb_controls.this, this);
        rv_color.setAdapter(_adapter);

        iv_add_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] colors = {"From Gallery", "Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(rgb_controls.this);
                builder.setTitle("Pick Image");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
//                        Toast.makeText(rgb_controls.this, which + "", Toast.LENGTH_LONG).show();
                        if (verifyStoragePermissions(which == 0 ? false : true)) {
                            if (which == 0) {
                                photoGalleryIntent();
                            } else {
                                photoCameraIntent();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    public boolean verifyStoragePermissions(boolean isFromCamera) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED || permission_camera != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!isFromCamera)
                    requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_EXTERNAL_STORAGE);
                else
                    requestPermissions(new String[]{"android.permission.CAMERA"}, REQUEST_FROM_CAMERA);
            }
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


    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    int CAMERA_REQUEST = 1;

    private void photoCameraIntent() {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } catch (Exception e) {
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Acrticv", "onActivityResult: ");

        if (resultCode == RESULT_OK && requestCode == EDIT_SCENE) {
            String id = data.getExtras().getString("Id");
            String newName = data.getExtras().getString("_new_name");
            String device = data.getExtras().getString("device");
            Gson gson = new Gson();


            ArrayList<String> anotherStr = gson.fromJson(device, new TypeToken<ArrayList<String>>() {
            }.getType());
            Log.e("TAGG", "Another json " + anotherStr);
            try {
                for (int i = 0; i < _mainList.size(); i++) {
                    if (id.equalsIgnoreCase(_mainList.get(i).getID())) {
                        _mainList.get(i).setScene_name(newName);
                        _mainList.get(i).setName(newName);
                        ArrayList<String> dev = new ArrayList<>();
                        dev.add(anotherStr.get(0));
                        _mainList.get(i).setDevices(dev);
                        _sceneAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at onResult " + e.getMessage());
            }
        } else if (resultCode == RESULT_OK && requestCode == 105) {
            if (data != null && data.hasExtra("refresh_list")) {
                getSceneFromServer();
            }
        } else if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d("Acrticv", "onActivityResult: fragment");
            Uri fileUri = data.getData();


            File myfile = new File(Utils.getPath(fileUri, rgb_controls.this));

            if (myfile.exists()) {
                showDialog(data.getData(), false, null);
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            showDialog(null, true, photo);
        }
    }

    com.skydoves.colorpickerview.ColorPickerView colorPickerView;

    public void showDialog(Uri imageUri, boolean fromcamera, Bitmap bitmap) {
        try {
            final Dialog dialog = new Dialog(rgb_controls.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.pick_color_from_image);


            AlphaTileView alphaTileView = dialog.findViewById(R.id.alphaTileView);
            colorPickerView = dialog.findViewById(R.id.colorPickerView);
            BubbleFlag bubbleFlag = new BubbleFlag(this, R.layout.layout_flag);
            bubbleFlag.setFlagMode(FlagMode.FADE);
            colorPickerView.setFlagView(bubbleFlag);


            if (fromcamera) {
                try {
                    Bitmap b = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    Drawable drawable = new BitmapDrawable(getResources(), b);
                    colorPickerView.setPaletteDrawable(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
                colorPickerView.setPaletteDrawable(drawable);
            }
            TextView dialogButton = dialog.findViewById(R.id.tv_add_color);
            dialogButton.setOnClickListener(v -> dialog.dismiss());

            colorPickerView.setColorListener(
                    new ColorEnvelopeListener() {
                        @Override
                        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                            alphaTileView.setPaintColor(envelope.getColor());
                        }
                    });

            // attach alphaSlideBar
            final AlphaSlideBar alphaSlideBar = dialog.findViewById(R.id.alphaSlideBar);
            colorPickerView.attachAlphaSlider(alphaSlideBar);

            // attach brightnessSlideBar
            final BrightnessSlideBar brightnessSlideBar = dialog.findViewById(R.id.brightnessSlide);
            colorPickerView.attachBrightnessSlider(brightnessSlideBar);
            colorPickerView.setLifecycleOwner(this);


            TextView tv_add = dialog.findViewById(R.id.tv_add_color);
            tv_add.setOnClickListener(view -> {
//                    Color.parseColor(colorPickerView.getColor());

                color_item _item = new color_item();
                _item.setSelected(false);
                _item.setColor_code(colorPickerView.getColor());
                _lst_clr_item.add(_item);
                _adapter.notifyItemInserted(_lst_clr_item.size());
                Toast.makeText(rgb_controls.this, "Color Added", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            });
            dialog.show();
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at showDialog " + e.getMessage(), e);
        }
    }


    @SuppressLint("SetTextI18n")
    private void setLayoutColor(ColorEnvelope envelope) {
        TextView textView = findViewById(R.id.textView);

    }

    void setupSceneList() {

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rv_scenelist.setLayoutManager(mLayoutManager);

        _mainList = new ArrayList<>();

        scene_model.Scene obj1 = new scene_model.Scene();
        obj1.setScene_name("Sunrise sunset");
        obj1.setDrawable(R.drawable.savana_sunset);
        obj1.setValue_r(255);
        obj1.setValue_g(160);
        obj1.setValue_b(75);
        obj1.setSelected(false);
        obj1.setStaticScene(true);

        scene_model.Scene obj2 = new scene_model.Scene();
        obj2.setScene_name("Candle Light");
        obj2.setDrawable(R.drawable.candle);
        obj2.setValue_r(255);
        obj2.setValue_g(149);
        obj2.setValue_b(44);
        obj2.setSelected(false);
        obj2.setStaticScene(true);

        scene_model.Scene obj3 = new scene_model.Scene();
        obj3.setScene_name("Moonlight");
        obj3.setDrawable(R.drawable.moonlight);
        obj3.setValue_r(255);
        obj3.setValue_g(250);
        obj3.setValue_b(235);
        obj3.setSelected(false);
        obj3.setStaticScene(true);

        scene_model.Scene obj4 = new scene_model.Scene();
        obj4.setScene_name("Match");
        obj4.setDrawable(R.drawable.match);
        obj4.setValue_r(255);
        obj4.setValue_g(140);
        obj4.setValue_b(39);
        obj4.setSelected(false);
        obj4.setStaticScene(true);


        scene_model.Scene obj5 = new scene_model.Scene();
        obj5.setScene_name("Electronic Flash");
        obj5.setDrawable(R.drawable.electr_flash);
        obj5.setValue_r(255);
        obj5.setValue_g(253);
        obj5.setValue_b(250);
        obj5.setSelected(false);
        obj5.setStaticScene(true);


        scene_model.Scene obj6 = new scene_model.Scene();
        obj6.setScene_name("Relax");
        obj6.setDrawable(R.drawable.relax);
        obj6.setValue_r(203);
        obj6.setValue_g(225);
        obj6.setValue_b(239);
        obj6.setSelected(false);
        obj6.setStaticScene(true);

        scene_model.Scene obj7 = new scene_model.Scene();
        obj7.setScene_name("Read");
        obj7.setDrawable(R.drawable.read);
        obj7.setValue_r(225);
        obj7.setValue_g(221);
        obj7.setValue_b(211);
        obj7.setSelected(false);
        obj7.setStaticScene(true);

        scene_model.Scene obj8 = new scene_model.Scene();
        obj8.setScene_name("Spring blossom");
        obj8.setDrawable(R.drawable.spring_blosom);
        obj8.setValue_r(225);
        obj8.setValue_g(183);
        obj8.setValue_b(197);
        obj8.setSelected(false);
        obj8.setStaticScene(true);

        scene_model.Scene obj9 = new scene_model.Scene();
        obj9.setScene_name("Night Light");
        obj9.setDrawable(R.drawable.nightlight);
        obj9.setValue_r(156);
        obj9.setValue_g(121);
        obj9.setValue_b(193);
        obj9.setSelected(false);
        obj9.setStaticScene(true);


        _mainList.add(obj1);
        _mainList.add(obj2);
        _mainList.add(obj3);
        _mainList.add(obj4);
        _mainList.add(obj5);
        _mainList.add(obj6);
        _mainList.add(obj7);
        _mainList.add(obj8);
        _mainList.add(obj9);

        _sceneAdapter = new SceneAdapter(_mainList, this, this, false);
        rv_scenelist.setAdapter(_sceneAdapter);
    }


    void getSceneFromServer() {
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        final String url = APIClient.BASE_URL + "/api/Get/getScene?UID=" + user;

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<scene_model> observable = apiInterface.getAllScene(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<scene_model>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(scene_model scene_models) {
                Log.e("TAGG", "OnNext  called " + scene_models.getLst_scene().size());
                try {
                    if (scene_models != null && scene_models.getLst_scene() != null) {
                        Log.e("TAGGG", "Size of scene Before " + scene_models.getLst_scene().size());
                        /*int total = _mainList.size();
                        for (int i = 0; i < total; i++) {
                            if (!_mainList.get(i).isStaticScene()) {
                                _mainList.remove(i);
                            }
                        }*/
                        Log.e("TAGGG", "Size of scene after " + scene_models.getLst_scene().size());
                        for (int i = 0; i < scene_models.getLst_scene().size(); i++) {
                            Log.e("TAGG", "Scene Name " + scene_models.getLst_scene().get(i).getName() + " Devices " + scene_models.getLst_scene().get(i).getDevices().get(0));
                            if (!isAdded(scene_models.getLst_scene().get(i).getID())) {
                                scene_models.getLst_scene().get(i).setStaticScene(false);
                                scene_models.getLst_scene().get(i).setScene_name(scene_models.getLst_scene().get(i).getName());
                                _mainList.add(scene_models.getLst_scene().get(i));
                                _sceneAdapter.notifyItemInserted(_mainList.size() - 1);
                            } else {
                                Log.e("TAGGG", "Already Added " + scene_models.getLst_scene().get(i).getName());
                            }
                        }
                        Log.e("TAGGG", "Size of scene after >>" + scene_models.getLst_scene().size());
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at onNext " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError  called " + e.toString(), e);
            }

            @Override
            public void onComplete() {
            }
        });
    }


    public boolean isAdded(String _id) {
        for (int i = 0; i < _mainList.size(); i++) {
            if (_mainList.get(i).getID().equalsIgnoreCase(_id)) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void triggerScene(String id) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/triggerScene?ID=" + id;
        Observable<SuccessResponse> observable = apiInterface.triggerScene(url);
        try {
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(SuccessResponse successResponse) {
                    Toast.makeText(rgb_controls.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAGG", "OnError " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at e " + e.getMessage(), e);
        }
    }

    @Override
    public void delScene(String id, int pos) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/delScene?ID=" + id;
        Observable<SuccessResponse> observable = apiInterface.delScene(url);
        try {
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(SuccessResponse successResponse) {
                    Toast.makeText(rgb_controls.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                    if (successResponse.getSuccess()) {
                        _mainList.remove(pos);
                        _sceneAdapter.notifyItemRemoved(pos);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAGG", "OnError " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at e " + e.getMessage(), e);
        }
    }


    public int getTotalScene() {
        int total = 0;
        for (int i = 0; i < _mainList.size(); i++) {
            if (!_mainList.get(i).isStaticScene()) {
                total = total + 1;
            }
        }
        return total;
    }


    @Override
    public void editScene(String id, int pos) {

        try {
            Intent _intent = new Intent(rgb_controls.this, CreateScene.class);
            _intent.putExtra("dno", dno1);
            _intent.putExtra("dtype", type1);
            Gson gson = new Gson();
            String data = gson.toJson(_mainList.get(pos));
            _intent.putExtra("data", data);
            _intent.putExtra("id", id);
            _intent.putExtra("name", _mainList.get(pos).getScene_name());
            _intent.putExtra("FromEdit", "true");
            String list_all = gson.toJson(_mainList);
            _intent.putExtra("list", list_all);
            startActivityForResult(_intent, EDIT_SCENE);

        } catch (Exception e) {

        }
    }


    void setColor(int progress) {

        int redValue, greenValue, blueValue;

        Log.e("TAGGG", "Progress " + progress);
        if (progress <= 18) {
            redValue = 254;
            greenValue = 242;
            blueValue = 54;
        } else if (progress == 19) {
            redValue = 254;
            greenValue = 244;
            blueValue = 81;
        } else if (progress == 20) {
            redValue = 254;
            greenValue = 247;
            blueValue = 99;
        } else if (progress == 21) {
            redValue = 254;
            greenValue = 249;
            blueValue = 112;
        } else if (progress == 22) {
            redValue = 254;
            greenValue = 251;
            blueValue = 123;
        } else if (progress == 23) {
            redValue = 254;
            greenValue = 253;
            blueValue = 132;
        } else if (progress == 24) {
            redValue = 254;
            greenValue = 254;
            blueValue = 139;
        } else if (progress == 25) {
            redValue = 252;
            greenValue = 254;
            blueValue = 145;
        } else if (progress == 26) {
            redValue = 251;
            greenValue = 254;
            blueValue = 149;
        } else if (progress == 27) {
            redValue = 250;
            greenValue = 254;
            blueValue = 153;
        } else if (progress == 28) {
            redValue = 248;
            greenValue = 254;
            blueValue = 157;
        } else if (progress == 29) {
            redValue = 247;
            greenValue = 254;
            blueValue = 161;
        } else if (progress == 30) {
            redValue = 246;
            greenValue = 254;
            blueValue = 164;
        } else if (progress == 31) {
            redValue = 245;
            greenValue = 254;
            blueValue = 167;
        } else if (progress == 32) {
            redValue = 244;
            greenValue = 254;
            blueValue = 169;
        } else if (progress == 33) {
            redValue = 244;
            greenValue = 254;
            blueValue = 172;
        } else if (progress == 34) {
            redValue = 243;
            greenValue = 254;
            blueValue = 174;
        } else if (progress == 35) {
            redValue = 242;
            greenValue = 254;
            blueValue = 176;
        } else if (progress == 36) {
            redValue = 241;
            greenValue = 254;
            blueValue = 178;
        } else if (progress == 37) {
            redValue = 241;
            greenValue = 254;
            blueValue = 180;
        } else if (progress == 38) {
            redValue = 240;
            greenValue = 253;
            blueValue = 182;
        } else if (progress == 39) {
            redValue = 240;
            greenValue = 253;
            blueValue = 184;
        } else if (progress == 40) {
            redValue = 239;
            greenValue = 253;
            blueValue = 185;
        } else if (progress == 41) {
            redValue = 239;
            greenValue = 253;
            blueValue = 187;
        } else if (progress == 42) {
            redValue = 238;
            greenValue = 253;
            blueValue = 188;
        } else if (progress == 43) {
            redValue = 238;
            greenValue = 253;
            blueValue = 190;
        } else if (progress == 44) {
            redValue = 237;
            greenValue = 253;
            blueValue = 191;
        } else if (progress == 45) {
            redValue = 237;
            greenValue = 253;
            blueValue = 192;
        } else if (progress == 46) {
            redValue = 237;
            greenValue = 253;
            blueValue = 193;
        } else if (progress == 47) {
            redValue = 236;
            greenValue = 253;
            blueValue = 194;
        } else if (progress == 48) {
            redValue = 236;
            greenValue = 253;
            blueValue = 195;
        } else if (progress == 49) {
            redValue = 235;
            greenValue = 253;
            blueValue = 197;
        } else if (progress == 50) {
            redValue = 235;
            greenValue = 253;
            blueValue = 198;
        } else if (progress >= 51 && progress <= 60) {
            redValue = 233;
            greenValue = 254;
            blueValue = 204;
        } else if (progress >= 61 && progress <= 70) {
            redValue = 232;
            greenValue = 254;
            blueValue = 209;
        } else if (progress >= 71 && progress <= 80) {
            redValue = 231;
            greenValue = 254;
            blueValue = 213;
        } else if (progress >= 81 && progress <= 90) {
            redValue = 230;
            greenValue = 254;
            blueValue = 217;
        } else {
            redValue = 229;
            greenValue = 254;
            blueValue = 220;
        }


        Constants.white = 0;
        Constants.warm_white = 0;

        Constants.red = redValue;
        Constants.green = greenValue;
        Constants.blue = blueValue;

        picker.setColor(Color.rgb(redValue, greenValue, blueValue));
        setRBG();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getSchedules() {
        try {
            Log.e("TAGG", "getSchedule Called");
            JSONObject _obj = new JSONObject();
            _obj.put("getSch", 1);
            String user = Constants.savetoShared(rgb_controls.this).getString(Constants.USER_ID, "0");
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    _obj.toString(),
                    1,
                    "d/" + dno1 + "/sub");
        } catch (Exception e) {
            Log.e("TAGG", "Exception at getSchedule " + e.getMessage(), e);
        }
    }

    @Override
    public void getSchedules(String scheduleData) {
//        Toast.makeText(this, scheduleData + "", Toast.LENGTH_SHORT).show();
        fillScheduleData(scheduleData);
    }


    Gson _gson = new Gson();
    ScheduleAdapter _schedule_adapter;
    schedule_model schedule_data;

    void fillScheduleData(String _scheduleData) {
        try {
            schedule_data = _gson.fromJson(_scheduleData, schedule_model.class);
            _schedule_adapter = new ScheduleAdapter(rgb_controls.this, schedule_data, this, null);
            rv_schedule_list.setAdapter(_schedule_adapter);

            latestSchedule(schedule_data.get_lst_schedule());
        } catch (Exception e) {
            Log.e("TAGG", "Exception at fill schedule " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteSchedule(int pos) {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new android.app.AlertDialog.Builder(this);
        }
        builder.setTitle("Remove Schedule")
                .setMessage("Are you sure you want to Remove this schedule ?")
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        schedule_data.get_lst_schedule().remove(pos);
                        String newList = _gson.toJson(schedule_data);
                        postSchedules(newList);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void postSchedules(String infos) {
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + dno1 + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void editSchedule(int pos) {

        Intent _intent = new Intent(rgb_controls.this, CreateSchedule.class);
        Gson gson = new Gson();
        String _list = gson.toJson(schedule_data);
        _intent.putExtra("_list", _list);
        _intent.putExtra("Dno", dno1);
        _intent.putExtra("SrNo", pos);
        _intent.putExtra("isFromEditSchedule", true);
        startActivity(_intent);
    }

    @Override
    public void updateSchedule(int pos) {
        new Handler().postDelayed(() -> {
            schedule_data.get_lst_schedule().get(pos).setState(!schedule_data.get_lst_schedule().get(pos).isState());
            String _data = _gson.toJson(schedule_data);
            postSchedules(_data);
        }, 1000);
    }

    public void publishRBGcolor(Context context, Boolean state) {
        String user = Constants.savetoShared(context).getString(Constants.USER_ID, "0");
        Log.e(TAG, "publishRBGcolor: " + jsonString);
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


            jsonObjectMain.put("dno", dno1);
            jsonObjectMain.put("key", key1);
            jsonObjectMain.put("dtype", Constants.jsonObjectreader(jsonString, dtype));
            jsonObjectMain.put("d1", jsonObjectD1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "publishRBGcolor: " + jsonObjectMain.toString());
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObjectMain.toString(),
                    1,
                    "d/" + dno1 + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        objInterface = null;
    }

    void latestSchedule(ArrayList<schedule_model.schedule> _lst) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dt = df.format(c.getTime());
        // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
//            schedule_model _model_convert = _gson.fromJson(_object, schedule_model.class);
            Log.e("TAG", "Schedule size " + _lst.size());
            ArrayList<_model_next_schedule> _lst_dates = new ArrayList<>();
            for (int j = 0; j < _lst.size(); j++) {
                if (_lst.get(j).getRepeat() == 1) {
                    c.setTime(sdf.parse(dt));

                    /*int k = (_model_convert.get_lst_schedule().get(j).getDays().indexOf("1")) + 1;
                    int day = c.get(Calendar.DAY_OF_WEEK) - 1;
*/
                    int selectedDay = (_lst.get(j).getDays().indexOf("1")) + 1;
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

                    int diff = ((7 - dayOfWeek) + selectedDay);
                    c.add(Calendar.DATE, diff);
                    Log.e("TAG", "Both Day dayOfWeek " + dayOfWeek + " selectedDay " + selectedDay);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                    String output = sdf1.format(c.getTime());
                    Log.e("TAG", "Both Day dayOfWeek Date " + output);
                    _model_next_schedule obj = new _model_next_schedule(output, _lst.get(j).getTime_H() + ":" + _lst.get(j).getTime_M(), (_lst.get(j).getRepeat()) + "");
                    _lst_dates.add(obj);

                } else {
                    _model_next_schedule obj = new _model_next_schedule(_lst.get(j).getDate(), _lst.get(j).getTime_H() + ":" + _lst.get(j).getTime_M(), (_lst.get(j).getRepeat()) + "");
                    _lst_dates.add(obj);
                    Log.e("TAG", "Schedule Added in list " + _lst.get(j).getDate());
                }
            }

            for (_model_next_schedule _object : _lst_dates) {
                Log.e("TAG", "From List Before sort <> " + _object.get_date());
            }

            Collections.sort(_lst_dates, new Comparator<_model_next_schedule>() {
                public int compare(_model_next_schedule o1, _model_next_schedule o2) {
                    return o1.get_date().compareTo(o2.get_date());
                }
            });

            Log.e("TAG", "*************SORTING*****************");

            for (_model_next_schedule _object_schedule : _lst_dates) {
                Log.e("TAG", "From List After sort <> " + _object_schedule.get_date());
            }

            if (_lst_dates.size() == 0)
                Constants.savetoShared(rgb_controls.this).edit().putString(dno1 + "_1", "").apply();
            else {
//                Constants.savetoShared(ScheduleList.this).edit().putString(dno1 + "_" + type, (_lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
                savetoShared(rgb_controls.this).edit().putString(dno1 + "_1", (getResources().getString(R.string.work_next) + _lst_dates.get(0).get_date() + " " + (_lst_dates.get(0).get_type().equalsIgnoreCase("1") ? "" : _lst_dates.get(0).get_time()))).apply();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at add schedule " + e.getMessage());
        }
    }

    class _model_next_schedule {
        public String _date;
        public String _time;
        public String _type;

        public _model_next_schedule(String _date, String _time, String _type) {
            this._date = _date;
            this._time = _time;
            this._type = _type;
        }

        public String get_date() {
            return _date;
        }

        public void set_date(String _date) {
            this._date = _date;
        }

        public String get_time() {
            return _time;
        }

        public void set_time(String _time) {
            this._time = _time;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }
    }
}
