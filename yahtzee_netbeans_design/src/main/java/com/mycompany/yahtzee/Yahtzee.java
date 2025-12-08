/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */

import java.awt.Font;
import java.util.Random;
public class Yahtzee {

    public static void main(String[] args) {
        TurnManager t = new TurnManager();
        Player p = new Player("Prototype");
        t.addPlayer(p);
        System.out.println(t.getCurrentPlayer());
        TurnManager tm = new TurnManager();

        EndPage startPage = new EndPage(69);
        startPage.setVisible(true);


        
        
    }
}
