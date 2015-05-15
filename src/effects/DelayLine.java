package effects;

import java.io.Serializable;
import java.util.Arrays;

import model.InputParameter;

/**
 * The delay line is the elementary unit which models acoustic propagation
 * delay.
 * 
 * <p>
 * The function of a delay line is to introduce a time delay between its input
 * buffer and the output buffer. The delay time is express by an integer number
 * and coincide with the number of sample of delay.
 * 
 * <p>
 * The output returned is attenuated by a real value included between 0 and 1
 * called feedback. If the feedback is equal to 0 the output is a zero buffer
 * (volume 0) that not contains any sounds, instead if its value is 1 the volume
 * of the output is the same as the original.
 * 
 * <p>
 * This class just add the input buffer to the previous buffers, storing them
 * into a circular buffer, and return the corresponding buffer, also took from
 * the circular buffer.
 * 
 * <p>
 * The delay length value stored is the number of samples of delay, considering
 * that the application work with a sample rate of 44100 Hz, that is 44100
 * samples per second, about 44 samples correspond to 1 millisecond of delay.
 * 
 * @author Francesco Del Duchetto
 */
public class DelayLine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3348182715758860788L;

	/**
	 * Max value of the delay time that can be applied.
	 */
	public static final int MAX_DELAY_LENGTH = 2000;

	/**
	 * The circular buffer that stores the echoes.
	 */
	private volatile short[] delayBuffer;

	/**
	 * The length of the circular buffer that correspond with the delay time.
	 * This value is passed as a parameter in the constructor to avoid occupy
	 * unnecessary memory by default.
	 */
	private InputParameter<Integer> delayLength;

	/**
	 * Minimum value of the feedback {@link InputParameter}.
	 */
	private static final Double MIN_FEEDBACK = 0.0;

	/**
	 * Maximum value of the feedback {@link InputParameter}.
	 */
	private static final Double MAX_FEEDBACK = 1.0;

	/**
	 * Initial value of the feedback {@link InputParameter}.
	 */
	private static final Double INITIAL_FEEDBACK = 0.4;
	
	/**
	 * The feedback value of the delay.
	 */
	private InputParameter<Double> feedback = new InputParameter<>("feedback",
			MIN_FEEDBACK, MAX_FEEDBACK, INITIAL_FEEDBACK);

	/**
	 * It keeps the index of the current position in the circular buffer.
	 */
	private int current;

	/**
	 * The buffer that is returned.
	 */
	private short audioResponse;

	/**
	 * Construct a {@code DelayLine}.
	 * 
	 * @param delayLength
	 *            the parameter that store the delay length of the internal
	 *            buffer.
	 */
	public DelayLine(final InputParameter<Integer> delayLength) {
		delayBuffer = new short[delayLength.getMaxValue()];
		this.delayLength = delayLength;
	}

	/**
	 * Stores the given buffer, multiplied by the feedback, into the circular
	 * buffer and returns the corresponding echoes.
	 * 
	 * @param audioIn
	 *            the input buffer.
	 * @param shortsRead
	 *            the length of the input buffer.
	 * @return the corresponding echoes for the given buffer.
	 */
	public final short getReponse(final short audioIn) {
		if (delayLength.getValue() != 0) {
			audioResponse = delayBuffer[current];
			delayBuffer[current] += audioIn;
			delayBuffer[current] *= -feedback.getValue();
			current++;
			current %= delayLength.getValue();
			return audioResponse;
		} else {
			return 0;
		}
	}

	/**
	 * @return the delay length parameter.
	 */
	public final InputParameter<Integer> getDelayLength() {
		return delayLength;
	}

	/**
	 * @return the feedback parameter.
	 */
	public final InputParameter<Double> getFeedback() {
		return feedback;
	}

	/**
	 * Empty the circular buffer.
	 */
	public final void emptyDelayBuffer() {
		emptyBuffer(delayBuffer);
	}

	/**
	 * Empty the given buffer.
	 * @param buffer
	 * 			the buffer that must be emptied.
	 */
	private void emptyBuffer(short[] buffer) {
		Arrays.fill(buffer, (short) 0);
	}
}
