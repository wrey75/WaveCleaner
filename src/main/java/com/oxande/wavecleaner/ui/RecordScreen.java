package com.oxande.wavecleaner.ui;

import javax.swing.JFrame;

public class RecordScreen extends AbstractRecordScreen {
	
	/**
	 * Make this recording screen visible
	 */
	public void initComponents(){
		super.initComponents();
		setVisible(true);
		setModal(true);
	}

	protected void startRecord(){
		
	}
}
