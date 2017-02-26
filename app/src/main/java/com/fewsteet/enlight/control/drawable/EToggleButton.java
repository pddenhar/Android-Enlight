package com.fewsteet.enlight.control.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

import com.fewsteet.enlight.R;

/**
 * Created by Alex Sherman on 2/25/2017.
 */

public class EToggleButton extends View {
    private boolean value;
    private RectF activeBounds, inactiveBounds;
    private final float inSS = 0.8f;
    private Paint active = new Paint();
    private Paint inactive = new Paint();
    private String label;
    private float shadowPadding;
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
        active.setColor(a.getColor(R.styleable.EToggleButton_colorActive, Color.GRAY));
        inactive.setColor(a.getColor(R.styleable.EToggleButton_colorInactive, Color.BLACK));
        setLayerType(LAYER_TYPE_SOFTWARE, active);
        shadowPadding = 10 * Resources.getSystem().getDisplayMetrics().density;
        active.setShadowLayer(shadowPadding * 0.8f, 0, 0, active.getColor());
        inactive.setShadowLayer(inSS * shadowPadding * 0.32f, inSS * shadowPadding * 0.48f, inSS * shadowPadding * 0.48f, R.color.black);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChecked(!isChecked());
            }
        });
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
        activeBounds = new RectF(shadowPadding, shadowPadding, w - shadowPadding, h - shadowPadding);
        inactiveBounds = new RectF(inSS * shadowPadding, inSS * shadowPadding, w - inSS * shadowPadding, h - inSS * shadowPadding);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.isChecked())
            canvas.drawRoundRect(activeBounds, 0, 0, active);
        else
            canvas.drawRoundRect(inactiveBounds, 0, 0, inactive);
        //Rect text_bounds = new Rect();
        //active.getTextBounds(getLabel(), 0, getLabel().length(), text_bounds);
        //canvas.drawText(getLabel(), activeBounds.centerX() - text_bounds.centerX(), activeBounds.centerY() - text_bounds.centerY(), this.isChecked() ? inactive : active);
    }

    public boolean isChecked() {
        return value;
    }

    public void setChecked(boolean value) {
        this.value = value;
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
