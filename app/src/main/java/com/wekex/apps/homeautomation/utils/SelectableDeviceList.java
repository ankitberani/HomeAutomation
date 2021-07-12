package com.wekex.apps.homeautomation.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wekex.apps.homeautomation.Interfaces.SelectableListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SceneListAdapter;
import com.wekex.apps.homeautomation.model.SceneListModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectableDeviceList extends AppCompatActivity implements SelectableListener {
    static String lastTab;
    String roomId = "";
    String TAG = "DeviceLIstActivity";
    LinearLayout deviceHolder;
    String deviceType = "0";
    LinearLayout deviceTypeHolder;
    Button btnProceed;
    /* access modifiers changed from: private */
    public int showDeviceRype;

    String hashStr = "";
    JSONArray hashData;

    Toolbar _toolbar;

    private ListView listView;
    private ArrayList<SceneListModel> listItems = new ArrayList<>();
    private SceneListAdapter adapter;
    private EditText etSearch;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        roomId = getIntent().getStringExtra("RoomId");
        Log.wtf("ROOM_ID_FOR_ACTIVITY", roomId);

        _toolbar = findViewById(R.id.toolbar);
        _toolbar.setTitle("All Devices");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(_toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etSearch = findViewById(R.id.et_search_scene);
        listView = findViewById(R.id.scene_listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        btnProceed = findViewById(R.id.scene_btn_proceed);

        btnProceed.setOnClickListener(View -> {
            try {
                hashData = new JSONArray(hashStr);
                JSONObject finalObj = new JSONObject();
                finalObj.put("UID", PreferencesHelper.getUserId(this));
                finalObj.put("Name", "Test1");
                finalObj.put("Devices", new JSONArray());
                JSONArray jsonArray = finalObj.getJSONArray("Devices");
                for (int i = 0; i < hashData.length(); i++) {
                    jsonArray.put(hashData.getString(i));
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("deviceInfo", finalObj.toString());
                setResult(Activity.RESULT_OK, returnIntent);
                Log.wtf("FINAL_FORMED_JSON", String.valueOf(finalObj));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        _toolbar.setNavigationOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });

//        this.deviceHolder = findViewById(R.id.deviceHolder);
//        this.deviceTypeHolder = findViewById(R.id.deviceTypeHolder);
//        this.RoomId = getIntent().getStringExtra("RoomId");
//        this.deviceType = getIntent().getStringExtra("DeviceType");
//
//        if (this.deviceType == null) {
//            this.deviceType = "0";
//        }
//
//        DtypeViews.getGetDevice(this);
//        final String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
//        String deviceTypes = Constants.savetoShared(this).getString(Constants.DEVICETYPES, "NA");
//        String str = this.TAG;
//        String sb = "deviceTypes: " + deviceTypes;
//        Log.d(str, sb);
//        updateUI(jsonString);
//        Log.wtf("JSON_STRING", jsonString);
//        if (!deviceTypes.equals("NA")) {
//            String str2 = this.TAG;
//            String sb2 = "onCreate: " + deviceTypes;
//            Log.d(str2, sb2);
//            try {
//                JSONArray jarray = new JSONArray(deviceTypes);
//                LayoutInflater from = LayoutInflater.from(this);
//                ViewGroup viewGroup = null;
//                int i = R.layout.device_type_card;
//                boolean z = false;
//                View view0 = from.inflate(R.layout.device_type_card, null, false);
//                int i2 = R.id.deviceTypeId;
//                TextView deviceTypeId0 = view0.findViewById(R.id.deviceTypeId);
//                String tag0 = "myTag" + 0;
//                String str3 = this.TAG;
//                String sb4 = "onCreate: " + tag0;
//                Log.e(str3, sb4);
//                deviceTypeId0.setTag(tag0);
//                deviceTypeId0.setText("All Devices");
//                deviceTypeId0.setOnClickListener(v -> {
//                    Constants.showDeviceRype = 0;
//                    updateUI(jsonString);
//                    View vv = SelectableDeviceList.this.deviceTypeHolder.findViewWithTag(SelectableDeviceList.lastTab);
//                    if (vv != null) {
//                        vv.setPressed(false);
//                    }
//                    v.setPressed(true);
//                    lastTab = v.getTag().toString();
//                });
//
//                this.deviceTypeHolder.addView(view0);
//                int i3 = 0;
//                while (i3 < jarray.length()) {
//                    String str4 = this.TAG;
//                    String sb5 = jarray.length() + " onCreate: " + i3;
//                    Log.d(str4, sb5);
//                    JSONObject jsonObject = jarray.getJSONObject(i3);
//                    View view = LayoutInflater.from(this).inflate(i, viewGroup, z);
//                    TextView deviceTypeId = view.findViewById(i2);
//                    final int devID = jsonObject.getInt("ID");
//                    String tag = "myTag" + devID;
//                    deviceTypeId.setText(jsonObject.getString("Name"));
//                    deviceTypeId.setTag(tag);
//                    String str5 = this.TAG;
//                    String sb7 = deviceTypeId.getTag() + " onCreate: myTAG " + jsonObject.getString("ID");
//                    Log.d(str5, sb7);
//                    String str6 = this.TAG;
//                    String sb8 = "onCreate: " + tag;
//                    Log.e(str6, sb8);
//                    deviceTypeId.setOnClickListener(v -> {
//                        Constants.showDeviceRype = devID;
//                        SelectableDeviceList.this.updateUI(jsonString);
//                        View vv = SelectableDeviceList.this.deviceTypeHolder.findViewWithTag(SelectableDeviceList.lastTab);
//                        if (vv != null) {
//                            vv.setPressed(false);
//                        }
//                        _toolbar.setTitle(deviceTypeId.getText().toString().trim());
//                        v.setPressed(true);
//                        SelectableDeviceList.lastTab = v.getTag().toString();
//                    });
//                    this.deviceTypeHolder.addView(view);
//                    i3++;
//                    viewGroup = null;
//                    i = R.layout.device_type_card;
//                    z = false;
//                    i2 = R.id.deviceTypeId;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        //mTDTV(1);

//        if (PreferencesHelper.getAllDevices(this) != null)
//            setUpList("");
//        else
        getDevices();

    }

    @Override
    public void selectedData(String data) {
        Log.wtf("SELECTED_DATA_STRING", data);
        hashStr = data;
    }

    private void setUpList(String dataSet) {
        try {
            JSONObject object = new JSONObject(dataSet);
//            if (PreferencesHelper.getAllDevices(this) == null)
//                object = new JSONObject(dataSet);
//            else
//                object = new JSONObject(PreferencesHelper.getAllDevices(this));

            JSONArray jsonArray = object.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = (JSONObject) jsonArray.get(i);
                Log.wtf("JSON_OBJECTS_SCENE", jObj.toString());
                SceneListModel sceneListModel = new SceneListModel();
//                JSONObject jObj = jsonArray.getJSONObject(i);

                JSONObject object1 = jObj.getJSONObject("d1");

                sceneListModel.setName(object1.optString("name"));
                sceneListModel.setStatus(object1.optBoolean("state"));

                sceneListModel.setR(object1.optInt("r"));
                sceneListModel.setG(object1.optInt("g"));
                sceneListModel.setB(object1.optInt("b"));
                sceneListModel.setW(object1.optInt("w"));
                sceneListModel.setWw(object1.optInt("ww"));
                sceneListModel.setBr(object1.optInt("br"));

                sceneListModel.setId(jObj.optString("dno"));
                sceneListModel.setdType(jObj.optInt("dtype"));
                sceneListModel.setRoomId(jObj.optString("room"));

                listItems.add(sceneListModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

        adapter = new SceneListAdapter(this, listItems, this, this);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        listView.setTextFilterEnabled(true);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

//    public void updateUI(String Rdata) {
//        Log.wtf("R_DATA", Rdata);
//        String data = Constants.jsonObjectreader(Rdata, "data");
////        initViews(data);
////        deviceHolder.removeAllViews();
//        Constants.jsonDeviceAdderByRoom(this, Rdata, deviceHolder, room_Id);
//    }

    private void initViews(String data) {
        deviceHolder = findViewById(R.id.deviceHolder);
        //Device Icon and count
        //LinearLayout iconholder = findViewById(R.id.iconholder);
        data = Constants.jsonObjectreader(Constants.savetoShared(this).getString(Constants.ROOMS, Constants.EMPTY), "data");
        String total = HomePage.homePageIconAdder(this, data, deviceHolder, roomId);
    }



    public void getDevices() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //PreferencesHelper.setAllDevices(SelectableDeviceList.this, null);
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        String url = APIClient.BASE_URL + "/api/Get/getRoomDevice?UID=" + user + "&room=" + roomId;
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

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        Intent returnIntent = new Intent();
//        setResult(Activity.RESULT_CANCELED, returnIntent);
//        finish();
//    }



}
