package com.roller;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.roller.exalted.ExaltedListAdapter;

public class MainWindow extends Activity {
    private ExaltedListAdapter rollAdapter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
}