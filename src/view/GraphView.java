package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.AudioSettings;

/**
 * This class shows the graph of the output signal.
 * 
 * <p>
 * It shows to the user the audio signal modified by {@link effects.Effect}. It contains
 * only a {@link GraphView} that override the {@code paintComponent} method of
 * its superclass {@code JPanel} to draw the output signal. The refresh of the
 * graph is made when a component calls the {@code repaint(short[])} method,
 * giving the new audio buffer to show.
 * 
 * The {@code GraphPanel}, at every refresh of the {@code GraphView} update the
 * graph with the new buffer given.
 * 
 * @author Francesco Del Duchetto
 * 
 */
public class GraphView extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * The audio buffer that is shown.
	 */
	private short[] buffer;
	
	private int i;

	/**
	 * Create a new {@code GraphView} with a {@link GraphView} in it.
	 */
	public GraphView() {
		super("Output graph");

		this.setSize(600, 400);
		this.setContentPane(new GraphPanel());
		this.buffer = new short[AudioSettings.getAudioSettings().getBufferLength()];
		this.i = 0;
	}

	/**
	 * Refresh the graph.
	 * 
	 * @param currentBuffer
	 *            the new signal buffer that must be shown.
	 */
	public final void repaint(final short currentBuffer) {
		this.buffer[i++] = currentBuffer;
		if (i == buffer.length) {
			i = 0;
			super.repaint();
		}
	}

	/**
	 * It is a private class that extends {@link JPanel} overriding the
	 * {@code paintComponent(Graphics)} method. This method repaint, at each
	 * refresh of the frame, the signal given from the buffer resizing it
	 * according to the size of the panel.
	 * 
	 * @author Francesco Del Duchetto
	 * 
	 */
	private class GraphPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		/**
		 * Create a new {@link GraphView}.
		 */
		public GraphPanel() {
			super();
			this.setBackground(Color.WHITE);
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			if (buffer != null) {
				double ax = (double) buffer.length / this.getSize().width;
				double ay = (Short.MAX_VALUE - Short.MIN_VALUE)
						/ (this.getSize().height - 3);
				for (int i = 0; i < buffer.length - 1; i++) {
					int x1 = (int) Math.round(i / ax);
					int x2 = (int) Math.round((i + 1) / ax);
					int y1 = (int) Math.round(buffer[i] / ay);
					int y2 = (int) Math.round(buffer[i + 1] / ay);
					y1 += this.getSize().height / 2 - 1;
					y2 += this.getSize().height / 2 - 1;
					g.drawLine(x1, y1, x2, y2);
				}
			}

		}
	}
}
