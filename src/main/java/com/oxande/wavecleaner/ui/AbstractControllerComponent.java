package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class AbstractControllerComponent extends JPanel {
   private JPanel jpanel1 = new JPanel();
   protected JCheckBox crackle = new JCheckBox();
   protected JSlider crackle_factor = new JSlider();
   protected JSlider crackle_average = new JSlider();
   protected JSlider crackle_window = new JSlider();

   public void initComponents()
   {
      this.setLayout(new BorderLayout());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel1.setLayout(layout1);
      
      crackle.setText("Decrackle");
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle, c1);
      jpanel1.add(crackle);
      
      crackle_factor.setMaximum(100);
      crackle_factor.setMinimum(1);
      crackle_factor.setOrientation(JSlider.HORIZONTAL);
      crackle_factor.setValue(2);
      c1.gridy = 0;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_factor, c1);
      jpanel1.add(crackle_factor);
      
      crackle_average.setMaximum(100);
      crackle_average.setMinimum(0);
      crackle_average.setOrientation(JSlider.HORIZONTAL);
      crackle_average.setValue(3);
      c1.gridy = 0;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_average, c1);
      jpanel1.add(crackle_average);
      
      crackle_window.setMaximum(20000);
      crackle_window.setMinimum(1);
      crackle_window.setOrientation(JSlider.HORIZONTAL);
      crackle_window.setValue(2000);
      c1.gridy = 0;
      c1.gridx = 3;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_window, c1);
      jpanel1.add(crackle_window);
      this.add(jpanel1, BorderLayout.CENTER);
      this.setPreferredSize(new java.awt.Dimension(600,400));
      this.setName("com.oxande.wavecleaner.ui.AbstractControllerComponent");
   }
}

