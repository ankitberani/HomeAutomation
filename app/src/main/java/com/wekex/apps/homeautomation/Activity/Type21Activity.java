package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.RoomActivity;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.GroupsAdapter;
import com.wekex.apps.homeautomation.adapter.SelectGroupsAdapter;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Type21Activity extends AppCompatActivity {

    ImageView iv_add_item;

    SelectGroupsAdapter _adapter;
    scene_model groupObject;


    String dno = "";
    Utility _utility;
    RecyclerView rvSelectedGrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type21);
        setupToolbar();
        rvSelectedGrp = findViewById(R.id.rvGroupsSelected);
        _utility = new Utility(Type21Activity.this);
        iv_add_item = findViewById(R.id.iv_add_item);
        iv_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialogForGroupSelection();
                prepareAndSend();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("dno")) {
            dno = getIntent().getStringExtra("dno");
        }
        getGroupFromServer();
    }

    void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.select_groups));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void prepareAndSend() {

        if (groupObject != null && _adapter != null) {
            JSONObject _object_main = new JSONObject();
            JSONObject _object_d1 = new JSONObject();
            JSONArray _arr = new JSONArray();
            String _data = "";
            for (int i = 0; i < groupObject.getLst_scene().size(); i++) {
                if (groupObject.getLst_scene().get(i).isSelected()) {
                    try {
                        _arr.put(groupObject.getLst_scene().get(i).getID());
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at setup Group" + e.toString());
                    }
                }
            }

            try {
                _object_d1.put("groups", _arr);
                _object_main.put("d1", _object_d1);
                _data = _object_main.toString();
                postNewGroups(_data);

                String _array = _arr.toString();
                setArrayInPreferece(_array);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("TAG", "Data Of Groups " + _data);
        }
    }

    void getGroupFromServer() {
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = APIClient.BASE_URL + "/api/Get/getGroup?UID=" + user;
        Log.e("TAG", "getGroupsFromServer called <>" + url);
        Observable<scene_model> observable = apiInterface.getGroups(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<scene_model>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(scene_model scene) {
                if (scene != null) {
                    groupObject = scene;
                    String _data = _utility.getString("group_" + dno);
                    Log.e("TAGGG", "On Next Called " + groupObject.lst_scene.size() + "<>Data " + _data);
//                    Gson _gson = new Gson();
                    try {
                        JSONArray _array = new JSONArray(_data);
                        for (int i = 0; i < scene.getLst_scene().size(); i++) {
                            for (int j = 0; j < _array.length(); j++) {
                                if (scene.getLst_scene().get(i).getID().equals(_array.getString(j))) {
                                    scene.getLst_scene().get(i).setSelected(true);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("TAG", "JSON Exception " + e.getMessage());
                    }
                    _adapter = new SelectGroupsAdapter(groupObject.lst_scene, Type21Activity.this, null);
                    rvSelectedGrp.setLayoutManager(new LinearLayoutManager(Type21Activity.this));
                    rvSelectedGrp.setAdapter(_adapter);
                } else
                    Toast.makeText(Type21Activity.this, "Group Null", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGGG", "Exception at getGroup " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {
                Log.e("TAGGG", "OnComplete Call");
            }
        });
    }

    public void postNewGroups(String infos) {
        Log.e("TAG", "postNewGroups At Infor " + infos);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, infos,
                    1,
                    "d/" + dno + "/sub");
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            Log.e("TAG", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d("TAG", e.getMessage());
            e.printStackTrace();
        }
    }

    public void setArrayInPreferece(String groupList) {
        Log.e("TAG", "Selected Group List " + groupList);
        _utility.putString("group_" + dno, groupList);
    }
}