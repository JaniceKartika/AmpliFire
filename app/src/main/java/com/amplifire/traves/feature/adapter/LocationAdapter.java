package com.amplifire.traves.feature.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.LocationDao;
import com.amplifire.traves.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {

    private QuestSelect questSelect;
    private List<LocationDao> locationDaos = new ArrayList<>();
    private Context context;

    public LocationAdapter(QuestSelect questSelect, List<LocationDao> blocks) {
        this.questSelect = questSelect;
        this.locationDaos = blocks;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.ivThumbnail)
        ImageView ivThumbnail;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_total_quest)
        TextView tvTotalQuest;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final LocationDao dao = locationDaos.get(position);

        Utils.setImage(context, dao.getImageUrl(), holder.ivThumbnail);

        holder.tvTitle.setText(dao.getName());
        if (dao.getQuest() != null) {
            holder.tvTotalQuest.setText(dao.getQuest().size() + "\n" + context.getString(R.string.text_quest));
        } else {
            holder.tvTotalQuest.setText("0\n" + context.getString(R.string.text_quest));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questSelect.selectedPosition(dao.getKey());
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationDaos.size();
    }

    public interface QuestSelect {
        void selectedPosition(String key);
    }

}
