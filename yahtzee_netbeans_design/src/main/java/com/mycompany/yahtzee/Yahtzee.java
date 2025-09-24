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
        YahtzeeDesign g = new YahtzeeDesign();
        g.setVisible(true);
    }
}

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
}