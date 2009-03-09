package com.roller;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
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
        registerForContextMenu(findViewById(R.main.list));
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
    
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        rollAdapter.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (rollAdapter.onContextItemSelected(item)) {
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }


    public void setResult(final CharSequence result) {
        final TextView successText = (TextView) findViewById(R.main.success_label);
        successText.setText(result);
    }
}