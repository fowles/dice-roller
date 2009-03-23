package com.roller.generic;

import java.io.Serializable;

import com.roller.Util;

@SuppressWarnings("serial")
public class GenericRoll {
    public static class Results implements Serializable {
        private final Details details;
        private final int[] rolls;
        
        public Results(final Details d) {
            this.details = d;
            this.rolls = Util.rollDice(d.getNumDice(), d.getNumSides());;
        }
        
        public int[] getRolls()  { return rolls; }
        public Details getDetails() { return details; }
        
        public CharSequence getResultString() {
            final int[] rolls = this.rolls;
            final int len = rolls.length;
            
            final StringBuilder rollStr = new StringBuilder();
            int total = 0;
            for (int i = 0; i < len; ++i) {
                final int r = rolls[i];
                if (i > 0) { rollStr.append(", "); }
                total += r;
                rollStr.append(r);
            }
            final StringBuilder result = new StringBuilder();
            result.append("Total: ").append(total);
            result.append("\nRolls: ").append(rollStr);
            return result;
        }
        
        public CharSequence getDetailsString() {
            final Details details = this.details;
            
            final StringBuilder res = new StringBuilder();
            res.append(details.getName());
            if (res.length() > 0) {
                res.append('\n');
            }
            res.append(details.getNumDice());
            res.append("D");
            res.append(details.getNumSides());
            return res;
            
        }
    }
    
    public static class Details implements Serializable {
        private final String name;
        private final int numDice;
        private final int numSides;
        
        public Details(final CharSequence name, final int dice, final int sides) {
            this.numDice = dice;
            this.numSides = sides;
            this.name = name.toString();
        }
        
        public int getNumDice() { return numDice; }
        public int getNumSides() { return numSides; }
        public String getName() { return name; }
    }
}