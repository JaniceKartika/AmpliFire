package com.amplifire.traves.feature.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.DrawerDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {

    private List<DrawerDao> blocks = new ArrayList<>();
    private DrawerView drawerView;
    private Context context;

    public DrawerAdapter(DrawerView drawerView, List<DrawerDao> blocks) {
        this.blocks = blocks;
        this.drawerView = drawerView;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title)
        TextView textTitle;
        @BindView(R.id.relative)
        RelativeLayout relative;
        @BindView(R.id.divider)
        View divider;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final DrawerDao dao = blocks.get(position);
        holder.textTitle.setText(dao.getTitle());


        holder.divider.setVisibility(View.GONE);
        if (dao.isDivider()) {
            holder.divider.setVisibility(View.VISIBLE);
        }

        if (dao.isSelected()) {
            holder.relative.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_light));
        } else {
            holder.relative.setBackgroundColor(0);
        }

        holder.relative.setOnClickListener(v -> {
            drawerView.drawerSelect(position);
        });


    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    public interface DrawerView {
        void drawerSelect(int position);
    }


}
