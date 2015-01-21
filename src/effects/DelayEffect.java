package effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.AudioSettings;
import model.InputParameter;

/**
 * Perform a delay on the audio signal.
 * 
 * <p>
 * Given one buffer returns the same audio plus the previous buffers attenuated,
 * considering the length of the delay and the feedback gain.
 * 
 * @author Francesco Del Duchetto
 */
@Effect.Attributes(name = "Delay")
public class DelayEffect implements Effect {

	/**
	 * Minimun value of the delayLength {@link InputParameter}.
	 */
	private static final Integer MIN_DELAY_LENGTH = 0;

	/**
	 * Minimun value of the delayLength {@link InputParameter}.
	 */
	private static final Integer MAX_DELAY_LENGTH = DelayLine.MAX_DELAY_LENGTH;

	/**
	 * Initial value of the delayLength {@link InputParameter}.
	 */
	private static final Integer INITIAL_DELAY_LENGTH = 
			DelayLine.MAX_DELAY_LENGTH / 2;
	/**
	 * The length of the delay.
	 */
	private InputParameter<Integer> delayLength = new InputParameter<>(
			"length", MIN_DELAY_LENGTH, MAX_DELAY_LENGTH, INITIAL_DELAY_LENGTH);

	/**
	 * {@link DelayLine} which stores the echoes.
	 */
	private DelayLine delayLine = new DelayLine(delayLength);

	/**
	 * The buffer of audio processed.
	 */
	private short[] audioResponse = new short[AudioSettings.getAudioSettings()
			.getShortBufferLength()];

	/**
	 * Construct a {@link DelayEffect}.
	 */
	public DelayEffect() {
		delayLength.addObserver(this);
	}

	@Override
	public final void process(final short[] audioIn, final int shortsRead) {
		audioResponse = delayLine.getReponse(audioIn, shortsRead);
		for (int i = 0; i < shortsRead; i++) {
			audioIn[i] += audioResponse[i];
		}
	}

	@Override
	public final List<InputParameter<? extends Number>> getInputParameters() {
		List<InputParameter<? extends Number>> list = new ArrayList<>();
		list.add(delayLength);
		list.add(delayLine.getFeedback());
		return list;
	}

	@Override
	public final void initialize() {
		delayLine.emptyDelayBuffer();
	}

	@Override
	public final void update(final Observable o, final Object arg) {
		delayLine.emptyDelayBuffer();
	}

	@Override
	public void touchpadEvent(int touchX, int touchY, int pressure) {
		// TODO Auto-generated method stub
		
	}
}
