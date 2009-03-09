package com.roller.exalted;

import java.io.Serializable;

import com.roller.Util;

@SuppressWarnings("serial")
public class ExaltedRoll {
    public static class Results implements Serializable {
        private final Details details;
        private final int[] rolls;
        private final int successes;
        private final boolean botch;
        
        public Results(final Details d) {
            final int len = d.getNumDice();
            final int[] rolls = Util.rollDice(len, 10);
            final boolean damage = d.isDamage();
            
            int successes = 0;
            boolean botchable = false;
            for (int i = 0; i < len; ++i) {
                final int r = rolls[i];
                
                if (r == 1) { botchable = true; } 
                if (r >= 7) {
                    successes += !damage && r == 10 ? 2 : 1;
                }
            }
            
            this.botch = botchable && successes == 0;
            this.successes = successes;
            this.details = d;
            this.rolls = rolls;
        }
        
        public int[] getRolls()  { return rolls; }
        public Details getDetails() { return details; }
        
        public CharSequence getResultString() {
            final int[] rolls = this.rolls;
            final int len = rolls.length;
            
            final StringBuilder result = new StringBuilder();
            result.append("Successes: ");
            if (botch) {
                result.append("BOTCH");
            } else {
                result.append(successes);
            }
            result.append("\nRolls: ");
            for (int i = 0; i < len; ++i) {
                final int r = rolls[i];
                if (i > 0) { result.append(", "); }
                result.append(r);
            }
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
            res.append("D10");
            if (details.isDamage()) {
                res.append(" Damage");
            }
            return res;
            
        }
    }
    
    public static class Details implements Serializable {
        private final String name;
        private final int numDice;
        private final boolean isDamage;
        
        public Details(final CharSequence n, final int d, final boolean damage) {
            name = n.toString();
            numDice = d;
            isDamage = damage;
        }
        
        public String getName() { return name; }
        public int getNumDice() { return numDice; }
        public boolean isDamage() { return isDamage; }
    }
}