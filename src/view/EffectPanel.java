package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import view.View.ViewObserver;
import model.InputParameter;
import effects.Effect;

/**
 * Panel that permits user to modify the effect's parameters.
 * 
 * <p>
 * It shows all the {@link Effect}'s {@code InputParameters} and some buttons to
 * switch its position and to remove itself.
 * 
 * @author Francesco Del Duchetto
 */

public class EffectPanel extends JPanel {
	private static final long serialVersionUID = 1;

	/**
	 * Construct an {@code EffectPanel}.
	 * 
	 * @param observer
	 *            the controller registered as observer.
	 * @param effect
	 *            the effect of which the panel shows the parameter.
	 */
	public EffectPanel(final ViewObserver observer, final Effect effect) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel paramPanel = new JPanel();	// Contains all the parameters
		paramPanel.setBorder(BorderFactory.createTitledBorder(effect.getClass()
				.getAnnotation(Effect.Attributes.class).name()));
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		/* Scrolls all the parameters of the effect */
		for (InputParameter<? extends Number> param : 
				effect.getInputParameters()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(param.getName() + " ",
					SwingConstants.RIGHT);
			label.setMaximumSize(new Dimension(100, Short.MAX_VALUE));
			label.setMinimumSize(new Dimension(100, 0));
			label.setPreferredSize(new Dimension(80, 20));
			label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
			JLabel labelValue = new JLabel("" + param.getValue());
			labelValue.setPreferredSize(new Dimension(45, 0));
			labelValue.setBorder(BorderFactory.createTitledBorder(""));
			JSlider slider = new JSlider(JSlider.HORIZONTAL,
					param.getIntMinValue(), param.getIntMaxValue(),
					param.getIntValue());
			slider.addChangeListener(observer.getInputParameterSldListener(
					param, slider, labelValue));
			/* Adds components */
			panel.add(label);
			panel.add(slider);
			panel.add(labelValue);
			paramPanel.add(panel);
		}
		paramPanel.setMaximumSize(new Dimension(605, 105));
		paramPanel.setMinimumSize(new Dimension(605, 80));
		paramPanel.setPreferredSize(new Dimension(605, 80));

		JButton upperSwitchBtn = new JButton("\u2191");
		upperSwitchBtn.addActionListener(observer
				.getUpperSwitchBtnListener(this));
		upperSwitchBtn.setMaximumSize(new Dimension(45, 20));
		upperSwitchBtn.setAlignmentX(CENTER_ALIGNMENT);

		JButton removeEffectBtn = new JButton("X");
		removeEffectBtn.addActionListener(observer
				.getRemoveEffectBtnListener(this));
		removeEffectBtn.setMaximumSize(new Dimension(45, 20));
		removeEffectBtn.setAlignmentX(CENTER_ALIGNMENT);

		JButton lowerSwitchBtn = new JButton("\u2193");
		lowerSwitchBtn.addActionListener(observer
				.getLowerSwitchBtnListener(this));
		lowerSwitchBtn.setMaximumSize(new Dimension(45, 20));
		lowerSwitchBtn.setAlignmentX(CENTER_ALIGNMENT);

		/* Contains the buttons to switch the effect whit the previous, whit 
		 * the following and to remove the effect itself.*/
		JPanel optionPanel = new JPanel();
		optionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		optionPanel.add(upperSwitchBtn);
		optionPanel.add(Box.createGlue());
		optionPanel.add(removeEffectBtn);
		optionPanel.add(Box.createGlue());
		optionPanel.add(lowerSwitchBtn);
		optionPanel.add(Box.createRigidArea(new Dimension(0, 4)));
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionPanel.setMaximumSize(new Dimension(55, 100));
		optionPanel.setMinimumSize(new Dimension(55, 80));
		optionPanel.setPreferredSize(new Dimension(55, 80));

		this.add(Box.createRigidArea(new Dimension(3, 0)));
		this.add(paramPanel);
		this.add(optionPanel);
		this.add(Box.createRigidArea(new Dimension(3, 0)));
		this.setMaximumSize(new Dimension(700, 110));
		this.setMinimumSize(new Dimension(700, 110));
		this.setPreferredSize(new Dimension(700, 110));
	}
}
