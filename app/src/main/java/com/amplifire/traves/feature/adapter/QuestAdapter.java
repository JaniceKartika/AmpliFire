package com.amplifire.traves.feature.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.maps.QuestDetailActivity;
import com.amplifire.traves.model.QuestDao;
import com.amplifire.traves.utils.FirebaseUtils;
import com.amplifire.traves.utils.Utils;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.MyViewHolder> {

    private ItemClickListener itemClickListener;
    private List<QuestDao> QuestDaos = new ArrayList<>();
    private Context context;

    public QuestAdapter(ItemClickListener itemClickListener, List<QuestDao> blocks) {
        this.itemClickListener = itemClickListener;
        this.QuestDaos = blocks;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivThumbnail)
        ImageView ivThumbnail;
        @BindView(R.id.tv_title_quest)
        TextView tvTitleQuest;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_complete)
        IconTextView tvComplete;
        @BindView(R.id.cardview)
        CardView cardview;
        @BindView(R.id.tv_point)
        TextView tvPoint;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quest, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final QuestDao dao = QuestDaos.get(position);

        holder.tvTitleQuest.setText(dao.getTitle());
        holder.tvDescription.setText(dao.getDesc());
        Utils.setImage(context, dao.getImageUrl(), holder.ivThumbnail);
        if (dao.getStatus() == 2) {
            holder.tvComplete.setText("{fa-check-circle}");
        }
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClickListener(dao.getKey());
                Intent intent = new Intent(context, QuestDetailActivity.class);
                intent.putExtra(FirebaseUtils.QUEST, dao.getKey());
            }
        });

    }

    @Override
    public int getItemCount() {
        return QuestDaos.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(String key);
    }
}
