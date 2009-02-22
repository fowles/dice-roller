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

//        listView.addView(item);
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
    }
    
    private class ListAdapter extends ArrayAdapter<String> {
        public ListAdapter(Context context) {
            super(context, R.layout.item, R.id.entry_dummy);            
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View res = super.getView(position, convertView, parent);
//            TextView ninja = (TextView) res.findViewById(R.id.ninja);
//            TextView pirate = (TextView) res.findViewById(R.id.pirate);
//
//            String item = this.getItem(position);
//            ninja.setText(item + position);
//            pirate.setText(position + item);
            return res;
        }
        
    }
}