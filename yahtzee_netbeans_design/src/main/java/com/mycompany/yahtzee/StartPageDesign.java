/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.yahtzee;
/**
 *
 * @author henni
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.text.*;
import javax.swing.plaf.basic.BasicSliderUI;


import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.mycompany.yahtzee.AIPlayer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;




/**
 *
 * @author henni
 */

public class StartPageDesign extends javax.swing.JFrame {
    private TurnManager tm;  
    private JPanel[] slots = new JPanel[6];
    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(StartPageDesign.class.getName());
    private int playerCount = 1;
    private final int NAME_LEFT_MARGIN = 60;
    private static boolean playing_music = false;
    public static volatile Clip startPageMusicClip = null;

public StartPageDesign() {   
    UIManager.put("Slider.foreground", new Color(230, 120, 40));
    UIManager.put("Slider.thumb", new Color(230, 120, 40));
    this.tm = new TurnManager();                         
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    setSize(screen.width, screen.height);
    setLocationRelativeTo(null);
    initComponents();
    initSlots();
    jPanel2.setLayout(null);
    jPanel1.setLayout(null);
    jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.Y_AXIS));
    jPanel3.setAlignmentX(Component.CENTER_ALIGNMENT);
    int w = jLayeredPane1.getWidth();
    int h = jLayeredPane1.getHeight();

    int panelW = (int)(w * 0.60);
    int panelH = (int)(h * 0.55);

    int panelX = (w - panelW) / 2;

    int panelY = (int)(h * 0.18);

    jPanel2.setBounds(panelX, panelY, panelW, panelH);
    jLayeredPane1.setOpaque(true);
    jLayeredPane1.setBackground(new Color(250, 235, 137));
    jPanel1.setOpaque(false);
    jPanel2.setOpaque(false);
    jPanel3.setOpaque(false);

    ArchPanel arch = new ArchPanel();
    arch.setOpaque(false);
    arch.setBounds(0, 0, jLayeredPane1.getWidth(), jLayeredPane1.getHeight());
    jLayeredPane1.add(arch, Integer.valueOf(-1));
    jLayeredPane1.repaint();

    jLayeredPane1.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            arch.setBounds(0, 0, jLayeredPane1.getWidth(), jLayeredPane1.getHeight());
            arch.repaint();
            layoutComponents();
        }
    });

    this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            layoutComponents();
        }
        @Override
        public void componentShown(ComponentEvent e) {
            layoutComponents();
        }
    });

    styleButton(PlayerButton);
    styleButton(AI_PlayerButton);
    styleButton(StartButton);

    Title.setText("Yahtzee");
    Title.setForeground(new Color(180, 20, 20));
  
    Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    PlayerButton.setText("Add Player");
    AI_PlayerButton.setText("Add AI Player");
    StartButton.setText("Start");

    playerCount = 0;
    StartButton.setEnabled(false);

    layoutComponents();
    initMusic();
}

private void initMusic() {
    if (startPageMusicClip != null) {
        startPageMusicClip.stop();
        startPageMusicClip.close();
        startPageMusicClip = null;
    }

    playing_music = true;
    try {
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(
            getClass().getResource("/sounds/background.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInput);
        startPageMusicClip = clip;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
        System.out.println(e);
    }
}

private Font uiFont(float size) {
    return new Font("Bauhaus 93", Font.BOLD, (int)size);
}

private void styleButton(RoundedButton btn) {
    btn.setFont(uiFont(24f));   
}


private void layoutComponents() {
    int w = jLayeredPane1.getWidth();
    int h = jLayeredPane1.getHeight();
    if (w == 0 || h == 0) return;

    jPanel1.setBounds(0, 0, w, h);

    Title.setBounds(0, (int)(h * 0.12), w, (int)(h * 0.16));
    float titleSize = h * 0.13f;
    Title.setFont(new java.awt.Font("Bauhaus 93", java.awt.Font.BOLD, (int)titleSize));

    int panel2W = (int)(w * 0.40);
    int panel2H = (int)(h * 0.25);
    int panel2Y = (int)(h * 0.68);
    jPanel2.setBounds((w - panel2W) / 2, panel2Y, panel2W, panel2H);

    int btnW   = (int)(panel2W * 0.42);
    int btnH   = (int)(h * 0.06);
    int gap    = panel2W - btnW * 2;
    int btnTop = (int)(panel2H * 0.10);

    int startW = (int)(panel2W * 0.90);
    int startH = (int)(h * 0.07);
    int startY = Math.min((int)(panel2H * 0.55), panel2H - startH - (int)(h * 0.01));

    PlayerButton.setBounds(0, btnTop, btnW, btnH);
    AI_PlayerButton.setBounds(btnW + gap, btnTop, btnW, btnH);
    StartButton.setBounds((panel2W - startW) / 2, startY, startW, startH);

    int panel3Top    = (int)(h * 0.28);
    int panel3Bottom = panel2Y - (int)(h * 0.01);
    int panel3Height = panel3Bottom - panel3Top;
    int panel3W      = (int)(w * 0.55);
    jPanel3.setBounds((w - panel3W) / 2, panel3Top, panel3W, panel3Height);

    int totalRows = getRowCount();
    int rowSlotH = panel3Height / 6;
    int fieldH = (int)(h * 0.045);
    int fieldW  = (int)(panel3W * 0.32);
    Dimension fieldSize = new Dimension(fieldW, fieldH);

    int btnSz    = (int)(jLayeredPane1.getHeight() * 0.033);
    int rowPad   = 16; 
    int trashX   = rowPad + NAME_LEFT_MARGIN;
    int nameX    = trashX + btnSz + 8;
    int sliderGapX  = nameX + fieldW + (int)(panel3W * 0.04); 
    float smallFont = h * 0.022f;
    int easyW    = (int)(panel3W * 0.09);
    int sliderW2 = (int)(panel3W * 0.28);
    int hardW    = (int)(panel3W * 0.09);
    int sliderX  = sliderGapX + easyW;
    int hardX    = sliderX + sliderW2;

    for (Component c : jPanel3.getComponents()) {
        if (!(c instanceof JPanel)) continue;
        JPanel row = (JPanel) c;
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowSlotH));
        row.setPreferredSize(new Dimension(panel3W, rowSlotH));

        int cy  = (rowSlotH - fieldH) / 2;
        int bcy = (rowSlotH - btnSz)  / 2;

        boolean isAI = "AI".equals(row.getClientProperty("type"));

        Object db = row.getClientProperty("deleteButton");
        if (db instanceof JButton)
            ((JButton) db).setBounds(trashX, bcy, btnSz, btnSz);

        if (isAI) {
            Object nl = row.getClientProperty("nameLabel");
            Object el = row.getClientProperty("easyLabel");
            Object ds = row.getClientProperty("difficultySlider");
            Object hl = row.getClientProperty("hardLabel");
            if (nl instanceof JLabel) {
                JLabel lbl = (JLabel) nl;
                lbl.setFont(new Font("TW Cen MT Condensed", Font.BOLD, Math.max(14, (int)(fieldH * 0.45))));
                lbl.setBounds(nameX, cy, fieldW, fieldH);
            }
            if (el instanceof JLabel) {
                JLabel lbl = (JLabel) el;
                lbl.setFont(uiFont(smallFont));
                lbl.setBounds(sliderGapX, cy, easyW, fieldH); 
            }
            if (ds instanceof JSlider)
                ((JSlider) ds).setBounds(sliderX, cy, sliderW2, fieldH);
            if (hl instanceof JLabel) {
                JLabel lbl = (JLabel) hl;
                lbl.setFont(uiFont(smallFont));
                lbl.setBounds(hardX, cy, hardW, fieldH);
            }
        } else {
            Object nf = row.getClientProperty("nameField");
            if (nf instanceof JTextField) {
                JTextField tf = (JTextField) nf;
                tf.setBounds(nameX, cy, fieldW, fieldH);
            }
        }
    }

    float btnFont = h * 0.030f;
    PlayerButton.setFont(uiFont(btnFont));
    AI_PlayerButton.setFont(uiFont(btnFont));
    StartButton.setFont(uiFont(btnFont * 1.8f));

    jLayeredPane1.revalidate();
    jLayeredPane1.repaint();
}

    private class RoundedBorder implements Border {
    private int radius;
    RoundedBorder(int radius) { this.radius = radius; }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius+1, radius+1, radius+2, radius);
    }

    public boolean isBorderOpaque() { return false; }

    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        g.drawRoundRect(x, y, w-1, h-1, radius, radius);
    }
}

    private static class AIPlayerComponents {
        JTextField nameField;
        JSlider difficultySlider;

        public AIPlayerComponents(JTextField nameField, JSlider difficultySlider) {
            this.nameField = nameField;
            this.difficultySlider = difficultySlider;
        }

        public String getName() {
            return nameField.getText();
        }

        public String getDifficulty() {
            return difficultySlider.getValue() == 0 ? "Easy" : "Hard";
        }
    }
    
        private void updateButtonState() {
        boolean full = playerCount >= 6;
        PlayerButton.setEnabled(!full);
        AI_PlayerButton.setEnabled(!full);
        StartButton.setEnabled(playerCount > 0);
        }
        private void initSlots() {
        for (int i = 0; i < 6; i++) {
            JPanel placeholder = new JPanel();
            placeholder.setOpaque(false);
            slots[i] = placeholder;
            jPanel3.add(placeholder);
        }
        jPanel3.revalidate();
    }

    private int firstEmptySlot() {
        for (int i = 0; i < 6; i++) {
            if (slots[i].getComponentCount() == 0) return i;
        }
        return -1;
    }

    private void fillSlotWithHuman(int slotIndex, int number) {
        JPanel slot = slots[slotIndex];
        slot.removeAll();
        slot.setLayout(null);
        slot.putClientProperty("type", null);
        slot.putClientProperty("nameEdited", Boolean.FALSE);

        boolean[] suppressListener = {false};

        String initialText = (number > 0) ? "Player " + number : "Player";
        JTextField nameField = new RoundedTextField(initialText, 12);

        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void onChange() {
                if (!suppressListener[0]) {
                    slot.putClientProperty("nameEdited", Boolean.TRUE);
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { onChange(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { onChange(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        slot.putClientProperty("nameFieldSetter", (java.util.function.Consumer<String>) text -> {
            suppressListener[0] = true;
            nameField.setText(text);
            suppressListener[0] = false;
        });

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/trash_icon.png"));
        int iconSize = (int)(jLayeredPane1.getHeight() * 0.025);
        Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        JButton deleteButton = new JButton(new ImageIcon(scaled));
        int btnSize = (int)(jLayeredPane1.getHeight() * 0.033);
        deleteButton.setPreferredSize(new Dimension(btnSize, btnSize));
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.addActionListener(e -> {
            slots[slotIndex].setBorder(null);
            int lastFilled = slotIndex;
            for (int i = slotIndex + 1; i < 6; i++) {
                if (slots[i].getComponentCount() > 0) lastFilled = i;
                else break;
            }
            int count = lastFilled - slotIndex;
            String[]  names  = new String[count];
            boolean[] isAIs  = new boolean[count];
            boolean[] edited = new boolean[count];
            for (int i = 0; i < count; i++) {
                JPanel next = slots[slotIndex + 1 + i];
                isAIs[i]  = "AI".equals(next.getClientProperty("type"));
                edited[i] = Boolean.TRUE.equals(next.getClientProperty("nameEdited"));
                names[i]  = "";
                for (Component c : next.getComponents()) {
                    if (c instanceof JTextField) { names[i] = ((JTextField) c).getText(); break; }
                    if (c instanceof JLabel && !"AI".equals(next.getClientProperty("type"))) {
                    }
                }
            }
            for (int i = 0; i < count; i++) {
                int destSlot = slotIndex + i;
                if (isAIs[i]) {
                    fillSlotWithAI(destSlot, 0);
                } else {
                    fillSlotWithHuman(destSlot, 0); 
                    if (edited[i]) {
                        @SuppressWarnings("unchecked")
                        java.util.function.Consumer<String> s =
                            (java.util.function.Consumer<String>) slots[destSlot].getClientProperty("nameFieldSetter");
                        if (s != null) s.accept(names[i]);
                        slots[destSlot].putClientProperty("nameEdited", Boolean.TRUE);
                    }
                }
            }
            slots[lastFilled].removeAll();
            slots[lastFilled].putClientProperty("type", null);
            slots[lastFilled].revalidate();
            slots[lastFilled].repaint();

            playerCount--;
            updateButtonState();
            refreshAILabels();
            refreshHumanLabels();
            SwingUtilities.invokeLater(() -> layoutComponents());
        });

        slot.add(deleteButton);
        slot.add(nameField);
        slot.putClientProperty("deleteButton", deleteButton);
        slot.putClientProperty("nameField",    nameField);
        slot.revalidate();
        slot.repaint();
    }


    private void refreshHumanLabels() {
        int humanCounter = 0;
        for (JPanel slot : slots) {
            if (slot == null || slot.getComponentCount() == 0) continue;
            if ("AI".equals(slot.getClientProperty("type"))) continue;
            humanCounter++;
            if (Boolean.TRUE.equals(slot.getClientProperty("nameEdited"))) continue;
            @SuppressWarnings("unchecked")
            java.util.function.Consumer<String> setter =
                (java.util.function.Consumer<String>) slot.getClientProperty("nameFieldSetter");
            String label = "Player " + humanCounter;
            if (setter != null) {
                setter.accept(label);
            } else {
                for (Component c : slot.getComponents()) {
                    if (c instanceof JTextField) { ((JTextField) c).setText(label); break; }
                }
                slot.putClientProperty("nameEdited", Boolean.FALSE);
            }
        }
    }

    private void fillSlotWithAI(int slotIndex, int number) {
        JPanel slot = slots[slotIndex];
        slot.removeAll();
        slot.setLayout(null);
        slot.putClientProperty("type", "AI");

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/trash_icon.png"));
        int iconSize = (int)(jLayeredPane1.getHeight() * 0.025);
        Image scaled = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        JButton deleteButton = new JButton(new ImageIcon(scaled));
        int btnSize = (int)(jLayeredPane1.getHeight() * 0.033);
        deleteButton.setPreferredSize(new Dimension(btnSize, btnSize));
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setOpaque(false);
        deleteButton.addActionListener(e -> {
            slots[slotIndex].setBorder(null);
            int lastFilled = slotIndex;
            for (int i = slotIndex + 1; i < 6; i++) {
                if (slots[i].getComponentCount() > 0) lastFilled = i;
                else break;
            }
            int count = lastFilled - slotIndex;
            String[]  names  = new String[count];
            boolean[] isAIs  = new boolean[count];
            boolean[] edited = new boolean[count];
            for (int i = 0; i < count; i++) {
                JPanel next = slots[slotIndex + 1 + i];
                isAIs[i]  = "AI".equals(next.getClientProperty("type"));
                edited[i] = Boolean.TRUE.equals(next.getClientProperty("nameEdited"));
                names[i]  = "";
                for (Component c : next.getComponents()) {
                    if (c instanceof JTextField) { names[i] = ((JTextField) c).getText(); break; }
                }
            }
            for (int i = 0; i < count; i++) {
                int destSlot = slotIndex + i;
                if (isAIs[i]) {
                    fillSlotWithAI(destSlot, 0);
                } else {
                    fillSlotWithHuman(destSlot, 0);
                    if (edited[i]) {
                        @SuppressWarnings("unchecked")
                        java.util.function.Consumer<String> s =
                            (java.util.function.Consumer<String>) slots[destSlot].getClientProperty("nameFieldSetter");
                        if (s != null) s.accept(names[i]);
                        slots[destSlot].putClientProperty("nameEdited", Boolean.TRUE);
                    }
                }
            }
            slots[lastFilled].removeAll();
            slots[lastFilled].putClientProperty("type", null);
            slots[lastFilled].revalidate();
            slots[lastFilled].repaint();
            playerCount--;
            updateButtonState();
            refreshAILabels();
            refreshHumanLabels();
            SwingUtilities.invokeLater(() -> layoutComponents());
        });

        JLabel nameLabel = new JLabel("AI Player") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 170, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        nameLabel.setOpaque(false);
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));

        JLabel easyLabel = new JLabel("Easy");
        easyLabel.setForeground(Color.BLACK);
        easyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JSlider difficultySlider = new JSlider(0, 1, 0);
        difficultySlider.setUI(new ThemedSliderUI(difficultySlider));
        difficultySlider.setOpaque(false);
        difficultySlider.setBackground(new Color(0, 0, 0, 0));
        JLabel hardLabel = new JLabel("Hard");
        hardLabel.setForeground(Color.BLACK);
        hardLabel.setHorizontalAlignment(SwingConstants.LEFT);

        difficultySlider.addChangeListener(ev -> refreshAILabels());

        slot.add(deleteButton);
        slot.add(nameLabel);
        slot.add(easyLabel);
        slot.add(difficultySlider);
        slot.add(hardLabel);

        slot.putClientProperty("deleteButton", deleteButton);
        slot.putClientProperty("nameLabel",nameLabel);
        slot.putClientProperty("easyLabel",easyLabel);
        slot.putClientProperty("difficultySlider",difficultySlider);
        slot.putClientProperty("hardLabel", hardLabel);

        slot.revalidate();
        slot.repaint();
    }

    private void refreshAILabels() {
        java.util.List<JPanel> easySlots = new java.util.ArrayList<>();
        java.util.List<JPanel> hardSlots = new java.util.ArrayList<>();
        for (JPanel slot : slots) {
            if (slot == null || !("AI".equals(slot.getClientProperty("type")))) continue;
            Object ds = slot.getClientProperty("difficultySlider");
            if (ds instanceof JSlider) {
                if (((JSlider) ds).getValue() == 0) easySlots.add(slot);
                else                                hardSlots.add(slot);
            }
        }
        boolean multiEasy = easySlots.size() > 1;
        boolean multiHard = hardSlots.size() > 1;
        for (int i = 0; i < easySlots.size(); i++) {
            Object nl = easySlots.get(i).getClientProperty("nameLabel");
            if (nl instanceof JLabel)
                ((JLabel) nl).setText(multiEasy ? "Easy AI Player " + (i + 1) : "Easy AI Player");
        }
        for (int i = 0; i < hardSlots.size(); i++) {
            Object nl = hardSlots.get(i).getClientProperty("nameLabel");
            if (nl instanceof JLabel)
                ((JLabel) nl).setText(multiHard ? "Hard AI Player " + (i + 1) : "Hard AI Player");
        }
    }

    private int getRowCount() {
        int count = 0;
        for (JPanel slot : slots) {
            if (slot != null && slot.getComponentCount() > 0) count++;
        }
        return count;
    }



    @SuppressWarnings("unchecked")
    // DONT EDIT PLEASSSSE
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
		Title = new OutlinedLabel("Yahtzee");
        jPanel2 = new javax.swing.JPanel();
		PlayerButton = new RoundedButton("Add Player");
        AI_PlayerButton = new RoundedButton("Add AI Player");
        StartButton = new RoundedButton("Start");
        jPanel3 = new GlassPanel();

        jPanel6.setBackground(new java.awt.Color(255, 255, 153));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 204));

        jLayeredPane1.setBackground(new java.awt.Color(255, 255, 204));

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        Title.setFont(new java.awt.Font("Bauhaus 93", 1, 48));
        Title.setForeground(new java.awt.Color(204, 0, 51));
        Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        PlayerButton.setBackground(new java.awt.Color(255, 255, 204));
        PlayerButton.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 18));
        PlayerButton.setText("Add Player");
        PlayerButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PlayerButtonActionPerformed(evt);
                try
                {
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        });

        AI_PlayerButton.setBackground(new java.awt.Color(255, 255, 204));
        AI_PlayerButton.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 18));
        AI_PlayerButton.setText("Add AI Player");
        AI_PlayerButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AI_PlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AI_PlayerButtonActionPerformed(evt);
            }
        });

        StartButton.setBackground(new java.awt.Color(255, 255, 204));
        StartButton.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 24));
        StartButton.setText("Start");
        StartButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        StartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(PlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(AI_PlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(StartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(369, 369, 369)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AI_PlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(StartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 204));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(136, 136, 136)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLayeredPane1.add(jPanel1);
        jPanel1.setBounds(-10, 0, 474, 357);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        pack();
    }// </editor-fold>                        

    private void StartButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            

        int idCounter = 1;

            for (Component comp : jPanel3.getComponents()) {
                if (!(comp instanceof JPanel)) continue;
                if (((JPanel) comp).getComponentCount() == 0) continue;

            JPanel row = (JPanel) comp;
            boolean isAI = "AI".equals(row.getClientProperty("type"));

            if (isAI) {
                Object ds  = row.getClientProperty("difficultySlider");
                Object nl  = row.getClientProperty("nameLabel");
                if (ds instanceof JSlider) {
                    String aiName = (nl instanceof JLabel) ? ((JLabel) nl).getText() : ("AI Player " + idCounter);
                    AIPlayer ai = new AIPlayer(idCounter++);
                    ai.setUsername(aiName);
                    if (((JSlider) ds).getValue() == 0) {
                        ai.setStrategy(new EasyYahtzeeAI());
                    } else {
                        ai.setStrategy(new MediumYahtzeeAI());
                    }
                    tm.addPlayer(ai);
                }
            } else {
                Object nf = row.getClientProperty("nameField");
                if (nf instanceof JTextField) {
                    tm.addPlayer(new Player(((JTextField) nf).getText()));
                    idCounter++;
                }
            }
        }
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        YahtzeeDesign y = new YahtzeeDesign(tm);
        y.setVisible(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
        this.setVisible(false);
    }                                           

    private void AI_PlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int slot = firstEmptySlot();
        if (slot == -1) return;
        fillSlotWithAI(slot, 0);
        playerCount = getRowCount();
        updateButtonState();
        refreshAILabels();
        SwingUtilities.invokeLater(() -> layoutComponents());
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }                                               

    private void PlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int slot = firstEmptySlot();
        if (slot == -1) return;
        fillSlotWithHuman(slot, 0); 
        playerCount = getRowCount();
        updateButtonState();
        refreshHumanLabels();
        SwingUtilities.invokeLater(() -> layoutComponents());
        try
        {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/Click.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }                                            

    // Variables declaration - do not modify   PLEASE               
	private RoundedButton PlayerButton;
	private RoundedButton AI_PlayerButton;
	private RoundedButton StartButton;
	private OutlinedLabel Title;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private GlassPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration                   
}

class OutlinedLabel extends JLabel {

    public OutlinedLabel(String text) {
        super(text);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = getText();
        FontMetrics fm = g2.getFontMetrics(getFont());
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        int outline = Math.max(2, getFont().getSize() / 24);

        g2.setColor(Color.BLACK);
        for (int dx = -outline; dx <= outline; dx++) {
            for (int dy = -outline; dy <= outline; dy++) {
                if (dx != 0 || dy != 0) {
                    g2.drawString(text, x + dx, y + dy);
                }
            }
        }

        g2.setColor(getForeground());
        g2.drawString(text, x, y);

        g2.dispose();
    }
}

class ThemedSliderUI extends BasicSliderUI {

    private final Color trackColor = new Color(140, 70, 20, 180);
    private final Color thumbColor = new Color(230, 120, 40);

    public ThemedSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int trackHeight = 4;
        int y = trackRect.y + (trackRect.height - trackHeight) / 2;

        g2.setColor(trackColor);
        g2.fillRoundRect(trackRect.x, y, trackRect.width, trackHeight, trackHeight, trackHeight);

        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = 22;
        int x = thumbRect.x + (thumbRect.width - size) / 2;
        int y = thumbRect.y + (thumbRect.height - size) / 2;

        g2.setColor(thumbColor);
        g2.fillOval(x, y, size, size);

        g2.setColor(new Color(140, 70, 20));
        g2.drawOval(x, y, size, size);

        g2.dispose();
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(24, 24);
    }
}

class GlassPanel extends JPanel {

    public GlassPanel() {
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

class RoundedButton extends JButton {

    private boolean hover = false;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setForeground(Color.BLACK);
    }

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hover = true;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = getHeight();

        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRoundRect(3, 6, getWidth()-6, getHeight()-6, arc, arc);

        Color fill = !isEnabled()
                ? new Color(190, 95, 25)
                : hover
                    ? new Color(255, 140, 50)
                    : new Color(230, 120, 40);

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, arc, arc);

        g2.setColor(Color.BLACK);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
        // intentionally NOT calling super.paintComponent 
    }
}

class LengthFilter extends DocumentFilter {
    private final int max;

    public LengthFilter(int max) {
        this.max = max;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) return;
        if (fb.getDocument().getLength() + string.length() <= max) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) return;
        int current = fb.getDocument().getLength();
        int newLength = current - length + text.length();
        if (newLength <= max) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

class RoundedTextField extends JTextField {
    private Color fill = new Color(255, 170, 60);

    public RoundedTextField(String text, int columns) {
        super(text, columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new LengthFilter(12));
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        int fontSize = Math.max(18, (int)(h * 0.45));
        setFont(new Font("TW Cen MT Condensed", Font.BOLD, fontSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        super.paintComponent(g);
        g2.dispose();
    }
}

class ArchPanel extends JPanel {

    private final Color yellow = new Color(247, 201, 72);
    private final Color lightOrange = new Color(255, 170, 60);
    private final Color darkOrange = new Color(230, 120, 40);

    public ArchPanel() {
        setOpaque(false);
    }

    private void drawArchStripe(Graphics2D g2, int ax, int ay, int aw, int ah, int h) {
        double midY = ay + ah / 2.0;

        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
        path.moveTo(ax, h);
        path.lineTo(ax, midY);
        path.append(new java.awt.geom.Arc2D.Double(
            ax, ay, aw, ah, 180, -180, java.awt.geom.Arc2D.OPEN), true);
        path.lineTo(ax + aw, h);

        g2.draw(path);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getWidth() == 0 || getHeight() == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int arcHeight  = (int)(h * 0.83);
        int sideMargin = (int)(w * 0.013);
        int shrink     = arcHeight / 30;
        int arcOffsetY = (int)(h * 0.026);
        int outlineExtra = (int)(Math.max(2, h * 0.004));

        int outerStroke  = (int)(h * 0.043);
        int middleStroke = (int)(h * 0.037);
        int innerStroke  = (int)(h * 0.031);

        int[] strokes    = { outerStroke, middleStroke, innerStroke };
        Color[] colors   = { yellow, lightOrange, darkOrange };

        {
            int ax0 = sideMargin;
            int ay0 = arcOffsetY;
            int aw0 = w - sideMargin * 2;
            int ah0 = arcHeight;

            java.awt.geom.GeneralPath interior = new java.awt.geom.GeneralPath();
            interior.moveTo(ax0, h);
            interior.lineTo(ax0, ay0 + ah0 / 2.0);
            interior.append(new java.awt.geom.Arc2D.Double(
                ax0, ay0, aw0, ah0, 180, -180, java.awt.geom.Arc2D.OPEN), true);
            interior.lineTo(ax0 + aw0, h);
            interior.closePath();

            java.awt.geom.Area blackArea = new java.awt.geom.Area(new java.awt.Rectangle(0, 0, w, h));
            blackArea.subtract(new java.awt.geom.Area(interior));
            g2.setColor(Color.BLACK);
            g2.fill(blackArea);

            g2.setColor(new Color(250, 235, 137));
            g2.fill(interior);
        }

        for (int i = 0; i < 3; i++) {
            int stroke = strokes[i];
            Color color = colors[i];
            int s  = shrink * i;
            int ax = sideMargin + s;
            int ay = arcOffsetY + s;
            int aw = w - (sideMargin + s) * 2;
            int ah = arcHeight - s * 2;

            g2.setStroke(new BasicStroke(stroke + outlineExtra,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.setColor(Color.BLACK);
            drawArchStripe(g2, ax, ay, aw, ah, h);

            g2.setStroke(new BasicStroke(stroke,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.setColor(color);
            drawArchStripe(g2, ax, ay, aw, ah, h);
        }

        g2.dispose();
    }
}