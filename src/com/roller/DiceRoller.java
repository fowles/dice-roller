package com.roller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiceRoller extends Activity implements View.OnClickListener {
    private ListAdapter rollAdapter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater inflater = getLayoutInflater();

        final View main = inflater.inflate(R.layout.main, null);

        final View addDiceButton = main.findViewById(R.id.add_dice);
        addDiceButton.setOnClickListener(this);

        final Context ctxt = getApplicationContext();
        final ListView listView = (ListView) main.findViewById(R.id.list);
        rollAdapter = new ListAdapter(ctxt);
        listView.setAdapter(rollAdapter);
        rollAdapter.add(new RollInfo("Attack", 10));
        rollAdapter.add(new RollInfo("Damage", 20));

        setContentView(main);
    }

    private static class RollInfo {
        private final String name;
        private final int numDice;

        public RollInfo(final String n, final int d) {
            name = n;
            numDice = d;
        }

        public String getName() {
            return name;
        }

        public int getNumDice() {
            return numDice;
        }

        public void updateView(final View v) {
            final TextView nameView = (TextView) v.findViewById(R.id.item_name);
            final TextView diceView = (TextView) v.findViewById(R.id.item_dice);
            nameView.setText(name);
            diceView.setText(numDice + "D10");
        }
    }

    private class ListAdapter extends ArrayAdapter<RollInfo> {
        public ListAdapter(final Context context) {
            super(context, R.layout.item, R.id.item_dummy);
        }

        @Override
        public View getView(final int position, final View convertView,
                final ViewGroup parent) {
            final View res = super.getView(position, convertView, parent);
            getItem(position).updateView(res);
            return res;
        }

    }

    public void onClick(final View v) {
        rollAdapter.add(new RollInfo("Damage", 20));
    }
}