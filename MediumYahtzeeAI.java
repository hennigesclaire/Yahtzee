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
    private final Random random = new Random();
    private static final int TRIALS = 1000;
    
    
    
    //Metho d to handle the int/Dice conversion issue I encountered
    private Dice[] convertToDiceObjects(int[] values) 
    {
        Dice[] diceArr = new Dice[values.length];

        for (int i = 0; i < values.length; i++) 
        {
            Dice d = new Dice();   
            d.setValue(values[i]); 
            diceArr[i] = d;
        }

        return diceArr;
    }


    public Set<Integer> chooseDiceToKeep(int[] diceValues, ScoreCard scoreCard, int rollsLeft) {
         // Quick filters to speed up decisions
        if (rollsLeft == 0) return Collections.emptySet();

        // Filter 1: If we have Yahtzee, keep it!
        Set<Integer> yahtzeeKeep = checkForYahtzee(diceValues);
        if (yahtzeeKeep != null && !scoreCard.isCategoryFilled(Category.YAHTZEE)) {
            return yahtzeeKeep;
        }

        // Filter 2: If we have 4 of a kind, keep them
        Set<Integer> fourKindKeep = checkForFourOfKind(diceValues);
        if (fourKindKeep != null && fourKindKeep.size() >= 4) {
            return fourKindKeep;
        }

        // Otherwise, evaluate all possibilities
        Set<Integer> bestKept = new HashSet<>();
        double bestExpected = Double.NEGATIVE_INFINITY;

        for (int mask = 0; mask < (1 << 5); mask++) {
            Set<Integer> keptIndices = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                if ((mask & (1 << i)) != 0) keptIndices.add(i);
            }

            double expected = estimateExpectedValue(diceValues, keptIndices, scoreCard, rollsLeft);
            if (expected > bestExpected) {
                bestExpected = expected;
                bestKept = keptIndices;
            }
        }
        return bestKept;
    }

    // Choose the best available category for the final dice values.
    // This is what really separaters it from the EasyAI
    
    public Category chooseCategory(int[] finalDice, ScoreCard scoreCard) {
        Dice[] diceObjs = convertToDiceObjects(finalDice);
        Map<Category, Integer> possible = scoreCard.calculatePossibleScores(diceObjs);

        Category best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (var entry : possible.entrySet()) {
            Category category = entry.getKey();
            int rawScore = entry.getValue();

            if (!scoreCard.isCategoryFilled(category)) {
                double strategicValue = calculateStrategicValue(category, rawScore, scoreCard, possible);
                if (strategicValue > bestValue) {
                    bestValue = strategicValue;
                    best = category;
                }
            }
        }
        return best;
    }

    private double calculateStrategicValue(Category category, int score,ScoreCard scoreCard, Map<Category, Integer> allPossible) {
        double value = score;

        // Upper section bonus awareness
        if (isUpperSection(category)) {
            int upperNeeded = getUpperSectionNeeded(scoreCard);
            int upperLeft = getUpperCategoriesLeft(scoreCard);

            if (upperLeft > 0 && upperNeeded > 0) {
                double avgNeeded = (double) upperNeeded / upperLeft;

                if (score >= avgNeeded * 0.8) {
                    value += 8.0;
                }

                if (upperNeeded <= 20 && score >= 3) {
                    value += 5.0;
                }
            }
        }

        // Penalize wasting high-value categories
        if (category == Category.YAHTZEE && score == 0) value -= 20.0;
        if (category == Category.LARGE_STRAIGHT && score == 0) value -= 15.0;
        if (category == Category.FULL_HOUSE && score == 0) value -= 12.0;

        // Late game so more aggressive about collecting points
        int remainingCategories = getRemainingCategoriesCount(scoreCard);
        if (remainingCategories <= 5) value += score * 0.1;

        if (remainingCategories > 8 && (category == Category.ONES || category == Category.TWOS)) {
            if (score < 3) value -= 5.0;
        }

        return value;
    }

    //MC estimation

    private double estimateExpectedValue(int[] currentDice, Set<Integer> keptIndices,
                                         ScoreCard scoreCard, int rollsLeft) {
        double totalValue = 0.0;

        for (int t = 0; t < TRIALS; t++) {
            int[] diceCopy = Arrays.copyOf(currentDice, currentDice.length);

            // Simulate remaining rolls 
            
            for (int r = 0; r < rollsLeft; r++) {
                for (int i = 0; i < 5; i++) {
                    if (!keptIndices.contains(i)) {
                        diceCopy[i] = random.nextInt(6) + 1;
                    }
                }
            }

            // Evaluate best category strategic value for simulated final dice
            
            Dice[] diceObj = convertToDiceObjects(diceCopy);
            Map<Category, Integer> possible = scoreCard.calculatePossibleScores(diceObj);

            double bestSimValue = Double.NEGATIVE_INFINITY;

            for (var entry : possible.entrySet()) {
                Category category = entry.getKey();
                int rawScore = entry.getValue();

                if (!scoreCard.isCategoryFilled(category)) {
                    double strategicValue = calculateStrategicValue(category, rawScore, scoreCard, possible);
                    bestSimValue = Math.max(bestSimValue, strategicValue);
                }
            }

            // If no category available, treat as 0
            if (bestSimValue == Double.NEGATIVE_INFINITY) bestSimValue = 0.0;

            totalValue += bestSimValue;
        }

        return totalValue / TRIALS;
    }

    // pattern detection

    private Set<Integer> checkForYahtzee(int[] dice) {
        int[] counts = new int[7];
        for (int d : dice) {
            if (d >= 1 && d <= 6) counts[d]++;
        }

        for (int face = 1; face <= 6; face++) {
            if (counts[face] == 5) {
                Set<Integer> all = new HashSet<>();
                for (int i = 0; i < 5; i++) all.add(i);
                return all;
            }
        }
        return null;
    }

    private Set<Integer> checkForFourOfKind(int[] dice) {
        int[] counts = new int[7];
        Map<Integer, List<Integer>> positions = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            int v = dice[i];
            if (v < 1 || v > 6) continue;
            counts[v]++;
            positions.computeIfAbsent(v, k -> new ArrayList<>()).add(i);
        }

        for (int face = 1; face <= 6; face++) {
            if (counts[face] >= 4) {
                return new HashSet<>(positions.get(face));
            }
        }
        return null;
    }


    private boolean isUpperSection(Category category) {
        return category == Category.ONES || category == Category.TWOS ||
               category == Category.THREES || category == Category.FOURS ||
               category == Category.FIVES || category == Category.SIXES;
    }

    // Returns how many points are still needed to reach the 63-point threshold.
    private int getUpperSectionNeeded(ScoreCard scoreCard) {
        Map<Category, Integer> scores = scoreCard.getScores();
        int currentUpper = 0;
        Category[] upperCats = {
            Category.ONES, Category.TWOS, Category.THREES,
            Category.FOURS, Category.FIVES, Category.SIXES
        };

        for (Category c : upperCats) {
            Integer v = scores.get(c);
            if (v != null) currentUpper += v;
        }

        return Math.max(0, 63 - currentUpper);
    }

    private int getUpperCategoriesLeft(ScoreCard scoreCard) {
        int left = 0;
        Category[] upperCats = {
            Category.ONES, Category.TWOS, Category.THREES,
            Category.FOURS, Category.FIVES, Category.SIXES
        };
        for (Category c : upperCats) {
            if (!scoreCard.isCategoryFilled(c)) left++;
        }
        return left;
    }

    private int getRemainingCategoriesCount(ScoreCard scoreCard) {
        int count = 0;
        for (Category c : Category.values()) {
            if (!scoreCard.isCategoryFilled(c)) count++;
        }
        return count;
    }
}
