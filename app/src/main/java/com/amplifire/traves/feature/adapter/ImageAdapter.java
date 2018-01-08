package com.amplifire.traves.feature.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amplifire.traves.R;
import com.amplifire.traves.model.ImageDao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.graphics.Bitmap.createScaledBitmap;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private List<ImageDao> image;
    private ImageAdapter.event ev;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            foto = (ImageView) view.findViewById(R.id.foto);
        }
    }

    public ImageAdapter(event ev, List<ImageDao> image) {
        this.ev = ev;
        this.image = image;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (image.get(position).isButton()) {
            holder.foto.setImageResource(R.drawable.ic_add_grey_24dp);
        } else {
            try {
                holder.foto.setImageBitmap(compress(createScaledBitmap(image.get(position).getImage(), 60, 60, true)));
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        holder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.get(position).isButton()) {
                    ev.imageAdd(-1);
                } else {
                    ev.imageViewer(position);
                }
            }
        });


    }

    private Bitmap compress(Bitmap original) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.PNG, 50, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }

    @Override
    public int getItemCount() {
        return image.size();
    }

    public interface event {
        void imageViewer(int position);

        void imageAdd(int position);
    }

}
