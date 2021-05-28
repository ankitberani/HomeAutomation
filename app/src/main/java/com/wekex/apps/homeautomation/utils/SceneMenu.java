package com.wekex.apps.homeautomation.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Switch;
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

public class SceneMenu extends BaseActivity {
    /* access modifiers changed from: private */
    public int SCENE_INTENT = 1;
    private String TAG = "SceneMenu";
    private LinearLayout deviceHolder;
    boolean resume = false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_scene_menu);
        this.deviceHolder = (LinearLayout) findViewById(R.id.deviceHolder);
        getScene();
    }

    private void getScene() {
        this.deviceHolder.removeAllViews();
        RequestQueue queue = Volley.newRequestQueue(this);
        String user = Constants.savetoShared(this).getString(Constants.USER_ID, "0");
        StringBuilder sb = new StringBuilder();
        sb.append(APIClient.BASE_URL + "/api/Get/getScene?UID=");
        sb.append(user);
        String url = sb.toString();
        String str = this.TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("delete: ");
        sb2.append(url);
        Log.d(str, sb2.toString());
        queue.add(new StringRequest(0, url, (Listener) obj -> lambda$getScene$0(SceneMenu.this, (String) obj), volleyError -> SceneMenu.lambda$getScene$1(SceneMenu.this, volleyError)));
        this.resume = true;
    }

    public  /* synthetic */ void lambda$getScene$0(SceneMenu sceneMenu, String response) {
        SceneMenu sceneMenu2 = sceneMenu;
        String str = response;
        String str2 = sceneMenu2.TAG;
        String sb = "response " +
                str;
        Log.d(str2, sb);
        JSONArray remo = null;
        String InvalidJSON = str.replace("\\\"", "\"").trim();
        String str3 = sceneMenu2.TAG;
        String sb2 = "getScene: " +
                InvalidJSON;
        Log.d(str3, sb2);
        try {
            JSONObject mainJsop = Constants.stringToJsonObject(response);
            remo = mainJsop.getJSONArray("Scene");
            boolean z = false;
            int i = 0;
            while (i < remo.length()) {
                JSONObject jsonObject = remo.getJSONObject(i);
                JSONArray devices = jsonObject.getJSONArray("Devices");
                final String id = jsonObject.getString("ID");
                View dli = LayoutInflater.from(sceneMenu).inflate(R.layout.scene_list_item, null, z);
                LinearLayout dli_parent = dli.findViewById(R.id.dli_parent);
                Switch switch1 = dli.findViewById(R.id.switch1);
                TextView schedules_name = dli.findViewById(R.id.schedules_name);
                TextView dli_room = dli.findViewById(R.id.dli_room);
                dli.findViewById(R.id.showMenu).setTag(jsonObject.toString());
                JSONObject mainJsop2 = mainJsop;
                String sb3 = "No. of devices " +
                        devices.length();
                dli_room.setText(sb3);
                schedules_name.setText(Constants.jsonobjectSTringJSON(jsonObject, "Name"));
                switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                });
                dli_parent.setOnClickListener(v -> triggerscene(id));
                sceneMenu2.deviceHolder.addView(dli);
                i++;
                mainJsop = mainJsop2;
                z = false;
            }
            String str4 = sceneMenu2.TAG;
            String sb4 = "onCreate: String using Replace  " +
                    remo;
            Log.d(str4, sb4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str5 = sceneMenu2.TAG;
        assert remo != null;
        String sb5 = "getScene: " +
                remo.toString();
        Log.d(str5, sb5);
    }

    public static /* synthetic */ void lambda$getScene$1(SceneMenu sceneMenu, VolleyError error) {
        String sb = MqttServiceConstants.TRACE_ERROR +
                error.getMessage();
        Log.d("Error.Response", sb);
        Toast.makeText(sceneMenu, "Unable to Delete", Toast.LENGTH_SHORT).show();
    }

    /* access modifiers changed from: private */
    public void triggerscene(String id) {
        showProgressDialog("Triggering...");
        //        sb.append("http://209.58.164.151:88/api/Get/triggerScene?ID=");
        String url = APIClient.BASE_URL + "/api/Get/triggerScene?ID=" +
                id;
        String str = this.TAG;
        String sb2 = "triggerscene: " +
                url;
        Log.d(str, sb2);
        Volley.newRequestQueue(this).add(new StringRequest(0, url, (Listener) obj -> lambda$triggerscene$2(SceneMenu.this, (String) obj), volleyError -> lambda$triggerscene$3(SceneMenu.this, volleyError)));
    }

    public /* synthetic */ void lambda$triggerscene$2(SceneMenu sceneMenu, String response) {
        String str = sceneMenu.TAG;
        String sb = "triggerscene: " +
                response;
        Log.d(str, sb);
        sceneMenu.hideProgressDialog();
    }

    public  /* synthetic */ void lambda$triggerscene$3(SceneMenu sceneMenu, VolleyError error) {
        String str = sceneMenu.TAG;
        String sb = "triggerscene: " +
                error.getCause();
        Log.d(str, sb);
        sceneMenu.hideProgressDialog();
    }

    public void device_menu_list(View view) {
        final String currentScene = view.getTag().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.scene_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delete_d) {
                SceneMenu.this.delete(currentScene);
            } else if (itemId == R.id.edit) {
                Intent intent = new Intent(SceneMenu.this, ScenesEditor.class);
                intent.putExtra("Devices", currentScene);
                SceneMenu sceneMenu = SceneMenu.this;
                sceneMenu.startActivityForResult(intent, sceneMenu.SCENE_INTENT);
            }
            return true;
        });
        popup.show();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getScene();
    }

    /* access modifiers changed from: private */
    public void delete(String currentScene) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = APIClient.BASE_URL + "/api/Get/delScene?ID=" +
                Constants.jsonObjectreader(currentScene, "ID");
        String str = this.TAG;
        String sb2 = "delete: " +
                url;
        Log.d(str, sb2);
        queue.add(new StringRequest(0, url, (Listener) obj -> SceneMenu.lambda$delete$4(SceneMenu.this, (String) obj), volleyError -> SceneMenu.lambda$delete$5(SceneMenu.this, volleyError)));
    }

    public static /* synthetic */ void lambda$delete$4(SceneMenu sceneMenu, String response) {
        String str = sceneMenu.TAG;
        String sb = "delete: " +
                response;
        Log.d(str, sb);
        sceneMenu.getScene();
    }

    public static /* synthetic */ void lambda$delete$5(SceneMenu sceneMenu, VolleyError error) {
        String str = sceneMenu.TAG;
        String sb = "delete: " +
                error.getCause();
        Log.d(str, sb);
    }

    public void addScenes(View view) {
        Intent intent = new Intent(this, ScenesEditor.class);
        intent.putExtra("Devices", "new");
        startActivityForResult(intent, this.SCENE_INTENT);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public void close(View view) {
        finish();
    }
}
