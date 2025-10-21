/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.yahtzee;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author henni
 */


public class ScoreCellRenderer extends DefaultTableCellRenderer {
    private final ScoreCard scoreCard;

    public ScoreCellRenderer(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel cell = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);

        Category category = Category.values()[row];

        if (scoreCard.isCategoryFilled(category)) {
            cell.setFont(cell.getFont().deriveFont(Font.BOLD));
        } else {
            cell.setFont(cell.getFont().deriveFont(Font.PLAIN));
        }

        cell.setHorizontalAlignment(SwingConstants.CENTER);

        return cell;
    }
}

