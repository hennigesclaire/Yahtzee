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
    private int rowSlotH; 

    public EndPage(TurnManager tm) {
        this.tm = tm;
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        buildUI();
        this.setSize(screen.width, screen.height);
        setLocationRelativeTo(null);

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
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screen.width, screen.height);
            setLocationRelativeTo(null);
            sp.setVisible(true);
            this.setVisible(false);
            dispose();
        });
        contentPanel.add(playAgainButton);

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
        int lbH      = lbTitleH + 6 * rowSlotH + lbPad * 3 + lbTitleGap;
        int lbW      = Math.min(innerW, (int)(W * 0.52));
        int lbX      = (W - lbW) / 2;
        int lbY      = wnY + wnH + (int)(H * 0.025);
        leaderboardPanel.setBounds(lbX, lbY, lbW, lbH);
        leaderboardTitle.setFont(uiFont(Math.max((int)(H * 0.018f), (int)(lbTitleH * 0.65f))));
        leaderboardTitle.setBounds(lbPad, lbPad, lbW - lbPad * 2, lbTitleH);
        leaderboardList.setBounds(lbPad, lbPad + lbTitleH + lbTitleGap, lbW - lbPad * 2, 6 * rowSlotH);

        int btnH = Math.max((int)(H * 0.055), (int)(H * 0.07));
        int btnW = Math.max((int)(W * 0.14), (int)(W * 0.20));
        int btnY = lbY + lbH + (int)(H * 0.035);
        playAgainButton.setBounds((W - btnW) / 2, btnY, btnW, btnH);
        playAgainButton.setFont(uiFont(Math.max((int)(H * 0.020f), (int)(btnH * 0.40f))));

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