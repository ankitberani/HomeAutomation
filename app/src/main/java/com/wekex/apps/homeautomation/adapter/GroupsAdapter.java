package com.wekex.apps.homeautomation.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wekex.apps.homeautomation.Interfaces.RoomOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.RoomActivity;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.GroupEditor;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.wekex.apps.homeautomation.utils.Constants.room_Id;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {


    ArrayList<scene_model.Scene> _list;
    Context context;
    RoomOperation objInterface;

    public GroupsAdapter(ArrayList<scene_model.Scene> _list, Context context, RoomOperation objInterface) {
        this._list = _list;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        scene_model.Scene object = _list.get(position);
        holder.tv_groupName.setText(object.getName());
        if (object.getDevices() != null)
            holder.tv_group_detail.setText("No. of devices " + object.getDevices().size());

        holder.switch1.setEnabled(_list.get(position).isOnline());
        Log.e("TAGGG", "Online Dno " + _list.get(position).getName() + " " + _list.get(position).isOnline());

        if (object.isOnline()) {
            holder.tv_device_status.setText("Online");
            holder.tv_device_status.setTextColor(context.getResources().getColor(R.color.green_));
        } else {
            holder.tv_device_status.setText("Offline");
            holder.tv_device_status.setTextColor(context.getResources().getColor(R.color.red_));
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_info, iv_setting;
        public TextView tv_groupName, tv_group_detail, tv_device_status;
        LinearLayout linearLayout;
        Switch switch1;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_groupName = itemView.findViewById(R.id.schedules_name);
            tv_device_status = itemView.findViewById(R.id.tv_status);
            tv_group_detail = itemView.findViewById(R.id.dli_room);
            linearLayout = itemView.findViewById(R.id.dli_parent);
            iv_info = itemView.findViewById(R.id.deviceInfo);
            iv_setting = itemView.findViewById(R.id.showMenu);

            switch1 = itemView.findViewById(R.id.switch1);

            linearLayout.setOnClickListener(v -> {
                if (_list.get(getAdapterPosition()).isOnline())
                    objInterface.SelectGroup(getAdapterPosition());
                else
                    Toast.makeText(context, "All Devices Offline!", Toast.LENGTH_SHORT).show();
            });

            iv_info.setOnClickListener(v -> showInfoDialog(_list.get(getAdapterPosition()).getDevices(), (_list.get(getAdapterPosition()).getGroupType() == 6 || _list.get(getAdapterPosition()).getGroupType() == 5)));

            iv_setting.setOnClickListener(v -> {
//                    if (_list.get(getAdapterPosition()).isOnline())
                device_menu_list(v, getAdapterPosition());
            });

            switch1.setOnCheckedChangeListener((buttonView, isChecked) -> objInterface.triggerGroup(getAdapterPosition(), isChecked));
        }
    }

    public void device_menu_list(View view, int position) {
//        String dno1 = Constants.jsonObjectreader(view.getTag().toString(), DtypeViews.dno);
//        DeviceInfos = view.getTag().toString();
        PopupMenu popup = new PopupMenu(context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.device_setting_menu, popup.getMenu());

//        String _id = (String) view.getTag(R.string.groups);
        //registering popup with OnMenuItemClickListener

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_d:
//                        delete(view);
//                        String dno1 = Constants.jsonObjectreader(DeviceInfos, DtypeViews.dno);
//                        remove(view, _id);
                    objInterface.DeleteGroup(position);
                    break;
                case R.id.rename_d:
                    objInterface.RenameGroup(position);
//                        rename(view);
                    break;
                case R.id.move_d:
//                        changeroom(view);
                    break;
                case R.id.scheduled_d:
//                        schedules(view);
                    break;

                case R.id.edit_grp:
                    objInterface.editGroup(position);
                    break;
            }
            return true;
        });
        //ImageView imageView = deviceHolder.findViewWithTag(dno1+"online_status");
        //String imageName = imageView.getContentDescription().toString();
        //Log.d(TAG, "device_menu_list: des"+imageName);
//        JSONObject deviceInfo = getDeviceById(dno1, context);
        boolean isOffline = _list.get(position).isOnline();
        if (!isOffline) {
            popup.getMenu().removeItem(R.id.move_d);
            popup.getMenu().removeItem(R.id.rename_d);
            popup.getMenu().removeItem(R.id.scheduled_d);
        }

        if (_list.get(position).getGroupType() != 0) {
            popup.getMenu().removeItem(R.id.edit_grp);
            popup.getMenu().removeItem(R.id.delete_d);
        }
        popup.show();
    }

    public void showInfoDialog(ArrayList<String> _all_devices, boolean isAnotherType) {
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.group_detail_dialog, null, false);
        dialog.setContentView(view);
        LinearLayout ll_container = view.findViewById(R.id.ll_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        for (int i = 0; i < _all_devices.size(); i++) {
            TextView tv = new TextView(context);
            tv.setLayoutParams(params);
            tv.setText(_all_devices.get(i));
//            if (isAnotherType)
//            else
//                tv.setText(getBulbName(_all_devices.get(i)));
            tv.setTextColor(context.getResources().getColor(R.color.colorAccent));
            tv.setTextSize(16);
            ll_container.addView(tv);
        }

        TextView tv_ok = view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public String getBulbName(String dno) {
        String _name = "";
        AllDataResponseModel all_data_model = RoomActivity._grp_interface.getAllData();
        if (all_data_model != null)
            for (int i = 0; i < all_data_model.getObjData().size(); i++) {
                if (all_data_model.getObjData().get(i).getDno().equalsIgnoreCase(dno)) {
                    return all_data_model.getObjData().get(i).getObjd1().getName();
                }
            }
        return _name;
    }
}
