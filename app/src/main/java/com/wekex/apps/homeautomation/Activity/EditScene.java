package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SceneAdapter;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.SuccessResponse;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;
import com.wekex.apps.homeautomation.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

public class EditScene extends AppCompatActivity implements rgb_color_interface {

    Toolbar toolbar;
    ArrayList<scene_model.Scene> _mainList;
    RecyclerView rv_scenelist;
    SceneAdapter _sceneAdapter;

    int EDIT_SCENE = 151;
    rgb_color_interface _interface;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_scene);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Scene");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rv_scenelist = findViewById(R.id.rv_scene_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_scenelist.setLayoutManager(mLayoutManager);
        _interface = this;
        getSceneFromServer();
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

                        _sceneAdapter = new SceneAdapter(_mainList, EditScene.this, _interface, true);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here
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
        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
        String url = APIClient.BASE_URL + "/api/Get/delScene?ID=" + id;
        showProgressDialog(getResources().getString(R.string.please_wait));
        Observable<SuccessResponse> observable = apiInterface.delScene(url);
        try {
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<SuccessResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(SuccessResponse successResponse) {
                    Toast.makeText(EditScene.this, successResponse.getSuccess() + "", Toast.LENGTH_SHORT).show();
                    if (successResponse.getSuccess()) {
                        _mainList.remove(pos);
                        _sceneAdapter.notifyItemRemoved(pos);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("TAGG", "OnError " + e.getMessage(), e);
                }

                @Override
                public void onComplete() {
                    hideProgressDialog();
                }
            });
        } catch (Exception e) {
            Log.e("TAGGG", "Exception at e " + e.getMessage(), e);
        }
    }

    @Override
    public void editScene(String id, int pos) {
        try {
            JSONObject _object = new JSONObject(_mainList.get(pos).getDevices().get(0));
            Intent _intent = new Intent(EditScene.this, CreateScene.class);
            if (_object.has("dno")) {
                _intent.putExtra("dno", _object.getString("dno"));
            }
//            _intent.putExtra("dtype", type1);
            Gson gson = new Gson();
            String data = gson.toJson(_mainList.get(pos));
            _intent.putExtra("data", data);
            _intent.putExtra("id", id);
            _intent.putExtra("name", _mainList.get(pos).getScene_name());
            _intent.putExtra("FromEdit", "true");
            String list_all = gson.toJson(_mainList);
            _intent.putExtra("list", list_all);
            startActivityForResult(_intent, EDIT_SCENE);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_SCENE) {
            String id = data.getExtras().getString("Id");
            String newName = data.getExtras().getString("_new_name");
            String device = data.getExtras().getString("device");
            Gson gson = new Gson();

            ArrayList<String> anotherStr = gson.fromJson(device, new TypeToken<ArrayList<String>>() {
            }.getType());
            Log.e("TAGG", "Another json " + anotherStr);
            try {
                for (int i = 0; i < _mainList.size(); i++) {
                    if (id.equalsIgnoreCase(_mainList.get(i).getID())) {
                        _mainList.get(i).setScene_name(newName);
                        _mainList.get(i).setName(newName);
                        ArrayList<String> dev = new ArrayList<>();
                        dev.add(anotherStr.get(0));
                        _mainList.get(i).setDevices(dev);
                        _sceneAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("TAGGG", "Exception at onResult " + e.getMessage());
            }
        }
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
}
