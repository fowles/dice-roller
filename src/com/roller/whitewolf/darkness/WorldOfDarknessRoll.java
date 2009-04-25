package com.roller.whitewolf.darkness;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.roller.Util;

@SuppressWarnings("serial")
public class WorldOfDarknessRoll {
    public static int countSuccesses(final List<Integer> rolls, final int numDice, final int difficulty) {
        int successes = 0;
        final int len = rolls.size();
        for (int i = 0; i < len; ++i) {
            final int r = rolls.get(i);
            if (r >= difficulty) { 
                ++successes;
            }
            if (r == 1 && i < numDice) {
                --successes;
            }
        }
        return successes;
    }
    
    public static ArrayList<Integer> makeRoll(final int numDice) {
        final ArrayList<Integer> rolls = new ArrayList<Integer>(numDice);
        int extraRolls = 0;
        for (int i = 0; i < numDice; ++i) {
            final int r = Util.rollDie(10);
            if (r == 10) { ++extraRolls; }
            if (r == 1)  { --extraRolls; }
            rolls.add(r);
        }
        
        for (int i = 0; i < extraRolls; ++i) {
            final int r = Util.rollDie(10);
            if (r == 10) { ++extraRolls; }
            rolls.add(r);
        }
        
        return rolls;
    }
    
    public static class Results implements Serializable {
        private final Details details;
        private final ArrayList<Integer> rolls;
        private final int successes;
        
        public Results(final Details d) {
            this.rolls = WorldOfDarknessRoll.makeRoll(d.getNumDice());
            this.successes = WorldOfDarknessRoll.countSuccesses(rolls, d.getNumDice(), d.getDifficulty());
            this.details = d;
        }
        
        public ArrayList<Integer> getRolls()  { return rolls; }
        public Details getDetails() { return details; }
        
        public CharSequence getResultString() {
            final StringBuilder result = new StringBuilder();
            result.append("Successes: ");
            if (successes < 0) {
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
            res.append("D10\nDifficulty: ");
            res.append(details.getDifficulty());
            return res;
            
        }
    }
    
    public static class Details implements Serializable {
        private final int numDice;
        private final int difficulty;
        
        public Details(final int dice, final int diff) {
            numDice = dice;
            difficulty = diff;
        }
        
        public int getNumDice() { return numDice; }
        public int getDifficulty() { return difficulty; }
    }
}