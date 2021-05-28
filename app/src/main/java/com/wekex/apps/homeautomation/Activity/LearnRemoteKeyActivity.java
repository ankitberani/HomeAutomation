package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.LearnRemoteAdapter;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.LearnRemoteModel;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.ir_remotes;
import com.wekex.apps.homeautomation.model.remote_model_codes_add_request;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;

public class LearnRemoteKeyActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv_remote_learn;

    ArrayList<LearnRemoteModel> _lst_learn_key = new ArrayList<>();
    Gson _gson = new Gson();

    String TAG = "LearnRemoteKeyActivity";
    String _dno = "";
    LearnRemoteAdapter _adapter;
    Utility _utility;
    int pos = -1;
    ir_remotes _selectedRemote = new ir_remotes();

    TextView tv_save, tv_add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_remote_key);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("_name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        _utility = new Utility(LearnRemoteKeyActivity.this);
        rv_remote_learn = findViewById(R.id.rv_remote_learn);
        rv_remote_learn.setLayoutManager(new LinearLayoutManager(LearnRemoteKeyActivity.this));

        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(onClick_listener);

        tv_add_btn = findViewById(R.id.tv_add_btn);

        String remote_data = getIntent().getStringExtra("remote_data");
        _dno = getIntent().getStringExtra("_dno");
        pos = getIntent().getIntExtra("pos", -1);

        if (remote_data.isEmpty()) {
            _selectedRemote.setName(getIntent().getStringExtra("_name"));
        }
        fillRecyclerView(remote_data);

        tv_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.layout_add_key);

        EditText edt_key_name = (TextInputEditText) dialog.findViewById(R.id.edt_key_name);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_add_key);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_key_name.getText().toString().trim().isEmpty()) {
                    edt_key_name.setError("Required!");
                } else {
                    dialog.dismiss();
                    addCustomKey(edt_key_name.getText().toString());
                }
            }
        });
        dialog.show();
    }


    void addCustomKey(String _keyname) {
        try {
            TypeToken<ArrayList<Model_UserRemoteList.keys>> token = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
            };
            ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_selectedRemote.getName() + "_custom"), token.getType());
            if (_remote_key_data == null) {
                ArrayList<Model_UserRemoteList.keys> _lst = new ArrayList<>();
                Model_UserRemoteList.keys _object = new Model_UserRemoteList.keys();
                _object.setKey(_keyname);
                _object.setValue("");
                _lst.add(_object);
                _utility.putString(_selectedRemote.getName() + "_custom", _gson.toJson(_lst));
                Log.e("TAG", "Key Logs First Added " + _gson.toJson(_lst) + " Remote Name " + _selectedRemote.getName() + "_custom");
            } else {
                for (int i = 0; i < _remote_key_data.size(); i++) {
                    if (_keyname.equalsIgnoreCase(_remote_key_data.get(i).getKey())) {
                        _remote_key_data.get(i).setValue("");
                        _utility.putString(_selectedRemote.getName() + "_custom", _gson.toJson(_remote_key_data));
                        Log.e("TAG", "Key Logs Existed " + _gson.toJson(_remote_key_data) + " Remote Name " + _selectedRemote.getName() + "_custom");
                        return;
                    }
                }
                Model_UserRemoteList.keys _object = new Model_UserRemoteList.keys();
                _object.setKey(_keyname);
                _object.setValue("");
                _remote_key_data.add(_object);
                Log.e("TAG", "Key Logs Added " + _gson.toJson(_remote_key_data));
                _utility.putString(_selectedRemote.getName() + "_custom", _gson.toJson(_remote_key_data));
            }

            LearnRemoteModel _model = new LearnRemoteModel(0, _keyname, false, getString(R.string.learn), true);
            _lst_learn_key.add(_model);
            _adapter.notifyItemInserted(_lst_learn_key.size() - 1);
        } catch (Exception e) {
            Log.e("TAG", "Exception at Learn Remote Key " + e.toString());
        }
    }

    private void fillRecyclerView(String remote_data) {

        LearnRemoteModel _obj_on_off = new LearnRemoteModel(R.drawable.remote_button_power, "POWER", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_exit = new LearnRemoteModel(android.R.drawable.ic_input_get, "EXIT", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_menu = new LearnRemoteModel(R.drawable.remote_menu, "MENU", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_record_icon = new LearnRemoteModel(R.drawable.record_icon, "REC", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_up = new LearnRemoteModel(R.drawable.remote_up, "CHANNEL UP", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_down = new LearnRemoteModel(R.drawable.remote_down, "CHANNEL DOWN", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_vol_up = new LearnRemoteModel(R.drawable.remote_add, "VOLUME UP", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_vol_down = new LearnRemoteModel(R.drawable.remote_minus, "VOLUME DOWN", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_mute = new LearnRemoteModel(R.drawable.remote_mute, "MUTE", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_more = new LearnRemoteModel(R.drawable.remote_more, "MORE", false, getString(R.string.learn), false);

        LearnRemoteModel _obj_cursor_right = new LearnRemoteModel(R.drawable.cursor_right, "CURSOR RIGHT", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_cursor_left = new LearnRemoteModel(R.drawable.cursor_left, "CURSOR LEFT", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_cursor_up = new LearnRemoteModel(R.drawable.cursor_up, "CURSOR UP", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_cursor_down = new LearnRemoteModel(R.drawable.cursor_down, "CURSOR DOWN", false, getString(R.string.learn), false);
        LearnRemoteModel _obj_cursor_ok = new LearnRemoteModel(R.drawable.remote_okay, "CURSOR ENTER", false, getString(R.string.learn), false);


        LearnRemoteModel _obj_key_0 = new LearnRemoteModel(0, getResources().getString(R.string.digit_0), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_1 = new LearnRemoteModel(0, getResources().getString(R.string.digit_1), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_2 = new LearnRemoteModel(0, getResources().getString(R.string.digit_2), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_3 = new LearnRemoteModel(0, getResources().getString(R.string.digit_3), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_4 = new LearnRemoteModel(0, getResources().getString(R.string.digit_4), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_5 = new LearnRemoteModel(0, getResources().getString(R.string.digit_5), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_6 = new LearnRemoteModel(0, getResources().getString(R.string.digit_6), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_7 = new LearnRemoteModel(0, getResources().getString(R.string.digit_7), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_8 = new LearnRemoteModel(0, getResources().getString(R.string.digit_8), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_9 = new LearnRemoteModel(0, getResources().getString(R.string.digit_9), false, getString(R.string.learn), false);
        LearnRemoteModel _obj_key_dash = new LearnRemoteModel(0, getResources().getString(R.string.key_num_dash), false, getString(R.string.learn), false);


        _lst_learn_key.add(_obj_on_off);
        _lst_learn_key.add(_obj_menu);
        _lst_learn_key.add(_obj_record_icon);
        _lst_learn_key.add(_obj_exit);

        _lst_learn_key.add(_obj_cursor_right);
        _lst_learn_key.add(_obj_cursor_left);
        _lst_learn_key.add(_obj_cursor_up);
        _lst_learn_key.add(_obj_cursor_down);
        _lst_learn_key.add(_obj_cursor_ok);

        _lst_learn_key.add(_obj_up);
        _lst_learn_key.add(_obj_down);
        _lst_learn_key.add(_obj_vol_up);
        _lst_learn_key.add(_obj_vol_down);
        _lst_learn_key.add(_obj_more);
        _lst_learn_key.add(_obj_mute);


        _lst_learn_key.add(_obj_key_0);
        _lst_learn_key.add(_obj_key_1);
        _lst_learn_key.add(_obj_key_2);
        _lst_learn_key.add(_obj_key_3);
        _lst_learn_key.add(_obj_key_4);
        _lst_learn_key.add(_obj_key_5);
        _lst_learn_key.add(_obj_key_6);
        _lst_learn_key.add(_obj_key_7);
        _lst_learn_key.add(_obj_key_8);
        _lst_learn_key.add(_obj_key_9);
        _lst_learn_key.add(_obj_key_dash);


        if (remote_data != null && !remote_data.isEmpty())
            _selectedRemote = _gson.fromJson(remote_data, ir_remotes.class);

        for (int i = 0; i < _lst_learn_key.size(); i++) {
            for (int k = 0; k < _selectedRemote.get_lst_key().size(); k++)
                if (_lst_learn_key.get(i).getKey_name().equalsIgnoreCase(_selectedRemote.get_lst_key().get(k).getKey())) {
                    _lst_learn_key.get(i).setAlreadyLearn(true);
                    _lst_learn_key.get(i).setLearn_text(getResources().getString(R.string.learn_again));
                    break;
                }
        }
        try {
            if (!_utility.getString(_selectedRemote.getName()).isEmpty() && _utility.getString(_selectedRemote.getName()) != null) {
                TypeToken<ArrayList<Model_UserRemoteList.keys>> token_key = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                };
                ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_selectedRemote.getName()), token_key.getType());
                for (int i = 0; i < _remote_key_data.size(); i++) {
                    for (int j = 0; j < _lst_learn_key.size(); j++) {
                        if (_lst_learn_key.get(j).getKey_name().equalsIgnoreCase(_remote_key_data.get(i).getKey())) {
                            _lst_learn_key.get(j).setAlreadyLearn(true);
                            _lst_learn_key.get(j).setLearn_text(getResources().getString(R.string.learn_again));
                            Log.e("TAG", "Learn Again " + _lst_learn_key.get(j).getKey_name());
                        }
                    }
                }
            }

            if (!_utility.getString(_selectedRemote.getName() + "_custom").isEmpty() && _utility.getString(_selectedRemote.getName()) != null) {
                TypeToken<ArrayList<Model_UserRemoteList.keys>> token_key = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                };
                ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_selectedRemote.getName() + "_custom"), token_key.getType());
                for (int i = 0; i < _remote_key_data.size(); i++) {
                    if (!isAdded(_remote_key_data.get(i).getKey())) {
                        LearnRemoteModel _model = new LearnRemoteModel(0, _remote_key_data.get(i).getKey(), false, getString(R.string.learn), true);
                        if (_remote_key_data.get(i).getValue().isEmpty())
                            _model.setAlreadyLearn(false);
                        else {
                            _model.setLearn_text(getResources().getString(R.string.learn_again));
                            _model.setAlreadyLearn(true);
                        }
                        _lst_learn_key.add(_model);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception " + e.getMessage());
        }


        _adapter = new LearnRemoteAdapter(LearnRemoteKeyActivity.this, _lst_learn_key, _listener, _selectedRemote.getName());
        rv_remote_learn.setAdapter(_adapter);
    }


    boolean isAdded(String keyname) {
        for (int i = 0; i < _lst_learn_key.size(); i++) {
            if (_lst_learn_key.get(i).getKey_name().equalsIgnoreCase(keyname))
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    String SELECTED_KEY = "";
    int SELECTED_POS = -1;

    View.OnClickListener _listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (int) view.getTag();
            SELECTED_KEY = _lst_learn_key.get(pos).getKey_name();
            SELECTED_POS = pos;
            showDialog(_lst_learn_key.get(pos).getKey_name());
        }
    };
    Dialog dialog_learn_key;

    public void showDialog(String keyName) {
        learnIR();
        dialog_learn_key = new Dialog(LearnRemoteKeyActivity.this);
        dialog_learn_key.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_learn_key.setCancelable(true);
        dialog_learn_key.setContentView(R.layout.learn_remote_wait_dialog);
        TextView tv_cancel = (TextView) dialog_learn_key.findViewById(R.id.tv_cancel);
        TextView tv_head = (TextView) dialog_learn_key.findViewById(R.id.tv_head);
        tv_head.setText(getResources().getString(R.string.learn_key_msg) + " " + keyName + " Button.");
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_learn_key.dismiss();
//                showTestKeyDialog(keyName);
            }
        });
        dialog_learn_key.show();
    }

    void learnIR() {
        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "channel", "irl");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, jsonObject1.toString(),
                    1,
                    "d/" + _dno + "/sub");
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String _datafromService = intent.getStringExtra("datafromService");
            Log.e(TAG, "updateUI: " + _datafromService);
            if (_datafromService != null && !_datafromService.isEmpty() && _datafromService.contains("=") && (_datafromService.startsWith("uint16_t") || _datafromService.contains("uint16_t"))) {
                StringBuilder _builder = new StringBuilder();
                boolean isFound = false;
                for (int i = 0; i < _datafromService.length(); i++) {
                    Character c = _datafromService.charAt(i);
                    if (!isFound && c == '{') {
                        isFound = true;
                    }
                    if (isFound && c != '{' && c != '}') {
                        _builder.append(c);
                    }
                    if (c == '}') {
                        Log.e("TAG", "Final String after calculate " + _builder.toString());
                        break;
                    }
                }
                try {
                    String _object = "38000," + _builder.toString().replace(" ", "").trim();
                    if (dialog_learn_key != null && dialog_learn_key.isShowing()) {
                        dialog_learn_key.dismiss();
                    }
                    showTestKeyDialog(_object);
                    Log.e("TAG", "Object To set " + _object);
                } catch (Exception e) {
                    Log.e("TAG", "Exception at set " + e.getMessage());
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //DtypeViews.getGetDevice(this);
        String jsonString = Constants.savetoShared(this).getString(Constants.ROOMS, "null");
        //addViews(jsonString);
        Log.d("TAG", "onResume: ");
        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    void showTestKeyDialog(String _receivedData) {

        final Dialog dialog = new Dialog(LearnRemoteKeyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.test_study_remote_dialog);
        TextView tv_test = (TextView) dialog.findViewById(R.id.tv_test);

        tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                RemotClickConfigure("30", _dno, "irs", _receivedData);
            }
        });
        dialog.show();
    }

    public void RemotClickConfigure(String format, String dnoValue, String irl, String Value) {

        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "format", format);
        jsonObjectPut(jsonObject1, "channel", irl);
        jsonObjectPut(jsonObject1, "data", Value);
        Log.e("TAG", "RemotClickConfigure dno " + dnoValue + " channel " + irl + " topic " + "d/" + _dno + "/sub");
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient, jsonObject1.toString(),
                    1,
                    "d/" + dnoValue + "/sub");

            confirmLeanKey(Value);
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    void confirmLeanKey(String Value) {
        final Dialog dialog = new Dialog(LearnRemoteKeyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.confirm_test_learn_key);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (_adapter != null && _lst_learn_key != null) {
                        if (!_lst_learn_key.get(SELECTED_POS).isCustomKey()) {
                            _lst_learn_key.get(SELECTED_POS).setAlreadyLearn(true);
                            _lst_learn_key.get(SELECTED_POS).setLearn_text(getResources().getString(R.string.learn_again));
                            _adapter.notifyItemChanged(SELECTED_POS);
                            try {
                                TypeToken<ArrayList<Model_UserRemoteList.keys>> token = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                                };
                                ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_selectedRemote.getName()), token.getType());
                                if (_remote_key_data == null) {
                                    ArrayList<Model_UserRemoteList.keys> _lst = new ArrayList<>();
                                    Model_UserRemoteList.keys _object = new Model_UserRemoteList.keys();
                                    _object.setKey(SELECTED_KEY);
                                    _object.setValue(Value);
                                    _lst.add(_object);
                                    _utility.putString(_selectedRemote.getName(), _gson.toJson(_lst));
                                    Log.e("TAG", "Key Logs First Added " + _gson.toJson(_lst));
                                } else {
                                    for (int i = 0; i < _remote_key_data.size(); i++) {
                                        if (SELECTED_KEY.equalsIgnoreCase(_remote_key_data.get(i).getKey())) {
                                            _remote_key_data.get(i).setValue(Value);
                                            _utility.putString(_selectedRemote.getName(), _gson.toJson(_remote_key_data));
                                            Log.e("TAG", "Key Logs Existed " + _gson.toJson(_remote_key_data));
                                            dialog.dismiss();
                                            return;
                                        }
                                    }
                                    Model_UserRemoteList.keys _object = new Model_UserRemoteList.keys();
                                    _object.setKey(SELECTED_KEY);
                                    _object.setValue(Value);
                                    _remote_key_data.add(_object);
                                    Log.e("TAG", "Key Logs Added " + _gson.toJson(_remote_key_data));
                                    _utility.putString(_selectedRemote.getName(), _gson.toJson(_remote_key_data));
                                }
                            } catch (Exception e) {
                                Log.e("TAG", "Exception at Learn Remote Key " + e.toString());
                            }
                        } else {
                            TypeToken<ArrayList<Model_UserRemoteList.keys>> token_key = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                            };
                            ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_selectedRemote.getName() + "_custom"), token_key.getType());

                            Log.e("TAG", "Button Name " + _selectedRemote.getName() + "_custom");
                            for (int i = 0; i < _remote_key_data.size(); i++) {
                                Log.e("TAG", "Both Key while add " + _lst_learn_key.get(SELECTED_POS).getKey_name() + " <> " + _remote_key_data.get(i).getKey() + " SELECTED_POS " + SELECTED_POS + " " + _lst_learn_key.size());
                                if (_lst_learn_key.get(SELECTED_POS).getKey_name().equalsIgnoreCase(_remote_key_data.get(i).getKey())) {
                                    _remote_key_data.get(i).setValue(Value);
                                    _utility.putString(_selectedRemote.getName() + "_custom", _gson.toJson(_remote_key_data));
                                    _lst_learn_key.get(SELECTED_POS).setAlreadyLearn(true);
                                    _lst_learn_key.get(SELECTED_POS).setLearn_text(getResources().getString(R.string.learn_again));
                                    _adapter.notifyItemChanged(SELECTED_POS);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
                dialog.dismiss();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    View.OnClickListener onClick_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!_utility.getString(_dno).isEmpty()) {
                TypeToken<ArrayList<Model_UserRemoteList.keys>> token = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                };
                ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_dno), token.getType());
                for (int i = 0; i < _remote_key_data.size(); i++) {
                    _selectedRemote.get_lst_key().add(_remote_key_data.get(i));
                }
                String _data = _gson.toJson(_selectedRemote);
                Log.e("TAG", "Data At Save " + _data);
                APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);

                remote_model_codes_add_request _object = new remote_model_codes_add_request();

                remote_model_codes_add_request.remotedata _remote_data = new remote_model_codes_add_request.remotedata();
                _remote_data.setName(_selectedRemote.getName());
                _remote_data.setR_Type(_selectedRemote.getR_Type());
                _remote_data.setR_Brand(_selectedRemote.getR_Brand());
                _remote_data.setFormat(Integer.parseInt(_selectedRemote.getFormat()));
                ArrayList<remote_model_codes_add_request.remotedata.keys> _lst_key = new ArrayList<>();
                for (int i = 0; i < _selectedRemote.get_lst_key().size(); i++) {
                    remote_model_codes_add_request.remotedata.keys _objKey = new remote_model_codes_add_request.remotedata.keys();
                    _objKey.setKey(_selectedRemote.get_lst_key().get(i).getKey());
                    _objKey.setValue(_selectedRemote.get_lst_key().get(i).getValue());
                    _lst_key.add(_objKey);
                }
                _remote_data.set_objKey(_lst_key);
                _object.set_object_remote_data(_remote_data);


                Log.e("TAG", "Object Sent In Save " + _gson.toJson(_object));
                Call<String> _call = _apiService.UpdateRemote(pos, _dno, "ir", _object);
                _call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response != null && response.isSuccessful()) {
                            Toast.makeText(LearnRemoteKeyActivity.this, "Remote Saved Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("TAG", "OnFailure Called " + t.toString());
                    }
                });
            } else {
                finish();
            }
        }
    };
}
