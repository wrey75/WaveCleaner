package com.oxande.xmlswing.example.explorer;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;




/**
 * The main explorer class. This is a very simple explorer
 * to explore the file directories of your machine. Do not
 * expect something useful: it is just a demo of the following
 * pieces:
 * <ul>
 *   <li>JTree - for displaying the directories</li>
 *   <li>JList - for displaying the file names</li>
 *   <li>JSplit - for splitting 2 components</li>
 *   <li>JToolBar - for a dynamic toolbar</li>
 *   <li>JLabel - a label used as status bar</li> 
 * </ul>
 * 
 * <p>
 * Only about 200 lines of code written. 200 others lines
 * of code created automatically to set the GUI (a very
 * basic one).
 * </p>
 * 
 * TODO: add the dialog screen.
 * 
 * @author wrey75
 * @version $Rev$
 *
 */
public class Explorer extends AbstractExplorerFrame {
	static final int MAX_LEVEL = 4;
	private List<AbstractButton> driveButtons = new ArrayList<AbstractButton>();

	public void initGUI() {
		initComponents();
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		pathTree.addTreeSelectionListener(new PathSelectListener());
		addDrives();
		setVisible(true);
		
	}
	
	class PathSelectListener implements TreeSelectionListener {

		/**
		 * When the user changes the current directory, we
		 * update the list of the files.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			fileList.removeAll();
			TreePath treePath = e.getNewLeadSelectionPath();
			if( treePath != null ){
				FileTreeExpander o = (FileTreeExpander)treePath.getLastPathComponent();
				if( o != null ){
					setStatusMessage(o.getDirectory().getAbsolutePath());
					File[] files = o.getFiles();
					DefaultListModel model = new DefaultListModel();
					for( File f : files ){
						if( f.isFile() ){
							model.addElement(f.getName());
						}
					}
					fileList.setModel(model);
				}
			}
		}
		
	}

	/**
	 * This class is used to dynamically drop-down the
	 * directories.
	 * 
	 *
	 */
	public class FileTreeExpander extends DefaultMutableTreeNode {
		boolean parsed = false;
		File file;
		File[] children = null;

		public FileTreeExpander(File f) {
			this.file = f;
		}
		
		/**
		 * Return the directory.
		 * @return the directory.
		 */
		public File getDirectory(){
			return file;
		}

		public File[] getFiles(){
			if( children == null ) defineChildNodes();
			return children;
		}

		/**
		 * It is a leaf if the file is NOT a directory.
		 * 
		 * @return <code>false</code> if it is a regular file or, more
		 *         generally, something else than a directory.
		 */
		public boolean isLeaf() {
			return !file.isDirectory();
		}

		public int getChildCount() {
			if (children == null) defineChildNodes();
			return (super.getChildCount());
		}

		/**
		 * Defines the nodes. If there is an issue,
		 * a empty array is created.
		 */
		private void defineChildNodes() {
			parsed = true; // To avoid the reload.
			children = file.listFiles();
			if( children != null ){
				for (File f : children) {
					if (!f.getName().startsWith(".")) {
						// Ignore hidden and special files.
						if (f.isDirectory()) {
							add(new FileTreeExpander(f));
						}
					}
				}
			}
			else {
				// Ignore the bugs: this is a sample.
				children = new File[0];
			}
		}

		/**
		 * Return the text to display.
		 */
		public String toString() {
			return file.getName();
		}

	}
	
	/**
	 * Add the drives to the toolbar (note for
	 * Unix, there is no different root files).
	 */
	protected void addDrives(){
		File[] roots = File.listRoots();
		for( File f : roots ){
			String rootName = f.getPath();
			JToggleButton btn = new JToggleButton( rootName );
			btn.setActionCommand(rootName);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPath(e.getActionCommand());
					// Unselect the other buttons...
					for( AbstractButton btn : driveButtons ){
						btn.setSelected(btn == e.getSource());
					}
				}
			});
			toolbar.add(f.getName(), btn );
			driveButtons.add(btn);
		}
		setPath(roots[0].getPath());
	}
	
	
	/**
	 * Set the root path for the tree.
	 * 
	 * @param path the path.
	 */
	public void setPath( String path ){
		File f = new File(path);
		Cursor old = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		pathTree.setModel(new DefaultTreeModel(new FileTreeExpander(f)));
		setCursor(old);
	}
	
	/**
	 * Create the explorer.
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		// Set the Look and feel to the value of the system.
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (Exception e) {
			// Exception has to be ignored
		}
		
		Explorer application = new Explorer();
		application.initGUI();
	}
	
	/**
	 * Exit of the program. Called by the menu.
	 * 
	 */
	public void exit(){
		System.exit(0);
	}
}
