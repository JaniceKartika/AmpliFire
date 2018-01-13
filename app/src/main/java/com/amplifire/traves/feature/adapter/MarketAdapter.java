package com.amplifire.traves.feature.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.amplifire.traves.R;
import com.amplifire.traves.model.MarketItemDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.MyViewHolder> {

    private List<MarketItemDao> daos;
    private event ev;

    public MarketAdapter(event ev, List<MarketItemDao> daos) {
        this.ev = ev;
        this.daos = daos;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox checkbox;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        MarketItemDao market = daos.get(position);
        holder.checkbox.setText(market.getQuantity() + " " + market.getName());
        holder.checkbox.setChecked(false);
        if (market.getChecked() == 1) {
            holder.checkbox.setChecked(true);
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.checkbox.isChecked() != b) {
                    ev.updateCheckMarket(position, b);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return daos.size();
    }

    public interface event {
        void updateCheckMarket(int position, boolean isChecked);
    }


}
