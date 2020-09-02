package com.jd.jrapp.other.pet.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class GradientColorTextView extends TextView {

    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private int mViewWidth = 0;
    private Rect mTextBound = new Rect();
    private int[] color = {0xFFA47048, 0xFF5E3617}; // 渐变色范围
    private ValueAnimator leftAnimator,rightAnimator;
    private boolean isAnimation = false; // 设置渐变色动态变化

    public GradientColorTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        invalidate();
    }

    public void setIsAnimation(boolean isAnimation){
        this.isAnimation = isAnimation;
    }

    public void setColor(int[] color){
        this.color = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mViewWidth = getMeasuredWidth();
        mPaint = getPaint();
        String mTipText = getText().toString();
        mPaint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
        leftAnimator = ValueAnimator.ofInt(0, color[0]);
        rightAnimator = ValueAnimator.ofInt(0, color[1]);
        // 自动更新view
        invalidate();
        // 设置是否有动画
        if (isAnimation){
            color[0] = color[0]-100;
            color[1] = color[1]-100;
        }
        // 设置颜色渐变
        mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                color,
                null, Shader.TileMode.REPEAT);
//        System.out.println(color.toString());
        mPaint.setShader(mLinearGradient);
        canvas.drawText(mTipText, getMeasuredWidth() / 2 - mTextBound.width() / 2, getMeasuredHeight() / 2 + mTextBound.height() / 2, mPaint);
    }
}

