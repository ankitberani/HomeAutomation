package com.wekex.apps.homeautomation.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.adapter.SelectedDeviceForRuleAdapter;
import com.wekex.apps.homeautomation.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class AddRulesActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_add_device, ll_devices, ll_then_condition;
    LinearLayout ll_devices_then;
    public final int REQUEST_CODE = 105;
    String selectedString = "";
    RecyclerView rv_seleted_device;
    RecyclerView rv_selected_devices_then;
    String _data = "";
    String _data_then = "";
    TextView tv_device_count;

    TextView tv_device_count_then;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rules);
        setupToolbar();
        setup();
        rv_seleted_device = findViewById(R.id.rv_selected_devices);
        rv_selected_devices_then = findViewById(R.id.rv_selected_devices_then);
        ll_devices = findViewById(R.id.ll_devices);
        tv_device_count = findViewById(R.id.tv_device_count);
        ll_then_condition = findViewById(R.id.ll_then_condition);

        ll_then_condition.setOnClickListener(this::onClick);
    }

    void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
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

        ll_devices_then = findViewById(R.id.ll_add_device);
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
                showBottomSheetDialog();
            }
            break;

        }
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
                showRoomLists(true);
            }
        });

        tv_all_cndition_met.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                showRoomLists(true);
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
                    intent.putExtra("_data", _data);
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
                        Log.e("TAG", "OnActivity Result Array Size " + _array.length());
                        SelectedDeviceForRuleAdapter _adapter = new SelectedDeviceForRuleAdapter(_array, AddRulesActivity.this);
                        LinearLayoutManager HorizontalLayout
                                = new LinearLayoutManager(
                                AddRulesActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false);
                        rv_seleted_device.setLayoutManager(HorizontalLayout);
                        rv_seleted_device.setAdapter(_adapter);
                        ll_devices.setVisibility(View.VISIBLE);
                        tv_device_count.setText(_array.length() + " Added");
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at onActivityResult " + e.toString());
                }
            } else if (requestCode == 500 && data != null && data.hasExtra("devices")) {
                _data = data.getStringExtra("devices");
                Log.e("TAG", "Devices List On Act Result " + _data);
                try {
                    JSONObject _main = new JSONObject(_data);
                    JSONArray _array = _main.getJSONArray("Devices");
                    if (_array != null) {
                        Log.e("TAG", "OnActivity Result Array Size " + _array.length());
                        SelectedDeviceForRuleAdapter _adapter = new SelectedDeviceForRuleAdapter(_array, AddRulesActivity.this);
                        LinearLayoutManager HorizontalLayout
                                = new LinearLayoutManager(
                                AddRulesActivity.this,
                                LinearLayoutManager.HORIZONTAL,
                                false);
                        rv_selected_devices_then.setLayoutManager(HorizontalLayout);
                        rv_selected_devices_then.setAdapter(_adapter);
                        ll_devices_then.setVisibility(View.VISIBLE);
                        tv_device_count_then.setText(_array.length() + " Added");
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at onActivityResult " + e.toString());
                }
            }
        }
    }
}