package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
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
   protected JCheckBox crackle = new JCheckBox();
   protected JSlider crackle_factor = new JSlider();
   protected JSlider crackle_average = new JSlider();
   protected JSlider crackle_window = new JSlider();
   private JLabel jlabel1 = new JLabel();
   private JLabel jlabel2 = new JLabel();
   private JLabel jlabel3 = new JLabel();
   private JLabel jlabel4 = new JLabel();
   protected JSlider volume = new JSlider();
   private JLabel jlabel5 = new JLabel();
   private JPanel jpanel2 = new JPanel();
   private FlowLayout flowlayout1 = new FlowLayout();
   protected JToggleButton finalOutput = new JToggleButton();
   ButtonGroup audioOut = new ButtonGroup();
   protected JToggleButton originalOutput = new JToggleButton();
   protected JToggleButton diffOutput = new JToggleButton();
   protected JToggleButton leftRightOutput = new JToggleButton();
public class ChangeListener1 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         crackleFactorChanged();
      }
}

public class ChangeListener2 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         crackleAverageChanged();
      }
}

private class SetCrackleWindowLabelClass implements Runnable {
      private String input;

      public  SetCrackleWindowLabelClass(String input)
      {
         this.input = input;
      }

      public void run()
      {
         jlabel3.setText(String.valueOf(input));
      }
}

public class ChangeListener3 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         crackleWindowChanged();
      }
}

private class SetCrackleFactorLabelClass implements Runnable {
      private String input;

      public  SetCrackleFactorLabelClass(String input)
      {
         this.input = input;
      }

      public void run()
      {
         jlabel1.setText(String.valueOf(input));
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

public class ChangeListener4 implements javax.swing.event.ChangeListener {

      public void stateChanged(ChangeEvent e)
      {
         volumeChanged();
      }
}


   /**
    * Called by the menu item <i>NORMAL</i>.
    */
   protected void onFinalOutput()
   {
      JOptionPane.showMessageDialog(finalOutput, "Not implemented.",finalOutput.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public String getCrackleFactorLabel()
   {
      return jlabel1.getText();
   }

   public void setCrackleAverageLabel(String in)
   {
      SwingUtilities.invokeLater(new SetCrackleAverageLabelClass(in));
   }

   protected void crackleAverageChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   /**
    * Called by the menu item <i>DIFF</i>.
    */
   protected void onDiffOutput()
   {
      JOptionPane.showMessageDialog(diffOutput, "Not implemented.",diffOutput.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public void initComponents()
   {
      this.setLayout(new BorderLayout());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel1.setLayout(layout1);
      
      crackle.setText("Decrackle");
      crackle.setFocusable(false);
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 2;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle, c1);
      jpanel1.add(crackle);
      
      crackle_factor.setMinimumSize(new java.awt.Dimension(100,50));
      crackle_factor.setMaximum(100);
      crackle_factor.setMinimum(1);
      crackle_factor.setOrientation(JSlider.HORIZONTAL);
      crackle_factor.setValue(2);
      crackle_factor.addChangeListener(new ChangeListener1());
      c1.gridy = 0;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_factor, c1);
      jpanel1.add(crackle_factor);
      
      crackle_average.setMinimumSize(new java.awt.Dimension(100,50));
      crackle_average.setMaximum(100);
      crackle_average.setMinimum(0);
      crackle_average.setOrientation(JSlider.HORIZONTAL);
      crackle_average.setValue(3);
      crackle_average.addChangeListener(new ChangeListener2());
      c1.gridy = 0;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_average, c1);
      jpanel1.add(crackle_average);
      
      crackle_window.setMinimumSize(new java.awt.Dimension(100,50));
      crackle_window.setMaximum(20000);
      crackle_window.setMinimum(1);
      crackle_window.setOrientation(JSlider.HORIZONTAL);
      crackle_window.setValue(2000);
      crackle_window.addChangeListener(new ChangeListener3());
      c1.gridy = 0;
      c1.gridx = 3;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(crackle_window, c1);
      jpanel1.add(crackle_window);
      
      jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      c1.gridy = 1;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel1.add(jlabel1);
      
      jlabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      c1.gridy = 1;
      c1.gridx = 2;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel2, c1);
      jpanel1.add(jlabel2);
      
      jlabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      c1.gridy = 1;
      c1.gridx = 3;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel3, c1);
      jpanel1.add(jlabel3);
      
      jlabel4.setText("Volume");
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel4, c1);
      jpanel1.add(jlabel4);
      
      volume.setMinimumSize(new java.awt.Dimension(100,50));
      volume.setMaximum(24);
      volume.setMinimum(-24);
      volume.setOrientation(JSlider.HORIZONTAL);
      volume.setValue(0);
      volume.addChangeListener(new ChangeListener4());
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 3;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 3;
      layout1.setConstraints(volume, c1);
      jpanel1.add(volume);
      
      jlabel5.setText("OUTPUT SELECTOR");
      c1.gridy = 3;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel5, c1);
      jpanel1.add(jlabel5);
      
      flowlayout1.setHgap(0);
      jpanel2.setLayout(flowlayout1);
      audioOut.add(finalOutput);
      finalOutput.setText("NORMAL");
      finalOutput.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onFinalOutput();
   }
}

);
      jpanel2.add(finalOutput);
      audioOut.add(originalOutput);
      originalOutput.setText("ORIGIN");
      originalOutput.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onOriginalOutput();
   }
}

);
      jpanel2.add(originalOutput);
      audioOut.add(diffOutput);
      diffOutput.setText("DIFF");
      diffOutput.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onDiffOutput();
   }
}

);
      jpanel2.add(diffOutput);
      audioOut.add(leftRightOutput);
      leftRightOutput.setText("L/R");
      leftRightOutput.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onLeftRightOutput();
   }
}

);
      jpanel2.add(leftRightOutput);
      c1.gridy = 3;
      c1.gridx = 1;
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

   public void setCrackleFactorLabel(String in)
   {
      SwingUtilities.invokeLater(new SetCrackleFactorLabelClass(in));
   }

   public void setCrackleWindowLabel(String in)
   {
      SwingUtilities.invokeLater(new SetCrackleWindowLabelClass(in));
   }

   protected void volumeChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   /**
    * Called by the menu item <i>L/R</i>.
    */
   protected void onLeftRightOutput()
   {
      JOptionPane.showMessageDialog(leftRightOutput, "Not implemented.",leftRightOutput.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>ORIGIN</i>.
    */
   protected void onOriginalOutput()
   {
      JOptionPane.showMessageDialog(originalOutput, "Not implemented.",originalOutput.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   protected void crackleFactorChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   public String getCrackleWindowLabel()
   {
      return jlabel3.getText();
   }

   protected void crackleWindowChanged()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   public String getCrackleAverageLabel()
   {
      return jlabel2.getText();
   }
}

