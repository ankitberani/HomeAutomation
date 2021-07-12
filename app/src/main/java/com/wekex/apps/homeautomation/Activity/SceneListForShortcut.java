package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wekex.apps.homeautomation.Interfaces.SelectSceneListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.SceneAdapter;
import com.wekex.apps.homeautomation.adapter.SceneAdapterForShortcuts;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SceneListForShortcut extends AppCompatActivity implements SelectSceneListener {

    Toolbar toolbar;
    ArrayList<scene_model.Scene> _mainList;
    RecyclerView rv_scenelist;
    SceneAdapterForShortcuts _sceneAdapter;

    private ProgressBar progressDialog;
    TextView tv_save;
    SelectSceneListener _interface;
    Gson _gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list_for_shortcut);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_scene));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rv_scenelist = findViewById(R.id.rv_scene_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rv_scenelist.setLayoutManager(mLayoutManager);
        tv_save = toolbar.findViewById(R.id.tv_save);
        tv_save.setVisibility(View.VISIBLE);
        progressDialog = findViewById(R.id.progressDialog);
        _interface = this;
        getSceneFromServer();

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "Save Shortcuts called");
                ArrayList<String> _selectedList = new ArrayList<>();
                for (int i = 0; i < _mainList.size(); i++) {
                    if (_mainList.get(i).isSelected())
                        _selectedList.add(_mainList.get(i).getID());
                }

                if (_selectedList.size() == 0) {
                    PreferencesHelper.setString(SceneListForShortcut.this, Constants.ShortcutList, "");
                    finish();
                } else {
                    String _selectedJson = _gson.toJson(_selectedList);
                    Log.e("TAg", "Selected Scene ID " + _selectedJson);
                    PreferencesHelper.setString(SceneListForShortcut.this, Constants.ShortcutList, _selectedJson);
                    finish();
                }
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
                        String _json = PreferencesHelper.getString(SceneListForShortcut.this, Constants.ShortcutList);

                        ArrayList<String> _selectedList = (ArrayList<String>) _gson.fromJson(_json,
                                new TypeToken<ArrayList<String>>() {
                                }.getType());

                        if (_selectedList != null && _selectedList.size() != 0)
                            for (int i = 0; i < _mainList.size(); i++) {
                                for (int j = 0; j < _selectedList.size(); j++) {
                                    if (_mainList.get(i).getID().equalsIgnoreCase(_selectedList.get(j))) {
                                        _mainList.get(i).setSelected(true);
                                        break;
                                    }
                                }
                            }
                        _sceneAdapter = new SceneAdapterForShortcuts(_mainList, SceneListForShortcut.this, _interface);
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

    public void showProgressDialog(String msg) {
        try {
            if (this.progressDialog != null && this.progressDialog.getVisibility() != View.VISIBLE) {
                this.progressDialog.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (this.progressDialog != null && this.progressDialog.getVisibility() == View.VISIBLE) {
                this.progressDialog.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectScene(int position) {
        try {
            Log.e("TAG", "Selected Scene Called " + position);
            _mainList.get(position).setSelected(!_mainList.get(position).isSelected());
            _sceneAdapter.notifyItemChanged(position);
        } catch (Exception e) {
            Log.e("TAG", "Exception at select scene " + e.toString());
        }
    }
}