package com.roller.exalted;

public final class ExaltedUtil {
    private ExaltedUtil() { }
    
    public static CharSequence calculateResult(final int[] rolls, final boolean damage) {
        final int len = rolls.length;
        
        final StringBuilder rollStr = new StringBuilder();
        rollStr.append("Rolls: ");
        int successes = 0;
        boolean botchable = true;
        for (int i = 0; i < len; ++i) {
            final int r = rolls[i];
            
            if (i > 0) { rollStr.append(", "); }
            rollStr.append(r);
            
            if (r != 1) { botchable = false; } 
            if (r >= 7) {
                successes += !damage && r == 10 ? 2 : 1;
            }
        }
        
        final StringBuilder result = new StringBuilder();
        result.append("Successes: ");
        if (botchable && successes == 0) {
            result.append("BOTCH");
        } else {
            result.append(successes);
        }
        result.append("\n").append(rollStr);
        return result;
    }
    
    public static CharSequence formatDetails(final ExaltedRollDetails details) {
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
