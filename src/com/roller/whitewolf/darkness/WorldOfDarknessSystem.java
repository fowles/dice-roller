/**
 * 
 */
package com.roller.whitewolf.darkness;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roller.DiceSystem;
import com.roller.MainWindow;
import com.roller.NumberSpinner;
import com.roller.R;

public class WorldOfDarknessSystem implements DiceSystem, OnItemClickListener, OnClickListener, OnSeekBarChangeListener  {
    private static final String TAG = "com.roller.whitewolf.darkness.WorldOfDarknessSystem";
    private static final String SAVE_FILE = "world-of-darkness-list-file";
    
    public static final int SYSTEM_ID = R.string.darkness;
    public int getSystemId() { return SYSTEM_ID; }
    
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_FILL_SIZE = 20;
    
    private static final int MENU_ROLL_NORMAL = Menu.FIRST + 0;
    private static final int MENU_DELETE      = Menu.FIRST + 2;
    
    private final WorldOfDarknessListAdapter adapter;
    private final ListView list;
    private final MainWindow mainWindow;
    private final NumberSpinner spinner;
    private final SeekBar difficultySeek;
    private final TextView difficultyText;
    

    public WorldOfDarknessSystem(final MainWindow m) {
        m.setContentView(R.layout.darkness);
        
        final ListView list = (ListView) m.findViewById(R.darkness.list);
        m.registerForContextMenu(list);
        
        final WorldOfDarknessListAdapter adapter = new WorldOfDarknessListAdapter(m);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        
        final NumberSpinner spinner = (NumberSpinner) m.findViewById(R.darkness.number_spinner);
        spinner.setOnClickListener(this);
        m.findViewById(R.darkness.roll_normal).setOnClickListener(this);
        
        final TextView difficultyText = (TextView) m.findViewById(R.darkness.difficulty_text);
        final SeekBar difficultySeek = (SeekBar) m.findViewById(R.darkness.difficulty_seek);
        difficultySeek.setOnSeekBarChangeListener(this);
        
        this.spinner = spinner;
        this.difficultySeek = difficultySeek;
        this.difficultyText = difficultyText;
        this.adapter = adapter;
        this.list = list;
        this.mainWindow = m;
    }
    
    public void addRoll(final WorldOfDarknessRoll.Details details) {
        final WorldOfDarknessRoll.Results res = new WorldOfDarknessRoll.Results(details);
        final WorldOfDarknessListAdapter adapter = this.adapter;
        adapter.insert(res, 0);
        while (MAX_SIZE < adapter.getCount()) {
            adapter.remove(adapter.getItem(MAX_SIZE));
        }
        list.setSelection(0);
    }
    
    public void loadState() {
        final WorldOfDarknessListAdapter adapter = this.adapter;
        adapter.clear();
        try {
            final ObjectInputStream ois = new ObjectInputStream(mainWindow.openFileInput(SAVE_FILE));
            final WorldOfDarknessRoll.Results[] rolls = (WorldOfDarknessRoll.Results[]) ois.readObject();
            ois.close();
            
            for (final WorldOfDarknessRoll.Results r : rolls) {
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
                addRoll(new WorldOfDarknessRoll.Details(i, 7));
            }
        }
    }

    public void saveState() {
        final WorldOfDarknessListAdapter adapter = this.adapter;
        final int len = adapter.getCount();
        final WorldOfDarknessRoll.Results[] rolls = new WorldOfDarknessRoll.Results[len];
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
        menu.add(0, MENU_ROLL_NORMAL, 0, "Roll");
        menu.add(0, MENU_DELETE, 0, "Delete");
    }
    
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;
        final WorldOfDarknessRoll.Results rollResults = adapter.getItem(pos);
        final WorldOfDarknessRoll.Details rollDetails = rollResults.getDetails();
        final int itemId = item.getItemId();
        switch (itemId) {
        case MENU_ROLL_NORMAL: 
            final WorldOfDarknessRoll.Details newRoll = new WorldOfDarknessRoll.Details(
                    rollDetails.getNumDice(),
                    rollDetails.getDifficulty());
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
        final WorldOfDarknessRoll.Details newRoll = new WorldOfDarknessRoll.Details(
                spinner.getValue(),
                difficultySeek.getProgress() + 2);
        addRoll(newRoll);
    }

    public void onProgressChanged(final SeekBar seekbar, final int i, final boolean flag) {
        difficultyText.setText("Difficulty: " + (i+2));
    }

    public void onStartTrackingTouch(final SeekBar seekbar) { }
    public void onStopTrackingTouch(final SeekBar seekbar) { }
}