/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */
public class Player {
    private String name;
    private int rolls;
    private boolean[] keptDice;
    private ScoreCard scores;
    
    
    public Player(String n)
    {
        name = n;
        rolls = 3;
        keptDice = new boolean[5];
        scores = new ScoreCard();
    }
    
    public Dice[] rollDice()
    {
        Dice[] ds = new Dice[5];
        for (Dice d : ds) {
            d.roll();
        }
        return ds;
    }
    
    public ScoreCard getScore()
    {
        return scores;
    }
    
    public String getUsername()
    {
        return name;
    }
}
