package com.roller;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public interface DiceSystem {
    public abstract void loadState();
    public abstract void saveState();
    public abstract void clearState();
    public abstract void showAddDialog();
    public abstract void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo);
    public abstract boolean onContextItemSelected(final MenuItem item);
}