package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wekex.apps.homeautomation.R;

public class NewSceneTempletActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView tv_1, tv_2, tv_3, tv_4, tv_5;
    String scenee;
    String roomID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scene_templet);
        setupToolbar();
        init();
        this.scenee = getIntent().getStringExtra("Devices");
        Log.wtf("SCENEE_INTENT_EXTRA", this.scenee);
        roomID = getIntent().getStringExtra("roomID");
    }

    void setupToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("New scene");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    void init() {
        tv_1 = findViewById(R.id.tv_suggest_1);
        tv_2 = findViewById(R.id.tv_suggest_2);
        tv_3 = findViewById(R.id.tv_suggest_3);
        tv_4 = findViewById(R.id.tv_suggest_4);
        tv_5 = findViewById(R.id.tv_suggest_5);
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
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_sug_1: {
                /*intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
                intent.putExtra("Devices", "new");
                intent.putExtra("roomID", roomID);
                intent.putExtra("name", tv_1.getText().toString().trim());
                startActivity(intent);
                */
                startAddScene(tv_1.getText().toString().trim());
            }
            break;
            case R.id.ll_sug_2: {
                /*intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
                intent.putExtra("Devices", "new");
                intent.putExtra("roomID", roomID);
                intent.putExtra("name", tv_2.getText().toString().trim());
                startActivity(intent);
                */
                startAddScene(tv_2.getText().toString().trim());
            }
            break;
            case R.id.ll_sug_3: {
                /*intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
                intent.putExtra("Devices", "new");
                intent.putExtra("roomID", roomID);
                intent.putExtra("name", tv_3.getText().toString().trim());
                startActivity(intent);*/
                startAddScene(tv_3.getText().toString().trim());
            }
            break;
            case R.id.ll_sug_4: {
                /*intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
                intent.putExtra("Devices", "new");
                intent.putExtra("roomID", roomID);
                intent.putExtra("name", tv_4.getText().toString().trim());
                startActivity(intent);*/
                startAddScene(tv_4.getText().toString().trim());
            }
            break;
            case R.id.ll_sug_5: {
                /*intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
                intent.putExtra("Devices", "new");
                intent.putExtra("roomID", roomID);
                intent.putExtra("name", tv_5.getText().toString().trim());
                startActivity(intent);
                finish();*/
                startAddScene(tv_5.getText().toString().trim());
            }
            break;

            case R.id.ll_custom: {
                startAddScene("");
            }
            break;
        }
    }

    void startAddScene(String name) {
        Intent intent = new Intent(NewSceneTempletActivity.this, AddNewScene.class);
        intent.putExtra("Devices", "new");
        intent.putExtra("roomID", roomID);
        intent.putExtra("name", name);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }
}