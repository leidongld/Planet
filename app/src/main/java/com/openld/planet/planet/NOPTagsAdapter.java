package com.openld.planet.planet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: lllddd
 * created on: 2021/9/4 21:40
 * description:
 */
class NOPTagsAdapter extends TagsAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        return null;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getPopularity(int position) {
        return 0;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor, float alpha) {

    }
}
