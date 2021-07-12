package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.Interfaces.SelectSceneListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.RoundRectCornerImageView;

import java.util.ArrayList;

public class SceneAdapterForShortcuts extends RecyclerView.Adapter<SceneAdapterForShortcuts.ViewHolder> {

    ArrayList<scene_model.Scene> _list;
    Context context;
    SelectSceneListener _interface;

    public SceneAdapterForShortcuts(ArrayList<scene_model.Scene> _list, Context context, SelectSceneListener _interface) {
        this._list = _list;
        this.context = context;
        this._interface = _interface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_list_for_shortcuts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        scene_model.Scene object = _list.get(position);
        holder.tv_sceneName.setText(object.getScene_name() != null ? object.getScene_name() : object.getName());
        if (object.isSelected())
            holder.cb_1.setChecked(true);
        else
            holder.cb_1.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_sceneName;
        AppCompatCheckBox cb_1;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_sceneName = itemView.findViewById(R.id.tv_scnene_name);
            cb_1 = itemView.findViewById(R.id.cb_1);
            linearLayout = itemView.findViewById(R.id.ll_scene_main);
            linearLayout.setOnClickListener(view -> {
                _interface.selectScene(getAdapterPosition());
            });
        }
    }
}
