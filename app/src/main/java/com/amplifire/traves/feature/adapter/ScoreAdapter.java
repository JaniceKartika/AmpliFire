package com.amplifire.traves.feature.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.ScoreDao;
import com.amplifire.traves.widget.ImageViewMeasurement;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by user on 18/10/2017.
 */

public class ScoreAdapter extends BaseAdapter{
    // params
    ArrayList listItem;
    Activity activity;

    public ScoreAdapter(ArrayList listItem, Activity activity) {
        this.listItem = listItem;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_rewards, null);
            holder.txtPoints = (TextView)view.findViewById(R.id.txt_item_points);
            holder.txtTitle = (TextView)view.findViewById(R.id.txt_item_title);
            holder.imgItem = (ImageViewMeasurement) view.findViewById(R.id.img_item_rewards);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        ScoreDao item = (ScoreDao) getItem(i);
        holder.txtTitle.setText(item.getTitle());
        holder.txtPoints.setText(item.getPoints());

        Glide.with(activity).load(item.getImgUrl()).into(holder.imgItem);

        return view;
    }

    static class ViewHolder{
        ImageViewMeasurement imgItem;
        TextView txtTitle, txtPoints;
    }
}
