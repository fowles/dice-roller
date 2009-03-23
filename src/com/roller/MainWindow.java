package com.roller;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

import com.roller.exalted.ExaltedSystem;
import com.roller.generic.GenericSystem;

public class MainWindow extends Activity {
    private DiceSystem system = null;
    
    private static final int MENU_ADD_ITEM = Menu.FIRST + 0;
    private static final int MENU_CLEAR    = Menu.FIRST + 1;
    private static final int MENU_SETTINGS = Menu.FIRST + 2;
    private static final int MENU_ABOUT    = Menu.FIRST + 3;
    
    private static final String RULE_SYSTEM_PREF = "rule-system";
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
    }
    
    public void loadPreferences() {
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        switch (prefs.getInt(RULE_SYSTEM_PREF, -1)) {
        case R.preferences.exalted:
            system = new ExaltedSystem(this);
            break;
         
        case R.preferences.generic:
        default:
            system = new GenericSystem(this);
            break;
        }
        system.loadState();
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
        menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
        return res;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
        case MENU_ADD_ITEM: system.showAddDialog(); return true;
        case MENU_CLEAR: system.clearState(); return true;
        case MENU_SETTINGS: showPreferences(); return true;
        case MENU_ABOUT: showAbout(); return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void showAbout() {
        final Dialog d = new Dialog(this);
        d.setTitle("About");
        d.setContentView(R.layout.about);
        d.show();
    }
    
    public void showPreferences() {
        final Dialog d = new Dialog(this);
        d.setTitle("Select System");
        d.setContentView(R.layout.preferences);

        final Button ok = (Button) d.findViewById(R.preferences.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final RadioGroup system = (RadioGroup) d.findViewById(R.preferences.system_group);
                final SharedPreferences pref = MainWindow.this.getPreferences(MODE_PRIVATE);
                final SharedPreferences.Editor edit = pref.edit();
                edit.putInt(RULE_SYSTEM_PREF, system.getCheckedRadioButtonId());
                edit.commit();
                d.dismiss();
                loadPreferences();
            }
        });

        final Button cancel = (Button) d.findViewById(R.preferences.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                d.dismiss();
            }
        });

        d.show();    
    }
}