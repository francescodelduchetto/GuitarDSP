package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.GraphView;
import view.View;
import model.InputParameter;
import model.Model;
import effects.Effect;

/**
 * GuitarDSP controller.
 * 
 * <p>
 * Acts as a bridge between the {@link View} and the {@link Model} providing
 * some methods to access resources of both. Also provide the{@code View}'s
 * {@link Component}'s listeners. It implements the {@code ViewObserver}, thus
 * provide the Listeners of the {@code View's} components.
 * 
 * @author Francesco Del Duchetto
 */
public class Controller implements View.ViewObserver {

	/**
	 * Instance of View.
	 */
	private View view;

	/**
	 * Instance of Model.
	 */
	private Model model;

	/**
	 * The value is true if a Streamer is streaming, false if not.
	 */
	private boolean isStreamRunning;

	/**
	 * The text that the start/stop button of the View must show when the
	 * Streamer is running.
	 */
	private static final String RUNNING_TEXT = "Stop";

	/**
	 * The text that the start/stop button of the View must show when the
	 * Streamer is not running.
	 */
	private static final String NOT_RUNNING_TEXT = "Play";

	/**
	 * The frame that shows the graph of the output audio.
	 */
	private GraphView graphView;

	/**
	 * Construct a {@link Controller} that create a {@link Model} and a
	 * {@link View}.
	 */
	public Controller() {
		this.model = new Model(this);
		this.view = new View(this);
		view.setVisible(true);
		view.registerObserver(this);
	}

	/**
	 * This method create an error dialog that tells the user what kind of error
	 * happened.
	 * 
	 * @param message
	 *            the string that the dialog must show.
	 */
	public final void showErrorDialog(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(view, message, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Must be invoked when a {@link model.OldStreamer} starts to stream.
	 */
	public final void streamStarted() {
		isStreamRunning = true;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.setStartStopButtonText(RUNNING_TEXT);
			}
		});
	}

	/**
	 * Must be invoked when a {@link model.OldStreamer} stops to stream.
	 */
	public final void streamStopped() {
		isStreamRunning = false;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.setStartStopButtonText(NOT_RUNNING_TEXT);
			}
		});
	}

	/**
	 * Create a new {@link GraphView} which shows the output signal.
	 */
	public final void createGraphView() {
		if (this.graphView == null) {
			this.graphView = new GraphView();
		}
	}

	/**
	 * Update the graph giving the new buffer processed.
	 * 
	 * @param currentBuffer
	 *            the new audio buffer processed.
	 */
	public final void updateGraph(final short newSample) {
		
		if (this.graphView != null) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					graphView.repaint(newSample);
					graphView.revalidate();
				}
			});
		}
	}

	/**
	 * @return the {@link InputParameter} of the input attenuation.
	 */
	public final InputParameter<Double> getInputAttenuation() {
		return model.getInputAttenuation();
	}

	@Override
	public final WindowListener getWindowListener() {
		return new WindowListener() {

			@Override
			public void windowOpened(final WindowEvent e) {
			}

			@Override
			public void windowIconified(final WindowEvent e) {
			}

			@Override
			public void windowDeiconified(final WindowEvent e) {
			}

			@Override
			public void windowDeactivated(final WindowEvent e) {
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				model.stopStream();
				System.exit(0); // close the application
			}

			@Override
			public void windowClosed(final WindowEvent e) {
			}

			@Override
			public void windowActivated(final WindowEvent e) {
			}
		};
	}

	@Override
	public final ActionListener getOpenButtonListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fileChooser = view.getFileChooser();
				int returnVal = fileChooser.showOpenDialog(view);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File soundFile = fileChooser.getSelectedFile();
					String filePath = soundFile.getPath();
					view.getFileNameText().setText(filePath);
				}
			}
		};
	}

	@Override
	public final ActionListener getAddButtonListener(final JButton addButton) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				JPopupMenu effectsPopup = new JPopupMenu();
				List<Class<? extends Effect>> effects = model
						.getAvailableEffects();
				for (Class<? extends Effect> c : effects) {
					if (c.getAnnotation(Effect.Attributes.class).isShowable()) {
						JMenuItem item = new JMenuItem(c.getAnnotation(
								Effect.Attributes.class).name());
						item.addActionListener(new PopupMenuItemListener(c));
						effectsPopup.add(item);
					}
				}
				Point mousePosition = MouseInfo.getPointerInfo().getLocation();
				effectsPopup.show(addButton,
						mousePosition.x - addButton.getLocationOnScreen().x,
						addButton.getSize().height);
			}
		};
	}

	@Override
	public final ActionListener getStartStopButtonListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (isStreamRunning) {
					model.stopStream();
				} else {
					model.startStream(view.getFileNameText().getText());
				}
			}
		};
	}

	@Override
	public final ChangeListener getInputParameterSldListener(
			final InputParameter<? extends Number> parameter,
			final JSlider slider, final JLabel labelValue) {
		return new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				parameter.setIntValue(slider.getValue());
				if (labelValue != null) {
					labelValue.setText("" + parameter.getValue());
				}
			}
		};
	}

	@Override
	public final ActionListener getUpperSwitchBtnListener(final JPanel panel) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Container outerPanel = panel.getParent();
				int panelIndex = view.getComponentIndex(panel);
				int prevPanelIndex = panelIndex - 1;
				if (prevPanelIndex >= 0) {
					List<Component> componentsList = new ArrayList<>();
					Component previousComp = outerPanel
							.getComponent(prevPanelIndex);
					Color prevColor = previousComp.getBackground();
					view.setComponentColor(previousComp, panel.getBackground());
					view.setComponentColor(panel, prevColor);
					componentsList.add(panel);
					componentsList.add(previousComp);
					for (int i = panelIndex + 1; i < outerPanel
							.getComponentCount(); i++) {
						componentsList.add(outerPanel.getComponent(i));
					}
					for (Component c : componentsList) {
						outerPanel.add(c);
					}
					model.exchangeEffects(panelIndex, prevPanelIndex);
					outerPanel.revalidate();
				}
			}
		};
	}

	@Override
	public final ActionListener getLowerSwitchBtnListener(final JPanel panel) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Container outerPanel = panel.getParent();
				int panelIndex = view.getComponentIndex(panel);
				int nextPanelIndex = panelIndex + 1;
				if (nextPanelIndex < panel.getParent().getComponentCount()) {
					Component nextComp = outerPanel
							.getComponent(nextPanelIndex);
					List<Component> componentsList = new ArrayList<>();
					Color nextColor = nextComp.getBackground();
					view.setComponentColor(nextComp, panel.getBackground());
					view.setComponentColor(panel, nextColor);
					componentsList.add(nextComp);
					componentsList.add(panel);
					for (int i = nextPanelIndex + 1; i < outerPanel
							.getComponentCount(); i++) {
						componentsList.add(outerPanel.getComponent(i));
					}
					for (Component c : componentsList) {
						outerPanel.add(c);
					}
					model.exchangeEffects(panelIndex, nextPanelIndex);
					outerPanel.revalidate();
				}
			}
		};
	}

	@Override
	public final ActionListener getRemoveEffectBtnListener(final JPanel panel) {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Container outerPanel = panel.getParent();
				int panelIndex = view.getComponentIndex(panel);
				for (int i = outerPanel.getComponentCount() - 1; i > panelIndex; i--) {
					Component prevComp = outerPanel.getComponent(i - 1);
					Color prevColor = prevComp.getBackground();
					view.setComponentColor(outerPanel.getComponent(i),
							prevColor);
				}
				outerPanel.remove(panel);
				model.removeEffect(panelIndex);
				outerPanel.revalidate();
				outerPanel.repaint();
			}
		};
	}

	@Override
	public final ActionListener getGraphBtnListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				createGraphView();
				graphView.setLocation(view.getSize().width, 0);
				graphView.setVisible(true);
			}
		};
	}

	/**
	 * The ActionListener which creates a new Effect and shows it on the View as
	 * a result of pressing of a JMenuItem.
	 */
	private class PopupMenuItemListener implements ActionListener {

		/**
		 * The type of the effect given by constructor.
		 */
		private final Class<? extends Effect> effectType;

		/**
		 * Construct a new {@code PopupMenuItemListener}.
		 * 
		 * @param classType
		 *            the type of the {@link Effect} selected.
		 */
		public PopupMenuItemListener(final Class<? extends Effect> classType) {
			this.effectType = classType;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				Effect effectInstance = effectType.newInstance();
				model.addEffect(effectInstance);
				view.createEffectPanel(effectInstance);
			} catch (InstantiationException | IllegalAccessException e1) {
				showErrorDialog("Unable to create effect");
			}
		}
	}
}
