/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.yahtzee;

/**
 *
 * @author henni
 */

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.List;

public class EndPage extends javax.swing.JFrame {

    private final TurnManager tm;

    private JPanel contentPanel;
    private OutlinedLabel gameOverLabel;
    private JLabel winnerLabel;
    private JLabel winnerNameLabel;
    private GlassPanel leaderboardPanel;
    private JLabel leaderboardTitle;
    private JPanel leaderboardList;
    private RoundedButton playAgainButton;
    private RoundedButton viewStatsButton;
    private int rowSlotH; 

    public EndPage(TurnManager tm) {
        this.tm = tm;
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { layoutComponents(); }
            @Override public void componentShown(java.awt.event.ComponentEvent e)   { layoutComponents(); }
        });
        SwingUtilities.invokeLater(this::layoutComponents);
    }

    private Font uiFont(float size) {
        return new Font("Bauhaus 93", Font.BOLD, (int) size);
    }

    private void buildUI() {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setOpaque(true);
        layeredPane.setBackground(new Color(250, 235, 137));

        ArchPanel arch = new ArchPanel();
        arch.setOpaque(false);
        layeredPane.add(arch, Integer.valueOf(-1));
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                arch.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                arch.repaint();
                layoutComponentsIn(layeredPane);
            }
        });

        contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        layeredPane.add(contentPanel, Integer.valueOf(1));

        gameOverLabel = new OutlinedLabel("Game Over!");
        gameOverLabel.setFont(uiFont(72f));
        gameOverLabel.setForeground(new Color(180, 20, 20));
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(gameOverLabel);

        winnerLabel = new JLabel("Winner");
        winnerLabel.setFont(uiFont(26f));
        winnerLabel.setForeground(new Color(100, 50, 0));
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(winnerLabel);

        winnerNameLabel = new JLabel("");
        winnerNameLabel.setFont(uiFont(42f));
        winnerNameLabel.setForeground(new Color(180, 20, 20));
        winnerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(winnerNameLabel);

        leaderboardPanel = new GlassPanel();
        leaderboardPanel.setLayout(null);
        leaderboardTitle = new JLabel("Final Scores");
        leaderboardTitle.setFont(uiFont(24f));
        leaderboardTitle.setForeground(new Color(180, 30, 30));
        leaderboardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardPanel.add(leaderboardTitle);
        leaderboardList = new JPanel();
        leaderboardList.setOpaque(false);
        leaderboardList.setLayout(new BoxLayout(leaderboardList, BoxLayout.Y_AXIS));
        leaderboardPanel.add(leaderboardList);
        contentPanel.add(leaderboardPanel);

        playAgainButton = new RoundedButton("Play Again");
        playAgainButton.setFont(uiFont(28f));
        playAgainButton.addActionListener(e -> {
            StartPageDesign sp = new StartPageDesign();
            sp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            sp.setVisible(true);
            this.setVisible(false);
            dispose();
        });
        contentPanel.add(playAgainButton);

        viewStatsButton = new RoundedButton("View Stats");
        viewStatsButton.setFont(uiFont(28f));
        viewStatsButton.addActionListener(e -> showStatsDialog());
        contentPanel.add(viewStatsButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(layeredPane, BorderLayout.CENTER);

        populateLeaderboard();
        pack();
    }

    private void populateLeaderboard() {
        leaderboardList.removeAll();
        if (tm == null) return;

        List<Player> players  = tm.getPlayers();
        List<ScoreCard> cards = tm.getScoreCards();

        java.util.List<int[]> indexed = new java.util.ArrayList<>();
        for (int i = 0; i < players.size(); i++) indexed.add(new int[]{i});
        indexed.sort((a, b) -> cards.get(b[0]).getTotalScore() - cards.get(a[0]).getTotalScore());

        if (!indexed.isEmpty()) {
            int topIdx = indexed.get(0)[0];
            winnerNameLabel.setText(players.get(topIdx).getUsername()
                + "  —  " + cards.get(topIdx).getTotalScore() + " pts");
        }

        String[] medals = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

        for (int rank = 0; rank < indexed.size(); rank++) {
            int idx   = indexed.get(rank)[0];
            Player p  = players.get(idx);
            int score = cards.get(idx).getTotalScore();
            boolean isWinner = (rank == 0);

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowSlotH));
            row.setPreferredSize(new Dimension(Integer.MAX_VALUE, rowSlotH));
            row.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

            String medalText = rank < medals.length ? medals[rank] : "";
            JLabel medal = new JLabel(medalText);
            medal.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            medal.setPreferredSize(new Dimension(36, 36));

            JLabel name = new JLabel(p.getUsername());
            name.setFont(uiFont(isWinner ? 24f : 20f));
            name.setForeground(isWinner ? new Color(180, 20, 20) : Color.BLACK);

            JLabel scoreLabel = new JLabel(String.valueOf(score));
            scoreLabel.setFont(uiFont(isWinner ? 24f : 20f));
            scoreLabel.setForeground(isWinner ? new Color(180, 20, 20) : new Color(100, 50, 0));
            scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(medal, BorderLayout.WEST);
            row.add(name, BorderLayout.CENTER);
            row.add(scoreLabel, BorderLayout.EAST);
            leaderboardList.add(row);
        }
        leaderboardList.add(Box.createVerticalGlue());
        leaderboardList.revalidate();
        leaderboardList.repaint();
    }

    private void layoutComponents() {
        Component lp = getContentPane().getComponent(0);
        if (lp instanceof JLayeredPane) layoutComponentsIn((JLayeredPane) lp);
    }

    private void layoutComponentsIn(JLayeredPane lp) {
        int W = lp.getWidth(), H = lp.getHeight();
        if (W == 0 || H == 0) return;

        contentPanel.setBounds(0, 0, W, H);

        int topInset  = (int)(H * 0.08);
        int sideInset = (int)(W * 0.08);
        int innerW    = W - sideInset * 2;

        int goH = Math.max((int)(H * 0.08), (int)(H * 0.13));
        gameOverLabel.setFont(new Font("Bauhaus 93", Font.BOLD, Math.max((int)(H * 0.05), (int)(goH * 0.72f))));
        gameOverLabel.setBounds(sideInset, (int)(topInset *1.3), innerW, goH);

        int wlH = Math.max((int)(H * 0.032), (int)(H * 0.05));
        int wlY = topInset + goH + (int)(H * 0.01);
        winnerLabel.setFont(uiFont(Math.max((int)(H * 0.020f), (int)(wlH * 0.70f))));
        winnerLabel.setBounds(sideInset, wlY, innerW, wlH);

        int wnH = Math.max((int)(H * 0.055), (int)(H * 0.08));
        int wnY = wlY + wlH + (int)(H * 0.003);
        winnerNameLabel.setFont(uiFont(Math.max((int)(H * 0.028f), (int)(wnH * 0.68f))));
        winnerNameLabel.setBounds(sideInset, wnY, innerW, wnH);

        int playerCount = (tm != null && tm.getPlayers() != null) ? tm.getPlayers().size() : 1;
        int lbPad    = (int)(W * 0.012);
        int lbTitleH = Math.max((int)(H * 0.030), (int)(H * 0.055));
        rowSlotH = Math.max((int)(H * 0.045), (int)(H * 0.070));
        for (java.awt.Component c : leaderboardList.getComponents()) {
            if (c instanceof JPanel) {
                ((JPanel)c).setMaximumSize(new Dimension(Integer.MAX_VALUE, rowSlotH));
                ((JPanel)c).setPreferredSize(new Dimension(Integer.MAX_VALUE, rowSlotH));
            }
        }
        int lbTitleGap = (int)(H * 0.008);
        int lbH  = lbTitleH + 6 * rowSlotH + lbPad * 3 + lbTitleGap;
        int lbW  = Math.min(innerW, (int)(W * 0.52));
        int lbX  = (W - lbW) / 2;
        int lbY  = wnY + wnH + (int)(H * 0.012);
        leaderboardPanel.setBounds(lbX, lbY, lbW, lbH);
        leaderboardTitle.setFont(uiFont(Math.max((int)(H * 0.018f), (int)(lbTitleH * 0.65f))));
        leaderboardTitle.setBounds(lbPad, lbPad, lbW - lbPad * 2, lbTitleH);
        leaderboardList.setBounds(lbPad, lbPad + lbTitleH + lbTitleGap, lbW - lbPad * 2, 6 * rowSlotH);

        int btnH = Math.max((int)(H * 0.055), (int)(H * 0.07));
        int btnW = Math.max((int)(W * 0.14), (int)(W * 0.20));
        int btnY = lbY + lbH + (int)(H * 0.018);
        int gap  = (int)(W * 0.025);
        int totalBtnW = btnW * 2 + gap;
        int btnStartX = (W - totalBtnW) / 2;
        viewStatsButton.setBounds(btnStartX, btnY, btnW, btnH);
        viewStatsButton.setFont(uiFont(Math.max((int)(H * 0.020f), (int)(btnH * 0.40f))));
        playAgainButton.setBounds(btnStartX + btnW + gap, btnY, btnW, btnH);
        playAgainButton.setFont(uiFont(Math.max((int)(H * 0.020f), (int)(btnH * 0.40f))));

        lp.revalidate();
        lp.repaint();
    }

    private void showStatsDialog() {
        if (tm == null) return;

        List<Player>    players = tm.getPlayers();
        List<ScoreCard> cards   = tm.getScoreCards();

        java.util.List<Integer> humanScores  = new java.util.ArrayList<>();
        java.util.List<Integer> easyAiScores = new java.util.ArrayList<>();
        java.util.List<Integer> hardAiScores = new java.util.ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            Player p  = players.get(i);
            int score = cards.get(i).getTotalScore();
            if (p instanceof AIPlayer) {
                AIPlayer ai = (AIPlayer) p;
                if (ai.getStrategy() instanceof EasyYahtzeeAI) {
                    easyAiScores.add(score);
                } else {
                    hardAiScores.add(score);
                }
            } else {
                humanScores.add(score);
            }
        }

        JLayeredPane lp = (JLayeredPane) getContentPane().getComponent(0);
        int LW = lp.getWidth();
        int LH = lp.getHeight();

        JPanel dimmer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 140));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        dimmer.setOpaque(false);
        dimmer.setBounds(0, 0, LW, LH);
        lp.add(dimmer, Integer.valueOf(10));

        int cardW = Math.min(600, (int)(LW * 0.62));
        int cardH = (int)(LH * 0.58);
        int cardX = (LW - cardW) / 2;
        int cardY = (LH - cardH) / 2;

        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 235, 137));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(new Color(200, 120, 30, 220));
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 28, 28);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(cardX, cardY, cardW, cardH);
        lp.add(card, Integer.valueOf(11));

        OutlinedLabel statsTitle = new OutlinedLabel("Player Stats");
        int titleFontSize = Math.max(24, (int)(cardH * 0.10f));
        statsTitle.setFont(uiFont(titleFontSize));
        statsTitle.setForeground(new Color(180, 20, 20));
        statsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        int titleH = (int)(cardH * 0.16);
        statsTitle.setBounds(0, (int)(cardH * 0.04), cardW, titleH);
        card.add(statsTitle);

        int pad = (int)(cardW * 0.03);
        int labelW   = (int)(cardW * 0.20);
        int dataAreaX = pad + labelW;
        int dataAreaW = cardW - dataAreaX - pad;
        int colCount  = 5;
        int colW  = dataAreaW / colCount;

        String[] cols = { "Min", "Max", "Med", "Avg", "Range" };
        int headerY   = (int)(cardH * 0.28);
        int headerH   = (int)(cardH * 0.07);
        int headerFont = Math.max(11, (int)(cardH * 0.048f));
        for (int ci = 0; ci < cols.length; ci++) {
            JLabel lbl = new JLabel(cols[ci]);
            lbl.setFont(uiFont(headerFont));
            lbl.setForeground(new Color(100, 50, 0));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBounds(dataAreaX + ci * colW, headerY, colW, headerH);
            card.add(lbl);
        }

        String[][] rowDefs = {
            { "Human",   "No human players" },
            { "Easy AI", "No Easy AI players" },
            { "Hard AI", "No Hard AI players" }
        };
        @SuppressWarnings("unchecked")
        java.util.List<Integer>[] buckets = new java.util.List[]{ humanScores, easyAiScores, hardAiScores };

        int rowsTop  = headerY + headerH + (int)(cardH * 0.01);
        int rowsArea = (int)(cardH * 0.44);
        int rowH     = rowsArea / 3;
        int rowFont  = Math.max(11, (int)(cardH * 0.048f));
        int valFont  = Math.max(11, (int)(cardH * 0.046f));

        for (int ri = 0; ri < 3; ri++) {
            java.util.List<Integer> bucket = buckets[ri];
            int ry = rowsTop + ri * rowH;

            GlassPanel rowCard = new GlassPanel();
            rowCard.setLayout(null);
            rowCard.setBounds(pad, ry, cardW - pad * 2, rowH - 4);
            card.add(rowCard);

            int rcH = rowH - 4;

            JLabel typeLabel = new JLabel(rowDefs[ri][0]);
            typeLabel.setFont(uiFont(rowFont));
            typeLabel.setForeground(new Color(180, 20, 20));
            typeLabel.setBounds(8, rcH / 2 - rowFont / 2 - 2, labelW, rowFont + 4);
            rowCard.add(typeLabel);

            if (bucket.isEmpty()) {
                JLabel none = new JLabel(rowDefs[ri][1]);
                none.setFont(uiFont(Math.max(10, rowFont - 2)));
                none.setForeground(new Color(120, 80, 30));
                none.setBounds(labelW + 8, rcH / 2 - rowFont / 2 - 2, cardW - labelW - pad * 3, rowFont + 4);
                rowCard.add(none);
            } else {
                java.util.Collections.sort(bucket);
                int min   = bucket.get(0);
                int max   = bucket.get(bucket.size() - 1);
                int range = max - min;
                double avg = bucket.stream().mapToInt(Integer::intValue).average().orElse(0);
                double med;
                int n = bucket.size();
                if (n % 2 == 1) {
                    med = bucket.get(n / 2);
                } else {
                    med = (bucket.get(n / 2 - 1) + bucket.get(n / 2)) / 2.0;
                }
                String[] vals = {
                    String.valueOf(min),
                    String.valueOf(max),
                    (med == (int) med) ? String.valueOf((int) med) : String.format("%.1f", med),
                    String.format("%.1f", avg),
                    String.valueOf(range)
                };
                int innerDataX = dataAreaX - pad;
                for (int ci = 0; ci < cols.length; ci++) {
                    JLabel v = new JLabel(vals[ci]);
                    v.setFont(uiFont(valFont));
                    v.setForeground(Color.BLACK);
                    v.setHorizontalAlignment(SwingConstants.CENTER);
                    v.setBounds(innerDataX + ci * colW, rcH / 2 - valFont / 2 - 2, colW, valFont + 4);
                    rowCard.add(v);
                }
            }
        }

        RoundedButton closeBtn = new RoundedButton("Close");
        int closeFontSize = Math.max(14, (int)(cardH * 0.055f));
        int closeBtnW = (int)(cardW * 0.32);
        int closeBtnH = (int)(cardH * 0.10);
        int closeBtnX = (cardW - closeBtnW) / 2;
        int closeBtnY = (int)(cardH * 0.87);
        closeBtn.setFont(uiFont(closeFontSize));
        closeBtn.setBounds(closeBtnX, closeBtnY, closeBtnW, closeBtnH);
        closeBtn.addActionListener(e -> {
            lp.remove(card);
            lp.remove(dimmer);
            lp.revalidate();
            lp.repaint();
        });
        card.add(closeBtn);

        lp.revalidate();
        lp.repaint();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) { javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; }
        } catch (Exception ex) { ex.printStackTrace(); }
        java.awt.EventQueue.invokeLater(() -> new EndPage(null).setVisible(true));
    }
}