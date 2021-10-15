package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.HomeActivity;
import com.wekex.apps.homeautomation.Interfaces.RuleOperationListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.RuleListAdapter;
import com.wekex.apps.homeautomation.model.RuleListModel;
import com.wekex.apps.homeautomation.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RuleListActivity extends AppCompatActivity implements View.OnClickListener, RuleOperationListener {

    ImageView iv_add_rule;
    LinearLayout ll_empty_layout;
    RecyclerView rvRuleList;
    RuleListAdapter _adapter;
    RuleOperationListener _listener;
    ArrayList<RuleListModel.Rules> _lstRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_list);
        setupToolbar();
        setup();
        getAllRule();
    }

    void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.rules));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    void setup() {
        ll_empty_layout = findViewById(R.id.ll_empty_layout);
        rvRuleList = findViewById(R.id.rvRuleList);
        rvRuleList.setLayoutManager(new LinearLayoutManager(this));
        iv_add_rule = findViewById(R.id.iv_add_rule);
        iv_add_rule.setOnClickListener(this::onClick);
        _listener = this;
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_rule: {
                startActivityForResult(new Intent(RuleListActivity.this, AddRulesActivity.class), 100);
            }
            break;
        }
    }

    public void getAllRule() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //PreferencesHelper.setAllDevices(SelectableDeviceList.this, null);
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/getRule?dno=&UID=" + Constants.savetoShared(this).getString(Constants.USER_ID, "NA");
        Call<String> _call = apiInterface.getRoomDevice(url);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.wtf("RESPONSE_OF_DEVICES_AS_PER_ROOM", response.body());
                    Gson _gson = new Gson();

                    RuleListModel _model = _gson.fromJson(response.body(), RuleListModel.class);
                    _lstRules = _model.getRuleList();
                    _adapter = new RuleListAdapter(RuleListActivity.this, _lstRules, _listener);
                    rvRuleList.setAdapter(_adapter);
                    if (_lstRules != null && _lstRules.size() > 0) {
                        rvRuleList.setVisibility(View.VISIBLE);
                        ll_empty_layout.setVisibility(View.GONE);
                    } else {
                        rvRuleList.setVisibility(View.GONE);
                        ll_empty_layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void editRules(int pos) {
        Intent _intent = new Intent(RuleListActivity.this, AddRulesActivity.class);
        _intent.putExtra("ruleID", _lstRules.get(pos).getID());
        _intent.putExtra("dno", _lstRules.get(pos).getDno());
        _intent.putExtra("sid", _lstRules.get(pos).getTriggerScene());
        _intent.putExtra("ftime", _lstRules.get(pos).getF_Time());
        _intent.putExtra("ttime", _lstRules.get(pos).getT_time());
        startActivityForResult(_intent, 100);
    }

    @Override
    public void delRules(int pos) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage("Do you really want to delete?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        delRule(_lstRules.get(pos).getID(), pos);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void delRule(String ruleID, int position) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //PreferencesHelper.setAllDevices(SelectableDeviceList.this, null);
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/delRule?ID=" + ruleID;
        Log.e("TAG", "Delete Rules URL :: " + url);
        Call<String> _call = apiInterface.getRoomDevice(url);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.wtf("RESPONSE_OF_DEVICES_AS_PER_ROOM", response.body());
                    try {
                        JSONObject json = new JSONObject(String.valueOf(response.body()));
                        Log.e(" responseee ", json.toString());

                        if (json.has("success") && json.getBoolean("success")) {
                            progressDialog.dismiss();
                            Toast.makeText(RuleListActivity.this, "Rule Deleted Successfully", Toast.LENGTH_SHORT).show();
                            _lstRules.remove(position);
                            _adapter.notifyItemRemoved(position);
                            _adapter.notifyDataSetChanged();
                            if (_lstRules.size() == 0) {
                                rvRuleList.setVisibility(View.GONE);
                                ll_empty_layout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RuleListActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(RuleListActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getAllRule();
        }
    }
}