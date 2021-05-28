package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.helperclass.rgb_color_interface;
import com.wekex.apps.homeautomation.model.color_item;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {


    ArrayList<color_item> _list;
    Context context;
    rgb_color_interface objInterface;

    public ColorPickerAdapter(ArrayList<color_item> _list, Context context, rgb_color_interface objInterface) {
        this._list = _list;
        this.context = context;
        this.objInterface = objInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        color_item object = _list.get(position);
        holder.view.setBackgroundColor(object.getColor_code());

        if (object.getSelected())
            holder.iv_selected.setVisibility(View.VISIBLE);
        else
            holder.iv_selected.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView iv_selected, iv_remove;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_1);
            iv_selected = itemView.findViewById(R.id.iv_tick);
            iv_remove = itemView.findViewById(R.id.iv_remove);
            iv_selected.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    objInterface.selectedColor(_list.get(getAdapterPosition()).getColor_code());
                    for (int i = 0; i < _list.size(); i++) {
                        _list.get(i).setSelected(false);
                    }
                    _list.get(getAdapterPosition()).setSelected(true);
                    notifyDataSetChanged();

                }
            });

            iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _list.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}
