package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.Interfaces.RemoteListInterface;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.UserRemoteCatFillAdapter;
import com.wekex.apps.homeautomation.adapter.UserRemoteFillAdapter;
import com.wekex.apps.homeautomation.model.Model_MainRemoteList;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.RemoteCounts;
import com.wekex.apps.homeautomation.model.UserRemoteListModel;
import com.wekex.apps.homeautomation.model.ir_remotes;
import com.wekex.apps.homeautomation.model.remote_model_codes_add_request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class CategoryRemoteList extends AppCompatActivity implements RemoteListInterface {

    Toolbar toolbar;
    String remote_data = "";
    UserRemoteFillAdapter _adapter;
    RecyclerView rv_remote_data;
    Gson _gson = new Gson();
    String _dno = "";

    RemoteListInterface _objRemoteList;
    ArrayList<ir_remotes> _lst;
    String _type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_remote_list);

        _type = getIntent().getStringExtra("_type");
        _dno = getIntent().getStringExtra("_dno");
        remote_data = getIntent().getStringExtra("data");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(_type + " Remotes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rv_remote_data = findViewById(R.id.rv_remote_list);
        rv_remote_data.setLayoutManager(new LinearLayoutManager(this));
        _objRemoteList = this;

        TypeToken<ArrayList<ir_remotes>> token = new TypeToken<ArrayList<ir_remotes>>() {
        };

        _lst = _gson.fromJson(remote_data, token.getType());
        _adapter = new UserRemoteFillAdapter(_lst, CategoryRemoteList.this, this);
        rv_remote_data.setAdapter(_adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void _click_remote(String _name, int pos) {
        Intent _intent = new Intent(CategoryRemoteList.this, RemoteKeyActivity.class);
        _intent.putExtra("_dno", _dno);
        _intent.putExtra("name", _name);
        String _data = _gson.toJson(_lst.get(pos));
        _intent.putExtra("data", _data);
        _intent.putExtra("pos", pos);
        startActivity(_intent);
    }

    @Override
    public void _remote_operation(int operation, int pos) {
        if (operation == 1)
            showAddRemoteDialog(pos);
        else
            Toast.makeText(CategoryRemoteList.this, "WIP", Toast.LENGTH_SHORT).show();
    }

    public void showAddRemoteDialog(int pos) {
        final Dialog dialog = new Dialog(CategoryRemoteList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_update_remote);


        String[] remote_type = getResources().getStringArray(R.array.remote_type);
        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_remote_name);
        EditText edt_remote_brand = (EditText) dialog.findViewById(R.id.edt_remote_brand);
        Spinner spn_type = (Spinner) dialog.findViewById(R.id.spn_type);

        for (int i = 0; i < remote_type.length; i++) {
            if (_type.equalsIgnoreCase(remote_type[i])) {
                spn_type.setSelection(i);
                break;
            }
        }
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_save);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        edt_name.setText(_lst.get(pos).getName());
        edt_remote_brand.setText(_lst.get(pos).getR_Brand());
        tv_yes.setOnClickListener(v -> {
            if (edt_name.getText().toString().trim().isEmpty()) {
                edt_name.setError("Required!");
            } else if (edt_remote_brand.getText().toString().trim().isEmpty()) {
                edt_remote_brand.setError("Required!");
            } else {
                updateRemote(pos, edt_name.getText().toString().trim(), edt_remote_brand.getText().toString(), remote_type[spn_type.getSelectedItemPosition()]);
                dialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    void updateRemote(int pos, String _name, String Brand, String _type) {
        APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);

        _lst.get(pos).setName(_name);
        _lst.get(pos).setR_Brand(Brand);
        _lst.get(pos).setR_Type(_type);


        remote_model_codes_add_request _object = new remote_model_codes_add_request();

        remote_model_codes_add_request.remotedata _remote_data = new remote_model_codes_add_request.remotedata();
        _remote_data.setName(_name);
        _remote_data.setR_Type(_lst.get(pos).getR_Type());
        _remote_data.setR_Brand(_lst.get(pos).getR_Brand());
        _remote_data.setFormat(Integer.parseInt(_lst.get(pos).getFormat()));
        ArrayList<remote_model_codes_add_request.remotedata.keys> _lst_key = new ArrayList<>();
        for (int i = 0; i < _lst.get(pos).get_lst_key().size(); i++) {
            remote_model_codes_add_request.remotedata.keys _objKey = new remote_model_codes_add_request.remotedata.keys();
            _objKey.setKey(_lst.get(pos).get_lst_key().get(i).getKey());
            _objKey.setValue(_lst.get(pos).get_lst_key().get(i).getValue());
            _lst_key.add(_objKey);
        }
        _remote_data.set_objKey(_lst_key);
        _object.set_object_remote_data(_remote_data);

        Log.e("TAG", "Data at update " + _gson.toJson(_object));
        Call<String> _call = _apiService.UpdateRemote(pos, _dno, "ir", _object);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    _adapter.notifyItemChanged(pos);
                    Toast.makeText(CategoryRemoteList.this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
            }
        });
    }

}
