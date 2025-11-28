/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */
public class AIPlayer {
    
    private int difficulty;
    Player p = new Player("AI Player");
    
    
    public AIPlayer(int d)
    {
        this.setDifficulty(d);
    }
    
    private void setDifficulty(int d)
    {
        difficulty = d;
    }
}
