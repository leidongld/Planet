package com.openld.planet.planet;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

/**
 * author: lllddd
 * created on: 2021/9/4 12:47
 * description:标签
 */
public class Tag implements Comparable<Tag> {
    private int mPopularity;

    private float mScale;

    private float[] mColors;

    private View mView;

    // 平面圆心
    private PointF mFlatCenter;

    // 空间球心
    private Point3DF mSpatialCenter;

    private static final int DEFAULT_POPULARITY = 5;

    public Tag() {
        this(0f, 0f, 0f, 1.0f, DEFAULT_POPULARITY);
    }

    public Tag(int popularity) {
        this(0f, 0f, 0f, 1.0f, popularity);
    }

    public Tag(float x, float y, float z) {
        this(x, y, z, 1.0f, DEFAULT_POPULARITY);
    }

    public Tag(float x, float y, float z, float scale) {
        this(x, y, z, scale, DEFAULT_POPULARITY);
    }

    public Tag(float x, float y, float z, float scale, int popularity) {
        this.mSpatialCenter = new Point3DF(x, y, z);
        this.mFlatCenter = new PointF(x, y);
        this.mColors = new float[]{1.0f, 0.5f, 0.5f, 0.5f};
        this.mScale = scale;
        this.mPopularity = popularity;
    }

    public float getSpatialX() {
        return mSpatialCenter.x;
    }

    public void setSpatialX(float x) {
        this.mSpatialCenter.x = x;
    }

    public float getSpatialY() {
        return mSpatialCenter.y;
    }

    public void setSpatialY(float y) {
        this.mSpatialCenter.y = y;
    }

    public float getSpatialZ() {
        return this.mSpatialCenter.z;
    }

    public void setSpatialZ(float z) {
        this.mSpatialCenter.z = z;
    }

    public float getScale() {
        return this.mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public View getView() {
        return this.mView;
    }

    public void bindView(View view) {
        this.mView = view;
    }

    public float getAlpha() {
        return this.mColors[0];
    }

    public void setAlpha(float alpha) {
        this.mColors[0] = alpha;
    }

    public int getPopularity() {
        return this.mPopularity;
    }

    public float getFlatX() {
        return this.mFlatCenter.x;
    }

    public void setFlatX(Float x) {
        this.mFlatCenter.x = x;
    }

    public float getFlatY() {
        return this.mFlatCenter.y;
    }

    public void setFlatY(float y) {
        this.mFlatCenter.y = y;
    }

    public void setColorComponent(float[] rgb) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, mColors, mColors.length - rgb.length, rgb.length);
        }
    }

    public int getColor() {
        int[] result = new int[4];
        for (int i = 0; i < mColors.length; i++) {
            result[i] = (int) (mColors[i] * 0xff);
        }
        return Color.argb(result[0], result[1], result[2], result[3]);
    }

    @Override
    public int compareTo(Tag o) {
        return this.mScale > o.mScale ? 1 : -1;
    }
}
