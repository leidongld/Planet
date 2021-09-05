package com.openld.planet.planet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.IntDef;

import com.openld.planet.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author: lllddd
 * created on: 2021/9/4 21:30
 * description:
 */
public class TagPlanetView extends ViewGroup implements Runnable, TagsAdapter.OnDataSetChangeListener {
    private static final float TOUCH_SCALE_FACTOR = 0.0f;

    private float mSpeed = 2.0f;

    private TagPlanet mTagPlanet;

    private float mInertiaX = 0.5f;

    private float mInertiaY = 0.5f;

    private float mCenterX;
    private float mCenterY;

    private float mRadius;

    private float mRadiusPercent = 0.9f;

    private float[] mDarkColor = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
    private float[] mLightColor = new float[]{0.9412f, 0.7686f, 0.2f, 1.0f};

    public static final int MODE_DISABLE = 0;
    public static final int MODE_DECELERATE = 1;
    public static final int MODE_UNIFORM = 2;

    @IntDef({MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {

    }

    private boolean mManualScroll;

    private int mMode;

    private MarginLayoutParams mMarginLayoutParams;

    private int mMinSize;

    private boolean mIsOnTouch = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TagsAdapter mAdapter = new NOPTagsAdapter();

    private OnTagClickListener mOnTagClickListener;


    public TagPlanetView(Context context) {
        this(context, null);
        init(context, null);
    }

    public TagPlanetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public TagPlanetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public TagPlanetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setFocusableInTouchMode(true);

        mTagPlanet = new TagPlanet();

        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TagPlanetView);

            String m = typedArray.getString(R.styleable.TagPlanetView_autoScrollMode);
            mMode = Integer.parseInt(m);

            setManualScroll(typedArray.getBoolean(R.styleable.TagPlanetView_manualScroll, true));
            mInertiaX = typedArray.getFloat(R.styleable.TagPlanetView_startAngleX, 0.5f);
            mInertiaX = typedArray.getFloat(R.styleable.TagPlanetView_startAngleY, 0.5f);

            int lightColor = typedArray.getColor(R.styleable.TagPlanetView_lightColor, Color.WHITE);
            setLightColor(lightColor);

            int darkColor = typedArray.getColor(R.styleable.TagPlanetView_lightColor, Color.WHITE);
            setDarkColor(darkColor);

            float percent = typedArray.getFloat(R.styleable.TagPlanetView_radiusPercent, mRadiusPercent);
            setRadiusPercent(percent);

            float speed = typedArray.getFloat(R.styleable.TagPlanetView_scrollSpeed, 2f);
            setScrollSpeed(speed);

            typedArray.recycle();
        }

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();

        point.x = wm.getDefaultDisplay().getWidth();
        point.y = wm.getDefaultDisplay().getHeight();

        int screenWidth = point.x;
        int screenHeight = point.y;
        mMinSize = Math.min(screenHeight, screenWidth);
    }

    public void setAutoScrollMode(@Mode int mode) {
        this.mMode = mode;
    }

    @Mode
    public int getAutoScrollMode() {
        return this.mMode;
    }

    public void setAdapter(TagsAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setOnDataSetChangeListener(this);
        onDataSetChanged();
    }

    private void initFromAdapter() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterX = (getRight() - getLeft()) >> 1;
                mCenterY = (getBottom() - getTop()) >> 1;
                mRadius = Math.min(mCenterX * mRadiusPercent, mCenterY * mRadiusPercent);
                mTagPlanet.setRadius((int) mRadius);

                mTagPlanet.setTagColorLight(mLightColor);
                mTagPlanet.setTagColorDark(mDarkColor);

                mTagPlanet.clear();
                removeAllViews();
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    Tag tag = new Tag(mAdapter.getPopularity(i));
                    View view = mAdapter.getView(getContext(), i, TagPlanetView.this);
                    tag.bindView(view);
                    mTagPlanet.addTag(tag);
                    addListener(view, i);
                }

                mTagPlanet.setInertia(mInertiaX, mInertiaY);
                mTagPlanet.create(true);

                resetChildren();
            }
        }, 0);
    }

    private void resetChildren() {
        removeAllViews();

        for (Tag tag : mTagPlanet.getTagList()) {
            addView(tag.getView());
        }
    }

    private void addListener(View view, int position) {
        if (mOnTagClickListener != null) {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTagClickListener.onTagClick(TagPlanetView.this, v, position);
                }
            });
            Log.e("TagCloudView", "Build version is less than 15, the OnClickListener may be overwritten.");
        }
    }

    private void setScrollSpeed(float speed) {
        this.mSpeed = speed;
    }

    private void setRadiusPercent(float percent) {
        if (percent > 1f || percent < 0f) {
            throw new IllegalArgumentException("Percent value not in range 0 to 1.");
        } else {
            mRadiusPercent = percent;
            onDataSetChanged();
        }
    }

    private void setDarkColor(int darkColor) {
        float[] argb = new float[4];
        argb[0] = Color.alpha(darkColor) / 1.0f / 0xff;
        argb[1] = Color.red(darkColor) / 1.0f / 0xff;
        argb[2] = Color.green(darkColor) / 1.0f / 0xff;
        argb[3] = Color.blue(darkColor) / 1.0f / 0xff;

        mLightColor = argb.clone();
        onDataSetChanged();
    }

    private void setLightColor(int lightColor) {
        float[] argb = new float[4];
        argb[0] = Color.alpha(lightColor) / 1.0f / 0xff;
        argb[1] = Color.red(lightColor) / 1.0f / 0xff;
        argb[2] = Color.green(lightColor) / 1.0f / 0xff;
        argb[3] = Color.blue(lightColor) / 1.0f / 0xff;

        mLightColor = argb.clone();
        onDataSetChanged();
    }

    public void setManualScroll(boolean manualScroll) {
        this.mManualScroll = manualScroll;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mMarginLayoutParams == null) {
            mMarginLayoutParams = (MarginLayoutParams) getLayoutParams();
        }

        int dimensionX = widthMode == MeasureSpec.EXACTLY ? contentWidth : mMinSize - mMarginLayoutParams.leftMargin - mMarginLayoutParams.rightMargin;
        int dimensionY = heightMode == MeasureSpec.EXACTLY ? contentHeight : mMinSize - mMarginLayoutParams.topMargin - mMarginLayoutParams.bottomMargin;

        setMeasuredDimension(dimensionX, dimensionY);

        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Tag tag = mTagPlanet.get(i);

            if (child != null && child.getVisibility() != GONE) {
                mAdapter.onThemeColorChanged(child, tag.getColor(), tag.getAlpha());
                child.setScaleX(tag.getScale());
                child.setScaleY(tag.getScale());

                int left, top;

                left = (int) ((mCenterX + tag.getFlatX()) - child.getMeasuredWidth() / 2);
                top = (int) ((mCenterY + tag.getFlatY()) - child.getMeasuredHeight() / 2);

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }

    public void reset() {
        mTagPlanet.reset();
        resetChildren();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mManualScroll) {
            handleTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mManualScroll) {
            handleTouchEvent(event);
        }
        return true;
    }

    private float downX, downY;

    private void handleTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                mIsOnTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                if (isValidMove(dx, dy)) {
                    mInertiaX = (dy / mRadius) * mSpeed * TOUCH_SCALE_FACTOR;
                    mInertiaY = (-dx / mRadius) * mSpeed * TOUCH_SCALE_FACTOR;
                    processTouch();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsOnTouch = false;
                break;
            default:
                break;
        }
    }

    private void processTouch() {
        if (mTagPlanet != null) {
            mTagPlanet.setInertia(mInertiaX, mInertiaY);
            mTagPlanet.update();
        }
        resetChildren();
    }

    private boolean isValidMove(float dx, float dy) {
        int minDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        return Math.abs(dx) > minDistance || Math.abs(dy) > minDistance;
    }

    @Override
    public void onDataSetChanged() {
        initFromAdapter();
    }

    @Override
    public void run() {
        if (!mIsOnTouch && mMode != MODE_DISABLE) {
            if (mMode == MODE_DECELERATE) {
                if (mInertiaX > 0.04f) {
                    mInertiaX -= 0.02f;
                }
                if (mInertiaY > 0.04f) {
                    mInertiaY -= 0.02f;
                }
                if (mInertiaX < -0.04f) {
                    mInertiaX += 0.02f;
                }
                if (mInertiaY < -0.04f) {
                    mInertiaY += 0.02f;
                }
            }
            processTouch();
        }

        mHandler.postDelayed(this, 16);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.mOnTagClickListener = listener;
    }

    public interface OnTagClickListener {
        void onTagClick(ViewGroup parent, View view, int position);
    }
}
