package com.roller;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainWindow extends Activity implements View.OnClickListener {
    private final Random random = new Random();
    private RollListAdapter rollAdapter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final View addDiceButton = findViewById(R.main.add_dice);
        addDiceButton.setOnClickListener(this);

        rollAdapter = new RollListAdapter(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        rollAdapter.loadList();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        rollAdapter.saveList();
    }

    public void onClick(final View v) {
        rollAdapter.showAddDialog();
    }

    public void performRoll(final RollDetails details, final int stunt) {
        final int dice = details.getNumDice() + stunt;
        final boolean damage = details.isDamage();
        
        final StringBuilder rolls = new StringBuilder();
        int successes = 0;
        boolean botchable = true;
        for (int i = 0; i < dice; ++i) {
            final int r = random.nextInt(10) + 1;
            
            if (i > 0) { rolls.append(", "); }
            rolls.append(r);
            
            if (r != 1) { botchable = false; } 
            if (r >= 7) {
                successes += !damage && r == 10 ? 2 : 1;
            }
        }
        
        final TextView successText = (TextView) findViewById(R.main.success_label);
        final TextView rollText = (TextView) findViewById(R.main.roll_label);
        if (botchable && successes == 0) {
            successText.setText("Successes: BOTCH");
        } else {
            successText.setText("Successes: " + successes);
        }
        rollText.setText(rolls);
    }
}