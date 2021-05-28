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

import java.util.ArrayList;

import static com.wekex.apps.homeautomation.selectRemote.apiString;
import static com.wekex.apps.homeautomation.utils.Constants.setIMagetoView;

class IconViewAdapter extends RecyclerView.Adapter<IconViewAdapter.ViewHolder> {
    private ArrayList<String> jsonList;
    private Context context;

    public IconViewAdapter(Context contxt, ArrayList<String> list) {
        context = contxt;
        jsonList = list;
    }

    @Override
    public IconViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profiles_card, parent, false);
        return new IconViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IconViewAdapter.ViewHolder holder, final int position) {
        String name = jsonList.get(position).toUpperCase().trim().replace("_", " ");

        final String heading = name;
        final String subHeading = "......";
        final String icon = "icons/" + name.toLowerCase().replace(" ", "_");
        holder.prof_heading.setText(heading);
        holder.prof_subheading.setText(subHeading);
        setIMagetoView(icon, holder.prof_img, context);

        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                apiString.add("type=" + jsonList.get(position));
                String apirequest = "";
                for (String values : apiString) {
                    apirequest += values + "&";
                }
                Log.d("RemoteAdapetr", "onClick: " + apirequest);
                ((selectRemote) context).getGetDevice(apirequest);
            }
        });


        //apiString.add(;
        //
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
