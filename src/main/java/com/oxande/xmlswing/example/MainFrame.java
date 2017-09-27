package com.oxande.xmlswing.example;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Class created automatically -- DO NOT UPDATE MANUALLY.
 * This class has been created based on a XML file and must
 * be extended by your own code. The following code only
 * provide an easy way to obtain a basic GUI.
 */
public class MainFrame extends JFrame {
   private JPanel jpanel1 = new JPanel();
   private JMenuBar jmenubar1 = new JMenuBar();
   private JMenu jmenu1 = new JMenu();
   private JMenuItem jmenuitem1 = new JMenuItem();
   private JMenuItem jmenuitem2 = new JMenuItem();
   private JMenuItem jmenuitem3 = new JMenuItem();
   private JMenu jmenu2 = new JMenu();
   private JCheckBoxMenuItem jcheckboxmenuitem1 = new JCheckBoxMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem1 = new JRadioButtonMenuItem();
   ButtonGroup r = new ButtonGroup();
   private JRadioButtonMenuItem jradiobuttonmenuitem2 = new JRadioButtonMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem3 = new JRadioButtonMenuItem();
   private JRadioButtonMenuItem jradiobuttonmenuitem4 = new JRadioButtonMenuItem();
   public ButtonGroup group1 = new ButtonGroup();
   private JMenu jmenu3 = new JMenu();
   private JMenu jmenu4 = new JMenu();
   private JMenuItem jmenuitem4 = new JMenuItem();
   private JMenuItem jmenuitem5 = new JMenuItem();
   private JTabbedPane jtabbedpane1 = new JTabbedPane();
   private JPanel jpanel2 = new JPanel();
   private JPanel jpanel3 = new JPanel();
   private JLabel jlabel1 = new JLabel();
   private JTextField jtextfield1 = new JTextField();
   private JLabel jlabel2 = new JLabel();
   private JLabel jlabel3 = new JLabel();
   private JLabel jlabel4 = new JLabel();
   private JTextField jtextfield2 = new JTextField();
   private JLabel jlabel5 = new JLabel();
   private JPasswordField jpasswordfield1 = new JPasswordField();
   private JPanel jpanel4 = new JPanel();
   private JButton jbutton1 = new JButton();
   private JPanel jpanel5 = new JPanel();
   private JSplitPane jsplitpane1 = new JSplitPane();
   private JTextArea jtextarea1 = new JTextArea();
   private JList jlist1 = new JList();
   private JPanel jpanel6 = new JPanel();
   private JComboBox jcombobox1 = new JComboBox();
private class SetAgeClass implements Runnable {
      private int input;

      public  SetAgeClass(int input)
      {
         this.input = input;
      }

      public void run()
      {
         jtextfield2.setText(String.valueOf(input));
      }
}

public class WindowAdapter1 extends java.awt.event.WindowAdapter {

      public void windowStateChanged(WindowEvent e)
      {
         statechanged(e);
      }

      public void windowOpened(WindowEvent e)
      {
         System.out.println("WindowEvent = " + e);
      }

      public void windowGainedFocus(WindowEvent e)
      {
         focusActivated();
      }
}

public class SimpleMapEntry implements java.util.Map.Entry<String,String> {
      private String key;
      private String value;

      public String getValue()
      {
         return value;
      }

      public String getKey()
      {
         return key;
      }

      public String toString()
      {
         return this.value;
      }

      public String setValue(String value)
      {
         String old = this.value;
         this.value = value.toString();
         return old;
      }

      public  SimpleMapEntry(java.lang.String key, java.lang.String value)
      {
         this.key = key;
         this.value = value;
      }
}


   protected void focusActivated()
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   protected void statechanged(WindowEvent e)
   {
      throw new UnsupportedOperationException("Not implemented");
   }

   public void setInsertMode(boolean b)
   {
      jcheckboxmenuitem1.setSelected(b);
   }

   public int getAge()
   {
      return Integer.parseInt(jtextfield2.getText());
   }

   public void setAge(int in)
   {
      SwingUtilities.invokeLater(new SetAgeClass(in));
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
      this.setIconImage((new ImageIcon(getClass().getResource("./forward16.gif"))).getImage());
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
      JOptionPane.showMessageDialog(jmenuitem1, "Not implemented.",jmenuitem1.getText(), JOptionPane.INFORMATION_MESSAGE);
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
      jmenuitem3.setText("Quit");
      jmenuitem3.setMnemonic(java.awt.event.KeyEvent.VK_Q);
      jmenuitem3.setDisplayedMnemonicIndex(0);
      jmenuitem3.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Quit");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_Q);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(0));
   }

   public void actionPerformed(ActionEvent e)
   {
      // Quit the application System.exit(0);
   }
}

);
      jmenu1.add(jmenuitem3);
      jmenubar1.add(jmenu1);
      jmenu2.setText("Edit");
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
      jmenu2.addSeparator();
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
      jmenubar1.add(jmenu2);
      jmenu3.setText("Help");
      jmenu4.setText("Special operations");
      jmenuitem4.setName("deleteProgram");
      jmenuitem4.setText("Delete the program");
      jmenuitem4.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "Delete the program");
   }

   public void actionPerformed(ActionEvent e)
   {
      JOptionPane.showMessageDialog(jmenuitem4, "Not implemented.",jmenuitem4.getText(), JOptionPane.INFORMATION_MESSAGE);
   }
}

);
      jmenu4.add(jmenuitem4);
      jmenu3.add(jmenu4);
      jmenuitem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke("ctrl A"));
      jmenuitem5.setText("A propos de...");
      jmenuitem5.setMnemonic(java.awt.event.KeyEvent.VK_A);
      jmenuitem5.setDisplayedMnemonicIndex(0);
      jmenuitem5.setAction(new AbstractAction()  {
   {
      putValue(Action.NAME, "A propos de...");
      putValue(Action.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_A);
      putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(0));
      putValue(Action.ACCELERATOR_KEY, jmenuitem5.getAccelerator());
   }

   public void actionPerformed(ActionEvent e)
   {
      MainDialog dlg = new MainDialog(); dlg.initComponents(); dlg.setVisible(true);
   }
}

);
      jmenu3.add(jmenuitem5);
      jmenubar1.add(jmenu3);
      this.setJMenuBar(jmenubar1);
      this.addWindowListener(new WindowAdapter1());
      GridBagLayout layout1 = new GridBagLayout();
      GridBagConstraints c1 = new GridBagConstraints();
      jpanel3.setLayout(layout1);
      javax.swing.border.TitledBorder border1 = new javax.swing.border.TitledBorder("Input block");
      jpanel3.setBorder(border1);
      
      jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jlabel1.setText("Nombre d\'adh\u00e9rents:");
      c1.gridy = 0;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel1, c1);
      jpanel3.add(jlabel1);
      
      jtextfield1.setColumns(15);
      c1.gridy = 0;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jtextfield1, c1);
      jpanel3.add(jtextfield1);
      
      jlabel2.setText("A very long label to test the size");
      c1.gridy = 1;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel2, c1);
      jpanel3.add(jlabel2);
      
      c1.gridy = 1;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel3, c1);
      jpanel3.add(jlabel3);
      
      jlabel4.setText("Age:");
      c1.gridy = 2;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel4, c1);
      jpanel3.add(jlabel4);
      
      jtextfield2.setColumns(15);
      c1.gridy = 2;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jtextfield2, c1);
      jpanel3.add(jtextfield2);
      
      jlabel5.setText("Password:");
      c1.gridy = 3;
      c1.gridx = 0;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jlabel5, c1);
      jpanel3.add(jlabel5);
      
      jpasswordfield1.setColumns(15);
      c1.gridy = 3;
      c1.gridx = 1;
      c1.gridheight = 1;
      c1.gridwidth = 1;
      c1.anchor = GridBagConstraints.WEST;
      c1.fill = GridBagConstraints.NONE;
      c1.weightx = 1;
      layout1.setConstraints(jpasswordfield1, c1);
      jpanel3.add(jpasswordfield1);
      jpanel2.add(jpanel3);
      GridBagLayout layout2 = new GridBagLayout();
      GridBagConstraints c2 = new GridBagConstraints();
      jpanel4.setLayout(layout2);
      
      jbutton1.setText("OK");
      jbutton1.setMnemonic(java.awt.event.KeyEvent.VK_O);
      jbutton1.setDisplayedMnemonicIndex(0);
      jbutton1.addActionListener(new ActionListener()  {

   public void actionPerformed(ActionEvent e)
   {
      JOptionPane.showMessageDialog(jbutton1, "Not implemented.",jbutton1.getText(), JOptionPane.INFORMATION_MESSAGE);
   }
}

);
      c2.gridy = 0;
      c2.gridx = 0;
      c2.gridheight = 1;
      c2.gridwidth = 1;
      c2.anchor = GridBagConstraints.WEST;
      c2.fill = GridBagConstraints.NONE;
      c2.weightx = 1;
      layout2.setConstraints(jbutton1, c2);
      jpanel4.add(jbutton1);
      jpanel2.add(jpanel4);
      jtabbedpane1.addTab("tab n\u00b01", null, jpanel2, null);
      jsplitpane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jtextarea1.setText("Partie haute");
      jtextarea1.setColumns(20);
      jtextarea1.setRows(20);
      JScrollPane scrollPane1 = new JScrollPane(jtextarea1,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsplitpane1.setTopComponent(scrollPane1);
      JScrollPane scrollPane2 = new JScrollPane(jlist1,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsplitpane1.setBottomComponent(scrollPane2);
      jpanel5.add(jsplitpane1);
      jtabbedpane1.addTab("tab n\u00b02", null, jpanel5, null);
      jcombobox1.addItem(new SimpleMapEntry( "1", "item one"));
      jcombobox1.addItem(new SimpleMapEntry( "2", "item two"));
      jcombobox1.addItem(new SimpleMapEntry( "3", "item three"));
      jcombobox1.setSelectedIndex(2);
      jcombobox1.addItem(new SimpleMapEntry( "4", "item four"));
      jpanel6.add(jcombobox1);
      jtabbedpane1.addTab("ComboBox", null, jpanel6, null);
      jpanel1.add(jtabbedpane1, BorderLayout.CENTER);
      this.setContentPane(jpanel1);
      this.setName("com.oxande.xmlswing.example.MainFrame");
      this.setLocationByPlatform(true);
      this.setTitle("Test");
      this.pack();
   }

   public boolean isInsertMode()
   {
      return jcheckboxmenuitem1.isSelected();
   }

   public static void main(String[] args)
   {
      MainFrame appl = new MainFrame();
      appl.initComponents();
      appl.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      appl.setVisible(true);
   }
}

