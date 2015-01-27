package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import controller.Controller;

import model.InputParameter;
import effects.Effect;

/**
 * This class is the main view of the application.
 * 
 * <p>
 * It is an instance of {@code JFrame} that shows and manages the whole GUI. In
 * addition this class provides some method for getting/setting useful
 * information from/to the view.
 * 
 * <p>
 * It also manage the main menu's listeners that needs to work with the GUI.
 * 
 * @author Francesco Del Duchetto
 */
public class View extends JFrame {
	private static final long serialVersionUID = 1;

	/**
	 * Button that start and stop the streaming of the sound.
	 */
	private JButton startStopButton = new JButton("Play");

	/**
	 * Shows the {@code GraphView}.
	 */
	private JButton graphBtn = new JButton("Graph");

	/**
	 * Button that allow user to open a new file.
	 */
//	private JButton openButton = new JButton("Open");

	/**
	 * Button that allow the user to add effects.
	 */
//	private JButton addButton = new JButton("Add effect");

	/**
	 * File chooser for select a sound file.
	 */
//	private JFileChooser fileChooser = new JFileChooser();

	/**
	 * TextField that show the path of the selected file.
	 */
//	private JTextField fileNameTxt = new JTextField();

	/**
	 * Slider to vary the input attenuation.
	 */
	private JSlider inputAttenuationSld = new JSlider();

	/**
	 * Label for the attenuation panel.
	 */
	private JLabel inputAttenuationLbl = new JLabel("Gain", JLabel.CENTER);

	/**
	 * Contains all the effects that the user can add.
	 */
	private JPanel effectsPanel = new JPanel();

	/**
	 * Contains the observer registered.
	 */
	private ViewObserver observer;

	/**
	 * The main controller.
	 */
	private Controller controller;

	/**
	 * Construct the main view of the application.
	 * 
	 * @param controller
	 *            the controller of the aplication.
	 */
	public View(final Controller controller) {
		super("Guitar DSP");

		this.controller = controller;

		/* Chief panel on which all the other panels are added */
		JPanel rootPanel = new JPanel();
		/* Panel that contains the controls to select and start a sound file */
		JPanel menuPanel = new JPanel();
		/* Contains the components to open and select a file */
		JPanel openPanel = new JPanel();
		/* Contains the components to start the stream */
		JPanel startPanel = new JPanel();
		/*
		 * Panel which contains the contents of the application, that are the
		 * attenuation controls, the controls to add an effects and the effects
		 * themselves
		 */
		JPanel contentsPanel = new JPanel();
		/* Wraps effectsPanel and adds it the vertical JScrollBar */
		JScrollPane effectsScrollPane = new JScrollPane();
		/* Panel that contains the slider for the attenuation */
		JPanel attenuationPanel = new JPanel();
		/* Panel that allows user to add an effect */
		JPanel addPanel = new JPanel();

		/* Set-up input attenuation */
		InputParameter<Double> inputAttenuation = controller
				.getInputAttenuation();
		inputAttenuationSld.setMinimum(inputAttenuation.getIntMinValue());
		inputAttenuationSld.setMaximum(inputAttenuation.getIntMaxValue());
		inputAttenuationSld.setValue(inputAttenuation.getIntValue());
		Dictionary<Integer, JLabel> labels = new Hashtable<>();
		labels.put(0, new JLabel("0"));
		labels.put(50, new JLabel("0.5"));
		labels.put(100, new JLabel("1"));
		inputAttenuationSld.setLabelTable(labels);
		inputAttenuationSld.setPaintLabels(true);
		inputAttenuationSld.setPaintTicks(true);
		inputAttenuationSld.setMinorTickSpacing(10);
		inputAttenuationLbl.setAlignmentX(CENTER_ALIGNMENT);

		/* Set the filter for .wav files */
//		fileChooser.setFileFilter(new WavFileFilter());

		/* Set-up open panel */
//		openPanel.setLayout(new BoxLayout(openPanel, BoxLayout.X_AXIS));
//		openPanel.setBorder(new EmptyBorder(13, 30, 13, 30));
//		fileNameTxt.setColumns(20);
//		openPanel.add(fileNameTxt);
//		openPanel.add(Box.createRigidArea(new Dimension(20, 0)));
//		openPanel.add(openButton);

		/* Set-up start panel */
		startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
		startPanel.setBorder(new EmptyBorder(0, 30, 0, 30));
		startStopButton.setMaximumSize(new Dimension(150, 25));
		startStopButton.setMinimumSize(new Dimension(150, 25));
		startStopButton.setPreferredSize(new Dimension(150, 25));
		startPanel.add(startStopButton);
		graphBtn.setMaximumSize(new Dimension(150, 20));
		graphBtn.setMinimumSize(new Dimension(150, 20));
		graphBtn.setPreferredSize(new Dimension(150, 20));
		startPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		startPanel.add(graphBtn);

		/* Set-up menu panel */
		menuPanel.setMaximumSize(new Dimension(700, 55));
		menuPanel.setMinimumSize(new Dimension(700, 55));
		menuPanel.setPreferredSize(new Dimension(700, 55));
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));
		menuPanel.add(openPanel);
		menuPanel.add(startPanel);

		/* Set-up attenuation panel */
		attenuationPanel.setLayout(new BoxLayout(attenuationPanel,
				BoxLayout.Y_AXIS));
		attenuationPanel.add(inputAttenuationLbl);
		attenuationPanel.add(inputAttenuationSld);

		/* Set-up effects panel */
//		effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
//		effectsScrollPane.setViewportView(effectsPanel);
//		effectsScrollPane
//				.setHorizontalScrollBarPolicy(
//						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		effectsScrollPane
//				.setVerticalScrollBarPolicy(
//						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		effectsScrollPane.setMaximumSize(new Dimension(680, Short.MAX_VALUE));
//		effectsScrollPane.setMinimumSize(new Dimension(680, 530));
//		effectsScrollPane.setPreferredSize(new Dimension(680, 530));
//		effectsScrollPane.setWheelScrollingEnabled(true);

		/* Set-up add panel */
//		addButton.setMaximumSize(new Dimension(680, 30));
//		addButton.setMinimumSize(new Dimension(680, 30));
//		addButton.setPreferredSize(new Dimension(680, 30));
//		addPanel.add(addButton);

		/* Set-up content panel */
		contentsPanel.setLayout(new BoxLayout(contentsPanel, BoxLayout.Y_AXIS));
		contentsPanel.add(attenuationPanel);
//		contentsPanel.add(addPanel);
		contentsPanel.add(effectsPanel);

		/* Set-up root panel */
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.add(menuPanel);
		rootPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		rootPanel.add(contentsPanel);
		rootPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		this.setContentPane(rootPanel);
		this.setSize(700, 740);
		this.setResizable(false);
	}

	/**
	 * Register to this class the {@link Observer} of this class.
	 * 
	 * @param obs
	 * 	
	 */
	public final void registerObserver(final ViewObserver obs) {
		this.observer = obs;
		/* Add the listeners */
		this.addWindowListener(observer.getWindowListener());
//		openButton.addActionListener(observer.getOpenButtonListener());
//		addButton.addActionListener(observer.getAddButtonListener(addButton));
		startStopButton
				.addActionListener(observer.getStartStopButtonListener());
		inputAttenuationSld.addChangeListener(observer
				.getInputParameterSldListener(controller.getInputAttenuation(),
						inputAttenuationSld, null));
		graphBtn.addActionListener(observer.getGraphBtnListener());
	}

	/**
	 * @return the {@link JFileChooser} for select .wav file.
	 */
//	public final JFileChooser getFileChooser() {
//		return this.fileChooser;
//	}

	/**
	 * @return the {@link JTextField} which shows the name of the selected file.
	 */
//	public final JTextField getFileNameText() {
//		return fileNameTxt;
//	}

	/**
	 * @param component
	 *            the {@link Component} of which we search the index.
	 * @return the index of the {@code Component} given.
	 */
	public final int getComponentIndex(final Component component) {
		if (component != null && component.getParent() != null) {
			Container c = component.getParent();
			for (int i = 0; i < c.getComponentCount(); i++) {
				if (c.getComponent(i) == component) {
					return i;
				}
			}
		}
		return -1;
	}

	public final void showEffect(final Class<? extends Effect> effectType) {
		JPanel effectPanel = new ExposedEffectPanel(observer, effectType);
		effectsPanel.add(effectPanel);

		if (getComponentIndex(effectPanel) % 2 == 0) {
			setComponentColor(effectPanel, new Color(224, 224, 224));
		}
		effectsPanel.repaint();
		effectsPanel.revalidate();
	}
	/**
	 * Create and shows a new panel that allows user to controls the effect's
	 * parameters.
	 * 
	 * @param effect
	 *            the type of {@link Effect} that must be showed.
	 */
//	public final void createEffectPanel(final Effect effect) {
//		JPanel effectPanel = new EffectPanel(observer, effect);
//		effectsPanel.add(effectPanel);
//
//		if (getComponentIndex(effectPanel) % 2 == 0) {
//			setComponentColor(effectPanel, new Color(224, 224, 224));
//		}
//		effectsPanel.repaint();
//		effectsPanel.revalidate();
//	}

	/**
	 * Set the background color of a component.
	 * 
	 * @param comp
	 *            the {@link Component} of which we want to change the color.
	 * @param color
	 *            the {@link Color}.
	 */
	public final void setComponentColor(final Component comp, 
			final Color color) {
		if (comp instanceof Container) {
			for (Component c : ((Container) comp).getComponents()) {
				if (c instanceof Container) {
					setComponentColor(c, color);
				}
			}
			if (!(comp instanceof JButton)) {
				comp.setBackground(color);
			}
		}
	}

	/**
	 * It allows to change the start/stop button.
	 * 
	 * @param text
	 *            that must be showed on the button.
	 */
	public final void setStartStopButtonText(final String text) {
		startStopButton.setText(text);
	}

	/**
	 * The {@link FileFilter} that allows to select .wav files from the
	 * {@link JFileChooser}.
	 */
//	private class WavFileFilter extends FileFilter {
//		@Override
//		public String getDescription() {
//			return ".wav";
//		}
//
//		@Override
//		public boolean accept(final File arg0) {
//			// Accept directory to navigate into file-system
//			if (arg0.isDirectory()) {
//				return true;
//			}
//			// Accept file with .wav extension
//			String extension = getExtension(arg0);
//			if (extension != null && extension.equals("wav")) {
//				return true;
//			}
//			return false;
//		}
//
//		/**
//		 * Get the extension of a file.
//		 * 
//		 * @param f 
//		 * 		the file.
//		 * @return the extension of the specified file.
//		 */
//		private String getExtension(final File f) {
//			String ext = null;
//			String s = f.getName();
//			int i = s.lastIndexOf('.');
//			if (i > 0 && i < s.length() - 1) {
//				ext = s.substring(i + 1).toLowerCase();
//			}
//			return ext;
//		}
//	}

	/**
	 * Define the methods that a {@link Controller} for this {@code View} must
	 * implement.
	 * 
	 * @author Francesco Del Duchetto
	 * 
	 */
	public interface ViewObserver {

		/**
		 * @return the {@link WindowListener} for the view.
		 */
		WindowListener getWindowListener();

		/**
		 * @return the {@link ActionListener} that shows the
		 *         {@link JFileChooser} to choose the file.
		 */
//		ActionListener getOpenButtonListener();

		/**
		 * @param addButton
		 *            the button on which this {@link ActionListener} must be
		 *            added.
		 * @return the {@code ActionListener} that shows the {@link JPopupMenu}
		 *         that allows user to select the effect to add.
		 */
//		ActionListener getAddButtonListener(JButton addButton);

		/**
		 * @return the {@link ActionListener} that start/stop the stream.
		 */
		ActionListener getStartStopButtonListener();

		/**
		 * @param inputPar
		 *            the {@link InputParameter} that needs a listener.
		 * @param slider
		 *            the {@link JSlider} associated with the
		 *            {@code InputParameter} .
		 * @param label
		 *            the {@link JLabel} that shows the current value of the
		 *            parameter.
		 * @return a {@code Listener} for the {@link JSlider}.
		 */
		ChangeListener getInputParameterSldListener(
				InputParameter<? extends Number> inputPar, JSlider slider,
				JLabel label);

		/**
		 * @param panel
		 *            the panel in which the switch {@link JButton} is located.
		 * @return a {@code Listener} for the {@link JButton}.
		 */
		ActionListener getUpperSwitchBtnListener(JPanel panel);

		/**
		 * @param panel
		 *            the panel in which the switch {@link JButton} is located.
		 * @return a {@code Listener} for the {@link JButton}.
		 */
		ActionListener getLowerSwitchBtnListener(JPanel panel);

		/**
		 * @param panel
		 *            the panel in which the remove {@link JButton} is located.
		 * @return a {@code Listener} for the {@link JButton}.
		 */
		ActionListener getRemoveEffectBtnListener(JPanel panel);
		
		ActionListener getAddEffectBtnListener(Class<? extends Effect> effectType);

		/**
		 * @return an {@code ActionListener} for the button which shows the
		 *         graph.
		 */
		ActionListener getGraphBtnListener();
	}
}