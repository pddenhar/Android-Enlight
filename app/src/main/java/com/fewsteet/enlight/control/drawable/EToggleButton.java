package com.fewsteet.enlight.control.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.fewsteet.enlight.R;

/**
 * Created by Alex Sherman on 2/25/2017.
 */

public class EToggleButton extends EButton {
    private boolean value;
    private RectF activeBounds, inactiveBounds;
    private Paint active = new Paint();
    private Paint inactive = new Paint();
    CheckChangeListener listener;

    public EToggleButton(Context context) {
        this(context, null);
    }

    public EToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EToggleButton, defStyleAttr, 0);
        active.setColor(a.getColor(R.styleable.EToggleButton_colorToggleActive, Color.GRAY));
        inactive.setColor(a.getColor(R.styleable.EToggleButton_colorToggleInactive, Color.BLACK));
        a.recycle();
        setLayerType(LAYER_TYPE_SOFTWARE, active);
        active.setShadowLayer(shadowPadding * 0.8f, 0, 0, active.getColor());
        inactive.setShadowLayer(inSS * shadowPadding * 0.32f, 0, inSS * shadowPadding * 0.48f, R.color.black);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChecked(!isChecked());
            }
        });
        update();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        activeBounds = new RectF(shadowPadding, shadowPadding, w - shadowPadding, h - shadowPadding);
        inactiveBounds = new RectF(inSS * shadowPadding, inSS * shadowPadding, w - inSS * shadowPadding, h - inSS * shadowPadding);
        update();
    }

    public boolean isChecked() {
        return value;
    }

    private void update() {
        paint = value ? active : inactive;
        bounds = value ? activeBounds : inactiveBounds;
    }

    public void setChecked(boolean value) {
        this.value = value;
        update();
        invalidate();
        if(listener != null) {
            listener.onCheckedChanged(this, this.value);
        }
    }

    public interface CheckChangeListener {
        void onCheckedChanged(EToggleButton buttonView, boolean isChecked);
    }

    public void setOnCheckedChangeListener(CheckChangeListener listener) {
        this.listener = listener;
    }
}
