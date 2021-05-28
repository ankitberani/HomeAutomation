package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.RoomModel;

import java.util.ArrayList;

public class SingleAdapter extends RecyclerView.Adapter<SingleAdapter.SingleViewHolder> {

    private Context context;
    private ArrayList<RoomModel> cities;

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private int checkedPosition = -1;

    public SingleAdapter(Context context, ArrayList<RoomModel> employees) {
        this.context = context;
        this.cities = employees;
    }

    public void setEmployees(ArrayList<RoomModel> employees) {
        this.cities = new ArrayList<>();
        this.cities = employees;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_move_to, viewGroup, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final RoomModel employee) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            textView.setText(employee.getName());

            itemView.setOnClickListener(view -> {
                imageView.setVisibility(View.VISIBLE);
                if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                }
            });
        }
    }

    public RoomModel getSelected() {
        if (checkedPosition != -1) {
            return cities.get(checkedPosition);
        }
        return null;
    }
}
