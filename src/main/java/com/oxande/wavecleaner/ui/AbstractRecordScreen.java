package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
   private JLabel jlabel2 = new JLabel();
   protected JComboBox inputLine = new JComboBox();
   private JButton jbutton1 = new JButton();
   private JButton jbutton2 = new JButton();
public class SimpleMapEntry implements java.util.Map.Entry<String,String> {
      private String key;
      private String value;

      public  SimpleMapEntry(java.lang.String key, java.lang.String value)
      {
         this.key = key;
         this.value = value;
      }

      public String getKey()
      {
         return key;
      }

      public String getValue()
      {
         return value;
      }

      public String setValue(String value)
      {
         String old = this.value;
         this.value = value.toString();
         return old;
      }

      public String toString()
      {
         return this.value;
      }
}


   /**
    * Called by the menu item <i>START RECORDING</i>.
    */
   protected void startRecord()
   {
      JOptionPane.showMessageDialog(jbutton1, "Not implemented.",jbutton1.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>END RECORDING</i>.
    */
   protected void endRecord()
   {
      JOptionPane.showMessageDialog(jbutton2, "Not implemented.",jbutton2.getText(), JOptionPane.INFORMATION_MESSAGE);
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
      
      jlabel1.setText("<html>You must have the turnable running (the disc turns) and \n"
 +    "\t\t\tthe stylus just above the disc. Then you can start recording.</html>");
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel2.add(jlabel1);
      
      jlabel2.setText("Select your input:");
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel2, c1);
      jpanel2.add(jlabel2);
      
      c1.gridy = 1;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(inputLine, c1);
      jpanel2.add(inputLine);
      
      jbutton1.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      startRecord();
   }
}

);
      jbutton1.setText("START RECORDING");
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jbutton1, c1);
      jpanel2.add(jbutton1);
      
      jbutton2.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      endRecord();
   }
}

);
      jbutton2.setText("END RECORDING");
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jbutton2, c1);
      jpanel2.add(jbutton2);
      jpanel1.add(jpanel2, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.pack();
      this.setPreferredSize(new java.awt.Dimension(400,200));
      this.setName("com.oxande.wavecleaner.ui.AbstractRecordScreen");
      this.setLocationByPlatform(true);
      this.setTitle("Recording");
   }
}

