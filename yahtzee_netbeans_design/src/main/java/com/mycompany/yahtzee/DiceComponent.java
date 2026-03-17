/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;

import javax.swing.*;
import java.awt.*;

public class DiceComponent extends JButton {

    private int value = 1;

    public DiceComponent() {
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setOpaque(false);          
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    public void setValue(int v) {
        value = v;
        repaint();
    }

    public int getValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w, h, 20, 20);

        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, 20, 20);


        int pipSize = Math.min(w, h) / 6;

        int left = w / 4;
        int centerX = w / 2;
        int right = 3 * w / 4;

        int top = h / 4;
        int centerY = h / 2;
        int bottom = 3 * h / 4;

        g2.setColor(Color.BLACK);

        switch (value) {
            case 1:
                drawPip(g2, centerX, centerY, pipSize);
                break;

            case 2:
                drawPip(g2, left, top, pipSize);
                drawPip(g2, right, bottom, pipSize);
                break;

            case 3:
                drawPip(g2, left, top, pipSize);
                drawPip(g2, centerX, centerY, pipSize);
                drawPip(g2, right, bottom, pipSize);
                break;

            case 4:
                drawPip(g2, left, top, pipSize);
                drawPip(g2, right, top, pipSize);
                drawPip(g2, left, bottom, pipSize);
                drawPip(g2, right, bottom, pipSize);
                break;

            case 5:
                drawPip(g2, left, top, pipSize);
                drawPip(g2, right, top, pipSize);
                drawPip(g2, centerX, centerY, pipSize);
                drawPip(g2, left, bottom, pipSize);
                drawPip(g2, right, bottom, pipSize);
                break;

            case 6:
                drawPip(g2, left, top, pipSize);
                drawPip(g2, left, centerY, pipSize);
                drawPip(g2, left, bottom, pipSize);
                drawPip(g2, right, top, pipSize);
                drawPip(g2, right, centerY, pipSize);
                drawPip(g2, right, bottom, pipSize);
                break;
        }

        g2.dispose();
    }

    private void drawPip(Graphics2D g2, int x, int y, int size) {
        g2.fillOval(x - size / 2, y - size / 2, size, size);
    }
}