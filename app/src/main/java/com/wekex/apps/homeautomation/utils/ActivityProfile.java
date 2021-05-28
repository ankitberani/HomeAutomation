package com.wekex.apps.homeautomation.utils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.PopupMenu.OnDismissListener;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.wekex.apps.homeautomation.BaseActivity;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.io.File;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityProfile extends BaseActivity {
    public static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    ImageView AdharFront;
    final int IMAGE_CAMERA_REQUEST = 8;
    final int IMAGE_GALLERY_REQUEST = 9;
    ImageView _banner;
    ImageView _profilePic;
    ImageView adharBack;
    View currentImageViews;
    /* access modifiers changed from: private */
    public int currentrequest;
    EditText dob;
    TextView email;
    EditText energycost;
    private File filePathImageCamera;
    EditText first_name;
    EditText last_name;
    EditText lat;
    EditText lon;
    ImageView pancard;
    EditText password;
    EditText phone;
    String sdate;
    String sdob;
    String semail;
    String senergycost;
    String sfirst_name;
    String shour;
    String slast_name;
    String slat;
    String slon;
    String smin;
    String smonth;
    String spassword;
    String sphone;
    String stimezone;
    String syear;
    EditText timezone;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_profile);
        this._banner = (ImageView) findViewById(R.id.banner);
        this._profilePic = (ImageView) findViewById(R.id.profilePic);
        this.email = (TextView) findViewById(R.id.email);
        this.phone = (EditText) findViewById(R.id.phone);
        this.first_name = (EditText) findViewById(R.id.first_name);
        this.last_name = (EditText) findViewById(R.id.last_name);
        this.dob = (EditText) findViewById(R.id.dob);
        this.timezone = (EditText) findViewById(R.id.timeZone);
        this.lat = (EditText) findViewById(R.id.lat);
        this.lon = (EditText) findViewById(R.id.lon);
        this.energycost = (EditText) findViewById(R.id.energyCost);
        findViewById(R.id.addprofilepic).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityProfile.this.Addimages(v);
            }
        });
        findViewById(R.id.addBanner).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityProfile.this.Addimages(v);
            }
        });
        setData();
    }

    private void setData() {
        this.email.setText(Constants.savetoShared(this).getString("email", ""));
        this.phone.setText(Constants.savetoShared(this).getString(Constants.PHONE, ""));
        this.first_name.setText(Constants.savetoShared(this).getString(Constants.FNAME, ""));
        this.last_name.setText(Constants.savetoShared(this).getString(Constants.LNAME, ""));
        this.dob.setText(Constants.savetoShared(this).getString(Constants.DOB, ""));
        this.timezone.setText(Constants.savetoShared(this).getString(Constants.TIMEZONE, ""));
        this.lat.setText(Constants.savetoShared(this).getString(Constants.LAT, ""));
        this.lon.setText(Constants.savetoShared(this).getString(Constants.LON, ""));
        this.energycost.setText(Constants.savetoShared(this).getString(Constants.ENERGYCOST, ""));
        String pic = Constants.savetoShared(this).getString(Constants.PROFILEPIC, "");
        String banner = Constants.savetoShared(this).getString(Constants.BANNER, "");
        if (!pic.equals("")) {
            this._profilePic.setImageBitmap(Utils.compressedBitmap(this, pic));
        }
        if (!banner.equals("")) {
            this._banner.setImageBitmap(Utils.compressedBitmap(this, banner));
        }
    }

    public String updateData(View view) {
        this.semail = this.email.getText().toString();
        this.sphone = this.phone.getText().toString();
        this.sfirst_name = this.first_name.getText().toString();
        this.slast_name = this.last_name.getText().toString();
        this.sdob = this.dob.getText().toString();
        this.stimezone = this.timezone.getText().toString();
        this.slat = this.lat.getText().toString();
        this.slon = this.lon.getText().toString();
        this.senergycost = this.energycost.getText().toString();
        if (this.semail.length() < 5) {
            return showret("Email");
        }
        if (this.sphone.length() < 10) {
            return showret("Email");
        }
        if (this.sfirst_name.length() < 3) {
            return showret("Email");
        }
        if (this.slast_name.length() < 3) {
            return showret("Email");
        }
        if (this.sdob.length() < 3) {
            return showret("Email");
        }
        if (this.slat.length() < 3) {
            return showret("Email");
        }
        if (this.slon.length() < 3) {
            return showret("Email");
        }
        if (this.senergycost.length() < 3) {
            return showret("Email");
        }
        try {
            String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
            JSONObject uj = new JSONObject();
            uj.put("Email", this.semail);
            uj.put("Phone", this.sphone);
            uj.put("FirstName", this.sfirst_name);
            uj.put("LastName", this.slast_name);
            uj.put("DOB", this.sdob);
            uj.put(Constants.TIMEZONE, this.stimezone);
            uj.put(Constants.LAT, this.slat);
            uj.put(Constants.LON, this.slon);
            uj.put(Constants.ENERGYCOST, this.senergycost);
            uj.put("ID", user);
            postData(uj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "done";
    }

    private void loginresponse(String res) {
        JSONObject response = Constants.stringToJsonObject(res);
        if (response.has("Phone")) {
            try {
                Constants.savetoShared(this).edit().putString(Constants.FNAME, response.getString("FirstName")).apply();
                Constants.savetoShared(this).edit().putString(Constants.LNAME, response.getString("LastName")).apply();
                Constants.savetoShared(this).edit().putString("email", response.getString("Email")).apply();
                Constants.savetoShared(this).edit().putString(Constants.PHONE, response.getString("Phone")).apply();
                Constants.savetoShared(this).edit().putString(Constants.DOB, response.getString("DOB")).apply();
                Constants.savetoShared(this).edit().putString(Constants.TIMEZONE, response.getString(Constants.TIMEZONE)).apply();
                Constants.savetoShared(this).edit().putString(Constants.LAT, response.getString(Constants.LAT)).apply();
                Constants.savetoShared(this).edit().putString(Constants.LON, response.getString(Constants.LON)).apply();
                Constants.savetoShared(this).edit().putString(Constants.ENERGYCOST, response.getString(Constants.ENERGYCOST)).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void postData(JSONObject data) {
        loginresponse(data.toString());
        String url = APIClient.BASE_URL + "/api/Login/userUpdate";
        StringBuilder sb = new StringBuilder();
        sb.append(data.toString());
        sb.append(" postData: ");
        sb.append(url);
        Log.d("postData", sb.toString());
        RequestQueue requstQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonobj = new JsonObjectRequest(1, url, data, $$Lambda$ActivityProfile$uEiqmXFZIvCnZ_fxBHgxO50oZs.INSTANCE, $$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ.INSTANCE);
        requstQueue.add(jsonobj);
    }

    static /* synthetic */ void lambda$postData$0(JSONObject response) {
        StringBuilder sb = new StringBuilder();
        sb.append("onResponse: ");
        sb.append(response.toString());
        Log.d("postData", sb.toString());
    }

    static /* synthetic */ void lambda$postData$1(VolleyError error) {
        StringBuilder sb = new StringBuilder();
        sb.append("onErrorResponse: ");
        sb.append(error.getCause());
        Log.d("postData", sb.toString());
    }

    private String showret(String email2) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid ");
        sb.append(email2);
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        return email2;
    }

    public void showDatePickerDialog(View v) {
        View view = LayoutInflater.from(this).inflate(R.layout.date_picker, null, false);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        WheelView date = (WheelView) view.findViewById(R.id.date);
        date.setWheelAdapter(new ArrayWheelAdapter(this));
        date.setSkin(WheelView.Skin.Holo);
        date.setWheelData(Constants.createdate(1, 31));
        WheelView month = (WheelView) view.findViewById(R.id.month);
        month.setWheelAdapter(new ArrayWheelAdapter(this));
        month.setSkin(WheelView.Skin.Holo);
        month.setWheelData(Constants.createdate(1, 12));
        WheelView year = (WheelView) view.findViewById(R.id.year);
        year.setWheelAdapter(new ArrayWheelAdapter(this));
        year.setSkin(WheelView.Skin.Holo);
        year.setWheelData(Constants.createdate(1950, 2050));
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        month.setStyle(style);
        date.setStyle(style);
        year.setStyle(style);
        TextView done = (TextView) view.findViewById(R.id.done);
        final WheelView wheelView = month;
        final WheelView wheelView2 = date;
        final WheelView wheelView3 = year;
        final Dialog dialog2 = dialog;
        OnClickListener r2 = new OnClickListener() {
            public void onClick(View v) {
                ActivityProfile.this.smonth = (String) Constants.createdate(1, 12).get(wheelView.getWheelCount() - 1);
                ActivityProfile.this.sdate = (String) Constants.createdate(1, 31).get(wheelView2.getWheelCount() - 1);
                ActivityProfile.this.syear = (String) Constants.createdate(1950, 2050).get(wheelView3.getWheelCount() - 1);
                EditText editText = ActivityProfile.this.dob;
                StringBuilder sb = new StringBuilder();
                sb.append(ActivityProfile.this.sdate);
                sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
                sb.append(ActivityProfile.this.smonth);
                sb.append(MqttTopic.TOPIC_LEVEL_SEPARATOR);
                sb.append(ActivityProfile.this.syear);
                editText.setText(sb.toString());
                dialog2.dismiss();
            }
        };
        done.setOnClickListener(r2);
        dialog.show();
    }

    public void getloacation(View view) {
        Log.d("dsfjkdsh", "getloacation: ");
        Toast.makeText(this, "sss", Toast.LENGTH_SHORT).show();
        new locationUtils(this).fn_getlocation();
        this.lat.setText(String.valueOf(Constants.latitude));
        this.lon.setText(String.valueOf(Constants.longitude));
    }

    public void Addimages(final View view) {
        int id = view.getId();
        if (id == R.id.addBanner) {
            this.currentImageViews = this._banner;
        } else if (id == R.id.addprofilepic) {
            this.currentImageViews = this._profilePic;
        }
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_images, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId != R.id.fromCamera) {
                    if (itemId == R.id.fromGallery && ActivityProfile.this.verifyStoragePermissions()) {
                        ActivityProfile.this.currentrequest = 9;
                        ActivityProfile.this.photoGalleryIntent();
                    }
                } else if (ActivityProfile.this.verifyStoragePermissions()) {
                    ActivityProfile.this.currentrequest = 8;
                    ActivityProfile.this.photoCameraIntent();
                }
                return true;
            }
        });
        popup.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(PopupMenu menu) {
                view.setVisibility(View.VISIBLE);
            }
        });
        popup.show();
    }

    public boolean verifyStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == 0) {
            switch (this.currentrequest) {
                case 8:
                    photoCameraIntent();
                    return;
                case 9:
                    photoGalleryIntent();
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void photoCameraIntent() {
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 9);
    }

    /* access modifiers changed from: private */
    public void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), 9);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9 && resultCode == -1 && data != null && data.getData() != null) {
            Log.d("Acrticv", "onActivityResult: fragment");
            String filepath = PathUtil.getFile(this, data.getData()).getAbsolutePath();
            ((ImageView) this.currentImageViews).setImageBitmap(Utils.compressedBitmap(this, filepath));
            if (this.currentImageViews.getTag().toString().equals(Constants.BANNER)) {
                Constants.savetoShared(this).edit().putString(Constants.BANNER, filepath).apply();
            } else {
                Constants.savetoShared(this).edit().putString(Constants.PROFILEPIC, filepath).apply();
            }
        }
    }
}
