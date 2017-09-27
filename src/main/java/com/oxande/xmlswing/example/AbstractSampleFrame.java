package com.oxande.xmlswing.example;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class AbstractSampleFrame extends JFrame {
   private JPanel jpanel1 = new JPanel();
   private JMenuBar jmenubar1 = new JMenuBar();
   private JMenu jmenu1 = new JMenu();
   private JMenuItem jmenuitem1 = new JMenuItem();
   private JMenuItem jmenuitem2 = new JMenuItem();
   private JMenuItem jmenuitem3 = new JMenuItem();
   private JMenu jmenu2 = new JMenu();
   public ButtonGroup group1 = new ButtonGroup();
   private JCheckBoxMenuItem jcheckboxmenuitem1 = new JCheckBoxMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem1 = new JRadioButtonMenuItem();
   ButtonGroup r = new ButtonGroup();
   private JRadioButtonMenuItem jradiobuttonmenuitem2 = new JRadioButtonMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem3 = new JRadioButtonMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem4 = new JRadioButtonMenuItem();
   protected JLabel statusBar = new JLabel();
public class WindowAdapter1 extends java.awt.event.WindowAdapter {

      public void windowClosing(WindowEvent e)
      {
         exit();
      }
}

private class SetStatusMessageClass implements Runnable {
      private String input;

      public  SetStatusMessageClass(String input)
      {
         this.input = input;
      }

      public void run()
      {
         statusBar.setText(String.valueOf(input));
      }
}


   public void setInsertMode(boolean b)
   {
      jcheckboxmenuitem1.setSelected(b);
   }

   /**
    * Called by the menu item <i>File/Open File</i>.
    */
   protected void openFile()
   {
      JOptionPane.showMessageDialog(jmenuitem1, "Not implemented.",jmenuitem1.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public String getStatusMessage()
   {
      return statusBar.getText();
   }

   public void setStatusMessage(String in)
   {
      SwingUtilities.invokeLater(new SetStatusMessageClass(in));
   }

   public void setRadio2(boolean b)
   {
      jradiobuttonmenuitem2.setSelected(b);
   }

   public boolean isRadio2()
   {
      return jradiobuttonmenuitem2.isSelected();
   }

   public void initComponents()
   {
      jpanel1.setLayout(new BorderLayout());
      jmenu1.setText("File");
      jmenu1.setMnemonic(java.awt.event.KeyEvent.VK_F);
      jmenu1.setDisplayedMnemonicIndex(0);
      jmenuitem1.setText("Open File");
      jmenuitem1.setMnemonic(java.awt.event.KeyEvent.VK_O);
      jmenuitem1.setDisplayedMnemonicIndex(0);
      jmenuitem1.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Open File");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_O);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(0));
   }

   public void actionPerformed(ActionEvent e)
   {
      openFile();
   }
}

);
      jmenu1.add(jmenuitem1);
      jmenuitem2.setText("Close File");
      jmenuitem2.setMnemonic(java.awt.event.KeyEvent.VK_E);
      jmenuitem2.setDisplayedMnemonicIndex(9);
      jmenuitem2.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Close File");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_E);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(9));
   }

   public void actionPerformed(ActionEvent e)
   {
      JOptionPane.showMessageDialog(jmenuitem2, "Not implemented.",jmenuitem2.getText(), JOptionPane.INFORMATION_MESSAGE);
   }
}

);
      jmenu1.add(jmenuitem2);
      jmenu1.addSeparator();
      jmenuitem3.setText("Exit");
      jmenuitem3.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Exit");
   }

   public void actionPerformed(ActionEvent e)
   {
      exit();
   }
}

);
      jmenu1.add(jmenuitem3);
      jmenubar1.add(jmenu1);
      jmenu2.setText("Help");
      UIManager.LookAndFeelInfo[] landf1 = UIManager.getInstalledLookAndFeels();
      for(int i = 0; i < landf1.length; i++)
      {
         JRadioButtonMenuItem item = new JRadioButtonMenuItem(landf1[i].getName());
         item.setActionCommand(landf1[i].getClassName());
         item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               try {
                  Cursor old = getCursor();
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  UIManager.setLookAndFeel(event.getActionCommand());
                  SwingUtilities.updateComponentTreeUI(getRootPane());
                  setCursor(old);
               } catch(Exception e) {
                  // Does nothing
               }
            }
         });
         jmenu2.add(item);
         group1.add(item);
         if( UIManager.getLookAndFeel().getName().equals(landf1[i].getName()) ){
            item.setSelected(true);
         }
      }
      jmenu2.addSeparator();
      jcheckboxmenuitem1.setText("Insert mode");
      jcheckboxmenuitem1.setState(true);
      jcheckboxmenuitem1.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Insert mode");
   }

   public void actionPerformed(ActionEvent e)
   {
   }
}

);
      jmenu2.add(jcheckboxmenuitem1);
      jradiobuttonmenuitem1.setText("r1");
      jradiobuttonmenuitem1.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "r1");
   }

   public void actionPerformed(ActionEvent e)
   {
   }
}

);
      r.add(jradiobuttonmenuitem1);
      jmenu2.add(jradiobuttonmenuitem1);
      jradiobuttonmenuitem2.setText("r2");
      jradiobuttonmenuitem2.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "r2");
   }

   public void actionPerformed(ActionEvent e)
   {
   }
}

);
      r.add(jradiobuttonmenuitem2);
      jmenu2.add(jradiobuttonmenuitem2);
      jradiobuttonmenuitem3.setText("r3");
      jradiobuttonmenuitem3.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "r3");
   }

   public void actionPerformed(ActionEvent e)
   {
   }
}

);
      r.add(jradiobuttonmenuitem3);
      jmenu2.add(jradiobuttonmenuitem3);
      jradiobuttonmenuitem4.setText("r4");
      jradiobuttonmenuitem4.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "r4");
   }

   public void actionPerformed(ActionEvent e)
   {
   }
}

);
      r.add(jradiobuttonmenuitem4);
      jmenu2.add(jradiobuttonmenuitem4);
      jmenubar1.add(jmenu2);
      this.setJMenuBar(jmenubar1);
      Border border1 = BorderFactory.createLoweredBevelBorder();
      statusBar.setBorder(border1);
      statusBar.setText("Ready.");
      statusBar.setFont(statusBar.getFont().deriveFont( Font.PLAIN ));
      jpanel1.add(statusBar, BorderLayout.SOUTH);
      this.addWindowListener(new WindowAdapter1());
      jpanel1.add(Box.createGlue(), BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.setPreferredSize(new java.awt.Dimension(300,150));
      this.setName("com.oxande.xmlswing.example.AbstractSampleFrame");
      this.setLocationByPlatform(true);
      this.setTitle("First frame");
      this.pack();
   }

   public boolean isInsertMode()
   {
      return jcheckboxmenuitem1.isSelected();
   }

   /**
    * Called by the menu item <i>File/Exit</i>.
    */
   protected void exit()
   {
      JOptionPane.showMessageDialog(jmenuitem3, "Not implemented.",jmenuitem3.getText(), JOptionPane.INFORMATION_MESSAGE);
   }

   public static void main(String[] args)
   {
      AbstractSampleFrame appl = new AbstractSampleFrame();
      appl.initComponents();
      appl.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      appl.setVisible(true);
   }
}

