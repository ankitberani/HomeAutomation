package com.wekex.apps.homeautomation.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SelectSceneAdapter;
import com.wekex.apps.homeautomation.adapter.SelectedDeviceForRuleAdapter;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddRulesActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_add_device, ll_devices, ll_then_condition;
    LinearLayout ll_devices_then;
    TextView tv_device_name, tv_scene_name;
    public final int REQUEST_CODE = 105;
    String selectedString = "";
//    RecyclerView rv_seleted_device;

    String _data = "";
    String _data_then = "";
    TextView tv_device_count;
    private ProgressDialog progressDialog;
    private String selectedScenID = "";
    private String selectedDno = "";
    ImageView iv_add_item, ivDelDevice, iv_delete_scene;

    boolean isFromUpdate;
    String ruleID = "";
    String ftime = "";
    String ttime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rules);

        setup();
//        rv_seleted_device = findViewById(R.id.rv_selected_devices);
        ll_devices = findViewById(R.id.ll_devices);
//        tv_device_count = findViewById(R.id.tv_device_count);

        ll_then_condition = findViewById(R.id.ll_then_condition);
        ll_then_condition.setOnClickListener(this::onClick);

        iv_delete_scene = findViewById(R.id.iv_delete_scene);
        ivDelDevice = findViewById(R.id.ivDelDevice);
        iv_add_item = findViewById(R.id.iv_add_item);
        iv_add_item.setOnClickListener(this::onClick);
        iv_delete_scene.setOnClickListener(this::onClick);
        ivDelDevice.setOnClickListener(this::onClick);

        this.progressDialog = new ProgressDialog(this);
        if (getIntent() != null && getIntent().hasExtra("ruleID")) {
            ruleID = getIntent().getStringExtra("ruleID");
            selectedDno = getIntent().getStringExtra("dno");
            selectedScenID = getIntent().getStringExtra("sid");
            ftime = getIntent().getStringExtra("ftime");
            ttime = getIntent().getStringExtra("ttime");
            isFromUpdate = true;
            getSceneFromServer();
            getDeviceNamefromDno();
            ll_add_device.setVisibility(View.GONE);
            ll_then_condition.setVisibility(View.GONE);
            ivDelDevice.setVisibility(View.GONE);
            iv_delete_scene.setVisibility(View.VISIBLE);
        }
        setupToolbar();
    }

    void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (isFromUpdate)
                toolbar.setTitle(getString(R.string.update));
            else
                toolbar.setTitle(getString(R.string.new_rules));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    void setup() {
        ll_add_device = findViewById(R.id.ll_add_device);
        ll_add_device.setOnClickListener(this);

        tv_scene_name = findViewById(R.id.tv_scene_name);
        tv_device_name = findViewById(R.id.tv_device_name);
        ll_devices_then = findViewById(R.id.ll_devices_then);
        ll_devices_then.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_add_device: {
                showRoomLists(false);
            }
            break;
            case R.id.ll_then_condition: {
//                showBottomSheetDialog();
                Intent _intent = new Intent(AddRulesActivity.this, SelectSceneActivity.class);
                startActivityForResult(_intent, 100);
            }
            break;
            case R.id.iv_add_item: {
                if (selectedDno.isEmpty() || selectedScenID.isEmpty()) {
                    Toast.makeText(this, "If and Then Condition required", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    showTimePicker();
//                    AddNewRule();
                } catch (Exception e) {
                    Log.e("TAG", "Exception at add rule " + e.toString());
                }
            }
            break;
            case R.id.iv_delete_scene: {
                selectedScenID = "";
                ll_devices_then.setVisibility(View.GONE);
                ll_then_condition.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.ivDelDevice: {
                selectedDno = "";
                ll_devices.setVisibility(View.GONE);
                ll_add_device.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    void showTimePicker() {
        final Dialog dialog = new Dialog(AddRulesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.set_time_rule);
        TimePicker timePickerFrm = dialog.findViewById(R.id.timePicker1);
        timePickerFrm.setIs24HourView(true);
        TimePicker timePickerTo = dialog.findViewById(R.id.timePicker2);
        timePickerTo.setIs24HourView(true);

        Button btn_addRule = dialog.findViewById(R.id.btn_addrule);
        if (isFromUpdate) {
            btn_addRule.setText(R.string.update);
            try {
                if (ftime != null && ftime.contains(":")) {
                    StringTokenizer token = new StringTokenizer(ftime, ":");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerFrm.setHour(Integer.parseInt(token.nextToken()));
                        timePickerFrm.setMinute(Integer.parseInt(token.nextToken()));
                    }
                }
                if (ttime != null && ttime.contains(":")) {
                    StringTokenizer token = new StringTokenizer(ttime, ":");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePickerTo.setHour(Integer.parseInt(token.nextToken()));
                        timePickerTo.setMinute(Integer.parseInt(token.nextToken()));
                    }
                }
            } catch (Exception e) {
            }
        }
        btn_addRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String frmTime = timePickerFrm.getCurrentHour() + ":" + timePickerFrm.getMinute();
                String toTime = timePickerTo.getCurrentHour() + ":" + timePickerTo.getMinute();
                try {
                    AddUpdateRule(frmTime, toTime);
                } catch (Exception e) {
                    Log.e("TAG", "Exception at addRule " + e.toString());
                }
                dialog.dismiss();
            }
        });
        ImageView ivClose = dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        TextView tv_all_cndition_met = bottomSheetDialog.findViewById(R.id.tv_all_cndition_met);
        TextView tv_any_cndition_met = bottomSheetDialog.findViewById(R.id.tv_any_cndition_met);

        tv_any_cndition_met.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
//                showRoomLists(true);
                Intent _intent = new Intent(AddRulesActivity.this, SelectSceneActivity.class);
                startActivityForResult(_intent, 100);
            }
        });

        tv_all_cndition_met.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
//                showRoomLists(true);
                Intent _intent = new Intent(AddRulesActivity.this, SelectSceneActivity.class);
                startActivityForResult(_intent, 100);
            }
        });

        /*LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
        LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
        LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
        LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);*/

        bottomSheetDialog.show();
    }

    public void showRoomLists(boolean isfromThen) {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.scene_picker, null, false);
        dialog.setContentView(view);

        LinearLayout sceneHolder = view.findViewById(R.id.picker_list);
        sceneHolder.removeAllViews();
        View inflate = LayoutInflater.from(this).inflate(R.layout.scene_picker_item, null, false);
        TextView headings = inflate.findViewById(R.id.schedules_head);
        headings.setText(getResources().getString(R.string.select_room_msg_rule));
        headings.setVisibility(View.VISIBLE);
        headings.setTextSize(16);
        headings.setTextColor(getResources().getColor(R.color.black));
        TextView schedules_name_ = inflate.findViewById(R.id.schedules_name);
        schedules_name_.setVisibility(View.GONE);
        sceneHolder.addView(inflate);

        String jstr = Constants.savetoShared(AddRulesActivity.this).getString(Constants.ROOMS, "null");
        try {
            JSONObject jsonObject = new JSONObject(jstr);
            JSONArray rooms = Constants.getJsonArray(jsonObject, "rooms");
            for (int i = 0; i < rooms.length(); i++) {
                View dli = LayoutInflater.from(this).inflate(R.layout.scene_picker_item, null, false);
                LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);
                TextView schedules_name = dli.findViewById(R.id.schedules_name);
                schedules_name.setText(rooms.getJSONObject(i).get("name").toString());
                schedules_name.setTag(rooms.getJSONObject(i).get("ID").toString());
                dli_parent.setOnClickListener(v -> {
                    Intent intent = new Intent(AddRulesActivity.this, ConfigueRuleDevices.class);
                    intent.putExtra("Devices", "new");
                    intent.putExtra("name", "");
                    intent.putExtra("roomID", (String) schedules_name.getTag());
//                    intent.putExtra("_data", _data);
                    intent.putExtra("_data", "");
                    startActivityForResult(intent, REQUEST_CODE);
                    dialog.dismiss();
                });
                sceneHolder.addView(dli);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE && data != null && data.hasExtra("devices")) {
                _data = data.getStringExtra("devices");
                Log.e("TAG", "Devices List On Act Result " + _data);
                try {
                    JSONObject _main = new JSONObject(_data);
                    JSONArray _array = _main.getJSONArray("Devices");
                    if (_array != null) {
                        String _object = _array.getString(0);
                        JSONObject jsonObject = new JSONObject(_object);
                        selectedDno = jsonObject.getString("dno");
                        Log.e("TAG", "OnActivity Result Array Size " + _array.length() + " DNO " + selectedDno);
                        SelectedDeviceForRuleAdapter _adapter = new SelectedDeviceForRuleAdapter(_array, AddRulesActivity.this);
                        /* LinearLayoutManager HorizontalLayout
                                = new LinearLayoutManager(
                                AddRulesActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false);
                       rv_seleted_device.setLayoutManager(HorizontalLayout);
                        rv_seleted_device.setAdapter(_adapter);*/
                        JSONObject d1 = jsonObject.getJSONObject("d1");
                        Log.e("TAG", "DNO at bind holder " + d1.getString("name"));
                        tv_device_name.setText(d1.getString("name"));
                        ll_devices.setVisibility(View.VISIBLE);
                        ll_add_device.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at onActivityResult " + e.toString());
                }
            } else if (requestCode == 100 && data != null && data.hasExtra("scene")) {
                Log.e("TAG", "Devices List On Act Result " + _data);
                try {
                    String scene = data.getStringExtra("scene");
//                    Toast.makeText(this, scene + "", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Selected Scene " + scene);
                    JSONObject _main = new JSONObject(scene);
                    selectedScenID = _main.getString("ID");
                    ll_devices_then.setVisibility(View.VISIBLE);
                    ll_then_condition.setVisibility(View.GONE);
                    tv_scene_name.setText(_main.getString("Name"));
                } catch (Exception e) {
                    Log.e("TAG", "Exception at onActivityResult " + e.toString());
                }
            }
        }
    }

    public void AddUpdateRule(String frmTime, String toTime) {

        showProgressDialog("Please wait");
        String ruleUrl = APIClient.BASE_URL + "api/Get/AddRule";
        try {
            JSONObject jsonObject = new JSONObject();
            if (isFromUpdate) {
                ruleUrl = APIClient.BASE_URL + "api/Get/EditRule";
                jsonObject.put("ID", ruleID);
            } else
                jsonObject.put("ID", "");
            jsonObject.put("UID", Constants.savetoShared(this).getString(Constants.USER_ID, "NA"));
            jsonObject.put("dno", selectedDno);
            jsonObject.put("F_Time", frmTime);
            jsonObject.put("T_time", toTime);
            jsonObject.put("checkTime", true);
            jsonObject.put("conditionKey", "motion");
            jsonObject.put("conditionOperator", "=");
            jsonObject.put("conditionVal", 1);
            jsonObject.put("TriggerScene", selectedScenID);
            jsonObject.put("Active", true);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Log.e("TAG", "Rules Data " + jsonObject + " \n " + jsonObject.toString() + " addRuleUrl " + ruleUrl);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ruleUrl,
                    jsonObject, response -> {
                hideProgressDialog();
                try {
                    JSONObject json = new JSONObject(String.valueOf(response));
                    Log.e(" responseee ", json.toString());

                    if (json.has("success") && json.getBoolean("success")) {
                        hideProgressDialog();
                        if (isFromUpdate)
                            Toast.makeText(this, "Rule Updated Successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(this, "Rule Added Successfully", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    } else {
                        hideProgressDialog();
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace);

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {

        }
    }

    public void showProgressDialog(String msg) {
        try {
            if (this.progressDialog != null && !this.progressDialog.isShowing()) {
                this.progressDialog.setMessage(msg);
                this.progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getSceneFromServer() {
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        final String url = APIClient.BASE_URL + "/api/Get/getScene?UID=" + user;
        showProgressDialog(getResources().getString(R.string.please_wait));
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
                        ArrayList<scene_model.Scene> _mainList = scene_models.getLst_scene();
                        for (int i = 0; i < _mainList.size(); i++) {
                            if (_mainList.get(i).getID().equalsIgnoreCase(selectedScenID)) {
                                ll_devices_then.setVisibility(View.VISIBLE);
                                ll_devices_then.setVisibility(View.VISIBLE);
                                tv_scene_name.setText(_mainList.get(i).getName());
                                break;
                            }
                        }
                        Log.e("TAGGG", "Size of scene Before " + scene_models.getLst_scene().size());
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at onNext " + e.getMessage(), e);
                }
                hideProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError  called " + e.toString(), e);
                hideProgressDialog();
            }

            @Override
            public void onComplete() {
                hideProgressDialog();
            }
        });
    }

    String getDeviceNamefromDno() {
        try {
            Gson _gson = new Gson();
            AllDataResponseModel _all_data = _gson.fromJson(PreferencesHelper.getAllDevices(this), AllDataResponseModel.class);
            Log.e("TAG", "All Data Size " + _all_data.getObjData().size());
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (_all_data.getObjData().get(i).getDno().equalsIgnoreCase(selectedDno)) {
                    tv_device_name.setText(_all_data.getObjData().get(i).getObjd1().getName());
                    ll_devices.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at getDeviceType " + e.toString());
        }
        return selectedDno;
    }
}