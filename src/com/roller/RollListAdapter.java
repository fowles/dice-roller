/**
 * 
 */
package com.roller;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RollListAdapter extends ArrayAdapter<RollDetails> {
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
    
    private static class RollItemListener implements OnClickListener {
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
}