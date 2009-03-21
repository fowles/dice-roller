package com.roller;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.roller.exalted.ExaltedSystem;

public class MainWindow extends Activity {
    private DiceSystem system = null;
    
    private static final int MENU_ADD_ITEM = Menu.FIRST + 0;
    private static final int MENU_CLEAR    = Menu.FIRST + 1;
    private static final int MENU_SETTINGS = Menu.FIRST + 2;
    private static final int MENU_ABOUT    = Menu.FIRST + 3;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerForContextMenu(findViewById(R.main.list));

        system = new ExaltedSystem(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        system.loadState();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        system.saveState();
    }
    
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        system.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (system.onContextItemSelected(item)) {
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
        case MENU_ADD_ITEM: system.showAddDialog(); return true;
        case MENU_CLEAR: system.clearState(); return true;
        
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