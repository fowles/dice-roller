/**
 * 
 */
package com.roller.exalted;

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

import com.roller.MainWindow;
import com.roller.R;

public class ExaltedListAdapter extends ArrayAdapter<ExaltedRollDetails> {
    private static final String TAG = "com.roller.RollListAdapter";
    private static final String SAVE_FILE = "roll-list-file";
    
    class RollItemListener implements OnClickListener {
        private final ExaltedRollDetails details;
        
        public RollItemListener(final ExaltedRollDetails roll) {
            details = roll;
        }
        
        public void onClick(final View v) {
            int stunt = 0;
            switch (v.getId()) {
            case R.exalted_item.stunt3: stunt = 3; break;
            case R.exalted_item.stunt2: stunt = 2; break;
            case R.exalted_item.stunt1: stunt = 1; break;
            case R.exalted_item.stunt0: default:   break;
            
            case R.exalted_item.delete: 
                ExaltedListAdapter.this.remove(details);
                return;
            }
            mainWindow.setResult(details.calculateResults(stunt));
        }
        
        public void registerListener(final View v) {
            v.findViewById(R.exalted_item.stunt0).setOnClickListener(this);
            v.findViewById(R.exalted_item.stunt1).setOnClickListener(this);
            v.findViewById(R.exalted_item.stunt2).setOnClickListener(this);
            v.findViewById(R.exalted_item.stunt3).setOnClickListener(this);
            v.findViewById(R.exalted_item.delete).setOnClickListener(this);
        }
    }
    
    private final ListView listView;
    private final MainWindow mainWindow;

    public ExaltedListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.exalted_item, R.exalted_item.dummy);
        mainWindow = m;
        listView = (ListView) m.findViewById(R.main.list);
        listView.setAdapter(this);
    }
    
     @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);
        final TextView nameView = (TextView) v.findViewById(R.exalted_item.name);
        final ExaltedRollDetails r = getItem(position);
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
            final ExaltedRollDetails[] rolls = (ExaltedRollDetails[]) ois.readObject();
            ois.close();
            
            for (final ExaltedRollDetails r : rolls) {
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
        final ExaltedRollDetails[] rolls = new ExaltedRollDetails[len];
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
        d.setContentView(R.layout.exalted_add);

        final Button ok = (Button) d.findViewById(R.exalted_add.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final TextView name = (TextView) d.findViewById(R.exalted_add.name);
                final TextView dice = (TextView) d.findViewById(R.exalted_add.dice);
                final CheckBox damage = (CheckBox) d.findViewById(R.exalted_add.damage);

                ExaltedListAdapter.this.add(new ExaltedRollDetails(
                        name.getText(),
                        Integer.parseInt(dice.getText().toString()),
                        damage.isChecked()
                ));
                d.dismiss();
            }
        });

        final Button cancel = (Button) d.findViewById(R.exalted_add.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                d.dismiss();
            }
        });

        d.show();    
    }
}