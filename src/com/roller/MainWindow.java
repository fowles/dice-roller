package com.roller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.roller.exalted.ExaltedSystem;
import com.roller.generic.GenericSystem;

public class MainWindow extends Activity {
    private static final String TAG = "com.roller.MainWindow";
    
    private DiceSystem system = null;
    private boolean keepAwake = false;
    private PowerManager.WakeLock wakeLock = null;

    
    private static final int MENU_CLEAR    = Menu.FIRST + 0;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_ABOUT    = Menu.FIRST + 2;
    
    private static final String RULE_SYSTEM_PREF = "rule-system";
    private static final String KEEP_AWAKE_PREF = "keep-awake";
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        wakeLock.setReferenceCounted(false);
    }
    
    public void load() {
        if (system != null) {
            system.saveState();
        }
        
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        keepAwake = prefs.getBoolean(KEEP_AWAKE_PREF, false);
        
        final String sys = prefs.getString(RULE_SYSTEM_PREF, "");
        changeSystem(sys);
    }
    
    public void changeSystem(final String sys) {
        if (sys.equals(getString(ExaltedSystem.SYSTEM_ID))) {
            system = new ExaltedSystem(this);
        } else {
            system = new GenericSystem(this);
        }
        system.loadState();
    }
    
    public void save() {
        final SharedPreferences pref = MainWindow.this.getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        edit.putString(RULE_SYSTEM_PREF, getString(system.getSystemId()));
        edit.putBoolean(KEEP_AWAKE_PREF, keepAwake);
        edit.commit();
        
        system.saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        load();
        if (keepAwake) {
            wakeLock.acquire();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        save();
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
        case MENU_SETTINGS: showDialog(R.layout.preferences); return true;
        case MENU_ABOUT: showDialog(R.layout.about); return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onPrepareDialog(final int id, final Dialog d) {
        super.onPrepareDialog(id, d);
        if (id == R.layout.preferences) {
            final RadioGroup sysGroup = (RadioGroup) d.findViewById(R.preferences.system);
            final int curSystem = system.getSystemId();
            switch (curSystem) {
            case ExaltedSystem.SYSTEM_ID:
                sysGroup.check(R.preferences.exalted);
                break;
            case GenericSystem.SYSTEM_ID:
            default:
                sysGroup.check(R.preferences.generic);
                break;
            }
            final CheckBox wakeButton = (CheckBox) d.findViewById(R.preferences.keep_awake);
            wakeButton.setChecked(keepAwake);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(final int id) {
        final Dialog d;
        switch (id) {
        case R.layout.preferences:
            d = new Dialog(this);
            d.setTitle("Preferences");
            d.setContentView(id);
            
            final CheckBox wakeButton = (CheckBox) d.findViewById(R.preferences.keep_awake);
            wakeButton.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    MainWindow.this.keepAwake = isChecked;
                    if (isChecked) {
                        wakeLock.acquire();
                    } else {
                        wakeLock.release();
                    }
                } 
            });
            
            final RadioGroup sysGroup = (RadioGroup) d.findViewById(R.preferences.system);
            sysGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                    final RadioButton checked = (RadioButton) group.findViewById(checkedId);
                    MainWindow.this.changeSystem(checked.getText().toString());
                }
            });
            break;
        case R.layout.about:
            d = new Dialog(this);
            d.setTitle("About");
            d.setContentView(id);
            break;
        default:
            d = null;
            break;
        }
        return d;
    }
        
    
}