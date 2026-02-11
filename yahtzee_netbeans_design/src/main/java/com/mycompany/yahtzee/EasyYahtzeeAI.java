package com.mycompany.yahtzee;

import java.util.*;

// I plan for this to be the easy AI version. 
// It's weakness is that it cannot plan ahead and due to it short sightedness, will always choose the higher point total
// I will add a filter, add strategic awareness, and make add filters on trail counts so it can be faster with certain decisions
// I'll use the Monte Carlo Tree Search to simulate a harder AI if need be. 

public class EasyYahtzeeAI implements YahtzeeAI {
    public Set<Integer> chooseDiceToKeep(Dice[] currentDice, ScoreCard scoreCard, int rollsLeft) {
        Set<Integer> bestKeep = new HashSet<>();
        double bestScore = Double.NEGATIVE_INFINITY;

        // Trying all possibilities(2^5) of dice
        for (int mask = 0; mask < (1 << 5); mask++) {
            Set<Integer> keepSet = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                if ((mask & (1 << i)) != 0) keepSet.add(i);
            }

            // Estimate expected score if we keep these dice
            double expected = estimateScore(currentDice, keepSet, scoreCard, rollsLeft);
            if (expected > bestScore) {
                bestScore = expected;
                bestKeep = keepSet;
            }
        }
        return bestKeep;
    }

    //Choose the best scoring category.
     
    @Override
    public Category chooseCategory(int[] finalDice, ScoreCard scoreCard) {
        Map<Category,Integer> possible = scoreCard.calculatePossibleScoresint(finalDice);
        Category best = null;
        int bestVal = -1;

        for (var entry : possible.entrySet()) {
            if (!scoreCard.isCategoryFilled(entry.getKey()) && entry.getValue() > bestVal) {
                bestVal = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    /*
     Monte Carlo in action: This simulates many random completions of the turn
      and does the decision making. 
     */
    private double estimateScore(Dice[] currentDice,
                                 Set<Integer> keep,
                                 ScoreCard scoreCard,
                                 int rollsLeft) {
        int trials = 2000;//The higher the number, the higher the accuracy
        int total = 0;

        for (int t = 0; t < trials; t++) {
            Dice[] diceCopy = Arrays.copyOf(currentDice, 5);

            // roll remaining dice
            for (int r = 0; r < rollsLeft; r++) {
                for (int i = 0; i < 5; i++) {
                    if (!keep.contains(i)) diceCopy[i].roll();
                }
            }

            // Evaluating best score per category
            Map<Category,Integer> possible = scoreCard.calculatePossibleScores(diceCopy);
            int best = possible.values().stream().max(Integer::compareTo).orElse(0);
            total += best;
        }
        return (double) total / trials;
    }
}
