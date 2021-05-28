package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.adapter.ViewAllLogsAdapter;
import com.wekex.apps.homeautomation.model.data;

import java.util.ArrayList;

public class ViewLogs extends AppCompatActivity {

    private ProgressDialog progressDialog;

    Toolbar toolbar;
    RecyclerView _rv_logs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Logs");

        _rv_logs = findViewById(R.id.rv_logs);

        if (getIntent().hasExtra("dno")) {
            String dno = getIntent().getStringExtra("dno");
            toolbar.setTitle(dno + " Logs");
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            getDeviceLogs(dno);

        }
    }


    void getDeviceLogs(String dno) {

        APIService apiInterface = APIClient.getClient_1().create(APIService.class);
//        String url = "http://209.58.164.151:88/api/Get/triggerGroup?ID=" + groupId + "&data=" + object.toString();
        String url = APIClient.BASE_URL + "/api/Get/GetDeviceLog?dno=" + dno + "&timef=1477468444&timet=1677468444";
        Log.e("TAGG", "Final URL " + url);
        Observable<ArrayList<data>> observable = apiInterface.getDeviceLogs(url);
        showProgressDialog(getString(R.string.please_wait));
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<data>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<data> successResponse) {
                Log.e("TAGG", "OnNext called successResponse ");
                if (successResponse != null) {
                    Log.e("TAG", "Total Log received " + successResponse.size());


                    /*ArrayList<data> _data=new ArrayList<>();

                    data _obj=new data();

                    _obj.setTime(123456);
                    _obj.setOnline(true);

                    _data.add(_obj);*/
                    ViewAllLogsAdapter _adapter = new ViewAllLogsAdapter(successResponse, ViewLogs.this);

                    GridLayoutManager mLayoutManager = new GridLayoutManager(ViewLogs.this, 1);
                    _rv_logs.setLayoutManager(mLayoutManager);
                    _rv_logs.setAdapter(_adapter);
                    hideProgressDialog();
                } else if (successResponse.size() == 0) {
                    Toast.makeText(ViewLogs.this, "Logs not found", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                Log.e("TAGG", "OnError Called " + e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void showProgressDialog(String msg) {
        try {
            Log.e("TAG", "showProgressDialog called");
            if (this.progressDialog != null && !this.progressDialog.isShowing()) {
                this.progressDialog.setMessage(msg);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
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
