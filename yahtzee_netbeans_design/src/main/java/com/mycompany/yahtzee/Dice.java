/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package com.mycompany.yahtzee;

import java.util.Random;

/**
 *
 * @author joshu
 */
class Dice{
    private int value;

    public Dice() {
        this.value = 0;
    }

    public int roll() {
        Random rand = new Random();
        this.value = rand.nextInt(6) + 1;
        return value;
    }

    public int getValue() {
        return value;
    }
    
      public void setValue(int value) {
        this.value = value;
    }
 
    // Let's AI safely simulate rolls without mutating live dice
    public Dice copy() {
        Dice d = new Dice();
        d.setValue(this.value);
        return d;
    }
}