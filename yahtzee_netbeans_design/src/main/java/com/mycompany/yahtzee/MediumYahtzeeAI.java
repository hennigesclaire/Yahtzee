package com.mycompany.yahtzee;

import java.util.*;

/*
 Medium AI - Adds strategic awareness and filtering:
 Prioritizes upper section bonus (needs 63+ for 35 point bonus)
 Uses strategic filters for category selection
 Reduced trial counts for faster decisions
 Considers game position and remaining categories
 */
public class MediumYahtzeeAI {
    private final Random rand = new Random();
    private static final int TRIALS = 1000; 

    public Set<Integer> chooseDiceToKeep(int[] currentDice, ScoreCard scoreCard, int rollsLeft) {
        // Quick filters to speed up decisions
        if (rollsLeft == 0) return new HashSet<>();
        
        // Filter 1: If we have Yahtzee, keep it!
        Set<Integer> yahtzeeKeep = checkForYahtzee(currentDice);
        if (yahtzeeKeep != null && !scoreCard.isCategoryTaken(Category.YAHTZEE)) {
            return yahtzeeKeep;
        }
        
        // Filter 2: If we have 4 of a kind, keep them
        Set<Integer> fourKindKeep = checkForFourOfKind(currentDice);
        if (fourKindKeep != null && fourKindKeep.size() == 4) {
            return fourKindKeep;
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

    public Category chooseCategory(int[] finalDice, ScoreCard scoreCard) {
        Map<Category, Integer> possible = scoreCard.calculatePossibleScores(finalDice);
        Category best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (var entry : possible.entrySet()) {
            if (!scoreCard.isCategoryTaken(entry.getKey())) {
                // Apply strategic value calculation
                double strategicValue = calculateStrategicValue(
                    entry.getKey(), 
                    entry.getValue(), 
                    scoreCard,
                    possible
                );
                
                if (strategicValue > bestValue) {
                    bestValue = strategicValue;
                    best = entry.getKey();
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
                    value += 8; // Incentivize working toward bonus
                }
                
                // Extra boost if we're close to bonus
                if (upperNeeded <= 20 && score >= 3) {
                    value += 5;
                }
            }
        }
        
        // Strategic Filter 2: Penalize wasting high-value categories
        if (category == Category.YAHTZEE && score == 0) {
            value -= 20; // Heavy penalty for zeroing Yahtzee
        }
        if (category == Category.LARGE_STRAIGHT && score == 0) {
            value -= 15;
        }
        if (category == Category.FULL_HOUSE && score == 0) {
            value -= 12;
        }
        
        // Strategic Filter 3: Late game - grab points when available
        int remainingCategories = 0;
        for (Category c : Category.values()) {
            if (!scoreCard.isCategoryTaken(c)) remainingCategories++;
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

    private double estimateScore(int[] currentDice, Set<Integer> keep, 
                                ScoreCard scoreCard, int rollsLeft) {
        int total = 0;

        for (int t = 0; t < TRIALS; t++) {
            int[] diceCopy = Arrays.copyOf(currentDice, 5);

            // Simulate remaining rolls
            for (int r = 0; r < rollsLeft; r++) {
                for (int i = 0; i < 5; i++) {
                    if (!keep.contains(i)) {
                        diceCopy[i] = rand.nextInt(6) + 1;
                    }
                }
            }

            // Find best category with strategic value
            Map<Category, Integer> possible = scoreCard.calculatePossibleScores(diceCopy);
            double bestScore = 0;
            
            for (var entry : possible.entrySet()) {
                if (!scoreCard.isCategoryTaken(entry.getKey())) {
                    double strategicValue = calculateStrategicValue(
                        entry.getKey(), 
                        entry.getValue(), 
                        scoreCard,
                        possible
                    );
                    bestScore = Math.max(bestScore, strategicValue);
                }
            }
            
            total += bestScore;
        }
        return (double) total / TRIALS;
    }

    // Quick filters for common patterns
    private Set<Integer> checkForYahtzee(int[] dice) {
        int[] counts = new int[7];
        for (int d : dice) counts[d]++;
        
        for (int i = 1; i <= 6; i++) {
            if (counts[i] == 5) {
                Set<Integer> keep = new HashSet<>();
                for (int j = 0; j < 5; j++) keep.add(j);
                return keep;
            }
        }
        return null;
    }

    private Set<Integer> checkForFourOfKind(int[] dice) {
        int[] counts = new int[7];
        Map<Integer, List<Integer>> positions = new HashMap<>();
        
        for (int i = 0; i < 5; i++) {
            int val = dice[i];
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
        // Returns how many more points needed for 63 (bonus threshold)
        int total = 0;
        Category[] upper = {Category.ONES, Category.TWOS, Category.THREES,
                           Category.FOURS, Category.FIVES, Category.SIXES};
        
        for (Category c : upper) {
            if (scoreCard.isCategoryTaken(c)) {
                // We need access to actual scores - for now estimate
                total += 10; // This is a limitation without scoreCard.getScore()
            }
        }
        
        return Math.max(0, 63 - total);
    }

    private int getUpperCategoriesLeft(ScoreCard scoreCard) {
        int count = 0;
        Category[] upper = {Category.ONES, Category.TWOS, Category.THREES,
                           Category.FOURS, Category.FIVES, Category.SIXES};
        
        for (Category c : upper) {
            if (!scoreCard.isCategoryTaken(c)) count++;
        }
        return count;
    }
}