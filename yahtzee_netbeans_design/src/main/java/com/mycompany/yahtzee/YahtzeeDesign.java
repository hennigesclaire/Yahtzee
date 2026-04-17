/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/*
 *
 * @author henni
 */
package com.mycompany.yahtzee;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import java.util.stream.IntStream;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.FontMetrics;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


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
    private boolean firstRollDone = false;
    private boolean turnActive = true;
    private ScoreCellRenderer upperRenderer;
    private ScoreCellRenderer lowerRenderer;
    private Category pendingCategory = null;
    private TurnManager t = null;
    private java.util.function.Consumer<Float> panelFlipper = p -> {};
    private java.util.Map<Integer, Integer> previousRanks = new java.util.HashMap<>();
    private java.util.Map<Integer, JPanel> existingRows = new java.util.HashMap<>();
    private java.util.Map<Integer, JLabel> scoreLabels = new java.util.HashMap<>();
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
    private javax.swing.JLabel yahtzeeTitle;
    private javax.swing.JLabel leaderboardTitle;
    private javax.swing.JPanel leaderboardList;

    private GlassPanel jPanel2;
    private javax.swing.JLabel UpperSectionLabel, LowerSectionLabel;
    private javax.swing.JScrollPane jScrollPane1, jScrollPane2;
    private javax.swing.JTable jTable1, jTable2;

    private GlassPanel summaryPanel;
    private javax.swing.JLabel summaryPlayerName;
    private javax.swing.JLabel summaryUpperLabel, summaryUpperVal;
    private javax.swing.JLabel summaryBonusLabel;
    private BonusProgressBar bonusBar;
    private javax.swing.JLabel summaryBonusVal;
    private javax.swing.JLabel summaryLowerLabel, summaryLowerVal;
    private javax.swing.JLabel summaryTotalLabel, summaryTotalVal;

    private javax.swing.JPanel jPanel1;
    private DiceComponent die1, die2, die3, die4, die5;
    private javax.swing.JLabel die1Label, die2Label, die3Label, die4Label, die5Label;

    private RoundedButton jButton1;

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

        yahtzeeTitle = new OutlinedLabel("Yahtzee");
        yahtzeeTitle.setFont(new Font("Bauhaus 93", Font.BOLD, 60));
        yahtzeeTitle.setForeground(new Color(180, 20, 20));
        yahtzeeTitle.setHorizontalAlignment(SwingConstants.CENTER);
        jLayeredPane1.add(yahtzeeTitle);

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

        jPanel2 = new GlassPanel() {
            float flipPhase = 1.0f;
            public void setPanelFlipPhase(float p) { flipPhase = p; repaint(); }
            @Override public void paint(java.awt.Graphics g) {
                if (flipPhase >= 1.0f) { super.paint(g); return; }
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.translate(cx, cy); g2.scale(flipPhase, 1.0); g2.translate(-cx, -cy);
                super.paint(g2); g2.dispose();
            }
        };
        panelFlipper = p -> {};
        try {
            java.lang.reflect.Method m = jPanel2.getClass().getMethod("setPanelFlipPhase", float.class);
            panelFlipper = phase -> { try { m.invoke(jPanel2, phase); } catch(Exception ex){} };
        } catch (NoSuchMethodException ex) {}
        jPanel2.setLayout(null);

        summaryPlayerName = new javax.swing.JLabel("Player 1");
        summaryPlayerName.setFont(uiFont(26f));
        summaryPlayerName.setForeground(new Color(180, 30, 30));
        summaryPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel2.add(summaryPlayerName);

        UpperSectionLabel = new javax.swing.JLabel("Upper Section");
        UpperSectionLabel.setFont(uiFont(15f));
        UpperSectionLabel.setForeground(new Color(100, 50, 0));
        jTable1 = makeTable();
        jTable1.setIntercellSpacing(new Dimension(0, 0));
        jScrollPane1 = makeScrollPane(jTable1);

        LowerSectionLabel = new javax.swing.JLabel("Lower Section");
        LowerSectionLabel.setFont(uiFont(15f));
        LowerSectionLabel.setForeground(new Color(100, 50, 0));
        jTable2 = makeTable();
        jTable2.setIntercellSpacing(new Dimension(0, 0));
        jScrollPane2 = makeScrollPane(jTable2);

        jPanel2.add(UpperSectionLabel); jPanel2.add(jScrollPane1);
        jPanel2.add(LowerSectionLabel); jPanel2.add(jScrollPane2);

        summaryUpperLabel = makeSummaryLabel("Upper Section");
        summaryUpperVal   = makeSummaryValue("0");
        jPanel2.add(summaryUpperLabel); jPanel2.add(summaryUpperVal);

        summaryBonusLabel = makeSummaryLabel("Bonus");
        summaryBonusVal   = makeSummaryValue("–");
        bonusBar = new BonusProgressBar();
        jPanel2.add(summaryBonusLabel); jPanel2.add(bonusBar); jPanel2.add(summaryBonusVal);

        summaryLowerLabel = makeSummaryLabel("Lower Section");
        summaryLowerVal   = makeSummaryValue("0");
        jPanel2.add(summaryLowerLabel); jPanel2.add(summaryLowerVal);

        summaryTotalLabel = makeSummaryLabel("Total");
        summaryTotalLabel.setFont(uiFont(22f));
        summaryTotalLabel.setForeground(new Color(180, 30, 30));
        summaryTotalVal = makeSummaryValue("0");
        summaryTotalVal.setFont(uiFont(22f));
        summaryTotalVal.setForeground(new Color(180, 30, 30));
        jPanel2.add(summaryTotalLabel); jPanel2.add(summaryTotalVal);

        jLayeredPane1.add(jPanel2);
        summaryPanel = jPanel2;

        jPanel1 = new javax.swing.JPanel(null);
        jPanel1.setOpaque(false);
        die1 = makeDieButton(); die2 = makeDieButton(); die3 = makeDieButton();
        die4 = makeDieButton(); die5 = makeDieButton();
        die1Label = makeDieLabel(); die2Label = makeDieLabel(); die3Label = makeDieLabel();
        die4Label = makeDieLabel(); die5Label = makeDieLabel();
        die1.addActionListener(e -> die1ActionPerformed(e));
        die2.addActionListener(e -> die2ActionPerformed(e));
        die3.addActionListener(e -> die3ActionPerformed(e));
        die4.addActionListener(e -> die4ActionPerformed(e));
        die5.addActionListener(e -> die5ActionPerformed(e));
        die1.setEnabled(false); die2.setEnabled(false); die3.setEnabled(false);
        die4.setEnabled(false); die5.setEnabled(false);
        for (DiceComponent b : new DiceComponent[]{die1,die2,die3,die4,die5}) jPanel1.add(b);
        for (javax.swing.JLabel l : new javax.swing.JLabel[]{die1Label,die2Label,die3Label,die4Label,die5Label}) jPanel1.add(l);
        jLayeredPane1.add(jPanel1);

        jButton1 = new RoundedButton("Roll");
        jButton1.setFont(uiFont(30f));
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));
        jLayeredPane1.add(jButton1);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jLayeredPane1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private javax.swing.JLabel makeSummaryLabel(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(uiFont(17f)); l.setForeground(Color.BLACK); return l;
    }
    private javax.swing.JLabel makeSummaryValue(String text) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(uiFont(17f)); l.setForeground(Color.BLACK);
        l.setHorizontalAlignment(SwingConstants.RIGHT); return l;
    }

    private void layoutComponents() {
        int W = jLayeredPane1.getWidth(), H = jLayeredPane1.getHeight();
        if (W == 0 || H == 0) return;

        int frameArm = 81, frameBuffer = 40, smallBorder = 28;
        int iL = frameArm + frameBuffer, iT = smallBorder;
        int iR = W - smallBorder, iW = iR - iL, iH = (H - smallBorder) - iT;

        int diceBottomBuffer = smallBorder + 50;
        int diceStripH = (int)(iH * 0.20);
        int diceStripY = H - diceBottomBuffer - diceStripH;
        int contentH   = diceStripY - iT - 10;

        int lbW = (int)(iW * 0.22);
        int btnWCalc = lbW, btnXCalc = iL + (lbW - btnWCalc) / 2, lbX = btnXCalc;
        int combX = iL + (int)(iW * 0.22) + (int)(iW * 0.09);
        int combW = iR - combX;

        int yahtzeeTitleH = Math.max(50, (int)(iH * 0.13));
        float yahtzeeTitleSize = Math.max(28f, yahtzeeTitleH * 0.72f);
        yahtzeeTitle.setFont(new Font("Bauhaus 93", Font.BOLD, (int)yahtzeeTitleSize));
        yahtzeeTitle.setBounds(lbX, iT, lbW, yahtzeeTitleH);

        int lbPad = 10;
        int lbY = iT + yahtzeeTitleH + (int)(iH * 0.18);
        int titleH = Math.max(24, (int)(contentH * 0.08));
        int rowSlotH = Math.max(28, (int)(contentH * 0.075));
        int lbH = titleH + 6 * rowSlotH + lbPad * 3;
        leaderboardPanel.setBounds(lbX, lbY, lbW, lbH);
        leaderboardTitle.setFont(uiFont(Math.max(13f, titleH * 0.62f)));
        leaderboardTitle.setBounds(lbPad, lbPad, lbW - lbPad * 2, titleH);
        leaderboardList.setBounds(lbPad, lbPad + titleH + 4, lbW - lbPad * 2, 6 * rowSlotH);
        refreshLeaderboard();

        jPanel2.setBounds(combX, iT, combW, contentH);
        {
            int pad = 12, nameH = Math.max(32, (int)(contentH * 0.10));
            summaryPlayerName.setFont(uiFont(Math.max(15f, nameH * 0.70f)));
            summaryPlayerName.setBounds(pad, pad, combW - pad * 2, nameH);
            int contentTop = pad + nameH + 6, bodyH = (contentH - pad) - contentTop;
            int scW = (int)(combW * 0.55), sumX = scW + pad;
            int labelH = Math.max(22, (int)(bodyH * 0.055));
            int tableGap = Math.max(4, (int)(bodyH * 0.015));
            int rowH = Math.max(22, (int)(bodyH * 0.035));
            jTable1.setRowHeight(rowH); jTable2.setRowHeight(rowH);
            int upperH = UPPER.length * rowH, lowerH = LOWER.length * rowH;
            int cy = contentTop;
            UpperSectionLabel.setFont(uiFont(Math.max(13f, labelH)));
            UpperSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH); cy += labelH + 2;
            jScrollPane1.setBounds(pad, cy, scW - pad * 2, upperH);
            int upperTableEndY = cy + upperH; cy += upperH + tableGap + rowH;
            LowerSectionLabel.setFont(uiFont(Math.max(13f, labelH)));
            LowerSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH); cy += labelH + 2;
            jScrollPane2.setBounds(pad, cy, scW - pad * 2, lowerH);
            int summaryRowH = Math.max(30, (int)(bodyH * 0.11));
            int barH = Math.max(18, (int)(summaryRowH * 0.45));
            int valX = combW - pad - 70, lblW2 = valX - sumX - 4;
            int upperRowY = contentTop + labelH + 2;
            summaryUpperLabel.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryUpperLabel.setBounds(sumX, upperRowY, lblW2, summaryRowH);
            summaryUpperVal.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryUpperVal.setBounds(valX, upperRowY, 70, summaryRowH);
            int bonusRowY = (int)(upperTableEndY + contentTop + labelH * 8.5 + 2 + tableGap) / 2 - summaryRowH / 2;
            summaryBonusLabel.setFont(uiFont(Math.max(13f, summaryRowH * 0.55f)));
            summaryBonusLabel.setBounds(sumX, bonusRowY, 80, summaryRowH);
            bonusBar.setBounds(sumX + 85, bonusRowY + (summaryRowH - barH) / 2, combW - (sumX + 85) - pad - 70, barH);
            summaryBonusVal.setFont(uiFont(Math.max(13f, summaryRowH * 0.55f)));
            summaryBonusVal.setBounds(valX, bonusRowY, 70, summaryRowH);
            int totalRowY = contentH - pad - summaryRowH, lowerRowY = totalRowY - summaryRowH - 8;
            summaryLowerLabel.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryLowerLabel.setBounds(sumX, lowerRowY, lblW2, summaryRowH);
            summaryLowerVal.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryLowerVal.setBounds(valX, lowerRowY, 70, summaryRowH);
            summaryTotalLabel.setFont(uiFont(Math.max(16f, summaryRowH * 0.70f)));
            summaryTotalLabel.setBounds(sumX, totalRowY, lblW2, summaryRowH);
            summaryTotalVal.setFont(uiFont(Math.max(16f, summaryRowH * 0.70f)));
            summaryTotalVal.setBounds(valX, totalRowY, 70, summaryRowH);
        }

        jPanel1.setBounds(iL, diceStripY, iR - iL, diceStripH);
        int diceSize = Math.min((int)(diceStripH * 0.68), combW / 5);
        int innerGap = (combW - 5 * diceSize) / 4, diceBaseX = combX - iL;
        int labelHt = (int)(diceStripH * 0.17), labelYd = (diceStripH - diceSize - labelHt - 2) / 2;
        int diceY2 = labelYd + labelHt + 2;
        javax.swing.JButton[] dBtns = {die1,die2,die3,die4,die5};
        javax.swing.JLabel[]  dLabels = {die1Label,die2Label,die3Label,die4Label,die5Label};
        for (int i = 0; i < 5; i++) {
            int dieX = diceBaseX + i * (diceSize + innerGap);
            dLabels[i].setBounds(dieX, labelYd, diceSize, labelHt);
            dLabels[i].setFont(uiFont(Math.max(8f, diceStripH * 0.10f)));
            dBtns[i].setBounds(dieX, diceY2, diceSize, diceSize);
            if (diceFont != null) dBtns[i].setFont(diceFont.deriveFont((float) diceSize * 0.60f));
        }
        int rollH = (int)(diceStripH * 0.52);
        int rollY = diceStripY + diceY2 + (diceStripH - diceY2 - rollH) / 2;
        jButton1.setBounds(btnXCalc, rollY, btnWCalc, rollH);
        jButton1.setFont(uiFont(Math.max(14f, rollH * 0.38f)));
        jLayeredPane1.revalidate(); jLayeredPane1.repaint();
    }

    private void refreshSummary() {
        if (scoreCard == null) return;
        java.util.Map<Category, Integer> s = scoreCard.getScores();
        int upper    = orZero(s, Category.UPPER_SCORE);
        int bonus    = orZero(s, Category.BONUS);
        int lower    = orZero(s, Category.LOWER_SCORE);
        int total    = scoreCard.getTotalScore();
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

    private static int orZero(java.util.Map<Category, Integer> map, Category c) {
        Integer v = map.get(c); return v == null ? 0 : v;
    }

    private DiceComponent makeDieButton() {
        DiceComponent b = new DiceComponent(); b.setBackground(Color.WHITE); b.setValue(0); return b;
    }
    private javax.swing.JLabel makeDieLabel() {
        javax.swing.JLabel l = new javax.swing.JLabel("");
        l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); l.setFont(uiFont(12f)); return l;
    }
    private javax.swing.JTable makeTable() {
        javax.swing.JTable t = new javax.swing.JTable();
        t.setBackground(new Color(255, 248, 220)); t.setGridColor(new Color(200, 160, 60));
        t.setRowHeight(26); t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0, 0));
        t.getTableHeader().setBackground(new Color(210, 150, 40));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Bauhaus 93", Font.BOLD, 13));
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setPreferredSize(new Dimension(0, 0));
        t.getTableHeader().setVisible(false);
        return t;
    }
    private JScrollPane makeScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(180, 150, 60), 1));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setBackground(new Color(255, 248, 220));
        return sp;
    }

    private void wireScoreCard() {
        if (t.getPlayers() == null || t.getPlayers().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No players found!"); return;
        }
        scoreCard  = t.getScoreCard();
        upperModel = new ScoreCardTableModel(scoreCard, UPPER);
        lowerModel = new ScoreCardTableModel(scoreCard, LOWER);
        jTable1.setModel(upperModel); jTable2.setModel(lowerModel);
        upperRenderer = new ScoreCellRenderer(scoreCard);
        lowerRenderer = new ScoreCellRenderer(scoreCard);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(upperRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(upperRenderer);
        jTable2.getColumnModel().getColumn(0).setCellRenderer(lowerRenderer);
        jTable2.getColumnModel().getColumn(1).setCellRenderer(lowerRenderer);
        this.t.createInterface();
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                upperRenderer.setHoveredRow(jTable1.rowAtPoint(e.getPoint())); jTable1.repaint();
            }
        });
        jTable1.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                upperRenderer.setHoveredRow(-1); jTable1.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint()), col = jTable1.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive && firstRollDone)
                    finalizeCategorySelection(upperModel.getCategoryAt(row));
                try
                {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
                catch (Exception ex)
                {
                    System.out.println(ex);
                }
            }
        });
        jTable2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                lowerRenderer.setHoveredRow(jTable2.rowAtPoint(e.getPoint())); jTable2.repaint();
            }
        });
        jTable2.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                lowerRenderer.setHoveredRow(-1); jTable2.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable2.rowAtPoint(e.getPoint()), col = jTable2.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive && firstRollDone)
                    finalizeCategorySelection(lowerModel.getCategoryAt(row));
                try
                {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
                catch (Exception ex)
                {
                    System.out.println(ex);
                }
            }
        });
    }

    public int[] getDiceValues() {
        int[] vals = new int[5];
        for (int i = 0; i < 5; i++) vals[i] = t.getDiceFromInterface()[i];
        return vals;
    }

    private void spinDie(DiceComponent die) {
        float[] phases = {0.85f, 0.6f, 0.3f, 0.05f, 1.0f};
        Timer spinTimer = new Timer(35, null);
        int[] step = {0};
        spinTimer.addActionListener(e -> {
            if (step[0] < phases.length) { die.setSpinPhase(phases[step[0]]); step[0]++; }
            else { die.setSpinPhase(1.0f); ((Timer) e.getSource()).stop(); }
        });
        spinTimer.start();
    }

    private void spinPanel(Runnable onFlip) {
        int intervalMs = 16; float halfDurMs = 320f;
        boolean[] flipped = {false}; long[] startTime = {System.currentTimeMillis()};
        Timer t2 = new Timer(intervalMs, null);
        t2.addActionListener(e -> {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - startTime[0]) / halfDurMs);
            if (!flipped[0]) {
                panelFlipper.accept(1.0f - easeIn(progress));
                if (progress >= 1.0f) {
                    flipped[0] = true; onFlip.run();
                    startTime[0] = System.currentTimeMillis(); panelFlipper.accept(0.0f);
                }
            } else {
                panelFlipper.accept(easeOut(progress));
                if (progress >= 1.0f) { panelFlipper.accept(1.0f); ((Timer) e.getSource()).stop(); }
            }
        });
        t2.start();
    }

    private static float easeIn(float t)  { return t * t; }
    private static float easeOut(float t) { return t * (2 - t); }

    private void updateHoldLabels() {
        die1.setHeld(holding[0]); die2.setHeld(holding[1]); die3.setHeld(holding[2]);
        die4.setHeld(holding[3]); die5.setHeld(holding[4]);
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
                    java.awt.EventQueue.invokeAndWait(this::performManualRoll);
                    while (t.getRolls() > 0) {
                        Thread.sleep(1500);
                        int rollsRemaining = t.getRolls();
                        java.util.Set<Integer> toKeep = brain.chooseDiceToKeep(dice, scoreCard, rollsRemaining);
                        if (toKeep.size() == 5) break;
                        for (int j = 0; j < 5; j++) holding[j] = toKeep.contains(j);
                        java.awt.EventQueue.invokeAndWait(() -> { updateHoldLabels(); performManualRoll(); });
                    }
                    Thread.sleep(1500);
                    int[] currentVals = t.getDiceFromInterface();
                    Category choice = brain.chooseCategory(currentVals, scoreCard);
                    java.awt.EventQueue.invokeLater(() -> finalizeCategorySelection(choice));
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        } else {
            jButton1.setEnabled(true); turnActive = true;
        }
    }

    private void finalizeCategorySelection(Category selectedCategory) {
        if (selectedCategory == Category.UPPER_SCORE || selectedCategory == Category.BONUS
                || selectedCategory == Category.LOWER_SCORE || selectedCategory == Category.TOTAL) {
            System.out.println("WARNING: Tried to finalize auto-category: " + selectedCategory);
            return;
        }
        if (scoreCard.isCategoryFilled(selectedCategory)) {
            if (!(t.getCurrentPlayer() instanceof AIPlayer))
                JOptionPane.showMessageDialog(this, "That category is already filled!");
            return;
        }

        snapshotRanks();
        scoreCard.fillCategory(selectedCategory, dice);
        upperModel.fireTableDataChanged();
        lowerModel.fireTableDataChanged();

        turnActive = false; rollCount = 0;
        Arrays.fill(holding, false); firstRollDone = false;
        jButton1.setEnabled(false);
        die1.setEnabled(false); die2.setEnabled(false); die3.setEnabled(false);
        die4.setEnabled(false); die5.setEnabled(false);
        for (DiceComponent d : new DiceComponent[]{die1,die2,die3,die4,die5}) {
            d.setHeld(false); d.setValue(0);
        }

        Timer delayTimer = new Timer(400, null);
        delayTimer.setRepeats(false);
        delayTimer.addActionListener(delayEvt -> {

            flipScoreLabels(() -> {

                animateLeaderboard(() -> {

                    if (t.completeGame()) {
                        new EndPage(t).setVisible(true);
                    } else {
                        pendingCategory = null;
                        t.nextPlayer(); t.resetRolls();
                        spinPanel(() -> {
                            jTable1.clearSelection(); jTable2.clearSelection();
                            scoreCard  = t.getScoreCard();
                            upperModel = new ScoreCardTableModel(scoreCard, UPPER);
                            lowerModel = new ScoreCardTableModel(scoreCard, LOWER);
                            jTable1.setModel(upperModel); jTable2.setModel(lowerModel);
                            upperRenderer = new ScoreCellRenderer(scoreCard);
                            lowerRenderer = new ScoreCellRenderer(scoreCard);
                            jTable1.getColumnModel().getColumn(0).setCellRenderer(upperRenderer);
                            jTable1.getColumnModel().getColumn(1).setCellRenderer(upperRenderer);
                            jTable2.getColumnModel().getColumn(0).setCellRenderer(lowerRenderer);
                            jTable2.getColumnModel().getColumn(1).setCellRenderer(lowerRenderer);
                            refreshSummary();
                            jButton1.setEnabled(true);
                            checkAndPlayAITurn();
                        });
                    }
                });
            });
        });
        delayTimer.start();
    }
    private void animateLeaderboard(Runnable onDone) {
    if (t == null) { onDone.run(); return; }

    java.util.List<Player> players = t.getPlayers();
    java.util.List<ScoreCard> cards = t.getScoreCards();

    java.util.List<int[]> indexed = new java.util.ArrayList<>();
    for (int i = 0; i < players.size(); i++) indexed.add(new int[]{i});

    indexed.sort((a, b) ->
        cards.get(b[0]).getTotalScore() - cards.get(a[0]).getTotalScore()
    );

    String[] diceIcons = {"⚀","⚁","⚂","⚃","⚄","⚅"};

    leaderboardList.removeAll();  

    for (int rank = 0; rank < indexed.size(); rank++) {

        int idx = indexed.get(rank)[0];
        Player p = players.get(idx);
        int score = cards.get(idx).getTotalScore();

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false); 
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

        JLabel icon = new JLabel(diceIcons[Math.min(rank, 5)]);
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        icon.setForeground(new Color(200, 80, 20));
        icon.setPreferredSize(new Dimension(30, 30));

        JLabel name = new JLabel(p.getUsername());
        name.setFont(uiFont(18f));
        name.setForeground(Color.BLACK);

        JLabel scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setFont(uiFont(18f));
        scoreLabel.setForeground(new Color(100, 50, 0));
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(icon, BorderLayout.WEST);
        row.add(name, BorderLayout.CENTER);
        row.add(scoreLabel, BorderLayout.EAST);

        int prevRank = previousRanks.getOrDefault(idx, rank);
        if (prevRank > rank) {
            animateRankUp(row);   
        }

        leaderboardList.add(row);
    }

    leaderboardList.revalidate();
    leaderboardList.repaint();

    for (int rank = 0; rank < indexed.size(); rank++) {
        previousRanks.put(indexed.get(rank)[0], rank);
    }

    onDone.run();
}
    private void snapshotRanks() {
        previousRanks.clear();
        if (t == null) return;
        java.util.List<ScoreCard> cards = t.getScoreCards();
        java.util.List<int[]> indexed = new java.util.ArrayList<>();
        for (int i = 0; i < cards.size(); i++) indexed.add(new int[]{i});
        indexed.sort((a, b) -> cards.get(b[0]).getTotalScore() - cards.get(a[0]).getTotalScore());
        for (int rank = 0; rank < indexed.size(); rank++)
            previousRanks.put(indexed.get(rank)[0], rank);
    }

    private void flipScoreLabels(Runnable onDone) {
        if (scoreCard == null) { onDone.run(); return; }

        java.util.Map<Category, Integer> s = scoreCard.getScores();
        int newUpper = orZero(s, Category.UPPER_SCORE);
        int newBonus = orZero(s, Category.BONUS);
        int newLower = orZero(s, Category.LOWER_SCORE);
        int newTotal = scoreCard.getTotalScore();
        int upperRaw = orZero(s, Category.ONES) + orZero(s, Category.TWOS)
                     + orZero(s, Category.THREES) + orZero(s, Category.FOURS)
                     + orZero(s, Category.FIVES)  + orZero(s, Category.SIXES);
        String newBonusTxt = newBonus > 0 ? "+35" : "–";
        Color  newBonusCol = newBonus > 0 ? new Color(0, 140, 0) : Color.BLACK;

        JLabel[] labels   = { summaryUpperVal, summaryBonusVal, summaryLowerVal, summaryTotalVal };
        String[] newTexts = {
            String.valueOf(newUpper), newBonusTxt,
            String.valueOf(newLower), String.valueOf(newTotal)
        };

        int halfMs = 170, staggerMs = 70, intervalMs = 14;
        long startTime = System.currentTimeMillis();

        for (JLabel lbl : labels) {
            lbl.putClientProperty("flipSwapped", null);
            lbl.putClientProperty("flipScale", 1.0f);
            lbl.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
                @Override public void paint(Graphics g, JComponent c) {
                    Object sv = ((JLabel) c).getClientProperty("flipScale");
                    float scale = (sv instanceof Float) ? (Float) sv : 1.0f;
                    if (scale >= 1.0f) { super.paint(g, c); return; }
                    Graphics2D g2 = (Graphics2D) g.create();
                    int cx = c.getWidth() / 2, cy = c.getHeight() / 2;
                    g2.translate(cx, cy);
                    g2.scale(1.0, Math.max(0.001, scale));
                    g2.translate(-cx, -cy);
                    super.paint(g2, c);
                    g2.dispose();
                }
            });
        }
        bonusBar.setProgress(upperRaw);

        Timer flipTimer = new Timer(intervalMs, null);
        flipTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean allDone = true;
            for (int i = 0; i < labels.length; i++) {
                long lElapsed = elapsed - (long) i * staggerMs;
                if (lElapsed <= 0) { allDone = false; continue; }
                float phase = Math.min(1.0f, lElapsed / (float)(halfMs * 2));
                if (phase < 1.0f) allDone = false;
                JLabel lbl = labels[i];
                if (phase < 0.5f) {
                    lbl.putClientProperty("flipScale", 1.0f - easeIn(phase * 2f));
                } else {
                    if (lbl.getClientProperty("flipSwapped") == null) {
                        lbl.putClientProperty("flipSwapped", Boolean.TRUE);
                        lbl.setText(newTexts[i]);
                        if (lbl == summaryBonusVal) lbl.setForeground(newBonusCol);
                    }
                    lbl.putClientProperty("flipScale", easeOut((phase - 0.5f) * 2f));
                }
                lbl.repaint();
            }
            if (allDone) {
                ((Timer) e.getSource()).stop();
                for (JLabel lbl : labels) {
                    lbl.putClientProperty("flipScale", null);
                    lbl.putClientProperty("flipSwapped", null);
                }
                onDone.run();
            }
        });
        flipTimer.start();
    }
    private void animateRankUp(JPanel row) {
        Color start = new Color(180, 255, 180);
        Color end = new Color(180, 255, 180, 0);

        final int steps = 40;
        final int[] step = {0};

        Timer timer = new Timer(20, e -> {
            float t = (float) step[0] / steps;

            int r = (int) (start.getRed()   * (1 - t) + end.getRed()   * t);
            int g = (int) (start.getGreen() * (1 - t) + end.getGreen() * t);
            int b = (int) (start.getBlue()  * (1 - t) + end.getBlue()  * t);
            int a = (int) (start.getAlpha() * (1 - t) + end.getAlpha() * t);

            row.setBackground(new Color(r, g, b, a));
            row.repaint();

            if (++step[0] > steps) ((Timer) e.getSource()).stop();
        });

        timer.start();
    }
   private void refreshLeaderboard() {
    if (t == null) return;

    java.util.List<Player> players = t.getPlayers();
    java.util.List<ScoreCard> cards = t.getScoreCards();

    for (int i = 0; i < players.size(); i++) {

        if (!existingRows.containsKey(i)) {

            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            row.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

            JLabel icon = new JLabel();
            icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
            icon.setForeground(new Color(200, 80, 20));
            icon.setPreferredSize(new Dimension(30, 30));

            JLabel name = new JLabel(players.get(i).getUsername());
            name.setFont(uiFont(18f));
            name.setForeground(Color.BLACK);

            JLabel score = new JLabel("0");
            score.setFont(uiFont(18f));
            score.setForeground(new Color(100, 50, 0));
            score.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(icon, BorderLayout.WEST);
            row.add(name, BorderLayout.CENTER);
            row.add(score, BorderLayout.EAST);

            existingRows.put(i, row);
            scoreLabels.put(i, score);
        }
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
                spinDie(j[i]);
            }
            currentRoll = Arrays.copyOf(dice, dice.length);
            Map<Category, Integer> possibleScores = scoreCard.calculatePossibleScores(dice);
            upperModel.setPossibleScores(possibleScores);
            lowerModel.setPossibleScores(possibleScores);
        }
        t.removeRoll();
        if (t.getRolls() == 0) jButton1.setEnabled(false);
        if (!firstRollDone) {
            firstRollDone = true;
            die1.setEnabled(true); die2.setEnabled(true); die3.setEnabled(true);
            die4.setEnabled(true); die5.setEnabled(true);
        }
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/DiceRoll.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void die1ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[0] = !holding[0]; 
        die1.setHeld(holding[0]); 
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    private void die2ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[1] = !holding[1]; 
        die2.setHeld(holding[1]); 
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    private void die3ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[2] = !holding[2]; 
        die3.setHeld(holding[2]); 
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    private void die4ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[3] = !holding[3]; 
        die4.setHeld(holding[3]); 
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    private void die5ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[4] = !holding[4]; 
        die5.setHeld(holding[4]); 
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

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
    public void setProgress(int value) { this.progress = Math.min(value, BONUS_TARGET); repaint(); }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int W = getWidth(), H = getHeight(), arc = H;
        g2.setColor(new Color(180, 150, 60));
        g2.fillRect(0, H - 2, W, 2);
        g2.fillRoundRect(0, 0, W, H, arc, arc);
        int fillW = (int)((double) progress / BONUS_TARGET * W);
        if (fillW > 0) {
            g2.setPaint(new GradientPaint(0, 0, new Color(220, 100, 20), fillW, 0, new Color(255, 210, 50)));
            g2.fillRoundRect(0, 0, fillW, H, arc, arc);
        }
        g2.setColor(new Color(160, 100, 20, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, W - 1, H - 1, arc, arc);
        g2.setColor(progress >= BONUS_TARGET ? new Color(0, 120, 0) : new Color(80, 40, 0));
        g2.setFont(new Font("Bauhaus 93", Font.BOLD, Math.max(9, H - 4)));
        String txt = progress + " / 63";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(txt, (W - fm.stringWidth(txt)) / 2, (H + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}


class GameFramePanel extends JPanel {
    private final Color YELLOW       = new Color(247, 201, 72);
    private final Color LIGHT_ORANGE = new Color(255, 170, 60);
    private final Color DARK_ORANGE  = new Color(230, 120, 40);
    private final Color INTERIOR     = new Color(250, 235, 137);
    public GameFramePanel() { setOpaque(false); }
    private java.awt.geom.GeneralPath halfFramePath(int W, int H, int offset, int radius) {
        int x = offset, y2 = H - offset, r = radius;
        java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
        p.moveTo(W + 50, y2); p.lineTo(x + r, y2);
        p.quadTo(x, y2, x, y2 - r); p.lineTo(x, -50);
        return p;
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(Color.BLACK); g2.fillRect(0, 0, w, h);
        int baseOffset = 30, baseRadius = 90;
        int[] strokeW = {46, 36, 26};
        g2.setColor(INTERIOR); g2.fillRect(baseOffset + strokeW[0] / 2 + 8, 0, w - (baseOffset + strokeW[0] / 2 + 8), h - (baseOffset + strokeW[0] / 2 + 8));
        Color[] colors = {YELLOW, LIGHT_ORANGE, DARK_ORANGE};
        for (int i = 0; i < 3; i++) {
            java.awt.geom.GeneralPath path = halfFramePath(w, h, baseOffset + i * 14, Math.max(20, baseRadius - i * 10));
            g2.setStroke(new BasicStroke(strokeW[i] + 6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.BLACK); g2.draw(path);
            g2.setStroke(new BasicStroke(strokeW[i], BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(colors[i]); g2.draw(path);
        }
        g2.dispose();
    }
}