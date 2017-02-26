package com.fewsteet.enlight.control.drawable;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.fewsteet.enlight.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class ColorSeekBar extends View {
    private int[] mColorSeeds = new int[]{0xFF000000, 0xFF9900FF, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF, 0xFFFF0000, 0xFFFF00FF, 0xFFFF6600, 0xFFFFFF00, 0xFFFFFFFF, 0xFF000000};
    Paint colorPaint, black;
    private float startDrag = 0.0f;
    private OnColorChangeListener mOnColorChangeLister;
    private Context mContext;
    private Bitmap mTransparentBitmap;
    private boolean mMovingColorBar;
    private Rect mColorRect;
    private int mThumbHeight = 20;
    private int mBarHeight = 2;
    private Paint mColorRectPaint;
    private int mBarWidth;
    private int mMaxPosition;
    private int mColorBarPosition;
    private float mThumbRadius;
    private List<Integer> mColors = new ArrayList<>();
    private int mColorsToInvoke = -1;
    private boolean mInit = false;
    public ColorSeekBar(Context context) {
        super(context);
        applyStyle(context, null, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyStyle(context, attrs, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyStyle(context, attrs, defStyleAttr, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int speMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(mThumbHeight + mBarHeight);
        if (speMode == MeasureSpec.AT_MOST || speMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(widthMeasureSpec, resolveSize(h, heightMeasureSpec));
        }
    }

    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        colorPaint = new Paint();
        black = new Paint();
        black.setARGB(255,0,0,0);
        mContext = context;
        //get attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorSeekBar, defStyleAttr, defStyleRes);
        int colorsId = a.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0);
        mMaxPosition = a.getInteger(R.styleable.ColorSeekBar_maxPosition, 100);
        mColorBarPosition = a.getInteger(R.styleable.ColorSeekBar_colorBarPosition, 0);
        int mBackgroundColor = a.getColor(R.styleable.ColorSeekBar_bgColor, Color.TRANSPARENT);
        mBarHeight = (int) a.getDimension(R.styleable.ColorSeekBar_barHeight, (float) dp2px(2));
        mThumbHeight = (int) a.getDimension(R.styleable.ColorSeekBar_thumbHeight, (float) dp2px(30));
        a.recycle();

        if (colorsId != 0) mColorSeeds = getColorsById(colorsId);

        setBackgroundColor(mBackgroundColor);

    }

    private int[] getColorsById(int id) {
        if (isInEditMode()) {
            String[] s = mContext.getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int j = 0; j < s.length; j++) {
                colors[j] = Color.parseColor(s[j]);
            }
            return colors;
        } else {
            TypedArray typedArray = mContext.getResources().obtainTypedArray(id);
            int[] colors = new int[typedArray.length()];
            for (int j = 0; j < typedArray.length(); j++) {
                colors[j] = typedArray.getColor(j, Color.BLACK);
            }
            typedArray.recycle();
            return colors;
        }
    }

    private void init() {
        //init size
        mThumbRadius = mThumbHeight / 2;
        int mPaddingSize = (int) mThumbRadius;

        //init l r t b
        int realLeft = getPaddingLeft() + mPaddingSize;
        int realRight = getWidth() - getPaddingRight() - mPaddingSize;
        int realTop = getHeight() / 2 - mBarHeight / 2;

        mBarWidth = realRight - realLeft;

        //init rect
        mColorRect = new Rect(realLeft, realTop, realRight, realTop + mBarHeight);

        //init paint
        LinearGradient mColorGradient = new LinearGradient(0, 0, mColorRect.width(), 0, mColorSeeds, null, Shader.TileMode.MIRROR);
        mColorRectPaint = new Paint();
        mColorRectPaint.setShader(mColorGradient);
        mColorRectPaint.setAntiAlias(true);
        cacheColors();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTransparentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        mTransparentBitmap.eraseColor(Color.TRANSPARENT);
        init();
        mInit = true;
        if(mColorsToInvoke != -1) setColor(mColorsToInvoke);
    }

    private void cacheColors() {
        //if the view's size hasn't been initialized. do not cache.
        if (mBarWidth < 1) return;
        mColors.clear();
        for (int i = 0; i <= mMaxPosition; i++) {
            mColors.add(pickColor(i));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float colorPosition = (float) mColorBarPosition / mMaxPosition * mBarWidth;

        colorPaint.setAntiAlias(true);
        int color = getColor();
        colorPaint.setColor(color);
        //clear
        canvas.drawBitmap(mTransparentBitmap, 0, 0, null);

        //draw color bar
        canvas.drawRect(mColorRect, mColorRectPaint);

        //draw color bar thumb
        float thumbX = colorPosition + mColorRect.left;
        float thumbY = mColorRect.top + mColorRect.height() / 2;

        canvas.drawCircle(thumbX, thumbY, mBarHeight + 7, black);
        canvas.drawCircle(thumbX, thumbY, mBarHeight + 5, colorPaint);

        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDrag = 1.0f * (x - mColorRect.left) / mBarWidth * mMaxPosition;
                if (isOnBar(mColorRect, x, y)) {
                    mMovingColorBar = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mMovingColorBar) {
                    float value = startDrag + (1.0f * (x - mColorRect.left) / mBarWidth * mMaxPosition - startDrag)
                            / Math.max(1, (abs(y - mColorRect.centerY()) - 100) / 100.0f);
                    mColorBarPosition = (int) value;
                    if (mColorBarPosition < 0) mColorBarPosition = 0;
                    if (mColorBarPosition > mMaxPosition) mColorBarPosition = mMaxPosition;
                }
                if (mOnColorChangeLister != null && (mMovingColorBar))
                    mOnColorChangeLister.onColorChangeListener(mColorBarPosition, getColor());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mMovingColorBar = false;
                break;
        }
        return true;
    }


    private boolean isOnBar(Rect r, float x, float y) {
        return r.left - mThumbRadius < x && x < r.right + mThumbRadius && r.top - mThumbRadius < y && y < r.bottom + mThumbRadius;
    }

    private int pickColor(int value) {
        return pickColor((float) value / mMaxPosition * mBarWidth);
    }

    private int pickColor(float position) {
        float unit = position / mBarWidth;
        if (unit <= 0.0)
            return mColorSeeds[0];

        if (unit >= 1)
            return mColorSeeds[mColorSeeds.length - 1];

        float colorPosition = unit * (mColorSeeds.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        int c0 = mColorSeeds[i];
        int c1 = mColorSeeds[i + 1];
//         mAlpha = mix(Color.alpha(c0), Color.alpha(c1), colorPosition);
        int mRed = mix(Color.red(c0), Color.red(c1), colorPosition);
        int mGreen = mix(Color.green(c0), Color.green(c1), colorPosition);
        int mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition);
        return Color.rgb(mRed, mGreen, mBlue);
    }

    private int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }

    public int getColor() {
        if (mColorBarPosition >= mColors.size()) {
            return pickColor(mColorBarPosition);
        }
        return mColors.get(mColorBarPosition);
    }

    public interface OnColorChangeListener {
        /**
         * @param colorBarPosition between 0-maxValue
         * @param color         return the color contains alpha value whether showAlphaBar is true or without alpha value
         */
        void onColorChangeListener(int colorBarPosition, int color);
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeLister = onColorChangeListener;
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setColorBarPosition(int value) {
        this.mColorBarPosition = value;
        mColorBarPosition = mColorBarPosition > mMaxPosition ? mMaxPosition : mColorBarPosition;
        mColorBarPosition = mColorBarPosition < 0 ? 0 : mColorBarPosition;
        invalidate();
        if (mOnColorChangeLister != null)
            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, getColor());
    }

    public void setColor(int color) {
        int withoutAlphaColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));

        if (mInit) {
            int value = mColors.indexOf(withoutAlphaColor);
            setColorBarPosition(value);
        } else {
            mColorsToInvoke = color;
        }
    }
}