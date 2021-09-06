package com.wekex.apps.homeautomation.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.triggertrap.seekarc.SeekArc;
import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.adapter.SelectedDeviceListAdapter;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.SceneListModel;
import com.wekex.apps.homeautomation.model.data;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.GradienSeekBar;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConfigueRuleDevices extends AppCompatActivity implements View.OnClickListener, RoomOperation {

    Toolbar toolbar;
    String scenee;
    String roomID = "";
    CardView ll_add_accesories;
    TextView tv_hint_accessories, tv_done;
    private ArrayList<SceneListModel> listItems = new ArrayList<>();
    private RecyclerView listView;
    LinearLayout ll_progressbar;
    Gson _gson = new Gson();
    JSONObject _sceneData = new JSONObject();
    boolean isFromUpdate = false;
    String sceneID = "";
    String _existingDevice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configue_room_devices);
        setupToolbar();
        this.scenee = getIntent().getStringExtra("Devices");
        Log.wtf("SCENEE_INTENT_EXTRA", this.scenee);
        ll_add_accesories = findViewById(R.id.ll_add_accesories);
        ll_add_accesories.setOnClickListener(this::onClick);

        listView = findViewById(R.id.rv_scene_list);
        listView.setLayoutManager(new GridLayoutManager(this, 1));

        ll_progressbar = findViewById(R.id.ll_progressbar);
        ll_progressbar.setVisibility(View.GONE);
        tv_done = findViewById(R.id.tv_done);
        tv_done.setOnClickListener(this::onClick);
        tv_hint_accessories = findViewById(R.id.tv_hint_accessories);
        if (getIntent() != null && getIntent().hasExtra("Devices")) {
            if (getIntent().getStringExtra("Devices").equalsIgnoreCase("new")) {
                roomID = getIntent().getStringExtra("roomID");

            } else if (getIntent().getStringExtra("Devices").equalsIgnoreCase("update") && getIntent().hasExtra("sceneJsonData")) {
                roomID = getIntent().getStringExtra("roomID");

                tv_done.setText(getString(R.string.update));
                isFromUpdate = true;
                try {
                    _sceneData = new JSONObject(getIntent().getStringExtra("sceneJsonData"));
                    sceneID = _sceneData.getString("ID");
                    if (_sceneData.has("Devices")) {
                        JSONArray _arr_devices = _sceneData.getJSONArray("Devices");
                        AllDataResponseModel _all_data_selected = new AllDataResponseModel();
                        for (int i = 0; i < _arr_devices.length(); i++) {
                            String objectData = _arr_devices.get(i).toString();
                            Log.e("TAG", "DNO From Array :: " + objectData);
                            data _objData = _gson.fromJson(objectData, data.class);
                            _objData.setDtype(getDeviceType(_objData.getDno()));
                            _objData.getObjd1().setName(getDeviceName(_objData.getDno()));
                            _objData.setName(getDeviceName(_objData.getDno()));
                            Log.e("TAG", "Object Name At Update::" + _objData.getName());
                            _all_data_selected.getObjData().add(_objData);
                            Log.e("TAG", "DNO From Array :: " + _objData.getDno() + " <> " + _objData.getDtype());
                        }
                        setUpList(_all_data_selected);
                        Log.e("TAG", "All Data Size selected " + _all_data_selected.getObjData().size());
                    }
                } catch (Exception e) {
                    Log.e("TAG", "exception at onCreate" + e.toString());
                }
            }
        }

        if (getIntent() != null && getIntent().hasExtra("_data")) {
            _existingDevice = getIntent().getStringExtra("_data");
        }
    }

    void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configure Rule");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_add_accesories: {
                Intent intent = new Intent(ConfigueRuleDevices.this, AddAccessories.class);
                intent.putExtra("roomID", roomID);
                if (_all_data != null && _all_data.getObjData() != null && _all_data.getObjData().size() != 0) {
                    String _json = _gson.toJson(_all_data);
                    intent.putExtra("Devices", _json);
                } else
                    intent.putExtra("Devices", "new");
                startActivityForResult(intent, 500);
            }
            break;
            case R.id.tv_done: {
                if (_all_data == null || _all_data.getObjData() == null || _all_data.getObjData().size() == 0) {
                    Toast.makeText(ConfigueRuleDevices.this, getString(R.string.add_access_msg), Toast.LENGTH_SHORT).show();
                } else {
                    String selectedJson = getJsonObjectForEdit().toString();
                    Intent _intent = new Intent();
                    _intent.putExtra("devices", selectedJson);
                    setResult(RESULT_OK, _intent);
                    finish();
                }
            }
            break;
        }
    }

    public JsonObject createScene() {
        JsonObject _main = null;
        try {
            String user = Constants.savetoShared(ConfigueRuleDevices.this).getString(Constants.USER_ID, "0");
            _main = new JsonObject();
            _main.addProperty("UID", user);
            JsonArray _array_device = new JsonArray();
            for (int i = 0; i < _device_adapter.getLatestData().getObjData().size(); i++) {
                JsonObject _objectDevice = new JsonObject();
                if (_device_adapter.getLatestData().getObjData().get(i).getObjd1() != null) {
                    String _data = _gson.toJson(_device_adapter.getLatestData().getObjData().get(i).getObjd1());
                    _objectDevice.add("d1", new JsonParser().parse(_data));
                } else if (_device_adapter.getLatestData().getObjData().get(i).getObjd2() != null) {
                    String _data = _gson.toJson(_device_adapter.getLatestData().getObjData().get(i).getObjd2());
                    _objectDevice.add("d2", new JsonParser().parse(_data));
                } else if (_device_adapter.getLatestData().getObjData().get(i).getObjd3() != null) {
                    String _data = _gson.toJson(_device_adapter.getLatestData().getObjData().get(i).getObjd3());
                    _objectDevice.add("d3", new JsonParser().parse(_data));
                } else if (_device_adapter.getLatestData().getObjData().get(i).getObjd4() != null) {
                    String _data = _gson.toJson(_device_adapter.getLatestData().getObjData().get(i).getObjd4());
                    _objectDevice.add("d4", new JsonParser().parse(_data));
                }
                _objectDevice.addProperty("dno", _device_adapter.getLatestData().getObjData().get(i).getDno());
                _array_device.add(_objectDevice.toString());
            }
            _main.add("Devices", _array_device);
            uploadScene(_main.toString());
            Log.e("TAGGG", "Generated String " + _main.toString());
        } catch (Exception e) {
            Log.e("TAGG", "Exception at generate gson " + e.getMessage(), e);
        }
        return _main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "OnActivity Result " + resultCode + " " + requestCode);
        if (resultCode == RESULT_OK && requestCode == 500) {
            String _data = data.getStringExtra("data");
            setUpList(_gson.fromJson(_data, AllDataResponseModel.class));
        }
    }

    SelectedDeviceListAdapter _device_adapter;
    AllDataResponseModel _all_data;

    private void setUpList(AllDataResponseModel data) {
        try {
            _all_data = data;
            Log.e("TAG", "Setup List Data " + _all_data + " <> " + listItems.size());
            if (_all_data != null && _all_data.getObjData().size() != 0) {
                listView.setVisibility(View.VISIBLE);
                _device_adapter = new SelectedDeviceListAdapter(_all_data, ConfigueRuleDevices.this, this);
                listView.setAdapter(_device_adapter);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at SetupList " + e.toString());
        }
    }

    public void uploadScene(String data) throws JSONException {
        ll_progressbar.setVisibility(View.VISIBLE);
        String sceneUrl = APIClient.BASE_URL + "/api/Get/AddScene";
        JSONObject jsonObject = new JSONObject(data);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.e("TAG", "Scene Data " + jsonObject + " \n " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sceneUrl,
                jsonObject, response -> {
            ll_progressbar.setVisibility(View.GONE);
            try {
                JSONObject json = new JSONObject(String.valueOf(response));
                Log.wtf(" responseee ", json.toString());

                if (json.has("success") && json.getBoolean("success")) {
                    ll_progressbar.setVisibility(View.GONE);
                    Toast.makeText(this, "Scene Added Successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ll_progressbar.setVisibility(View.GONE);
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    onPause();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void SelectGroup(int pos) {

    }

    @Override
    public void DeleteGroup(int pos) {
        //Used unused interface to delete device inside scene
        try {
            _all_data.getObjData().remove(pos);
            _device_adapter.notifyItemRemoved(pos);
        } catch (Exception e) {
            Log.e("TAG", "Exception at delete device " + e.toString());
        }
    }

    @Override
    public void RenameGroup(int pos) {

    }

    @Override
    public void triggerGroup(int pos, boolean state) {

    }

    @Override
    public void MoveTo(String roomId, String dno) {

    }

    @Override
    public void Scheduled(int pos) {

    }

    @Override
    public void selectType(int type) {

    }

    @Override
    public void click_device(int pos, int type) {
        if (type == 2) {
            try {
                showType2Dialog(pos);
                /*Gson gson = new Gson();
                String data = gson.toJson(_all_data.getObjData().get(pos));
                Log.e("TAGG", "Data for type 2 " + data);
                Intent intent = new Intent(AddNewScene.this, rgb_controls.class);
                intent.putExtra("jsonString", data);
                intent.putExtra("dno", _all_data.getObjData().get(pos).getDno());
                intent.putExtra("_name", _all_data.getObjData().get(pos).getObjd1().getName());
                startActivityForResult(intent, colorpickerActivity);*/
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at click " + e.getMessage());
            }
        }
    }

    @Override
    public void updateStatus(String deviceInfo) {

    }

    @Override
    public void RenameDevice(int pos, String type) {

    }

    @Override
    public void TurnOnOffDevice(int pos, String type, boolean state, String dno, String key, String dtype) {

    }

    @Override
    public AllDataResponseModel getAllData() {
        return null;
    }

    @Override
    public void scheduleDevice(int pos, int type) {

    }

    @Override
    public void editGroup(int pos) {

    }

    @Override
    public void publishSeekbar(int pos, int br) {

    }

    @Override
    public void publishSeekbarType16(int pos, double br) {

    }

    @Override
    public void view_logs(int pos, String logs) {

    }

    RelativeLayout rl_rgb, rl_white;
    ColorPicker picker;

    int colorpic;
    GradienSeekBar alphaSlider;
    SeekBar _brightness;

    int red = 0;
    int green = 0;
    int blue = 0;
    int white = 0;
    int warm_white = 0;

    TextView tv_bright_perc;
    TextView brigtnessTV;
    SeekArc seekArc;
    SeekBar seekbar_ww;

    int brightNess;
    SeekBar seekbar_clr_picker;
    Button btnDone;
    View dialogView;

    private void showType2Dialog(int pos) {
        LayoutInflater factory = LayoutInflater.from(this);

        dialogView = factory.inflate(R.layout.dialog_rgb_controls, null);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setView(dialogView);

        seekArc = dialogView.findViewById(R.id.seekArcWhite);
        brigtnessTV = dialogView.findViewById(R.id.brightnessTV);
        rl_white = dialogView.findViewById(R.id.rl_whitelayout);
        rl_rgb = dialogView.findViewById(R.id.rl_rgblayout);
        btnDone = dialogView.findViewById(R.id.btn_done_rgb_controls);

        seekbar_ww = dialogView.findViewById(R.id.seekbar_ww);
        tv_bright_perc = dialogView.findViewById(R.id.tv_bright_perc);
        //seekArc.setProgress(50);

        _brightness = dialogView.findViewById(R.id.brightness);
        _brightness.incrementProgressBy(1);
        _brightness.setProgress(10);
        _brightness.setMax(100);
        //_brightness.setProgress(50);

        picker = dialogView.findViewById(R.id.picker);
        picker.setShowOldCenterColor(false);

        alphaSlider = dialogView.findViewById(R.id.alphaSlider);
        alphaSlider.setColor(colorpic);

        btnDone.setOnClickListener(v -> {
            dialog.cancel();
            try {
                _all_data.getObjData().get(pos).getObjd1().setR(red);
                _all_data.getObjData().get(pos).getObjd1().setG(green);
                _all_data.getObjData().get(pos).getObjd1().setB(blue);
                _all_data.getObjData().get(pos).getObjd1().setW(white);
                _all_data.getObjData().get(pos).getObjd1().setWw(warm_white);
                _all_data.getObjData().get(pos).getObjd1().setB(brightNess);

                _device_adapter.notifyItemChanged(pos);

                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setB(blue);
                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setR(red);
                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setG(green);
                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setW(white);
                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setWw(warm_white);
                _device_adapter.getLatestData().getObjData().get(pos).getObjd1().setBr(brightNess);

                Log.e("TAG", "Latest Json " + _gson.toJson(_all_data));

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "Exception at add RGB " + e.getMessage());
            }
        });

        picker.setOnColorSelectedListener(color -> {
            Log.e("DIALOG", "onColorSelected: color " + color);
            colorpic = color;
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            alphaSlider.setColor(color);
            setRBG();

            white = 0;
            warm_white = 0;
        });

        alphaSlider.setOnALphaChangeListener(new GradienSeekBar.OnAlphaChangeListener() {
            @Override
            public void onAlphaColorChnage(int color) {
                Log.e("DIALOG", "onAlphaColorChnage called");
                Log.e("DIALOG", "r " + Color.red(color) + " g " + Color.green(color) + " b " + Color.blue(color) + "onAlphaColorChnage: " + color);
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);

                warm_white = 0;
                white = 0;
                setRBG();
            }

            @Override
            public void onAlphaColorChnaged(boolean alpha) {
            }
        });

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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

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
                Log.e("DIALOG", "onProgressChanged: " + i + " At " + Constants.red + " " + Constants.green + " " + Constants.blue);
                red = 0;
                green = 0;
                blue = 0;
                white = i;
                warm_white = seekbar_ww.getProgress();

                double per = ((double) i / 100) * 100;
                Log.e("DIALOG", "Percentage " + (Double) (per / 100));
                brightNess = i;
                tv_bright_perc.setText((String.format("%.0f", per) + "%"));
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
                Log.e("DIALOG", "Seekbar Change " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.e("DIALOG", "ONStop WW " + seekBar.getProgress() + " W " + (255 - seekBar.getProgress()));
                red = 0;
                green = 0;
                blue = 0;
                warm_white = seekbar_ww.getProgress();
                white = (255 - seekBar.getProgress());

            }
        });
        seekbar_clr_picker = dialogView.findViewById(R.id.seekbar_clr_picker);
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

        dialog.show();
    }

    public void setRBG() {
        TextView r, b, g;
        r = dialogView.findViewById(R.id.red);
        b = dialogView.findViewById(R.id.blue);
        g = dialogView.findViewById(R.id.green);

        r.setText("R " + red);
        b.setText("B " + blue);
        g.setText("G " + green);

        r.setBackgroundColor(android.graphics.Color.argb(255, red, 0, 0));
        b.setBackgroundColor(android.graphics.Color.argb(255, 0, 0, blue));
        g.setBackgroundColor(android.graphics.Color.argb(255, 0, green, 0));
    }

    public void setColorToPicker(int progress) {

        int redValue, greenValue, blueValue;
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

    public int getDeviceType(String dno) {
        try {
            AllDataResponseModel _all_data = _gson.fromJson(PreferencesHelper.getAllDevices(this), AllDataResponseModel.class);
            Log.e("TAG", "All Data Size " + _all_data.getObjData().size());
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (_all_data.getObjData().get(i).getDno().equalsIgnoreCase(dno))
                    return _all_data.getObjData().get(i).getDtype();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at getDeviceType " + e.toString());
        }
        return 0;
    }

    public String getDeviceName(String dno) {
        try {
            AllDataResponseModel _all_data = _gson.fromJson(PreferencesHelper.getAllDevices(this), AllDataResponseModel.class);
            Log.e("TAG", "All Data Size " + _all_data.getObjData().size());
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (_all_data.getObjData().get(i).getDno().equalsIgnoreCase(dno))
                    if (!_all_data.getObjData().get(i).getObjd1().getName().isEmpty())
                        return _all_data.getObjData().get(i).getObjd1().getName();
                    else
                        return _all_data.getObjData().get(i).getName();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at getDeviceType " + e.toString());
        }
        return "";
    }


    public JSONObject getJsonObjectForEdit() {
        JSONObject _main = null;
        try {
            _main = new JSONObject();
            JSONArray _array_device = new JSONArray();

            //Add Existing devices received from Add screen
            if (_existingDevice != null && !_existingDevice.isEmpty()) {
                JSONObject _object = new JSONObject(_existingDevice);
                JSONArray arr_device = _object.getJSONArray("Devices");

                for (int i = 0; i < arr_device.length(); i++) {
                    _array_device.put(new JSONObject(arr_device.getString(i)));
                }
            }

            Log.e("TAG", "Size of Device Json Before " + _array_device.length());
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                JSONObject _objectDevice = new JSONObject();
                _objectDevice.put("dno", _all_data.getObjData().get(i).getDno());
                JSONObject obj_d1 = new JSONObject();
                obj_d1.put("r", _all_data.getObjData().get(i).getObjd1().getR());
                obj_d1.put("g", _all_data.getObjData().get(i).getObjd1().getG());
                obj_d1.put("b", _all_data.getObjData().get(i).getObjd1().getB());
                obj_d1.put("w", _all_data.getObjData().get(i).getObjd1().getW());
                obj_d1.put("ww", _all_data.getObjData().get(i).getObjd1().getWw());

                obj_d1.put("state", _all_data.getObjData().get(i).getObjd1().isState());
                obj_d1.put("br", (double) _all_data.getObjData().get(i).getObjd1().getBr() / 100);
                if (_all_data.getObjData().get(i).getObjd1().getName() != null)
                    obj_d1.put("name", _all_data.getObjData().get(i).getObjd1().getName());
                else
                    obj_d1.put("name", _all_data.getObjData().get(i).getName());
                _objectDevice.put("d1", obj_d1);
                _array_device.put(_objectDevice.toString());
                _main.put("Devices", _array_device);
            }
            Log.e("TAG", "Size of Device Json After " + _array_device.length());

            Gson gson = new Gson();
            String bodyInStringFormat = gson.toJson(_main);
            Log.e("TAGGG", "Generated String " + bodyInStringFormat + " ");
        } catch (Exception e) {
            Log.e("TAGG", "Exception at generate gson " + e.getMessage(), e);
        }
        return _main;
    }
}