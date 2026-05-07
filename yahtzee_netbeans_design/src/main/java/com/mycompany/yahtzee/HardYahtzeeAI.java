package com.mycompany.yahtzee;

import java.util.*;

/*
 Hard AI - Adds strategic awareness and filtering:
 Prioritizes upper section bonus (needs 63+ for 35 point bonus)
 Uses strategic filters for category selection
 Reduced trial counts for faster decisions
 Considers game position and remaining categories
 */

public class HardYahtzeeAI implements YahtzeeAI{
    private static final int TRIALS = 1000; 
    private static final Category[] PLAYABLE = {
        Category.ONES, Category.TWOS, Category.THREES,
        Category.FOURS, Category.FIVES, Category.SIXES,
        Category.THREE_OF_A_KIND, Category.FOUR_OF_A_KIND,
        Category.FULL_HOUSE, Category.SMALL_STRAIGHT,
        Category.LARGE_STRAIGHT, Category.EVEN, Category.ODD,
        Category.YAHTZEE, Category.CHANCE
    };
    public Set<Integer> chooseDiceToKeep(Dice[] currentDice, ScoreCard scoreCard, int rollsLeft) {
        // Quick filters to speed up decisions
        if (rollsLeft == 0) return new HashSet<>();
        
        // Filter 1: If we have Yahtzee, keep it!
        Set<Integer> yahtzeeKeep = checkForYahtzee(currentDice);
        if (yahtzeeKeep != null && !scoreCard.isCategoryFilled(Category.YAHTZEE)) {
            return yahtzeeKeep;
        }
        
        // Filter 2: If we have 4 of a kind, keep them
        Set<Integer> fourKindKeep = checkForFourOfKind(currentDice);
        if (fourKindKeep != null && fourKindKeep.size() >= 4) {
            // ***
            boolean fourFilled = scoreCard.isCategoryFilled(Category.FOUR_OF_A_KIND);
            boolean yahtzeeFilled = scoreCard.isCategoryFilled(Category.YAHTZEE);
            if (!fourFilled || !yahtzeeFilled) {
                return fourKindKeep;
            }
        }
        
        // Otherwise, evaluate all possibilities
        Set<Integer> bestKeep = new HashSet<>();
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int mask = 0; mask < (1 << 5); mask++) {
            Set<Integer> keepSet = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                if ((mask & (1 << i)) != 0) keepSet.add(i);
            }

            double expected = estimateScore(currentDice, keepSet, scoreCard, rollsLeft);
            if (expected > bestScore) {
                bestScore = expected;
                bestKeep = keepSet;
            }
        }
        return bestKeep;
    }
    
    @Override
    public Category chooseCategory(int[] finalDice, ScoreCard scoreCard) {
        Map<Category, Integer> possible = scoreCard.calculatePossibleScoresint(finalDice);
        Category best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
       

        for (Category c : PLAYABLE) {                         // ← only playable categories
            if (!scoreCard.isCategoryFilled(c)) {
                Integer score = possible.get(c);
                if (score == null) continue;
                double strategicValue = calculateStrategicValue(c, score, scoreCard, possible);
                if (strategicValue > bestValue) {
                    bestValue = strategicValue;
                    best = c;
                }
            }
        }
        return best;
    }

    /**
     * Calculate strategic value with bonus awareness
     */
    private double calculateStrategicValue(Category category, int score, 
                                          ScoreCard scoreCard, 
                                          Map<Category, Integer> allPossible) {
        double value = score;
        
        // Strategic Filter 1: Upper section bonus awareness
        if (isUpperSection(category)) {
            int upperNeeded = getUpperSectionNeeded(scoreCard);
            int upperLeft = getUpperCategoriesLeft(scoreCard);
            
            if (upperLeft > 0 && upperNeeded > 0) {
                double avgNeeded = (double) upperNeeded / upperLeft;
                
                // Boost value if this helps toward bonus
                if (score >= avgNeeded * 0.8) {
                    value += 8; 
                }
                
                // Extra boost if we're close to bonus
                if (upperNeeded <= 20 && score >= 3) {
                    value += 5;
                }
            }
        }
        
        // Strategic Filter 2: Penalize wasting high-value categories
        if (category == Category.YAHTZEE && score == 0)        { value -= 20; }
        if (category == Category.LARGE_STRAIGHT && score == 0) { value -= 15; }
        if (category == Category.FOUR_OF_A_KIND && score == 0) { value -= 15; }
        if (category == Category.FULL_HOUSE && score == 0)     { value -= 12; }
        if (category == Category.THREE_OF_A_KIND && score == 0){ value -= 10; }

        // added to prefer zeroing four of kind instead of 3
        if (category == Category.FOUR_OF_A_KIND && score > 0) {
            Integer threeScore = allPossible.get(Category.THREE_OF_A_KIND);
            if (threeScore != null && score >= threeScore) {
                value += 6;
            }
        }
        
        // Strategic Filter 3: Late game - grab points when available
        int remainingCategories = 0;
        for (Category c : PLAYABLE) {
            if (!scoreCard.isCategoryFilled(c)) remainingCategories++;
        }
        
        if (remainingCategories <= 5) {
            // Late game: be more aggressive about taking points
            value += score * 0.1;
        }
        
        // Strategic Filter 4: Avoid burning "dump" categories too early
        if (remainingCategories > 8 && (category == Category.ONES || category == Category.TWOS)) {
            if (score < 3) {
                value -= 5; // Keep dump categories for later bad rolls
            }
        }
        
        return value;
    }

    private double estimateScore(Dice[] currentDice, Set<Integer> keep, 
                                ScoreCard scoreCard, int rollsLeft) {
        double total = 0;

       for (int t = 0; t < TRIALS; t++) {
            Dice[] diceCopy = new Dice[5];
            for (int i = 0; i < 5; i++) {
                diceCopy[i] = currentDice[i].copy();
            }
            
            // Simulate remaining rolls
            for (int r = 0; r < rollsLeft; r++) {
                for (int i = 0; i < 5; i++) {
                    if (!keep.contains(i)) {
                        diceCopy[i].roll();
                    }
                }
            }

            // Find best category with strategic value
            int[] values = diceToIntArray(diceCopy);
            Map<Category, Integer> possible = scoreCard.calculatePossibleScoresint(values);
            double bestScore = 0;
            
            for (Category c : PLAYABLE) {                     // ← only playable categories
                if (!scoreCard.isCategoryFilled(c)) {
                    Integer s = possible.get(c);
                    if (s == null) continue;
                    double sv = calculateStrategicValue(c, s, scoreCard, possible);
                    bestScore = Math.max(bestScore, sv);
                }
            }
            total += bestScore;
        }
        return (double) total / TRIALS;
    }

    // Quick filters for common patterns
    private Set<Integer> checkForYahtzee(Dice[] dice) {
        int[] counts = new int[7];
        for (Dice d : dice) counts[d.getValue()]++;
        
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) {
                Set<Integer> keep = new HashSet<>();
                for (int j = 0; j < 5; j++) keep.add(j);
                return keep;
            }
        }
        return null;
    }

    private Set<Integer> checkForFourOfKind(Dice[] dice) {
        int[] counts = new int[7];
        Map<Integer, List<Integer>> positions = new HashMap<>();
        
        for (int i = 0; i < 5; i++) {
            int val = dice[i].getValue();
            counts[val]++;
            positions.computeIfAbsent(val, k -> new ArrayList<>()).add(i);
        }
        
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 4) {
                return new HashSet<>(positions.get(i));
            }
        }
        return null;
    }

    private boolean isUpperSection(Category category) {
        return category == Category.ONES || category == Category.TWOS ||
               category == Category.THREES || category == Category.FOURS ||
               category == Category.FIVES || category == Category.SIXES;
    }

    private int getUpperSectionNeeded(ScoreCard scoreCard) {
       Map<Category, Integer> scores = scoreCard.getScores();
        int currentUpper = 0;
        for (Category c : new Category[]{
                Category.ONES, Category.TWOS, Category.THREES,
                Category.FOURS, Category.FIVES, Category.SIXES}) {
            Integer v = scores.get(c);
            if (v != null) currentUpper += v;
        }
        return Math.max(0, 63 - currentUpper);
    }

    private int getUpperCategoriesLeft(ScoreCard scoreCard) {
        int count = 0;
        Category[] upper = {Category.ONES, Category.TWOS, Category.THREES,
                           Category.FOURS, Category.FIVES, Category.SIXES};
        
        for (Category c : upper) {
            if (!scoreCard.isCategoryFilled(c)) count++;
        }
        return count;
    }
    
    private int[] diceToIntArray(Dice[] dice) {
    int[] values = new int[dice.length];
    for (int i = 0; i < dice.length; i++) {
        values[i] = dice[i].getValue();
    }
    return values;
}

}