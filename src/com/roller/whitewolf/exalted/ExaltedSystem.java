/**
 * 
 */
package com.roller.whitewolf.exalted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.roller.DiceSystem;
import com.roller.MainWindow;
import com.roller.NumberSpinner;
import com.roller.R;

public class ExaltedSystem implements DiceSystem, OnItemClickListener, OnClickListener  {
    private static final String TAG = "com.roller.exalted.ExaltedSystem";
    private static final String SAVE_FILE = "exalted-list-file";
    
    public static final int SYSTEM_ID = R.string.exalted;
    public int getSystemId() { return SYSTEM_ID; }
    
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_FILL_SIZE = 20;
    
    private static final int MENU_ROLL_NORMAL = Menu.FIRST + 0;
    private static final int MENU_ROLL_DAMAGE = Menu.FIRST + 1;
    private static final int MENU_DELETE      = Menu.FIRST + 2;
    
    private final ExaltedListAdapter adapter;
    private final ListView list;
    private final MainWindow mainWindow;
    private final NumberSpinner spinner;
    

    public ExaltedSystem(final MainWindow m) {
        m.setContentView(R.layout.exalted);
        
        final ListView list = (ListView) m.findViewById(R.exalted.list);
        m.registerForContextMenu(list);
        
        final ExaltedListAdapter adapter = new ExaltedListAdapter(m);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        
        final NumberSpinner spinner = (NumberSpinner) m.findViewById(R.exalted.number_spinner);
        spinner.setOnClickListener(this);
        m.findViewById(R.exalted.roll_damage).setOnClickListener(this);
        m.findViewById(R.exalted.roll_normal).setOnClickListener(this);
        
        this.spinner = spinner;
        this.adapter = adapter;
        this.list = list;
        this.mainWindow = m;
    }
    
    public void addRoll(final ExaltedRoll.Details details) {
        final ExaltedRoll.Results res = new ExaltedRoll.Results(details);
        final ExaltedListAdapter adapter = this.adapter;
        adapter.insert(res, 0);
        while (MAX_SIZE < adapter.getCount()) {
            adapter.remove(adapter.getItem(MAX_SIZE));
        }
        list.setSelection(0);
    }
    
    public void loadState() {
        final ExaltedListAdapter adapter = this.adapter;
        adapter.clear();
        try {
            final ObjectInputStream ois = new ObjectInputStream(mainWindow.openFileInput(SAVE_FILE));
            final ExaltedRoll.Results[] rolls = (ExaltedRoll.Results[]) ois.readObject();
            ois.close();
            
            for (final ExaltedRoll.Results r : rolls) {
                adapter.add(r);
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
        
        if (adapter.isEmpty()) {
            for (int i = 1; i <= DEFAULT_FILL_SIZE; ++i) {
                addRoll(new ExaltedRoll.Details(i, false));
            }
        }
    }

    public void saveState() {
        final ExaltedListAdapter adapter = this.adapter;
        final int len = adapter.getCount();
        final ExaltedRoll.Results[] rolls = new ExaltedRoll.Results[len];
        for (int i = 0; i < len; ++i) {
            rolls[i] = adapter.getItem(i);
        }
        
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(mainWindow.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE));
            oos.writeObject(rolls);
            oos.close();
        } catch (final FileNotFoundException e) {
            Log.w(TAG, e);
        } catch (final IOException e) {
            Log.w(TAG, e);
        }
    }
    
    public void clearState() {
        adapter.clear();
    }

    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {       
        menu.add(0, MENU_ROLL_NORMAL, 0, "Roll as normal");
        menu.add(0, MENU_ROLL_DAMAGE, 0, "Roll as damage");
        menu.add(0, MENU_DELETE, 0, "Delete");
    }
    
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        final ExaltedRoll.Results rollResults = adapter.getItem(pos);
        final ExaltedRoll.Details rollDetails = rollResults.getDetails();
        final int itemId = item.getItemId();
        switch (itemId) {
        case MENU_ROLL_NORMAL: 
        case MENU_ROLL_DAMAGE: 
            final ExaltedRoll.Details newRoll = new ExaltedRoll.Details(
                    rollDetails.getNumDice(),
                    itemId == MENU_ROLL_DAMAGE);
            addRoll(newRoll);
            return true;
        case MENU_DELETE: 
            adapter.remove(rollResults);
            return true;
        default: return false;
        }
    }
    
    public void onItemClick(final AdapterView<?> a, final View item, final int pos, final long id) {
        addRoll(adapter.getItem(pos).getDetails());
    }

    public void onClick(final View v) {
        addRoll(new ExaltedRoll.Details(
                spinner.getValue(), 
                v.getId() == R.exalted.roll_damage));
    }
}