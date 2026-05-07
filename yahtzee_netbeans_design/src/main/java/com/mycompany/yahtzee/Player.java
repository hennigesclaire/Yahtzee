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
    private boolean complete;
    
    
    public Player(String n)
    {
        name = n;
        rolls = 3;
        keptDice = new boolean[5];
        scores = new ScoreCard();
        complete = false;
    }
   
     public Dice[] rollDice() {
        Dice[] ds = new Dice[5];
        for (int i = 0; i < ds.length; i++) {
            ds[i] = new Dice();
            ds[i].roll();
        }
        return ds;
    }
        
    public void resetTurn(){
        rolls = 3; 
        keptDice = new boolean[5];
    }
    public ScoreCard getScore()
    {
        return scores;
    }
    
    public String getUsername()
    {
        return name;
    }

    public void setUsername(String newName)
    {
        name = newName;
    }
    public int getRollsLeft(){
        return rolls;
    }
    public void useRoll(){
        if (rolls >0) rolls--;
    }
    public boolean[] keptDice(){
        return keptDice;
    }
   public ScoreCard getScoreCard(){
       return scores;
   }
}
