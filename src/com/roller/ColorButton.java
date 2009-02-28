/*
 * Copyright (C) 2008 Matt Fowles
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Modified from com.android.calculator2.ColorButton
 */
class ColorButton extends Button implements OnClickListener {
    int CLICK_FEEDBACK_COLOR;
    static final int CLICK_FEEDBACK_INTERVAL = 10;
    static final int CLICK_FEEDBACK_DURATION = 350;
    
    Drawable mButtonBackground;
    Drawable mButton;
    float mTextX;
    float mTextY;
    long mAnimStart;
    OnClickListener mListener;
    
    public ColorButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
        super.setOnClickListener(this);
    }
     
    @Override
    public void setOnClickListener(final OnClickListener l) {
        mListener = l;
    }

    public void onClick(final View view) {
        animateClickFeedback();
        if (mListener != null) {
            mListener.onClick(this);
        }
    }

    private void init() {
        setBackgroundDrawable(null);

        final Resources res = getResources();

        mButtonBackground = res.getDrawable(R.drawable.button_bg);
        mButton = res.getDrawable(R.drawable.button);
        CLICK_FEEDBACK_COLOR = res.getColor(R.color.magic_flame);
        getPaint().setColor(res.getColor(R.color.button_text));
        
        mAnimStart = -1;
    }


    @Override 
    public void onSizeChanged(final int w, final int h, final int oldW, final int oldH) {
        final int selfW = mButton.getIntrinsicWidth();
        final int selfH = mButton.getIntrinsicHeight();
        final int marginX = (w - selfW) / 2;
        final int marginY = (h - selfH) / 2;
        mButtonBackground.setBounds(marginX, marginY, marginX + selfW, marginY + selfH);
        mButton.setBounds(marginX, marginY, marginX + selfW, marginY + selfH);
        measureText();
    }

    private void measureText() {
        final Paint paint = getPaint();
        mTextX = (getWidth() - paint.measureText(getText().toString())) / 2;
        mTextY = (getHeight() - paint.ascent() - paint.descent()) / 2;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        measureText();
    }

    private void drawMagicFlame(final int duration, final Canvas canvas) {
        final int alpha = 255 - 255 * duration / CLICK_FEEDBACK_DURATION;
        final int color = CLICK_FEEDBACK_COLOR | (alpha << 24);
        mButtonBackground.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        
        final int cx = getWidth() / 2;
        final int cy = getHeight() / 2;
        final float angle = 250.0f * duration / CLICK_FEEDBACK_DURATION;
        canvas.rotate(angle, cx, cy);
        mButtonBackground.draw(canvas);
        canvas.rotate(-angle, cx, cy);
    }

    @Override
    public void onDraw(final Canvas canvas) {
        if (mAnimStart != -1) {
            final int animDuration = (int) (System.currentTimeMillis() - mAnimStart);
            
            if (animDuration >= CLICK_FEEDBACK_DURATION) {
                mButtonBackground.clearColorFilter();
                mAnimStart = -1;
            } else {
                drawMagicFlame(animDuration, canvas);
                postInvalidateDelayed(CLICK_FEEDBACK_INTERVAL);
            }
        } else if (isPressed()) {
            drawMagicFlame(0, canvas);
        }
        
        mButton.draw(canvas);
        
        final CharSequence text = getText();
        canvas.drawText(text, 0, text.length(), mTextX, mTextY, getPaint());
    }

    public void animateClickFeedback() {
        mAnimStart = System.currentTimeMillis();
        invalidate();        
    } 
    
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final int a = event.getAction();
        if (a == MotionEvent.ACTION_DOWN 
                || a == MotionEvent.ACTION_CANCEL
                || a == MotionEvent.ACTION_UP) {
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
