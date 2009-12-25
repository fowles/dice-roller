package com.roller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NumberSpinner extends LinearLayout implements OnClickListener,
		OnKeyListener {
	private static final String TAG = "com.roller.NumberSpinner";
	private int minimum;
	private int maximum;
	private final int inital;

	private final ImageView minusView;
	private final ImageView plusView;
	private final EditText numberEdit;

	public NumberSpinner(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.NumberSpinner);
		minimum = a
				.getInt(R.styleable.NumberSpinner_minimum, Integer.MIN_VALUE);
		maximum = a
				.getInt(R.styleable.NumberSpinner_maximum, Integer.MAX_VALUE);
		int inital = a.getInt(R.styleable.NumberSpinner_inital, 0);
		if (inital < minimum)
			inital = minimum;
		this.inital = inital;
		a.recycle();

		final Bitmap plusBMP = BitmapFactory.decodeResource(context
				.getResources(), android.R.drawable.ic_media_play);
		final Matrix mirrorTX = new Matrix();
		mirrorTX.preScale(-1.0f, 1.0f);
		final Bitmap minusBMP = Bitmap.createBitmap(plusBMP, 0, 0, plusBMP
				.getWidth(), plusBMP.getHeight(), mirrorTX, false);

		minusView = new ImageView(context, attrs);
		minusView.setImageBitmap(minusBMP);
		minusView.setOnClickListener(this);

		plusView = new ImageView(context, attrs);
		plusView.setImageBitmap(plusBMP);
		plusView.setOnClickListener(this);

		numberEdit = new EditText(context, attrs);
		numberEdit.setKeyListener(new DigitsKeyListener(minimum < 0, false));
		numberEdit.setText(Integer.toString(this.inital));
		numberEdit.setSelectAllOnFocus(true);

		addView(minusView);
		addView(numberEdit);
		addView(plusView);

		numberEdit.setOnKeyListener(this);
	}

	public int getValue() {
		try {
			int value = Integer.parseInt(numberEdit.getText().toString());
			if (value < minimum)
				return minimum;
			if (value > maximum)
				return maximum;
			return value;
		} catch (final NumberFormatException nfe) {
			return inital;
		}
	}

	public void setValue(int i) {
		if (i < minimum) {
			i = minimum;
		}
		if (i > maximum) {
			i = maximum;
		}
		numberEdit.setText(Integer.toString(i));
	}

	@Override
	public void setOnClickListener(final OnClickListener l) {
		numberEdit.setOnClickListener(l);
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

	public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
		if (event.getAction() != KeyEvent.ACTION_DOWN) {
			return false;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (numberEdit.getSelectionStart() <= 0) {
				setValue(getValue() - 1);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (numberEdit.getSelectionEnd() >= numberEdit.length()) {
				setValue(getValue() + 1);
				numberEdit.setSelection(numberEdit.length());
				return true;
			}
			break;
		}
		return false;
	}
}
