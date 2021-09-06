package com.wekex.apps.homeautomation.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wekex.apps.homeautomation.BaseActivity;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Retrofit.APIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupMenu extends BaseActivity {
    /* access modifiers changed from: private */
    public int SCENE_INTENT = 1;
    private String TAG = "SceneMenu";
    private LinearLayout deviceHolder;
    String room_Id = "";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_group_menu);
        this.deviceHolder = (LinearLayout) findViewById(R.id.deviceHolder);
        if (getIntent() != null && getIntent().hasExtra("room_Id")) {
            room_Id = getIntent().getStringExtra("room_Id");
        }
        getgroup();
    }

    void getgroup() {
        this.deviceHolder.removeAllViews();
        showProgressDialog("Refreshing List");
        RequestQueue queue = Volley.newRequestQueue(this);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        StringBuilder sb = new StringBuilder();
//        sb.append("http://209.58.164.151:88/api/Get/getGroup?UID=");
        sb.append(APIClient.BASE_URL + "/api/Get/getGroup?UID=");
        sb.append(user);
        String url = sb.toString();
        String str = this.TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delete: ");
        sb2.append(url);
        Log.d(str, sb2.toString());
        queue.add(new StringRequest(0, url, new Listener() {
            public final void onResponse(Object obj) {
                lambda$getgroup$0(GroupMenu.this, (String) obj);
            }
        }, new ErrorListener() {
            public final void onErrorResponse(VolleyError volleyError) {
                GroupMenu.lambda$getgroup$1(GroupMenu.this, volleyError);
            }
        }));
    }

    public   /* synthetic */ void lambda$getgroup$0(GroupMenu groupMenu, String response) {
        GroupMenu groupMenu2 = groupMenu;
        String str = response;
        groupMenu.hideProgressDialog();
        String str2 = groupMenu2.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("response ");
        sb.append(str);
        Log.d(str2, sb.toString());
        String InvalidJSON = str.replace("\\\"", "\"").trim();
        String str3 = groupMenu2.TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("getScene: ");
        sb2.append(InvalidJSON);
        Log.d(str3, sb2.toString());
        try {
            JSONObject mainJsop = Constants.stringToJsonObject(response);
            JSONArray remo = mainJsop.getJSONArray("Scene");
            boolean z = false;
            int i = 0;
            while (i < remo.length()) {
                JSONObject jsonObject = remo.getJSONObject(i);
                JSONArray devices = jsonObject.getJSONArray("Devices");
                final String id = jsonObject.getString("ID");
                final String name = jsonObject.getString("Name");
                View dli = LayoutInflater.from(groupMenu).inflate(R.layout.scene_list_item, null, z);
                LinearLayout dli_parent = (LinearLayout) dli.findViewById(R.id.dli_parent);
                TextView schedules_name = (TextView) dli.findViewById(R.id.schedules_name);
                TextView dli_room = (TextView) dli.findViewById(R.id.dli_room);
                ((ImageView) dli.findViewById(R.id.showMenu)).setTag(jsonObject.toString());
                StringBuilder sb3 = new StringBuilder();
                JSONObject mainJsop2 = mainJsop;
                sb3.append("No. of devices ");
                sb3.append(devices.length());
                dli_room.setText(sb3.toString());
                schedules_name.setText(Constants.jsonobjectSTringJSON(jsonObject, "Name"));
                dli_parent.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        GroupMenu.this.triggerGroup(id, name);
                    }
                });
                groupMenu2.deviceHolder.addView(dli);
                i++;
                mainJsop = mainJsop2;
                z = false;
            }
            String str4 = groupMenu2.TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("onCreate: String using Replace  ");
            sb4.append(remo);
            Log.d(str4, sb4.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static /* synthetic */ void lambda$getgroup$1(GroupMenu groupMenu, VolleyError error) {
        StringBuilder sb = new StringBuilder();
        sb.append(MqttServiceConstants.TRACE_ERROR);
        sb.append(error.getMessage());
        Log.d("Error.Response", sb.toString());
        Toast.makeText(groupMenu, "Unable to Delete", Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: private */
    public void triggerGroup(String id, String name) {
        Intent intent = new Intent(this, group_rgb_controls.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void device_menu_list(View view) {
        final String currentScene = view.getTag().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.scene_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.delete_d) {
                    GroupMenu.this.delete(currentScene);
                } else if (itemId == R.id.edit) {
                    Intent intent = new Intent(GroupMenu.this, GroupEditor.class);
                    intent.putExtra("room_Id", room_Id);
                    intent.putExtra("Devices", currentScene);
                    GroupMenu groupMenu = GroupMenu.this;
                    groupMenu.startActivityForResult(intent, groupMenu.SCENE_INTENT);
                }
                return true;
            }
        });
        popup.show();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getgroup();
    }

    /* access modifiers changed from: private */
    public void delete(String currentScene) {
        showProgressDialog("Deleting...");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringBuilder sb = new StringBuilder();
        sb.append(APIClient.BASE_URL + "/api/Get/delGroup?ID=");
        sb.append(Constants.jsonObjectreader(currentScene, "ID"));
        String url = sb.toString();
        String str = this.TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delete: ");
        sb2.append(url);
        Log.d(str, sb2.toString());
        queue.add(new StringRequest(0, url, new Listener() {
            public final void onResponse(Object obj) {
                GroupMenu.lambda$delete$2(GroupMenu.this, (String) obj);
            }
        }, new ErrorListener() {
            public final void onErrorResponse(VolleyError volleyError) {
                GroupMenu.lambda$delete$3(GroupMenu.this, volleyError);
            }
        }));
    }

    public static /* synthetic */ void lambda$delete$2(GroupMenu groupMenu, String response) {
        String str = groupMenu.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("delete: ");
        sb.append(response);
        Log.d(str, sb.toString());
        groupMenu.getgroup();
        groupMenu.hideProgressDialog();
    }

    public static /* synthetic */ void lambda$delete$3(GroupMenu groupMenu, VolleyError error) {
        String str = groupMenu.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("delete: ");
        sb.append(error.getCause());
        Log.d(str, sb.toString());
        groupMenu.hideProgressDialog();
    }

    public void addScenes(View view) {
        Intent intent = new Intent(this, GroupEditor.class);
        intent.putExtra("room_Id", room_Id);
        intent.putExtra("Devices", "new");
        startActivityForResult(intent, this.SCENE_INTENT);
    }

    public void close(View view) {
        finish();
    }
}
