/**
 * 
 */
package com.roller.generic;

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

public class GenericSystem implements DiceSystem, OnItemClickListener,
		OnClickListener {
	private static final String TAG = "com.roller.generic.GenericSystem";
	private static final String SAVE_FILE = "generic-list-file";
	public static final int SYSTEM_ID = R.string.generic;

	public int getSystemId() {
		return SYSTEM_ID;
	}

	private static final int MAX_SIZE = 100;

	private static final int MENU_REROLL = Menu.FIRST + 0;
	private static final int MENU_DELETE = Menu.FIRST + 1;

	private final GenericListAdapter adapter;
	private final ListView list;
	private final MainWindow mainWindow;
	private final NumberSpinner diceSpinner;
	private final NumberSpinner sidesSpinner;
	private final NumberSpinner modSpinner;

	public GenericSystem(final MainWindow m) {
		m.setContentView(R.layout.generic);

		final ListView list = (ListView) m.findViewById(R.generic.list);
		m.registerForContextMenu(list);

		final NumberSpinner diceSpinner = (NumberSpinner) m
				.findViewById(R.generic.dice_spinner);
		final NumberSpinner sideSpinner = (NumberSpinner) m
				.findViewById(R.generic.sides_spinner);
		final NumberSpinner modSpinner = (NumberSpinner) m
				.findViewById(R.generic.mod_spinner);

		diceSpinner.setOnClickListener(this);
		sideSpinner.setOnClickListener(this);
		modSpinner.setOnClickListener(this);
		m.findViewById(R.generic.roll).setOnClickListener(this);

		final GenericListAdapter adapter = new GenericListAdapter(m);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		this.diceSpinner = diceSpinner;
		this.sidesSpinner = sideSpinner;
		this.modSpinner = modSpinner;
		this.adapter = adapter;
		this.list = list;
		this.mainWindow = m;
	}

	public void addRoll(final GenericRoll.Details details) {
		final GenericRoll.Results res = new GenericRoll.Results(details);
		final GenericListAdapter adapter = this.adapter;
		adapter.insert(res, 0);
		while (MAX_SIZE < adapter.getCount()) {
			adapter.remove(adapter.getItem(MAX_SIZE));
		}
		list.setSelection(0);
	}

	public void loadState() {
		final GenericListAdapter adapter = this.adapter;
		adapter.clear();
		try {
			final ObjectInputStream ois = new ObjectInputStream(mainWindow
					.openFileInput(SAVE_FILE));
			final GenericRoll.Results[] rolls = (GenericRoll.Results[]) ois
					.readObject();
			ois.close();

			for (final GenericRoll.Results r : rolls) {
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
			addRoll(new GenericRoll.Details(1, 2, 0));
			addRoll(new GenericRoll.Details(1, 4, 0));
			addRoll(new GenericRoll.Details(1, 6, 0));
			addRoll(new GenericRoll.Details(1, 8, 0));
			addRoll(new GenericRoll.Details(1, 10, 0));
			addRoll(new GenericRoll.Details(1, 12, 0));
			addRoll(new GenericRoll.Details(1, 20, 0));
			addRoll(new GenericRoll.Details(1, 100, 0));
		}
	}

	public void saveState() {
		final GenericListAdapter adapter = this.adapter;
		final int len = adapter.getCount();
		final GenericRoll.Results[] rolls = new GenericRoll.Results[len];
		for (int i = 0; i < len; ++i) {
			rolls[i] = adapter.getItem(i);
		}

		try {
			final ObjectOutputStream oos = new ObjectOutputStream(mainWindow
					.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE));
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

	public void onCreateContextMenu(final ContextMenu menu, final View v,
			final ContextMenuInfo menuInfo) {
		menu.add(0, MENU_REROLL, 0, "Reroll");
		menu.add(0, MENU_DELETE, 0, "Delete");
	}

	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int pos = info.position;
		final GenericRoll.Results rollResults = adapter.getItem(pos);
		final GenericRoll.Details rollDetails = rollResults.getDetails();
		final int itemId = item.getItemId();
		switch (itemId) {
		case MENU_REROLL:
			final GenericRoll.Details newRoll = new GenericRoll.Details(
					rollDetails.getNumDice(), rollDetails.getNumSides(),
					rollDetails.getModifier());
			addRoll(newRoll);
			return true;
		case MENU_DELETE:
			adapter.remove(rollResults);
			return true;
		default:
			return false;
		}
	}

	public void onItemClick(final AdapterView<?> a, final View item,
			final int pos, final long id) {
		addRoll(adapter.getItem(pos).getDetails());
	}

	public void onClick(final View v) {
		addRoll(new GenericRoll.Details(diceSpinner.getValue(), sidesSpinner
				.getValue(), modSpinner.getValue()));
	}
}