package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.Runnable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
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
   private JMenu jmenu2 = new JMenu();
   private JMenuItem jmenuitem4 = new JMenuItem();
   protected JLabel statusBar = new JLabel();
   protected JToolBar toolbar = new JToolBar();
   private JSplitPane jsplitpane1 = new JSplitPane();
   protected JTree pathTree = new JTree();
   protected com.oxande.wavecleaner.ui.WaveFormComponent song = new com.oxande.wavecleaner.ui.WaveFormComponent();
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
      JOptionPane.showMessageDialog(jmenuitem4, "Not implemented.",jmenuitem4.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public void setStatusMessage(String in)
   {
      SwingUtilities.invokeLater(new SetStatusMessageClass(in));
   }

   /**
    * Called by the menu item <i>File/Exit</i>.
    */
   protected void onExit()
   {
      JOptionPane.showMessageDialog(jmenuitem3, "Not implemented.",jmenuitem3.getText(), JOptionPane.INFORMATION_MESSAGE);
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

   public void initComponents()
   {
      jpanel1.setLayout(new BorderLayout());
      jmenu1.setText("File");
      jmenu1.setMnemonic(java.awt.event.KeyEvent.VK_F);
      jmenu1.setDisplayedMnemonicIndex(0);
      jmenuitem1.setText("Load the music");
      jmenuitem1.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Load the music");
   }

   public void actionPerformed(ActionEvent e)
   {
      onLoadSound();
   }
}

);
      jmenu1.add(jmenuitem1);
      jmenuitem2.setText("Record");
      jmenuitem2.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Record");
   }

   public void actionPerformed(ActionEvent e)
   {
      onRecordSound();
   }
}

);
      jmenu1.add(jmenuitem2);
      jmenuitem3.setText("Exit");
      jmenuitem3.setMnemonic(java.awt.event.KeyEvent.VK_X);
      jmenuitem3.setDisplayedMnemonicIndex(1);
      jmenuitem3.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Exit");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_X);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(1));
   }

   public void actionPerformed(ActionEvent e)
   {
      onExit();
   }
}

);
      jmenu1.add(jmenuitem3);
      jmenubar1.add(jmenu1);
      jmenu2.setText("Help");
      jmenu2.setMnemonic(java.awt.event.KeyEvent.VK_H);
      jmenu2.setDisplayedMnemonicIndex(0);
      jmenuitem4.setText("About");
      jmenuitem4.setMnemonic(java.awt.event.KeyEvent.VK_A);
      jmenuitem4.setDisplayedMnemonicIndex(0);
      jmenuitem4.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "About");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_A);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(0));
   }

   public void actionPerformed(ActionEvent e)
   {
      showAboutDlg();
   }
}

);
      jmenu2.add(jmenuitem4);
      jmenubar1.add(jmenu2);
      this.setJMenuBar(jmenubar1);
      Border border1 = BorderFactory.createLoweredBevelBorder();
      statusBar.setBorder(border1);
      statusBar.setText("Ready.");
      statusBar.setFont(statusBar.getFont().deriveFont( Font.PLAIN ));
      jpanel1.add(statusBar, BorderLayout.SOUTH);
      toolbar.setOrientation(JToolBar.HORIZONTAL);
      jpanel1.add(toolbar, "North");
      JScrollPane pathTreeScroll = new JScrollPane(pathTree,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsplitpane1.setTopComponent(pathTreeScroll);
      jsplitpane1.setBottomComponent(song);
      jpanel1.add(jsplitpane1, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.setPreferredSize(new java.awt.Dimension(600,400));
      this.setName("com.oxande.wavecleaner.ui.AbstractMainScreen");
      this.setLocationByPlatform(true);
      this.setTitle("Wave Cleaner");
      this.pack();
   }
}

