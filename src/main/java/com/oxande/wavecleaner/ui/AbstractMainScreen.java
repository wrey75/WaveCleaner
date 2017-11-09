package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class AbstractMainScreen extends JFrame {
   private JPanel jpanel1 = new JPanel();
   private JMenuBar jmenubar1 = new JMenuBar();
   private JMenu jmenu1 = new JMenu();
   private JMenuItem jmenuitem1 = new JMenuItem();
   private JMenuItem jmenuitem2 = new JMenuItem();
   private JMenuItem jmenuitem3 = new JMenuItem();
   private JMenuItem jmenuitem4 = new JMenuItem();
   private JMenu jmenu2 = new JMenu();
   private JMenuItem jmenuitem5 = new JMenuItem();
   private JMenuItem jmenuitem6 = new JMenuItem();
   private JMenu jmenu3 = new JMenu();
   private JMenuItem jmenuitem7 = new JMenuItem();
   protected JLabel statusBar = new JLabel();
   protected JToolBar toolbar = new JToolBar();
   private JSplitPane jsplitpane1 = new JSplitPane();
   private JSplitPane jsplitpane2 = new JSplitPane();
   protected WaveComponent instant = new WaveComponent();
   protected VUMeterComponent vuMeter = new VUMeterComponent();
   private JSplitPane jsplitpane3 = new JSplitPane();
   protected com.oxande.wavecleaner.ui.WaveFormComponent song = new com.oxande.wavecleaner.ui.WaveFormComponent();
   protected com.oxande.wavecleaner.ui.ControllerComponent controller = new com.oxande.wavecleaner.ui.ControllerComponent();
public class WindowAdapter1 extends java.awt.event.WindowAdapter {

      public void windowClosing(WindowEvent e)
      {
         onExit();
      }
}

private class SetStatusMessageClass implements Runnable {
      private String input;

      public void run()
      {
         statusBar.setText(String.valueOf(input));
      }

      public  SetStatusMessageClass(String input)
      {
         this.input = input;
      }
}


   /**
    * Called by the menu item <i>Help/About</i>.
    */
   protected void showAboutDlg()
   {
      JOptionPane.showMessageDialog(jmenuitem7, "Not implemented.",jmenuitem7.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public void setStatusMessage(String in)
   {
      SwingUtilities.invokeLater(new SetStatusMessageClass(in));
   }

   /**
    * Called by the menu item <i>Edit/Zoom In</i>.
    */
   protected void onZoomIn()
   {
      JOptionPane.showMessageDialog(jmenuitem5, "Not implemented.",jmenuitem5.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>File/Exit</i>.
    */
   protected void onExit()
   {
      JOptionPane.showMessageDialog(jmenuitem4, "Not implemented.",jmenuitem4.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public String getStatusMessage()
   {
      return statusBar.getText();
   }

   /**
    * Called by the menu item <i>File/Record</i>.
    */
   protected void onRecordSound()
   {
      JOptionPane.showMessageDialog(jmenuitem2, "Not implemented.",jmenuitem2.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>File/Play/Pause</i>.
    */
   protected void onPlayPause()
   {
      JOptionPane.showMessageDialog(jmenuitem3, "Not implemented.",jmenuitem3.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Called by the menu item <i>File/Load the music</i>.
    */
   protected void onLoadSound()
   {
      JOptionPane.showMessageDialog(jmenuitem1, "Not implemented.",jmenuitem1.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public static void main(String[] args)
   {
      AbstractMainScreen appl = new AbstractMainScreen();
      appl.initComponents();
      appl.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      appl.setVisible(true);
   }

   /**
    * Called by the menu item <i>Edit/Zoom Out</i>.
    */
   protected void onZoomOut()
   {
      JOptionPane.showMessageDialog(jmenuitem6, "Not implemented.",jmenuitem6.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public void initComponents()
   {
      jpanel1.setLayout(new BorderLayout());
      jmenu1.setText("File");
      jmenu1.setMnemonic(java.awt.event.KeyEvent.VK_F);
      jmenu1.setDisplayedMnemonicIndex(0);
      jmenuitem1.setText("Load the music");
      jmenuitem1.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onLoadSound();
   }
}

);
      jmenu1.add(jmenuitem1);
      jmenuitem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke("ctrl R"));
      jmenuitem2.setText("Record");
      jmenuitem2.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onRecordSound();
   }
}

);
      jmenu1.add(jmenuitem2);
      jmenuitem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke("SPACE"));
      jmenuitem3.setText("Play/Pause");
      jmenuitem3.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onPlayPause();
   }
}

);
      jmenu1.add(jmenuitem3);
      jmenuitem4.setText("Exit");
      jmenuitem4.setMnemonic(java.awt.event.KeyEvent.VK_X);
      jmenuitem4.setDisplayedMnemonicIndex(1);
      jmenuitem4.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onExit();
   }
}

);
      jmenu1.add(jmenuitem4);
      jmenubar1.add(jmenu1);
      jmenu2.setText("Edit");
      jmenu2.setMnemonic(java.awt.event.KeyEvent.VK_E);
      jmenu2.setDisplayedMnemonicIndex(0);
      jmenuitem5.setName("zoomIn");
      jmenuitem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke("ctrl L"));
      jmenuitem5.setText("Zoom In");
      jmenuitem5.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onZoomIn();
   }
}

);
      jmenu2.add(jmenuitem5);
      jmenuitem6.setName("zoomOut");
      jmenuitem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke("ctrl K"));
      jmenuitem6.setText("Zoom Out");
      jmenuitem6.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      onZoomOut();
   }
}

);
      jmenu2.add(jmenuitem6);
      jmenubar1.add(jmenu2);
      jmenu3.setText("Help");
      jmenu3.setMnemonic(java.awt.event.KeyEvent.VK_H);
      jmenu3.setDisplayedMnemonicIndex(0);
      jmenuitem7.setText("About");
      jmenuitem7.setMnemonic(java.awt.event.KeyEvent.VK_A);
      jmenuitem7.setDisplayedMnemonicIndex(0);
      jmenuitem7.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      showAboutDlg();
   }
}

);
      jmenu3.add(jmenuitem7);
      jmenubar1.add(jmenu3);
      this.setJMenuBar(jmenubar1);
      Border border1 = BorderFactory.createLoweredBevelBorder();
      statusBar.setBorder(border1);
      statusBar.setText("Ready.");
      statusBar.setFont(statusBar.getFont().deriveFont( Font.PLAIN ));
      jpanel1.add(statusBar, BorderLayout.SOUTH);
      toolbar.setOrientation(JToolBar.HORIZONTAL);
      jpanel1.add(toolbar, "North");
      this.addWindowListener(new WindowAdapter1());
      jsplitpane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jsplitpane2.setDividerLocation(0.5);
      jsplitpane2.setTopComponent(instant);
      jsplitpane2.setBottomComponent(vuMeter);
      jsplitpane1.setTopComponent(jsplitpane2);
      jsplitpane3.setMinimumSize(new java.awt.Dimension(100,200));
      jsplitpane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jsplitpane3.setTopComponent(song);
      jsplitpane3.setBottomComponent(controller);
      jsplitpane1.setBottomComponent(jsplitpane3);
      jpanel1.add(jsplitpane1, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.setPreferredSize(new java.awt.Dimension(600,400));
      this.setName("com.oxande.wavecleaner.ui.AbstractMainScreen");
      this.setLocationByPlatform(true);
      this.setTitle("Wave Cleaner");
      this.pack();
   }
}

