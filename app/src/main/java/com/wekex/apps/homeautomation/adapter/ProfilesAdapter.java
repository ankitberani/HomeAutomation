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


/**
 * Created by Nirmal on 22/04/2018.
 */

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
            final String heading = jsonObject.getString(HEADING);
            final String subHeading = jsonObject.getString(SUBHEADING);
            final String icon = jsonObject.getString(ICON);
            final String sceneId = jsonObject.getString(SCENEID);

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity) context).triggerscene(sceneId);
                }
            });
            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!sceneId.equals("add"))
                        ((HomeActivity) context).scene_edit_menu(jsonObject, position);
                    return false;
                }
            });

            ((HomeActivity) context).setIMagetoView(icon, holder.prof_img);
            holder.prof_heading.setText(heading);
            holder.prof_subheading.setText(subHeading);

            if (sceneId.equals("add")) {
                holder.prof_heading.setVisibility(View.GONE);
                holder.prof_subheading.setVisibility(View.GONE);
                holder.prof_img.setImageResource(R.drawable.remote_add);
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
/*
    private ArrayList<String> noOfplayer(String sweet){

        for(int i=0;i<players.size();i++)
        {
            if(players.get(i).startsWith(sweet.toUpperCase()))
            {
                MySortStrings.add(players.get(i));
             //   Log.d("lets check",players.get(i));
            }
        }
        return MySortStrings;
    }
    private void dailog(String label){
        MySortStrings = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.dialogtextview
                , noOfplayer(label));
        Dialog adb = new Dialog(context);
        adb.setContentView(R.layout.view);
        ListView lv = adb.findViewById(R.id.mList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(context, MySortStrings.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,profile.class);
                intent.putExtra("byalpha",MySortStrings.get(i));
                context.startActivity(intent);

            }
        });
        adb.show();
    }
    */
}
