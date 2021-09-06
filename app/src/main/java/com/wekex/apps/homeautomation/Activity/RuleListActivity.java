package com.wekex.apps.homeautomation.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.wekex.apps.homeautomation.HomeActivity;
import com.wekex.apps.homeautomation.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class RuleListActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_add_rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_list);
        setupToolbar();
        setup();
    }

    void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.rules));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            Log.e("TAG", "Exception at setupToolbar " + e.getMessage());
        }
    }

    void setup() {
        iv_add_rule = findViewById(R.id.iv_add_rule);
        iv_add_rule.setOnClickListener(this::onClick);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_rule: {
                startActivityForResult(new Intent(RuleListActivity.this, AddRulesActivity.class), 100);
            }
            break;
        }
    }
}