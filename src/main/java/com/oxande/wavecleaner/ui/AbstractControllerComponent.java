package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class AbstractControllerComponent extends JPanel {
   private JPanel jpanel1 = new JPanel();
   protected JPanel panelCrackle = new JPanel();
   protected JCheckBox crackle = new JCheckBox();
   protected com.oxande.swing.JMeter crackleFactor = new com.oxande.swing.JMeter();
   protected com.oxande.swing.JMeter crackle_average = new com.oxande.swing.JMeter();
   protected com.oxande.swing.JMeter crackle_window = new com.oxande.swing.JMeter();
   protected JPanel panelDeclick = new JPanel();
   protected JCheckBox click = new JCheckBox();
   protected com.oxande.swing.JMeter declickThresold = new com.oxande.swing.JMeter();
   protected com.oxande.swing.JMeter declickWindow = new com.oxande.swing.JMeter();
   private JPanel jpanel2 = new JPanel();
   private FlowLayout flowlayout1 = new FlowLayout();
   protected JPanel preampPanel = new JPanel();
   protected com.oxande.swing.JToggleSelect output = new com.oxande.swing.JToggleSelect();
   protected com.oxande.swing.JMeter volume = new com.oxande.swing.JMeter();

   public void initComponents()
   {
      this.setLayout(new BorderLayout());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel1.setLayout(layout1);
      
      crackle.setText("Decrackle");
      crackle.setFocusable(false);
      panelCrackle.add(crackle);
      panelCrackle.add(crackleFactor);
      panelCrackle.add(crackle_average);
      panelCrackle.add(crackle_window);
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 3;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 3;
      layout1.setConstraints(panelCrackle, c1);
      jpanel1.add(panelCrackle);
      
      click.setText("Remove clicks");
      click.setFocusable(false);
      panelDeclick.add(click);
      panelDeclick.add(declickThresold);
      panelDeclick.add(declickWindow);
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 3;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 3;
      layout1.setConstraints(panelDeclick, c1);
      jpanel1.add(panelDeclick);
      
      jpanel2.setLayout(flowlayout1);
      jpanel2.add(preampPanel);
      jpanel2.add(output);
      jpanel2.add(volume);
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 3;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 3;
      layout1.setConstraints(jpanel2, c1);
      jpanel1.add(jpanel2);
      this.add(jpanel1, BorderLayout.CENTER);
      this.setPreferredSize(new java.awt.Dimension(600,400));
      this.setName("com.oxande.wavecleaner.ui.AbstractControllerComponent");
   }
}

