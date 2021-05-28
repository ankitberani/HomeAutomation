package com.wekex.apps.homeautomation.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.Utils;
import com.flask.colorpicker.builder.PaintBuilder;
import com.flask.colorpicker.slider.AbsCustomSlider;
import com.flask.colorpicker.slider.OnValueChangedListener;

public class GradienSeekBar extends AbsCustomSlider {
    private Paint alphaPatternPaint = PaintBuilder.newPaint().build();
    private Paint barPaint = PaintBuilder.newPaint().build();
    private Paint clearingStroke = PaintBuilder.newPaint().color(-1).xPerMode(Mode.CLEAR).build();
    public int color;
    private ColorPickerView colorPicker;
    private float[] hsv = new float[3];
    private float[] hsvClone;
    private OnAlphaChangeListener onAlphaChangeListener;
    private Paint solid = PaintBuilder.newPaint().build();

    public interface OnAlphaChangeListener {
        void onAlphaColorChnage(int i);

        void onAlphaColorChnaged(boolean z);
    }

    public GradienSeekBar(Context context) {
        super(context);
    }

    public GradienSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradienSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void createBitmaps() {
        super.createBitmaps();
        this.alphaPatternPaint.setShader(PaintBuilder.createAlphaPatternShader(this.barHeight / 2));
    }

    /* access modifiers changed from: protected */
    public void drawBar(Canvas barCanvas) {
        int width = barCanvas.getWidth();
        int height = barCanvas.getHeight();
        barCanvas.drawRect(0.0f, 0.0f, (float) width, (float) height, this.alphaPatternPaint);
        int l = Math.max(2, width / 256);
        for (int x = 0; x <= width; x += l) {
            float alpha = ((float) x) / ((float) (width - 1));
            this.barPaint.setColor(this.color);
            this.barPaint.setAlpha(Math.round(255.0f * alpha));
            barCanvas.drawRect((float) x, 0.0f, (float) (x + l), (float) height, this.barPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void onValueChanged(float value) {
        ColorPickerView colorPickerView = this.colorPicker;
        if (colorPickerView != null) {
            colorPickerView.setAlphaValue(value);
        }
        int myColor = Color.rgb((int) (((float) Color.red(this.color)) * value), (int) (((float) Color.green(this.color)) * value), (int) (((float) Color.blue(this.color)) * value));
        this.onAlphaChangeListener.onAlphaColorChnage(myColor);
        StringBuilder sb = new StringBuilder();
        sb.append(myColor);
        sb.append(" onValueChanged: ");
        Log.d(sb.toString(), String.valueOf(value));
    }

    public float[] getHsvWithLightness(float lightness) {
        if (this.hsvClone == null) {
            this.hsvClone = (float[]) this.hsv.clone();
        }
        float[] fArr = this.hsvClone;
        float[] fArr2 = this.hsv;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[2] = lightness;
        return fArr;
    }

    /* access modifiers changed from: protected */
    public void drawHandle(Canvas canvas, float x, float y) {
        this.solid.setColor(this.color);
        this.solid.setAlpha(Math.round(this.value * 255.0f));
        canvas.drawCircle(x, y, (float) this.handleRadius, this.clearingStroke);
        if (this.value < 1.0f) {
            canvas.drawCircle(x, y, ((float) this.handleRadius) * 0.75f, this.alphaPatternPaint);
        }
        canvas.drawCircle(x, y, ((float) this.handleRadius) * 0.75f, this.solid);
    }

    public void setColorPicker(ColorPickerView colorPicker2) {
        this.colorPicker = colorPicker2;
    }

    public void setColor(int color2) {
        this.color = color2;
        this.value = Utils.getAlphaPercent(color2);
        if (this.bar != null) {
            updateBar();
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1) {
            this.onAlphaChangeListener.onAlphaColorChnaged(true);
        }
        return super.onTouchEvent(event);
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        Log.d("setOnValutener: ", "valuechange");
        super.setOnValueChangedListener(onValueChangedListener);
    }

    public void setOnALphaChangeListener(OnAlphaChangeListener onAlphaChange) {
        this.onAlphaChangeListener = onAlphaChange;
    }
}
