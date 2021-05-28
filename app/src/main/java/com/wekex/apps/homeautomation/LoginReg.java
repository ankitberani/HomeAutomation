package com.wekex.apps.homeautomation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.wekex.apps.homeautomation.Activity.RemoteBrandActivity;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.model.LoginRequestModel;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.locationUtils;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static com.wekex.apps.homeautomation.utils.Constants.DOB;
import static com.wekex.apps.homeautomation.utils.Constants.EMAIL;
import static com.wekex.apps.homeautomation.utils.Constants.ENERGYCOST;
import static com.wekex.apps.homeautomation.utils.Constants.FNAME;
import static com.wekex.apps.homeautomation.utils.Constants.LAT;
import static com.wekex.apps.homeautomation.utils.Constants.LNAME;
import static com.wekex.apps.homeautomation.utils.Constants.LON;
import static com.wekex.apps.homeautomation.utils.Constants.PHONE;
import static com.wekex.apps.homeautomation.utils.Constants.TIMEZONE;
import static com.wekex.apps.homeautomation.utils.Constants.USER_ID;
import static com.wekex.apps.homeautomation.utils.Constants.createdate;
import static com.wekex.apps.homeautomation.utils.Constants.savetoShared;
import static com.wekex.apps.homeautomation.utils.Constants.stringToJsonObject;

public class LoginReg extends AppCompatActivity {
    String _email, _password, _phone, _fist_name, _last_name, _dob, _confirm_password;
    static EditText dob;
    EditText email, password, phone, first_name, last_name;
    ShowHidePasswordEditText confirm_password;
    private AwesomeValidation awesomeValidation;
    private String TAG = "LoginRegActivity";
    String shour, smin, sdate, smonth, syear;

    Spinner _spn_domain;
    Utility _utility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_reg_login);
        _utility = new Utility(LoginReg.this);
        startgps();
    }

    public void signupLayout(View view) {
        setContentView(R.layout.reg_layout);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        phone = findViewById(R.id.phone);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        dob = findViewById(R.id.dob);
    }

    public void loginLayout(View view) {
        setContentView(R.layout.activity_login_reg);
    }

    public void otpVerify(View view) {

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        EditText email, password;
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        _email = gettext(email);
        _password = gettext(password);
        awesomeValidation.addValidation(LoginReg.this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerror);
        // String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.errpassword);

        if (awesomeValidation.validate()) {
            Log.d(TAG, "Signup Validation Successful");
            JSONObject data = getJSONdataLogin(_email, _password);
            Log.d(TAG, "login: " + data.toString());
//            postData("http://209.58.164.151:88/api/Login/userLogin", data);
            postData(APIClient.BASE_URL + "/api/Login/userLogin", data);
        }
        // if (_email,va)
    }

    public void login(View view) {

        _spn_domain = (Spinner) findViewById(R.id._spn_domain);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        EditText email, password;
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        _email = gettext(email);
        _password = gettext(password);
        awesomeValidation.addValidation(LoginReg.this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerror);
        // String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.errpassword);
        APIClient.BASE_URL = getResources().getStringArray(R.array.domains)[_spn_domain.getSelectedItemPosition()];
        _utility.putString("selected_domain", APIClient.BASE_URL);
        if (awesomeValidation.validate()) {
            Log.d(TAG, "signup: Successful");
            JSONObject data = getJSONdataLogin(_email, _password);
            Log.d(TAG, "login: " + data.toString());
//            sendPost("http://209.58.164.151:88/api/Login/userLogin", data);
            sendPost(APIClient.BASE_URL + "/api/Login/userLogin", data);
        }
        // if (_email,va)
    }

    public void signup(View view) {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        _email = gettext(email);
        _password = gettext(password);
        _confirm_password = gettext(confirm_password);
        Constants.mobNo = _phone = gettext(phone);
        _fist_name = gettext(first_name);
        _last_name = gettext(last_name);
        _dob = gettext(dob);
        awesomeValidation.addValidation(this, R.id.first_name, "[a-zA-Z\\s]+", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.last_name, "[a-zA-Z\\s]+", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.phone, RegexTemplate.TELEPHONE, R.string.mobileerror);
        awesomeValidation.addValidation(LoginReg.this, R.id.email, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerror);
        //String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
        awesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.errpassword);
        awesomeValidation.addValidation(this, R.id.collapseActionView, R.id.password, R.string.errpassword);
        awesomeValidation.addValidation(this, R.id.dob, RegexTemplate.NOT_EMPTY, R.string.errpassword);


        if (awesomeValidation.validate()) {
            Log.d(TAG, "signup: Successful");
            JSONObject data = getJSONdataSignUP(_fist_name, _last_name, _phone, _email, _password, _dob);

            Log.e(TAG, "signup: " + data.toString());
//            postData("http://209.58.164.151:88/api/UserRegistration/newUserRegistration", data);
            postData(APIClient.BASE_URL + "/api/UserRegistration/newUserRegistration", data);
        }

    }

    private JSONObject getJSONdataSignUP(String fist_name, String last_name, String phone, String email, String password, String dob) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("FirstName", fist_name);
            obj.put("LastName", last_name);
            obj.put("Email", email);
            obj.put("Phone", phone);
            obj.put("DOB", dob);
            obj.put("Password", password);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("kuch", "createJson: " + obj.toString());
        return obj;
    }

    private JSONObject getJSONdataLogin(String email, String password) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("user", email);
            //   obj.put("Phone", phone);
            obj.put("Password", password);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("kuch", "createJson: " + obj.toString());
        return obj;
    }

    private String gettext(EditText EDName) {
        return EDName.getText().toString();
    }

    public void postData(String url, JSONObject data) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue requstQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, data,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (response.has(FNAME)) {
                        try {
                            savetoShared(LoginReg.this).edit().putString(FNAME, response.getString(FNAME)).apply();
                            savetoShared(LoginReg.this).edit().putString(LNAME, response.getString(LNAME)).apply();
                            savetoShared(LoginReg.this).edit().putString(EMAIL, response.getString(EMAIL)).apply();
                            savetoShared(LoginReg.this).edit().putString(PHONE, response.getString(PHONE)).apply();
                            savetoShared(LoginReg.this).edit().putString(DOB, response.getString(DOB)).apply();
                            savetoShared(LoginReg.this).edit().putString(USER_ID, response.getString(USER_ID)).apply();
                            //Utility.setIsFresh(this, true);
                            startActivity(new Intent(LoginReg.this, HomeActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (response.has("success")) {
                        try {
                            if (response.getString("success").equals("true")) {
                                //  getRequest("http://209.58.164.151:88/api/UserRegistration/SendOTPMobile?mobile="+Constants.mobNo);
                                loginLayout(findViewById(R.id.last_name));
                                // Toast.makeText(LoginReg.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginReg.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    //Toast.makeText(LoginReg.this, "", Toast.LENGTH_SHORT).show();
                    // loginLayout(findViewById(R.id.last_name));
                    Toast.makeText(LoginReg.this, "Error User Exists" + error.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: Error  " + error.getMessage());
                }
        );
        requstQueue.add(jsonobj);

    }

    public void getRequest(String url) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue requstQueue = Volley.newRequestQueue(this);
        StringRequest getReq = new StringRequest(Request.Method.GET, url,
                response -> {
                    // response
                    Log.d("Response", response);
                },
                error -> {
                    // error
                    Log.d("Error.Response", error.getMessage());
                }
        );


    }

    public void showDatePickerDialog(View v) {
        WheelView date, month, year;


        View view = LayoutInflater.from(this).inflate(R.layout.date_picker, null, false);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(view);


        date = view.findViewById(R.id.date);
        date.setWheelAdapter(new ArrayWheelAdapter(this));
        date.setSkin(WheelView.Skin.Holo);
        date.setWheelData(createdate(1, 31));


        month = view.findViewById(R.id.month);
        month.setWheelAdapter(new ArrayWheelAdapter(this));
        month.setSkin(WheelView.Skin.Holo);
        month.setWheelData(createdate(1, 12));


        year = view.findViewById(R.id.year);
        year.setWheelAdapter(new ArrayWheelAdapter(this));
        year.setSkin(WheelView.Skin.Holo);
        year.setWheelData(createdate(1950, 2050));

        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        month.setStyle(style);
        date.setStyle(style);
        year.setStyle(style);

        TextView done = view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smonth = createdate(1, 12).get(month.getWheelCount() - 1);
                sdate = createdate(1, 31).get(date.getWheelCount() - 1);
                syear = createdate(1950, 2050).get(year.getWheelCount() - 1);
                dob.setText(sdate + "/" + smonth + "/" + syear);
                dialog.dismiss();
            }
        });
        dialog.show();


        /*
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");*/
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            dob.setText(day + "/" + month + 1 + "/" + year);
        }

    }

    public void sendPost(String url1, JSONObject data) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        Log.e("TAG", "SendPost Data url1 " + url1 + " data " + data.toString());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*try {
                    URL url = new URL(url1);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON aa", data.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(data.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG1", conn.getResponseMessage());
                    BufferedReader br;
                    if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String output;
                    StringBuilder sb = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Log.i("JSON ab", sb.toString());
                    loginresponse(sb.toString());
                    responseData(sb.toString());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                try {
                    APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);

                    LoginRequestModel _model = new LoginRequestModel(data.getString("user"), data.getString("Password"));
                    Call<String> _call = _apiService.loginUser(_model);


                    _call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                            try {
                                if (response.body() == null) {
                                    JSONObject _object = new JSONObject(response.errorBody().string());
                                    Log.e("TAGGG", "OnResponse of Login " + _object.toString());
                                    if (_object.has("success") && _object.getBoolean("success") == false) {
                                        Toast.makeText(LoginReg.this, _object.getString("error"), Toast.LENGTH_SHORT).show();
                                    } else if (_object.has("Message")) {
                                        Toast.makeText(LoginReg.this, _object.getString("Message"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    try {
                                        JSONObject _object = new JSONObject(response.body());
                                        if (_object.has("status") && _object.getBoolean("status") != true) {
                                            Toast.makeText(LoginReg.this, _object.getString("data"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            loginresponse(response.body());
                                            responseData(response.body());
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            } catch (Exception e) {
                                Log.e("TAG", "Exception at login " + e.getMessage());
                            }
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("TAGGG", "OnResponse onFailure " + t.getMessage());
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", "Exception at login " + e.getCause() + " " + e.getMessage());
                }
            }
        });

        thread.start();
    }

    private void loginresponse(String res) {
        JSONObject response = stringToJsonObject(res);
        if (response.has(FNAME)) {
            try {
                savetoShared(LoginReg.this).edit().putString(FNAME, response.getString(FNAME)).apply();
                savetoShared(LoginReg.this).edit().putString(LNAME, response.getString(LNAME)).apply();
                savetoShared(LoginReg.this).edit().putString(EMAIL, response.getString(EMAIL)).apply();
                savetoShared(LoginReg.this).edit().putString(PHONE, response.getString(PHONE)).apply();
                savetoShared(LoginReg.this).edit().putString(DOB, response.getString(DOB)).apply();
                savetoShared(LoginReg.this).edit().putString(USER_ID, response.getString(USER_ID)).apply();

                savetoShared(LoginReg.this).edit().putString(TIMEZONE, response.getString(TIMEZONE)).apply();
                savetoShared(LoginReg.this).edit().putString(LAT, response.getString(LAT)).apply();
                savetoShared(LoginReg.this).edit().putString(LON, response.getString(LON)).apply();
                savetoShared(LoginReg.this).edit().putString(ENERGYCOST, response.getString(ENERGYCOST)).apply();
                //Utility.setIsFresh(this, true);

//                startActivity(new Intent(LoginReg.this, HomeActivity.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void responseData(String data) {
        LoginReg.this.runOnUiThread(() -> {
            JSONObject response = null;
            try {
                response = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (response.has(FNAME)) {
                try {
                    savetoShared(LoginReg.this).edit().putString(FNAME, response.getString(FNAME)).apply();
                    savetoShared(LoginReg.this).edit().putString(LNAME, response.getString(LNAME)).apply();
                    savetoShared(LoginReg.this).edit().putString(EMAIL, response.getString(EMAIL)).apply();
                    savetoShared(LoginReg.this).edit().putString(PHONE, response.getString(PHONE)).apply();
                    savetoShared(LoginReg.this).edit().putString(DOB, response.getString(DOB)).apply();
                    savetoShared(LoginReg.this).edit().putString(USER_ID, response.getString(USER_ID)).apply();
                    //Utility.setIsFresh(this, true);

                    startActivity(new Intent(LoginReg.this, HomeActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (response.has("status")) {
                try {
                    if (response.getBoolean("status")) {
                        JSONObject _object = response.getJSONObject("data");
                        savetoShared(LoginReg.this).edit().putString(FNAME, _object.getString(FNAME)).apply();
                        savetoShared(LoginReg.this).edit().putString(LNAME, _object.getString(LNAME)).apply();
                        savetoShared(LoginReg.this).edit().putString(EMAIL, _object.getString(EMAIL)).apply();
                        savetoShared(LoginReg.this).edit().putString(PHONE, _object.getString(PHONE)).apply();
                        savetoShared(LoginReg.this).edit().putString(DOB, _object.getString(DOB)).apply();
                        savetoShared(LoginReg.this).edit().putString(USER_ID, _object.getString(USER_ID)).apply();
                        //Utility.setIsFresh(this, true);

                        startActivity(new Intent(LoginReg.this, HomeActivity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (response.has("success")) {
                try {
                    if (response.getString("success").equals("true")) {
                        //  getRequest("http://209.58.164.151:88/api/UserRegistration/SendOTPMobile?mobile="+Constants.mobNo);
                        loginLayout(findViewById(R.id.last_name));
                        // Toast.makeText(LoginReg.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    } else {
//                            password.setText("");
                        Toast.makeText(LoginReg.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            finish();
        });
    }

/*


 @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","hqplayer","valvole");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }};
 */

    public String startgps() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return new locationUtils(this).fn_getlocation();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET", "android.permission.WRITE_EXTERNAL_STORAGE"}, 10);
        }
        return "NA";
    }

}
