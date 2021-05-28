package com.wekex.apps.homeautomation.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.triggertrap.seekarc.SeekArc;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.GradienSeekBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateScene extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    RelativeLayout rl_rgb, rl_white;
    ColorPicker picker;

    private int colorpic;
    GradienSeekBar alphaSlider;
    SeekBar _brightness;


    private float[] currentColorHsv = new float[3];
    View view;

    int red = 0;
    int green = 0;
    int blue = 0;
    int white = 0;
    int warm_white = 0;

    TextView tv_bright_perc;
    TextView brigtnessTV;
    SeekArc seekArc;
    SeekBar seekbar_ww;

    EditText edt_scene_name;
    TextView tv_add;
    String sceneUrl, UID;

    String dno, dtype;

    int brightNess;

    boolean isFromEdit = false;
    String edit_deviceID = "";
    String UID_edit = "";
    String jsonData = "";
    String s_name_edit;

    ArrayList<scene_model.Scene> baseList;

    SeekBar seekbar_clr_picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_scene);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Setup Scene");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        edt_scene_name = findViewById(R.id.edt_scene_name);
        seekArc = findViewById(R.id.seekArcWhite);
        tv_add = findViewById(R.id.tv_add_scene);
        brigtnessTV = findViewById(R.id.brightnessTV);
        rl_white = findViewById(R.id.rl_whitelayout);
        rl_rgb = findViewById(R.id.rl_rgblayout);

        seekbar_ww = findViewById(R.id.seekbar_ww);
        tv_bright_perc = findViewById(R.id.tv_bright_perc);
        seekArc.setProgress(50);

        _brightness = findViewById(R.id.brightness);
        _brightness.incrementProgressBy(1);
        _brightness.setMax(100);
        _brightness.setProgress(50);

        picker = findViewById(R.id.picker);
        picker.setShowOldCenterColor(false);
//        picker.addOnColorSelectedListener(null);
//        picker.addOnColorChangedListener(null);

        alphaSlider = findViewById(R.id.alphaSlider);
        alphaSlider.setColor(colorpic);

        int type = getIntent().getIntExtra("type", 0);
        dno = getIntent().getStringExtra("dno");
        dtype = getIntent().getStringExtra("dtype");

        if (getIntent().hasExtra("FromEdit")) {
            isFromEdit = true;
            jsonData = getIntent().getStringExtra("data");
            Log.e("TAGG", "Data ID " + edit_deviceID + " jsonData " + jsonData + " sname " + s_name_edit);
            Gson gson = new Gson();
            scene_model.Scene obj = gson.fromJson(jsonData, scene_model.Scene.class);
            s_name_edit = obj.getScene_name();
            if (obj.getScene_name() == null || obj.getScene_name().isEmpty())
                edt_scene_name.setText(obj.getName());
            else
                edt_scene_name.setText(obj.getScene_name());
            edit_deviceID = obj.getID();
            UID_edit = obj.getUID();
            tv_add.setText("Edit");

            String device = obj.getDevices().get(0);
            try {
                JSONObject _object = new JSONObject(device);
                JSONObject _objectd1 = _object.getJSONObject("d1");
                red = (Integer.parseInt(_objectd1.getString("r")));
                green = (Integer.parseInt(_objectd1.getString("g")));
                blue = (Integer.parseInt(_objectd1.getString("b")));
                warm_white = (Integer.parseInt(_objectd1.getString("ww")));
                white = (Integer.parseInt(_objectd1.getString("w")));

                Log.e("TAGGG", "colores RED " + red + " Green " + green + " Blue " + blue + " ww " + warm_white + " w " + white + " brightness " + brightNess);

                if (red > 0 || green > 0 || blue > 0) {
                    rl_white.setVisibility(View.GONE);
                    rl_rgb.setVisibility(View.VISIBLE);
                } else {
                    rl_white.setVisibility(View.VISIBLE);
                    rl_rgb.setVisibility(View.GONE);
                }
                double d = (Double.parseDouble(_objectd1.getString("br")) * 100);
                brightNess = (int) d;
                _brightness.setProgress(brightNess);
                brigtnessTV.setText("Brightness " + brightNess + "%");
                tv_bright_perc.setText(brightNess + "%");

                colorpic = Color.rgb(red, green, blue);
                picker.setColor(colorpic);

            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage(), e);
            }
        } else {
            Log.e("TAGGG", "Create Scenee Type " + type + " dno " + dno);
            if (type == 1) {
                seekbar_ww.setProgress(50);
                warm_white = seekbar_ww.getProgress();
                white = (255 - 50);
            } else if (type == 0) {
                colorpic = -7409665;
                red = Color.red(colorpic);
                green = Color.green(colorpic);
                blue = Color.blue(colorpic);
            }

            if (type == 0) {
                rl_white.setVisibility(View.GONE);
                rl_rgb.setVisibility(View.VISIBLE);
            } else {
                rl_white.setVisibility(View.VISIBLE);
                rl_rgb.setVisibility(View.GONE);
            }
            brigtnessTV.setText("Brightness 50%");
            brightNess = 50;
            tv_bright_perc.setText(50 + "%");
        }

        if (getIntent().hasExtra("list")) {
            String lst = getIntent().getStringExtra("list");
            Gson gson = new Gson();

            baseList = gson.fromJson(lst, new TypeToken<ArrayList<scene_model.Scene>>() {
            }.getType());
        }


        sceneUrl = APIClient.BASE_URL + "/api/Get/AddScene";
        UID = Constants.savetoShared(this).getString(Constants.USER_ID, "NA");

        setRBG();
        initDiy();
        setRBG();

        picker.setOnColorSelectedListener(color -> {
            Log.e("TAG", "onColorSelected: color " + color);
            colorpic = color;
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            alphaSlider.setColor(color);
            setRBG();

            white = 0;
            warm_white = 0;
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        alphaSlider.setOnALphaChangeListener(new GradienSeekBar.OnAlphaChangeListener() {
            @Override
            public void onAlphaColorChnage(int color) {
                Log.e("TAGGG", "onAlphaColorChnage called");
                Log.e("TAG", "r " + Color.red(color) + " g " + Color.green(color) + " b " + Color.blue(color) + "onAlphaColorChnage: " + color);
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);

                warm_white = 0;
                white = 0;
                setRBG();
            }

            @Override
            public void onAlphaColorChnaged(boolean alpha) {
//                DtypeViews.publishRBGcolor(rgb_controls.this, dno1, (Boolean) onoff_colorpicker.getTag());
            }
        });
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT);

        view = findViewById(R.id.view_1);
//        params.setMargins(20, 0, 20, 0);


        _brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    progress = 1;
                brightNess = progress;

                brigtnessTV.setText("Brightness " + progress + "%");

                if (progress < 10) {
                    _brightness.setProgress(10);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                white = 0;
                warm_white = 0;
                brightNess = seekBar.getProgress();
            }
        });

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                Log.e("TAG", "onProgressChanged: " + i + " At " + Constants.red + " " + Constants.green + " " + Constants.blue);
                red = 0;
                green = 0;
                blue = 0;
                white = i;
                warm_white = seekbar_ww.getProgress();

                Double per = ((double) i / 100) * 100;
                Log.e("TAGG", "Percentage " + (Double) (per / 100));
                brightNess = i;
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

                brightNess = seekArc.getProgress();
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
                red = 0;
                green = 0;
                blue = 0;
//        Constants.brightness = 100;
                warm_white = seekbar_ww.getProgress();
                white = (255 - seekBar.getProgress());

            }
        });


        tv_add.setOnClickListener(view -> {

            if (edt_scene_name.getText().toString().isEmpty()) {
                edt_scene_name.setError("Required!");
                return;
            }

            if (isFromEdit) {
                boolean isExist = false;
                if (!edt_scene_name.getText().toString().trim().equalsIgnoreCase(s_name_edit))
                    for (int i = 0; i < baseList.size(); i++) {
                        if (edt_scene_name.getText().toString().trim().equalsIgnoreCase(baseList.get(i).getScene_name() != null ? baseList.get(i).getScene_name() : baseList.get(i).getName())) {
                            isExist = true;
                            edt_scene_name.setError("Name Exist!");
                            return;
                        }
                    }
                editScene(getJsonObjectForEdit());
            } else {
                if (isNameExist(edt_scene_name.getText().toString())) {
                    edt_scene_name.setError("Name Exist!");
                } else
                    addnewScene(getJsonObject());
            }
        });

        seekbar_clr_picker = (SeekBar) findViewById(R.id.seekbar_clr_picker);
        seekbar_clr_picker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setColorToPicker(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    boolean isNameExist(String enteredName) {
//        if (baseList == null)
//            return false;
        try {
            boolean isExist = false;
            for (int i = 0; i < baseList.size(); i++) {
                if (baseList.get(i).getScene_name() == null) {
                    if (enteredName.equalsIgnoreCase(baseList.get(i).getName()))
                        return true;
                }
                if (enteredName.equalsIgnoreCase(baseList.get(i).getScene_name())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRBG() {
        TextView r, b, g;
        r = findViewById(R.id.red);
        b = findViewById(R.id.blue);
        g = findViewById(R.id.green);

        r.setText("R " + red);
        b.setText("B " + blue);
        g.setText("G " + green);

        r.setBackgroundColor(android.graphics.Color.argb(255, red, 0, 0));
        b.setBackgroundColor(android.graphics.Color.argb(255, 0, 0, blue));
        g.setBackgroundColor(android.graphics.Color.argb(255, 0, green, 0));
    }

    public void initDiy() {
        TextView diy1 = findViewById(R.id.diy1);
        TextView diy2 = findViewById(R.id.diy2);
        TextView diy3 = findViewById(R.id.diy3);
        TextView diy4 = findViewById(R.id.diy4);
        TextView diy5 = findViewById(R.id.diy5);

        /*Constants.savetoShared(this).edit().putString(dno1 + "diy1", getJsonStringWhite()).apply();
        diy2.setOnLongClickListener(this);
        diy3.setOnLongClickListener(this);
        diy4.setOnLongClickListener(this);
        diy5.setOnLongClickListener(this);*/

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


    @Override
    public void onClick(View v) {
        ColorDrawable viewColor = (ColorDrawable) v.getBackground();
        int color = viewColor.getColor();
        //Log.d(TAG, "onClick: CI "+colorId);yu
        Log.e("TAG", "onColorSelected: ");
        colorpic = color;
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
        white = 0;
        warm_white = 0;
        alphaSlider.setColor(color);
        picker.setColor(color);
        setRBG();
    }

    public int setColor(View v) {
        ColorDrawable viewColor = (ColorDrawable) v.getBackground();
        int colorId = viewColor.getColor();
        int col = colorId;
//        String jsonstring = Constants.savetoShared(CreateScene.this).getString(dno1 + v.getTag(), "false");
//        if (!jsonstring.equals("false")) {
//            col = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "color"));
//        } else {
//            Constants.red = Color.red(col);
//            Constants.green = Color.green(col);
//            Constants.blue = Color.blue(col);
//            /*Constants.brightness = brightness.getf;
//            Constants.warm_white = 150;
//            Constants.white = 100;*/
//            colorpic = col;
//            Constants.savetoShared(this).edit().putString(dno1 + v.getTag(), getJsonString()).apply();
//        }
        return col;
    }


    public int get_width(View v) {
        checkLayoutParams(v);
        return ((ViewGroup.LayoutParams) v.getLayoutParams()).width;
    }

    private static void checkLayoutParams(View v) {
        if (v.getLayoutParams() == null)
            v.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
    }


    private float getHue() {
        return currentColorHsv[0];
    }


    public String getJson() {
        String _jsonData = "";
        try {
            JSONObject _main = new JSONObject();
            _main.put("UID", UID);
            _main.put("Name", edt_scene_name.getText().toString().trim());
//            _main.put("state", true);

            JSONArray _array_device = new JSONArray();
            JSONObject _objectDevice = new JSONObject();

            _objectDevice.put("dno", dno);

            JSONObject obj_d1 = new JSONObject();

            obj_d1.put("r", red);
            obj_d1.put("g", green);
            obj_d1.put("b", blue);
//            obj_d1.put("state", true);
            obj_d1.put("state", true);
            obj_d1.put("br", (double) brightNess / 100);
            _objectDevice.put("d1", obj_d1);

            _array_device.put(_objectDevice.toString());
            _main.put("Devices", _array_device);
            Log.e("TAGGG", "Generated String " + _main.toString());
        } catch (Exception e) {
            Log.e("TAGG", "EXception at generate gson " + e.getMessage(), e);
        }
        return _jsonData;
    }

    private void addnewScene(JsonObject jsonObject) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<SuccessResponse> observable = apiInterface.addNewScene(jsonObject);
        Log.wtf(">>>>>>>>>>ADD_SCENE*************", jsonObject.toString());
        tv_add.setText("Adding...");
        tv_add.setTextColor(getResources().getColor(android.R.color.darker_gray));
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {

            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(SuccessResponse successResponse) {
                tv_add.setText("Add");
                tv_add.setTextColor(getResources().getColor(android.R.color.white));
                if (successResponse != null) {
                    if (successResponse.getSuccess()) {
//                        edt_scene_name.setText("");
                        Intent _intent = new Intent();
                        _intent.putExtra("refresh_list", "true");
                        setResult(RESULT_OK, _intent);
                        finish();
                        Toast.makeText(CreateScene.this, "Scene Added Successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CreateScene.this, successResponse.getSuccess() + "", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                tv_add.setText("Add");
                tv_add.setTextColor(getResources().getColor(android.R.color.white));
                Toast.makeText(CreateScene.this, "Error ", Toast.LENGTH_LONG).show();
                Log.e("TAGG", "Error " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public JsonObject getJsonObject() {
        String _jsonData = "";
        JsonObject _main = null;
        try {
            _main = new JsonObject();
            _main.addProperty("UID", UID);
            _main.addProperty("Name", edt_scene_name.getText().toString().trim());

            JsonArray _array_device = new JsonArray();
            JsonObject _objectDevice = new JsonObject();

            _objectDevice.addProperty("dno", dno);

            JsonObject obj_d1 = new JsonObject();

            obj_d1.addProperty("r", red);
            obj_d1.addProperty("g", green);
            obj_d1.addProperty("b", blue);
            obj_d1.addProperty("w", white);
            obj_d1.addProperty("ww", warm_white);


//            obj_d1.addProperty("status", true);
            obj_d1.addProperty("state", true);
            obj_d1.addProperty("br", (double) brightNess / 100);

            _objectDevice.add("d1", obj_d1);

            _array_device.add(_objectDevice.toString());
            _main.add("Devices", _array_device);
            Log.e("TAGGG", "Generated String " + _main.toString());
        } catch (Exception e) {
            Log.e("TAGG", "Exception at generate gson " + e.getMessage(), e);
        }
        return _main;
    }

    public JsonObject getJsonObjectForEdit() {
        String _jsonData = "";
        JsonObject _main = null;
        try {
            _main = new JsonObject();
            _main.addProperty("UID", UID);
            _main.addProperty("ID", edit_deviceID);
            _main.addProperty("Name", edt_scene_name.getText().toString().trim());

            JsonArray _array_device = new JsonArray();
            JsonObject _objectDevice = new JsonObject();

            _objectDevice.addProperty("dno", dno);

            JsonObject obj_d1 = new JsonObject();

            obj_d1.addProperty("r", red);
            obj_d1.addProperty("g", green);
            obj_d1.addProperty("b", blue);
            obj_d1.addProperty("w", white);
            obj_d1.addProperty("ww", warm_white);


//            obj_d1.addProperty("status", true);
            obj_d1.addProperty("state", true);
            obj_d1.addProperty("br", (double) brightNess / 100);

            _objectDevice.add("d1", obj_d1);

            _array_device.add(_objectDevice.toString());
            _main.add("Devices", _array_device);
            Log.e("TAGGG", "Generated String " + _main.toString());
        } catch (Exception e) {
            Log.e("TAGG", "Exception at generate gson " + e.getMessage(), e);
        }
        return _main;
    }

    private void editScene(JsonObject jsonObject) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<SuccessResponse> observable = apiInterface.editScene(jsonObject);
        tv_add.setText("Editing...");
        tv_add.setTextColor(getResources().getColor(android.R.color.darker_gray));
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SuccessResponse successResponse) {
                tv_add.setText("Add");
                tv_add.setTextColor(getResources().getColor(android.R.color.white));
                if (successResponse != null) {
                    if (successResponse.getSuccess()) {
//                        edt_scene_name.setText("");
                        Intent _intent = new Intent();
                        _intent.putExtra("Id", edit_deviceID);
                        _intent.putExtra("_new_name", edt_scene_name.getText().toString().trim());
                        _intent.putExtra("edit", "true");
                        _intent.putExtra("device", jsonObject.get("Devices") + "");
                        setResult(RESULT_OK, _intent);
                        finish();
                        Toast.makeText(CreateScene.this, "Scene Added Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateScene.this, successResponse.getSuccess() + "", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                tv_add.setText("Add");
                tv_add.setTextColor(getResources().getColor(android.R.color.white));
                Toast.makeText(CreateScene.this, "Error ", Toast.LENGTH_LONG).show();
                Log.e("TAGG", "Error " + e.getMessage(), e);
            }

            @Override
            public void onComplete() { }
        });
    }

    void setColorToPicker(int progress) {

        int redValue = 0, greenValue = 0, blueValue = 0;

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

}
