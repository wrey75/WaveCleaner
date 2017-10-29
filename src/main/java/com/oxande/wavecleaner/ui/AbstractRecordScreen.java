package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
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
public class AbstractRecordScreen extends JDialog {
   private JPanel jpanel1 = new JPanel();
   private JPanel jpanel2 = new JPanel();
   private JLabel jlabel1 = new JLabel();
   private JButton jbutton1 = new JButton();

   /**
    * Called by the menu item <i>START RECORDING</i>.
    */
   protected void startRecord()
   {
      JOptionPane.showMessageDialog(jbutton1, "Not implemented.",jbutton1.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public static void main(String[] args)
   {
      AbstractRecordScreen appl = new AbstractRecordScreen();
      appl.initComponents();
      appl.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      appl.setVisible(true);
   }

   public void initComponents()
   {
      jpanel1.setLayout(new BorderLayout());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel2.setLayout(layout1);
      
      jlabel1.setText("You must have the turnable running (the disc turns) and the stylus just above the disc. Then you can start recording.");
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel2.add(jlabel1);
      
      jbutton1.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      startRecord();
   }
}

);
      jbutton1.setText("START RECORDING");
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jbutton1, c1);
      jpanel2.add(jbutton1);
      jpanel1.add(jpanel2, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.pack();
      this.setPreferredSize(new java.awt.Dimension(400,200));
      this.setName("com.oxande.wavecleaner.ui.AbstractRecordScreen");
      this.setLocationByPlatform(true);
      this.setTitle("Recording");
   }
}

