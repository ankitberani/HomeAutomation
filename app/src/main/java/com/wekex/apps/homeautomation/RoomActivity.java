package com.wekex.apps.homeautomation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wekex.apps.homeautomation.Activity.DeviceTyp15;
import com.wekex.apps.homeautomation.Activity.EditScene;
import com.wekex.apps.homeautomation.Activity.NewSceneTempletActivity;
import com.wekex.apps.homeautomation.Activity.RuleListActivity;
import com.wekex.apps.homeautomation.Activity.ScheduleList;
import com.wekex.apps.homeautomation.Activity.Type21Activity;
import com.wekex.apps.homeautomation.Activity.UserRemoteCatList;
import com.wekex.apps.homeautomation.Activity.ViewLogs;
import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.DeviceTypeAdapter;
import com.wekex.apps.homeautomation.adapter.GroupsAdapter;
import com.wekex.apps.homeautomation.adapter.RoomDeviceAdapter;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.DeviceType;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.data;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;
import com.wekex.apps.homeautomation.utils.GroupEditor;
import com.wekex.apps.homeautomation.utils.GroupMenu;
import com.wekex.apps.homeautomation.utils.group_rgb_controls;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import static com.wekex.apps.homeautomation.utils.Constants.USER_ID;
import static com.wekex.apps.homeautomation.utils.Constants.cardColorup;
import static com.wekex.apps.homeautomation.utils.Constants.colorpickerActivity;
import static com.wekex.apps.homeautomation.utils.Constants.getDeviceById;
import static com.wekex.apps.homeautomation.utils.Constants.jsonobjectSTringJSON;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;
import static com.wekex.apps.homeautomation.utils.DtypeViews.isOnline;

public class RoomActivity extends BaseActivity implements RoomOperation {

    private String Room_Name;
    TextView roomNameTV, no_of_device;
    String TAG = "RoomActivity";
    LinearLayout sceneHolder;
    String DeviceInfos;
    Dialog menuDialog;
    public int SCENE_INTENT = 1;
    RelativeLayout top;
    private int ADDSCENE = 5;
    private int ADDGROUP = 6;
    public static LinearLayout deviceTypeHolder;
    static String lastTab;

    BottomNavigationBar bottomNavigationBar;
    //    ScrollView scoll_device_holder;
    TextView tv_scene;

    TextView tv_no_scene, btnAddRule;
    ImageView refreshBtn;

    RecyclerView rv_group, rv_device_list, rv_device_type;
    public static RoomOperation _grp_interface;

    Gson gson = new Gson();
    ArrayList<DeviceType> _lst_device_type;

    scene_model _mainList_scene;
    Gson _gson = new Gson();

    SwipeRefreshLayout swipe_refresh_group;
    Utility _utility;
    ScrollView scroll_scene_holder;
    DeviceTypeAdapter _adapter_device_type;

    String room_Id = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        room_Id = getIntent().getStringExtra("room_id");
        Room_Name = getIntent().getStringExtra("room_name");
        deviceTypeHolder = findViewById(R.id.deviceTypeHolder);
        Constants.showDeviceRype = 0;

        Log.e("TAG", "");

        _utility = new Utility(RoomActivity.this);
        _grp_interface = this;
        tv_no_scene = findViewById(R.id.tv_no_scene);
        tv_no_scene.setVisibility(View.GONE);

        scroll_scene_holder = findViewById(R.id.scroll_scene_holder);
        scroll_scene_holder.setVisibility(View.GONE);

        swipe_refresh_group = findViewById(R.id.swipe_refresh_group);
        swipe_refresh_group.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        tv_scene = findViewById(R.id.tv_scene);
        tv_scene.setText(getResources().getString(R.string.devices));
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        btnAddRule = findViewById(R.id.btn_add_new_rule);
        btnAddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RoomActivity.this, RuleListActivity.class));
            }
        });


/*.addItem(new BottomNavigationItem(R.drawable.home_icon, "Home").setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))
                .addItem(new BottomNavigationItem(R.drawable.refresh, "Refresh").setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))
                */
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.device_icon_new, getResources().getString(R.string.devices)).setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))
                .addItem(new BottomNavigationItem(R.drawable.scene_icon_new, getResources().getString(R.string.scene)).setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))
                .addItem(new BottomNavigationItem(R.drawable.group_icon_new, getResources().getString(R.string.groups)).setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))
                .addItem(new BottomNavigationItem(R.drawable.document, "Rules").setActiveColorResource(R.color.colorPrimaryDark).setInActiveColor(R.color.gray600))

                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTag(0);

        roomNameTV = findViewById(R.id.roomName);
        no_of_device = findViewById(R.id.no_of_device);

        sceneHolder = findViewById(R.id.sceneHolder);
        refreshBtn = findViewById(R.id.iv_refresh);
        refreshBtn.setOnClickListener(v -> getDevices(true));

        roomNameTV.setText(Room_Name);
        rv_group = findViewById(R.id.rv_group);
        rv_group.setLayoutManager(new LinearLayoutManager(this));
        rv_device_list = findViewById(R.id.rv_device_list);
        rv_device_list.setVisibility(View.VISIBLE);

        rv_device_list.setLayoutManager(new LinearLayoutManager(this));
        rv_device_type = findViewById(R.id.rv_device_type);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_device_type.setLayoutManager(mLayoutManager);

        String deviceTypes = savetoShared(this).getString(DEVICETYPES, "NA");

        Log.e("TAG", "Device Type In RoomActivity " + deviceTypes);

        ArrayList<DeviceType> _list_device_type = gson.fromJson(deviceTypes, new TypeToken<ArrayList<DeviceType>>() {
        }.getType());
        DeviceType obj_all = new DeviceType();
        obj_all.setID(0);
        obj_all.setName("All");
        obj_all.setSelected(true);
        obj_all.set_icon(getIcon(0));
        _list_device_type.add(0, obj_all);

        top = findViewById(R.id.top);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                /*
                if (position == 0) {
                    RoomActivity.this.finish();
                } else if (position == 1) {
                    int pos = (int) bottomNavigationBar.getTag();
                    Log.e("TAG", "OnRefresh called Selected Pos " + pos);
                    if (pos == 2)
                        getDevices(true);
                    else if (pos == 3) {
                        getScene();
                    } else
                        getGroupsFromServer();
                } else*/
                if (position == 1) {
                    rv_device_type.setVisibility(View.GONE);

                    sceneHolder.setVisibility(View.VISIBLE);
                    scroll_scene_holder.setVisibility(View.VISIBLE);
                    tv_scene.setText(getResources().getString(R.string.scene));
                    rv_group.setVisibility(View.GONE);
                    rv_device_list.setVisibility(View.GONE);
                    if (_mainList_scene == null) {
                        getScene();
                    } else {
                        no_of_device.setText("Devices (" + _mainList_scene.getLst_scene().size() + ")");
                    }
                    btnAddRule.setVisibility(View.GONE);

                } else if (position == 0) {
                    tv_no_scene.setVisibility(View.GONE);
                    sceneHolder.setVisibility(View.GONE);
                    scroll_scene_holder.setVisibility(View.GONE);

                    rv_device_type.setVisibility(View.VISIBLE);
                    tv_scene.setText(getResources().getString(R.string.devices));
                    rv_group.setVisibility(View.GONE);
                    rv_device_list.setVisibility(View.VISIBLE);
                    if (filteredList == null) {
                        tv_no_scene.setText("No Device Found!");
                    }
                    no_of_device.setText("Devices (" + filteredList.getObjData().size() + ")");
                    tv_no_scene.setVisibility(View.GONE);
                    btnAddRule.setVisibility(View.GONE);
                } else if (position == 3) {
                    tv_no_scene.setVisibility(View.GONE);
                    sceneHolder.setVisibility(View.GONE);
                    scroll_scene_holder.setVisibility(View.GONE);
                    rv_device_type.setVisibility(View.GONE);
                    rv_group.setVisibility(View.GONE);
                    rv_device_list.setVisibility(View.GONE);
                    tv_no_scene.setVisibility(View.GONE);
                    btnAddRule.setVisibility(View.VISIBLE);
                } else {
                    rv_device_list.setVisibility(View.GONE);
                    tv_no_scene.setVisibility(View.GONE);
                    rv_device_type.setVisibility(View.GONE);
                    sceneHolder.setVisibility(View.GONE);
                    scroll_scene_holder.setVisibility(View.GONE);
//                    grpHolder.setVisibility(View.VISIBLE);
                    rv_group.setVisibility(View.VISIBLE);
                    tv_scene.setText(getResources().getString(R.string.groups));

                    if (_list == null)
                        getGroupsFromServer();
                    else
                        no_of_device.setText("Devices (" + _list.size() + ")");

                    btnAddRule.setVisibility(View.GONE);
                }
                bottomNavigationBar.setTag(position);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
        _lst_device_type = new ArrayList<>();
        for (int i = 0; i < _list_device_type.size(); i++) {
            _list_device_type.get(i).set_icon(getIcon(_list_device_type.get(i).getID()));
            _lst_device_type.add(_list_device_type.get(i));
        }

        _adapter_device_type = new DeviceTypeAdapter(_lst_device_type, this, this);
        rv_device_type.setAdapter(_adapter_device_type);
        _adapter_device_type.notifyDataSetChanged();

        swipe_refresh_group.setOnRefreshListener(() -> {
            swipe_refresh_group.setRefreshing(true);
            int pos = (int) bottomNavigationBar.getTag();
            Log.e("TAG", "OnRefresh called Selected Pos " + pos);
            if (pos == 2)
                getDevices(true);
            else if (pos == 3) {
                getScene();
            } else
                getGroupsFromServer();
        });

        String _data = _utility.getString(room_Id);
        if (_data == null || _data.isEmpty()) {
            getDevices(false);
        } else {
            updateDevices(_data, true);
        }
    }

    public void Refresh(View view) {
        int pos = (int) bottomNavigationBar.getTag();
        Log.e("TAG", "OnRefresh called Selected Pos " + pos);
        if (pos == 0)
            getDevices(true);
        else if (pos == 1) {
            getScene();
        } else
            getGroupsFromServer();
    }


    public void getDevices(boolean isFromSwipe) {
        showProgressDialog("Please wait...");
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = APIClient.BASE_URL + "/api/Get/getRoomDevice?UID=" + user + "&room=" + room_Id;
        Log.wtf("ROOM_DEVICE_URL", url);
        Call<String> _call = apiInterface.getRoomDevice(url);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (Utility.getIsFresh(RoomActivity.this)) {
                        hideProgressDialog();
//                            Utility.setIsFresh(RoomActivity.this, false);
                    }
                    if (isFromSwipe) hideProgressDialog();
                    _utility.putString(room_Id, response.body());
                    updateDevices(response.body(), false);
                }
                swipe_refresh_group.setRefreshing(false);
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                //hideProgressDialog();
            }
        });
    }

    private void initViews(String data) {
        sceneHolder.setVisibility(View.GONE);
        scroll_scene_holder.setVisibility(View.GONE);
        roomNameTV.setText(Room_Name);
        TextView no_of_device = findViewById(R.id.no_of_device);
        if (filteredList != null && filteredList.getObjData() != null)
            no_of_device.setText(filteredList.getObjData().size());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (filteredList != null && filteredList.getObjData() != null && filteredList.getObjData().size() != 0) {
                _utility.putString(room_Id, intent.getStringExtra("datafromService"));
                updateDevices(intent.getStringExtra("datafromService"), false);
            }
            /*else
                Toast.makeText(context, "Skip Update", Toast.LENGTH_SHORT).show();*/
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "LifeCycle onResume Called isPaused " + isPaused);
        try {
            /*if (isPaused || Rdata.isEmpty() || filteredList == null) {
                getDevices(false);
            }*/
        } catch (Exception e) {
            Log.e("TAGG", "Exception at resume " + e.getMessage());
        }

        /*if (_device_adapter != null) {
            _device_adapter.notifyDataSetChanged();
        }*/
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
    }

    boolean isPaused = false;

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TAG", "LifeCycle OnPause Called");
        isPaused = true;
        unregisterReceiver(broadcastReceiver);
    }

    public void addViews(String Rdata) {
        String data = Constants.jsonObjectreader(Rdata, "data");
        initViews(data);
        Constants.getProgressBar(findViewById(R.id.progressBar));
    }

    public void AddDevice(View view) {
        TextView fromExistingDevice, fromSmartCOnfig;
        Dialog dialog = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_adddevicefrom, null, false);
        dialog.setContentView(view1);
        fromExistingDevice = view1.findViewById(R.id.fromExistingDevice);
        fromSmartCOnfig = view1.findViewById(R.id.fromSmartCOnfig);
        fromExistingDevice.setOnClickListener(v -> {
            Intent intent = new Intent(RoomActivity.this, DeviceList.class);
            intent.putExtra("RoomId", room_Id);
            startActivityForResult(intent, 124);
            dialog.dismiss();
        });
        fromSmartCOnfig.setOnClickListener(v -> {
            Intent intent = new Intent(RoomActivity.this, SmartConfig.class);
            intent.putExtra("RoomId", room_Id);
            startActivityForResult(intent, 123);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "CHECK onActivityResult called RoomActivity " + requestCode);
        isPaused = false;
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                DtypeViews.getGetDevice(this);
                String result = data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: " + result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No device Added", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 124) {
            if (resultCode == Activity.RESULT_OK) {
                DtypeViews.getGetDevice(this);
                String result = data.getStringExtra("result");
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: " + result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No device Added", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == colorpickerActivity) {
            String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
            Log.e("TAGG", "JsonString From Result " + jsonString);
        } else if (requestCode == 500) {
            getScene();
        } else if (requestCode == SCENE_INTENT) {
            getGroupsFromServer();
        }
    }

    public void home(View view) {
        startActivity(new Intent(this, HomeActivity.class));
        Log.wtf("DESTRUCT_CALL_HOME", "SUCCESS");
        finish();
    }

    public void menu(View view) {
        Dialog menu = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = menu.getWindow();
        menu.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.END | Gravity.BOTTOM);

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        View view1 = LayoutInflater.from(this).inflate(R.layout.menu_roomactivity, null, false);
        menu.setContentView(view1);
        menu.show();
    }

    public void logout(View view) {
        savetoShared(RoomActivity.this).edit().putString(USER_ID, "NA").apply();
        startActivity(new Intent(this, LoginReg.class));
        finish();
    }

    public void about_change(View view) {
        Intent intent = new Intent(this, AboutAndChange.class);
        switch (view.getId()) {
            case R.id.about:
                intent.putExtra("what", "about");
                break;
            case R.id.change_log:
                intent.putExtra("what", "Change_log");
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void device_menu_list(View view) {
        String dno1 = Constants.jsonObjectreader(view.getTag().toString(), DtypeViews.dno);
        DeviceInfos = view.getTag().toString();
        PopupMenu popup = new PopupMenu(RoomActivity.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.device_setting_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListener

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_d:
//                        delete(view);
                    String dno11 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
//                        remove(_id);
                    break;
                case R.id.rename_d:
                    rename(view);
                    break;
                case R.id.move_d:
                    changeroom(view);
                    break;
                case R.id.scheduled_d:
                    schedules(view);
                    break;

            }
            return true;
        });
        //ImageView imageView = deviceHolder.findViewWithTag(dno1+"online_status");
        //String imageName = imageView.getContentDescription().toString();
        //Log.d(TAG, "device_menu_list: des"+imageName);
        JSONObject deviceInfo = getDeviceById(dno1, RoomActivity.this);
        boolean isOffline = Boolean.parseBoolean(Constants.jsonObjectreader(deviceInfo.toString(), isOnline));
        if (!isOffline) {
            popup.getMenu().removeItem(R.id.move_d);
            popup.getMenu().removeItem(R.id.rename_d);
            popup.getMenu().removeItem(R.id.scheduled_d);
        }
        popup.show();

    }

    public void remove(final String grpID, int pos) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Remove Group")
                .setMessage("Are you sure you want to Remove this Group ?")
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    // continue with delete
                    deleteGroup(grpID, pos);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void rename(View view) {
        String dno1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
        JSONObject deviceInfo = getDeviceById(dno1, RoomActivity.this);

        Dialog dialog = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_change_device_name, null, false);
        dialog.setContentView(view1);
        EditText deviceName = view1.findViewById(R.id.diaEditName);
        TextView change = view1.findViewById(R.id.dia_change);
        TextView cancel = view1.findViewById(R.id.dia_cancel);
        TextView title = view1.findViewById(R.id.tv_title_dialog);
        title.setText(R.string.rename_device);

        Log.e("TAGGG", "Device Info " + deviceInfo.toString());
        cancel.setOnClickListener(v -> dialog.dismiss());
        change.setOnClickListener(v -> {
            DtypeViews.renameDevice(RoomActivity.this, deviceName.getText().toString(), dno1);
            dialog.dismiss();
        });
        dialog.show();
        closeDialogProgress();
    }


    /* access modifiers changed from: private */
    public void deleteGroup(String Id, int pos) {

        showProgressDialog("Deleting...");
        String url = APIClient.BASE_URL + "/api/Get/delGroup?ID=" + Id;
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<SuccessResponse> observable = apiInterface.delGroup(url);

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SuccessResponse successResponse) {
                hideProgressDialog();
                if (successResponse.getSuccess()) {
                    _list.remove(pos);
                    adapter.notifyItemRemoved(pos);
                } else
                    Toast.makeText(RoomActivity.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void delete(View view) {
        String dno1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
        String dtype1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dtype);
        DtypeViews.removeDeviceFromRoom(this, dno1);
        closeDialogProgress();
    }

    public void changeroom(View view) {
        Dialog dia_addDevice = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_add_device_home, null, false);
        dia_addDevice.setContentView(view1);
        LinearLayout linearLayout = view1.findViewById(R.id.Dia_roomholder);
        linearLayout.removeAllViews();
        String jstr = Constants.savetoShared(RoomActivity.this).getString(Constants.ROOMS, "null");
        if (!jstr.equals("null")) {
            try {
                JSONObject jsonObject = new JSONObject(jstr);
                JSONArray rooms = Constants.getJsonArray(jsonObject, "rooms");
                for (int i = 0; i < rooms.length(); i++) {
                    if (room_Id.equals(rooms.getJSONObject(i).get("ID").toString()))
                        continue;
                    View viewDiaRooms = LayoutInflater.from(RoomActivity.this).inflate(R.layout.add_device_home_dialog_room_holder, null, false);
                    TextView Dia_RoomsTV = viewDiaRooms.findViewById(R.id.dia_room);
                    Dia_RoomsTV.setTag(rooms.getJSONObject(i).get("ID").toString());
                    // Log.d(TAG, "addRooms: "+rooms.getJSONObject(i).get("name"));
                    String roomName = rooms.getJSONObject(i).get("name").toString();
                    Dia_RoomsTV.setText(roomName);
                    Dia_RoomsTV.setOnClickListener(v -> {
                        String dno1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
                        String dtype1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dtype);
                        String key1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.key);
                        DtypeViews.chageDevicefromRoom(RoomActivity.this, key1, v.getTag().toString(), dno1, dtype1);
                        dia_addDevice.dismiss();
                    });
                    linearLayout.addView(viewDiaRooms);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dia_addDevice.show();
        closeDialogProgress();
    }

    public void schedules(View view) {
        closeDialogProgress();
        Intent intent = new Intent(this, schedules_menu.class);
        intent.putExtra("DeviceInfos", DeviceInfos);
        startActivity(intent);

    }

    public void showGraph(View view) {
        Log.d(TAG, view.getTag() + " showGraph: " + Constants.savetoShared(this).getString(view.getTag().toString() + "R", Constants.EMPTY));
        Intent intent = new Intent(this, Graphview.class);
        intent.putExtra("code", view.getTag().toString());
        startActivity(intent);
    }

    public void closeDialogProgress() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    public void updateColor(int color) {
        //EditText ed = findViewById(R.id.color);
        // int color = Color.parseColor("#"+hashColor);
        cardColorup = color;
        top.setBackgroundColor(color);
        String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        addViews(jsonString);
    }

    public void headMenu(View view) {

        PopupMenu popup = new PopupMenu(RoomActivity.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.room_activiy_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.AddDevice:
                    AddDevice(view);
                    break;

                case R.id.AddProfiles:
                    addNewScene();
                    break;

                case R.id.editscene:
//                        startActivityForResult(new Intent(RoomActivity.this, SceneMenu.class), ADDSCENE);
                    startActivityForResult(new Intent(RoomActivity.this, EditScene.class), 500);
                    break;

                //case R.id.SetColor : setColor();break;
                case R.id.addGroup:
                    showGroups();
                    break;
            }
            return true;
        });
        popup.show(); //showing popup menu
    }

    private void addNewScene() {

        if (_mainList_scene != null && _mainList_scene.getLst_scene() != null && _mainList_scene.getLst_scene().size() >= 10) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(RoomActivity.this);
            dialog.setTitle("Scene limit exceeded");
            dialog.setMessage("You can add only 10 scene, please delete existing scene to add more.");
            dialog.setPositiveButton("Okay", (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
            return;
        }
        Intent intent = new Intent(RoomActivity.this, NewSceneTempletActivity.class);
        intent.putExtra("Devices", "new");
        intent.putExtra("roomID", room_Id);
        startActivityForResult(intent, 500);
    }

    private void showGroups() {
//        startActivityForResult(new Intent(RoomActivity.this, GroupMenu.class), ADDGROUP);

        Intent intent = new Intent(this, GroupMenu.class);
        intent.putExtra("Devices", "new");
        intent.putExtra("room_Id", room_Id);
        startActivityForResult(intent, this.SCENE_INTENT);
        /*Intent intent = new Intent(this, GroupEditor.class);
        intent.putExtra("Devices", "new");
        intent.putExtra("room_Id", room_Id);
        startActivityForResult(intent, this.SCENE_INTENT);*/
    }

    private void setColor() {

        final Context context = RoomActivity.this;
        int currentBackgroundColor = 0xffffffff;

        ColorPickerDialogBuilder
                .with(context)
                .setTitle("hello")
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(selectedColor -> {
                    // Handle on color change
                    Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                    updateColor(selectedColor);
                })
                .setOnColorSelectedListener(selectedColor -> {
                    Toast.makeText(context, "" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    updateColor(selectedColor);
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    // changeBackgroundColor(selectedColor);
                    savetoShared(RoomActivity.this).edit().putInt(room_Id + "color", selectedColor).apply();
                })
                .setNegativeButton("cancel", (dialog, which) -> updateColor(cardColorup))
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(RoomActivity.this, android.R.color.holo_blue_bright))
                .build()
                .show();
    }


    public void showGroups(View view) {
        showGroups();
    }


    private void getScene() {
        sceneHolder.removeAllViews();
        RequestQueue queue = Volley.newRequestQueue(this);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        final String url = APIClient.BASE_URL + "/api/Get/getScene?UID=" + user;
        if (!swipe_refresh_group.isRefreshing())
            showProgressDialog(getResources().getString(R.string.please_wait));
        StringRequest getRequest = new StringRequest(GET, url,
                response -> {
                    JSONArray remo;
                    String InvalidJSON = response.replace("\\\"", "\"").trim();
                    try {
                        _mainList_scene = _gson.fromJson(response, scene_model.class);
                        JSONObject mainJsop = stringToJsonObject(response);
                        remo = mainJsop.getJSONArray("Scene");
                        for (int i = 0; i < remo.length(); i++) {

                            JSONObject jsonObject = remo.getJSONObject(i);
                            JSONArray devices = jsonObject.getJSONArray("Devices");
                            String id = jsonObject.getString("ID");

                            View dli = LayoutInflater.from(this).inflate(R.layout.item_scene, null, false);
                            LinearLayout dli_parent = dli.findViewById(R.id.ll_main_item);

                            RadioButton rb = dli.findViewById(R.id.radiobtn);
                            rb.setVisibility(View.GONE);
                            TextView schedules_name = dli.findViewById(R.id.tv_device_name);

                            schedules_name.setText(jsonobjectSTringJSON(jsonObject, "Name"));

                            dli_parent.setOnClickListener(v -> triggerscene(id));

                            sceneHolder.addView(dli);
                        }

                        View dli = LayoutInflater.from(this).inflate(R.layout.add_scene_item, null, false);
                        LinearLayout dli_parent = dli.findViewById(R.id.ll_main_item);
                        dli_parent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNewScene();
                            }
                        });
                        sceneHolder.addView(dli);
                        // remo = new JSONArray(InvalidJSON);
                        Log.d(TAG, "onCreate: String using Replace  " + remo);
                        no_of_device.setText("Devices (" + remo.length() + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (sceneHolder.getChildCount() == 0) {
                        tv_no_scene.setText("No Scene Found!");
                    }
                    tv_no_scene.setVisibility(View.GONE);

                    swipe_refresh_group.setRefreshing(false);
                    hideProgressDialog();
                },
                error -> {
                    hideProgressDialog();
                    swipe_refresh_group.setRefreshing(false);
                    Log.d("Error.Response", "error" + error.getMessage());
                    Toast.makeText(this, "Unable to Delete", Toast.LENGTH_SHORT).show();
                }

        );
// add it to the RequestQueue
        queue.add(getRequest);
    }

    private void triggerscene(String id) {
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


    GroupsAdapter adapter;
    ArrayList<scene_model.Scene> _list;

    void getGroupsFromServer() {
        Log.e("TAG", "getGroupsFromServer called");
        if (!swipe_refresh_group.isRefreshing())
            showProgressDialog(getResources().getString(R.string.please_wait));

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
//        String url="http://209.58.164.151:88/api/Get/updRoom?UID={UID}&roomID={RoomID}&RoomName={Room_Name}"
        String url = APIClient.BASE_URL + "/api/Get/getGroup?UID=" + user;
        Observable<scene_model> observable = apiInterface.getGroups(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<scene_model>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(scene_model scene) {
                if (scene != null) {
                    Log.e("TAGGG", "On Next Called " + scene.lst_scene.size());
                    _list = scene.getLst_scene();
                    Log.e("TAGGG", "Group Size " + _list.size());

                    AllDataResponseModel _all_data = gson.fromJson(Rdata, AllDataResponseModel.class);
                    Log.wtf("GROUP_JSON_DATA", String.valueOf(_all_data));
                    try {
                        for (int i = 0; i < _list.size(); i++) {
                            boolean isOnline;
                            isOnline = isDeviceOnline(_list.get(i).getID());
                            if (isOnline)
                                _list.get(i).setOnline(true);
                            for (int j = 0; j < _list.get(i).getDevices().size(); j++) {
                                isOnline = isDeviceOnline(_list.get(i).getDevices().get(j));
                                if (isOnline) {
                                    _list.get(i).setOnline(true);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at set status " + e.getMessage(), e);
                    }
                    adapter = new GroupsAdapter(_list, RoomActivity.this, _grp_interface);
                    rv_group.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    swipe_refresh_group.setRefreshing(false);
                    no_of_device.setText("Devices (" + _list.size() + ")");
                } else
                    Toast.makeText(RoomActivity.this, "Group Null", Toast.LENGTH_SHORT).show();

                try {
                    if (_list == null || _list.size() == 0) {
                        tv_no_scene.setText("No Groups Found!");
//                        tv_no_scene.setVisibility(View.VISIBLE);
                    }
                    tv_no_scene.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipe_refresh_group.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGGG", "Exception at getGroup " + e.getMessage(), e);
                swipe_refresh_group.setRefreshing(false);
                hideProgressDialog();
            }

            @Override
            public void onComplete() {
                Log.e("TAGGG", "OnComplete Call");
                hideProgressDialog();
                swipe_refresh_group.setRefreshing(false);
            }
        });
    }

    boolean isDeviceOnline(String device) {
        AllDataResponseModel _all_data = gson.fromJson(Rdata, AllDataResponseModel.class);
        for (int k = 0; k < _all_data.getObjData().size(); k++) {
            if (device.equalsIgnoreCase(_all_data.getObjData().get(k).getDno())) {
//                Log.e("TAGG", "isDeviceOnline device from list " + filteredList.getObjData().get(k).getDno());
                Log.e("TAG", "Device Check Status" + device + " isOnline " + _all_data.getObjData().get(k).isOnline());
                if (_all_data.getObjData().get(k).isOnline()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void SelectGroup(int pos) {
        Intent intent = new Intent(this, group_rgb_controls.class);
        intent.putExtra("id", _list.get(pos).getID());
        intent.putExtra("name", _list.get(pos).getName());
        startActivity(intent);
    }

    @Override
    public void DeleteGroup(int pos) {
        remove(_list.get(pos).getID(), pos);
    }

    @Override
    public void RenameGroup(int pos) {
        renameGroup(pos);
    }

    @Override
    public void triggerGroup(int pos, boolean state) {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);

        if (_list.get(pos).getGroupType() == 0) {
            String groupId = _list.get(pos).getID();
            JsonObject object = new JsonObject();
            object.addProperty("state", state);

//            String url = "http://209.58.164.151:88/api/Get/triggerGroup?ID=" + groupId + "&data=" + object.toString();
            String url = APIClient.BASE_URL + "/api/Get/triggerGroup?ID=" + groupId + "&data=" + object.toString();
            Log.e("TAGG", "Final URL " + url);
            Observable<SuccessResponse> observable = apiInterface.triggerRoom(url);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(SuccessResponse successResponse) {
                    Log.e("TAGG", "OnNext called successResponse ");
                    if (successResponse != null) {
                        if (successResponse.getSuccess())
                            Log.e("TAGG", "Triggered Successfully");
                        else
                            Log.e("TAGG", "Triggered Failed");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAGG", "OnError Called " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {
                }
            });
        } else if (_list.get(pos).getGroupType() == 6) {
            try {
                for (int i = 0; i < 4; i++) {
                    JsonObject _obj = new JsonObject();
                    _obj.addProperty("dno", _list.get(pos).getID());
                    _obj.addProperty("key", getKeyFromDno(_list.get(pos).getID()));
                    _obj.addProperty("dtype", "6");

                    JsonObject object = new JsonObject();
                    object.addProperty("state", state);
                    _obj.add("d" + (i + 1), object);
                    String json = gson.toJson(_obj);
                    Log.e("TAGGG", "Object in String " + json);
                    pushLog(json);

                    new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                            json,
                            1,
                            "d/" + _list.get(pos).getID() + "/sub");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAGGG", "Exception at Rename Devices " + e.getMessage(), e);
            }
        } else if (_list.get(pos).getGroupType() == 5) {
            try {
                for (int i = 0; i < 2; i++) {
                    JsonObject _obj = new JsonObject();
                    _obj.addProperty("dno", _list.get(pos).getID());
                    _obj.addProperty("key", getKeyFromDno(_list.get(pos).getID()));
                    _obj.addProperty("dtype", "5");

                    JsonObject object = new JsonObject();
                    object.addProperty("state", state);
                    _obj.add("d" + (i + 1), object);
                    String json = gson.toJson(_obj);
                    Log.e("TAGGG", "Object in String " + json);
                    pushLog(json);
                    new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                            json,
                            1,
                            "d/" + _list.get(pos).getID() + "/sub");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAGGG", "Exception at Rename Devices " + e.getMessage(), e);
            }
        }
    }

    public String getKeyFromDno(String dno) {
        for (int i = 0; i < filteredList.getObjData().size(); i++) {
            if (filteredList.getObjData().get(i).getDno().equalsIgnoreCase(dno)) {
                return filteredList.getObjData().get(i).getKey();
            }
        }
        return "";
    }

    @Override
    public void MoveTo(String roomID, String dno) {
        try {
            Log.wtf("MOVE_TO", roomID + " > " + dno);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("room", roomID);

            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject.toString(),
                    1,
                    "d/" + dno + "/sub");
        } catch (JSONException | UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Scheduled(int pos) {
    }

    int selectedType = 0;

    @Override
    public void selectType(int type) {
        selectedType = type;
        try {
            AllDataResponseModel _all_data = gson.fromJson(Rdata, AllDataResponseModel.class);
            if (filteredList != null)
                filteredList.getObjData().clear();
            ArrayList<data> _lst = new ArrayList<>();
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (selectedType == 0 || _all_data.getObjData().get(i).getRoom().equalsIgnoreCase(room_Id)) {
                    try {
                        if (selectedType == 0 || !isDeviceAdded(_lst, _all_data.getObjData().get(i).getDno()) && _all_data.getObjData().get(i).getDtype() == selectedType) {
                            _all_data.getObjData().get(i).setD_typeName(getNameFromType(_all_data.getObjData().get(i).getDtype()));
                            _all_data.getObjData().get(i).setDrawable(getDrawable(RoomActivity.this, _all_data.getObjData().get(i).getDtype() + ""));
                            _all_data.getObjData().get(i).setSignalDrawable(setSignal(_all_data.getObjData().get(i).getSignal(), RoomActivity.this));
//                        filteredList.getObjData().add(filteredList.getObjData().get(i));
                            _lst.add(_all_data.getObjData().get(i));
                        }
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at setName " + e.getMessage());
                    }
                }
            }

            filteredList.setObjData(_lst);
            no_of_device.setText("Devices (" + filteredList.getObjData().size() + ")");
            _device_adapter = new RoomDeviceAdapter(filteredList, RoomActivity.this, this);
            rv_device_list.setAdapter(_device_adapter);
            _device_adapter.notifyDataSetChanged();
            Log.e("TAG", "Data at select filtered size " + filteredList.getObjData().size() + " All size " + _all_data.getObjData().size());
        } catch (Exception e) {
            Log.e("TAG", "Exception at selectType " + e.getMessage());
        }
    }

    public void renameGroup(int pos) {
        Dialog dialog = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_change_device_name, null, false);
        dialog.setContentView(view1);
        EditText devicename = view1.findViewById(R.id.diaEditName);
        TextView change = view1.findViewById(R.id.dia_change);
        TextView cancel = view1.findViewById(R.id.dia_cancel);
        devicename.setText(_list.get(pos).getName());
        cancel.setOnClickListener(v -> dialog.dismiss());
        change.setOnClickListener(v -> {
            if (devicename.getText().toString().isEmpty()) {
                devicename.setError(getResources().getString(R.string.require));
                return;
            }
            for (int i = 0; i < _list.size(); i++) {
                if (devicename.getText().toString().trim().equalsIgnoreCase(_list.get(i).getName())) {
                    devicename.setError(devicename.getText().toString() + " Already Exist!");
                    return;
                }
            }
            try {
                String user = Constants.savetoShared(RoomActivity.this).getString(Constants.USER_ID, "0");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("UID", user);
                jsonObject.addProperty("ID", _list.get(pos).getID());
                jsonObject.addProperty("Name", devicename.getText().toString().trim());

                JsonArray _arr = new JsonArray();
                for (int i = 0; i < _list.get(pos).getDevices().size(); i++) {
                    _arr.add(new JsonPrimitive(_list.get(pos).getDevices().get(i)));
                }
                jsonObject.add("Devices", _arr);

                Log.e("TAGGG", "Json Object " + jsonObject.toString());
                renameGroup(jsonObject, pos, devicename.getText().toString());
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at e " + e.getMessage(), e);
            }
            dialog.dismiss();
        });
        dialog.show();
        closeDialogProgress();
    }


    AllDataResponseModel filteredList = new AllDataResponseModel();
    RoomDeviceAdapter _device_adapter;

    //    ArrayList<data> all_device_list = new ArrayList<>();
    String Rdata = "";

    boolean isDeviceSubscribed = false;

    public void updateDevices(String data, boolean isFromOffline) {
        try {
            this.Rdata = data;
            AllDataResponseModel _all_data = gson.fromJson(Rdata, AllDataResponseModel.class);
            if (filteredList != null)
                filteredList.getObjData().clear();

            ArrayList<data> _lst = new ArrayList<>();
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                try {
                    if (!isDeviceSubscribed) {
                        subscribe(_all_data.getObjData().get(i).getDno());
                    }
                    if (selectedType == 0 || _all_data.getObjData().get(i).getDtype() == selectedType || _all_data.getObjData().get(i).getRoom().isEmpty()) {
                        _all_data.getObjData().get(i).setD_typeName(getNameFromType(_all_data.getObjData().get(i).getDtype()));
                        _all_data.getObjData().get(i).setDrawable(getDrawable(RoomActivity.this, _all_data.getObjData().get(i).getDtype() + ""));
                        _all_data.getObjData().get(i).setSignalDrawable(setSignal(_all_data.getObjData().get(i).getSignal(), RoomActivity.this));
                        _lst.add(_all_data.getObjData().get(i));
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at setName " + e.getMessage());
                }
            }
            isDeviceSubscribed = true;
            filteredList.setObjData(_lst);
            no_of_device.setText("Devices (" + filteredList.getObjData().size() + ")");
            _device_adapter = new RoomDeviceAdapter(filteredList, RoomActivity.this, this);
            rv_device_list.setAdapter(_device_adapter);
            _device_adapter.notifyDataSetChanged();

            //setup offline update WIP
            if (isFromOffline)
                getUpdatedData();
            else
                hideProgressDialog();

            filterEmptyGroup(_all_data);
        } catch (Exception e) {
            hideProgressDialog();
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "Exception at update " + e.getMessage());
        }
    }

    public boolean isDeviceAdded(ArrayList<data> _lst, String dno) {
        for (int i = 0; i < _lst.size(); i++) {
            Log.e("TAG", "isDeviceAdded <> dno " + dno + " List DNO " + _lst.get(i).getDno());
            if (dno.equalsIgnoreCase(_lst.get(i).getDno())) {
                return false;
            }
        }
        return false;
    }

    void filterEmptyGroup(AllDataResponseModel _all_data) {
        try {
            Log.e("TAG", "filterEmptyGroup size Before " + _lst_device_type.size());
            ArrayList<DeviceType> _temp = new ArrayList<>();
            for (int j = 0; j < _lst_device_type.size(); j++) {
                _temp.add(_lst_device_type.get(j));
            }
            for (int i = 0; i < _temp.size(); i++) {
                boolean isFound = false;
                for (int j = 0; j < _all_data.getObjData().size(); j++) {
//                    Log.e("TAG", "Both Types temp " + _temp.get(i).getID() + " " + _all_data.getObjData().get(j).getDtype());
                    if (room_Id.equalsIgnoreCase(_all_data.getObjData().get(j).getRoom())) {
                        if (_temp.get(i).getID() == _all_data.getObjData().get(j).getDtype()) {
                            isFound = true;
                            break;
                        }
                    }
                }
                if (!isFound && _temp.get(i).getID() != 0) {
                    for (int k = 0; k < _lst_device_type.size(); k++) {
                        if (_temp.get(i).getID() == _lst_device_type.get(k).getID()) {
                            _lst_device_type.remove(k);
                            _adapter_device_type.notifyItemRemoved(k);
                        }
                    }
                }
            }
            Log.e("TAG", "filterEmptyGroup size After " + _lst_device_type.size());
        } catch (Exception e) {
            Log.e("TAG", "Exception at FilterEmpty " + e.getMessage());
        }
    }

    private void subscribe(String adno) {
        try {
            new PahoMqttClient().subscribe(Constants.GeneralpahoMqttClient, "d/" + adno + "/#", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    String getNameFromType(int id) {
        String name = "";
        try {
            for (int i = 0; i < _lst_device_type.size(); i++) {
                if (id == _lst_device_type.get(i).getID()) {
                    name = _lst_device_type.get(i).getName();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at getName " + e.getMessage());
        }
        return name;
    }

    @Override
    public void click_device(int pos, int type) {
        if (type == 2) {
            try {
                String data = gson.toJson(filteredList.getObjData().get(pos));
                Log.e("TAGG", "Data for type 2 " + data);
                Intent intent = new Intent(RoomActivity.this, rgb_controls.class);
                intent.putExtra("jsonString", data);
                intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());
                intent.putExtra("_name", filteredList.getObjData().get(pos).getObjd1().getName());
                startActivityForResult(intent, colorpickerActivity);
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at click " + e.getMessage());
            }
        } else if (type == 10) {
//            Intent intent = new Intent(RoomActivity.this, RemoteMenu.class);
            Intent intent = new Intent(RoomActivity.this, UserRemoteCatList.class);
            if (filteredList.getObjData() != null && filteredList.getObjData().get(pos).getDno() != null)
                intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());

            if (filteredList.getObjData() != null && filteredList.getObjData().get(pos).getObjd1() != null && filteredList.getObjData().get(pos).getObjd1().getName() != null)
                intent.putExtra("dname", filteredList.getObjData().get(pos).getObjd1().getName());
            else
                intent.putExtra("dname", filteredList.getObjData().get(pos).getName());
//            ArrayList<String> _lst_remote = filteredList.getObjData().get(pos).getIr();
//            String remote_data = String.valueOf(_lst_remote);
//            intent.putStringArrayListExtra("remotes_data", _lst_remote);
//                        intent.putExtra("r_type", "rf");
            intent.putExtra("remotes", "rf");
            intent.putExtra("mode", "0");// "1" for working remote
            startActivity(intent);
        } else if (type == 15) {
            //setup for type 15
            Intent intent = new Intent(RoomActivity.this, DeviceTyp15.class);
            if (filteredList.getObjData() != null && filteredList.getObjData().get(pos).getDno() != null)
                intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());

            if (filteredList.getObjData() != null && filteredList.getObjData().get(pos).getObjd1() != null && filteredList.getObjData().get(pos).getObjd1().getName() != null)
                intent.putExtra("dname", filteredList.getObjData().get(pos).getObjd1().getName());
            else
                intent.putExtra("dname", filteredList.getObjData().get(pos).getName());

//            intent.putExtra("br", filteredList.getObjData().get(pos).getObjd1().getBr());// "1" for working remote
            intent.putExtra("br", filteredList.getObjData().get(pos).getObjd1().getBr());// "1" for working remote
            intent.putExtra("key", filteredList.getObjData().get(pos).getKey() + "");// "1" for working remote
            intent.putExtra("state", filteredList.getObjData().get(pos).getObjd1().isState() + "");// "1" for working remote
            startActivity(intent);
        } else if (type == 21) {
            Intent intent = new Intent(RoomActivity.this, Type21Activity.class);
            intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());
            startActivity(intent);
        }
    }


    int getDrawable(Context context, String dtype) {
        int resource = 0;
        switch (dtype) {
            case "1":
            case "5":
                resource = R.drawable.socket_g_svg;
                break;
            case "2":
                resource = R.drawable.rgb_bulb;
                break;
            case "10":
                resource = R.drawable.d_10_icon;
                break;
            case "11":
                resource = R.drawable.open_door;
                break;
                case "12":
                resource = R.drawable.icon_multi_sensor;
                break;
            default:
                resource = R.drawable.rgb_bulb;

        }
        return resource;
    }

    public int setSignal(int strength, Context context) {
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
        return signalDawables;
    }


    @Override
    public void updateStatus(String deviceInfo) {
        Log.e("TAGGG", "Update Status Called deviceInfo " + deviceInfo);
        String dNum = "";
        String power = "";

        AllDataResponseModel _all_data = gson.fromJson(Rdata, AllDataResponseModel.class);
        hideProgressDialog();
        Log.e("TAGGG", "All Data size Before " + _all_data.getObjData().size());
        if (_device_adapter != null) {
            _device_adapter.notifyDataSetChanged();
        }
        data obj = gson.fromJson(deviceInfo, data.class);

        for (int i = 0; i < filteredList.getObjData().size(); i++) {
//            Log.e("TAGGG", "Update Status From Object " + obj.getDno() + " From List " + filteredList.getObjData().get(i).getDno());

            try {
                JSONObject jsonObject = new JSONObject(deviceInfo);
                dNum = jsonObject.getString("dno");
                power = jsonObject.getString("power");
//                Log.wtf("UPDATED_POWER_DATA", dNum + " - " + power);

                _device_adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (obj.getDno().equalsIgnoreCase(filteredList.getObjData().get(i).getDno())) {
                if (obj.getDtype() == 6) {
                    try {
                        JSONObject _object = new JSONObject(deviceInfo);
                        if (_object.has("d1")) {
                            JSONObject objD1 = _object.getJSONObject("d1");
                            filteredList.getObjData().get(i).getObjd1().setState(objD1.getBoolean("state"));
                        }
                        if (_object.has("d2")) {
                            JSONObject objD2 = _object.getJSONObject("d2");
                            filteredList.getObjData().get(i).getObjd2().setState(objD2.getBoolean("state"));
                        }
                        if (_object.has("d3")) {
                            JSONObject objD3 = _object.getJSONObject("d3");
                            filteredList.getObjData().get(i).getObjd3().setState(objD3.getBoolean("state"));
                        }
                        if (_object.has("d4")) {
                            JSONObject objD4 = _object.getJSONObject("d4");
                            filteredList.getObjData().get(i).getObjd4().setState(objD4.getBoolean("state"));
                        }

                        if (_object.has("isOnline")) {
                            filteredList.getObjData().get(i).setOnline(_object.getBoolean("isOnline"));
                        }
                        filteredList.getObjData().get(i).setDrawable(getDrawable(RoomActivity.this, obj.getDtype() + ""));
                        filteredList.getObjData().get(i).setSignal(setSignal(obj.getSignal(), RoomActivity.this));
                    } catch (Exception e) {
                        Log.e("TAGGG", "Exception at parse data ");
                    }
                } else if (obj.getDtype() == 8) {
                    try {
                        JSONObject _object = new JSONObject(deviceInfo);
                        if (_object.has("d1")) {
                            JSONObject objD1 = _object.getJSONObject("d1");
                            filteredList.getObjData().get(i).getObjd1().setBr(objD1.getDouble("br"));
                            filteredList.getObjData().get(i).getObjd1().setState(objD1.getBoolean("state"));
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Exception " + e.getMessage());
                    }
                } else {
                    try {
                        Log.e("TAG", "New Value of ********************************");
                        JSONObject _object = new JSONObject(deviceInfo);
                        if (_object.has("d1")) {
                            JSONObject objD1 = _object.getJSONObject("d1");
                            if (objD1.has("state"))
                                filteredList.getObjData().get(i).getObjd1().setState(objD1.getBoolean("state"));

                            if (objD1.has("name"))
                                filteredList.getObjData().get(i).getObjd1().setName(objD1.getString("name"));

                            if (objD1.has("r")) {
                                Log.e("TAG", "New Valued of RGB Before R " + filteredList.getObjData().get(i).getObjd1().getR());
                                filteredList.getObjData().get(i).getObjd1().setR(objD1.getInt("r"));
                                Log.e("TAG", "New Valued of RGB After R " + filteredList.getObjData().get(i).getObjd1().getR());
                            }
                            if (objD1.has("g")) {
                                Log.e("TAG", "New Valued of RGB Before G " + filteredList.getObjData().get(i).getObjd1().getG());
                                filteredList.getObjData().get(i).getObjd1().setG(objD1.getInt("g"));
                                Log.e("TAG", "New Valued of RGB After G " + filteredList.getObjData().get(i).getObjd1().getG());
                            }

                            if (objD1.has("b")) {
                                Log.e("TAG", "New Valued of RGB B Before " + filteredList.getObjData().get(i).getObjd1().getB());
                                filteredList.getObjData().get(i).getObjd1().setB(objD1.getInt("b"));
                                Log.e("TAG", "New Valued of RGB B After " + filteredList.getObjData().get(i).getObjd1().getB());
                            }

                            if (objD1.has("br")) {
                                Log.e("TAG", "New Valued of RGB Br Before " + filteredList.getObjData().get(i).getObjd1().getBr());
                                filteredList.getObjData().get(i).getObjd1().setBr(objD1.getDouble("br"));
                                Log.e("TAG", "New Valued of RGB Br After " + filteredList.getObjData().get(i).getObjd1().getBr());
                            }
                        }

                        if (_object.has("d2")) {
                            JSONObject objD2 = _object.getJSONObject("d2");
                            if (objD2.has("state"))
                                filteredList.getObjData().get(i).getObjd2().setState(Boolean.parseBoolean(objD2.getString("state")));

                            if (objD2.has("name"))
                                filteredList.getObjData().get(i).getObjd2().setName(objD2.getString("name"));
                        }

                        if (_object.has("isOnline")) {
                            filteredList.getObjData().get(i).setOnline(_object.getBoolean("isOnline"));
                        }
                        filteredList.getObjData().get(i).setDrawable(getDrawable(RoomActivity.this, obj.getDtype() + ""));
                        filteredList.getObjData().get(i).setSignal(setSignal(obj.getSignal(), RoomActivity.this));
//                        Log.e("TAGGG", "Updated Data online  After " + filteredList.getObjData().get(i).isOnline());

                    } catch (Exception e) {
                        Log.e("TAGG", "Exception at setType 2 " + e.getMessage(), e);
                    }
                }
                _device_adapter.notifyItemChanged(i);
                _device_adapter.notifyDataSetChanged();
                break;
            }
        }

        //Update in all data of Rdata
        for (int i = 0; i < _all_data.getObjData().size(); i++) {
            for (int j = 0; j < filteredList.getObjData().size(); j++) {
                if (_all_data.getObjData().get(i).getDno().equalsIgnoreCase(filteredList.getObjData().get(j).getDno())) {
                    _all_data.getObjData().set(i, filteredList.getObjData().get(j));
//                    Log.e("TAG", "Data Replaced DNO <> " + _all_data.getObjData().get(i).getDno());
                    _all_data.getObjData().get(i).setPowerPuff(power);
                    _all_data.getObjData().get(i).setdNum(dNum);
                    break;
                }
            }
        }

        Rdata = gson.toJson(_all_data, AllDataResponseModel.class);
        Log.e("TAG", "Data After Update " + Rdata);
        _utility.putString(room_Id, Rdata);

        Log.e("TAGGG", "All Data size After " + _all_data.getObjData().size());
    }

    @Override
    public void RenameDevice(int pos, String type) {
        renameDevice(filteredList.getObjData().get(pos), type);
    }


    void renameGroup(JsonObject _object, int pos, String newname) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<SuccessResponse> observable = apiInterface.UpdateGroup(_object);

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SuccessResponse successResponse) {
                if (successResponse != null) {
                    if (successResponse.getSuccess()) {
                        _list.get(pos).setName(newname);
                        adapter.notifyItemChanged(pos);
                    }
                } else
                    Toast.makeText(RoomActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        //getGetDevice(context);
    }

    public void renameDevice(data objData, String type) {

        Log.e("TAGGG", "It's type " + type);

        Dialog dialog = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dia_change_device_name, null, false);
        dialog.setContentView(view1);
        EditText devicename = view1.findViewById(R.id.diaEditName);
        TextView change = view1.findViewById(R.id.dia_change);
        TextView cancel = view1.findViewById(R.id.dia_cancel);
        TextView title = view1.findViewById(R.id.tv_title_dialog);
        if (type.equals("d1")) title.setText("RENAME DEVICE");
        else title.setText("RENAME GROUP");

        if (objData.getDtype() == 2) {
            devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
        } else if (objData.getDtype() == 6) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            } else if (type.equalsIgnoreCase("d3")) {
                devicename.setText(objData.getObjd3() != null ? objData.getObjd3().getName() : "");
            } else if (type.equalsIgnoreCase("d4")) {
                devicename.setText(objData.getObjd4() != null ? objData.getObjd4().getName() : "");
            }
        } else if (objData.getDtype() == 20) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            } else if (type.equalsIgnoreCase("d3")) {
                devicename.setText(objData.getObjd3() != null ? objData.getObjd3().getName() : "");
            } else if (type.equalsIgnoreCase("d4")) {
                devicename.setText(objData.getObjd4() != null ? objData.getObjd4().getName() : "");
            }
        } else if (objData.getDtype() == 5) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 19) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 8) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 9) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 18) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 1) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            }
        } else if (objData.getDtype() == 14) {
            if (type.equalsIgnoreCase("d1"))
                devicename.setText(objData.getObjd1() != null ? objData.getObjd1().getName() : "");
            else if (type.equalsIgnoreCase("d2")) {
                devicename.setText(objData.getObjd2() != null ? objData.getObjd2().getName() : "");
            } else if (type.equalsIgnoreCase("d3")) {
                devicename.setText(objData.getObjd3() != null ? objData.getObjd3().getName() : "");
            } else if (type.equalsIgnoreCase("d4")) {
                devicename.setText(objData.getObjd4() != null ? objData.getObjd4().getName() : "");
            }
        }

        cancel.setOnClickListener(v -> dialog.dismiss());
        change.setOnClickListener(v -> {
            if (devicename.getText().toString().isEmpty()) {
                devicename.setError(getResources().getString(R.string.require));
                return;
            }
            for (int i = 0; i < filteredList.getObjData().size(); i++) {
                if (filteredList.getObjData().get(i).getObjd1() != null)
                    if (devicename.getText().toString().trim().equalsIgnoreCase(filteredList.getObjData().get(i).getObjd1().getName())) {
                        devicename.setError(devicename.getText().toString() + " Already Exist!");
                        return;
                    }
            }
            try {
                JsonObject _obj = new JsonObject();
                _obj.addProperty("dno", objData.getDno());
                _obj.addProperty("key", objData.getKey());
                _obj.addProperty("dtype", objData.getDtype());

                JsonObject _obj_d1 = new JsonObject();
                _obj_d1.addProperty("name", devicename.getText().toString().trim());

                _obj.add(type, _obj_d1);
                String json = gson.toJson(_obj);

                Log.e("TAGGG", "Object in String " + json);
                pushLog(json);
                new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                        json,
                        1,
                        "d/" + objData.getDno() + "/sub");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAGGG", "Exception at Rename Devices " + e.getMessage(), e);
            }
            dialog.dismiss();
        });
        dialog.show();
        closeDialogProgress();
    }


    @Override
    public void TurnOnOffDevice(int pos, String type, boolean state, String dno, String key, String dtype) {

        //showProgressDialog(getResources().getString(R.string.please_wait));
        try {
            JsonObject _obj = new JsonObject();
            _obj.addProperty("dno", filteredList.getObjData().get(pos).getDno());
            _obj.addProperty("key", filteredList.getObjData().get(pos).getKey());
            _obj.addProperty("dtype", filteredList.getObjData().get(pos).getDtype());
//            filteredList.getObjData().get(pos).getObjd1().setState(state);

/*
            _obj.addProperty("dno", dno);
            _obj.addProperty("key", key);
            _obj.addProperty("dtype", dtype);*/

            JsonObject object = new JsonObject();
            object.addProperty("state", state);
            _obj.add(type, object);

            /*filteredList.getObjData().get(pos).getObjd1().setState(state);
            _device_adapter.notifyItemChanged(pos);*/
            String json = gson.toJson(_obj);
            Log.e("TURN_ON_OFF_DEVICE", "Object in String " + json);
            pushLog(json);
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    json,
                    1,
                    "d/" + dno + "/sub");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAGGG", "Exception at TurnOnOffDevice Devices " + e.getMessage(), e);
        }
    }

    @Override
    public AllDataResponseModel getAllData() {
        return filteredList;
    }

    @Override
    public void scheduleDevice(int pos, int type) {
        try {
            Log.e("TAG", "scheduleDevice filteredList size " + filteredList.getObjData().size());
            int DeviceType = filteredList.getObjData().get(pos).getDtype();
            if (DeviceType == 2) {
                try {
                    String data = gson.toJson(filteredList.getObjData().get(pos));
                    Log.e("TAGG", "Data for type 2 " + data);
                    Intent intent = new Intent(RoomActivity.this, rgb_controls.class);
                    intent.putExtra("jsonString", data);
                    intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());
                    intent.putExtra("isFromSchedul", true);
                    intent.putExtra("_name", filteredList.getObjData().get(pos).getObjd1().getName());
                    startActivityForResult(intent, colorpickerActivity);
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at click " + e.getMessage());
                }
            } else if (DeviceType == 4) {
                try {
                    String data = gson.toJson(filteredList.getObjData().get(pos));
                    Log.e("TAGG", "Data for type 2 " + data);
                    Intent intent = new Intent(RoomActivity.this, rgb_controls.class);
                    intent.putExtra("jsonString", data);
                    intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());
                    intent.putExtra("isFromSchedul", true);
                    intent.putExtra("_name", filteredList.getObjData().get(pos).getObjd1().getName());
                    startActivityForResult(intent, colorpickerActivity);
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at click " + e.getMessage());
                }
            } else {
                Intent intent = new Intent(RoomActivity.this, ScheduleList.class);
                intent.putExtra("dno", filteredList.getObjData().get(pos).getDno());
                intent.putExtra("type", type);
                startActivityForResult(intent, 501);
            }
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at schedule Device " + e.getMessage());
        }
    }

    @Override
    public void editGroup(int pos) {

        Intent intent = new Intent(this, GroupEditor.class);
        intent.putExtra("room_Id", room_Id);
        intent.putExtra("FromEdit", true);
        intent.putExtra("name", _list.get(pos).getName());
        intent.putExtra("grp_id", _list.get(pos).getID());
        intent.putStringArrayListExtra("devices_lst", _list.get(pos).getDevices());

        startActivityForResult(intent, this.SCENE_INTENT);
    }

    @Override
    public void publishSeekbar(int pos, int br) {
        JSONObject jsonObjectD1 = new JSONObject();
        JSONObject jsonObjectMain = new JSONObject();
        String _json = "";
        try {
            jsonObjectD1.put("state", filteredList.getObjData().get(pos).getObjd1().isState());
            jsonObjectD1.put("br", (double) br / 100);
            jsonObjectMain.put("dno", filteredList.getObjData().get(pos).getDno());
            jsonObjectMain.put("key", filteredList.getObjData().get(pos).getKey());
            jsonObjectMain.put("dtype", filteredList.getObjData().get(pos).getDtype());
            jsonObjectMain.put("d1", jsonObjectD1);
            _json = jsonObjectMain.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    _json,
                    1,
                    "d/" + filteredList.getObjData().get(pos).getDno() + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void publishSeekbarType16(int pos, double br) {
        JSONObject jsonObjectD1 = new JSONObject();
        JSONObject jsonObjectMain = new JSONObject();
        String _json = "";
        try {
            jsonObjectD1.put("state", filteredList.getObjData().get(pos).getObjd1().isState());
            jsonObjectD1.put("br", br);
            jsonObjectMain.put("dno", filteredList.getObjData().get(pos).getDno());
            jsonObjectMain.put("key", filteredList.getObjData().get(pos).getKey());
            jsonObjectMain.put("dtype", filteredList.getObjData().get(pos).getDtype());
            jsonObjectMain.put("d1", jsonObjectD1);
            _json = jsonObjectMain.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "publishSeekbarType16 ::" + _json);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    _json,
                    1,
                    "d/" + filteredList.getObjData().get(pos).getDno() + "/sub");
        } catch (MqttException | UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void view_logs(int pos, String dno) {
        Log.e("TAG", "view_logs called pos " + pos + " dno " + dno);
        Intent _intent = new Intent(RoomActivity.this, ViewLogs.class);
        _intent.putExtra("dno", dno);
        startActivityForResult(_intent, 121);

        //getDeviceLogs(dno);
    }

    void getDeviceLogs(String dno) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
//        String url = "http://209.58.164.151:88/api/Get/triggerGroup?ID=" + groupId + "&data=" + object.toString();
        String url = APIClient.BASE_URL + "/api/Get/GetDeviceLog?dno=" + dno + "&timef=1477468444&timet=1677468444";
        Log.e("TAGG", "Final URL " + url);
        Observable<ArrayList<data>> observable = apiInterface.getDeviceLogs(url);
        showProgressDialog(getString(R.string.please_wait));
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<data>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ArrayList<data> successResponse) {
                Log.e("TAGG", "OnNext called successResponse ");
                if (successResponse != null) {
                    Log.e("TAG", "Total Log received " + successResponse.size() + " " + successResponse.get(0).getTime());
                }
                hideProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                Log.e("TAGG", "OnError Called " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void getUpdatedData() {

        Log.e("TAG", "getUpdatedData called");
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);

        //showProgressDialog("Updating Data...");
        JsonObject _object = new JsonObject();

        JsonArray _arr = new JsonArray();
        for (int i = 0; i < filteredList.getObjData().size(); i++) {
            _arr.add(new JsonPrimitive(filteredList.getObjData().get(i).getDno()));
        }
        _object.add("Devices", _arr);

        Call<String> _call = apiInterface.getUpdatedData(_object);
        Log.e("TAG", "getUpdatedData called " + _object.toString());
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                try {
                    AllDataResponseModel main_object = gson.fromJson(response.body(), AllDataResponseModel.class);
                    for (int i = 0; i < main_object.getObjData().size(); i++) {
                        for (int j = 0; j < filteredList.getObjData().size(); j++) {
                            if (main_object.getObjData().get(i).getDno().equals(filteredList.getObjData().get(j).getDno())) {
                                filteredList.getObjData().get(j).setEnable(main_object.getObjData().get(i).isEnable());
                                filteredList.getObjData().get(j).setOnline(main_object.getObjData().get(i).isOnline());
                                filteredList.getObjData().get(j).setSignal(main_object.getObjData().get(i).getSignal());
                                _device_adapter.notifyItemChanged(j);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at getUpdated Data " + e.getMessage());
                }
                String _data = _gson.toJson(filteredList);
                Log.e("TAG", "Data After Update " + _data);
                _utility.putString(room_Id, _data);
//                Toast.makeText(RoomActivity.this, main_object.getObjData().size() + " Received In Updates!", Toast.LENGTH_SHORT).show();
                //hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                hideProgressDialog();
            }
        });
    }

    public int getIcon(int _deviceID) {
        switch (_deviceID) {
            case 0:
                return R.drawable.icon_all_new;
            case 1:
                return R.drawable.icon_plug;
            case 2:
                return R.drawable.icon_rgbb_bulb;
            case 3:
                return R.drawable.icon_rgb_bulb;
            case 4:
                return R.drawable.icon_rgb_controller;
            case 5:
                return R.drawable.icon_dull_wall;
            case 6:
                return R.drawable.icon_quad_in_wall;
            case 10:
                return R.drawable.icon_hub;
            case 11:
                return R.drawable.icon_door_sensor;
            case 12:
                return R.drawable.icon_multi_sensor;
            case 7:
                return R.drawable.icon_dual_dimmer;
            case 13:
                return R.drawable.icon_smoke_sensor;
            case 14:
                return R.drawable.icon_touch;
            case 8:
                return R.drawable.icon_single_dimmer;
            case 9:
                return R.drawable.icon_single_in_wall;
            case 15:
                return R.drawable.icon_fan;
            case 16:
                return R.drawable.icon_htm;
            case 17:
                return R.drawable.icon_pir_sensor;
        }
        return R.drawable.icon_rgb_bulb;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.e("TAG_ROOM_ACTIVITY", "OnDestroy Called");
            String _data = _utility.getString(room_Id);
            Gson gson = new Gson();
            AllDataResponseModel _all_data = gson.fromJson(_data, AllDataResponseModel.class);
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                try {
                    new PahoMqttClient().unSubscribe(Constants.GeneralpahoMqttClient, "d/" + _all_data.getObjData().get(i).getDno() + "/#");
                } catch (Exception e) {
                    Log.e("TAG", "Exception at unsubscribe " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at onDestroy " + e.getMessage());
        }
    }

    public void finish(View view) {
        Log.wtf("DESCTRUCT_CALL", "SUCCESS");
        RoomActivity.this.finish();
    }

    public void pushLog(String data) {
        File logFile = new File("/data/data/com.wekex.apps.homeautomation/smarty_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            String deviceNo = jsonObject.getString("dno");
            JSONObject object = jsonObject.getJSONObject("d1");
            boolean deviceStatus = object.getBoolean("state");

            Time t = new Time(Time.getCurrentTimezone());
            t.setToNow();
            String date1 = t.format("%Y-%m-%d");

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String var = dateFormat.format(date);
            String fetch = date1 + "|" + var;

            String stat;
            if (deviceStatus)
                stat = "Device Turned ON";
            else
                stat = "Device Turned OFF";

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(fetch).append(" >> ").append(deviceNo).append(" : ").append(stat).append("\n");
            buf.newLine();
            buf.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String getOfflineData(String userID) {
        File file = new File("/data/data/com.wekex.apps.homeautomation/", userID + ".txt");

        StringBuilder text = new StringBuilder();
        String data = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                data = text.append(line).toString();
                //text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}