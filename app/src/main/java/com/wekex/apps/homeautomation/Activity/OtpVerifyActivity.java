package com.wekex.apps.homeautomation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.model.SuccessResponse;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    EditText edtphone, edtOtp;
    LinearLayout ll_resend_otp;
    CardView cardConfirmOtp;
    String pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);
        progressBar = findViewById(R.id.progressBar);
        cardConfirmOtp = findViewById(R.id.cardConfirmOtp);
        ll_resend_otp = findViewById(R.id.ll_resend_otp);
        edtphone = findViewById(R.id.phone);
        edtOtp = findViewById(R.id.otp);
        if (getIntent() != null && getIntent().hasExtra("phone")) {
            edtphone.setText(getIntent().getStringExtra("phone"));
        }
        if (getIntent() != null && getIntent().hasExtra("pass")) {
            pass=getIntent().getStringExtra("phone");
        }
        ll_resend_otp.setOnClickListener(this::onClick);
        cardConfirmOtp.setOnClickListener(this::onClick);
    }


    private void resendOTP(String mobileNumber) {
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
        String url = APIClient.BASE_URL + "/UserRegistration/ResendOTP?mobile=" + mobileNumber;
        Call<String> _call = apiInterface.resendOTP(url);
        progressBar.setVisibility(View.VISIBLE);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    Toast.makeText(OtpVerifyActivity.this, response.toString() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_resend_otp: {
                if (edtphone.getText().toString().trim().isEmpty()) {
                    edtphone.setError(getString(R.string.required));
                } else {
                    resendOTP(edtphone.getText().toString());
                }
            }
            break;
            case R.id.cardConfirmOtp: {
                if (edtphone.getText().toString().trim().isEmpty()) {
                    edtphone.setError(getString(R.string.required));
                } else if (edtOtp.getText().toString().trim().isEmpty()) {
                    edtOtp.setError(getString(R.string.required));
                } else {
                    verifyOTP(edtphone.getText().toString());
                }
            }
            break;
        }
    }

    private void verifyOTP(String mobileNumber) {
        APIService apiInterface = APIClient.getClientForStringResponse().create(APIService.class);
//        "http://209.58.164.151:88/api/UserRegistration/VerifyRequestOTP?mobile=9870854226&otpcode=23456&pass=9870854226"/
        String url = APIClient.BASE_URL + "/UserRegistration/VerifyRequestOTP?mobile=" + mobileNumber + "&otpcode=" + edtOtp.getText().toString() + "&pass=" + pass;
//        http://smartyhome.in/api/UserRegistration/VerifyRequestOTP?mobile=9898322555&otpcode=724
//        http://smartyhome.in/api/UserRegistration/VerifyRequestOTP?mobile=9870854226&otpcode=23456&pass=9870854226%22
        Call<String> _call = apiInterface.resendOTP(url);
        progressBar.setVisibility(View.VISIBLE);
        _call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e("TAG", "OnFailure Called " + t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}