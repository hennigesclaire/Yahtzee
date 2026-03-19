/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;
/**
 *
 * @author henni
 */
import javax.swing.*;
import java.awt.*;

public class DiceComponent extends JButton {
    private int value = 1;
    private boolean held = false;
    private float spinPhase = 1.0f; // 1.0 = normal, shrinks toward 0 then snaps back

    public DiceComponent() {
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setOpaque(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
    }

    public void setValue(int v) { value = v; repaint(); }
    public int getValue() { return value; }

    public void setHeld(boolean h) { held = h; repaint(); }
    public boolean isHeld() { return held; }

    public void setSpinPhase(float phase) { spinPhase = phase; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Orange glow border when held
        if (held) {
            g2.setColor(new Color(255, 140, 0, 80));
            g2.fillRoundRect(-4, -4, w + 8, h + 8, 26, 26);
            g2.setColor(new Color(255, 140, 0));
            g2.setStroke(new BasicStroke(3.5f));
            g2.drawRoundRect(-3, -3, w + 6, h + 6, 24, 24);
        }

        // Apply horizontal squish for spin animation
        g2.translate(w / 2.0, h / 2.0);
        g2.scale(spinPhase, 1.0);
        g2.translate(-w / 2.0, -h / 2.0);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w, h, 20, 20);
        g2.setColor(held ? new Color(255, 140, 0) : new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(held ? 2.5f : 1.5f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, 20, 20);

        if (value > 0) {
            int pipSize = Math.min(w, h) / 6;
            int left = w / 4, centerX = w / 2, right = 3 * w / 4;
            int top = h / 4, centerY = h / 2, bottom = 3 * h / 4;
            g2.setColor(Color.BLACK);
            switch (value) {
                case 1: drawPip(g2, centerX, centerY, pipSize); break;
                case 2: drawPip(g2, left, top, pipSize); drawPip(g2, right, bottom, pipSize); break;
                case 3: drawPip(g2, left, top, pipSize); drawPip(g2, centerX, centerY, pipSize); drawPip(g2, right, bottom, pipSize); break;
                case 4: drawPip(g2, left, top, pipSize); drawPip(g2, right, top, pipSize); drawPip(g2, left, bottom, pipSize); drawPip(g2, right, bottom, pipSize); break;
                case 5: drawPip(g2, left, top, pipSize); drawPip(g2, right, top, pipSize); drawPip(g2, centerX, centerY, pipSize); drawPip(g2, left, bottom, pipSize); drawPip(g2, right, bottom, pipSize); break;
                case 6: drawPip(g2, left, top, pipSize); drawPip(g2, left, centerY, pipSize); drawPip(g2, left, bottom, pipSize); drawPip(g2, right, top, pipSize); drawPip(g2, right, centerY, pipSize); drawPip(g2, right, bottom, pipSize); break;
            }
        }
        g2.dispose();
    }

    private void drawPip(Graphics2D g2, int x, int y, int size) {
        g2.fillOval(x - size / 2, y - size / 2, size, size);
    }
}