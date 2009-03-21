/**
 * 
 */
package com.roller.exalted;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.roller.MainWindow;
import com.roller.R;

public class ExaltedListAdapter extends ArrayAdapter<ExaltedRoll.Results> {
    public ExaltedListAdapter(final MainWindow m) {
        super(m.getApplicationContext(), R.layout.exalted_item, R.exalted_item.dummy);
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
}