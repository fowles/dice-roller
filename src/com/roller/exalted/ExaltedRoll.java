package com.roller.exalted;

import java.io.Serializable;

import com.roller.Util;

@SuppressWarnings("serial")
public class ExaltedRoll {
    public static boolean isBotch(final int[] rolls) {
        boolean botchable = false;
        final int len = rolls.length;
        for (int i = 0; i < len; ++i) {
            final int r = rolls[i];
            if (r >= 7) { return false; }
            if (r == 1) { botchable = true; }
        }
        return botchable;
    }
    
    public static int countSuccesses(final int[] rolls, final boolean isDamage) {
        int successes = 0;
        final int len = rolls.length;
        for (int i = 0; i < len; ++i) {
            final int r = rolls[i];
            if (r >= 7) { 
                successes += !isDamage && r == 10 ? 2 : 1;
            }
        }
        return successes;
    }
    
    public static class Results implements Serializable {
        private final Details details;
        private final int[] rolls;
        private final int successes;
        private final boolean botch;
        
        public Results(final Details d) {
            final int len = d.getNumDice();
            final int[] rolls = Util.rollDice(len, 10);
            final boolean damage = d.isDamage();
            
            this.successes = ExaltedRoll.countSuccesses(rolls, damage);
            this.botch = ExaltedRoll.isBotch(rolls);
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
            res.append(details.getNumDice());
            res.append("D10");
            if (details.isDamage()) {
                res.append(" Damage");
            }
            return res;
            
        }
    }
    
    public static class Details implements Serializable {
        private final int numDice;
        private final boolean isDamage;
        
        public Details(final int d, final boolean damage) {
            numDice = d;
            isDamage = damage;
        }
        
        public int getNumDice() { return numDice; }
        public boolean isDamage() { return isDamage; }
    }
}