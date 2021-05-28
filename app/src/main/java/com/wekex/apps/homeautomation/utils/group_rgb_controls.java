package com.wekex.apps.homeautomation.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;
import com.wekex.apps.homeautomation.BaseActivity;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.helperclass.MqttMessageService;
import com.wekex.apps.homeautomation.secondaryActivity.rgb_controls;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.Toolbar;


public class group_rgb_controls extends BaseActivity implements OnLongClickListener, OnClickListener {
    String TAG = "rgb_controls_activity";
    SeekBar _brightness;
    GradienSeekBar alphaSlider;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            group_rgb_controls.this.updateUI(intent);
        }
    };
    /* access modifiers changed from: private */
    public int colorpic;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private String dno1;

    /* renamed from: id */
    String f197id;
    boolean isChanged = true;
    TextView onoff_colorpicker;
    ColorPicker picker;
    LinearLayout rbg_off_layout;
    LinearLayout rbg_on_layout;
    String state;
    SeekBar warmwhite;

    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_rgb_controls);
        this.f197id = getIntent().getStringExtra("id");
        String pname1 = getIntent().getStringExtra("name");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pname1);
//        toolbar.setTitleTextAppearance(this, R.style.style_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        this.alphaSlider = (GradienSeekBar) findViewById(R.id.alphaSlider);

        this.onoff_colorpicker = (TextView) findViewById(R.id.onoff_colorpicker);
        this.onoff_colorpicker.setTag(Boolean.valueOf(true));
        this.rbg_on_layout = (LinearLayout) findViewById(R.id.rbg_on_layout);
        this.rbg_off_layout = (LinearLayout) findViewById(R.id.rbg_off_layout);
        initDiy();
        this.picker = (ColorPicker) findViewById(R.id.picker11_new);
        this.picker.setTouchAnywhereOnColorWheelEnabled(true);

        picker.setShowOldCenterColor(false);

        this.colorpic = this.picker.getColor();
        /*this.picker.addOnColorSelectedListener(new OnColorSelectedListener() {
            public void onColorSelected(int color) {
                Log.d(group_rgb_controls.this.TAG, "onColorSelected: ");
                group_rgb_controls.this.colorpic = color;
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);
                group_rgb_controls.this.alphaSlider.setColor(color);
                group_rgb_controls.this.setRBG();
                group_rgb_controls.this.triggerscene();
            }
        });*/


        picker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                Log.e("TAGG", "Color is setOnColorChangedListener " + color);
                try {
                    Log.d(TAG, "onColorSelected: ");
                    colorpic = color;
                    Constants.red = Color.red(color);
                    Constants.green = Color.green(color);
                    Constants.blue = Color.blue(color);
                    alphaSlider.setColor(color);
                    setRBG();
                    triggerscene();
                } catch (Exception e) {
                    Log.e("TAGGG", "Exception at on color " + e.getMessage(), e);
                }
            }
        });
        this.alphaSlider.setOnALphaChangeListener(new GradienSeekBar.OnAlphaChangeListener() {
            public void onAlphaColorChnage(int color) {
                String str = group_rgb_controls.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("r ");
                sb.append(Color.red(color));
                sb.append(" g ");
                sb.append(Color.green(color));
                sb.append(" b ");
                sb.append(Color.blue(color));
                sb.append("onAlphaColorChnage: ");
                sb.append(color);
                Log.d(str, sb.toString());
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);
                group_rgb_controls.this.setRBG();
            }

            public void onAlphaColorChnaged(boolean alpha) {
                group_rgb_controls.this.triggerscene();
            }
        });

       /* this.picker.addOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int color) {
                String str = group_rgb_controls.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onColor: ");
                sb.append(color);
                Log.d(str, sb.toString());
                group_rgb_controls.this.colorpic = color;
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);
                group_rgb_controls.this.setRBG();
            }
        });*/

        final TextView brigtnessTV = (TextView) findViewById(R.id.brightnessTV);
        this._brightness = (SeekBar) findViewById(R.id.brightness);
        if (Constants.brightness == 0) {
            Constants.brightness = 100;
        }
        this._brightness.setProgress(Constants.brightness);
        this._brightness.incrementProgressBy(1);
        this._brightness.setMax(100);
//        Constants.brightness = 100;

        brigtnessTV.setText("Brightness " + Constants.brightness + "%");
        this._brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                Constants.brightness = progress;
                TextView textView = brigtnessTV;
                StringBuilder sb = new StringBuilder();
                sb.append("Brightness ");
                sb.append(progress);
                sb.append("%");
                textView.setText(sb.toString());
                String str = group_rgb_controls.this.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("onProgressChanged: bb ");
                sb2.append(Constants.brightness);
                Log.d(str, sb2.toString());
                Log.e("TAGGG", "progress " + progress);

                if (progress <= 10) {
                    _brightness.setProgress(10);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                group_rgb_controls.this.triggerscene();
            }
        });
    }


    public void initDiy() {
        TextView diy1 = (TextView) findViewById(R.id.diy1);
        TextView diy2 = (TextView) findViewById(R.id.diy2);
        TextView diy3 = (TextView) findViewById(R.id.diy3);
        TextView diy4 = (TextView) findViewById(R.id.diy4);
        TextView diy5 = (TextView) findViewById(R.id.diy5);
        Editor edit = Constants.savetoShared(this).edit();
        StringBuilder sb = new StringBuilder();
        sb.append(this.dno1);
        sb.append("diy1");
        edit.putString(sb.toString(), getJsonStringWhite()).apply();
        diy2.setOnLongClickListener(this);
        diy3.setOnLongClickListener(this);
        diy4.setOnLongClickListener(this);
        diy5.setOnLongClickListener(this);
        diy1.setOnClickListener(this);
        diy2.setOnClickListener(this);
        diy3.setOnClickListener(this);
        diy4.setOnClickListener(this);
        diy5.setOnClickListener(this);
        diy1.setBackgroundColor(setColor(diy1));
        diy2.setBackgroundColor(setColor(diy2));
        diy3.setBackgroundColor(setColor(diy3));
        diy4.setBackgroundColor(setColor(diy4));
        diy5.setBackgroundColor(setColor(diy5));
    }

    public int setColor(View v) {
        int col = ((ColorDrawable) v.getBackground()).getColor();
        SharedPreferences savetoShared = Constants.savetoShared(this);
        StringBuilder sb = new StringBuilder();
        sb.append(this.dno1);
        sb.append(v.getTag());
        String jsonstring = savetoShared.getString(sb.toString(), "false");
        if (!jsonstring.equals("false")) {
            return Integer.parseInt(Constants.jsonObjectreader(jsonstring, "color"));
        }
        Constants.red = Color.red(col);
        Constants.green = Color.green(col);
        Constants.blue = Color.blue(col);
        this.colorpic = col;
        Editor edit = Constants.savetoShared(this).edit();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.dno1);
        sb2.append(v.getTag());
        edit.putString(sb2.toString(), getJsonString()).apply();
        return col;
    }

    public boolean onLongClick(View v) {
        v.setBackgroundColor(this.colorpic);
        Editor edit = Constants.savetoShared(this).edit();
        StringBuilder sb = new StringBuilder();
        sb.append(this.dno1);
        sb.append(v.getTag());
        edit.putString(sb.toString(), getJsonString()).apply();
        return false;
    }

    private String getJsonString() {
        JSONObject jsonObjectD1 = new JSONObject();
        try {
            jsonObjectD1.put("r", Constants.red);
            jsonObjectD1.put("g", Constants.green);
            jsonObjectD1.put("b", Constants.blue);
            jsonObjectD1.put("w", Constants.white);
            jsonObjectD1.put("ww", Constants.warm_white);
            jsonObjectD1.put("br", Constants.brightness);
            jsonObjectD1.put("color", this.colorpic);
        } catch (Exception e) {
        }
        return jsonObjectD1.toString();
    }

    private String getJsonStringWhite() {
        JSONObject jsonObjectD1 = new JSONObject();
        try {
            jsonObjectD1.put("r", 255);
            jsonObjectD1.put("g", 255);
            jsonObjectD1.put("b", 255);
            jsonObjectD1.put("w", 0);
            jsonObjectD1.put("ww", 255);
            jsonObjectD1.put("br", Constants.brightness);
            jsonObjectD1.put("color", 0);
        } catch (Exception e) {
        }
        return jsonObjectD1.toString();
    }

    public void onClick(View v) {
        SharedPreferences savetoShared = Constants.savetoShared(this);
        StringBuilder sb = new StringBuilder();
        sb.append(this.dno1);
        sb.append(v.getTag());
        String jsonstring = savetoShared.getString(sb.toString(), "false");
        if (!jsonstring.equals("false")) {
            Constants.red = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "r"));
            Constants.green = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "g"));
            Constants.blue = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "b"));
//            Constants.brightness = Math.round(Float.parseFloat(Constants.jsonObjectreader(jsonstring, "br")));
//            Constants.brightness = _brightness.getProgress();
            Constants.warm_white = Integer.parseInt(Constants.jsonObjectreader(jsonstring, "ww"));
            setRBG();
            triggerscene();
            return;
        }
        ((ColorDrawable) v.getBackground()).getColor();
    }

    public void setRBG() {
        TextView r = (TextView) findViewById(R.id.red);
        TextView b = (TextView) findViewById(R.id.blue);
        TextView g = (TextView) findViewById(R.id.green);
        StringBuilder sb = new StringBuilder();
        sb.append("R ");
        sb.append(String.valueOf(Constants.red));
        r.setText(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("B ");
        sb2.append(String.valueOf(Constants.blue));
        b.setText(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("G ");
        sb3.append(String.valueOf(Constants.green));
        g.setText(sb3.toString());
        r.setBackgroundColor(Color.argb(255, Constants.red, 0, 0));
        b.setBackgroundColor(Color.argb(255, 0, 0, Constants.blue));
        g.setBackgroundColor(Color.argb(255, 0, Constants.green, 0));
    }

    public void power(View view) {
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onOptionsItemSelected: ");
        sb.append(this.onoff_colorpicker.getTag());
        Log.d(str, sb.toString());
        if (!((Boolean) this.onoff_colorpicker.getTag()).booleanValue()) {
            this.onoff_colorpicker.setTag(Boolean.valueOf(true));
            this.onoff_colorpicker.setText("ON");
            this.rbg_off_layout.setVisibility(View.INVISIBLE);
            this.rbg_on_layout.setVisibility(View.VISIBLE);
            triggerscene();
            return;
        }
        this.onoff_colorpicker.setTag(Boolean.valueOf(false));
        this.onoff_colorpicker.setText("OFF");
        this.rbg_on_layout.setVisibility(View.INVISIBLE);
        this.rbg_off_layout.setVisibility(View.VISIBLE);
        triggerscene();
    }

    public void finish(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Added");
        setResult(-1, returnIntent);
        finish();
    }

    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "onResume: ");
        registerReceiver(this.broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(this.broadcastReceiver);
    }

    /* access modifiers changed from: private */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateUI(Intent r7) {
        /*
            r6 = this;
            java.lang.String r0 = "datafromService"
            java.lang.String r0 = r7.getStringExtra(r0)
            com.wekex.apps.homeautomation.utils.RoomControl.udpdateJSON(r6, r0)
            r1 = 0
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0098 }
            r2.<init>(r0)     // Catch:{ JSONException -> 0x0098 }
            r1 = r2
            java.lang.String r2 = "data"
            boolean r2 = r1.has(r2)     // Catch:{ JSONException -> 0x0098 }
            if (r2 == 0) goto L_0x001a
            goto L_0x0097
        L_0x001a:
            java.lang.String r2 = "duser"
            boolean r2 = r1.has(r2)     // Catch:{ JSONException -> 0x0098 }
            if (r2 == 0) goto L_0x0024
            goto L_0x0097
        L_0x0024:
            java.lang.String r2 = "room"
            boolean r2 = r1.has(r2)     // Catch:{ JSONException -> 0x0098 }
            if (r2 == 0) goto L_0x002e
            goto L_0x0097
        L_0x002e:
            java.lang.String r2 = "name"
            boolean r2 = r1.has(r2)     // Catch:{ JSONException -> 0x0098 }
            if (r2 == 0) goto L_0x0037
            goto L_0x0097
        L_0x0037:
            java.lang.String r2 = "power"
            boolean r2 = r1.has(r2)     // Catch:{ JSONException -> 0x0098 }
            if (r2 == 0) goto L_0x0040
            goto L_0x0097
        L_0x0040:
            java.lang.String r2 = "dtype"
            java.lang.String r2 = com.wekex.apps.homeautomation.utils.Constants.jsonObjectreader(r0, r2)     // Catch:{ JSONException -> 0x0098 }
            java.lang.String r3 = r6.TAG     // Catch:{ JSONException -> 0x0098 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0098 }
            r4.<init>()     // Catch:{ JSONException -> 0x0098 }
            java.lang.String r5 = "updateUI2: "
            r4.append(r5)     // Catch:{ JSONException -> 0x0098 }
            r4.append(r0)     // Catch:{ JSONException -> 0x0098 }
            java.lang.String r4 = r4.toString()     // Catch:{ JSONException -> 0x0098 }
            android.util.Log.d(r3, r4)     // Catch:{ JSONException -> 0x0098 }
            r3 = -1
            int r4 = r2.hashCode()     // Catch:{ JSONException -> 0x0098 }
            switch(r4) {
                case 49: goto L_0x0083;
                case 50: goto L_0x0079;
                case 51: goto L_0x006f;
                case 52: goto L_0x0065;
                default: goto L_0x0064;
            }     // Catch:{ JSONException -> 0x0098 }
        L_0x0064:
            goto L_0x008c
        L_0x0065:
            java.lang.String r4 = "4"
            boolean r4 = r2.equals(r4)     // Catch:{ JSONException -> 0x0098 }
            if (r4 == 0) goto L_0x0064
            r3 = 3
            goto L_0x008c
        L_0x006f:
            java.lang.String r4 = "3"
            boolean r4 = r2.equals(r4)     // Catch:{ JSONException -> 0x0098 }
            if (r4 == 0) goto L_0x0064
            r3 = 2
            goto L_0x008c
        L_0x0079:
            java.lang.String r4 = "2"
            boolean r4 = r2.equals(r4)     // Catch:{ JSONException -> 0x0098 }
            if (r4 == 0) goto L_0x0064
            r3 = 1
            goto L_0x008c
        L_0x0083:
            java.lang.String r4 = "1"
            boolean r4 = r2.equals(r4)     // Catch:{ JSONException -> 0x0098 }
            if (r4 == 0) goto L_0x0064
            r3 = 0
        L_0x008c:
            switch(r3) {
                case 0: goto L_0x0096;
                case 1: goto L_0x0090;
                case 2: goto L_0x0090;
                case 3: goto L_0x0090;
                default: goto L_0x008f;
            }     // Catch:{ JSONException -> 0x0098 }
        L_0x008f:
            goto L_0x0097
        L_0x0090:
            java.lang.String r3 = r6.dno1     // Catch:{ JSONException -> 0x0098 }
            r0.contains(r3)     // Catch:{ JSONException -> 0x0098 }
            goto L_0x0097
        L_0x0096:
        L_0x0097:
            goto L_0x009c
        L_0x0098:
            r2 = move-exception
            r2.printStackTrace()
        L_0x009c:
            return
        */
//        throw new UnsupportedOperationException("Method not decompiled: com.wekex.apps.homeautomation.secondaryActivity.group_rgb_controls.updateUI(android.content.Intent):void");
    }

    public void warmWhite(View view) {
        Constants.red = 255;
        Constants.green = 255;
        Constants.blue = 255;
        Constants.brightness = 1;
        Constants.warm_white = 255;
        Constants.white = 100;
        DtypeViews.publishRBGcolor(this, this.dno1, Boolean.valueOf(true));
    }

    public void white(View view) {
        Constants.red = 255;
        Constants.green = 255;
        Constants.blue = 255;
        Constants.brightness = 1;
        Constants.warm_white = 255;
        Constants.white = 100;
        DtypeViews.publishRBGcolor(this, this.dno1, Boolean.valueOf(true));
    }

    public void colorTab(View view) {
        findViewById(R.id.rgblayout).setVisibility(View.VISIBLE);
        findViewById(R.id.whitelayout).setVisibility(View.GONE);
    }

    public void whiteTab(View view) {
        findViewById(R.id.rgblayout).setVisibility(View.GONE);
        findViewById(R.id.whitelayout).setVisibility(View.VISIBLE);
        ((SeekArc) findViewById(R.id.seekArcWhite)).setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                String str = group_rgb_controls.this.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onProgressChanged: ");
                sb.append(i);
                Log.d(str, sb.toString());
                Constants.red = 0;
                Constants.green = 0;
                Constants.blue = 0;
                Constants.white = i;
            }

            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            public void onStopTrackingTouch(SeekArc seekArc) {
                group_rgb_controls.this.triggerscene();
            }
        });
    }

    /* access modifiers changed from: private */
    public void triggerscene() {
        showProgressDialog("Trigering...");
        JSONObject jsonObjectD1 = new JSONObject();
        try {
            jsonObjectD1.put("state", (Boolean) this.onoff_colorpicker.getTag());
            jsonObjectD1.put("r", Constants.red);
            jsonObjectD1.put("g", Constants.green);
            jsonObjectD1.put("b", Constants.blue);
            jsonObjectD1.put("w", Constants.white);
            jsonObjectD1.put("ww", Constants.warm_white);
            String str = "br";
            double d = (double) Constants.brightness;
            Double.isNaN(d);
            jsonObjectD1.put(str, d / 100.0d);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(APIClient.BASE_URL + "/api/Get/triggerGroup?ID=");
        sb.append(this.f197id);
        sb.append("&data=");
        sb.append(jsonObjectD1.toString());
        String url = sb.toString();
        String str2 = this.TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("triggerscene: ");
        sb2.append(url);
        Log.d(str2, sb2.toString());
        Volley.newRequestQueue(this).add(new StringRequest(0, url, new Listener() {
            public final void onResponse(Object obj) {
                group_rgb_controls.lambda$triggerscene$0(group_rgb_controls.this, (String) obj);
            }
        }, new ErrorListener() {
            public final void onErrorResponse(VolleyError volleyError) {
                group_rgb_controls.lambda$triggerscene$1(group_rgb_controls.this, volleyError);
            }
        }));
    }

    public static /* synthetic */ void lambda$triggerscene$0(group_rgb_controls group_rgb_controls, String response) {
        String str = group_rgb_controls.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("triggerscene: ");
        sb.append(response);
        Log.d(str, sb.toString());
        group_rgb_controls.hideProgressDialog();
    }

    public static /* synthetic */ void lambda$triggerscene$1(group_rgb_controls group_rgb_controls, VolleyError error) {
        String str = group_rgb_controls.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("triggerscene: ");
        sb.append(error.getCause());
        Log.d(str, sb.toString());
        group_rgb_controls.hideProgressDialog();
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
}
