/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.yahtzee;

/**
 *
 * @author joshu
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

public class Yahtzee {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            StartPageDesign startPage = new StartPageDesign();
            startPage.setExtendedState(JFrame.MAXIMIZED_BOTH);
            startPage.setVisible(true);
        });
    }
}
