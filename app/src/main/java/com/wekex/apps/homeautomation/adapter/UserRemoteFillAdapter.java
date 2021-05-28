package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wekex.apps.homeautomation.Interfaces.RemoteListInterface;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.ir_remotes;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserRemoteFillAdapter extends RecyclerView.Adapter<UserRemoteFillAdapter.ViewHolder> {


    ArrayList<ir_remotes> _list;

    Context context;

    RemoteListInterface _objRemoteList;

    public UserRemoteFillAdapter(ArrayList<ir_remotes> object_main, Context context, RemoteListInterface _objRemoteList) {
        this._list = object_main;
        this.context = context;
        this._objRemoteList = _objRemoteList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_remote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.e("TAG", "OnBindHolder " + _list.get(position).getR_Brand());
        holder.tv_cat.setText(_list.get(position).getR_Brand());
        holder.tv_brand.setText(_list.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_main;
        TextView tv_cat, tv_brand;
        ImageView iv_three_dot;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_main = itemView.findViewById(R.id.rl_main);
            tv_cat = itemView.findViewById(R.id.tv_cate_name);
            tv_brand = itemView.findViewById(R.id.tv_brandName);
            iv_three_dot = itemView.findViewById(R.id.iv_three_dot);
            rl_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    v.setTag(_list.get(getAdapterPosition()).getName());
                    _objRemoteList._click_remote(_list.get(getAdapterPosition()).getName(), getAdapterPosition());
                }
            });

            iv_three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, iv_three_dot);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.scene_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                            if (item.getItemId() == R.id.delete_d) {

                            } else {
                                _objRemoteList._remote_operation(1, getAdapterPosition());
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                }
            });
        }
    }
}
