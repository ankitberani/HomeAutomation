package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.DeviceListAdapter;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.device_model;

import java.util.ArrayList;

public class RemoteTypeListActivity extends AppCompatActivity {

    Toolbar toolbar;
    String dno = "";
    RecyclerView rv_device_list;
    ArrayList<device_model> _lst_model = new ArrayList<>();

    public int TYPE_SELECT = 100;
    Utility _utility;
    Gson _gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_activity_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Device Type");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dno = getIntent().getStringExtra("dno");

        rv_device_list = findViewById(R.id.rv_device_list);
        rv_device_list.setLayoutManager(new GridLayoutManager(this, 2));

        device_model _obj_ac = new device_model(getResources().getString(R.string.ac), R.drawable.ic_ac, false, "");
        device_model _obj_tv = new device_model(getResources().getString(R.string.tv), R.drawable.ic_tv, false, "");
        device_model _obj_setup_box = new device_model(getResources().getString(R.string.stp_bx), R.drawable.setup_box, false, "");
        device_model _obj_fan = new device_model(getResources().getString(R.string.fan), R.drawable.fan, false, "");
        device_model _obj_custom_type = new device_model(getResources().getString(R.string.custom_typ), R.drawable.custom_type, false, "");


        _lst_model.add(_obj_ac);
        _lst_model.add(_obj_tv);
        _lst_model.add(_obj_setup_box);
        _lst_model.add(_obj_fan);
        _lst_model.add(_obj_custom_type);

        TypeToken<ArrayList<device_model>> token = new TypeToken<ArrayList<device_model>>() {
        };

        _utility = new Utility(RemoteTypeListActivity.this);
        if (!_utility.getString("custome_remote").isEmpty()) {
            ArrayList<device_model> _lst = _gson.fromJson(_utility.getString("custome_remote"), token.getType());
            for (int i = 0; i < _lst.size(); i++) {
                _lst_model.add(_lst.get(i));
            }
        }

        DeviceListAdapter _adapter = new DeviceListAdapter(RemoteTypeListActivity.this, _lst_model, dno, _listener);
        rv_device_list.setAdapter(_adapter);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TYPE_SELECT) {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    View.OnClickListener _listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String _selected = (String) v.getTag().toString();
            if (_selected.equalsIgnoreCase(getResources().getString(R.string.custom_typ))) {
                showAddRemoteDialog();
            } else {
                Intent _intent = new Intent(RemoteTypeListActivity.this, RemoteCatList.class);
                _intent.putExtra("dno", dno);
                _intent.putExtra("type", (String) v.getTag());
                startActivityForResult(_intent, TYPE_SELECT);
            }
        }
    };

    public void showAddRemoteDialog() {
        final Dialog dialog = new Dialog(RemoteTypeListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_update_remote);


        String[] remote_type = getResources().getStringArray(R.array.remote_type);
        EditText edt_name = (EditText) dialog.findViewById(R.id.edt_remote_name);
        EditText edt_remote_brand = (EditText) dialog.findViewById(R.id.edt_remote_brand);
        Spinner spn_type = (Spinner) dialog.findViewById(R.id.spn_type);

        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText(R.string.custom_typ);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_save);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);


        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError("Required!");
                } else if (edt_remote_brand.getText().toString().trim().isEmpty()) {
                    edt_remote_brand.setError("Required!");
                } else {
                    if (spn_type.getSelectedItemPosition() == 0) {
                        String _tempDno = "custom_" + System.currentTimeMillis();

                        createCustomRemote(_tempDno, edt_name.getText().toString().trim(), edt_remote_brand.getText().toString(), getResources().getStringArray(R.array.remote_type)[spn_type.getSelectedItemPosition()]);
                        Intent _intent = new Intent(RemoteTypeListActivity.this, RemoteKeyActivity.class);
                        _intent.putExtra("_dno", dno);
                        _intent.putExtra("name", edt_name.getText().toString().trim());
                        String _data = "";
                        _intent.putExtra("data", _data);
                        _intent.putExtra("pos", -1);
                        startActivity(_intent);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(RemoteTypeListActivity.this, "WIP this category", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void createCustomRemote(String tempDno, String name, String brand, String type) {

        try {
            TypeToken<ArrayList<Model_UserRemoteList>> token = new TypeToken<ArrayList<Model_UserRemoteList>>() {
            };
            ArrayList<Model_UserRemoteList> _remote_key_data = _gson.fromJson(_utility.getString("custom"), token.getType());
            if (_remote_key_data == null) {
                ArrayList<Model_UserRemoteList> _lst = new ArrayList<>();
                Model_UserRemoteList _object = new Model_UserRemoteList();
                _object.setName(name);
                _object.setR_Brand(brand);
                _object.setR_Type(type);
                _lst.add(_object);
                _utility.putString("custom", _gson.toJson(_lst));
                Toast.makeText(this, "Added First Custome Remote tempDno " + tempDno, Toast.LENGTH_SHORT).show();
            } else {
                Model_UserRemoteList _object = new Model_UserRemoteList();
                _object.setName(name);
                _object.setR_Brand(brand);
                _object.setR_Type(type);
                _remote_key_data.add(_object);
                _utility.putString("custom", _gson.toJson(_remote_key_data));
                Toast.makeText(this, "Added " + _remote_key_data.size() + " Remote " + tempDno, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at Learn Remote Key " + e.toString());
        }
    }
}
