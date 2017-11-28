package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.filters.AudioFilter;
import com.oxande.wavecleaner.filters.AudioFilter.Parameter;
import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.WaveUtils;
import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * A replacement for the {@link JSlider} which is not perfect for what we want.
 * Basically, we need a precise value using steps like a incrementing value but
 * it is not perfect in terms of user interface. I opted for a "-"/"+" interface.
 * Buttons are replaced by {@link JLabel} for a better control. The user has to use
 * the mouse to change the values. Using a shortcut could be possible in the future
 * versions.
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class JFilterMeter extends JPanel {
	private static Logger LOG = LogFactory.getLog(JFilterMeter.class);

	private AudioFilter filter;
	private Parameter control;
	private float value;

	private JLabel minusBtn;
	private JLabel plusBtn;
	private JLabel valueLabel = new JLabel();
	private JLabel titleLabel = new JLabel();
	
	ListenerManager<ValueListener> manager = new ListenerManager<>();
	
	public static interface ValueListener {

//		public String formattedValue();
		
		public void valueIncremented(JFilterMeter e);
		
		public void valueDecremented(JFilterMeter e);
		
	}
	

	public static final int BTN_SIZE = 20;

	public void addValueListener(ValueListener listener) {
		manager.add(listener);
	}

	public void removeValueListener(ValueListener listener) {
		manager.remove(listener);
	}

	/**
	 * Return the control parameter linked to this object.
	 * @return
	 */
	public Parameter getControl(){
		return this.control;
	}
	
	/**
	 * Create a button for "+" and "-".
	 * 
	 * @param imgFile the image file stored as PNG (for transparency!).
	 * @param label the label (if image load loaded).
	 * @param function the callback.
	 * @return the new {@link JButton} created.
	 * 
	 */
	public JLabel newButton(String imgFile, int dir, String label) {
		JLabel btn = new JLabel();
		btn.setOpaque(false);
		btn.setAlignmentY(Component.CENTER_ALIGNMENT);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		Icon icon = WaveUtils.loadIcon(imgFile, BTN_SIZE);
		if( icon != null ){
			btn.setIcon(icon);
			Dimension dimension = new Dimension(BTN_SIZE+2, 30);
//			btn.setMinimumSize(dimension);
//			btn.setMaximumSize(dimension);
		} else {
			LOG.error("Can not load {}", imgFile);
			btn.setText(label); // use label instead
		}
		JFilterMeter self = this;

		btn.addMouseListener(new MouseListener() {
			Timer mouseTimer;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if( mouseTimer != null ){
					this.mouseTimer.cancel();
					this.mouseTimer = null;
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if( this.mouseTimer != null ){
					LOG.error("We expected a null mouseTimer!");
					this.mouseTimer.cancel();
				}
				this.mouseTimer = new Timer();
				Parameter p = self.control;
				int scale = (int)( 10000.0 * p.getTick() / (p.getMaximum() - p.getMinimum()) );
				mouseTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						mouseClicked(e);
					}
				}, 1000, scale);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				manager.publish(l -> {
					String v;
					if( dir < 0 ){
						l.valueDecremented(self);
					} else {
						l.valueIncremented(self);
					}
				});
			}
			
		});
		return btn;
	}

	/**
	 * Get the current value. Thread-safe.
	 * 
	 * @return the current value
	 */
	public float getValue() {
		return this.control.getValue();
	}
	

	/**
	 * Create a {@link JFilterMeter} with all the value.
	 * 
	 * @param min
	 *            the minimum value
	 * @param max
	 *            the maximum value
	 * @param value
	 *            the step used
	 */
	public JFilterMeter(AudioFilter filter, String control, String label) {
		super();
		this.setOpaque(false);
		
		this.filter = filter;
		Parameter p = this.filter.getParameter(control);
		this.control = p;
		setTitle(label);
		
		// this.setBorder( BorderFactory.createRaisedBevelBorder());
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10 );
		titleLabel.setFont(font);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		minusBtn = newButton("less-than.png", -1, "<");
		plusBtn = newButton("greater-than.png", +1, ">");

		BorderLayout layout = new BorderLayout();
		layout.setHgap(2);
		setLayout(layout);
//		setMaximumSize(new Dimension(100 + BTN_SIZE * 2,BTN_SIZE * 3)); // sure?
//		setPreferredSize(new Dimension(90 + BTN_SIZE * 2,BTN_SIZE * 2)); // sure?
//		setMinimumSize(new Dimension(60 + BTN_SIZE * 2,BTN_SIZE * 2)); // sure?
		add(minusBtn, BorderLayout.WEST);
		
		font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		valueLabel.setPreferredSize(new Dimension(80,BTN_SIZE));		
		valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		valueLabel.setHorizontalAlignment(JLabel.CENTER);
		valueLabel.setFont(font);
		add(valueLabel, BorderLayout.CENTER);
		add(titleLabel, BorderLayout.SOUTH);
		add(plusBtn, BorderLayout.EAST);

		this.valueLabel.setText( p.getFormattedValue() );
		this.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		this.validate();
		this.repaint();
	}

	/**
	 * Change the value of the label (usually in reaction to a click on the button).
	 * 
	 * @param text the new text to display.
	 */
	public void setValue(String text){
		SwingUtilities.invokeLater(() -> {
			valueLabel.setText(text);
		});
	}

	public void setTitle(String title){
		SwingUtilities.invokeLater(() -> {
			titleLabel.setText(title);
		});
	}

	
	/**
	 * Set the new value. If the new value is near of the current one (less than one step),
	 * nothing changed for performance reasons.
	 * 
	 * @param newValue the new value
	 */
	public void setValue(float newValue) {
		if (newValue != this.value) {
			this.filter.setControl(this.control.getName(), newValue);
		}
	}

}
