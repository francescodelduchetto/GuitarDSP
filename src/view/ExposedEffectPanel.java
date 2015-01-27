package view;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.View.ViewObserver;
import effects.Effect;

public class ExposedEffectPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8644688883310112468L;

	public ExposedEffectPanel(final ViewObserver observer, final Class<? extends Effect> effectType) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel expositionPanel = new JPanel();
		expositionPanel.setLayout(new BoxLayout(expositionPanel,
				BoxLayout.Y_AXIS));

		expositionPanel.setMaximumSize(new Dimension(205, 105));
		expositionPanel.setMinimumSize(new Dimension(205, 80));
		expositionPanel.setPreferredSize(new Dimension(205, 80));

		JLabel expositionLabel = new JLabel();
		expositionLabel.setText(effectType.getAnnotation(Effect.Attributes.class).name());
		
		JButton addButton = new JButton();
		addButton.setText("Click to use effect");
		addButton.addActionListener(observer.getAddEffectBtnListener(effectType));
		expositionPanel.add(expositionLabel);
		expositionPanel.add(addButton);

		/*
		 * Contains the buttons to switch the effect whit the previous, whit the
		 * following and to remove the effect itself.
		 */

		this.add(Box.createRigidArea(new Dimension(3, 0)));
		this.add(expositionPanel);
		this.add(addButton);
		this.add(Box.createRigidArea(new Dimension(3, 0)));
		this.setMaximumSize(new Dimension(700, 110));
		this.setMinimumSize(new Dimension(700, 110));
		this.setPreferredSize(new Dimension(700, 110));
	}
}
