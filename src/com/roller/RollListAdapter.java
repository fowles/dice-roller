/**
 * 
 */
package com.roller;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RollListAdapter extends ArrayAdapter<RollDetails> {
    private static final String ROLL_LIST_PREFS = "roll-list-prefs";
    private static final String PREFS_NAME_PREFIX = "roll-name-";
    private static final String PREFS_DICE_PREFIX = "roll-dice";
    private static final String PREFS_DAMAGE_PREFIX = "roll-damage";
    
    private final ListView listView;
    private final MainWindow mainWindow;
    
    public RollListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.item, R.item.dummy);
        mainWindow = m;
        listView = (ListView) m.findViewById(R.main.list);
        listView.setAdapter(this);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);
        final TextView nameView = (TextView) v.findViewById(R.item.name);
        final TextView diceView = (TextView) v.findViewById(R.item.dice);
        final RollDetails r = getItem(position);
        nameView.setText(r.getName());
        diceView.setText(r.getNumDice() + "D10");
        
        final RollItemListener l = new RollItemListener(mainWindow, r);
        l.registerListener(v);
        
        return v;
    }
    
    static class RollItemListener implements OnClickListener {
        private final RollDetails details;
        private final MainWindow mainWindow;
        
        public RollItemListener(final MainWindow m, final RollDetails roll) {
            details = roll;
            mainWindow = m;
        }
        
        public void onClick(final View v) {
            int stunt = 0;
            switch (v.getId()) {
            case R.item.stunt3: stunt = 3; break;
            case R.item.stunt2: stunt = 2; break;
            case R.item.stunt1: stunt = 1; break;
            case R.item.stunt0: default:   break;
            }
            
            mainWindow.performRoll(details, stunt);
        }
        
        public void registerListener(final View v) {
            v.findViewById(R.item.stunt0).setOnClickListener(this);
            v.findViewById(R.item.stunt1).setOnClickListener(this);
            v.findViewById(R.item.stunt2).setOnClickListener(this);
            v.findViewById(R.item.stunt3).setOnClickListener(this);
        }
    }

    public void loadList() {
        final SharedPreferences settings = getContext().getSharedPreferences(ROLL_LIST_PREFS, 0);
        clear();
        int i = 0;
        String k = PREFS_NAME_PREFIX + i;
        while (settings.contains(k)) {
            final String name = settings.getString(k, "");
            final int dice = settings.getInt(PREFS_DICE_PREFIX+i, 0);
            final boolean damage = settings.getBoolean(PREFS_DAMAGE_PREFIX+i, false);
            add(new RollDetails(name, dice, damage));
            k = PREFS_NAME_PREFIX + ++i;
        }
    }

    public void saveList() {
        final SharedPreferences settings = getContext().getSharedPreferences(ROLL_LIST_PREFS, 0);
        final Editor edit = settings.edit();
        final int len = getCount();
        for (int i = 0; i < len; ++i) {
            final RollDetails r = getItem(i);
            edit.putString(PREFS_NAME_PREFIX+i, r.getName().toString());
            edit.putInt(PREFS_DICE_PREFIX+i, r.getNumDice());
            edit.putBoolean(PREFS_DAMAGE_PREFIX+i, r.isDamage());
        }
        edit.commit();
    }
}