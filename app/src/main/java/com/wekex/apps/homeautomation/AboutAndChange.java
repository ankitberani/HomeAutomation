package com.wekex.apps.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class AboutAndChange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_and_change);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String id = extras.getString("what");

        TextView version = findViewById(R.id.version);
        TextView description = findViewById(R.id.description);

        version.setText(getResources().getString(R.string.appversion));
        assert id != null;
        switch (id) {
            case "about":
                description.setText(getResources().getString(R.string.aboutdes));
                break;
            case "Change_log":
                description.setText(getResources().getString(R.string.changelog));
                break;
        }
    }
}
