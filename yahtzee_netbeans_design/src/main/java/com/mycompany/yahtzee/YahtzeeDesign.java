/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package com.mycompany.yahtzee;
/**
 *
 * @author henni
 */

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


public class YahtzeeDesign extends javax.swing.JFrame {

    Dice[] dice = {new Dice(), new Dice(), new Dice(), new Dice(), new Dice()};
    private Font diceFont = null;
    private void loadDiceFont() {
        try {
            InputStream is = YahtzeeDesign.class.getResourceAsStream("/fonts/yahtzee-dice.ttf");
            if (is == null) throw new RuntimeException("Dice font not found");
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
    private boolean turnActive = true;
    private Category pendingCategory = null;
    private TurnManager t = null;

    public static final Category[] UPPER = {
        Category.ONES, Category.TWOS, Category.THREES, Category.FOURS,
        Category.FIVES, Category.SIXES
    };
    public static final Category[] LOWER = {
        Category.THREE_OF_A_KIND, Category.FOUR_OF_A_KIND, Category.FULL_HOUSE,
        Category.SMALL_STRAIGHT, Category.LARGE_STRAIGHT,
        Category.EVEN, Category.ODD, Category.YAHTZEE,
        Category.CHANCE
    };

    private javax.swing.JLayeredPane jLayeredPane1;

    private GlassPanel leaderboardPanel;
    private javax.swing.JLabel leaderboardTitle;
    private javax.swing.JPanel leaderboardList;   // holds player rows

    private GlassPanel jPanel2;
    private javax.swing.JLabel UpperSectionLabel, LowerSectionLabel;
    private javax.swing.JScrollPane jScrollPane1, jScrollPane2;
    private javax.swing.JTable jTable1, jTable2;

    private GlassPanel summaryPanel;
    private javax.swing.JLabel summaryPlayerName;
    private javax.swing.JLabel summaryUpperLabel, summaryUpperVal;
    private javax.swing.JLabel summaryBonusLabel;
    private BonusProgressBar bonusBar;
    private javax.swing.JLabel summaryBonusVal;        // "+35" or "–"
    private javax.swing.JLabel summaryLowerLabel, summaryLowerVal;
    private javax.swing.JLabel summaryTotalLabel, summaryTotalVal;

    private javax.swing.JPanel jPanel1;
    private DiceComponent die1, die2, die3, die4, die5;
    private javax.swing.JLabel die1Label, die2Label, die3Label, die4Label, die5Label;

    private RoundedButton jButton1;   // Roll
    private RoundedButton jButton2;   // End Game

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

        // Decorative frame (same as before)
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

        leaderboardPanel = new GlassPanel();
        leaderboardPanel.setLayout(null);

        leaderboardTitle = new javax.swing.JLabel("Leaderboard");
        leaderboardTitle.setFont(uiFont(22f));
        leaderboardTitle.setForeground(new Color(180, 30, 30));
        leaderboardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardPanel.add(leaderboardTitle);

        leaderboardList = new javax.swing.JPanel();
        leaderboardList.setOpaque(false);
        leaderboardList.setLayout(new BoxLayout(leaderboardList, BoxLayout.Y_AXIS));
        leaderboardPanel.add(leaderboardList);

        jLayeredPane1.add(leaderboardPanel);

        jPanel2 = new GlassPanel();
        jPanel2.setLayout(null);

        UpperSectionLabel = new javax.swing.JLabel("Upper Section");
        UpperSectionLabel.setFont(uiFont(14f));
        UpperSectionLabel.setForeground(new Color(100, 50, 0));

        jTable1 = makeTable();
        jScrollPane1 = makeScrollPane(jTable1);

        LowerSectionLabel = new javax.swing.JLabel("Lower Section");
        LowerSectionLabel.setFont(uiFont(14f));
        LowerSectionLabel.setForeground(new Color(100, 50, 0));

        jTable2 = makeTable();
        jScrollPane2 = makeScrollPane(jTable2);

        jPanel2.add(UpperSectionLabel);
        jPanel2.add(jScrollPane1);
        jPanel2.add(LowerSectionLabel);
        jPanel2.add(jScrollPane2);
        jLayeredPane1.add(jPanel2);

        summaryPanel = new GlassPanel();
        summaryPanel.setLayout(null);

        summaryPlayerName = new javax.swing.JLabel("Player 1");
        summaryPlayerName.setFont(uiFont(26f));
        summaryPlayerName.setForeground(new Color(180, 30, 30));
        summaryPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
        summaryPanel.add(summaryPlayerName);

        summaryUpperLabel = makeSummaryLabel("Upper Section");
        summaryUpperVal   = makeSummaryValue("0");
        summaryPanel.add(summaryUpperLabel);
        summaryPanel.add(summaryUpperVal);

        summaryBonusLabel = makeSummaryLabel("Bonus");
        summaryBonusVal   = makeSummaryValue("–");
        bonusBar = new BonusProgressBar();
        summaryPanel.add(summaryBonusLabel);
        summaryPanel.add(bonusBar);
        summaryPanel.add(summaryBonusVal);

        summaryLowerLabel = makeSummaryLabel("Lower Section");
        summaryLowerVal   = makeSummaryValue("0");
        summaryPanel.add(summaryLowerLabel);
        summaryPanel.add(summaryLowerVal);

        summaryTotalLabel = makeSummaryLabel("Total");
        summaryTotalLabel.setFont(uiFont(22f));
        summaryTotalLabel.setForeground(new Color(180, 30, 30));
        summaryTotalVal   = makeSummaryValue("0");
        summaryTotalVal.setFont(uiFont(22f));
        summaryTotalVal.setForeground(new Color(180, 30, 30));
        summaryPanel.add(summaryTotalLabel);
        summaryPanel.add(summaryTotalVal);

        jLayeredPane1.add(summaryPanel);

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

        for (DiceComponent b : new DiceComponent[]{die1, die2, die3, die4, die5}) jPanel1.add(b);
        for (javax.swing.JLabel l : new javax.swing.JLabel[]{die1Label, die2Label, die3Label, die4Label, die5Label}) jPanel1.add(l);
        jLayeredPane1.add(jPanel1);

        jButton1 = new RoundedButton("Roll");
        jButton1.setFont(uiFont(30f));
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));
        jLayeredPane1.add(jButton1);

        jButton2 = new RoundedButton("End Game");
        jButton2.setFont(uiFont(20f));
        jButton2.addActionListener(e -> jButton2ActionPerformed(e));
        jLayeredPane1.add(jButton2);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jLayeredPane1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private javax.swing.JLabel makeSummaryLabel(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(uiFont(17f));
        l.setForeground(Color.BLACK);
        return l;
    }
    private javax.swing.JLabel makeSummaryValue(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(uiFont(17f));
        l.setForeground(Color.BLACK);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private void layoutComponents() {
        int W = jLayeredPane1.getWidth();
        int H = jLayeredPane1.getHeight();
        if (W == 0 || H == 0) return;

        int border = 110;
        int iL = border;
        int iT = border;          
        int iR = W - border;
        int iB = H - border;
        int iW = iR - iL;
        int iH = iB - iT;

        int diceStripH = (int)(iH * 0.20);
        int diceStripY = iB - diceStripH + (int)(H * 0.04);
        int contentH   = diceStripY - iT - 6;   // height above dice strip

        // ── Horizontal thirds ─────────────────────────────────────────
        // Left  ~26%  → Leaderboard
        // Center ~34% → Scorecard (thin)
        // Right ~40%  → Player summary
        int lbW  = (int)(iW * 0.20);
        int scW  = (int)(iW * 0.34);
        int sumW = iW - lbW - scW - 16;   

        int lbX  = iL;
        int scX  = lbX + lbW + 8;
        int sumX = scX + scW + 8;

        leaderboardPanel.setBounds(lbX, iT, lbW, contentH);
        int lbPad = 10;
        int titleH = Math.max(28, (int)(contentH * 0.09));
        leaderboardTitle.setFont(uiFont(Math.max(13f, titleH * 0.62f)));
        leaderboardTitle.setBounds(lbPad, lbPad, lbW - lbPad * 2, titleH);
        leaderboardList.setBounds(lbPad, lbPad + titleH + 4, lbW - lbPad * 2, contentH - titleH - lbPad * 3);
        refreshLeaderboard();

        jPanel2.setBounds(scX, iT, scW, contentH);
        {
            int pad     = 8;
            int labelH  = Math.max(18, (int)(contentH * 0.040));
            int gap     = Math.max(3,  (int)(contentH * 0.012));
            int available = contentH - labelH * 2 - gap * 3 - pad * 2;
            int upperRows = UPPER.length + 1;
            int lowerRows = LOWER.length + 1;
            int upperH  = (available * upperRows) / (upperRows + lowerRows);
            int lowerH  = available - upperH;

            int cy = pad;
            UpperSectionLabel.setFont(uiFont(Math.max(10f, labelH * 0.70f)));
            UpperSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH);
            cy += labelH + 2;
            jScrollPane1.setBounds(pad, cy, scW - pad * 2, upperH);
            jTable1.setRowHeight(Math.max(14, upperH / (upperRows)));
            cy += upperH + gap;
            LowerSectionLabel.setFont(uiFont(Math.max(10f, labelH * 0.70f)));
            LowerSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH);
            cy += labelH + 2;
            jScrollPane2.setBounds(pad, cy, scW - pad * 2, lowerH);
            jTable2.setRowHeight(Math.max(14, lowerH / (lowerRows)));
        }

        summaryPanel.setBounds(sumX, iT, sumW, contentH);
        {
            int pad  = 14;
            int nameH = Math.max(30, (int)(contentH * 0.10));
            summaryPlayerName.setFont(uiFont(Math.max(15f, nameH * 0.58f)));
            summaryPlayerName.setBounds(pad, pad, sumW - pad * 2, nameH);


            int rowH   = Math.max(28, (int)(contentH * 0.10));
            int barH   = Math.max(18, (int)(rowH * 0.45));
            int rowGap = Math.max(8,  (int)(contentH * 0.025));
            int rowY   = pad + nameH + rowGap * 2;
            int valX   = sumW - pad - 60;
            int lblW   = valX - pad - 4;

            summaryUpperLabel.setFont(uiFont(Math.max(13f, rowH * 0.55f)));
            summaryUpperLabel.setBounds(pad, rowY, lblW, rowH);
            summaryUpperVal.setFont(uiFont(Math.max(13f, rowH * 0.55f)));
            summaryUpperVal.setBounds(valX, rowY, 60, rowH);
            rowY += rowH + rowGap;

            summaryBonusLabel.setFont(uiFont(Math.max(12f, rowH * 0.50f)));
            summaryBonusLabel.setBounds(pad, rowY, 70, rowH);
            int barX = pad + 75;
            int barW = sumW - barX - pad - 60;
            bonusBar.setBounds(barX, rowY + (rowH - barH) / 2, barW, barH);
            summaryBonusVal.setFont(uiFont(Math.max(12f, rowH * 0.50f)));
            summaryBonusVal.setBounds(valX, rowY, 60, rowH);
            rowY += rowH + rowGap;

            summaryLowerLabel.setFont(uiFont(Math.max(13f, rowH * 0.55f)));
            summaryLowerLabel.setBounds(pad, rowY, lblW, rowH);
            summaryLowerVal.setFont(uiFont(Math.max(13f, rowH * 0.55f)));
            summaryLowerVal.setBounds(valX, rowY, 60, rowH);

            int totalRowY = contentH - pad - rowH;
            summaryTotalLabel.setFont(uiFont(Math.max(15f, rowH * 0.65f)));
            summaryTotalLabel.setBounds(pad, totalRowY, lblW, rowH);
            summaryTotalVal.setFont(uiFont(Math.max(15f, rowH * 0.65f)));
            summaryTotalVal.setBounds(valX, totalRowY, 60, rowH);
        }


        int diceZoneX = scX - iL;       
        int diceZoneW = (sumX + sumW) - scX;
        jPanel1.setBounds(iL, diceStripY, iR - iL, diceStripH);

        int diceSize  = (int)(diceStripH * 0.68);
        int spacing   = diceZoneW / 5;
        int diceOffX  = (spacing - diceSize) / 2;
        int labelHt   = (int)(diceStripH * 0.17);
        int labelYd   = (diceStripH - diceSize - labelHt - 2) / 2;
        int diceY2    = labelYd + labelHt + 2;

        javax.swing.JButton[] dBtns   = {die1, die2, die3, die4, die5};
        javax.swing.JLabel[]  dLabels = {die1Label, die2Label, die3Label, die4Label, die5Label};
        for (int i = 0; i < 5; i++) {
            int slotX = diceZoneX + spacing * i + diceOffX;
            dLabels[i].setBounds(slotX, labelYd, diceSize, labelHt);
            dLabels[i].setFont(uiFont(Math.max(8f, diceStripH * 0.10f)));
            dBtns[i].setBounds(slotX, diceY2, diceSize, diceSize);
            if (diceFont != null) dBtns[i].setFont(diceFont.deriveFont((float) diceSize * 0.60f));
        }

        int btnZoneW = scX - iL - 8;                  
        int btnW     = (int)(btnZoneW * 0.80);
        int btnX     = iL + (btnZoneW - btnW) / 2;

        int rollH    = (int)(diceStripH * 0.52);
        int rollY    = diceStripY + diceY2 + (diceStripH - diceY2 - rollH) / 2;
        jButton1.setBounds(btnX, rollY, btnW, rollH);
        jButton1.setFont(uiFont(Math.max(14f, rollH * 0.38f)));

        int endH = (int)(contentH * 0.13);
        int endY = iT + (contentH - endH) / 2;
        jButton2.setBounds(btnX, endY, btnW, endH);
        jButton2.setFont(uiFont(Math.max(12f, endH * 0.38f)));

        jLayeredPane1.revalidate();
        jLayeredPane1.repaint();
    }


    private void refreshLeaderboard() {
        leaderboardList.removeAll();
        if (t == null) { leaderboardList.revalidate(); leaderboardList.repaint(); return; }

        java.util.List<Player> players = t.getPlayers();
        java.util.List<ScoreCard> cards = t.getScoreCards();

        java.util.List<int[]> indexed = new java.util.ArrayList<>();
        for (int idx = 0; idx < players.size(); idx++) indexed.add(new int[]{idx});
        indexed.sort((ia, ib) -> cards.get(ib[0]).getTotalScore() - cards.get(ia[0]).getTotalScore());

        String[] diceIcons = {"⚀","⚁","⚂","⚃","⚄","⚅"};
        for (int rank = 0; rank < indexed.size(); rank++) {
            int idx = indexed.get(rank)[0];
            Player p = players.get(idx);
            int totalScore = cards.get(idx).getTotalScore();
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            row.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

            JLabel icon = new JLabel(diceIcons[Math.min(rank, 5)]);
            icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
            icon.setForeground(new Color(200, 80, 20));
            icon.setPreferredSize(new Dimension(30, 30));

            JLabel name = new JLabel(p.getUsername());
            name.setFont(uiFont(14f));
            name.setForeground(Color.BLACK);

            JLabel score = new JLabel(String.valueOf(totalScore));
            score.setFont(uiFont(14f));
            score.setForeground(new Color(100, 50, 0));
            score.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(icon,  BorderLayout.WEST);
            row.add(name,  BorderLayout.CENTER);
            row.add(score, BorderLayout.EAST);
            leaderboardList.add(row);
        }
        leaderboardList.revalidate();
        leaderboardList.repaint();
    }


    private void refreshSummary() {
        if (scoreCard == null) return;

        java.util.Map<Category, Integer> s = scoreCard.getScores();
        int upper = orZero(s, Category.UPPER_SCORE);
        int bonus = orZero(s, Category.BONUS);
        int lower = orZero(s, Category.LOWER_SCORE);
        int total = scoreCard.getTotalScore();

        int upperRaw = orZero(s, Category.ONES) + orZero(s, Category.TWOS)
                     + orZero(s, Category.THREES) + orZero(s, Category.FOURS)
                     + orZero(s, Category.FIVES)  + orZero(s, Category.SIXES);

        summaryPlayerName.setText(t.getCurrentPlayer().getUsername());
        summaryUpperVal.setText(String.valueOf(upper));
        bonusBar.setProgress(upperRaw);
        summaryBonusVal.setText(bonus > 0 ? "+35" : "–");
        summaryBonusVal.setForeground(bonus > 0 ? new Color(0, 140, 0) : Color.BLACK);
        summaryLowerVal.setText(String.valueOf(lower));
        summaryTotalVal.setText(String.valueOf(total));

        refreshLeaderboard();
        summaryPanel.repaint();
    }

    /** Null-safe map lookup — treats null (unfilled) as 0. */
    private static int orZero(java.util.Map<Category, Integer> map, Category c) {
        Integer v = map.get(c);
        return v == null ? 0 : v;
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


    private void wireScoreCard() {
        scoreCard  = t.getScoreCard();
        upperModel = new ScoreCardTableModel(scoreCard, UPPER);
        lowerModel = new ScoreCardTableModel(scoreCard, LOWER);
        jTable1.setModel(upperModel);
        jTable2.setModel(lowerModel);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new ScoreCellRenderer(scoreCard));

        this.t.createInterface();
        summaryPlayerName.setText(this.t.getCurrentPlayer().getUsername());
        refreshSummary();

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
                        refreshSummary();
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
                        refreshSummary();
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
        summaryPlayerName.setText("Turn: " + current.getUsername());
        if (current instanceof AIPlayer) {
            AIPlayer ai = (AIPlayer) current;
            jButton1.setEnabled(false);
            die1.setEnabled(false); die2.setEnabled(false); die3.setEnabled(false);
            die4.setEnabled(false); die5.setEnabled(false);
            new Thread(() -> {
                try {
                    YahtzeeAI brain = ai.getStrategy();
                    Thread.sleep(1000);
                    // First roll — jButton1ActionPerformed calls t.removeRoll() internally
                    java.awt.EventQueue.invokeAndWait(this::performManualRoll);
                    while (t.getRolls() > 0) {
                        Thread.sleep(1500);
                        int rollsRemaining = t.getRolls();
                        java.util.Set<Integer> toKeep = brain.chooseDiceToKeep(dice, scoreCard, rollsRemaining);
                        if (toKeep.size() == 5) break;
                        for (int j = 0; j < 5; j++) holding[j] = toKeep.contains(j);
                        // performManualRoll → jButton1ActionPerformed → t.removeRoll() — no extra call needed
                        java.awt.EventQueue.invokeAndWait(() -> { updateHoldLabels(); performManualRoll(); });
                    }
                    Thread.sleep(1500);
                    int[] currentVals = t.getDiceFromInterface();
                    Category choice = brain.chooseCategory(currentVals, scoreCard);
                    java.awt.EventQueue.invokeLater(() -> {
                        finalizeCategorySelection(choice);
                        refreshSummary();   // ensure summary updates after AI scores
                    });
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
        if (selectedCategory == Category.UPPER_SCORE ||
            selectedCategory == Category.BONUS       ||
            selectedCategory == Category.LOWER_SCORE ||
            selectedCategory == Category.TOTAL) {
            System.out.println("WARNING: Tried to finalize auto-category: " + selectedCategory);
            return;
        }
        if (scoreCard.isCategoryFilled(selectedCategory)) {
            if (!(t.getCurrentPlayer() instanceof AIPlayer))
                JOptionPane.showMessageDialog(this, "That category is already filled!");
            return;
        }
        scoreCard.fillCategory(selectedCategory, dice);
        upperModel.fireTableDataChanged();
        lowerModel.fireTableDataChanged();
        refreshSummary();
        turnActive = false;
        rollCount  = 0;
        Arrays.fill(holding, false);
        DiceComponent[] diceButtons = {die1, die2, die3, die4, die5};
        javax.swing.JLabel[] labels = {die1Label, die2Label, die3Label, die4Label, die5Label};
        for (int i = 0; i < diceButtons.length; i++) {
            diceButtons[i].setValue(1);
            labels[i].setText("");
        }
        if (t.completeGame()) {
            int score = scoreCard.getTotalScore();
            EndPage ep = new EndPage(score);
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

            refreshSummary();
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
    private void die4ActionPerformed(java.awt.event.ActionEvent evt) { holding[3] = !holding[3]; die3Label.setText(holding[3] ? "KEEPING" : "ROLLING"); }
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


class BonusProgressBar extends JPanel {
    private static final int BONUS_TARGET = 63;
    private int progress = 0;  

    public BonusProgressBar() { setOpaque(false); }

    public void setProgress(int value) {
        this.progress = Math.min(value, BONUS_TARGET);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int arc = H;

        g2.setColor(new Color(200, 150, 60, 120));
        g2.fillRoundRect(0, 0, W, H, arc, arc);

        int fillW = (int)((double) progress / BONUS_TARGET * W);
        if (fillW > 0) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(220, 100, 20),
                                                  fillW, 0, new Color(255, 210, 50));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, fillW, H, arc, arc);
        }

        g2.setColor(new Color(160, 100, 20, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, W - 1, H - 1, arc, arc);

        g2.setColor(progress >= BONUS_TARGET ? new Color(0, 120, 0) : new Color(80, 40, 0));
        g2.setFont(new Font("Bauhaus 93", Font.BOLD, Math.max(9, H - 4)));
        String txt = progress + " / 63";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (W - fm.stringWidth(txt)) / 2;
        int ty = (H + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(txt, tx, ty);

        g2.dispose();
    }
}



class GameFramePanel extends JPanel {
    private final Color YELLOW       = new Color(247, 201, 72);
    private final Color LIGHT_ORANGE = new Color(255, 170, 60);
    private final Color DARK_ORANGE  = new Color(230, 120, 40);
    private final Color INTERIOR     = new Color(250, 235, 137);

    public GameFramePanel() { setOpaque(false); }

    private java.awt.geom.GeneralPath roundedRect(int W, int H, int offset, int radius) {
        int x = offset, y = offset, x2 = W - offset, y2 = H - offset;
        int r = Math.min(radius, Math.min((x2 - x) / 2, (y2 - y) / 2));
        java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
        p.moveTo(x + r, y);   p.lineTo(x2 - r, y);
        p.quadTo(x2, y, x2, y + r);   p.lineTo(x2, y2 - r);
        p.quadTo(x2, y2, x2 - r, y2); p.lineTo(x + r, y2);
        p.quadTo(x, y2, x, y2 - r);   p.lineTo(x, y + r);
        p.quadTo(x, y, x + r, y);     p.closePath();
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(Color.BLACK); g2.fillRoundRect(0, 0, w, h, 15, 15);
        int baseOffset = 30, baseRadius = 90;
        int[] strokeW = {46, 36, 26};
        Color[] colors = {YELLOW, LIGHT_ORANGE, DARK_ORANGE};
        int gap = 2;
        int innerOff = baseOffset + strokeW[0] / 2 + 6 / 2 + gap * 2 + 5;
        g2.setColor(INTERIOR);
        g2.fill(roundedRect(w, h, innerOff, Math.max(8, baseRadius - gap * 2)));
        int stripeSpacing = 14;
        for (int i = 0; i < 3; i++) {
            int off = baseOffset + i * stripeSpacing;
            java.awt.geom.GeneralPath path = roundedRect(w, h, off, baseRadius);
            g2.setStroke(new BasicStroke(strokeW[i] + 6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.BLACK); g2.draw(path);
            g2.setStroke(new BasicStroke(strokeW[i], BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(colors[i]); g2.draw(path);
        }
        g2.dispose();
    }
}