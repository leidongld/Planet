package com.openld.planet.planet;

import android.telephony.ims.ImsManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: lllddd
 * created on: 2021/9/4 14:02
 * description:标签星球
 */
public class TagPlanet {
    private List<Tag> mTagList;

    private int mRadius;

    private static final int DEFAULT_RADIUS = 3;

    private static final float[] DEFAULT_COLOR_DARK = {0.886f, 0.725f, 0.188f, 1.0f};

    private static final float[] DEFAULT_COLOR_LIGHT = {0.3f, 0.3f, 0.3f, 1.0f};

    private float[] mLightColor;

    private float[] mDarkColor;

    private float mSinX;
    private float mCosX;
    private float mSinY;
    private float mCosY;
    private float mSinZ;
    private float mCosZ;

    private float mInertiaX = 0f;
    private float mInertiaY = 0f;
    private float mInertiaZ = 0f;

    private int mMinPopularity;
    private int mMaxPopularity;

    private boolean mRebuildOnUpdate = true;

    private float mMinDelta;
    private float mMaxDelta;

    public TagPlanet() {
        this(DEFAULT_RADIUS);
    }


    public TagPlanet(int radius) {
        this(new ArrayList<Tag>(), radius);
    }

    public TagPlanet(@NonNull ArrayList<Tag> tagList) {
        this(tagList, DEFAULT_RADIUS);
    }

    public TagPlanet(@NonNull ArrayList<Tag> tagList, int radius) {
        this(tagList, radius, DEFAULT_COLOR_DARK, DEFAULT_COLOR_LIGHT);
    }

    public TagPlanet(@NonNull ArrayList<Tag> tagList, int radius, float[] darkColor, float[] lightColor) {
        this.mTagList = tagList;
        this.mRadius = radius;
        this.mDarkColor = darkColor;
        this.mLightColor = lightColor;
    }

    public void clear() {
        if (mTagList == null || mTagList.isEmpty()) {
            return;
        }
        mTagList.clear();
    }

    public List<Tag> getTagList() {
        return mTagList;
    }

    public Tag get(int position) {
        if (mTagList == null || position >= mTagList.size()) {
            return null;
        }
        return mTagList.get(position);
    }

    public void reset() {
        create(mRebuildOnUpdate);
    }

    public void update() {
        if (Math.abs(mInertiaX) > 0.1f || Math.abs(mInertiaY) > 0.1f) {
            recalculateAngle();
            updateAll();
        }
    }

    public void initTag(Tag tag) {
        float percentage = getPercentage(tag);
        float[] argb = getColorFromGradient(percentage);
        tag.setColorComponent(argb);
    }

    private float getPercentage(Tag tag) {
        int p = tag.getPopularity();
        return mMinPopularity == mMaxPopularity ? 1.0f : ((float) p - mMinPopularity) / (mMaxPopularity - mMinPopularity);
    }

    public void addTag(Tag newTag) {
        initTag(newTag);

        position(newTag);

        mTagList.add(newTag);

        updateAll();
    }

    /**
     * 根据球坐标随机生成Tag的位置
     *
     * @param newTag 新Tag
     */
    private void position(Tag newTag) {
        double phi = Math.random() * Math.PI;
        double theta = Math.random() * Math.PI * 2;

        newTag.setSpatialX((float) (mRadius * Math.cos(theta) * Math.sin(phi)));
        newTag.setSpatialY((float) (mRadius * Math.sin(theta) * Math.sin(phi)));
        newTag.setSpatialZ((float) (mRadius * Math.cos(phi)));
    }

    public void create(boolean rebuild) {
        this.mRebuildOnUpdate = rebuild;
        positionAll(mRebuildOnUpdate);
        calculatePopularity();
        recalculateAngle();
        updateAll();
    }

    /**
     * 更新所有Tag的透明度与大小
     */
    private void updateAll() {
        for (int i = 0; i < mTagList.size(); i++) {
            Tag tag = mTagList.get(i);
            float x = tag.getSpatialX();
            float y = tag.getSpatialY();
            float z = tag.getSpatialZ();

            float rx1 = x;
            float ry1 = y * mCosX + z * -mSinX;
            float rz1 = y * mSinX + z * mCosX;

            float rx2 = rx1 * mCosY + rz1 * mSinY;
            float ry2 = ry1;
            float rz2 = rx1 * -mSinY + rz1 * mCosY;

            float rx3 = rx2 * mCosZ + ry2 * -mSinZ;
            float ry3 = rx2 * mSinZ + ry2 * mCosZ;
            float rz3 = rz2;

            tag.setSpatialX(rx3);
            tag.setSpatialY(ry3);
            tag.setSpatialZ(rz3);
            int diameter = 2 * mRadius;
            float per = diameter / 1.0f / (diameter + rz3);
            tag.setFlatX(rx3 * per);
            tag.setFlatY(ry3 * per);
            tag.setScale(per);

            float delta = diameter + rz3;
            mMaxDelta = Math.max(mMaxDelta, delta);
            mMinDelta = Math.min(mMinDelta, delta);
            float alpha = (delta - mMinDelta) / (mMaxDelta - mMinDelta);
            tag.setAlpha(1 - alpha);
        }

        sortTagsByScale();
    }

    private void sortTagsByScale() {
        Collections.sort(mTagList);
    }

    private void recalculateAngle() {
        double degToRed = Math.PI / 100;
        mSinX = (float) Math.sin(mInertiaX * degToRed);
        mCosX = (float) Math.cos(mInertiaX * degToRed);
        mSinY = (float) Math.sin(mInertiaY * degToRed);
        mCosX = (float) Math.cos(mInertiaY * degToRed);
        mSinZ = (float) Math.sin(mInertiaZ * degToRed);
        mCosZ = (float) Math.cos(mInertiaZ * degToRed);
    }

    private void calculatePopularity() {
        for (int i = 0; i < mTagList.size(); i++) {
            Tag tag = mTagList.get(i);
            int popularity = tag.getPopularity();
            mMaxPopularity = Math.max(mMaxPopularity, popularity);
            mMinPopularity = Math.min(mMinPopularity, popularity);
        }

        for (Tag tag : mTagList) {
            initTag(tag);
        }
    }

    private float[] getColorFromGradient(float percentage) {
        float[] argb = new float[4];
        argb[0] = 1f;
        argb[1] = (percentage * (mDarkColor[0])) + ((1f - percentage) * (mLightColor[0]));
        argb[2] = (percentage * (mDarkColor[1])) + ((1f - percentage) * (mLightColor[1]));
        argb[3] = (percentage * (mDarkColor[2])) + ((1f - percentage) * (mLightColor[2]));
        return argb;
    }


    private void positionAll(boolean rebuild) {
        double phi;
        double theta;
        int tagListSize = mTagList.size();

        for (int i = 0; i < tagListSize; i++) {
            if (rebuild) {
                phi = Math.acos(-1.0 + (2.0 * i - 1.0) / tagListSize);
                theta = Math.sqrt(tagListSize * Math.PI) + phi;
            } else {
                phi = Math.random() * Math.PI;
                theta = Math.random() * 2 * Math.PI;
            }

            mTagList.get(i).setSpatialX((float) (mRadius * Math.cos(theta) * Math.sin(phi)));
            mTagList.get(i).setSpatialY((float) (mRadius * Math.sin(theta) * Math.sin(phi)));
            mTagList.get(i).setSpatialZ((float) (mRadius * Math.cos(phi)));
        }
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    public void setTagColorLight(float[] color) {
        this.mLightColor = color;
    }

    public void setTagColorDark(float[] color) {
        this.mDarkColor = color;
    }

    public void setInertia(float x, float y) {
        this.mInertiaX = x;
        this.mInertiaY = y;
    }
}
