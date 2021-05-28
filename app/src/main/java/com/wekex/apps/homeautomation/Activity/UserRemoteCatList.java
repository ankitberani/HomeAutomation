package com.wekex.apps.homeautomation.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;
import com.wekex.apps.homeautomation.Retrofit.APIService;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.adapter.UserRemoteCatFillAdapter;
import com.wekex.apps.homeautomation.bluetooth.BluetoothActivity;
import com.wekex.apps.homeautomation.model.Model_MainRemoteList;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.model.RemoteCounts;
import com.wekex.apps.homeautomation.model.UserRemoteListModel;
import com.wekex.apps.homeautomation.model.device_model;
import com.wekex.apps.homeautomation.model.ir_remotes;
import com.wekex.apps.homeautomation.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class UserRemoteCatList extends AppCompatActivity {

    Toolbar toolbar;
    String dno = "";
    String dname = "";

    TextView tv_add_remote;
    public int REMOTE_LIST = 101;
    Utility _utility;
    Gson _gson = new Gson();

    UserRemoteListModel _obj_user_remote;

    ImageView iv_bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_remote_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dno = getIntent().getStringExtra("dno");
        dname = getIntent().getStringExtra("dname");
        toolbar.setTitle("IR Hub");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.e("TAG", "UserRemoteCatList OnCreate Called");

        _utility = new Utility(UserRemoteCatList.this);
        tv_add_remote = findViewById(R.id.tv_add_remote);
        tv_add_remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(UserRemoteCatList.this, RemoteTypeListActivity.class);
                _intent.putExtra("dno", dno);
                startActivityForResult(_intent, REMOTE_LIST);
            }
        });

        iv_bluetooth = findViewById(R.id.iv_bluetooth);
        iv_bluetooth.setVisibility(View.GONE);
        iv_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(UserRemoteCatList.this, "Click Bluetooth", Toast.LENGTH_SHORT).show();

                Intent _intent = new Intent(UserRemoteCatList.this, BluetoothActivity.class);
                startActivity(_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.remote_menu_new, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getUserRemoteList();
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                break;
            case R.id.add_new_remote:
                Intent _intent = new Intent(UserRemoteCatList.this, RemoteTypeListActivity.class);
                _intent.putExtra("dno", dno);
                startActivityForResult(_intent, REMOTE_LIST);
                break;

            case R.id.add_type:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    String str_path = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REMOTE_LIST) {
                getUserRemoteList();
            } else if (requestCode == SELECT_PHOTO_REQUEST) {
                Uri imageUri = data.getData();
                str_path = getPath(imageUri);
                if (str_path == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRemoteCatList.this);
                    builder.setTitle("Can't Load");
                    builder.setMessage("Selected image is not on your local storage, please download and pick image from there.");
                    builder.setNegativeButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = getBitmap(str_path, BitmapFactory.decodeFile(str_path, bmOptions));
                iv_pick_img.setImageBitmap(bitmap);
                iv_pick_img.setTag(str_path);
            }
        }
    }


    void AddNewCatType(String iconpath, String cate_name) {
        device_model _cat = new device_model(cate_name, 0, true, iconpath);


        Utility _utility = new Utility(UserRemoteCatList.this);
        TypeToken<ArrayList<device_model>> token = new TypeToken<ArrayList<device_model>>() {
        };

        ArrayList<device_model> _lst = _gson.fromJson(_utility.getString("custome_remote"), token.getType());
        if (_lst == null) {
            ArrayList<device_model> _arr_custome = new ArrayList<>();
            _arr_custome.add(_cat);
            String _data = _gson.toJson(_arr_custome);
            _utility.putString("custome_remote", _data);
            Log.e("TAG", "Data At AdType " + _data);
        } else {
            _lst.add(_cat);
            String _data = _gson.toJson(_lst);
            Log.e("TAG", "Data At AdType " + _data);
            _utility.putString("custome_remote", _data);
        }
        str_path = "";
//        Toast.makeText(this, cate_name + " Added successfully!", Toast.LENGTH_SHORT).show();
    }


    public Bitmap getBitmap(String photoPath, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        Log.e("TAG", "getBitmap photoPath " + photoPath);
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (Exception e) {
            Log.e("TAG", "getBitmap Exception " + e.getMessage());
        }
        return rotatedBitmap;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /*This method will get real path of sected image from the system generated path*/
    private String getPath(Uri uri) {
        String[] projection;
        Cursor cursor;
        int column_index;
        projection = new String[]{MediaStore.Images.Media.DATA};
        cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        String path = cursor.getString(column_index);

        return path;
    }

    View.OnClickListener _listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String _type = (String) v.getTag();
            if (_type.equalsIgnoreCase("Add New")) {
                Intent _intent = new Intent(UserRemoteCatList.this, RemoteTypeListActivity.class);
                _intent.putExtra("dno", dno);
                startActivityForResult(_intent, REMOTE_LIST);
            } else {
                Intent _intent = new Intent(UserRemoteCatList.this, CategoryRemoteList.class);
                _intent.putExtra("_type", _type);
                _intent.putExtra("_dno", dno);
                ArrayList<ir_remotes> _lst = new ArrayList<>();
                for (int i = 0; i < _obj_user_remote.get_ir_remotes().size(); i++) {
                    if (_obj_user_remote.get_ir_remotes().get(i).getR_Type().equalsIgnoreCase(_type)) {
                        _lst.add(_obj_user_remote.get_ir_remotes().get(i));
                    }
                }

                if (_utility.getString("custom") != null && !_utility.getString("custom").isEmpty()) {
                    TypeToken<ArrayList<Model_UserRemoteList>> token = new TypeToken<ArrayList<Model_UserRemoteList>>() {
                    };
                    ArrayList<Model_UserRemoteList> _remote_key_data = _gson.fromJson(_utility.getString("custom"), token.getType());

                    for (int i = 0; i < _remote_key_data.size(); i++) {
                        if (_remote_key_data.get(i).getR_Type().equalsIgnoreCase(_type)) {

                            ir_remotes _remotes = new ir_remotes();
                            _remotes.setR_Type(_remote_key_data.get(i).getR_Type());
                            _remotes.setR_Brand(_remote_key_data.get(i).getR_Brand());
//                            _remotes.setR_MOdel(_remote_key_data.get(i).get);
                            _remotes.setName(_remote_key_data.get(i).getName());
                            _remotes.set_lst_key(_remote_key_data.get(i).get_lst_key());
                            _lst.add(_remotes);
                        }
                    }
                }

                String _data = _gson.toJson(_lst);
                _intent.putExtra("data", _data);
                Log.e("TAG", "Data On Click " + _lst.size());
                startActivity(_intent);
            }
        }
    };

    boolean isAdded(String _type, ArrayList<RemoteCounts> _lst_remote_count) {
        for (int i = 0; i < _lst_remote_count.size(); i++) {
            if (_type.equalsIgnoreCase(_lst_remote_count.get(i).getTypes())) {
                return true;
            }
        }
        return false;
    }

    public int SELECT_PHOTO_REQUEST = 100;

    ImageView iv_pick_img;

    public void showDialog() {
        final Dialog dialog = new Dialog(UserRemoteCatList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_new_device_type);
        iv_pick_img = dialog.findViewById(R.id.tv_selected_image);
        EditText edt_name = dialog.findViewById(R.id.edt_device_name);
        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
        TextView tv_save = dialog.findViewById(R.id.tv_save);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_name.getText().toString().trim().isEmpty()) {
                    edt_name.setError("Required!");
                } else if (str_path.isEmpty()) {
                    Toast.makeText(UserRemoteCatList.this, "Please pick device icon", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    AddNewCatType(str_path, edt_name.getText().toString().trim());
                }
            }
        });
        iv_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO_REQUEST);
            }
        });
        dialog.show();
    }


    void getUserRemoteList() {
        try {
            ArrayList<RemoteCounts> _lst_remote_count = new ArrayList<>();
            RecyclerView rv_user_remote = findViewById(R.id.rv_user_remote);
            rv_user_remote.setLayoutManager(new GridLayoutManager(this, 2));
            APIService _apiService = APIClient.getClientForStringResponse().create(APIService.class);
            String _url = APIClient.BASE_URL + "/api/Get/getUserRemote?dno=" + dno;
            Log.e("TAG", "getUserRemoteList Url " + _url);
            Call<String> _call = _apiService.getUserRemote(_url);
            _call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    try {
                        if (response.body() == null) {
                            JSONObject _object = new JSONObject(response.errorBody().string());
                            Log.e("TAGGG", "OnResponse of Login " + _object.toString());
                            if (_object.has("success") && _object.getBoolean("success") == false) {
                                Toast.makeText(UserRemoteCatList.this, _object.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.body().equalsIgnoreCase("{}")) {
                            Toast.makeText(UserRemoteCatList.this, "Remotes Not Founds!", Toast.LENGTH_SHORT).show();
                            RemoteCounts _count = new RemoteCounts();
                            _count.setTypes("Add New");
                            _count.setIcon(R.drawable.addmore);
                            _lst_remote_count.add(_count);
                            UserRemoteCatFillAdapter _adapter = new UserRemoteCatFillAdapter(_lst_remote_count, UserRemoteCatList.this, _listener);
                            rv_user_remote.setAdapter(_adapter);
                        } else {
                            _obj_user_remote = _gson.fromJson(response.body(), UserRemoteListModel.class);
                            for (int i = 0; i < _obj_user_remote.get_ir_remotes().size(); i++) {
                                if (!isAdded(_obj_user_remote.get_ir_remotes().get(i).getR_Type(), _lst_remote_count)) {
                                    RemoteCounts _count = new RemoteCounts();
                                    if (_obj_user_remote.get_ir_remotes().get(i).getR_Type().equalsIgnoreCase("tv")) {
                                        _count.setIcon(R.drawable.tv_svg);
                                    } else if (_obj_user_remote.get_ir_remotes().get(i).getR_Type().equalsIgnoreCase("ac")) {
                                        _count.setIcon(R.drawable.ic_ac);
                                    }

                                    _count.setTypes(_obj_user_remote.get_ir_remotes().get(i).getR_Type());
                                    _count.set_remote_name(_obj_user_remote.get_ir_remotes().get(i).getName());
                                    Log.e("TAG", "Remote Name At Cat List " + _obj_user_remote.get_ir_remotes().get(i).getName());
                                    int count = 0;
                                    for (int j = 0; j < _obj_user_remote.get_ir_remotes().size(); j++) {
                                        if (_count.getTypes().equalsIgnoreCase(_obj_user_remote.get_ir_remotes().get(j).R_Type)) {
                                            count++;
                                        }
                                    }
                                    _count.setType_counts(count);
                                    _lst_remote_count.add(_count);
                                }
                            }


                            if (_utility.getString("custom") != null && !_utility.getString("custom").isEmpty()) {
                                TypeToken<ArrayList<Model_UserRemoteList>> token = new TypeToken<ArrayList<Model_UserRemoteList>>() {
                                };
                                ArrayList<Model_UserRemoteList> _remote_key_data = _gson.fromJson(_utility.getString("custom"), token.getType());

                                for (int i = 0; i < _remote_key_data.size(); i++) {
                                    if (!isAdded(_remote_key_data.get(i).getR_Type(), _lst_remote_count)) {
                                        RemoteCounts _count = new RemoteCounts();
                                        if (_remote_key_data.get(i).getR_Type().equalsIgnoreCase("tv")) {
                                            _count.setIcon(R.drawable.tv_svg);
                                        } else if (_remote_key_data.get(i).getR_Type().equalsIgnoreCase("ac")) {
                                            _count.setIcon(R.drawable.ic_ac);
                                        }

                                        _count.setTypes(_remote_key_data.get(i).getR_Type());
                                        _count.set_remote_name(_remote_key_data.get(i).getName());
                                        int count = 0;
                                        for (int j = 0; j < _remote_key_data.size(); j++) {
                                            if (_count.getTypes().equalsIgnoreCase(_remote_key_data.get(j).R_Type)) {
                                                count++;
                                            }
                                        }
                                        _count.setType_counts(count);
                                        _lst_remote_count.add(_count);
                                    } else {
                                        for (int j = 0; j < _lst_remote_count.size(); j++) {
                                            if (_lst_remote_count.get(j).getTypes().equalsIgnoreCase(_remote_key_data.get(i).getR_Type())) {
                                                _lst_remote_count.get(j).setType_counts(_lst_remote_count.get(j).getType_counts() + 1);
                                            }
                                        }
                                    }
                                }
                            }

                            Collections.reverse(_lst_remote_count);
                            RemoteCounts _count = new RemoteCounts();
                            _count.setTypes("Add New");
                            _count.setIcon(R.drawable.addmore);
                            _lst_remote_count.add(_count);
                            UserRemoteCatFillAdapter _adapter = new UserRemoteCatFillAdapter(_lst_remote_count, UserRemoteCatList.this, _listener);
                            rv_user_remote.setAdapter(_adapter);
                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Exception at login " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("TAGGG", "OnResponse onFailure " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "Exception at login " + e.getCause() + " " + e.getMessage());
        }
    }
}
