/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

import java.util.ArrayList;

/**
 *
 * @author joshu
 */
public class Interface {
    private int[] diceValues;
    private ScoreCard scores;
    private String username;
    private ArrayList<Player> players = new ArrayList<>();
    
    public Interface(TurnManager t)
    {
        for(int i = 0; i < t.getCount(); i++)
        {
            players.add(t.nextPlayer());
            System.out.println(players);
        }
        diceValues = new int[5];
    }
    
    public void setDiceValues(int[] v)
    {
        for(int i = 0; i < 5; i++)
        {
            diceValues[i] = v[i];
        }
    }
    
    public int[] getDiceValues()
    {
        return diceValues;
    }
    
    public void setScoreCard(ScoreCard sc)
    {
        this.scores = sc;
    }
    
    public void setName(String s)
    {
        this.username = s;
    }
    
    
}
