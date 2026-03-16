/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.yahtzee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.awt.geom.GeneralPath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author henni
 */
public class YahtzeeDesign extends javax.swing.JFrame {

    Dice[] dice = {new Dice(), new Dice(), new Dice(), new Dice(), new Dice()};
    private Font diceFont = null;
    private void loadDiceFont() {
        try {
            InputStream is = YahtzeeDesign.class
                .getResourceAsStream("/fonts/yahtzee-dice.ttf");

            if (is == null) {
                throw new RuntimeException("Dice font not found in resources");
            }

            diceFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception ex) {
            ex.printStackTrace();
            diceFont = new Font("Serif", Font.PLAIN, 36);
        }
    }
    boolean[] holding = {false, false, false, false, false};
    private ScoreCard scoreCard;
    private ScoreCardTableModel upperModel;
    private ScoreCardTableModel lowerModel;
    private Dice[] currentRoll;
    private int rollCount  = 0;
    private boolean  turnActive = true;
    private Category pendingCategory = null;
    private TurnManager t = null;

    public static final Category[] UPPER = {
        Category.ONES,
        Category.TWOS,
        Category.THREES,
        Category.FOURS,
        Category.FIVES,
        Category.SIXES,
        Category.BONUS,
        Category.UPPER_SCORE
    };
    public static final Category[] LOWER = {
        Category.THREE_OF_A_KIND,
        Category.FOUR_OF_A_KIND,
        Category.FULL_HOUSE,
        Category.SMALL_STRAIGHT,
        Category.LARGE_STRAIGHT,
        Category.EVEN, Category.ODD,
        Category.YAHTZEE,
        Category.CHANCE,
        Category.LOWER_SCORE
    };

    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel UsersName;
    private RoundedButton jButton1;   // Roll
    private RoundedButton jButton2;   // End Game
    private javax.swing.JPanel jPanel1;    // dice strip
    private DiceComponent die1, die2, die3, die4, die5;
    private javax.swing.JLabel die1Label, die2Label, die3Label, die4Label, die5Label;
    private GlassPanel jPanel2;    // scorecard
    private javax.swing.JScrollPane jScrollPane1, jScrollPane2;
    private javax.swing.JTable jTable1, jTable2;
    private javax.swing.JLabel UpperSectionLabel, LowerSectionLabel, Total;

    private Font uiFont(float size) {
        return new Font("Bauhaus 93", Font.BOLD, (int) size);
    }

    public YahtzeeDesign(TurnManager t) {
        this.t = t;
        t.resetRolls();
        loadDiceFont();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        buildComponents();
        wireScoreCard();
        checkAndPlayAITurn();

        jLayeredPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { layoutComponents(); }
        });
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { layoutComponents(); }
            @Override public void componentShown(java.awt.event.ComponentEvent e)   { layoutComponents(); }
        });
        SwingUtilities.invokeLater(this::layoutComponents);
    }

    private void buildComponents() {
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane1.setLayout(null);
        jLayeredPane1.setOpaque(true);
        jLayeredPane1.setBackground(new Color(250, 235, 137));

        GameFramePanel frameArch = new GameFramePanel();
        frameArch.setOpaque(false);
        SwingUtilities.invokeLater(() -> {
            frameArch.setBounds(0, 0, jLayeredPane1.getWidth(), jLayeredPane1.getHeight());
            jLayeredPane1.add(frameArch, Integer.valueOf(-1));
            jLayeredPane1.repaint();
        });
        jLayeredPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                frameArch.setBounds(0, 0, jLayeredPane1.getWidth(), jLayeredPane1.getHeight());
                frameArch.repaint();
            }
        });

        UsersName = new javax.swing.JLabel("Turn: Player 1");
        UsersName.setFont(uiFont(28f));
        UsersName.setForeground(Color.BLACK);
        jLayeredPane1.add(UsersName);

        jButton1 = new RoundedButton("Roll");
        jButton1.setFont(uiFont(30f));
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));
        jLayeredPane1.add(jButton1);

        jButton2 = new RoundedButton("End Game");
        jButton2.setFont(uiFont(22f));
        jButton2.addActionListener(e -> jButton2ActionPerformed(e));
        jLayeredPane1.add(jButton2);

        jPanel1 = new javax.swing.JPanel(null);
        jPanel1.setOpaque(false);

        die1 = makeDieButton(); die2 = makeDieButton();
        die3 = makeDieButton(); die4 = makeDieButton(); die5 = makeDieButton();

        die1Label = makeDieLabel(); die2Label = makeDieLabel();
        die3Label = makeDieLabel(); die4Label = makeDieLabel(); die5Label = makeDieLabel();

        die1.addActionListener(e -> die1ActionPerformed(e));
        die2.addActionListener(e -> die2ActionPerformed(e));
        die3.addActionListener(e -> die3ActionPerformed(e));
        die4.addActionListener(e -> die4ActionPerformed(e));
        die5.addActionListener(e -> die5ActionPerformed(e));

        for (DiceComponent b : new DiceComponent[]{die1, die2, die3, die4, die5})
            jPanel1.add(b);

        for (javax.swing.JLabel l : new javax.swing.JLabel[]{die1Label, die2Label, die3Label, die4Label, die5Label})
            jPanel1.add(l);
        jLayeredPane1.add(jPanel1);

        jPanel2 = new GlassPanel();
        jPanel2.setLayout(null);

        UpperSectionLabel = new javax.swing.JLabel("Upper Section");
        UpperSectionLabel.setFont(uiFont(18f));

        jTable1 = makeTable();
        jScrollPane1 = makeScrollPane(jTable1);

        LowerSectionLabel = new javax.swing.JLabel("Lower Section");
        LowerSectionLabel.setFont(uiFont(18f));

        jTable2 = makeTable();
        jScrollPane2 = makeScrollPane(jTable2);

        Total = new javax.swing.JLabel("Total: 0");
        Total.setFont(uiFont(22f));

        jPanel2.add(UpperSectionLabel);
        jPanel2.add(jScrollPane1);
        jPanel2.add(LowerSectionLabel);
        jPanel2.add(jScrollPane2);
        jPanel2.add(Total);
        jLayeredPane1.add(jPanel2);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jLayeredPane1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private DiceComponent makeDieButton() {
        DiceComponent b = new DiceComponent();
        b.setBackground(Color.WHITE);
        return b;
    }
    private javax.swing.JLabel makeDieLabel() {
        javax.swing.JLabel l = new javax.swing.JLabel("");
        l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        l.setFont(uiFont(12f));
        return l;
    }
    private javax.swing.JTable makeTable() {
        javax.swing.JTable t = new javax.swing.JTable();
        t.setBackground(new Color(255, 255, 220));
        t.setGridColor(new Color(200, 180, 100));
        t.setRowHeight(26);
        t.setShowGrid(true);
        return t;
    }
    private javax.swing.JScrollPane makeScrollPane(javax.swing.JTable table) {
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(table);
        sp.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 150, 60), 1));
        sp.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sp.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    private void layoutComponents() {
        int w = jLayeredPane1.getWidth();
        int h = jLayeredPane1.getHeight();
        if (w == 0 || h == 0) return;


        int border = 110;
        int innerLeft = border;
        int innerTop = border;
        int innerRight = w - border;
        int innerBottom = h - border;

        int diceStripH = (int)(h * 0.20);
        int diceStripY = innerBottom - diceStripH + (int)(h * 0.04);

        int dividerX = innerLeft + (int)((innerRight - innerLeft) * 0.30);
        int leftW  = dividerX - innerLeft - 8;

        int nameH = (int)(h * 0.07);
        UsersName.setFont(uiFont(Math.max(16f, h * 0.028f)));
        UsersName.setBounds(innerLeft, innerTop, leftW, nameH);

        int endH = (int)(h * 0.065);
        int endW = (int)(leftW * 0.80);
        int endX = innerLeft + (leftW - endW) / 2;
        int midY = innerTop + nameH + (diceStripY - innerTop - nameH) / 2;
        int endY = midY - endH / 2;
        jButton2.setBounds(endX, endY, endW, endH);
        jButton2.setFont(uiFont(Math.max(13f, h * 0.024f)));

        int scX = dividerX + 8;
        int scY = innerTop;
        int scW = innerRight - scX;
        int scH = diceStripY - innerTop - 6;
        jPanel2.setBounds(scX, scY, scW, scH);

        int pad = 10;
        int labelH = Math.max(20, (int)(scH * 0.043));
        int totalLH = Math.max(22, (int)(scH * 0.055));
        int gap = Math.max(4,  (int)(scH * 0.018));
        int available = scH - labelH * 2 - totalLH - gap - pad * 3;
        int upperH = (available * UPPER.length) / (UPPER.length + LOWER.length);
        int lowerH = available - upperH;

        int cy = pad;
        UpperSectionLabel.setFont(uiFont(Math.max(12f, labelH * 0.72f)));
        UpperSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH);
        cy += labelH + 2;

        jScrollPane1.setBounds(pad, cy, scW - pad * 2, upperH);
        jTable1.setRowHeight(Math.max(16, upperH / (UPPER.length + 1)));
        cy += upperH + gap;

        LowerSectionLabel.setFont(uiFont(Math.max(12f, labelH * 0.72f)));
        LowerSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH);
        cy += labelH + 2;

        jScrollPane2.setBounds(pad, cy, scW - pad * 2, lowerH);
        jTable2.setRowHeight(Math.max(16, lowerH / (LOWER.length + 1)));
        cy += lowerH + 4;

        Total.setFont(uiFont(Math.max(13f, totalLH * 0.65f)));
        Total.setBounds(pad, cy, scW - pad * 2, totalLH);

        int stripW = innerRight - innerLeft;
        jPanel1.setBounds(innerLeft, diceStripY, stripW, diceStripH);

        int diceZoneX = dividerX - innerLeft + 8;
        int diceZoneW = stripW - diceZoneX;
        int diceSize  = (int)(diceStripH * 0.68);
        int spacing   = diceZoneW / 5;
        int diceOffX  = (spacing - diceSize) / 2;
        int labelHt   = (int)(diceStripH * 0.17);
        int labelY    = (diceStripH - diceSize - labelHt - 2) / 2;
        int diceY2    = labelY + labelHt + 2;

        javax.swing.JButton[] dBtns   = {die1, die2, die3, die4, die5};
        javax.swing.JLabel[]  dLabels = {die1Label, die2Label, die3Label, die4Label, die5Label};
        for (int i = 0; i < 5; i++) {
            int slotX = diceZoneX + spacing * i + diceOffX;
            dLabels[i].setBounds(slotX, labelY, diceSize, labelHt);
            dLabels[i].setFont(uiFont(Math.max(8f, diceStripH * 0.10f)));
            dBtns[i].setBounds(slotX, diceY2, diceSize, diceSize);
            // Dice dot font — rescale to button size so dots render correctly
            if (diceFont != null) {
                dBtns[i].setFont(diceFont.deriveFont((float) diceSize * 0.60f));            }
        }

        int rollH = (int)(diceStripH * 0.52);
        int rollW = (int)(leftW * 0.80);
        int rollY = (int)(diceY2 *(15.7)); 
        jButton1.setBounds(endX, rollY, rollW, rollH);
        jButton1.setFont(uiFont(Math.max(14f, rollH * 0.38f)));

        jLayeredPane1.revalidate();
        jLayeredPane1.repaint();
    }

    private void wireScoreCard() {
        scoreCard  = t.getScoreCard();
        upperModel = new ScoreCardTableModel(scoreCard, UPPER);
        lowerModel = new ScoreCardTableModel(scoreCard, LOWER);
        jTable1.setModel(upperModel);
        jTable2.setModel(lowerModel);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));

        this.t.createInterface();
        UsersName.setText(this.t.getCurrentPlayer().getUsername());

        jTable1.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint());
                int col = jTable1.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive) {
                    Category sel = upperModel.getCategoryAt(row);
                    if (pendingCategory == null || !pendingCategory.equals(sel)) {
                        pendingCategory = sel;
                    } else {
                        finalizeCategorySelection(sel);
                        Total.setText("Total: " + scoreCard.getTotalScore());
                        pendingCategory = null;
                        turnActive = false;
                    }
                }
            }
        });

        jTable2.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable2.rowAtPoint(e.getPoint());
                int col = jTable2.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive) {
                    Category sel = lowerModel.getCategoryAt(row);
                    if (pendingCategory == null || !pendingCategory.equals(sel)) {
                        pendingCategory = sel;
                    } else {
                        finalizeCategorySelection(sel);
                        Total.setText("Total: " + scoreCard.getTotalScore());
                        pendingCategory = null;
                        turnActive = false;
                    }
                }
            }
        });
    }

    public int[] getDiceValues() {
        int[] vals = new int[5];
        for (int i = 0; i < 5; i++) vals[i] = t.getDiceFromInterface()[i];
        return vals;
    }

    private void updateHoldLabels() {
    
        die5Label.setText((holding[0]) ? "KEEPING" : "ROLLING");
        die3Label.setText((holding[1]) ? "KEEPING" : "ROLLING");
        die1Label.setText((holding[2]) ? "KEEPING" : "ROLLING");
        die4Label.setText((holding[3]) ? "KEEPING" : "ROLLING");
        die2Label.setText((holding[4]) ? "KEEPING" : "ROLLING");
    }

    private void checkAndPlayAITurn() {
        Player current = t.getCurrentPlayer();
        UsersName.setText("Turn: " + current.getUsername());
        if (current instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) current;
            jButton1.setEnabled(false);
            die1.setEnabled(false); die2.setEnabled(false); die3.setEnabled(false);
            die4.setEnabled(false); die5.setEnabled(false);
            new Thread(() -> {
                try {
                    YahtzeeAI brain = ai.getStrategy();
                    Thread.sleep(1000);
                    java.awt.EventQueue.invokeLater(this::performManualRoll);
                    while (t.getRolls() > 0) {
                        Thread.sleep(1500);
                        int rollsRemaining = t.getRolls();
                        java.util.Set<Integer> toKeep = brain.chooseDiceToKeep(dice, scoreCard, rollsRemaining);
                        if (toKeep.size() == 5) break;
                        for (int j = 0; j < 5; j++) holding[j] = toKeep.contains(j);
                        java.awt.EventQueue.invokeLater(() -> { updateHoldLabels(); performManualRoll(); });
                        t.removeRoll();
                    }
                    Thread.sleep(1500);
                    int[] currentVals = t.getDiceFromInterface();
                    Category choice = brain.chooseCategory(currentVals, scoreCard);
                    java.awt.EventQueue.invokeLater(() -> finalizeCategorySelection(choice));
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        } else {
            jButton1.setEnabled(true);
            die1.setEnabled(true); die2.setEnabled(true); die3.setEnabled(true);
            die4.setEnabled(true); die5.setEnabled(true);
            turnActive = true;
        }
    }

    private void finalizeCategorySelection(Category selectedCategory) {
        if (scoreCard.isCategoryFilled(selectedCategory)) {
            if (!(t.getCurrentPlayer() instanceof AIPlayer))
                JOptionPane.showMessageDialog(this, "That category is already filled!");
            return;
        }
        scoreCard.fillCategory(selectedCategory, dice);
        upperModel.fireTableDataChanged();
        lowerModel.fireTableDataChanged();
        turnActive = false;
        rollCount  = 0;
        Arrays.fill(holding, false);
        DiceComponent[] diceButtons = {die1, die2, die3, die4, die5};
        javax.swing.JLabel[]  labels      = {die1Label, die2Label, die3Label, die4Label, die5Label};
        for (int i = 0; i < diceButtons.length; i++) { 
            diceButtons[i].setValue(1);
            labels[i].setText(""); }
        if (t.completeGame()) {
            int i = scoreCard.getTotalScore();
            EndPage ep = new EndPage(i);
            ep.setVisible(true);
            jButton1.setEnabled(false);
            turnActive = false;
        } else {
            pendingCategory = null;
            t.nextPlayer();
            jTable1.clearSelection(); jTable2.clearSelection();
            jButton1.setEnabled(true);
            scoreCard  = t.getScoreCard();
            upperModel = new ScoreCardTableModel(scoreCard, UPPER);
            lowerModel = new ScoreCardTableModel(scoreCard, LOWER);
            jTable1.setModel(upperModel); jTable2.setModel(lowerModel);
            jTable1.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));
            jTable2.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));
            t.resetRolls();
            checkAndPlayAITurn();
        }
    }

    private void performManualRoll() { jButton1ActionPerformed(null); }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!turnActive && t.getRolls() > 0) turnActive = true;
        if (!turnActive) return;
        DiceComponent[] j = {die1, die2, die3, die4, die5};
        for (int i = 0; i < dice.length; i++) {
            if (!holding[i]) {
                dice[i].roll();
                t.updateInterface(dice);
                j[i].setValue(t.getDiceFromInterface()[i]);
            }
            currentRoll = Arrays.copyOf(dice, dice.length);
            Map<Category, Integer> possibleScores = scoreCard.calculatePossibleScores(dice);
            upperModel.setPossibleScores(possibleScores);
            lowerModel.setPossibleScores(possibleScores);
        }
        t.removeRoll();
        if (t.getRolls() == 0) jButton1.setEnabled(false);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        EndPage end = new EndPage(1);
        end.setVisible(true);
        this.dispose();
    }

    private void die1ActionPerformed(java.awt.event.ActionEvent evt) { holding[0] = !holding[0]; die1Label.setText(holding[0] ? "KEEPING" : "ROLLING"); }
    private void die2ActionPerformed(java.awt.event.ActionEvent evt) { holding[1] = !holding[1]; die2Label.setText(holding[1] ? "KEEPING" : "ROLLING"); }
    private void die3ActionPerformed(java.awt.event.ActionEvent evt) { holding[2] = !holding[2]; die3Label.setText(holding[2] ? "KEEPING" : "ROLLING"); }
    private void die4ActionPerformed(java.awt.event.ActionEvent evt) { holding[3] = !holding[3]; die4Label.setText(holding[3] ? "KEEPING" : "ROLLING"); }
    private void die5ActionPerformed(java.awt.event.ActionEvent evt) { holding[4] = !holding[4]; die5Label.setText(holding[4] ? "KEEPING" : "ROLLING"); }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) { javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; }
        } catch (Exception ex) { Logger.getLogger(YahtzeeDesign.class.getName()).log(Level.SEVERE, null, ex); }
        TurnManager t = new TurnManager();
        java.awt.EventQueue.invokeLater(() -> new YahtzeeDesign(t).setVisible(true));
    }
}


class GlassPanelScoreCard  extends JPanel {

    public GlassPanelScoreCard() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 30;
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setColor(new Color(200, 120, 30, 220));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
class GameFramePanel extends JPanel {

    private final Color YELLOW       = new Color(247, 201, 72);
    private final Color LIGHT_ORANGE = new Color(255, 170, 60);
    private final Color DARK_ORANGE  = new Color(230, 120, 40);
    private final Color INTERIOR = new Color(250, 235, 137);

    public GameFramePanel() { setOpaque(false); }

    private GeneralPath roundedRect(int W, int H, int offset, int radius) {
        int x = offset, y = offset;
        int x2 = W - offset, y2 = H - offset;
        int r = Math.min(radius, Math.min((x2 - x) / 2, (y2 - y) / 2));
        GeneralPath p = new GeneralPath();
        p.moveTo(x + r, y);
        p.lineTo(x2 - r, y);
        p.quadTo(x2, y,  x2, y + r);
        p.lineTo(x2, y2 - r);
        p.quadTo(x2, y2, x2 - r, y2);
        p.lineTo(x + r, y2);
        p.quadTo(x,  y2, x,  y2 - r);
        p.lineTo(x,  y + r);
        p.quadTo(x,  y,  x + r, y);
        p.closePath();
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0,0,w,h,15,15);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0,0,w-1,h-1,15,15);

        int baseOffset = 30;
        int baseRadius = 90;     
        int[] strokeW  = {46, 36, 26};
        Color[] colors = {YELLOW, LIGHT_ORANGE, DARK_ORANGE};
        int outline = 6;  
        int gap  = 2;       

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);

        int innerOff = baseOffset + strokeW[0] / 2 + outline / 2 + gap * 2 + 5;
        g2.setColor(INTERIOR);
        g2.fill(roundedRect(w, h, innerOff, Math.max(8, baseRadius - gap * 2)));

        int stripeSpacing = 14; 

        for (int i = 0; i < 3; i++) {
            int off = baseOffset + i * stripeSpacing;
            int rad = baseRadius;

            GeneralPath path = roundedRect(w, h, off, rad);

            g2.setStroke(new BasicStroke(strokeW[i] + outline,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.BLACK);
            g2.draw(path);

            g2.setStroke(new BasicStroke(strokeW[i],
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(colors[i]);
            g2.draw(path);
        }
        g2.dispose();
    }
}