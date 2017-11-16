package com.oxande.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * A replacement for the {@link JSlider} wich is not perfect for what we want.
 * 
 * @author wrey
 *
 */
@SuppressWarnings("serial")
public class JMeter extends JPanel {
	private static Logger LOG = LogFactory.getLog(JMeter.class);

	private float maxValue;
	private float minValue;
	private float value;
	private float step;
	private JButton minusBtn;
	private JButton plusBtn;
	private JLabel labelValue = new JLabel();
	private String pattern = "0.0";
	ListenerManager<ChangeListener> manager = new ListenerManager<>();
	private Function<Float, String> formatter = (v) -> {
		DecimalFormat formatter = new DecimalFormat(this.pattern);
		return formatter.format(v);
	};
	
	public static final int MIN_SIZE = 32;

	public void addChangeListener(ChangeListener listener) {
		manager.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
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
	public JButton newButton(String imgFile, String label, Consumer<ActionEvent> function) {
		JButton btn = new JButton();
		btn.setOpaque(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
//		btn.setMinimumSize(new Dimension(28,28));
		URL url = getClass().getResource("/images/" + imgFile);
		try {
			BufferedImage img = ImageIO.read(url);
			Image tmp = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(tmp);
			btn.setIcon(icon);
		} catch (IOException ex) {
			LOG.error("Can not load {}", imgFile);
			btn.setText(label); // use label instead
		}
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				function.accept(e);
			}
		});
		btn.setActionCommand(label);
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
				mouseTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						btn.doClick();
					}
				}, 1000, 20);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		return btn;
	}

	public void setMinimumValue(float min) {
		this.minValue = min;
	}

	public void setMaximumValue(float max) {
		this.maxValue = max;
	}

	/**
	 * Get the current value. Thread-safe.
	 * 
	 * @return the current value
	 */
	public float getValue() {
		return this.value;
	}
	
	public void setStepValue(float v) {
		if (v < 0.1) {
			this.pattern = "0.00";
		} else if (v < 1) {
			this.pattern = "0.0";
		} else {
			this.pattern = "0";
		}
		this.step = v;
		setValue(this.value);
	}


	/**
	 * Create a {@link JMeter} with all the value.
	 * 
	 * @param min
	 *            the minimum value
	 * @param max
	 *            the maximum value
	 * @param value
	 *            the tep used
	 */
	public JMeter(float min, float max, float value, float step) {
		super();
		labelValue.setHorizontalAlignment(JLabel.CENTER);
//		labelValue.setMinimumSize(new Dimension(40,28));
//		setMinimumSize(new Dimension(50, 100));
//		setPreferredSize(new Dimension(75, 200));
//		setMaximumSize(new Dimension(100, 500));
		// BorderLayout borderLayout = new BorderLayout();
		minusBtn = newButton("minus.png", "<", (e) -> {
			decrementValue();
		});
		plusBtn = newButton("plus.png", ">", (e) -> {
			incrementValue();
		});

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
		// layout.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		layout.setHgap(2);
		setLayout(layout);
		labelValue.setMinimumSize(new Dimension(60,30));
		labelValue.setPreferredSize(new Dimension(60,30));		
//		add(minusBtn, BorderLayout.WEST);
//		add(plusBtn, BorderLayout.EAST);
//		add(labelValue, BorderLayout.CENTER);

		add(minusBtn);
		add(labelValue);
		add(plusBtn);

		this.step = step;
		setMinimumValue(min);
		setMaximumValue(max);
		
		this.validate();
		forceValue(value);
		this.repaint();
	}

	public JMeter(float min, float max, float value) {
		this(min, max, value, (max - min) / 10.0f);
	}

	public JMeter() {
		this(0, 100, 0, 10);
	}
	
	public void setTitle(String title){
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder(title);
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10 );
		titleBorder.setTitleFont(font);
		this.setBorder(titleBorder);
	}

	protected void forceValue(float newValue) {
		if (newValue < minValue) {
			this.value = minValue;
		} else if (newValue > maxValue) {
			this.value = maxValue;
		} else {
			this.value = newValue;
		}
		SwingUtilities.invokeLater(() -> {
			manager.send((listener) -> {
				listener.stateChanged(new ChangeEvent(this));
			});
			String s = this.formatter.apply(this.value);
			labelValue.setText(s);
		});
	}
	
	/**
	 * Set a dedicated formatter for this component.
	 * 
	 * @param formatter a functional to convert {@link Float} to {@link String}.
	 */
	public void setFormatter( Function<Float, String> formatter ){
		this.formatter = formatter;
	}
	
	/**
	 * Set the new value. If the new value is near of the current one (less than one step),
	 * nothing changed for performance reasons.
	 * 
	 * @param newValue the new value
	 */
	public void setValue(float newValue) {
		if (Math.abs(newValue - this.value) > this.step / 2.0f) {
			this.forceValue(newValue);
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

	protected void incrementValue() {
		setValue(this.value + this.step);
	}

	protected void decrementValue() {
		setValue(this.value - this.step);
	}

}
