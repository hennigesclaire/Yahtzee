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
    
    public TurnManager()
    {
    
    }
    
    public Player nextPlayer()
    {
        return this.playerList.next();
    }
    
    public void addPlayer(Player p)
    {
        this.playerList.addLast(p);
    }
    
    public Player removePlayer(Player p)
    {
        CircularLinkedList<Player> temp = new CircularLinkedList<>();
        Player removedPlayer = new Player("");
        while(!this.playerList.isEmpty())
        {
            Player q = this.playerList.removeFirst();
            if(!p.getUsername().equals(q.getUsername()))
            {
                temp.addLast(q);
            }
            else removedPlayer = q;
        }
        this.playerList = temp;
        temp = null;
        return removedPlayer;
    }
    
    public void removeRoll()
    {
        this.rolls--;
    }
    
    public void resetRolls()
    {
        this.rolls = 3;
    }
    
    public void updateInterface()
    {
        //This is used to update the game interface for each player. Currently, this isn't possible.
    }
    
}
