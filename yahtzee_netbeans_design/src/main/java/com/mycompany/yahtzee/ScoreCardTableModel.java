/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yahtzee;
import javax.swing.table.AbstractTableModel;
import java.util.*;


public class ScoreCardTableModel extends AbstractTableModel {
    private final ScoreCard scoreCard;
    private final String[] columnNames = {"Category", "Score"};
    private Map<Category, Integer> possibleScores = new HashMap<>();


    public ScoreCardTableModel(ScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    @Override
    public int getRowCount() {
        return Category.values().length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Category c = Category.values()[rowIndex];
        switch (columnIndex) {
            case 0: return c.name();
            case 1: 
               if (scoreCard.isCategoryFilled(c)) {
                return scoreCard.getScores().get(c);
            } else {
                return possibleScores.getOrDefault(c, 0);
            }
            default: return "";
        }
    }
    public Category getCategoryAt(int rowIndex) {
        return Category.values()[rowIndex];
    }
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    public void setPossibleScores(Map<Category, Integer> scores) {
    this.possibleScores = scores;
    fireTableDataChanged(); 

    }
    
}
