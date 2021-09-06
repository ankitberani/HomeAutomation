package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.scene_model;

import java.util.ArrayList;

public class SelectGroupsAdapter extends RecyclerView.Adapter<SelectGroupsAdapter.ViewHolder> {


    ArrayList<scene_model.Scene> _list;
    Context context;
    RoomOperation objInterface;

    public SelectGroupsAdapter(ArrayList<scene_model.Scene> _list, Context context, RoomOperation objInterface) {
        this._list = _list;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_selection_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        scene_model.Scene object = _list.get(position);
        holder.tv_groupName.setText(object.getName());
        Log.e("TAGGG", "Online Dno " + _list.get(position).getName() + " " + _list.get(position).isOnline());
        holder.cb1.setChecked(_list.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_groupName, tv_device_status;
        LinearLayout linearLayout;
        CheckBox cb1;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_groupName = itemView.findViewById(R.id.tv_group_name);
            tv_device_status = itemView.findViewById(R.id.tv_online_status);
            linearLayout = itemView.findViewById(R.id.dli_parent);
            cb1 = itemView.findViewById(R.id.cb_1);
            linearLayout.setOnClickListener(v -> {
                _list.get(getAdapterPosition()).setSelected(!_list.get(getAdapterPosition()).isSelected());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

}
