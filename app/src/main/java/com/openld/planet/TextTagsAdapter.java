package com.openld.planet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.openld.planet.planet.TagsAdapter;

/**
 * author: lllddd
 * created on: 2021/9/5 14:33
 * description:
 */
public class TextTagsAdapter extends TagsAdapter {
    private String[] mArray;

    public TextTagsAdapter(String[] array) {
        super();
        this.mArray = array;
    }

    @Override
    public int getCount() {
        return mArray.length;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setText("No." + position);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Tag " + position + " clicked.");
                Toast.makeText(context, "Tag " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
        tv.setTextColor(Color.WHITE);
        return tv;
    }

    @Override
    public Object getItem(int position) {
        return mArray[position];
    }

    @Override
    public int getPopularity(int position) {
        return position % 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor, float alpha) {
        view.setBackgroundColor(themeColor);
    }
}
