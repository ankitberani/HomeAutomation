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
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.RoundRectCornerImageView;

import java.util.ArrayList;

public class SelectSceneAdapter extends RecyclerView.Adapter<SelectSceneAdapter.ViewHolder> {

    ArrayList<scene_model.Scene> _list;
    Context context;
    rgb_color_interface objInterface;


    public SelectSceneAdapter(ArrayList<scene_model.Scene> _list, Context context, rgb_color_interface objInterface) {
        this._list = _list;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_list_in_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        scene_model.Scene object = _list.get(position);

        holder.tv_sceneName.setText(object.getScene_name() != null ? object.getScene_name() : object.getName());
        holder.iv_scene_image.setImageResource(object.getDrawable());
        if (object.isSelected())
            holder.rb_1.setChecked(true);
        else
            holder.rb_1.setChecked(false);

        if (object.isStaticScene()) {
            holder.circular.setVisibility(View.VISIBLE);
        } else {
            holder.circular.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_scene_image, iv_edit, iv_delete;
        public TextView tv_sceneName;
        RadioButton rb_1;
        LinearLayout linearLayout;
        RoundRectCornerImageView circular;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_scene_image = itemView.findViewById(R.id.iv_sceneicon);
            tv_sceneName = itemView.findViewById(R.id.tv_scnene_name);
            rb_1 = itemView.findViewById(R.id.rb_1);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            circular = itemView.findViewById(R.id.iv_circullar);
            linearLayout = itemView.findViewById(R.id.ll_scene_main);

            linearLayout.setOnClickListener(view -> {
                if (_list.get(getAdapterPosition()).isStaticScene()) {
                    objInterface.selectedScene(getAdapterPosition());
                    for (int i = 0; i < _list.size(); i++) {
                        _list.get(i).setSelected(false);
                    }
                } else {
                    objInterface.triggerScene(_list.get(getAdapterPosition()).getID());
                    for (int i = 0; i < _list.size(); i++) {
                        _list.get(i).setSelected(false);
                    }

                }
                _list.get(getAdapterPosition()).setSelected(true);
                notifyDataSetChanged();
            });

            iv_edit.setVisibility(View.GONE);
            iv_delete.setVisibility(View.GONE);
        }
    }

}
