package com.oxande.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.oxande.wavecleaner.util.ListenerManager;

public class JToggleSelect extends JPanel implements ChangeListener {

	private ListenerManager<SelectedListener> listenerManager = new ListenerManager<>();
	
	public static interface SelectedListener {
		public void selectionChanged(AbstractButton btn);
	}
	
	public JToggleSelect() {
		this.setLayout(new GridLayout());
	}
	
	public void addChangeListener( SelectedListener e ){
		listenerManager.add(e);
	}
	
	public void removeChangeListener(SelectedListener e){
		listenerManager.remove(e);
	}
	
	public void setButtons(List<String> buttons){
		this.setLayout(new GridLayout(1, buttons.size()));
		
		removeAll();
		boolean first = true;
		for(String str : buttons){
			JToggleButton btn = new JToggleButton(str);
			this.add(btn);
			btn.addChangeListener( this );
			btn.setSelected(first);
			btn.setActionCommand(str);
			first = false;
		}
		invalidate();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		listenerManager.publish((listener) -> {
			AbstractButton btn = (AbstractButton)e.getSource();
			listener.selectionChanged(btn);
			for(Component button : getComponents()){
				if( button instanceof JToggleButton ){
					((JToggleButton) button).setSelected(button == e.getSource());
				}
			}
		});
		
	}
	
}
