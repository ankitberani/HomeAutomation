package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SceneAdapter;
import com.wekex.apps.homeautomation.adapter.SelectSceneAdapter;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SelectSceneActivity extends AppCompatActivity implements rgb_color_interface {

    Toolbar toolbar;
    ArrayList<scene_model.Scene> _mainList;
    RecyclerView rv_scenelist;
    SelectSceneAdapter _sceneAdapter;
    private ProgressDialog progressDialog;
    rgb_color_interface _interface;
    TextView tv_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_scene);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Scene");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        _interface = this;
        rv_scenelist = findViewById(R.id.rv_scene_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_scenelist.setLayoutManager(mLayoutManager);
        tv_save = toolbar.findViewById(R.id.tv_save);
        tv_save.setVisibility(View.VISIBLE);
        getSceneFromServer();
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < _mainList.size(); i++) {
                    if (_mainList.get(i).isSelected()) {
//                        Toast.makeText(SelectSceneActivity.this, _mainList.get(i).getName() + "", Toast.LENGTH_SHORT).show();
                        Intent _intent = new Intent();
                        Gson _gson = new Gson();
                        String _scene = _gson.toJson(_mainList.get(i));
                        _intent.putExtra("scene", _scene);
                        setResult(RESULT_OK, _intent);
                        finish();
                        return;
                    }
                }
                Toast.makeText(SelectSceneActivity.this, "Please select scene", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getSceneFromServer() {
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        final String url = APIClient.BASE_URL + "/api/Get/getScene?UID=" + user;
        showProgressDialog(getResources().getString(R.string.please_wait));
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        Observable<scene_model> observable = apiInterface.getAllScene(url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<scene_model>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(scene_model scene_models) {
                Log.e("TAGG", "OnNext  called " + scene_models.getLst_scene().size());
                try {
                    _mainList = scene_models.getLst_scene();
                    if (scene_models != null && scene_models.getLst_scene() != null) {
                        Log.e("TAGGG", "Size of scene Before " + scene_models.getLst_scene().size());
                        _sceneAdapter = new SelectSceneAdapter(_mainList, SelectSceneActivity.this, _interface);
                        rv_scenelist.setAdapter(_sceneAdapter);
                        Log.e("TAGGG", "Size of scene after >>" + scene_models.getLst_scene().size());
                    }
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at onNext " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAGG", "OnError  called " + e.toString(), e);
            }

            @Override
            public void onComplete() {
                hideProgressDialog();
            }
        });
    }


    public void showProgressDialog(String msg) {
        try {
            if (this.progressDialog != null && !this.progressDialog.isShowing()) {
                this.progressDialog.setMessage(msg);
                this.progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void selectedColor(int color) {

    }

    @Override
    public void selectedScene(int position) {

    }

    @Override
    public void triggerScene(String id) {

    }

    @Override
    public void delScene(String id, int pos) {

    }

    @Override
    public void editScene(String id, int pos) {

    }

    @Override
    public void getSchedules(String scheduleData) {

    }

    @Override
    public void deleteSchedule(int pos) {

    }

    @Override
    public void editSchedule(int pos) {

    }

    @Override
    public void updateSchedule(int pos) {

    }
}