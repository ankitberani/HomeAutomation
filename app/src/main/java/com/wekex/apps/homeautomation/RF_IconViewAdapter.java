package com.wekex.apps.homeautomation;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.wekex.apps.homeautomation.utils.Constants.setIMagetoView;

class RF_IconViewAdapter extends RecyclerView.Adapter<RF_IconViewAdapter.ViewHolder> {

    private ArrayList<JSONObject> jsonList = new ArrayList<>();
    private Context context;

    public RF_IconViewAdapter(Context contxt, JSONArray list) {
        context = contxt;

        for (int i = 0; i < list.length(); i++) {
            try {
                jsonList.add(list.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("RFI", "RF_IconViewAdapter: " + e.getMessage());
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
            final String heading = jsonObject.getString("Name");
            final String subHeading = jsonObject.getString("R_Type");
            final String icon = "icons/" + jsonObject.getString("R_Type").toLowerCase();
            final String keys = jsonObject.getString("Keys");

            holder.prof_heading.setText(heading);
            holder.prof_subheading.setText(subHeading);
            setIMagetoView(icon, holder.prof_img, context);

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RF_selectRemote) context).callRemote(jsonObject);
                }

            });

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
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            prof_heading = itemView.findViewById(R.id.prof_heading);
            prof_subheading = itemView.findViewById(R.id.prof_subheading);
            prof_img = itemView.findViewById(R.id.prof_img);
            card = itemView.findViewById(R.id.card);
        }
    }


}
