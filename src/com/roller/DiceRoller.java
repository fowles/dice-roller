package com.roller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiceRoller extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Context ctxt = getApplicationContext();
        ListView listView = (ListView) findViewById(R.id.list);
        ListAdapter la = new ListAdapter(ctxt);
        listView.setAdapter(la);
        la.add(new RollInfo("Attack", 10));
        la.add(new RollInfo("Damage", 20));
        listView.invalidate();
    }
    
    private static class RollInfo {
        private final String name;
        private final int numDice;
        public RollInfo(String n, int d) {
            name = n;
            numDice = d;
        }
        public String getName() { return name; }
        public int getNumDice() { return numDice; }
        
        public void updateView(View v) {
            TextView nameView = (TextView) v.findViewById(R.id.item_name);
            TextView diceView = (TextView) v.findViewById(R.id.item_dice);
            nameView.setText(name);
            diceView.setText(numDice + "D10");
        }
    }
    
    private class ListAdapter extends ArrayAdapter<RollInfo> {
        public ListAdapter(Context context) {
            super(context, R.layout.item, R.id.item_name);            
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View res = super.getView(position, convertView, parent);
            getItem(position).updateView(res);
            return res;
        }
        
    }
}