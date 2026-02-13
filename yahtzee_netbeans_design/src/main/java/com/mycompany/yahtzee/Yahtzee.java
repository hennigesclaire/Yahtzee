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
        System.out.println(t.getCurrentPlayer());

        StartPageDesign startPage = new StartPageDesign(tm);
        startPage.setVisible(true);
    }
}
