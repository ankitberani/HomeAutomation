package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.data;

public class Touchable_device extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rl_1, rl_2, rl_3, rl_4, rl_5, rl_6, rl_7, rl_8, rl_9;

    View v1_on_ff, v2_on_ff, v3_on_ff, v4_on_ff, v5_on_ff, v6_on_ff, v7_on_ff, v8_on_ff, v9_on_ff;

    TextView tv_view1, tv_view2, tv_view3, tv_view4, tv_view5, tv_view6, tv_view7, tv_view8, tv_view9;

    String _data;
    data object;

    Gson _gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchable_device);

        rl_1 = findViewById(R.id.view_1);
        rl_2 = findViewById(R.id.view_2);
        rl_3 = findViewById(R.id.view_3);
        rl_4 = findViewById(R.id.view_4);

        rl_5 = findViewById(R.id.view_5);
        rl_6 = findViewById(R.id.view_6);
        rl_7 = findViewById(R.id.view_7);
        rl_8 = findViewById(R.id.view_8);
        rl_9 = findViewById(R.id.view_9);


        rl_1.setTag(0);
        rl_2.setTag(0);
        rl_3.setTag(0);
        rl_4.setTag(0);
        rl_5.setTag(0);
        rl_6.setTag(0);
        rl_7.setTag(0);
        rl_8.setTag(0);
        rl_9.setTag(0);


        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_3.setOnClickListener(this);
        rl_4.setOnClickListener(this);
        rl_5.setOnClickListener(this);
        rl_6.setOnClickListener(this);
        rl_7.setOnClickListener(this);
        rl_8.setOnClickListener(this);
        rl_9.setOnClickListener(this);

        v1_on_ff = (View) findViewById(R.id.v1_on_ff);
        v2_on_ff = (View) findViewById(R.id.v2_on_ff);
        v3_on_ff = (View) findViewById(R.id.v3_on_ff);
        v4_on_ff = (View) findViewById(R.id.v4_on_ff);

       /* v1_on_ff.setVisibility(View.GONE);
        v2_on_ff.setVisibility(View.GONE);
        v3_on_ff.setVisibility(View.GONE);
        v4_on_ff.setVisibility(View.GONE);*/

        v5_on_ff = (View) findViewById(R.id.v5_on_ff);
        v6_on_ff = (View) findViewById(R.id.v6_on_ff);
        v7_on_ff = (View) findViewById(R.id.v7_on_ff);
        v8_on_ff = (View) findViewById(R.id.v8_on_ff);
        v9_on_ff = (View) findViewById(R.id.v9_on_ff);

        tv_view1 = findViewById(R.id.tv_view1);
        tv_view2 = findViewById(R.id.tv_view2);
        tv_view3 = findViewById(R.id.tv_view3);
        tv_view4 = findViewById(R.id.tv_view4);
        tv_view5 = findViewById(R.id.tv_view5);
        tv_view6 = findViewById(R.id.tv_view6);
        tv_view7 = findViewById(R.id.tv_view7);
        tv_view8 = findViewById(R.id.tv_view8);
        tv_view9 = findViewById(R.id.tv_view9);


        if (getIntent().hasExtra("_data")) {
            _data = getIntent().getStringExtra("_data");
            object = _gson.fromJson(_data, data.class);
            if (object != null) {
                if (object.getObjd1() != null && object.getObjd1().getName() != null) {
                    tv_view1.setText(object.getObjd1().getName());
                    tv_view5.setText(object.getObjd1().getName());
                } else {
                    tv_view1.setText(getResources().getString(R.string.not_avail));
                    tv_view5.setText(getResources().getString(R.string.not_avail));
                }

                if (object.getObjd2() != null && object.getObjd2().getName() != null) {
                    tv_view2.setText(object.getObjd2().getName());
                    tv_view6.setText(object.getObjd2().getName());
                } else {
                    tv_view2.setText(getResources().getString(R.string.not_avail));
                    tv_view6.setText(getResources().getString(R.string.not_avail));

                }

                if (object.getObjd3() != null && object.getObjd3().getName() != null) {
                    tv_view3.setText(object.getObjd3().getName());
                    tv_view8.setText(object.getObjd3().getName());
                } else {
                    tv_view3.setText(getResources().getString(R.string.not_avail));
                    tv_view8.setText(getResources().getString(R.string.not_avail));
                }

                if (object.getObjd4() != null && object.getObjd4().getName() != null) {
                    tv_view4.setText(object.getObjd4().getName());
                    tv_view9.setText(object.getObjd4().getName());
                } else {
                    tv_view4.setText(getResources().getString(R.string.not_avail));
                    tv_view9.setText(getResources().getString(R.string.not_avail));
                }

                if (object.getObjd1().isState()) {
                    rl_5.setTag(1);
                    v5_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    rl_5.setTag(0);
                    v5_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                }
                if (object.getObjd2().isState()) {
                    rl_6.setTag(1);
                    v6_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    rl_6.setTag(0);
                    v6_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                }

                if (object.getObjd3().isState()) {
                    rl_8.setTag(1);
                    v8_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    rl_8.setTag(0);
                    v8_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                }

                if (object.getObjd4().isState()) {
                    rl_9.setTag(1);
                    v9_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    rl_9.setTag(0);
                    v9_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                }

                if (object.isOnline()) {
                    rl_7.setTag(1);
//                    tv_view7.setText("ON");
                    v7_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
//                    tv_view7.setText("OFF");
                    rl_7.setTag(0);
                    v7_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_1: {
                int tag = (int) rl_1.getTag();
                if (tag == 0) {
                    rl_1.setTag(1);
                    v1_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v1_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_1.setTag(0);
                }
            }
            break;
            case R.id.view_2: {
                int tag = (int) rl_2.getTag();
                if (tag == 0) {
                    rl_2.setTag(1);
                    v2_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v2_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_2.setTag(0);
                }
            }
            break;
            case R.id.view_3: {
                int tag = (int) rl_3.getTag();
                if (tag == 0) {
                    rl_3.setTag(1);
                    v3_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v3_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_3.setTag(0);
                }
            }
            break;
            case R.id.view_4: {
                int tag = (int) rl_4.getTag();
                if (tag == 0) {
                    rl_4.setTag(1);
                    v4_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v4_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_4.setTag(0);
                }
            }
            break;

            case R.id.view_5: {
                int tag = (int) rl_5.getTag();
                if (tag == 0) {
                    rl_5.setTag(1);
                    v5_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v5_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_5.setTag(0);
                }
            }
            break;
            case R.id.view_6: {
                int tag = (int) rl_6.getTag();
                if (tag == 0) {
                    rl_6.setTag(1);
                    v6_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v6_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_6.setTag(0);
                }
            }
            break;
            case R.id.view_7: {
                int tag = (int) rl_7.getTag();
                if (tag == 0) {
                    rl_7.setTag(1);
                    tv_view7.setText("ON");
                    v7_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    tv_view7.setText("OFF");
                    v7_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_7.setTag(0);
                }
            }
            break;
            case R.id.view_8: {
                int tag = (int) rl_8.getTag();
                if (tag == 0) {
                    rl_8.setTag(1);
                    v8_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v8_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_8.setTag(0);
                }
            }
            break;
            case R.id.view_9: {
                int tag = (int) rl_9.getTag();
                if (tag == 0) {
                    rl_9.setTag(1);
                    v9_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_on));
                } else {
                    v9_on_ff.setBackground(getResources().getDrawable(R.drawable.circullar_view_off));
                    rl_9.setTag(0);
                }
            }
            break;

        }
    }
}
