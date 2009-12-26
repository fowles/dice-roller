/**
 * 
 */
package com.roller.whitewolf.newdarkness;

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

public class NewWorldOfDarknessSystem implements DiceSystem, OnItemClickListener, OnClickListener, OnSeekBarChangeListener  {
    private static final String TAG = "com.roller.whitewolf.newdarkness.NewWorldOfDarknessSystem";
    private static final String SAVE_FILE = "new-world-of-darkness-list-file";

    public static final int SYSTEM_ID = R.string.newdarkness;
    public int getSystemId() { return SYSTEM_ID; }
    
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_FILL_SIZE = 20;
    
    private static final int MENU_ROLL_NORMAL = Menu.FIRST + 0;
    private static final int MENU_DELETE      = Menu.FIRST + 2;
    
    private final NewWorldOfDarknessListAdapter adapter;
    private final ListView list;
    private final MainWindow mainWindow;
    private final NumberSpinner spinner;
    private final SeekBar difficultySeek;
    private final TextView difficultyText;
    

    public NewWorldOfDarknessSystem(final MainWindow m) {
        m.setContentView(R.layout.newdarkness);
        
        final ListView list = (ListView) m.findViewById(R.newdarkness.list);
        m.registerForContextMenu(list);
        
        final NewWorldOfDarknessListAdapter adapter = new NewWorldOfDarknessListAdapter(m);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        
        final NumberSpinner spinner = (NumberSpinner) m.findViewById(R.newdarkness.number_spinner);
        spinner.setOnClickListener(this);
        m.findViewById(R.newdarkness.roll_normal).setOnClickListener(this);
        
        final TextView difficultyText = (TextView) m.findViewById(R.newdarkness.difficulty_text);
        final SeekBar difficultySeek = (SeekBar) m.findViewById(R.newdarkness.difficulty_seek);
        difficultySeek.setOnSeekBarChangeListener(this);
        
        this.spinner = spinner;
        this.difficultySeek = difficultySeek;
        this.difficultyText = difficultyText;
        this.adapter = adapter;
        this.list = list;
        this.mainWindow = m;
    }
    
    public void addRoll(final NewWorldOfDarknessRoll.Details details) {
        final NewWorldOfDarknessRoll.Results res = new NewWorldOfDarknessRoll.Results(details);
        final NewWorldOfDarknessListAdapter adapter = this.adapter;
        adapter.insert(res, 0);
        while (MAX_SIZE < adapter.getCount()) {
            adapter.remove(adapter.getItem(MAX_SIZE));
        }
        list.setSelection(0);
    }
    
    public void loadState() {
        final NewWorldOfDarknessListAdapter adapter = this.adapter;
        adapter.clear();
        try {
            final ObjectInputStream ois = new ObjectInputStream(mainWindow.openFileInput(SAVE_FILE));
            final NewWorldOfDarknessRoll.Results[] rolls = (NewWorldOfDarknessRoll.Results[]) ois.readObject();
            ois.close();
            
            for (final NewWorldOfDarknessRoll.Results r : rolls) {
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
                addRoll(new NewWorldOfDarknessRoll.Details(i, 10));
            }
        }
    }

    public void saveState() {
        final NewWorldOfDarknessListAdapter adapter = this.adapter;
        final int len = adapter.getCount();
        final NewWorldOfDarknessRoll.Results[] rolls = new NewWorldOfDarknessRoll.Results[len];
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
        final NewWorldOfDarknessRoll.Results rollResults = adapter.getItem(pos);
        final NewWorldOfDarknessRoll.Details rollDetails = rollResults.getDetails();
        final int itemId = item.getItemId();
        switch (itemId) {
        case MENU_ROLL_NORMAL: 
            final NewWorldOfDarknessRoll.Details newRoll = new NewWorldOfDarknessRoll.Details(
                    rollDetails.getNumDice(),
                    rollDetails.getAgain());
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
        final NewWorldOfDarknessRoll.Details newRoll = new NewWorldOfDarknessRoll.Details(
                spinner.getValue(),
                difficultySeek.getProgress() + 8);
        addRoll(newRoll);
    }

    public void onProgressChanged(final SeekBar seekbar, final int i, final boolean flag) {
    	if (i == 3) difficultyText.setText("No Again");
    	else difficultyText.setText("Again: " + (i+8));
    }

    public void onStartTrackingTouch(final SeekBar seekbar) { }
    public void onStopTrackingTouch(final SeekBar seekbar) { }
}