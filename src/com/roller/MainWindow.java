package com.roller;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.roller.exalted.ExaltedListAdapter;

public class MainWindow extends ListActivity {
    private ExaltedListAdapter rollAdapter = null;
    
    private static final int MENU_ADD_ITEM = Menu.FIRST + 0;
    private static final int MENU_CLEAR    = Menu.FIRST + 1;
    private static final int MENU_SETTINGS = Menu.FIRST + 2;
    private static final int MENU_ABOUT    = Menu.FIRST + 3;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerForContextMenu(getListView());

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
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final boolean res = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ADD_ITEM, 0, "Add").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_CLEAR, 0, "Clear").setIcon(android.R.drawable.ic_menu_delete);
        //menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
        return res;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
        case MENU_ADD_ITEM: rollAdapter.showAddDialog(); return true;
        case MENU_CLEAR: rollAdapter.clear(); return true;
        
        case MENU_ABOUT:
            final Dialog d = new Dialog(this);
            d.setTitle("About");
            d.setContentView(R.layout.about);
            d.show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}