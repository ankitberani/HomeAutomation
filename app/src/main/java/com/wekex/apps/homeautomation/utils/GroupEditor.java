package com.wekex.apps.homeautomation.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wekex.apps.homeautomation.BaseActivity;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.FillDeviceAdapter;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GroupEditor extends BaseActivity {
    int GET_DEVICE = 1;

    /* renamed from: ID */
    String f183ID;
    String TAG = "GroupEditor";
    String UID;
    LinearLayout configHolder;
    JSONObject configedJSON;
    LinearLayout deviceConfig;
    private LinearLayout deviceHolder;
    int deviceNo = 0;
    TextView devicename;
    JSONArray devices;
    TextView done;
    EditText sceneName;
    String sceneUrl;
    String scenee;

    RecyclerView rv_device_list;
    String room_Id;

    Toolbar _toolbar;
    AllDataResponseModel objAlldata;

    boolean isFromEdit = false;

    String grp_id = "";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_group_editor);
        this.deviceHolder = (LinearLayout) findViewById(R.id.deviceHolder);
        this.sceneName = (EditText) findViewById(R.id.sceneName);
        this.devices = new JSONArray();

        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        _toolbar.setTitle("Create New Group");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.scenee = getIntent().getStringExtra("Devices");
        this.room_Id = getIntent().getStringExtra("room_Id");
        rv_device_list = (RecyclerView) findViewById(R.id.rv_device_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_device_list.setLayoutManager(mLayoutManager);

        final String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        Log.e("TAGGG", "Json String " + jsonString);

        Gson gson = new Gson();
        AllDataResponseModel obj_lst = null;
        FillDeviceAdapter adapter = null;
        try {
            objAlldata = gson.fromJson(jsonString, AllDataResponseModel.class);
            Toast.makeText(this, objAlldata.getObjData().size() + " Rooms " + objAlldata.getLst_rooms().size() + " ", Toast.LENGTH_SHORT).show();

            obj_lst = new AllDataResponseModel();
            ArrayList<data> lst_Data = new ArrayList<>();
            for (int i = 0; i < objAlldata.getObjData().size(); i++) {
                if (objAlldata.getObjData().get(i).getRoom() != null && objAlldata.getObjData().get(i).getDtype() == 2) {
                    if (room_Id.equalsIgnoreCase(objAlldata.getObjData().get(i).getRoom())) {
                        lst_Data.add(objAlldata.getObjData().get(i));
                    }
                }
            }
            obj_lst.setObjData(lst_Data);

            adapter = new FillDeviceAdapter(obj_lst, GroupEditor.this, false, null);
            rv_device_list.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("TAGG", "Exception at  parse " + e.getMessage(), e);
        }


        if (getIntent().hasExtra("Devices")) {
            this.f183ID = UUID.randomUUID().toString();
            this.UID = Constants.savetoShared(this).getString(Constants.USER_ID, "NA");
            this.sceneUrl = APIClient.BASE_URL + "/api/Get/AddGroup";
            return;
        }
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onCreate: ");
        sb.append(this.scenee);
        Log.d(str, sb.toString());
        /*try {
            this.sceneUrl = "http://209.58.164.151:88/api//Get/EditGroup";
            JSONObject jsonObject = new JSONObject(this.scenee);
            this.sceneName.setText(jsonObject.getString("Name"));
            this.devices = jsonObject.getJSONArray("Devices");
            this.f183ID = jsonObject.getString("ID");
            this.UID = jsonObject.getString(Constants.USER_ID);
            addToLIst();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        try {
            if (getIntent().hasExtra("FromEdit")) {
                this.UID = Constants.savetoShared(this).getString(Constants.USER_ID, "NA");
                isFromEdit = true;
                String _name = getIntent().getStringExtra("name");
                grp_id = getIntent().getStringExtra("grp_id");
                sceneName.setText(_name);
                _toolbar.setTitle("Edit " + _name);
                ArrayList<String> _lst = getIntent().getStringArrayListExtra("devices_lst");
                if (obj_lst != null) {
                    for (int i = 0; i < _lst.size(); i++) {
                        for (int j = 0; j < obj_lst.getObjData().size(); j++) {
                            if (_lst.get(i).equalsIgnoreCase(obj_lst.getObjData().get(j).getDno())) {
                                obj_lst.getObjData().get(j).setChecked(true);
                                if (adapter != null) {
                                    adapter.notifyItemChanged(j);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TAGG", "Exception " + e.getMessage(), e);
        }
    }

    private void addToLIst() {
        this.deviceHolder.removeAllViews();
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("addToLIst: ");
        sb.append(this.devices);
        Log.d(str, sb.toString());
        int i = 0;
        while (i < this.devices.length()) {
            try {
                String str2 = this.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("onCreate: ");
                sb2.append(this.devices.getString(i));
                Log.d(str2, sb2.toString());
                JSONObject deviceInfo = Constants.getDeviceById(this.devices.getString(i), this);
                if (deviceInfo != null) {
                    String str3 = this.TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("onCreate: ");
                    sb3.append(deviceInfo.toString());
                    Log.d(str3, sb3.toString());
                    View dli = LayoutInflater.from(this).inflate(R.layout.schedules_list_item, null, false);
                    LinearLayout dli_parent = (LinearLayout) dli.findViewById(R.id.dli_parent);
                    TextView schedules_name = (TextView) dli.findViewById(R.id.schedules_name);
                    TextView dli_room = (TextView) dli.findViewById(R.id.dli_room);
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Room : ");
                    sb4.append(Constants.savetoShared(this).getString(Constants.jsonObjectreader(deviceInfo.toString(), DtypeViews.room), "Null"));
                    dli_room.setText(sb4.toString());
                    schedules_name.setText(Constants.jsonobjectSTringJSON(deviceInfo, "name"));
                    final int finalI = i;
                    dli_parent.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            GroupEditor.this.deviceNo = finalI;
                        }
                    });
                    this.deviceHolder.addView(dli);
                }
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void getDevice(View view) {
        Intent intent = new Intent(this, SelectableDeviceList.class);
        intent.putExtra("RoomId", "NA");
        intent.putExtra("DeviceType", "2");
        startActivityForResult(intent, this.GET_DEVICE);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.GET_DEVICE) {
            if (resultCode == -1) {
                String result = data.getStringExtra("deviceInfo");
                String str = this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onActivityResult: ");
                sb.append(result);
                Log.d(str, sb.toString());
                try {
                    this.devices.put(this.devices.length(), Constants.jsonObjectreader(result, DtypeViews.dno));
                    addToLIst();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == 0) {
                Toast.makeText(this, "No Schedules Added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void typeOne(LinearLayout configHolder2, String result) {
        String dno = null;
        Boolean state = Boolean.valueOf(true);
        try {
            JSONObject device = new JSONObject(result);
            state = Boolean.valueOf(device.getJSONObject("d1").getBoolean("state"));
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("typeOne: ");
            sb.append(state);
            Log.d(str, sb.toString());
            dno = device.getString(DtypeViews.dno);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view = LayoutInflater.from(this).inflate(R.layout.scene_config, null, false);
        ((TextView) view.findViewById(R.id.device3)).setText("Device No. 1");
        Switch aSwitch = (Switch) view.findViewById(R.id.switch1);
        final String dno2 = dno;
        aSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    GroupEditor.this.configedJSON = new JSONObject();
                    JSONObject jsonInternal = new JSONObject();
                    GroupEditor.this.configedJSON.put(DtypeViews.dno, dno2);
                    jsonInternal.put(NotificationCompat.CATEGORY_STATUS, isChecked);
                    GroupEditor.this.configedJSON.put("d1", jsonInternal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String str = GroupEditor.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onCheckedChanged: ");
                sb.append(GroupEditor.this.configedJSON.toString());
                Log.d(str, sb.toString());
            }
        });
        if (!this.scenee.equals("new")) {
            try {
                JSONObject d = Constants.stringToJsonObject(this.devices.get(this.deviceNo).toString());
                String str2 = this.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(d.toString());
                sb2.append(" typeOne: aa ");
                Log.d(str2, sb2.toString());
                state = Boolean.valueOf(d.getJSONObject("d1").getBoolean(NotificationCompat.CATEGORY_STATUS));
                String str3 = this.TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append(d.toString());
                sb3.append(" typeOne: aa ");
                sb3.append(state);
                Log.d(str3, sb3.toString());
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        aSwitch.setChecked(state.booleanValue());
        configHolder2.addView(view);
    }

    public void done(View view) {
        String sn = this.sceneName.getText().toString();
        try {
            if (sn.isEmpty()) {
                sceneName.setError(getResources().getString(R.string.require));
            } else if (getSelectedDevice() == null) {
                Toast.makeText(this, "Select Atleast One Device.", Toast.LENGTH_SHORT).show();
            } else {
                addScene(getSelectedDevice());
            }
        } catch (Exception e) {
            String str2 = this.TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("done: ");
            sb2.append(e.getMessage());
            Log.d(str2, sb2.toString());
        }
    }

    public void close(View view) {
        finish();
    }


    void addScene(JsonObject jsonObject) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<SuccessResponse> observable;

        if (!isFromEdit) {
            observable = apiInterface.AddGroup(jsonObject);
            sceneName.setText("Adding..");
        } else {
            jsonObject.addProperty("ID", grp_id);
            observable = apiInterface.UpdateGroup(jsonObject);
            sceneName.setText("Updating..");
        }
        sceneName.setEnabled(false);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SuccessResponse successResponse) {
                try {
                    Log.e("TAGGGG", "New Group created " + successResponse.getSuccess());
                    if (successResponse.getSuccess())
                        finish();
                    else
                        Toast.makeText(GroupEditor.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("TAGGGG", "New Group Exception at create group " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {

                Log.e("TAGGGG", "New Group OnError " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    JsonObject getSelectedDevice() {

        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();
        /*array.add(new JsonPrimitive("one"));
        array.add(new JsonPrimitive("two"));
        array.add(new JsonPrimitive("three"));*/
//        obj.add("main", array);

        obj.addProperty("UID", UID);
        obj.addProperty("Name", sceneName.getText().toString());
        String selected = "";

        for (int i = 0; i < objAlldata.getObjData().size(); i++) {
            if (objAlldata.getObjData().get(i).isChecked()) {
                array.add(new JsonPrimitive(objAlldata.getObjData().get(i).getDno()));
            }
        }
        if (array.size() == 0)
            return null;
        obj.add("Devices", array);
        selected = obj.toString();
        Log.e("TAGGG", "SElected Arrray " + selected);
        return obj;
    }
}
