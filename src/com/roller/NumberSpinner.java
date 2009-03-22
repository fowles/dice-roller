package com.roller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NumberSpinner extends LinearLayout implements OnClickListener {
    private static final String TAG = "com.roller.NumberSpinner";
    
    private final ImageView minusView;
    private final ImageView plusView;
    private final EditText numberEdit;

    public NumberSpinner(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        
        final Bitmap plusBMP = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_media_play);
        final Matrix mirrorTX = new Matrix();
        mirrorTX.preScale(-1.0f, 1.0f);
        final Bitmap minusBMP = Bitmap.createBitmap(plusBMP, 0, 0, plusBMP.getWidth(), plusBMP.getHeight(), mirrorTX, false);
        
        minusView = new ImageView(context, attrs);
        minusView.setImageBitmap(minusBMP);
        minusView.setOnClickListener(this);
        
        plusView = new ImageView(context, attrs);
        plusView.setImageBitmap(plusBMP);
        plusView.setOnClickListener(this);
        
        numberEdit = new EditText(context, attrs);
        numberEdit.setKeyListener(new DigitsKeyListener());
        numberEdit.setText("8");
        
        
        addView(minusView);
        addView(numberEdit);
        addView(plusView);
    }
    
    public int getValue() {
        try {
            return Integer.parseInt(numberEdit.getText().toString());
        } catch (final NumberFormatException nfe) {
            return 1;
        }
    }
    
    public void setValue(int i) {
        if (i < 1) { i = 1; }
        numberEdit.setText(Integer.toString(i));
    }

    public void onClick(final View v) {
        if (v == minusView) {
            setValue(getValue() - 1);
        } else if (v == plusView) {
            setValue(getValue() + 1);
        } else {
            Log.w(TAG, "Unknown view: " + v);
        }
    }
}
