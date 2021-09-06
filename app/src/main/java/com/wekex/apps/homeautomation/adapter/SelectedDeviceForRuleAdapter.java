package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelectedDeviceForRuleAdapter extends RecyclerView.Adapter<SelectedDeviceForRuleAdapter.ViewHolder> {

    JSONArray _list;
    Context context;

    public SelectedDeviceForRuleAdapter(JSONArray _list, Context context) {
        this._list = _list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_device_for_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        scene_model.Scene object = _list.get(position);
        try {
            String _object = _list.getString(position);
            JSONObject jsonObject = new JSONObject(_object);
            JSONObject d1 = jsonObject.getJSONObject("d1");
            Log.e("TAG", "DNO at bind holder " + d1.getString("name"));
            holder.tv_sceneName.setText(d1.getString("name"));
            holder.tv_device_dno.setText(jsonObject.getString("dno"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Exception at BindViewHolder " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return _list.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_scene_image, iv_delete;
        public TextView tv_sceneName, tv_device_dno;


        public ViewHolder(View itemView) {
            super(itemView);

            iv_scene_image = itemView.findViewById(R.id.iv_device_icon);
            tv_sceneName = itemView.findViewById(R.id.tv_device_name);
            tv_device_dno = itemView.findViewById(R.id.tv_device_dno);

            iv_delete = itemView.findViewById(R.id.iv_delete_device);


            iv_delete.setOnClickListener(view -> {

            });
        }
    }
}
