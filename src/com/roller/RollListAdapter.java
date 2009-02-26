/**
 * 
 */
package com.roller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class RollListAdapter extends ArrayAdapter<RollDetails> {
    private static final String TAG = "com.roller.RollListAdapter";
    private static final String SAVE_FILE = "roll-list-file";
    
    class RollItemListener implements OnClickListener {
        private final RollDetails details;
        
        public RollItemListener(final RollDetails roll) {
            details = roll;
        }
        
        public void onClick(final View v) {
            int stunt = 0;
            switch (v.getId()) {
            case R.item.stunt3: stunt = 3; break;
            case R.item.stunt2: stunt = 2; break;
            case R.item.stunt1: stunt = 1; break;
            case R.item.stunt0: default:   break;
            
            case R.item.delete: 
                RollListAdapter.this.remove(details);
                return;
            }
            
            mainWindow.performRoll(details, stunt);
        }
        
        public void registerListener(final View v) {
            v.findViewById(R.item.stunt0).setOnClickListener(this);
            v.findViewById(R.item.stunt1).setOnClickListener(this);
            v.findViewById(R.item.stunt2).setOnClickListener(this);
            v.findViewById(R.item.stunt3).setOnClickListener(this);
            v.findViewById(R.item.delete).setOnClickListener(this);
        }
    }
    
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
        final RollDetails r = getItem(position);
        nameView.setText(r.getName() + "\n"
                + r.getNumDice() + "D10");
        
        final RollItemListener l = new RollItemListener(r);
        l.registerListener(v);
        
        return v;
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
            Log.w(TAG, e);
        } catch (final IOException e) {
            Log.w(TAG, e);
        } catch (final ClassNotFoundException e) {
            Log.w(TAG, e);
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
            Log.w(TAG, e);
        } catch (final IOException e) {
            Log.w(TAG, e);
        }
    }

    public void showAddDialog() {
        final Dialog d = new Dialog(mainWindow);
        d.setTitle("Add Roll Option");
        d.setContentView(R.layout.add_item);

        final Button ok = (Button) d.findViewById(R.add_item.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final TextView name = (TextView) d.findViewById(R.add_item.name);
                final TextView dice = (TextView) d.findViewById(R.add_item.dice);
                final CheckBox damage = (CheckBox) d.findViewById(R.add_item.damage);

                RollListAdapter.this.add(new RollDetails(
                        name.getText(),
                        Integer.parseInt(dice.getText().toString()),
                        damage.isChecked()
                ));
                d.dismiss();
            }
        });

        final Button cancel = (Button) d.findViewById(R.add_item.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                d.dismiss();
            }
        });

        d.show();    
    }
}