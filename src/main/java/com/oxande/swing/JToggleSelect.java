package com.oxande.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;

import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.ListenerManager;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Button selection.
 * 
 * @author wrey75
 *
 */
public class JToggleSelect extends JPanel implements MouseListener {

	private ListenerManager<ActionListener> listenerManager = new ListenerManager<>();

	/**
	 * The selected component.
	 * 
	 */
	private JComponent selected = null;
	

	public JToggleSelect() {
		this.setLayout(new GridLayout());
	}

	public void addActionListener(ActionListener e) {
		listenerManager.add(e);
	}

	public void removeActionListener(ActionListener e) {
		listenerManager.remove(e);
	}

	public void setButtons(String... buttons) {
		setButtons(Arrays.asList(buttons));
	}

	public void setButtons(List<String> buttons) {
		this.setLayout(new GridLayout(1, buttons.size()));

		removeAll();
		boolean first = true;
		for (String str : buttons) {
			JLabel btn = new JLabel(str);
			this.add(btn);
			// btn.setSelected(first);
			// btn.setActionCommand(str);
			btn.setFocusable(false);
			btn.setName(str);
			btn.setOpaque(true);
			btn.addMouseListener(this);
			btn.setHorizontalAlignment(JLabel.CENTER);
			renderSelected(btn, first);
			if(first){
				this.selected = btn;
			}
			first = false;
		}
		invalidate();
	}

	void renderSelected( JLabel component, boolean selected ){
		Assert.isEventDispatchThread();
		component.setBackground(selected ? Color.DARK_GRAY : Color.LIGHT_GRAY);
		component.setForeground(selected ? Color.WHITE : Color.BLACK);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JComponent abstractButton = (JComponent) e.getSource();
		if(abstractButton != this.selected){
			// Another button has been selected
			for (Component button : getComponents()) {
				renderSelected((JLabel)button, (button == abstractButton));
			}

			listenerManager.send((listener) -> {
				this.selected = abstractButton;
				ActionEvent evt = new ActionEvent(abstractButton, 0, "SELECTED");
				listener.actionPerformed(evt);
			});
		}
		
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
}
