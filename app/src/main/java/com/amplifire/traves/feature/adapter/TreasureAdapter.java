package com.amplifire.traves.feature.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.TreasureDao;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TreasureAdapter extends RecyclerView.Adapter<TreasureAdapter.MyViewHolder> {

    private List<TreasureDao> daos;
    private event ev;

    public TreasureAdapter(event ev, List<TreasureDao> daos) {
        this.ev = ev;
        this.daos = daos;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.treasure_title)
        TextView treasureTitle;
        @BindView(R.id.treasure_complete)
        IconTextView treasureComplete;
        @BindView(R.id.treasure)
        LinearLayout treasure;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treasure, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        TreasureDao treasure = daos.get(position);
        holder.treasureTitle.setText(treasure.getDesc() + "");
        holder.treasureComplete.setText("");
        if (treasure.getStatus() == 2) {
            holder.treasureComplete.setText("{fa-check-circle}");
        }
        holder.treasure.setOnClickListener(v -> {
            ev.searchBarcode(position);
        });
    }


    @Override
    public int getItemCount() {
        return daos.size();
    }

    public interface event {
        void searchBarcode(int position);
    }


}
