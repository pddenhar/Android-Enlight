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

import java.util.ArrayList;

/**
 * Created by Alex Sherman on 2/25/2017.
 */

public class EButton extends View {
    protected RectF bounds;
    private float textSize;
    protected final float inSS = 0.8f;
    protected Paint paint = new Paint();
    private Paint textPaint = new Paint();
    private String text;
    protected float shadowPadding;

    public EButton(Context context) {
        this(context, null);
    }

    public EButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EButton, defStyleAttr, 0);
        paint.setColor(a.getColor(R.styleable.EButton_colorButton, Color.GRAY));
        shadowPadding = 10 * Resources.getSystem().getDisplayMetrics().density;
        paint.setShadowLayer(inSS * shadowPadding * 0.32f, 0, inSS * shadowPadding * 0.48f, R.color.black);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
        textSize = 20 * Resources.getSystem().getDisplayMetrics().density;
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(5 * Resources.getSystem().getDisplayMetrics().density, 0, 0, R.color.black);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
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
        bounds = new RectF(shadowPadding, shadowPadding, w - shadowPadding, h - shadowPadding);
    }

    protected void drawText(Canvas canvas, String text) {
        if(text == null) return;
        String remainingText = text.toUpperCase();
        ArrayList<String> toDraw = new ArrayList<>();
        while(remainingText.length() > 0) {
            int numOfChars = textPaint.breakText(remainingText, true, bounds.width(),null);
            for(int i = numOfChars - 1; i >= 0; i--) {
                if(remainingText.charAt(i) == ' ') {
                    numOfChars = i;
                    remainingText = remainingText.substring(0, numOfChars) + remainingText.substring(numOfChars+1);
                    break;
                }
            }
            if(numOfChars == 0)
                break;
            toDraw.add(remainingText.substring(0, numOfChars));
            remainingText = remainingText.substring(numOfChars);
        }
        int currentHeight = (int)(0.5f * (2-toDraw.size()) * textSize);
        for(String cutText : toDraw) {
            canvas.drawText(cutText, 0, cutText.length(), bounds.centerX(), bounds.centerY() + currentHeight, textPaint);
            currentHeight += textSize;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(bounds, 0, 0, paint);
        drawText(canvas, getText());
    }
}
