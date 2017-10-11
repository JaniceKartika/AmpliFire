package com.amplifire.traves.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.amplifire.traves.R;


public class ImageViewMeasurement extends AppCompatImageView {

    private int mWidthDivider = 1;
    private int mHeightDivider = 1;
    private boolean mMeasureWithHeight;

    public ImageViewMeasurement(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ImageViewMeasurement(Context context, AttributeSet attrs) {
        super(context, attrs);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageViewMeasurement);
        mWidthDivider = a.getInteger(R.styleable.ImageViewMeasurement_width_divider, 1);
        mHeightDivider = a.getInteger(R.styleable.ImageViewMeasurement_height_divider, 1);
        mMeasureWithHeight = a.getBoolean(R.styleable.ImageViewMeasurement_measure_with_height, false);
        a.recycle();


    }

    public ImageViewMeasurement(Context context) {
        super(context);
    }

    public void setAdjustViewBounds(boolean adjustViewBounds) {
        super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        if (mMeasureWithHeight) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = height / mWidthDivider;
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = width / mHeightDivider;
        }

        setMeasuredDimension(width, height);
    }


}