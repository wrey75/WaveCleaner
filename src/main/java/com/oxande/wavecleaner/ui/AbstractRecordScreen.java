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
   protected RealtimeWaveComponent fastWave = new RealtimeWaveComponent();
   private JLabel jlabel2 = new JLabel();
   private JLabel jlabel3 = new JLabel();
   private JLabel jlabel4 = new JLabel();
   protected JButton recStart = new JButton();
   protected JButton recStop = new JButton();

   /**
    * Called by the menu item <i>START RECORDING</i>.
    */
   protected void startRecord()
   {
      JOptionPane.showMessageDialog(recStart, "Not implemented.",recStart.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>END RECORDING</i>.
    */
   protected void endRecord()
   {
      JOptionPane.showMessageDialog(recStop, "Not implemented.",recStop.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public static void main(String[] args)
   {
      AbstractRecordScreen appl = new AbstractRecordScreen();
      appl.initComponents();
      appl.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
      c1.gridwidth = 2;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 2;
      layout1.setConstraints(jlabel1, c1);
      jpanel2.add(jlabel1);
      
      c1.gridy = 0;
      c1.gridx = 2;
      c1.gridheight = 3;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(fastWave, c1);
      jpanel2.add(fastWave);
      
      jlabel2.setText("Due to a technical limitation of your sound library, you must select your input directly through your operating system bef"
 +    "ore starting the program. Please refer to the documentation of the project.");
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 2;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 2;
      layout1.setConstraints(jlabel2, c1);
      jpanel2.add(jlabel2);
      
      jlabel3.setText("Follow your recording on the left.");
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel3, c1);
      jpanel2.add(jlabel3);
      
      jlabel4.setText("TODO: The record will be saved as \"noname.wav\" until you save the project.");
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel4, c1);
      jpanel2.add(jlabel4);
      
      recStart.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      startRecord();
   }
}

);
      recStart.setText("START RECORDING");
      c1.gridy = 3;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(recStart, c1);
      jpanel2.add(recStart);
      
      recStop.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      endRecord();
   }
}

);
      recStop.setText("END RECORDING");
      c1.gridy = 3;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(recStop, c1);
      jpanel2.add(recStop);
      jpanel1.add(jpanel2, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.pack();
      this.setPreferredSize(new java.awt.Dimension(400,200));
      this.setName("com.oxande.wavecleaner.ui.AbstractRecordScreen");
      this.setLocationByPlatform(true);
      this.setTitle("Recording");
   }
}

