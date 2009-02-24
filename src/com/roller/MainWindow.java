package com.roller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class MainWindow extends Activity implements View.OnClickListener {
    private ListAdapter rollAdapter = null;
    private ListView listView = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LayoutInflater inflater = getLayoutInflater();

        final View main = inflater.inflate(R.layout.main, null);

        final View addDiceButton = main.findViewById(R.main.add_dice);
        addDiceButton.setOnClickListener(this);

        final Context ctxt = getApplicationContext();
        listView = (ListView) main.findViewById(R.main.list);
        rollAdapter = new ListAdapter(ctxt);
        listView.setAdapter(rollAdapter);
        setContentView(main);
    }

    private static class RollInfo {
        private final CharSequence name;
        private final int numDice;
        private final boolean isDamage;

        public RollInfo(final CharSequence n, final int d, final boolean damage) {
            name = n;
            numDice = d;
            isDamage = damage;
        }

        public CharSequence getName() { return name; }
        public int getNumDice() { return numDice; }
        public boolean isDamage() { return isDamage; }

        public void updateView(final View v) {
            final TextView nameView = (TextView) v.findViewById(R.item.name);
            final TextView diceView = (TextView) v.findViewById(R.item.dice);
            nameView.setText(name);
            diceView.setText(numDice + "D10");
        }
    }

    private class ListAdapter extends ArrayAdapter<RollInfo> {
        public ListAdapter(final Context context) {
            super(context, R.layout.item, R.item.dummy);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View res = super.getView(position, convertView, parent);
            getItem(position).updateView(res);
            return res;
        }

    }

    public void onClick(final View v) {
        final Dialog d = new Dialog(this);
        d.setTitle("Add Roll Option");
        d.setContentView(R.layout.add_item);

        final Button ok = (Button) d.findViewById(R.add_item.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                final TextView name = (TextView) d.findViewById(R.add_item.name);
                final TextView dice = (TextView) d.findViewById(R.add_item.dice);
                final CheckBox damage = (CheckBox) d.findViewById(R.add_item.damage);

                rollAdapter.add(new RollInfo(
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