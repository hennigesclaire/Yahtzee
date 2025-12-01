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
    
    private int difficulty;
    
    
    public AIPlayer(int d)
    {
        super("AI Player");
        this.difficulty = d;
    }
    
    private int getDifficulty()
    {
        return difficulty;
    }
    
}
