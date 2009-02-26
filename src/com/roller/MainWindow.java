package com.roller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roller.exalted.ExaltedListAdapter;

public class MainWindow extends Activity implements View.OnClickListener {
    private ExaltedListAdapter rollAdapter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final View addDiceButton = findViewById(R.main.add_dice);
        addDiceButton.setOnClickListener(this);

        rollAdapter = new ExaltedListAdapter(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        rollAdapter.loadList();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        rollAdapter.saveList();
    }

    public void onClick(final View v) {
        rollAdapter.showAddDialog();
    }

    public void setResult(final CharSequence result) {
        final TextView successText = (TextView) findViewById(R.main.success_label);
        successText.setText(result);
    }
}