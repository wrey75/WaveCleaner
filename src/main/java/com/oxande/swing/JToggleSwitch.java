package com.oxande.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.WaveUtils;

/**
 * Button selection ON/OFF.
 * 
 * @author wrey75
 *
 */
public class JToggleSwitch extends JLabel implements MouseListener {
	private ListenerManager<ActionListener> listenerManager = new ListenerManager<>();

	private Icon btnOn;
	private Icon btnOff;
	private boolean isOn = false;
	public boolean reactOnChange = false;
	
	/**
	 * The selected component.
	 * 
	 */
	private String[] labels = new String[] {"ON", "OFF"};
	

	public JToggleSwitch() {
		super();
		this.setOpaque(false);
		this.initComponent(true);
	}
	
	/**
	 * Set the labels (used only for ON/OFF values).
	 */
	public void setLabels(String on, String off){
		if( off == null ){
			off = on;
		}
		this.labels[0] = on;
		this.labels[1] = off;
		SwingUtilities.invokeLater(() -> {
			renderSelected(isOn);
		});
	}

	public void addActionListener(ActionListener e) {
		listenerManager.add(e);
	}

	public void removeActionListener(ActionListener e) {
		listenerManager.remove(e);
	}

	private void initComponent(boolean on) {
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
		this.setLayout(/* new GridLayout(1, buttons.size())*/ layout);
		
		btnOn = WaveUtils.loadIcon("btn-on.png", 12);
		btnOff = WaveUtils.loadIcon("btn-off.png", 12);
		
		this.setOpaque(false);
		this.setFocusable(false);
		this.addMouseListener(this);
		if( btnOn != null && btnOff != null ){
			this.setHorizontalAlignment(JLabel.LEFT);
		}
		else {
			this.setHorizontalAlignment(JLabel.CENTER);
		}
		this.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		this.setMinimumSize(new Dimension(60,24));
		this.setPreferredSize(new Dimension(80,24));
		renderSelected(on);
		// invalidate();
	}

	void renderSelected( boolean selected ){
		Assert.isEventDispatchThread();

		if( btnOn != null && btnOff != null ){
			this.setIcon(selected ? btnOn : btnOff);
			this.setForeground(selected ? Color.BLACK : Color.DARK_GRAY);
		}
		else {
			this.setBackground(selected ? Color.DARK_GRAY : Color.LIGHT_GRAY);
			this.setForeground(selected ? Color.WHITE : Color.BLACK);
		}
		this.setText(labels[selected ? 0 : 1]);
		isOn = selected;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JComponent abstractButton = (JComponent) e.getSource();
		listenerManager.send((listener) -> {
			isOn = !isOn;
			ActionEvent evt = new ActionEvent(abstractButton, 0, (isOn ? "ON" : "OFF"));
			if(reactOnChange) renderSelected(isOn);
			listener.actionPerformed(evt);
		});
	}

	@Override
	public final void mouseEntered(MouseEvent e) {
	}

	@Override
	public final void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	/**
	 * Select the button. A call to this method does not generate an event.
	 * 
	 * @param btn the button name or 
	 */
	public void setSelected(boolean selected){
		renderSelected(selected);
	}
}
