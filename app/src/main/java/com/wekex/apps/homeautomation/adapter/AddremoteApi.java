package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.selectRemote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.wekex.apps.homeautomation.selectRemote.apiString;

public class AddremoteApi extends RecyclerView.Adapter<AddremoteApi.MyPlayerHolder> {
    ArrayList<String> objects;
    Context context;
    String team;
    String key;
    public AddremoteApi(Context playerList, String arrayList) {
        context = playerList;
        objects = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(arrayList);
            key = jsonObject.keys().next();
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0;i < jsonArray.length();i++) {
                objects.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public MyPlayerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remote_selectlist_item,viewGroup,false);
        return new MyPlayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlayerHolder v, int i) {
        String data = objects.get(i);
        Log.d("lll", "onBindViewHolder: "+data.toString());
        v.brandname.setText(data);
        v.brandname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (key){
                    case "type" :   apiString.add("type="+data);break;
                    case "brands" :   apiString.add("brand="+data);break;
                    case "models" :   apiString.add("model="+data);break;
                }
                String apirequest = "";
                for (String values: apiString) {
                    apirequest += values+"&";
                }
                Log.d("RemoteAdapetr", "onClick: "+apirequest);
             ((selectRemote)context).getGetDevice(apirequest);
            }
        });
    }




    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class MyPlayerHolder extends RecyclerView.ViewHolder {
        TextView brandname,value,r_type;
        ImageView overseas,retained;
        LinearLayout card;
        public MyPlayerHolder(@NonNull View view) {
            super(view);

            brandname = view.findViewById(R.id.brandName);
            r_type = view.findViewById(R.id.r_type);
            r_type.setVisibility(View.GONE);

        }
    }

    public int getColorByName(String name) {
        int colorId = 0;
        Log.d("PLadapt", "getColorByName: "+name);

        try {
            Class res = R.color.class;
            Field field = res.getField(name);
            colorId = field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return colorId;
    }
}




