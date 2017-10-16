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
import com.amplifire.traves.model.QuestDao;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zacky on 10/15/2017.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {

    private PlaceSelect placeSelect;
    private ArrayList<QuestDao> mQuestsDao = new ArrayList<>();
    private Context context;


    public PlaceListAdapter(PlaceSelect placeSelect, Map<String, QuestDao> questDaos) {

        this.placeSelect = placeSelect;
        mQuestsDao.clear();
        mQuestsDao.addAll(questDaos.values());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview)
        CardView mCardView;
        @BindView(R.id.ivThumbnail)
        ImageView mIVThumbnail;
        @BindView(R.id.tv_title_quest)
        TextView mTitle;
        @BindView(R.id.tv_description)
        TextView mDescription;
        @BindView(R.id.tv_complete)
        IconTextView mComplete;
        @BindView(R.id.tv_point)
        TextView mPoint;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quest, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        QuestDao questDao = mQuestsDao.get(position);
        holder.mTitle.setText(questDao.getTitle());
        holder.mDescription.setText(questDao.getDesc());

        Glide.with(context)
                .load(questDao.getImageUrl())
                .placeholder(R.drawable.ic_default_profil_pict).error(R.drawable.ic_default_profil_pict)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIVThumbnail);

        holder.mCardView.setOnClickListener(v -> placeSelect.selectedPosition(questDao.getKey()));

        // TODO hardcoded point value
        holder.mPoint.setText("Point: 0");
    }

    @Override
    public int getItemCount() {
        return mQuestsDao.size();
    }

    public interface PlaceSelect {
        void selectedPosition(String key);
    }

}
