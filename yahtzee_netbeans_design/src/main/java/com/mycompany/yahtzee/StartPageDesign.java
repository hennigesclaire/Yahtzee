/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.yahtzee;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.util.Hashtable;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.util.List;
import com.mycompany.yahtzee.AIPlayer; 



/**
 *
 * @author henni
 */
public class StartPageDesign extends javax.swing.JFrame {

    private TurnManager tm;  
    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(StartPageDesign.class.getName());
    private int playerCount = 1;

    /**
     * Creates new form StartPageDesign
     */
    public StartPageDesign(TurnManager t) {   
        this.tm = t;                         
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initComponents();
        //createHostRow();

        ImageIcon icon1 = (ImageIcon) jLabel2.getIcon();
        Image image1 = icon1.getImage().getScaledInstance(
            jLabel2.getWidth(), jLabel2.getHeight(), Image.SCALE_SMOOTH);
        jLabel2.setIcon(new ImageIcon(image1));

        ImageIcon icon2 = (ImageIcon) jLabel3.getIcon();
        Image image2 = icon2.getImage().getScaledInstance(
            jLabel3.getWidth(), jLabel3.getHeight(), Image.SCALE_SMOOTH);
        jLabel3.setIcon(new ImageIcon(image2));
        playerCount=0;
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
    jButton1.setEnabled(!full);
    jButton2.setEnabled(!full);
    }
    private JPanel createHostRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JLabel spacer = new JLabel(); 
        spacer.setPreferredSize(new Dimension(28, 28)); 

        JTextField nameField = new JTextField("Player 1", 12);
        row.add(spacer); 
        row.add(nameField);



        jPanel3.add(row);
        jPanel3.add(Box.createRigidArea(new Dimension(0, 8)));
        jPanel3.revalidate();
        jPanel3.repaint();

        return row;
    }


    private JPanel createHumanPlayerRow(int number) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JTextField nameField = new JTextField("Player " + number, 12);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/trash_icon.png"));
        Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton deleteButton1 = new JButton(new ImageIcon(scaled));
        deleteButton1.setPreferredSize(new Dimension(28, 28)); 
        deleteButton1.setBorderPainted(false); 
        deleteButton1.setContentAreaFilled(false); 
        deleteButton1.addActionListener(e -> {
            playerCount--;
            jPanel3.remove(row);
            jPanel3.revalidate();
            jPanel3.repaint();
           updateButtonState();

        });
        row.add(deleteButton1);

        row.add(nameField);
        row.add(Box.createHorizontalStrut(35)); 

        return row;
    }
    private JPanel createAIPlayerRow(int number) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.putClientProperty("type", "AI");
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JTextField nameField = new JTextField("AI Player " + number, 12);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/trash_icon.png"));
        Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton deleteButton = new JButton(new ImageIcon(scaled));        
        deleteButton.setPreferredSize(new Dimension(28, 28)); 
        deleteButton.setBorderPainted(false); 
        deleteButton.setContentAreaFilled(false); 
        deleteButton.addActionListener(e -> {
            playerCount--;
           jPanel3.remove(row);
           jPanel3.revalidate();
           jPanel3.repaint();
           updateButtonState();
        
       });


        JSlider diffSlider = new JSlider(0, 1, 0);
        diffSlider.setPaintTicks(false);
        diffSlider.setPaintLabels(false);
        diffSlider.setPreferredSize(new Dimension(100, 20));
        diffSlider.setMaximumSize(new Dimension(100, 20));

        JLabel easyLabel = new JLabel("Easy");
        JLabel hardLabel = new JLabel("Hard");
        row.add(deleteButton);

        row.add(nameField);
        row.add(Box.createHorizontalStrut(15)); 
        row.add(easyLabel);
        row.add(Box.createHorizontalStrut(6));

        row.add(diffSlider);
        row.add(Box.createHorizontalStrut(6));
        row.add(hardLabel);
        

        return row;
    }




    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLayeredPane1.setBackground(new java.awt.Color(102, 0, 0));
        jLayeredPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 0, 0));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Yahtzee");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/1_die.png"))); // NOI18N
        jLabel3.setText("jLabel3");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/1_die.png"))); // NOI18N
        jLabel2.setText("jLabel2");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setBackground(new java.awt.Color(153, 153, 153));
        jButton1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton1.setText("Add Player");
        jButton1.setBorder(new javax.swing.border.MatteBorder(null));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(153, 153, 153));
        jButton2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton2.setText("Add AI Player");
        jButton2.setBorder(new javax.swing.border.MatteBorder(null));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65))
        );

        jButton3.setBackground(new java.awt.Color(153, 153, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton3.setText("Start");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(316, 316, 316)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(137, 137, 137)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(494, 494, 494)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(347, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jLayeredPane1.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 590));

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
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    playerCount++;
    JPanel row = createHumanPlayerRow(playerCount);

    jPanel3.add(row);
    jPanel3.add(Box.createRigidArea(new Dimension(0, 8)));
    jPanel3.revalidate();
    jPanel3.repaint();
    updateButtonState();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    playerCount++;
    JPanel row = createAIPlayerRow(playerCount);

    jPanel3.add(row);
    jPanel3.add(Box.createRigidArea(new Dimension(0, 8)));
    jPanel3.revalidate();
    jPanel3.repaint();
    updateButtonState();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
 
    int idCounter = 1;

    for (Component comp : jPanel3.getComponents()) {
        if (!(comp instanceof JPanel)) continue;

        JPanel row = (JPanel) comp;
        JTextField nameField = null;
        JSlider difficultySlider = null;

        for (Component inner : row.getComponents()) {
            if (inner instanceof JTextField)
                nameField = (JTextField) inner;
            if (inner instanceof JSlider)
                difficultySlider = (JSlider) inner;
        }

        boolean isAI = "AI".equals(row.getClientProperty("type"));

        if (isAI && difficultySlider != null) {
            AIPlayer ai = new AIPlayer(idCounter++);
            //ai.setUsername(nameField.getText());

            if (difficultySlider.getValue() == 0) {
                ai.setStrategy(new EasyYahtzeeAI());
            } else {
                ai.setStrategy(new MediumYahtzeeAI());
            }

            tm.addPlayer(ai);
        } else if (nameField != null) {
            tm.addPlayer(new Player(nameField.getText()));
            idCounter++;
        }
    }

    YahtzeeDesign y = new YahtzeeDesign(tm);
    y.setVisible(true);
        dispose();    }//GEN-LAST:event_jButton3ActionPerformed
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new StartPageDesign().setVisible(true));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
