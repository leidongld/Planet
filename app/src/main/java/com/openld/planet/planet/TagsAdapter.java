package com.openld.planet.planet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: lllddd
 * created on: 2021/9/4 13:33
 * description:抽象标签适配器
 */
public abstract class TagsAdapter {
    private OnDataSetChangeListener onDataSetChangeListener;

    public abstract int getCount();

    public abstract View getView(Context context, int position, ViewGroup parent);

    public abstract Object getItem(int position);

    public abstract int getPopularity(int position);

    public abstract void onThemeColorChanged(View view, int themeColor, float alpha);

    protected interface OnDataSetChangeListener {
        void onDataSetChanged();
    }

    public void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
        this.onDataSetChangeListener = listener;
    }
}
