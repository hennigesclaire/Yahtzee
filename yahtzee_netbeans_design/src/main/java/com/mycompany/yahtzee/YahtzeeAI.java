/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author nifey
 */
import java.util.Set;

public interface YahtzeeAI {
    Set<Integer> chooseDiceToKeep(Dice[] dice, ScoreCard scoreCard, int rollsLeft);
    Category chooseCategory(int[] finalDice, ScoreCard scoreCard);
}
