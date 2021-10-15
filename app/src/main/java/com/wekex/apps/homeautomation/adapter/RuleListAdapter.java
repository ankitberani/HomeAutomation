package com.wekex.apps.homeautomation.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wekex.apps.homeautomation.Interfaces.RuleOperationListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.AllDataResponseModel;
import com.wekex.apps.homeautomation.model.RuleListModel;
import com.wekex.apps.homeautomation.model.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RuleListAdapter extends RecyclerView.Adapter<RuleListAdapter.myViewHolder> {

    Activity activity;
    ArrayList<RuleListModel.Rules> _ruleList;
    boolean isFromAccesories;
    RuleOperationListener _listener;

    public RuleListAdapter(Activity activity, ArrayList<RuleListModel.Rules> _ruleList, RuleOperationListener _listener) {
        this.activity = activity;
        this._ruleList = _ruleList;
        this._listener = _listener;
    }

    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_list_item, parent, false);
        return new RuleListAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myViewHolder holder, int position) {
        try {
            RuleListModel.Rules obj = _ruleList.get(position);
            if (obj != null) {
                holder.tvRuleName.setText(obj.getDno());

                holder.tvTime.setText("From " + obj.F_Time + " To " + obj.T_time);
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at bind " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return _ruleList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvRuleName, tvTime;
        LinearLayout ll_main_item;
        ImageView ivDelet, ivEdit;

        public myViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvRuleName = itemView.findViewById(R.id.tv_rule_name);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivDelet = itemView.findViewById(R.id.iv_del_rule);
            ivEdit = itemView.findViewById(R.id.iv_edt_rule);

            ll_main_item = itemView.findViewById(R.id.ll_main_item);
            ivDelet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _listener.delRules(getAdapterPosition());
                }
            });
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _listener.editRules(getAdapterPosition());
                }
            });
        }
    }

}