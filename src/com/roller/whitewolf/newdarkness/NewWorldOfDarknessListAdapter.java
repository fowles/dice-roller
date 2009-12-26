/**
 * 
 */
package com.roller.whitewolf.newdarkness;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.roller.MainWindow;
import com.roller.R;

public class NewWorldOfDarknessListAdapter extends ArrayAdapter<NewWorldOfDarknessRoll.Results> {
    public NewWorldOfDarknessListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.generic_item, R.generic_item.dummy);
    }
    
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);
        final TextView details = (TextView) v.findViewById(R.generic_item.details);
        final TextView succ = (TextView) v.findViewById(R.generic_item.summary);
        
        final NewWorldOfDarknessRoll.Results r = this.getItem(position);
        details.setText(r.getDetailsString());
        succ.setText(r.getResultString());
        return v;
    }
}