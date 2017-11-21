package com.oxande.wavecleaner.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
	private JLabel labelValue = new JLabel();
	private JLabel titleLabel = new JLabel();
	private String pattern = "0.0";
	
	ListenerManager<ValueListener> manager = new ListenerManager<>();
	
//	private Function<Float, String> formatter = (v) -> {
//		DecimalFormat formatter = new DecimalFormat(this.pattern);
//		return formatter.format(v);
//	};
	
	public static interface ValueListener {

		public String formattedValue();
		
		public void valueIncremented(JFilterMeter e);
		
		public void valueDecremented(JFilterMeter e);
		
	}
	

	public static final int BTN_SIZE = 15;

	public void addValueListener(ValueListener listener) {
		manager.add(listener);
	}

	public void removeValueListener(ValueListener listener) {
		manager.remove(listener);
	}

//	public Dimension getMinimumSize(){
//		int wBorderSize = 0;
//		int hBorderSize = 0;
//		if( this.getBorder() != null){
//			Insets insets = this.getBorder().getBorderInsets(this);
//			hBorderSize = insets.bottom + insets.top;
//			wBorderSize = insets.left + insets.right;
//		}
//		return new Dimension( Math.max( 120, MIN_SIZE * 3) + wBorderSize, MIN_SIZE + hBorderSize);
//	}
	
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

		Icon icon = WaveUtils.loadIcon(imgFile, BTN_SIZE);
		if( icon != null ){
			btn.setIcon(icon);
			Dimension dimension = new Dimension(BTN_SIZE+2, 30);
			btn.setMinimumSize(dimension);
			btn.setMaximumSize(dimension);
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
//				int scale = 1000 / (int)((maxValue - minValue) / step);
//				mouseTimer.schedule(new TimerTask() {
//					@Override
//					public void run() {
//						mouseClicked(e);
//					}
//				}, 1000, scale);
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
					self.labelValue.setText(l.formattedValue());
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
		this.filter = filter;
		Parameter p = this.filter.getParameter(control);
		this.control = p;
		setTitle(label);
		
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10 );
		titleLabel.setFont(font);
		labelValue.setHorizontalAlignment(JLabel.CENTER);
		labelValue.setOpaque(true);

		minusBtn = newButton("minus.png", -1, "<");
		plusBtn = newButton("plus.png", +1, ">");

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
		// BorderLayout layout = new BorderLayout();
		layout.setHgap(2);
		setLayout(layout);
		add(minusBtn);
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setHorizontalTextPosition(JLabel.CENTER);
		centerPanel.add(titleLabel);
		this.setMinimumSize(new Dimension(100,BTN_SIZE * 2));
		this.setPreferredSize(new Dimension(100,BTN_SIZE * 2));
		// labelValue.setPreferredSize(new Dimension(60,3));		
		labelValue.setAlignmentX(Component.CENTER_ALIGNMENT);
		font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		labelValue.setFont(font);
		centerPanel.add(labelValue);
		add(centerPanel);
		add(plusBtn);

		this.labelValue.setText( p.getFormattedValue() );
		this.validate();
//		forceValue(value);
		this.repaint();
	}

	public void setTitle(String title){
		SwingUtilities.invokeLater(() -> {
			titleLabel.setText(title);
		});
	}

//	protected void forceValue(float newValue) {
//		if (newValue < minValue) {
//			this.value = minValue;
//		} else if (newValue > maxValue) {
//			this.value = maxValue;
//		} else {
//			this.value = newValue;
//		}
//		SwingUtilities.invokeLater(() -> {
//			manager.send((listener) -> {
//				listener.stateChanged(new ChangeEvent(this));
//			});
//			String s = this.formatter.apply(this.value);
//			labelValue.setText(s);
//		});
//	}
	
//	/**
//	 * Set a dedicated formatter for this component.
//	 * 
//	 * @param formatter a functional to convert {@link Float} to {@link String}.
//	 */
//	public void setFormatter( Function<Float, String> formatter ){
//		this.formatter = formatter;
//	}
	
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

//	@Override
//	public void paintComponent(Graphics g0) {
//		// int width = getWidth();
//		// int height = getHeight();
//		// int iconSize = Math.min( width / 3, height );
//
//		Graphics2D g = (Graphics2D) g0;
//		super.paintComponent(g);
//	}

//	protected void incrementValue() {
//		setValue(this.value + this.tickSpacing);
//	}
//
//	protected void decrementValue() {
//		setValue(this.value - this.tickSpacing);
//	}

}
