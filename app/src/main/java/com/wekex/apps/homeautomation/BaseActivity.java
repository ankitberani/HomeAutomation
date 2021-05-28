package com.wekex.apps.homeautomation;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import com.wekex.apps.homeautomation.utils.locationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private static String appUrl = "https://play.google.com/store/apps/details?id=";
    private static Locale myLocale;
    private ProgressDialog progressDialog;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(17432576, 17432577);
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.requestWindowFeature(1);
        try {
            setActionBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: protected */
    public void setActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void showProgressDialog(String msg) {
        try {
            if (this.progressDialog != null && !this.progressDialog.isShowing()) {
                this.progressDialog.setMessage(msg);
                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setCancelable(false);
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: protected */
    public void updateLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public String startgps() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            return new locationUtils(this).fn_getlocation();
        }
        if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET"}, 10);
        }
        return "NA";
    }
}
