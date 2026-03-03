/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */
public class TurnManager {
    private CircularLinkedList<Player> playerList = new CircularLinkedList<>();
    private int turn = 0;
    private int rolls = 0;
    private Player currentPlayer;
    private int count = 0;
    private Interface inter = null;
    private CircularLinkedList<ScoreCard> a = new CircularLinkedList<>();
    
    public TurnManager()
    {
        currentPlayer = null;
    }
    
    public Player getCurrentPlayer()
    {
        currentPlayer = this.playerList.first();
        return this.currentPlayer;
    }
    
    public Player nextPlayer()
    {
        this.a.rotate();
        return this.playerList.next();
    }
    
    public void addPlayer(Player p)
    {
        this.playerList.addLast(p);
        this.a.addLast(new ScoreCard());
        System.out.println(p);
        this.count++;

    }
    
    public Player removePlayer(Player p)
    {
        CircularLinkedList<Player> temp = new CircularLinkedList<>();
        CircularLinkedList<ScoreCard> temp2 = new CircularLinkedList<>();
        Player removedPlayer = new Player("");
        while(!this.playerList.isEmpty())
        {
            Player q = this.playerList.removeFirst();
            ScoreCard s = this.a.removeFirst();
            if(!p.getUsername().equals(q.getUsername()))
            {
                temp.addLast(q);
                temp2.addLast(s);
            }
            else removedPlayer = q;
        }
        this.playerList = temp;
        temp = null;
        this.a = temp2;
        temp2 = null;
        this.count--;
        return removedPlayer;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public void removeRoll()
    {
        this.rolls--;
    }
    
    public void resetRolls()
    {
        this.rolls = 3;
    }
    
    public int getRolls()
    {
        return this.rolls;
    }
    
    public void printCurrent()
    {
        System.out.println(getCurrentPlayer());
        this.nextPlayer();
    }
    
    public void createInterface()
    {
        inter = new Interface(this);
    }
    
    public void updateInterface(Dice[] d) // Dice
    {
        int[] temp = new int[5];
        for(int i = 0; i < 5; i++)
        {
            temp[i] = d[i].getValue();
        }
        inter.setDiceValues(temp);
    }
    
    public int[] getDiceFromInterface()
    {
        return inter.getDiceValues();
    }
   
    public ScoreCard getScoreCard()
    {
        return a.first();
    }
    
    public boolean completeGame()
    {
        boolean temp = true;
        for(int i = 0; i < count; i++)
        {
            temp = a.first().isFull();
            this.a.rotate();
            if(!temp) break;
        }
        return temp;
    }
    
}
