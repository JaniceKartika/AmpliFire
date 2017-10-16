package com.amplifire.traves.feature.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.QuestDao;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestStartedAdapter extends RecyclerView.Adapter<QuestStartedAdapter.QuestStartedViewHolder> {
    private Context mContext;
    private ArrayList<QuestDao> mQuestsDao = new ArrayList<>();
    private ItemClickListener mClickListener;

    public QuestStartedAdapter(Context context, ItemClickListener clickListener) {
        mContext = context;
        mClickListener = clickListener;
    }

    public void setData(Map<String, QuestDao> questsDao) {
        mQuestsDao.clear();
        mQuestsDao.addAll(questsDao.values());
    }

    @Override
    public QuestStartedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_quest, parent, false);
        return new QuestStartedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestStartedViewHolder holder, int position) {
        QuestDao questDao = mQuestsDao.get(position);
        holder.tvTitle.setText(questDao.getTitle());
        holder.tvDescription.setText(questDao.getDesc());

        // TODO hardcoded point value
        holder.tvPoint.setText("Point: 0");
    }

    @Override
    public int getItemCount() {
        if (mQuestsDao == null) return 0;
        return mQuestsDao.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }

    class QuestStartedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivThumbnail)
        ImageView ivThumbnail;
        @BindView(R.id.tv_title_quest)
        TextView tvTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_complete)
        IconTextView tvComplete;
        @BindView(R.id.tv_point)
        TextView tvPoint;

        QuestStartedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setTag(itemView);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }
}
