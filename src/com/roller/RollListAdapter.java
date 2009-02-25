/**
 * 
 */
package com.roller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RollListAdapter extends ArrayAdapter<RollDetails> {
    private static final String LOG_TAG = "com.roller.RollListAdapter";
    private static final String SAVE_FILE = "roll-list-file";
    
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
        clear();
        try {
            final ObjectInputStream ois = new ObjectInputStream(getContext().openFileInput(SAVE_FILE));
            final RollDetails[] rolls = (RollDetails[]) ois.readObject();
            ois.close();
            
            for (final RollDetails r : rolls) {
                this.add(r);
            }
        } catch (final FileNotFoundException e) {
            // oh well
        } catch (final StreamCorruptedException e) {
            Log.w(LOG_TAG, e);
        } catch (final IOException e) {
            Log.w(LOG_TAG, e);
        } catch (final ClassNotFoundException e) {
            Log.w(LOG_TAG, e);
        }
    }

    public void saveList() {
        final int len = getCount();
        final RollDetails[] rolls = new RollDetails[len];
        for (int i = 0; i < len; ++i) {
            rolls[i] = getItem(i);
        }
        
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(getContext().openFileOutput(SAVE_FILE, Context.MODE_PRIVATE));
            oos.writeObject(rolls);
            oos.close();
        } catch (final FileNotFoundException e) {
            Log.w(LOG_TAG, e);
        } catch (final IOException e) {
            Log.w(LOG_TAG, e);
        }
    }
}