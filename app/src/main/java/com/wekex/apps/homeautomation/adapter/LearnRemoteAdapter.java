package com.wekex.apps.homeautomation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.Utility;
import com.wekex.apps.homeautomation.model.LearnRemoteModel;
import com.wekex.apps.homeautomation.model.Model_UserRemoteList;
import com.wekex.apps.homeautomation.selectRemote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.wekex.apps.homeautomation.selectRemote.apiString;

public class LearnRemoteAdapter extends RecyclerView.Adapter<LearnRemoteAdapter.MyPlayerHolder> {
    ArrayList<LearnRemoteModel> _lst_key;
    Context context;
    String team;
    String key;
    View.OnClickListener _listener;
    String _remote_name;

    public LearnRemoteAdapter(Context playerList, ArrayList<LearnRemoteModel> arrayList, View.OnClickListener _listener, String _remote_name) {
        context = playerList;
        _lst_key = arrayList;
        this._listener = _listener;
        this._remote_name = _remote_name;
    }


    @NonNull
    @Override
    public MyPlayerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.learn_remote_layout, viewGroup, false);
        return new MyPlayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlayerHolder v, int i) {
        LearnRemoteModel data = _lst_key.get(i);
        Log.d("lll", "onBindViewHolder: " + data.toString());
        v.tv_key_name.setText(data.getKey_name());
        v.iv_icon.setImageResource(data.getKey_icon());
        if (data.isAlreadyLearn())
            v.ll_learn.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        else
            v.ll_learn.setBackgroundColor(context.getResources().getColor(R.color.gray600));

        if (data.isCustomKey()) {
            v.iv_delete.setVisibility(View.VISIBLE);
        } else
            v.iv_delete.setVisibility(View.GONE);
        v.tv_learn.setText(data.getLearn_text());
    }


    @Override
    public int getItemCount() {
        return _lst_key.size();
    }

    public class MyPlayerHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon, iv_delete;
        TextView tv_key_name, tv_learn;
        LinearLayout ll_learn;

        public MyPlayerHolder(@NonNull View view) {
            super(view);
            ll_learn = view.findViewById(R.id.ll_learn);
            tv_key_name = view.findViewById(R.id.tv_key_name);
            tv_learn = view.findViewById(R.id.tv_learn);
            iv_icon = view.findViewById(R.id.iv_key_icon);
            iv_delete = view.findViewById(R.id.iv_delete);
            ll_learn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setTag(getAdapterPosition());
                    _listener.onClick(view);
                }
            });

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context).setTitle(context.getString(R.string.delete)).setMessage("Want to delete " + _lst_key.get(getAdapterPosition()).getKey_name() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Gson _gson = new Gson();
                                    Utility _utility = new Utility(context);
                                    TypeToken<ArrayList<Model_UserRemoteList.keys>> token_key = new TypeToken<ArrayList<Model_UserRemoteList.keys>>() {
                                    };
                                    ArrayList<Model_UserRemoteList.keys> _remote_key_data = _gson.fromJson(_utility.getString(_remote_name + "_custom"), token_key.getType());


                                    for (int j = 0; j < _remote_key_data.size(); j++) {
                                        if (_lst_key.get(getAdapterPosition()).getKey_name().equalsIgnoreCase(_remote_key_data.get(j).getKey())) {
                                            _remote_key_data.remove(j);
                                            break;
                                        }
                                    }

                                    _utility.putString(_remote_name + "_custom", _gson.toJson(_remote_key_data));

                                    _lst_key.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            });
        }
    }

}




