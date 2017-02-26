package com.fewsteet.enlight.control.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.fewsteet.enlight.R;

/**
 * Created by Alex Sherman on 2/25/2017.
 */

public class EToggleButton extends ToggleButton {
    private Rect bounds;
    private Paint active = new Paint();
    private Paint inactive = new Paint();
    private String label;

    public EToggleButton(Context context) {
        this(context, null);
    }

    public EToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EToggleButton, defStyleAttr, 0);
        active.setColor(a.getColor(R.styleable.EToggleButton_colorActive, Color.GRAY));
        inactive.setColor(a.getColor(R.styleable.EToggleButton_colorInactive, Color.BLACK));
    }

    public void setLabel(String text) {
        this.label = text;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int speMode = MeasureSpec.getMode(heightMeasureSpec);
        if (speMode == MeasureSpec.AT_MOST || speMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bounds = new Rect(0, 0, w, h);
    }

    @Override
    public Drawable getBackground() {
        return null;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(bounds, this.isChecked() ? active : inactive);
        //Rect text_bounds = new Rect();
        //active.getTextBounds(getLabel(), 0, getLabel().length(), text_bounds);
        //canvas.drawText(getLabel(), bounds.centerX() - text_bounds.centerX(), bounds.centerY() - text_bounds.centerY(), this.isChecked() ? inactive : active);
    }

    @Override
    protected void onDraw(Canvas canvas) { }
}
