package com.oxande.xmlswing.example;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class MainDialog extends JDialog {
   private JPanel jpanel1 = new JPanel();
   private JPanel jpanel2 = new JPanel();
   private JLabel jlabel1 = new JLabel();
   private JLabel jlabel2 = new JLabel();
   private JLabel jlabel3 = new JLabel();
   private JLabel jlabel4 = new JLabel();
   private JLabel jlabel5 = new JLabel();
   private JLabel jlabel6 = new JLabel();
   protected JLabel currentTime = new JLabel();
   private JPanel jpanel3 = new JPanel();
   protected JButton okButton = new JButton();
   protected JButton cancelButton = new JButton();

   public void initComponents()
   {
      jpanel1.setLayout(new BorderLayout());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel2.setLayout(layout1);
      
      jlabel1.setText("LOGO");
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 3;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel2.add(jlabel1);
      
      jlabel2.setText("Written by:");
      c1.gridy = 0;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel2, c1);
      jpanel2.add(jlabel2);
      
      jlabel3.setText("William Rey");
      c1.gridy = 0;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel3, c1);
      jpanel2.add(jlabel3);
      
      jlabel4.setText("Licence:");
      c1.gridy = 1;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel4, c1);
      jpanel2.add(jlabel4);
      
      jlabel5.setText("LGPL (or any other if needed)");
      c1.gridy = 1;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel5, c1);
      jpanel2.add(jlabel5);
      
      jlabel6.setText("Current Time:");
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel6, c1);
      jpanel2.add(jlabel6);
      
      currentTime.setText("???");
      c1.gridy = 2;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(currentTime, c1);
      jpanel2.add(currentTime);
      
      jpanel3.setLayout(new FlowLayout());
      okButton.setDefaultCapable(true);
      okButton.setText("OK");
      okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
      okButton.setDisplayedMnemonicIndex(0);
      okButton.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      JOptionPane.showMessageDialog(okButton, "Not implemented.",okButton.getText(), JOptionPane.INFORMATION_MESSAGE);
   }
}

);
      jpanel3.add(okButton);
      cancelButton.setText("Cancel");
      cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
      cancelButton.setDisplayedMnemonicIndex(0);
      cancelButton.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      dispose();
   }
}

);
      jpanel3.add(cancelButton);
      c1.gridy = 3;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 3;
      c1.anchor = GridBagConstraints.EAST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 3;
      layout1.setConstraints(jpanel3, c1);
      jpanel2.add(jpanel3);
      jpanel1.add(jpanel2, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      getRootPane().setDefaultButton(okButton);
      this.pack();
      this.setName("com.oxande.xmlswing.example.MainDialog");
      this.setLocationByPlatform(true);
      this.setTitle("Dialog Test");
      this.setResizable(false);
      this.setModal(true);
   }

   public static void main(String[] args)
   {
      MainDialog appl = new MainDialog();
      appl.initComponents();
      appl.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      appl.setVisible(true);
   }
}

