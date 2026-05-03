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
import javax.imageio.ImageIO;
import java.awt.Toolkit;
public class YahtzeeDesign extends javax.swing.JFrame {

    Dice[] dice = {new Dice(), new Dice(), new Dice(), new Dice(), new Dice()};
    private Font diceFont = null;
//    private void loadDiceFont() {
//        try {
//            InputStream is = YahtzeeDesign.class.getResourceAsStream("/fonts/yahtzee-dice.ttf");
//            if (is == null) throw new RuntimeException("Dice font not found");
//            diceFont = Font.createFont(Font.TRUETYPE_FONT, is);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            diceFont = new Font("Serif", Font.PLAIN, 36);
//        }
//    }
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
    private boolean soundEffectsOn = true;
    private volatile boolean musicOn = true;
    private volatile Clip backgroundMusicClip = null;
    private javax.swing.JButton menuIconBtn;
    private javax.swing.JPanel menuDropdown;
    private javax.swing.JButton soundBtn, musicBtn, helpBtn, aiSpeedBtn, exitBtn;
    private boolean menuOpen = false;
    private volatile boolean aiSpeedUp = false;
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
//        loadDiceFont();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        buildComponents();
        this.setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
        wireScoreCard();
        animateLeaderboard(() -> {}); 
        checkAndPlayAITurn();
//        Clip spClip = StartPageDesign.startPageMusicClip;
//        StartPageDesign.startPageMusicClip = null;
//        if (spClip != null) {
//            backgroundMusicClip = spClip;
//            startMusicLoopThread(spClip);
//        } else {
            new Thread(this::startBackgroundMusic).start();
//        }
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
                g2.translate(cx, cy); g2.scale(Math.max(0.01f, flipPhase), 1.0); g2.translate(-cx, -cy);
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

        menuIconBtn = new javax.swing.JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    java.net.URL url = getClass().getResource("/img/menuIcon.png");
                    if (url != null) {
                        java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);
                        g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                    }
                } catch(Exception ex) {
                    g2.setColor(new Color(180,20,20));
                    g2.setFont(new Font("Bauhaus 93", Font.BOLD, 18));
                    g2.drawString("☰", 8, getHeight()-8);
                }
                g2.dispose();
            }
        };
        menuIconBtn.setOpaque(false); menuIconBtn.setContentAreaFilled(false);
        menuIconBtn.setBorderPainted(false); menuIconBtn.setFocusPainted(false);
        menuIconBtn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        menuIconBtn.addActionListener(e -> toggleMenu());
        jLayeredPane1.add(menuIconBtn, Integer.valueOf(10));

        menuDropdown = new javax.swing.JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 170, 60));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.setColor(new Color(200, 120, 20));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        
        menuDropdown.setOpaque(false);
        menuDropdown.setVisible(false);
        
        musicBtn = makeMenuIconButton("/img/MusicIconOn.png");
        musicBtn.addActionListener(e -> {
            playSoundEffect("/sounds/Click.wav");
            musicOn = !musicOn;
            if (musicOn) startBackgroundMusic(); else stopBackgroundMusic();
            updateMenuIcons();
        });
        soundBtn = makeMenuIconButton("/img/SoundOnIcon1.png");
        soundBtn.addActionListener(e -> {
            if (soundEffectsOn) playSoundEffect("/sounds/Click.wav"); 
            soundEffectsOn = !soundEffectsOn;
            updateMenuIcons();
        });
        aiSpeedBtn = makeMenuIconButton("/img/SpeedUpIcon.png");
        aiSpeedBtn.addActionListener(e -> {
            playSoundEffect("/sounds/Click.wav");
            aiSpeedUp = !aiSpeedUp;
            updateMenuIcons();
        });
        exitBtn = makeMenuIconButton("/img/exitIcon.png");
        exitBtn.addActionListener(e -> {
            menuDropdown.setVisible(false); menuOpen = false;
            stopBackgroundMusic();
            StartPageDesign sp = new StartPageDesign();
            sp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            sp.setVisible(true);
            this.setVisible(false);
            dispose();
        });
        helpBtn = makeMenuIconButton("/img/helpIcon.png");
        helpBtn.addActionListener(e -> {
            menuDropdown.setVisible(false); menuOpen = false;
            openHelpPage();
        });

        menuDropdown.add(musicBtn); menuDropdown.add(soundBtn); menuDropdown.add(aiSpeedBtn);menuDropdown.add(exitBtn); menuDropdown.add(helpBtn);
        jLayeredPane1.add(menuDropdown, Integer.valueOf(11));

        jLayeredPane1.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (menuOpen && !menuDropdown.getBounds().contains(e.getPoint())
                        && !menuIconBtn.getBounds().contains(e.getPoint())) {
                    menuDropdown.setVisible(false); menuOpen = false;
                }
            }
        });

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(jLayeredPane1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private javax.swing.JButton makeMenuIconButton(String resource) {
        javax.swing.JButton btn = new javax.swing.JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 200, 100, 120));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                Object res = getClientProperty("iconRes");
                String iconPath = (res instanceof String) ? (String) res : null;
                try {
                    if (iconPath != null) {
                        java.net.URL url = getClass().getResource(iconPath);
                        if (url != null) {
                            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);
                            int pad = 4;
                            g2.drawImage(img, pad, pad, getWidth()-pad*2, getHeight()-pad*2, null);
                        }
                    }
                } catch(Exception ex) { }
                g2.dispose();
            }
        };
        btn.putClientProperty("iconRes", resource);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    private void toggleMenu() {
        menuOpen = !menuOpen;
        menuDropdown.setVisible(menuOpen);
        if (menuOpen) menuDropdown.repaint();
        playSoundEffect("/sounds/Click.wav");
    }

    private void updateMenuIcons() {
        String soundRes = soundEffectsOn ? "/img/SoundOnIcon1.png" : "/img/SoundOffIcon1.png";
        String musicRes = musicOn ? "/img/MusicIconOn.png" : "/img/MusicIconOff.png";
        String aiSpeedRes = aiSpeedUp ? "/img/SlowDownIcon.png" : "/img/SpeedUpIcon.png";
        soundBtn.putClientProperty("iconRes", soundRes);
        musicBtn.putClientProperty("iconRes", musicRes);
        aiSpeedBtn.putClientProperty("iconRes", aiSpeedRes);
        soundBtn.repaint(); musicBtn.repaint(); aiSpeedBtn.repaint();
    }

    private void playSoundEffect(String path) {
        if (!soundEffectsOn) return;
        try {
            if (System.getProperty("java.vendor").contains("Leaning Technologies Ltd")) {
                    System.out.println("PLAY_Click");
                } else {
                    
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                        getClass().getResource(path));

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
        } catch (Exception ex) { System.out.println(ex); }
    }

    private void startBackgroundMusic() {
        if (!musicOn) return;
        if (backgroundMusicClip != null) return;
        try {
            if (System.getProperty("java.vendor").contains("Leaning Technologies Ltd")) {
                System.out.println("PLAY_background");
            } else {
                java.net.URL musicUrl = getClass().getResource("/sounds/background.wav");
                if (musicUrl == null) { System.out.println("background.wav not found"); return; }
                AudioInputStream ais = AudioSystem.getAudioInputStream(musicUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                backgroundMusicClip = clip;
                clip.start();
                startMusicLoopThread(clip);
            }
        } catch (Exception ex) { System.out.println("Music error: " + ex); }
    }

    private void startMusicLoopThread(Clip clip) {
        Thread loopThread = new Thread(() -> {
            while (clip == backgroundMusicClip && musicOn) {
                if (!clip.isRunning()) {
                    if (clip == backgroundMusicClip && musicOn) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }
                try { Thread.sleep(200); } catch (InterruptedException e) { break; }
            }
        });
        loopThread.setDaemon(true);
        loopThread.start();
    }
    // almost the end of me 
    private void stopBackgroundMusic() {
        if (System.getProperty("java.vendor").contains("Leaning Technologies Ltd")) {
                System.out.println("STOP_background");
        } else {
            Clip clip = backgroundMusicClip;
            backgroundMusicClip = null;
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
    }


    private void openHelpPage() {
        HelpPage hp = new HelpPage(this);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
        hp.setVisible(true);
        this.setVisible(false);
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

        int frameArm    = Math.max(50, (int)(W * 0.065));
        int frameBuffer = Math.max(20, (int)(W * 0.028));
        int smallBorder = Math.max(16, (int)(H * 0.030));

        int iL = frameArm + frameBuffer, iT = smallBorder;
        int menuIconSize = Math.max(36, (int)(W * 0.038));
        int menuMargin = Math.max(10, (int)(W * 0.012));
        int menuIconX = W - menuMargin - menuIconSize;
        int menuIconY = iT + 4;
        menuIconBtn.setBounds(menuIconX, menuIconY, menuIconSize, menuIconSize);

        int dropBtnSize = Math.max(34, (int)(W * 0.034));
        int dropPad = 8;
        int dropW = dropBtnSize + dropPad * 2;
        int dropY = menuIconY + menuIconSize + 4;
        int diceBottomBuffer2 = Math.max(16, (int)(H * 0.030)) + 50;
        int diceStripH2 = (int)(((H - Math.max(16, (int)(H * 0.030))) - iT) * 0.20);
        int diceStripY2 = H - diceBottomBuffer2 - diceStripH2;
        int contentH2 = diceStripY2 - iT - 10;
        int dropH = (iT + contentH2) - dropY;
        int dropX = menuIconX + menuIconSize - dropW;
        int alignedIconX = dropX + (dropW - menuIconSize) / 2;
        menuIconBtn.setBounds(alignedIconX, menuIconY, menuIconSize, menuIconSize);
        menuDropdown.setBounds(dropX, dropY, dropW, dropH);
        int numBtns = 5;
        int spacing = Math.max(4, (dropH - numBtns * dropBtnSize) / (numBtns + 1));
        javax.swing.JButton[] dropBtns = {musicBtn, soundBtn, aiSpeedBtn, exitBtn, helpBtn};
        for (int i = 0; i < numBtns; i++) {
            dropBtns[i].setBounds(dropPad, spacing + i * (dropBtnSize + spacing), dropBtnSize, dropBtnSize);
        }

        int iR = menuIconX - menuMargin;
        int iW = iR - iL, iH = (H - smallBorder) - iT;

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
            int pad = 12, nameH = Math.max(28, (int)(contentH * 0.08));
            summaryPlayerName.setFont(uiFont(Math.max(14f, nameH * 0.70f)));
            summaryPlayerName.setBounds(pad, pad, combW - pad * 2, nameH);
            int contentTop = pad + nameH + 6, bodyH = (contentH - pad) - contentTop;
            int scW = (int)(combW * 0.55), sumX = scW + pad;
            int labelH = Math.max(18, (int)(bodyH * 0.050));
            int tableGap = Math.max(6, (int)(bodyH * 0.025));

            int totalRows = UPPER.length + LOWER.length;
            int totalLabelH = labelH * 2 + 2 * 2; 
            int availForRows = bodyH - totalLabelH - tableGap - pad;
            int rowH = Math.max(18, Math.min(32, availForRows / (totalRows + 1)));
            jTable1.setRowHeight(rowH); jTable2.setRowHeight(rowH);

            Font tableFont = new Font("Bauhaus 93", Font.BOLD, Math.max(9, rowH - 7));
            jTable1.setFont(tableFont);
            jTable2.setFont(tableFont);

            int upperH = UPPER.length * rowH, lowerH = LOWER.length * rowH;
            int cy = contentTop;
            UpperSectionLabel.setFont(uiFont(Math.max(12f, labelH * 0.9f)));
            UpperSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH); cy += labelH + 2;
            jScrollPane1.setBounds(pad, cy, scW - pad * 2, upperH);
            int upperTableEndY = cy + upperH; cy += upperH + tableGap;
            LowerSectionLabel.setFont(uiFont(Math.max(12f, labelH * 0.9f)));
            LowerSectionLabel.setBounds(pad, cy, scW - pad * 2, labelH); cy += labelH + 2;
            jScrollPane2.setBounds(pad, cy, scW - pad * 2, lowerH);
            int summaryRowH = Math.max(26, (int)(bodyH * 0.10));
            int barH = Math.max(16, (int)(summaryRowH * 0.45));

            int valW = Math.max(50, (int)(combW * 0.13));
            int valX = combW - pad - valW;
            int lblW2 = valX - sumX - 4;

            int upperRowY = contentTop + labelH + 2;
            summaryUpperLabel.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryUpperLabel.setBounds(sumX, upperRowY, lblW2, summaryRowH);
            summaryUpperVal.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryUpperVal.setBounds(valX, upperRowY, valW, summaryRowH);

            int bonusRowY = (int)(upperTableEndY + contentTop + labelH * 8.5 + 2 + tableGap) / 2 - summaryRowH / 2;
            summaryBonusLabel.setFont(uiFont(Math.max(13f, summaryRowH * 0.55f)));

            int bonusLblW = Math.max(50, (int)(combW * 0.15));
            summaryBonusLabel.setBounds(sumX, bonusRowY, bonusLblW, summaryRowH);
            bonusBar.setBounds(sumX + bonusLblW + 10, bonusRowY + (summaryRowH - barH) / 2,
                valX - (sumX + bonusLblW + 5) - 4, barH);

            summaryBonusVal.setFont(uiFont(Math.max(13f, summaryRowH * 0.55f)));
            summaryBonusVal.setBounds(valX, bonusRowY, valW, summaryRowH);  

            int totalRowY = contentH - pad - summaryRowH, lowerRowY = totalRowY - summaryRowH - 8;
            summaryLowerLabel.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryLowerLabel.setBounds(sumX, lowerRowY, lblW2, summaryRowH);
            summaryLowerVal.setFont(uiFont(Math.max(14f, summaryRowH * 0.60f)));
            summaryLowerVal.setBounds(valX, lowerRowY, valW, summaryRowH);  
            summaryTotalLabel.setFont(uiFont(Math.max(16f, summaryRowH * 0.70f)));
            summaryTotalLabel.setBounds(sumX, totalRowY, lblW2, summaryRowH);
            summaryTotalVal.setFont(uiFont(Math.max(16f, summaryRowH * 0.70f)));
            summaryTotalVal.setBounds(valX, totalRowY, valW, summaryRowH);  
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
                if (t.getCurrentPlayer() instanceof AIPlayer) { upperRenderer.setHoveredRow(-1); jTable1.repaint(); return; }
                upperRenderer.setHoveredRow(jTable1.rowAtPoint(e.getPoint())); jTable1.repaint();
            }
        });
        jTable1.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                upperRenderer.setHoveredRow(-1); jTable1.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint()), col = jTable1.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive && firstRollDone
                        && !(t.getCurrentPlayer() instanceof AIPlayer))
                    finalizeCategorySelection(upperModel.getCategoryAt(row));
                playSoundEffect("/sounds/Click.wav");
            }
        });
        jTable2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                if (t.getCurrentPlayer() instanceof AIPlayer) { lowerRenderer.setHoveredRow(-1); jTable2.repaint(); return; }
                lowerRenderer.setHoveredRow(jTable2.rowAtPoint(e.getPoint())); jTable2.repaint();
            }
        });
        jTable2.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                lowerRenderer.setHoveredRow(-1); jTable2.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                int row = jTable2.rowAtPoint(e.getPoint()), col = jTable2.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 1 && turnActive && firstRollDone
                        && !(t.getCurrentPlayer() instanceof AIPlayer))
                    finalizeCategorySelection(lowerModel.getCategoryAt(row));
                playSoundEffect("/sounds/Click.wav");
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
                    Thread.sleep(aiSpeedUp ? 100 : 1000);
                    java.awt.EventQueue.invokeAndWait(this::performManualRoll);
                    while (t.getRolls() > 0) {
                        Thread.sleep(aiSpeedUp ? 150 : 1500);
                        int rollsRemaining = t.getRolls();
                        java.util.Set<Integer> toKeep = brain.chooseDiceToKeep(dice, scoreCard, rollsRemaining);
                        if (toKeep.size() == 5) break;
                        for (int j = 0; j < 5; j++) holding[j] = toKeep.contains(j);
                        java.awt.EventQueue.invokeAndWait(() -> { updateHoldLabels(); performManualRoll(); });
                    }
                    Thread.sleep(aiSpeedUp ? 150 : 1500);
                    int[] currentVals = t.getDiceFromInterface();
                    Category choice = brain.chooseCategory(currentVals, scoreCard);
                    java.awt.EventQueue.invokeLater(() -> finalizeCategorySelection(choice));
                    playSoundEffect("/sounds/Click.wav");
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
                        if (backgroundMusicClip != null) {
                            backgroundMusicClip.stop();
                            backgroundMusicClip.close();
                            backgroundMusicClip = null;
                        }
                        EndPage ep = new EndPage(t);
                        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                        setSize(screen.width, screen.height);
                        setLocationRelativeTo(null);
                        ep.setVisible(true);
                        dispose();
                    }else {
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
        boolean aiSpeedRoll = aiSpeedUp && (t.getCurrentPlayer() instanceof AIPlayer);
        DiceComponent[] j = {die1, die2, die3, die4, die5};
        for (int i = 0; i < dice.length; i++) {
            if (!holding[i]) {
                dice[i].roll();
                t.updateInterface(dice);
                j[i].setValue(t.getDiceFromInterface()[i]);
                if (!aiSpeedRoll) spinDie(j[i]);
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
        try {
            if (soundEffectsOn) {
                if (System.getProperty("java.vendor").contains("Leaning Technologies Ltd")) {
                    System.out.println("PLAY_DiceRoll");
                } else {
                    
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                        getClass().getResource("/sounds/DiceRoll.wav"));

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (soundEffectsOn) {
                if (System.getProperty("java.vendor").contains("Leaning Technologies Ltd")) {
                    System.out.println("PLAY_Click");
                } else {
                    
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                        getClass().getResource("/sounds/Click.wav"));

                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void die1ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[0] = !holding[0]; 
        die1.setHeld(holding[0]); 
        playSoundEffect("/sounds/Click.wav");
    }
    private void die2ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[1] = !holding[1]; 
        die2.setHeld(holding[1]); 
        playSoundEffect("/sounds/Click.wav");
    }
    private void die3ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[2] = !holding[2]; 
        die3.setHeld(holding[2]); 
        playSoundEffect("/sounds/Click.wav");
    }
    private void die4ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[3] = !holding[3]; 
        die4.setHeld(holding[3]); 
        playSoundEffect("/sounds/Click.wav");
    }
    private void die5ActionPerformed(java.awt.event.ActionEvent evt)
    { 
        holding[4] = !holding[4]; 
        die5.setHeld(holding[4]); 
        playSoundEffect("/sounds/Click.wav");
    }

    public static void main(String args[]) {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
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
    private final Color YELLOW  = new Color(247, 201, 72);
    private final Color LIGHT_ORANGE = new Color(255, 170, 60);
    private final Color DARK_ORANGE = new Color(230, 120, 40);
    private final Color INTERIOR  = new Color(250, 235, 137);
    public GameFramePanel() { setOpaque(false); }    private java.awt.geom.GeneralPath halfFramePath(int W, int H, int offset, int radius) {
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
        java.awt.Rectangle rightClip = new java.awt.Rectangle(0, 0, w, h);
        g2.setClip(rightClip);
        Color[] colors = {YELLOW, LIGHT_ORANGE, DARK_ORANGE};
        for (int i = 0; i < 3; i++) {
            java.awt.geom.GeneralPath path = halfFramePath(w, h, baseOffset + i * 14, Math.max(20, baseRadius - i * 10));
            g2.setStroke(new BasicStroke(strokeW[i] + 6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.setColor(Color.BLACK); g2.draw(path);
            g2.setStroke(new BasicStroke(strokeW[i], BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.setColor(colors[i]); g2.draw(path);
        }
        g2.dispose();
    }
}
class HelpPage extends javax.swing.JFrame {
    private final YahtzeeDesign gameRef;

    public HelpPage(YahtzeeDesign gameRef) {
        this.gameRef = gameRef;
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        buildUI();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private Font helpFont(float size) {
        return new Font("Bauhaus 93", Font.BOLD, (int) size);
    }

    private void buildUI() {
        javax.swing.JLayeredPane layered = new javax.swing.JLayeredPane();
        layered.setLayout(null);
        layered.setBackground(new Color(250, 235, 137));
        layered.setOpaque(true);

        GameFramePanel arch = new GameFramePanel();
        arch.setOpaque(false);
        layered.add(arch, Integer.valueOf(-1));

        javax.swing.JButton homeBtn = new javax.swing.JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    java.net.URL url = getClass().getResource("/img/homeIcon.png");
                    if (url != null) {
                        java.awt.image.BufferedImage img = ImageIO.read(url);
                        g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                    } else {
                        g2.setColor(new Color(180,20,20));
                        g2.setFont(helpFont(22));
                        g2.drawString("H", 4, getHeight()-6);
                    }
                } catch(Exception ex) {}
                g2.dispose();
            }
        };
        homeBtn.setOpaque(false); homeBtn.setContentAreaFilled(false);
        homeBtn.setBorderPainted(false); homeBtn.setFocusPainted(false);
        homeBtn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screen.width, screen.height);
            setLocationRelativeTo(null);
            gameRef.setVisible(true);
            dispose();
        });
        layered.add(homeBtn, Integer.valueOf(5));

        javax.swing.JLabel titleLbl = new OutlinedLabel("How to Play");
        titleLbl.setFont(helpFont(52));
        titleLbl.setForeground(new Color(180, 20, 20));
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        layered.add(titleLbl, Integer.valueOf(2));

        javax.swing.JLabel rulesLbl = new javax.swing.JLabel(
            "<html>"
            + "<b>Overview</b><br>"
            + "&#8226; Each player takes turns rolling five dice up to <b>three times</b> per turn.<br>"
            + "&#8226; After rolling, click dice to <b>hold</b> them before re-rolling the rest.<br>"
            + "&#8226; After your rolls are used (or any time after the first roll), click a scorecard category to score it.<br>"
            + "&#8226; The game ends when every player has filled all 13 categories.<br>"
            + "<br>"
            + "<b>Scoring Rules</b><br>"
            + "&#8226; Each category can only be scored <b>once</b> per game.<br>"
            + "&#8226; If your dice don't meet a category's criteria, you may still select it to take a <b>score of 0</b> — this is called taking a zero.<br>"
            + "&#8226; Taking a zero is sometimes a smart strategy to protect higher-value categories for later.<br>"
            + "&#8226; You <b>must</b> score a category after your third roll — you cannot roll again.<br>"
            + "<br>"
            + "<b>Upper Section Bonus</b><br>"
            + "&#8226; If your total in the upper section (1's through 6's) reaches <b>63 or more</b>, you earn a <b>35-point bonus</b>.<br>"
            + "&#8226; 63 points equals scoring exactly three of each face value — aim for that as a benchmark.<br>"
            + "</html>");
        rulesLbl.setFont(new Font("Bauhaus 93", Font.PLAIN, 14));
        rulesLbl.setForeground(new Color(50, 25, 0));
        rulesLbl.setVerticalAlignment(SwingConstants.TOP);
        layered.add(rulesLbl, Integer.valueOf(2));

        String[] cols = {"Category", "Criteria", "Score"};
        String[][] rows = {
            {"1's",              "No criteria",                                        "Sum of dice showing 1"},
            {"2's",              "No criteria",                                        "Sum of dice showing 2"},
            {"3's",              "No criteria",                                        "Sum of dice showing 3"},
            {"4's",              "No criteria",                                        "Sum of dice showing 4"},
            {"5's",              "No criteria",                                        "Sum of dice showing 5"},
            {"6's",              "No criteria",                                        "Sum of dice showing 6"},
            {"Three of a Kind",  "\u2265 3 dice the same face value",                 "Sum of all dice"},
            {"Four of a Kind",   "\u2265 4 dice the same face value",                 "Sum of all dice"},
            {"Full House",       "Three of one value + two of another",               "25 points"},
            {"Small Straight",   "Four sequential dice (e.g. 1-2-3-4)",              "30 points"},
            {"Large Straight",   "Five sequential dice (e.g. 1-2-3-4-5)",            "40 points"},
            {"Even",             "All dice must be even",                              "Sum of all dice"},
            {"Odd",              "All dice must be odd",                               "Sum of all dice"},
            {"Yahtzee",          "All five dice show the same value",                  "50 points"},
            {"Chance",           "Any combination",                                    "Sum of all dice"},
        };

        javax.swing.JTable tbl = new javax.swing.JTable(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                java.awt.Component c = super.prepareRenderer(renderer, row, col);
                    c.setBackground(new Color(255, 245, 195));
                    c.setForeground(new Color(50, 20, 0));
                return c;
            }
        };
        tbl.setFont(new Font("Bauhaus 93", Font.PLAIN, 14));
        tbl.setForeground(new Color(50, 20, 0));
        tbl.setBackground(new Color(255, 248, 220));
        tbl.setGridColor(new Color(200, 160, 60));
        tbl.setRowHeight(26);
        tbl.setShowGrid(true);
        tbl.getTableHeader().setFont(helpFont(15));
        tbl.getTableHeader().setBackground(new Color(180, 30, 30));
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        tbl.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(220);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(140);

        javax.swing.JScrollPane tblScroll = new javax.swing.JScrollPane(tbl);
        tblScroll.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 50), 2));
        tblScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tblScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tblScroll.getViewport().setBackground(new Color(255, 248, 220));
        layered.add(tblScroll, Integer.valueOf(2));

        javax.swing.JLabel bonusNote = new javax.swing.JLabel(
            "<html><i>\u2605 Upper Section Bonus: +35 points when 1's\u20136's total \u2265 63</i></html>");
        bonusNote.setFont(new Font("Bauhaus 93", Font.ITALIC, 13));
        bonusNote.setForeground(new Color(130, 60, 0));
        layered.add(bonusNote, Integer.valueOf(2));

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(layered, java.awt.BorderLayout.CENTER);

        layered.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                int W = layered.getWidth(), H = layered.getHeight();
                arch.setBounds(0, 0, W, H);
                int margin = Math.max(12, (int)(W * 0.012));
                int iconSize = Math.max(36, (int)(W * 0.038));
                homeBtn.setBounds(W - margin - iconSize, margin + 4, iconSize, iconSize);
                int frameArm = Math.max(50, (int)(W * 0.065));
                int frameBuffer = Math.max(20, (int)(W * 0.028));
                int iL = frameArm + frameBuffer;
                int iT = Math.max(16, (int)(H * 0.030));
                int iW = W - iL - margin;
                int titleH = Math.max(55, (int)(H * 0.10));
                titleLbl.setBounds(iL, iT, iW, titleH);
                int y = iT + titleH + 20; 
                int remainH = H - y - margin;
                int colGap = Math.max(16, (int)(W * 0.018));
                int leftW  = (int)(iW * 0.42);
                int rightW = iW - leftW - colGap;
                rulesLbl.setBounds(iL, y, leftW, remainH);
                int noteH = Math.max(22, (int)(H * 0.035));
                int rowHeight = tbl.getRowHeight();
                int headerH = tbl.getTableHeader().getPreferredSize().height;
                int exactTblH = headerH + tbl.getRowCount() * rowHeight + 4; 
                tblScroll.setBounds(iL + leftW + colGap, y, rightW, exactTblH);
                bonusNote.setBounds(iL + leftW + colGap, y + exactTblH + 4, rightW, noteH);
            }
        });
        pack();
    }

    @SuppressWarnings("unused")
    private javax.swing.JPanel buildSectionPanel(String title, String[] cols, String[][] rows, String note) {
        javax.swing.JPanel panel = new javax.swing.JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        javax.swing.JLabel hdr = new javax.swing.JLabel("\u25B6  " + title);
        hdr.setFont(helpFont(22));
        hdr.setForeground(new Color(180, 30, 30));
        panel.add(hdr, BorderLayout.NORTH);

        javax.swing.JTable tbl2 = new javax.swing.JTable(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl2.setFont(new Font("Bauhaus 93", Font.PLAIN, 15));
        tbl2.setForeground(new Color(50, 20, 0));
        tbl2.setBackground(new Color(255, 248, 210));
        tbl2.setGridColor(new Color(200, 160, 60));
        tbl2.setRowHeight(28);
        tbl2.setShowGrid(true);
        tbl2.getTableHeader().setFont(helpFont(15));
        tbl2.getTableHeader().setBackground(new Color(210, 140, 30));
        tbl2.getTableHeader().setForeground(Color.WHITE);
        tbl2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(tbl2);
        sp.setBorder(BorderFactory.createLineBorder(new Color(180, 140, 50), 1));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getViewport().setOpaque(false); sp.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);

        if (note != null) {
            javax.swing.JLabel noteLbl = new javax.swing.JLabel("<html><i>" + note + "</i></html>");
            noteLbl.setFont(new Font("Bauhaus 93", Font.ITALIC, 14));
            noteLbl.setForeground(new Color(100, 60, 0));
            panel.add(noteLbl, BorderLayout.SOUTH);
        }
        return panel;
    }
}