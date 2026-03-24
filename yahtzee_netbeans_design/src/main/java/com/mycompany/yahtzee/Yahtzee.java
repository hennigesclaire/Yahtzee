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
    javax.swing.SwingUtilities.invokeLater(() -> {
        StartPageDesign startPage = new StartPageDesign();
        startPage.setVisible(true);
    });
    }
}