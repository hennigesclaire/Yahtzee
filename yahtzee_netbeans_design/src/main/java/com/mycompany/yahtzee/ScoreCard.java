package com.mycompany.yahtzee;

import java.util.*;

public class ScoreCard {
    private Map<Category, Integer> scores = new LinkedHashMap<>(); 

    public ScoreCard() {
        for (Category c : Category.values()) {
            scores.put(c, null);
        }
    }

    public Map<Category, Integer> calculatePossibleScoresint(int[] diceValues) {
    Map<Category, Integer> result = new LinkedHashMap<>();
    int[] counts = new int[7]; 

    for (int val : diceValues) {
        counts[val]++;
    }

    result.put(Category.ONES, counts[1] * 1);
    result.put(Category.TWOS, counts[2] * 2);
    result.put(Category.THREES, counts[3] * 3);
    result.put(Category.FOURS, counts[4] * 4);
    result.put(Category.FIVES, counts[5] * 5);
    result.put(Category.SIXES, counts[6] * 6);
    
    int totalSum = 0;
    for (int v : diceValues) totalSum += v;

    result.put(Category.THREE_OF_A_KIND, hasCount(counts, 3) ? totalSum : 0);
    result.put(Category.FOUR_OF_A_KIND, hasCount(counts, 4) ? totalSum : 0);
    result.put(Category.FULL_HOUSE, isFullHouse(counts) ? 25 : 0);
    result.put(Category.SMALL_STRAIGHT, hasStraight(counts, 4) ? 30 : 0);
    result.put(Category.LARGE_STRAIGHT, hasStraight(counts, 5) ? 40 : 0);
    result.put(Category.YAHTZEE, hasCount(counts, 5) ? 50 : 0);
    result.put(Category.CHANCE, totalSum);

    return result;
}

    public Map<Category, Integer> calculatePossibleScores(Dice[] dice) {
    Map<Category, Integer> result = new LinkedHashMap<>();
    int[] counts = new int[7]; 

    for (Dice d : dice) {
        counts[d.getValue()]++;
    }

    result.put(Category.ONES, counts[1] * 1);
    result.put(Category.TWOS, counts[2] * 2);
    result.put(Category.THREES, counts[3] * 3);
    result.put(Category.FOURS, counts[4] * 4);
    result.put(Category.FIVES, counts[5] * 5);
    result.put(Category.SIXES, counts[6] * 6);

    result.put(Category.THREE_OF_A_KIND,
        hasCount(counts, 3) ? sum(dice) : 0);

    result.put(Category.FOUR_OF_A_KIND,
        hasCount(counts, 4) ? sum(dice) : 0);

    result.put(Category.FULL_HOUSE,
         isFullHouse(counts) ? 25 : 0);


    result.put(Category.SMALL_STRAIGHT,
        hasStraight(counts, 4) ? 30 : 0);

    result.put(Category.LARGE_STRAIGHT,
        hasStraight(counts, 5) ? 40 : 0);

    result.put(Category.EVEN,
        (counts[2] + counts[4] + counts[6] == 5) ? sum(dice) : 0);

    result.put(Category.ODD,
        (counts[1] + counts[3] + counts[5] == 5) ? sum(dice) : 0);

    result.put(Category.YAHTZEE,
        hasCount(counts, 5) ? 50 : 0);

    result.put(Category.CHANCE, sum(dice));

    return result;
}
    
    private int sumIntArray(int[] dice) {
    int total = 0;
    for (int d : dice) total += d;
    return total;
}

    private boolean hasCount(int[] counts, int n) {
        for (int c : counts) {
            if (c >= n) return true;
        }
        return false;
    }

    private boolean hasStraight(int[] counts, int length) {
        int consecutive = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] > 0) {
                consecutive++;
                if (consecutive >= length) return true;
            } else {
                consecutive = 0;
            }
        }
        return false;
    }
    private boolean isFullHouse(int[] counts) {
    boolean hasThree = false;
    boolean hasTwo = false;

    for (int c : counts) {
        if (c == 3) hasThree = true;
        if (c == 2) hasTwo = true;
    }

    return hasThree && hasTwo;
}

    private int sum(Dice[] dice) {
        int total = 0;
        for (Dice d : dice) {
            total += d.getValue();
        }
        return total;
    }

public void fillCategory(Category category, Dice[] dice) {
    if (category == Category.UPPER_SCORE ||
        category == Category.BONUS ||
        category == Category.LOWER_SCORE) {
        return;  
    }

    Map<Category, Integer> possibleScores = calculatePossibleScores(dice);
    int score = possibleScores.get(category);

    scores.put(category, score);

    updateTotals();
}


public void updateTotals() {
    int upper = 0;
    upper += getOrZero(Category.ONES);
    upper += getOrZero(Category.TWOS);
    upper += getOrZero(Category.THREES);
    upper += getOrZero(Category.FOURS);
    upper += getOrZero(Category.FIVES);
    upper += getOrZero(Category.SIXES);

    int bonus = (upper >= 63) ? 35 : 0;

    int lower = 0;
    lower += getOrZero(Category.THREE_OF_A_KIND);
    lower += getOrZero(Category.FOUR_OF_A_KIND);
    lower += getOrZero(Category.FULL_HOUSE);
    lower += getOrZero(Category.SMALL_STRAIGHT);
    lower += getOrZero(Category.LARGE_STRAIGHT);
    lower += getOrZero(Category.EVEN);
    lower += getOrZero(Category.ODD);
    lower += getOrZero(Category.YAHTZEE);
    lower += getOrZero(Category.CHANCE);
    
    int total =0;
    total += getOrZero(Category.ONES);
    total += getOrZero(Category.TWOS);
    total += getOrZero(Category.THREES);
    total += getOrZero(Category.FOURS);
    total += getOrZero(Category.FIVES);
    total += getOrZero(Category.SIXES);
    total += getOrZero(Category.THREE_OF_A_KIND);
    total += getOrZero(Category.FOUR_OF_A_KIND);
    total += getOrZero(Category.FULL_HOUSE);
    total += getOrZero(Category.SMALL_STRAIGHT);
    total += getOrZero(Category.LARGE_STRAIGHT);
    total += getOrZero(Category.EVEN);
    total += getOrZero(Category.ODD);
    total += getOrZero(Category.YAHTZEE);
    total += getOrZero(Category.CHANCE);
    total += getOrZero(Category.BONUS);

    scores.put(Category.UPPER_SCORE, upper);
    scores.put(Category.BONUS, bonus);
    scores.put(Category.LOWER_SCORE, lower);
    scores.put(Category.TOTAL, total);
}

private int getOrZero(Category c) {
    return scores.get(c) == null ? 0 : scores.get(c);
}


    public void assignScore(int choice, Map<Category, Integer> possibleScores) {
        Category c = Category.values()[choice - 1];
        if (scores.get(c) != null) {
            System.out.println("That category is already filled!");
            return;
        }
        scores.put(c, possibleScores.get(c));
    }

    public boolean isFull() {
        for (Category c : Category.values()) {
            if (scores.get(c) == null) { 
                return false;
            }
        }
        return true;
    }
    public Map<Category, Integer> getScores() {
    return scores;
}
 public boolean isCategoryFilled(Category c) {
    return scores.get(c) != null;  
}
public int getTotalScore() {
    int upper = getOrZero(Category.UPPER_SCORE);
    int bonus = getOrZero(Category.BONUS);
    int lower = getOrZero(Category.LOWER_SCORE);
    return upper + bonus + lower;
}



}
