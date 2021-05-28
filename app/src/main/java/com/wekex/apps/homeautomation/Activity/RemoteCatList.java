package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.SplashActivity;
import com.wekex.apps.homeautomation.adapter.RemoteCatAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RemoteCatList extends AppCompatActivity {

    Toolbar toolbar;
    String dno = "";

    RecyclerView rv_cate_list;
    String _cat_type;

    int REMOTE_CAT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Brand");
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (getIntent().hasExtra("dno"))
            dno = getIntent().getStringExtra("dno");
        if (getIntent().hasExtra("type"))
            _cat_type = getIntent().getStringExtra("type");

        rv_cate_list = findViewById(R.id.rv_cate_list);
        rv_cate_list.setLayoutManager(new GridLayoutManager(this, 1));
        getRemoteCatyegory(_cat_type);
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

    void getRemoteCatyegory(String _type) {
        APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);
        String _url = APIClient.BASE_URL + "/api/Get/Remote?type=" + _type;
        Log.e("TAG", "URL of getRemoteCatyegory " + _url);
        Call<String> _call = _apiService.getRemoteCategory(_url);

        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("TAGGG", "OnResponse of device " + response.body().toString());
                try {
                    JSONObject _object = new JSONObject(response.body().toString());
                    JSONArray _arr_brand = _object.getJSONArray("brands");
                    ArrayList<String> _lst_cat = new ArrayList<>();
                    for (int i = 0; i < _arr_brand.length(); i++) {
                        _lst_cat.add(_arr_brand.getString(i));
                    }

                    _lst_cat.add("Add Custom Brand");
                    if (_lst_cat.size() == 0) {
                        Toast.makeText(RemoteCatList.this, "Data not found!", Toast.LENGTH_SHORT).show();
                    } else {
                        RemoteCatAdapter _adapter = new RemoteCatAdapter(_lst_cat, RemoteCatList.this, dno, _type, _listener);
                        rv_cate_list.setAdapter(_adapter);
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
            Intent _intent = new Intent(RemoteCatList.this, RemoteBrandActivity.class);
            _intent.putExtra("_cat", (String) v.getTag());
            _intent.putExtra("dno", dno);
            _intent.putExtra("_type", _cat_type);
            startActivityForResult(_intent, REMOTE_CAT);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REMOTE_CAT) {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
