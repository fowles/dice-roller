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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.roller.MainWindow;
import com.roller.R;

public class ExaltedListAdapter extends ArrayAdapter<ExaltedRoll.Results> implements OnItemClickListener {
    private static final String TAG = "com.roller.ExaltedListAdapter";
    private static final String SAVE_FILE = "exalted-list-file";
    
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_FILL_SIZE = 20;
    
    private static final int MENU_ROLL_NORMAL = Menu.FIRST + 0;
    private static final int MENU_ROLL_DAMAGE = Menu.FIRST + 1;
    private static final int MENU_DELETE      = Menu.FIRST + 2;
    
    private final ListView listView;
    private final MainWindow mainWindow;

    public ExaltedListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.exalted_item, R.exalted_item.dummy);
        mainWindow = m;
        listView = m.getListView();
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }
    
    public void addRoll(final ExaltedRoll.Details details) {
        final ExaltedRoll.Results res = new ExaltedRoll.Results(details);
        insert(res, 0);
        listView.setSelection(0);
        while (MAX_SIZE < getCount()) {
            remove(getItem(MAX_SIZE));
        }
    }
    
    
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);
        final TextView details = (TextView) v.findViewById(R.exalted_item.details);
        final TextView succ = (TextView) v.findViewById(R.exalted_item.successes);
        
        final ExaltedRoll.Results r = this.getItem(position);
        details.setText(r.getDetailsString());
        succ.setText(r.getResultString());
        return v;
    }
     
    public void loadList() {
        clear();
        try {
            final ObjectInputStream ois = new ObjectInputStream(getContext().openFileInput(SAVE_FILE));
            final ExaltedRoll.Results[] rolls = (ExaltedRoll.Results[]) ois.readObject();
            ois.close();
            
            for (final ExaltedRoll.Results r : rolls) {
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
        
        if (isEmpty()) {
            for (int i = 1; i <= DEFAULT_FILL_SIZE; ++i) {
                addRoll(new ExaltedRoll.Details("", i, false));
            }
        }
    }

    public void saveList() {
        final int len = getCount();
        final ExaltedRoll.Results[] rolls = new ExaltedRoll.Results[len];
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
        d.setTitle("Add Roll");
        d.setContentView(R.layout.exalted_add);

        final Button ok = (Button) d.findViewById(R.exalted_add.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final TextView name = (TextView) d.findViewById(R.exalted_add.name);
                final TextView dice = (TextView) d.findViewById(R.exalted_add.dice);
                final CheckBox damage = (CheckBox) d.findViewById(R.exalted_add.damage);

                try {
                    ExaltedListAdapter.this.addRoll(new ExaltedRoll.Details(
                            name.getText(),
                            Integer.parseInt(dice.getText().toString()),
                            damage.isChecked()
                    ));
                } catch (final NumberFormatException nfe) {
                    Log.w(TAG, nfe);
                }
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

    public void onItemClick(final AdapterView<?> adapter, final View item, final int pos, final long id) {
        addRoll(getItem(pos).getDetails());
    }

    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {       
        menu.add(0, MENU_ROLL_NORMAL, 0, "Roll as normal");
        menu.add(0, MENU_ROLL_DAMAGE, 0, "Roll as damage");
        menu.add(0, MENU_DELETE, 0, "Delete");
    }
    
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        final ExaltedRoll.Results rollResults = getItem(pos);
        final ExaltedRoll.Details rollDetails = rollResults.getDetails();
        final int itemId = item.getItemId();
        switch (itemId) {
        case MENU_ROLL_NORMAL: 
        case MENU_ROLL_DAMAGE: 
            final ExaltedRoll.Details newRoll = new ExaltedRoll.Details(
                    rollDetails.getName(),
                    rollDetails.getNumDice(),
                    itemId == MENU_ROLL_DAMAGE);
            addRoll(newRoll);
            return true;
        case MENU_DELETE: 
            remove(rollResults);
            return true;
        default: return false;
        }
    }
}