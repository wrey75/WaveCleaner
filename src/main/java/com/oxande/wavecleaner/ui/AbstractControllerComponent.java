package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
   protected JSlider crackle_average = new JSlider();
   protected com.oxande.swing.JMeter crackle_window = new com.oxande.swing.JMeter();
   private JLabel jlabel1 = new JLabel();
   private JLabel jlabel2 = new JLabel();
   private JLabel jlabel3 = new JLabel();
   protected JCheckBox click = new JCheckBox();
   protected JSlider thresold_factor = new JSlider();
   protected JSlider declick_window = new JSlider();
   private JPanel jpanel2 = new JPanel();
   private FlowLayout flowlayout1 = new FlowLayout();
   protected JPanel preampPanel = new JPanel();
   protected com.oxande.swing.JToggleSelect output = new com.oxande.swing.JToggleSelect();
   protected com.oxande.swing.JMeter volume = new com.oxande.swing.JMeter();
public class ChangeListener1 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         crackleAverageChanged();
      }
}

public class ChangeListener2 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         clickThresoldChanged();
      }
}

public class ChangeListener3 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         clickWindowChanged();
      }
}

private class SetCrackleAverageLabelClass implements Runnable {
      private String input;

      public void run()
      {
         jlabel2.setText(String.valueOf(input));
      }

      public  SetCrackleAverageLabelClass(String input)
      {
         this.input = input;
      }
}


   protected void clickWindowChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   public void setCrackleAverageLabel(String in)
   {
      SwingUtilities.invokeLater(new SetCrackleAverageLabelClass(in));
   }

   protected void clickThresoldChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   protected void crackleAverageChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   public String getCrackleAverageLabel()
   {
      return jlabel2.getText();
   }

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
      crackle_average.setMinimumSize(new java.awt.Dimension(100,50));
      crackle_average.setMaximum(100);
      crackle_average.setMinimum(0);
      crackle_average.setOrientation(JSlider.HORIZONTAL);
      crackle_average.setValue(3);
      crackle_average.addChangeListener(new ChangeListener1());
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
      
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel1.add(jlabel1);
      
      jlabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      c1.gridy = 1;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel2, c1);
      jpanel1.add(jlabel2);
      
      c1.gridy = 1;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel3, c1);
      jpanel1.add(jlabel3);
      
      click.setText("Remove clicks");
      click.setFocusable(false);
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(click, c1);
      jpanel1.add(click);
      
      thresold_factor.setMinimumSize(new java.awt.Dimension(100,50));
      thresold_factor.setMaximum(900);
      thresold_factor.setMinimum(0);
      thresold_factor.setOrientation(JSlider.HORIZONTAL);
      thresold_factor.setValue(200);
      thresold_factor.addChangeListener(new ChangeListener2());
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(thresold_factor, c1);
      jpanel1.add(thresold_factor);
      
      declick_window.setMinimumSize(new java.awt.Dimension(100,50));
      declick_window.setMaximum(40);
      declick_window.setMinimum(0);
      declick_window.setOrientation(JSlider.HORIZONTAL);
      declick_window.setValue(20);
      declick_window.addChangeListener(new ChangeListener3());
      c1.gridy = 2;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(declick_window, c1);
      jpanel1.add(declick_window);
      
      jpanel2.setLayout(flowlayout1);
      jpanel2.add(preampPanel);
      jpanel2.add(output);
      jpanel2.add(volume);
      c1.gridy = 3;
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

