package com.mycompany.yahtzee;

import java.util.*;

public class ScoreCard {
    private Map<Category, Integer> scores = new LinkedHashMap<>(); 

    public ScoreCard() {
        for (Category c : Category.values()) {
            scores.put(c, null);
        }
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
    if (isCategoryFilled(category)) {
        System.out.println(category + " is already filled!");
        return;
    }

    Map<Category, Integer> possibleScores = calculatePossibleScores(dice);

    int score = possibleScores.get(category);

    scores.put(category, score);

    System.out.println("Filled " + category + " with " + score + " points.");
}

    public void FinalScoreCard(){
        int i = 1;
        for (Category c : scores.keySet()) {
            Integer assigned = scores.get(c);
            System.out.printf("%2d) %-20s \033[1m%d\033[0m\n", i, c, assigned);
            i++;
        }
        int upper = scores.get(Category.ONES)
           + scores.get(Category.TWOS)
           + scores.get(Category.THREES)
           + scores.get(Category.FOURS)
           + scores.get(Category.FIVES)
           + scores.get(Category.SIXES);
        int lower = scores.get(Category.THREE_OF_A_KIND)
           + scores.get(Category.FOUR_OF_A_KIND)
           + scores.get(Category.FULL_HOUSE)
           + scores.get(Category.SMALL_STRAIGHT)
           + scores.get(Category.LARGE_STRAIGHT)
           + scores.get(Category.EVEN)
           + scores.get(Category.ODD)
           + scores.get(Category.YAHTZEE)
           + scores.get(Category.CHANCE);
        int bonus = (upper >= 63) ? 35 : 0;
        System.out.print("Upper Score:") ;
        System.out.println(upper);
        System.out.print("Bonus:") ;
        System.out.println(bonus);
        System.out.print("Lower Score:") ;
        System.out.println(lower);
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


}
