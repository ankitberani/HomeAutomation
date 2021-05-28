package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.HomeActivity;
import com.wekex.apps.homeautomation.Interfaces.HomeScreenOperation;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.GetAppHomeModel;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.DtypeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.wekex.apps.homeautomation.utils.DtypeViews.isOnline;
import static com.wekex.apps.homeautomation.utils.DtypeViews.room;
import static com.wekex.apps.homeautomation.utils.HomePage.getDrawable;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {


    GetAppHomeModel _model;
    Context context;
    String data;
    HomeScreenOperation _interface;
    int _pos = 0;

    public RoomAdapter(GetAppHomeModel _model, Context context, HomeScreenOperation _interface, String data) {
        this._model = _model;
        this.context = context;
        this._interface = _interface;
//        data = Constants.jsonObjectreader(Constants.savetoShared(context).getString(Constants.ROOMS, Constants.EMPTY), "data");
        this.data = Constants.jsonObjectreader(data, "data");
        Log.e("TAGGG", "Data " + data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rooms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("TAG", "OnBindHolder Room Name " + _model.get_lst_rooms().get(position).getName());
        _pos = position;
        if (_model != null) {
            if (!_model.get_lst_rooms().get(position).getID().equalsIgnoreCase("-1")) {
                if (!_model.get_lst_rooms().get(position).getName().equals("null")) {
                    holder.roomName.setText(_model.get_lst_rooms().get(position).getName());
//                    holder.no_of_device.setText(homePageIconAdder(context, data, holder.iconholder, _model.get_lst_rooms().get(position).getID()));
                    holder.card_add_room.setVisibility(View.GONE);
                    holder.cardRoom.setVisibility(View.VISIBLE);
                }
            } else {
                holder.card_add_room.setVisibility(View.VISIBLE);
                holder.cardRoom.setVisibility(View.GONE);
            }

            if (_model.get_lst_rooms().get(position).get_drawable_name() != null && !_model.get_lst_rooms().get(position).get_drawable_name().isEmpty()) {
//                holder.cardRoom.setBackgroundDrawable(_model.get_lst_rooms().get(position).getRoomBackground());
                holder.cardRoom.setBackgroundDrawable(Constants.getDrawableByName(_model.get_lst_rooms().get(position).get_drawable_name(), context));
            } else {
                holder.cardRoom.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gradtwo));
            }
        }
    }


    @Override
    public int getItemCount() {
        return (_model != null ? _model.get_lst_rooms().size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView roomName, no_of_device;
        //        LinearLayout iconholder;
        RelativeLayout cardRoom;

        CardView card_add_room;

        public ViewHolder(View itemView) {
            super(itemView);

//            iconholder = itemView.findViewById(R.id.iconholder);
            roomName = itemView.findViewById(R.id.roomName);
            no_of_device = itemView.findViewById(R.id.no_of_device);
            cardRoom = itemView.findViewById(R.id.cardRoom);
            card_add_room = itemView.findViewById(R.id.card_add_room);
            cardRoom.setOnClickListener(v -> _interface.selectRoom(_model.get_lst_rooms().get(getAdapterPosition()).getID(), _model.get_lst_rooms().get(getAdapterPosition()).getName()));
            cardRoom.setOnLongClickListener(v -> {
                _interface.updateRoom(_model.get_lst_rooms().get(getAdapterPosition()).getID(), _model.get_lst_rooms().get(getAdapterPosition()).getName(), getAdapterPosition());
                return true;
            });
            card_add_room.setOnClickListener(view -> _interface.selectRoom(_model.get_lst_rooms().get(getAdapterPosition()).getID(), _model.get_lst_rooms().get(getAdapterPosition()).getName()));
        }
    }

    public String homePageIconAdder(Context context, String data, LinearLayout deviceHolder, String room_id) {
        deviceHolder.removeAllViews();
        String TAG = "ContantsClass";
        int roomCount = 0;
        int onlibneCount = 0;
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
//                String isOnline1 = Constants.jsonObjectreader(json.toString(), isOnline);
//                if (Boolean.parseBoolean(isOnline1)) {
//                    onlibneCount++;
//                }
                if (Constants.FIRSTROOM) {
                    if (json.has("room")) {
                        Log.d(TAG, Constants.jsonObjectreader(json.toString(), "room") + " jsonDeviceAdder1: " + json.toString());
                        if (json.getString("room").equals(room_id)) {
                            Log.d(TAG, "jsonDeviceAdder: " + json.toString());
                            deviceHolder.addView(getIconsByType(context, json.getString(DtypeViews.dtype)));
                            roomCount++;
                        }
                        if (!Constants.jsonObjectreader(Constants.savetoShared(context).getString(Constants.ROOMS, Constants.EMPTY), "rooms").contains(json.getString("room"))) {
                            deviceHolder.addView(getIconsByType(context, json.getString(DtypeViews.dtype)));
                            roomCount++;
                        }
                    } else {
                        deviceHolder.addView(getIconsByType(context, json.getString(DtypeViews.dtype)));
                        roomCount++;
                    }
                } else {
                    if (json.has("room")) {
                        if (json.getString("room").equals(room_id)) {
                            roomCount++;
                            String isOnline1 = Constants.jsonObjectreader(json.toString(), isOnline);
                            deviceHolder.addView(getIconsByType(context, json.getString(DtypeViews.dtype)));
                            if (Boolean.parseBoolean(isOnline1))
                                onlibneCount++;
                        }
                    }
                }
            }
            return roomCount + "(" + onlibneCount + ")";
        } catch (JSONException e) {
            e.printStackTrace();
            return roomCount + "(" + onlibneCount + ")";
        }
    }

    private View getIconsByType(Context context, String dtype) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_page_room_card_icon, null, false);
        ImageView imageView = view.findViewById(R.id.iconView);
        imageView.setImageDrawable(getDrawable(context, dtype));
        return view;
    }
}