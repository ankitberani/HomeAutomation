package com.wekex.apps.homeautomation.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.BaseActivity;
import com.wekex.apps.homeautomation.MainActivity;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.RoomActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.wekex.apps.homeautomation.utils.DtypeViews.isOnline;


public class HomePage {
    private static final String TAG = "HomePageClass";
    private static String roomsString;

    public static void addRooms(JSONArray rooms, LinearLayout roomholder, Context context) {
        roomsString = rooms.toString();
        String JSONroomname = "";
        String JSONroomId = "";
        for (int i = 0; i < rooms.length(); i++) {
            String backgroundColor = "not";
            View view = LayoutInflater.from(context).inflate(R.layout.card_rooms, null, false);
            RelativeLayout constraintLayout = view.findViewById(R.id.cardRoom);
            TextView roomName = view.findViewById(R.id.roomName);
            if (i == 0) {
                Constants.FIRSTROOM = true;
                roomName.setTag("true");

            } else {
                Constants.FIRSTROOM = false;
                roomName.setTag("false");
            }

            try {
                // Log.d(TAG, "addRooms: "+rooms.getJSONObject(i).get("name"));
                JSONroomname = rooms.getJSONObject(i).get("name").toString();
                JSONroomId = rooms.getJSONObject(i).get("ID").toString();
                roomName.setText(JSONroomname);
                Constants.savetoShared(context).getString(JSONroomname, "not");
                if (!Constants.savetoShared(context).getString(JSONroomname, "not").equals("not")) {
                    backgroundColor = Constants.savetoShared(context).getString(JSONroomname, "not");
                    constraintLayout.setBackground(Constants.getDrawableByName(Constants.savetoShared(context).getString(JSONroomname, "not"), context));
                }
                Constants.savetoShared(context).edit().putString(JSONroomId, rooms.getJSONObject(i).get("name").toString()).apply();
                constraintLayout.setTag(rooms.getJSONObject(i).get("ID").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Device Icon and Device count
//            LinearLayout iconholder = view.findViewById(R.id.iconholder);
            String data = Constants.jsonObjectreader(Constants.savetoShared(context).getString(Constants.ROOMS, Constants.EMPTY), "data");
//            String total = homePageIconAdder(context, data, iconholder, JSONroomId);
//            TextView no_of_device = view.findViewById(R.id.no_of_device);
//            no_of_device.setText(String.valueOf(total));

            //roomName.setText(jsonObjectreader());
            final String backgroundColorFOrintent = backgroundColor;
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "onClick: " + Boolean.parseBoolean(roomName.getTag().toString()));
                    Constants.FIRSTROOM = Boolean.parseBoolean(roomName.getTag().toString());

                    Intent intent = new Intent(context, RoomActivity.class);
                    intent.putExtra("room_id", constraintLayout.getTag().toString());
                    intent.putExtra("backcolor", backgroundColorFOrintent);
                    Log.d(TAG, "addRooms: " + constraintLayout.getTag().toString());
                    //  Toast.makeText(context, constraintLayout.getTag().toString(), Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                }
            });
            String finalJSONroomname = JSONroomname;
            String finalJSONroomId = JSONroomId;
            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Attention !")
                            .setMessage("Do you want to Edit or Remove " + finalJSONroomname + " Room ?")
                            .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((BaseActivity) context).showProgressDialog("Removing Room");
                                    DtypeViews.removeRoom(context, constraintLayout.getTag().toString());
                                }
                            })
                            .setNegativeButton(R.string.editRoom, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "addRooms: " + constraintLayout.getTag().toString());
                                    // do nothing
                                    editRoom(context, finalJSONroomname, finalJSONroomId);
                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return false;
                }
            });

            roomholder.addView(view);
        }

    }

    public static String homePageIconAdder(Context context, String data, LinearLayout deviceHolder, String room_id) {
        deviceHolder.removeAllViews();
        String TAG = "ContantsClass";
        int roomCount = 0;
        int onlibneCount = 0;
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = new JSONObject(jsonArray.getString(i));
                String isOnline1 = Constants.jsonObjectreader(json.toString(), isOnline);
                if (Boolean.parseBoolean(isOnline1)) {
                    onlibneCount++;
                }

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
                            deviceHolder.addView(getIconsByType(context, json.getString(DtypeViews.dtype)));
                            roomCount++;
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

    private static View getIconsByType(Context context, String dtype) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_page_room_card_icon, null, false);
        ImageView imageView = view.findViewById(R.id.iconView);
        imageView.setImageDrawable(getDrawable(context, dtype));
        return view;
    }

    public static Drawable getDrawable(Context context, String dtype) {
        Drawable drawable = null;
        switch (dtype) {
            case "1":
                drawable = context.getResources().getDrawable(R.drawable.socket_g_svg);
                break;
            case "2":
                drawable = context.getResources().getDrawable(R.drawable.bulb_svg);
                break;
            case "5":
                drawable = context.getResources().getDrawable(R.drawable.socket_g_svg);
                break;
            case "10":
                drawable = context.getResources().getDrawable(R.drawable.remote_icon);
                break;
            case "11":
                drawable = context.getResources().getDrawable(R.drawable.ic_device_door);
                break;
        }
        return drawable;
    }

    private static void editRoom(Context context, String JSONroomname, String finalJSONroomId) {
        final Dialog dialog = new Dialog(context);
        final String[] dia_name = new String[1];
        View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_addroom, null, false);
        Constants.dia_cardRoom = view1.findViewById(R.id.cardRoom);
        if (!Constants.savetoShared(context).getString(JSONroomname, "not").equals("not")) {
            Constants.dia_cardRoom.setBackground(Constants.getDrawableByName(Constants.savetoShared(context).getString(JSONroomname, "not"), context));
        }
        EditText editText = view1.findViewById(R.id.diaEditName);
        editText.setText(JSONroomname);
        dia_name[0] = JSONroomname;
        final TextView textView = view1.findViewById(R.id.roomName);
        textView.setText(JSONroomname);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
                dia_name[0] = s.toString();
            }
        });
        Button dia_add = view1.findViewById(R.id.dia_add);
        dia_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view2 = LayoutInflater.from(context).inflate(R.layout.card_rooms, null, false);
                TextView textView = view2.findViewById(R.id.roomName);
                RelativeLayout dia_cardRoom = view2.findViewById(R.id.cardRoom);
                textView.setText(dia_name[0]);
                ((BaseActivity) context).showProgressDialog("Updating");

                DtypeViews.UpdateRoomName(context, dia_name[0], finalJSONroomId);

                Log.d(TAG, dia_name[0] + " onClick: " + Constants.gradename);
                Constants.savetoShared(context).edit().putString(dia_name[0], Constants.gradename).apply();
                dia_cardRoom.setBackground(Constants.dia_backdrawable);
                //roomholder.addView(view2);
                //  DtypeViews.getGetDevice(context);
                //((MainActivity)context).addViews(Constants.savetoShared(context).getString(Constants.ROOMS,"NA"));
                dialog.dismiss();

            }
        });
        dialog.setContentView(view1);
        dialog.show();
    }

}
