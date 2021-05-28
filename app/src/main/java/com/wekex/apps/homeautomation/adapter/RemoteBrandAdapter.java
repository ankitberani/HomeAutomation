package com.wekex.apps.homeautomation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wekex.apps.homeautomation.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RemoteBrandAdapter extends RecyclerView.Adapter<RemoteBrandAdapter.ViewHolder> {


    ArrayList<String> _list;
    Context context;

    String _dno = "";
    String _type = "";
    View.OnClickListener _listener;

    public RemoteBrandAdapter(ArrayList<String> _list, Context context, String _dno, String _cat_type, View.OnClickListener _listener) {
        this._list = _list;
        this.context = context;
        this._dno = _dno;
        this._type = _cat_type;
        this._listener = _listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder._tv_cat_name.setText(_list.get(position));
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parent;
        TextView _tv_cat_name;

        public ViewHolder(View itemView) {
            super(itemView);
            _tv_cat_name = itemView.findViewById(R.id.tv_cate_name);
            parent = itemView.findViewById(R.id.parent);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, _list.get(getAdapterPosition()) + "", Toast.LENGTH_SHORT).show();
                    v.setTag(_list.get(getAdapterPosition()));
                    _listener.onClick(v);
                }
            });
        }
    }
}
