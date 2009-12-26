package com.roller.whitewolf.newdarkness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.roller.Util;

@SuppressWarnings("serial")
public class NewWorldOfDarknessRoll {
    public static int countSuccesses(final List<Integer> rolls) {
        int successes = 0;
        final int difficulty = 8;
        for (int i : rolls) {
            if (i >= difficulty) { 
                ++successes;
            }
        }
        return successes;
    }
    
    public static ArrayList<Integer> makeRoll(final int numDice, final int again) {
        final ArrayList<Integer> rolls = new ArrayList<Integer>(numDice);
        int extraRolls = 0;
        for (int i = 0; i < numDice + extraRolls; ++i) {
            final int r = Util.rollDie(10);
            if (r >= again) { ++extraRolls; }
            rolls.add(r);
        }
        
        return rolls;
    }
    
    public static class Results implements Serializable {
        private final Details details;
        private final ArrayList<Integer> rolls;
        private final int successes;
        
        public Results(final Details d) {
            this.rolls = NewWorldOfDarknessRoll.makeRoll(d.getNumDice(), d.getAgain());
            this.successes = NewWorldOfDarknessRoll.countSuccesses(rolls);
            this.details = d;
        }
        
        public ArrayList<Integer> getRolls()  { return rolls; }
        public Details getDetails() { return details; }
        
        public CharSequence getResultString() {
            final StringBuilder result = new StringBuilder();
            result.append("Successes: ");
            if (successes == 0 && rolls.size() == 0) {
                result.append("BOTCH");
            } else {
                result.append(successes);
            }
            result.append("\nRolls: ");
            
            final ArrayList<Integer> rolls = this.rolls;
            final int len = rolls.size();
            for (int i = 0; i < len; ++i) {
                final int r = rolls.get(i);
                if (i > 0) { result.append(", "); }
                result.append(r);
            }
            return result;
        }
        
        public CharSequence getDetailsString() {
            final Details details = this.details;
            
            final StringBuilder res = new StringBuilder();
            res.append(details.getNumDice());
            if (details.getAgain() > 10) {
            	res.append("D10\nNo Again");
        	}else {
            	res.append("D10\nAgain: ");
            	res.append(details.getAgain());
        	}
            return res;
            
        }
    }
    
    public static class Details implements Serializable {
        private final int numDice;
        private final int again;
        
        public Details(final int dice, final int diff) {
            numDice = dice;
            again = diff;
        }
        
        public int getNumDice() { return numDice; }
        public int getAgain() { return again; }
    }
}