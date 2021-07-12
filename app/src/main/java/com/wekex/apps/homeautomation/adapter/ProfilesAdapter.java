package com.wekex.apps.homeautomation.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.HomeActivity;
import com.wekex.apps.homeautomation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.wekex.apps.homeautomation.utils.Utils.HEADING;
import static com.wekex.apps.homeautomation.utils.Utils.ICON;
import static com.wekex.apps.homeautomation.utils.Utils.SCENEID;
import static com.wekex.apps.homeautomation.utils.Utils.SUBHEADING;


public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ViewHolder> {

    private ArrayList<JSONObject> jsonList;
    private Context context;

    public ProfilesAdapter(Context contxt, JSONArray list) {
        context = contxt;
        jsonList = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            try {
                jsonList.add(list.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profiles_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject jsonObject = jsonList.get(position);
        Log.d("letsee", "onBindViewHolder: " + jsonObject.toString());
        try {
            final String sceneId = jsonObject.getString("ID");
            final String heading = jsonObject.getString("Name");

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heading.equalsIgnoreCase("add"))
                        ((HomeActivity) context).triggerscene(heading);
                    else
                        ((HomeActivity) context).triggerscene(sceneId);
                }
            });
            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!heading.equals("add"))
                        ((HomeActivity) context).scene_edit_menu(jsonObject, position);
                    return false;
                }
            });
            holder.prof_heading.setText(heading);

            if (heading.equals("add")) {
                holder.prof_heading.setVisibility(View.VISIBLE);
                holder.prof_subheading.setVisibility(View.GONE);
                holder.prof_heading.setText("Scene Shortcut");
                holder.prof_img.setImageResource(R.drawable.remote_add_active);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //handling item click event
    }

    @Override
    public int getItemCount() {
        return jsonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView prof_heading, prof_subheading;
        ImageView prof_img;
        LinearLayout parent;

        public ViewHolder(View itemView) {
            super(itemView);
            prof_heading = itemView.findViewById(R.id.prof_heading);
            prof_subheading = itemView.findViewById(R.id.prof_subheading);
            prof_img = itemView.findViewById(R.id.prof_img);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
