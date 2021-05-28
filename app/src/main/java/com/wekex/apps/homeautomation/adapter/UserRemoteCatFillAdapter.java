package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.RemoteCounts;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserRemoteCatFillAdapter extends RecyclerView.Adapter<UserRemoteCatFillAdapter.ViewHolder> {


    ArrayList<RemoteCounts> _lst_counts;
    Context context;
    View.OnClickListener _listener;

    public UserRemoteCatFillAdapter(ArrayList<RemoteCounts> _lst_counts, Context context, View.OnClickListener _listener) {
        this._lst_counts = _lst_counts;
        this.context = context;
        this._listener = _listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_remote_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RemoteCounts object = _lst_counts.get(position);

        if (object.getTypes().equalsIgnoreCase("Add new"))
            holder.tv_device_name.setText(object.getTypes());
        else
            holder.tv_device_name.setText(object.getTypes() + " (" + object.getType_counts() + ")");
        holder.iv_device_icon.setImageResource(object.getIcon());
//        holder.tv_brand.setText(object.getType_counts() + " Remotes");

        /*holder.view.setBackgroundColor(object.getColor_code());

        if (object.getSelected())
            holder.iv_selected.setVisibility(View.VISIBLE);
        else
            holder.iv_selected.setVisibility(View.GONE);*/
    }

    @Override
    public int getItemCount() {
        return _lst_counts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parent;

        TextView tv_device_name;
        ImageView iv_device_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            tv_device_name = itemView.findViewById(R.id.tv_device_name);
            iv_device_icon = itemView.findViewById(R.id.iv_device_icon);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(_lst_counts.get(getAdapterPosition()).getTypes());
                    _listener.onClick(v);
                }
            });
        }
    }
}
