package com.wekex.apps.homeautomation.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SceneListAdapterNew;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.SceneListModel;
import com.wekex.apps.homeautomation.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAccessories extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    String scenee;
    String roomID = "";
    private RecyclerView listView;
    private SceneListAdapterNew adapter;
    AppCompatCheckBox cb_all;
    ImageView iv_tick;
    Gson _gson = new Gson();
    boolean isFromScene = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accessories);
        setupToolbar();
        try {
            iv_tick = findViewById(R.id.iv_tick);
            iv_tick.setImageResource(R.drawable.tick);
            iv_tick.setOnClickListener(this::onClick);
            cb_all = findViewById(R.id.cb_all);
            cb_all.setVisibility(View.GONE);
            if (getIntent().hasExtra("Devices"))
                this.scenee = getIntent().getStringExtra("Devices");
            if (getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase("FromScene")) {
                isFromScene = true;
                cb_all.setVisibility(View.VISIBLE);
            }
            Log.wtf("SCENEE_INTENT_EXTRA", this.scenee);
            roomID = getIntent().getStringExtra("roomID");
            listView = findViewById(R.id.scene_listview);
            listView.setLayoutManager(new GridLayoutManager(this, 2));
            getDevices();
            cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        checkAll(true);
                    else
                        checkAll(false);
                }
            });
            _all_data_temp = new AllDataResponseModel();
            //Add previously selected list if received from Intent
            if (scenee != null && !scenee.isEmpty() && !scenee.equalsIgnoreCase("new")) {
                AllDataResponseModel _selectedList = _gson.fromJson(scenee, AllDataResponseModel.class);
                for (int i = 0; i < _selectedList.getObjData().size(); i++) {
                    _all_data_temp.getObjData().add(_selectedList.getObjData().get(i));
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at setup List " + e.getMessage());
        }
    }

    private void checkAll(boolean isChecked) {
        try {
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (isChecked)
                    _all_data.getObjData().get(i).setChecked(true);
                else
                    _all_data.getObjData().get(i).setChecked(false);
            }
            adapter.notifyItemRangeChanged(0, _all_data.getObjData().size());
        } catch (Exception e) {
            Log.e("TAG", "Exception at checkAll " + e.getMessage());
        }
    }

    void setupToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.setup_accesories);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    public void getDevices() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //PreferencesHelper.setAllDevices(SelectableDeviceList.this, null);
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = APIClient.BASE_URL + "/api/Get/getRoomDevice?UID=" + user + "&room=" + roomID;
        Log.wtf("DEVICES_AS_PER_ROOM", url);
        Call<String> _call = apiInterface.getRoomDevice(url);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
//                    PreferencesHelper.setAllDevices(SelectableDeviceList.this, response.body());
                    setUpList(response.body());
                    Log.wtf("RESPONSE_OF_DEVICES_AS_PER_ROOM", response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
            }
        });
    }

    AllDataResponseModel _all_data;
    AllDataResponseModel _all_data_temp;

    private void setUpList(String dataSet) {
        try {
            _all_data = _gson.fromJson(dataSet, AllDataResponseModel.class);
            Log.e("TAG", "All Data while setup " + _all_data.getObjData().size());
            JSONObject object = new JSONObject(dataSet);
            JSONArray jsonArray = object.getJSONArray("data");
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (_all_data.getObjData().get(i).getDtype() != 0) {
                    _all_data.getObjData().get(i).setDrawable(getIcon(_all_data.getObjData().get(i).getDtype()));
                }
            }

            for (int i = 0; i < _all_data_temp.getObjData().size(); i++) {
                for (int j = 0; j < _all_data.getObjData().size(); j++) {
                    if (_all_data.getObjData().get(j).getDno().equalsIgnoreCase(_all_data_temp.getObjData().get(i).getDno())) {
                        _all_data.getObjData().get(j).setChecked(true);
                        break;
                    }
                }
            }

            adapter = new SceneListAdapterNew(this, _all_data, _listener, true);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setup Json " + e.toString());
            e.printStackTrace();
        }
    }

    public int getIcon(int type) {
        try {
            switch (type) {
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
        } catch (Exception e) {
            Log.e("TAG", "Exception at drawable " + e.getMessage());
        }
        return R.drawable.icon_rgb_bulb;
    }

    View.OnClickListener _listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int _pos = (int) view.getTag();
            if (_all_data.getObjData().get(_pos).isChecked()) {
                //Remove from received list
                for (int i = 0; i < _all_data_temp.getObjData().size(); i++) {
                    if (_all_data_temp.getObjData().get(i).getDno().equalsIgnoreCase(_all_data.getObjData().get(_pos).getDno())) {
                        _all_data_temp.getObjData().remove(i);
                        Toast.makeText(AddAccessories.this, "Removed " + _all_data.getObjData().get(_pos).getDno(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            if (!isFromScene) {
                for (int i = 0; i < _all_data.getObjData().size(); i++) {
                    if (i == _pos) {
                        _all_data.getObjData().get(_pos).setChecked(!_all_data.getObjData().get(_pos).isChecked());
                    } else
                        _all_data.getObjData().get(i).setChecked(false);
                }

                adapter.notifyItemRangeChanged(0, _all_data.getObjData().size());
            } else {
                _all_data.getObjData().get(_pos).setChecked(!_all_data.getObjData().get(_pos).isChecked());
                adapter.notifyItemChanged(_pos);
            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_tick: {
                setupList();
            }
            break;
        }
    }

    void setupList() {
        try {
            //DOUBLING ISSUE
            for (int i = 0; i < _all_data.getObjData().size(); i++) {
                if (_all_data.getObjData().get(i).isChecked()) {
                    boolean isAllreadyAdded = false;
                    for (int j = 0; j < _all_data_temp.getObjData().size(); j++) {
                        if (_all_data_temp.getObjData().get(j).getDno().equalsIgnoreCase(_all_data.getObjData().get(i).getDno())) {
                            isAllreadyAdded = true;
                            break;
                        }
                    }
                    Log.e("TAG", "DNO " + _all_data.getObjData().get(i) + "<>isAllreadyAdded<>" + isAllreadyAdded);
                    if (!isAllreadyAdded)
                        _all_data_temp.getObjData().add(_all_data.getObjData().get(i));
                }
            }
            Gson _gson = new Gson();
            String _str_data = _gson.toJson(_all_data_temp);
            Log.e("TAG", "Selected Data " + _str_data + " <> " + _all_data_temp.getObjData().size());
            Intent _intent = new Intent();
            _intent.putExtra("data", _str_data);
            setResult(RESULT_OK, _intent);
            finish();
        } catch (Exception e) {
            Log.e("TAG", "Exception at click tick " + e.getMessage());
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
}