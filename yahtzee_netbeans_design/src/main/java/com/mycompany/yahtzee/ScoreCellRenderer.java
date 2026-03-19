/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author henni
 */

package com.mycompany.yahtzee;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class ScoreCellRenderer extends DefaultTableCellRenderer {

    private final ScoreCard scoreCard;

    private static final Color BG_BASE      = new Color(255, 248, 220);
    private static final Color BG_HOVER     = new Color(255, 200, 80);
    private static final Color BG_FILLED    = new Color(255, 220, 125);

    private static final Color FG_FILLED    = new Color(100, 60, 0);
    private static final Color FG_POSSIBLE  = new Color(180, 100, 0);
    private static final Color FG_EMPTY     = new Color(160, 140, 100);
    private static final Color FG_CATEGORY  = new Color(80, 40, 0);

    private static final Color BORDER_COLOR = Color.BLACK;

    private int hoveredRow = -1;

    public ScoreCellRenderer(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
        setOpaque(false); // we paint everything ourselves
    }

    public void setHoveredRow(int row) {
        this.hoveredRow = row;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        ScoreCardTableModel model = (ScoreCardTableModel) table.getModel();
        Category category = model.getCategoryAt(row);

        boolean filled   = scoreCard.isCategoryFilled(category);
        boolean hovered  = (row == hoveredRow);
        boolean possible = !filled && value != null && !value.toString().isEmpty();

        // Background
        if (hovered && !filled) {
            setBackground(BG_HOVER);
        } else if (filled) {
            setBackground(BG_FILLED);
        } else {
            setBackground(BG_BASE);
        }

        // Font scaling
        int rowH = table.getRowHeight();
        int fontSize = (int)(rowH * 0.9f);
        setFont(new Font("Bauhaus 93",
                filled ? Font.BOLD : Font.PLAIN,
                fontSize));

        // Foreground
        if (column == 0) {
            setForeground(FG_CATEGORY);
        } else if (filled) {
            setForeground(FG_FILLED);
        } else if (possible) {
            setForeground(FG_POSSIBLE);
        } else {
            setForeground(FG_EMPTY);
        }

        // Alignment
        setHorizontalAlignment(column == 0 ? LEFT : CENTER);

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Bottom border
        g2.setColor(BORDER_COLOR);
        g2.fillRect(0, getHeight() - 1, getWidth(), 1);

        // Text anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(getFont());
        g2.setColor(getForeground());

        FontMetrics fm = g2.getFontMetrics();
        String text = getText();

        // Perfect vertical centering (ignores descent padding)
        int ascent = fm.getAscent();
        int y = (getHeight() - ascent) / 2 + ascent;

        // Horizontal alignment
        int x;
        int textWidth = fm.stringWidth(text);

        switch (getHorizontalAlignment()) {
            case CENTER:
                x = (getWidth() - textWidth) / 2;
                break;
            case RIGHT:
                x = getWidth() - textWidth - 4;
                break;
            default:
                x = 4;
                break;
        }

        g2.drawString(text, x, y);
        g2.dispose();
    }
}