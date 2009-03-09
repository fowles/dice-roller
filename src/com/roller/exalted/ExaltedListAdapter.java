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
    private static final int MENU_REROLL = Menu.FIRST + 0;
    private static final int MENU_DELETE = Menu.FIRST + 1;
    
    private final ListView listView;
    private final MainWindow mainWindow;

    public ExaltedListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.exalted_item, R.exalted_item.dummy);
        mainWindow = m;
        listView = (ListView) m.findViewById(R.main.list);
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
        d.setTitle("Add Roll Option");
        d.setContentView(R.layout.exalted_add);

        final Button ok = (Button) d.findViewById(R.exalted_add.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final TextView name = (TextView) d.findViewById(R.exalted_add.name);
                final TextView dice = (TextView) d.findViewById(R.exalted_add.dice);
                final CheckBox damage = (CheckBox) d.findViewById(R.exalted_add.damage);

                ExaltedListAdapter.this.addRoll(new ExaltedRoll.Details(
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

    public void onItemClick(final AdapterView<?> adapter, final View item, final int pos, final long id) {
        addRoll(getItem(pos).getDetails());
    }

    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {       
        menu.add(0, MENU_REROLL, 0, "Roll");
        menu.add(0, MENU_DELETE, 0, "Delete");
    }
    
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case MENU_REROLL: 
            addRoll(getItem(info.position).getDetails());
            return true;
        case MENU_DELETE: 
            remove(getItem(info.position));
            return true;
        default: return false;
        }
    }
}