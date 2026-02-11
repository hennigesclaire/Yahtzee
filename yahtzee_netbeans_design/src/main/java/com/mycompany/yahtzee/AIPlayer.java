/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */
public class AIPlayer extends Player {
    private YahtzeeAI strategy; 

    public AIPlayer(int id) {
        super("AI Player " + id);
    }

    public void setStrategy(YahtzeeAI strategy) {
        this.strategy = strategy;
    }

    public YahtzeeAI getStrategy() {
        return strategy;
    }
}
