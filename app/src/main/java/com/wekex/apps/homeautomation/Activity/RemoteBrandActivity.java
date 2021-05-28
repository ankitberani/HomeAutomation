package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.RemoteBrandAdapter;
import com.wekex.apps.homeautomation.helperclass.PahoMqttClient;
import com.wekex.apps.homeautomation.model.LoginRequestModel;
import com.wekex.apps.homeautomation.model.Model_MainRemoteList;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.remote_model_codes;
import com.wekex.apps.homeautomation.model.remote_model_codes_add_request;
import com.wekex.apps.homeautomation.utils.Constants;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.android.volley.Request.Method.POST;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPut;
import static com.wekex.apps.homeautomation.utils.Constants.jsonObjectPutInt;

public class RemoteBrandActivity extends AppCompatActivity {

    String _dno = "";
    String _cat = "";
    String _type = "";

    Toolbar toolbar;

    RecyclerView rv_model_list;
    String _selected_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_brand);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Model");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rv_model_list = findViewById(R.id.rv_model_list);
        rv_model_list.setLayoutManager(new LinearLayoutManager(this));

        _dno = getIntent().getStringExtra("dno");
        _cat = getIntent().getStringExtra("_cat");
        _type = getIntent().getStringExtra("_type");

        getRemoteCategory(_type);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void getRemoteCategory(String _type) {
        APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);
//        String _url = "http://209.58.164.151:88/api/Get/Remote?type=" + _type + "&brand=" + _cat;
        String _url = APIClient.BASE_URL + "/api/Get/Remote?type=" + _type + "&brand=" + _cat;
        Log.e("TAG", "URL of getRemoteCategory " + _url);
        Call<String> _call = _apiService.getRemoteCategory(_url);

        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAGGG", "OnResponse of device " + response.body().toString());
                try {
                    JSONObject _object = new JSONObject(response.body().toString());
                    JSONArray _arr_brand = _object.getJSONArray("models");
                    ArrayList<String> _lst_cat = new ArrayList<>();
                    for (int i = 0; i < _arr_brand.length(); i++) {
                        _lst_cat.add(_arr_brand.getString(i));
                    }

                    if (_lst_cat.size() == 0) {
                        Toast.makeText(RemoteBrandActivity.this, "Data not found!", Toast.LENGTH_SHORT).show();
                    } else {
                        RemoteBrandAdapter _adapter = new RemoteBrandAdapter(_lst_cat, RemoteBrandActivity.this, _dno, _type, _listener);
                        rv_model_list.setAdapter(_adapter);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Exception at getRemoteCate " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAGGG", "OnResponse onFailure " + t.getMessage());
            }
        });
    }

    View.OnClickListener _listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            _selected_model = (String) v.getTag();

//            Intent _intent = new Intent(RemoteBrandActivity.this, RemoteKeyActivity.class);
//            _intent.putExtra("type", _type);
//            _intent.putExtra("brand", _cat);
//            _intent.putExtra("model", _selected_model);
//            startActivity(_intent);

            getmodelCodes(_cat, _selected_model);
        }
    };

    void getmodelCodes(String brand, String model) {
        APIService _apiService = APIClient.getClient_1().create(APIService.class);
//        String _url = "http://209.58.164.151:88/api/Get/Remote?type=" + _type + "&brand=" + brand + "&model=" + model;
        String _url = APIClient.BASE_URL + "/api/Get/Remote?type=" + _type + "&brand=" + brand + "&model=" + model;
        Log.e("TAG", "URL of getmodelCodes " + _url);

        Observable<remote_model_codes> _observable = _apiService.getModelCode(_url);
        _observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<remote_model_codes>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(remote_model_codes remote_model_codes) {
                Log.e("TAG", "OnNext Called " + remote_model_codes.getName() + remote_model_codes.get_objKey().size());

                Gson _gson = new Gson();
                String _data = _gson.toJson(remote_model_codes);

                try {
                    JSONObject _object = new JSONObject(_data);
                    callRemote(_object);
                } catch (Exception e) {
                    Log.e("TAG", "Exception at on Next " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "OnError Called " + e.getMessage() + " " + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    int format = 0;
    String key = "";
    String value = "";

    private void callRemote(JSONObject response) {

        Log.e("TAG", "Call Remote Response " + response);
        View power_button_dialog = LayoutInflater.from(this).inflate(R.layout.dia_remote_button_test, null, false);
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(true);
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(power_button_dialog);
        try {
            value = response.getJSONArray("keys").getJSONObject(0).getString("value");
            key = response.getJSONArray("keys").getJSONObject(0).getString("key");
            format = response.getInt("Format");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout power = power_button_dialog.findViewById(R.id.power);
        Button yes = power_button_dialog.findViewById(R.id.yes);
        Button no = power_button_dialog.findViewById(R.id.no);
        LinearLayout testButton = power_button_dialog.findViewById(R.id.testButton);
        LinearLayout manual = power_button_dialog.findViewById(R.id.manual);
        //manual.setVisibility(View.GONE);

        power.getResources().getDrawable(R.drawable.remote_button_click_2);
        power.setBackground(getResources().getDrawable(R.drawable.remote_button_click_2));

        String finalValue = value;
        power.setOnClickListener(v -> {
            RemotClick(RemoteBrandActivity.this, response.toString(), finalValue, "irs");
            power_button_dialog.findViewById(R.id.worked).setVisibility(View.VISIBLE);
        });


        yes.setOnClickListener(v -> {
            dialog.dismiss();
            ConfirmRemoteAdd(response);
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual.setVisibility(View.VISIBLE);
                testButton.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(RemoteBrandActivity.this, "Please choose another brand and try again!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    void ConfirmRemoteAdd(JSONObject response) {
        try {
            Log.e("TAG", "ConfirmRemoteAdd " + response + " " + response.getJSONArray("keys").length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(RemoteBrandActivity.this).setTitle("Add Remote").setMessage("Do you want to add this remote ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialog(response);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void RemotClick(Context context, String sname, String value, String irs) {
        Log.d("TAG", "pubit: " + Constants.savetoShared(context).getString(sname, "0"));
        String jsonObject = Constants.savetoShared(context).getString(sname, "0");

        JSONObject jsonObject1 = new JSONObject();
        jsonObjectPut(jsonObject1, "dno", _dno);
        jsonObjectPut(jsonObject1, "key", "POWER");
        jsonObjectPutInt(jsonObject1, "format", 30);
        jsonObjectPut(jsonObject1, "dtype", "10");
        jsonObjectPut(jsonObject1, "data", value);
        jsonObjectPut(jsonObject1, "channel", irs);
        try {
            new PahoMqttClient().publishMessage(Constants.GeneralpahoMqttClient,
                    jsonObject1.toString(),
                    1,
                    "d/" + _dno + "/sub");
        } catch (MqttException e) {
            Log.d("TAG", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d("TAG", e.getMessage());
            e.printStackTrace();
        }
    }


    public void showDialog(JSONObject response) {
        final Dialog dialog = new Dialog(RemoteBrandActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialog_remote_name);
        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_remote_name);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_save);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError("Required!");
                } else {
                    dialog.dismiss();
                    addNewRemote(response, edt_name.getText().toString().trim());
//                    addRemote(response, edt_name.getText().toString().trim());
                }
            }
        });

        tv_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }


    void addRemote(JSONObject response, String _remote_name) {
        Toast.makeText(this, "Please wait, Verifying Data ...", Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley.newRequestQueue(this);
//        final String url = "http://209.58.164.151:88/api/Get/UpdateRemote?device=" + _dno + "&channel=ir";
        final String url = APIClient.BASE_URL + "/api/Get/UpdateRemote?device=" + _dno + "&channel=ir";

        JSONObject data = new JSONObject();
        try {
            response.put("Name", _remote_name);
            data.put("remotedata", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG", data + " saveRemote: " + url);
        JsonObjectRequest getRequest = new JsonObjectRequest(POST, url, data,
                upresponse -> {
                    Log.d("Response", "response " + upresponse);
                    // display response
                    try {


                        Utility _utility = new Utility(RemoteBrandActivity.this);
                        JSONObject toUpdateJSOn = new JSONObject();
                        toUpdateJSOn.put("dno", _dno);
                        toUpdateJSOn.put("ir", response);

                        Gson _gson = new Gson();
                        Model_UserRemoteList object = _gson.fromJson(response.toString(), Model_UserRemoteList.class);
                        object.setName(_remote_name);
                        if (!_utility.getString("user_remotes").isEmpty() && _utility.getString("user_remotes") != null) {
                            Model_MainRemoteList _obj_main = _gson.fromJson(_utility.getString("user_remotes"), Model_MainRemoteList.class);
                            _obj_main.get_lst().add(object);
                            _utility.putString("user_remotes", _gson.toJson(_obj_main));
                            Log.e("TAG", "Updated Json Size " + _obj_main.get_lst().size() + " <> " + _gson.toJson(_obj_main));
                        } else {
//                                Model_UserRemoteList lst_remotes = _gson.fromJson(response.toString(), Model_UserRemoteList.class);
                            ArrayList<Model_UserRemoteList> remoteList = new ArrayList();
                            remoteList.add(object);
                            Model_MainRemoteList _obj_main = new Model_MainRemoteList();
                            _obj_main.set_lst(remoteList);
                            _utility.putString("user_remotes", _gson.toJson(_obj_main));
                            Log.e("TAG", "Updated Json Size " + _obj_main.get_lst().size() + " <> " + _gson.toJson(_obj_main));
                        }
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

                       /* Intent _intent = new Intent();
                        setResult(RESULT_OK, _intent);
                        finish();*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Error.Response", "error" + error.getMessage())
        );
        queue.add(getRequest);
    }

    remote_model_codes_add_request _object_main = new remote_model_codes_add_request();

    void addNewRemote(JSONObject response, String _remote_name) {
        APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);


        try {

            remote_model_codes_add_request.remotedata _object = new remote_model_codes_add_request.remotedata();
            _object.setFormat(response.has("Format") ? response.getInt("Format") : 0);
            _object.setName(_remote_name);
            _object.setR_Brand(response.has("R_Brand") ? response.getString("R_Brand") : "");
            _object.setR_Type(response.has("R_Type") ? response.getString("R_Type") : "");

            ArrayList<remote_model_codes_add_request.remotedata.keys> _key_list = new ArrayList<>();
            if (response.has("keys")) {
                JSONArray _arr = response.getJSONArray("keys");
                for (int i = 0; i < _arr.length(); i++) {
                    remote_model_codes_add_request.remotedata.keys _key = new remote_model_codes_add_request.remotedata.keys();
                    JSONObject _objfromArray = _arr.getJSONObject(i);
                    _key.setKey(_objfromArray.has("key") ? _objfromArray.getString("key") : "");
                    _key.setValue(_objfromArray.has("value") ? _objfromArray.getString("value") : "");
                    _key_list.add(_key);
                }
                _object.set_objKey(_key_list);
            }
            _object_main.set_object_remote_data(_object);
            Log.e("TAG", "AddNewRemote From Model " + _object_main.get_object_remote_data().get_objKey().size() + " " + _object_main.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<String> _call = _apiService.AddNewRemote(_dno, "ir", _object_main);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                Toast.makeText(RemoteBrandActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
