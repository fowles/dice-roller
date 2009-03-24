package com.roller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import com.roller.exalted.ExaltedSystem;
import com.roller.generic.GenericSystem;

public class MainWindow extends Activity {
    private DiceSystem system = null;
    
    private static final int MENU_CLEAR    = Menu.FIRST + 0;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_ABOUT    = Menu.FIRST + 2;
    
    private static final String RULE_SYSTEM_PREF = "rule-system";
    
    private static final String GENERIC_SYSTEM = "Generic";
    private static final String EXALTED_SYSTEM = "Exalted";
    private static final String[] SYSTEMS = new String[] {
        GENERIC_SYSTEM,
        EXALTED_SYSTEM,
    };
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
    }
    
    public void loadPreferences() {
        if (system != null) {
            system.saveState();
        }
        
        final String pref = getSystemPref();
        if (pref.equals(EXALTED_SYSTEM)) {
            system = new ExaltedSystem(this);
        } else if (pref.equals(GENERIC_SYSTEM)) {
            system = new GenericSystem(this);
        } else {
            system = new GenericSystem(this);
        }
        system.loadState();
    }
    
    public void putSystemPref(final String sys) {
        final SharedPreferences pref = MainWindow.this.getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        edit.putString(RULE_SYSTEM_PREF, sys);
        edit.commit();
    }
    
    public String getSystemPref() {
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getString(RULE_SYSTEM_PREF, GENERIC_SYSTEM);
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
        menu.add(0, MENU_CLEAR, 0, "Clear").setIcon(android.R.drawable.ic_menu_delete);
        menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ABOUT, 0, "About").setIcon(android.R.drawable.ic_menu_info_details);
        return res;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
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
        final String sys = getSystemPref();
        int cur = 0;
        for (; cur < SYSTEMS.length; ++cur) {
            if (sys.equals(SYSTEMS[cur])) {
                break;
            }
        }
        
        final AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle("Select System");
        db.setSingleChoiceItems(new CharSequence[] {
                GENERIC_SYSTEM,
                EXALTED_SYSTEM,
        }, cur, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int which) {
                MainWindow.this.putSystemPref(SYSTEMS[which]);
                loadPreferences();
                dialog.dismiss();
            }
        });
        db.show();
    }
}