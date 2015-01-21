package effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import model.AudioSettings;
import model.InputParameter;

/**
 * Perform a reverberation on the audio signal.
 * 
 * <p>
 * This {@link Effect} simulates the natural reverb that occurs in closed rooms
 * (eg theaters, churches, gyms). In this environments a sound, emitted from a
 * source situated in the room, is reflected by each wall many times making a
 * reverberation of the sound. Much of the power of the sound is absorbed when
 * it reflect and when it travels through the air, especially the higher
 * frequencies, therefore this {@code Effect} attenuates the echoes and filters
 * it with a {@link ButterworthLPFilterEffect}.
 * 
 * <p>
 * This effect is based on Schroeder reverberations studies that uses earlier a
 * chain of all-pass filters (filter that passes all frequencies equally in gain
 * but changes the phase realtionship between various frequencies) and later
 * some comb filters(essentially a delay line) in parallel.
 * 
 * @author Francesco Del Duchetto
 */
@Effect.Attributes(name = "Reverb")
public class ReverbEffect implements Effect {

	/**
	 * Number of all-pass filter.
	 */
	private static final int NUM_EARLY_LINES = 3;

	/**
	 * Vector of delay lines used by the all-pass filter.
	 */
	private DelayLine[] earlyDelayLines = new DelayLine[NUM_EARLY_LINES];

	/**
	 * Number of comb filters.
	 */
	private static final int NUM_LATE_LINES = 4;

	/**
	 * Vector of delay lines used by the comb filters.
	 */
	private DelayLine[] lateDelayLines = new DelayLine[NUM_LATE_LINES];

	/**
	 * Attenuation of the echoes.
	 */
	private static final double ATTENUATION = 0.7;

	/**
	 * Minimum value of the gain {@link InputParameter}.
	 */
	private static final Double MIN_GAIN = 0.0;

	/**
	 * Maximum value of the gain {@link InputParameter}.
	 */
	private static final Double MAX_GAIN = 0.5;

	/**
	 * Initial value of the gain {@link InputParameter}.
	 */
	private static final Double INITIAL_GAIN = 0.4;

	/**
	 * Parameter that controls the delay of the delay lines.
	 */
	private InputParameter<Double> gain = new InputParameter<>("gain",
			MIN_GAIN, MAX_GAIN, INITIAL_GAIN);

	/**
	 * Minimum value of the roomSize {@link InputParameter}.
	 */
	private static final Integer MIN_ROOM_SIZE = 0;

	/**
	 * Maximum value of the roomSize {@link InputParameter}.
	 */
	private static final Integer MAX_ROOM_SIZE = 9000;

	/**
	 * Initial value of the roomSize {@link InputParameter}.
	 */
	private static final Integer INITIAL_ROOM_SIZE = 7000;

	/**
	 * Parameter that controls the size of the imaginary room that causes the
	 * reverberation.
	 */
	private InputParameter<Integer> roomSize = new InputParameter<Integer>(
			"size", MIN_ROOM_SIZE, MAX_ROOM_SIZE, INITIAL_ROOM_SIZE);

	/**
	 * A {@code ButterworthLPFilterEffect} that attenuate the high frequencies.
	 */
	private ButterworthLPFilterEffect lowPassFilter = 
			new ButterworthLPFilterEffect();

	/**
	 * Cutoff value of the {@code ButterworthLPFilterEffect}.
	 */
	private static final int LP_FILTER_CUTOFF = 3000;
	
	/**
	 * Stores the echoes generated by the all-pass filters.
	 */
	private short[][] earlyEchoes = new short[NUM_EARLY_LINES][AudioSettings
			.getAudioSettings().getShortBufferLength()];

	/**
	 * Stores the echoes generated by the comb filters.
	 */
	private short[][] lateEchoes = new short[NUM_LATE_LINES][AudioSettings
			.getAudioSettings().getShortBufferLength()];

	/**
	 * Internal use vector.
	 */
	private short[] x = new short[AudioSettings.getAudioSettings()
			.getShortBufferLength()];

	/**
	 * Internal use vector.
	 */
	private short[] echo = new short[AudioSettings.getAudioSettings()
			.getShortBufferLength()];

	/**
	 * Construct a {@code ReverbEffect}.
	 */
	@SuppressWarnings("unchecked")
	public ReverbEffect() {
		/* Create early delay lines */
		for (int i = 0; i < NUM_EARLY_LINES; i++) {
			earlyDelayLines[i] = new DelayLine(new InputParameter<Integer>("",
					roomSize.getMinValue(), roomSize.getMaxValue(),
					roomSize.getValue()));
			Arrays.fill(earlyEchoes[i], (short) 0);
		}
		/* Create late delay lines */
		for (int i = 0; i < NUM_LATE_LINES; i++) {
			lateDelayLines[i] = new DelayLine(new InputParameter<Integer>("",
					roomSize.getMinValue(), roomSize.getMaxValue(),
					roomSize.getValue()));
			Arrays.fill(lateEchoes[i], (short) 0);
		}
		((InputParameter<Integer>) lowPassFilter.getInputParameters().get(0))
				.setValue(LP_FILTER_CUTOFF);
		/* Set the observers */
		roomSize.addObserver(this);
		gain.addObserver(this);
		this.update(gain, null);
		this.update(roomSize, null);
	}

	@Override
	public final void process(final short[] audioIn, final int shortsRead) {

		echo = Arrays.copyOf(audioIn, shortsRead);

		/* Create late echoes */
		for (int i = 0; i < NUM_LATE_LINES; i++) {
			lateEchoes[i] = lateDelayLines[i].getReponse(audioIn, shortsRead);
			for (int j = 0; j < shortsRead; j++) {
				audioIn[j] += lateEchoes[i][j] * ATTENUATION;
			}
		}
		lowPassFilter.process(audioIn, shortsRead);

		/* Create early echoes */
		for (int i = 0; i < NUM_EARLY_LINES; i++) {
			for (int j = 0; j < shortsRead; j++) {
				x[j] = (short) (echo[j] + earlyEchoes[i][j] * ATTENUATION);
			}
			earlyEchoes[i] = earlyDelayLines[i].getReponse(x, shortsRead);
			for (int j = 0; j < shortsRead; j++) {
				echo[j] = (short) (x[j] * -ATTENUATION + earlyEchoes[i][j]);
			}
		}
		/* Apply high frequecies cutting */
		for (int i = 0; i < shortsRead; i++) {
			audioIn[i] += echo[i];
		}

	}

	@Override
	public final List<InputParameter<? extends Number>> getInputParameters() {
		List<InputParameter<? extends Number>> list = new ArrayList<>();
		list.add(gain);
		list.add(roomSize);
		return list;
	}

	@Override
	public final void initialize() {
		for (DelayLine line : lateDelayLines) {
			line.emptyDelayBuffer();
		}
		for (DelayLine line : earlyDelayLines) {
			line.emptyDelayBuffer();
		}
		for (int i = 0; i < NUM_EARLY_LINES; i++) {
			Arrays.fill(earlyEchoes[i], (short) 0);
		}
		for (int i = 0; i < NUM_LATE_LINES; i++) {
			Arrays.fill(lateEchoes[i], (short) 0);
		}
	}

	@Override
	public final void update(final Observable o, final Object arg) {
		if (o.equals(gain)) {
			for (int i = 0; i < NUM_EARLY_LINES; i++) {
				earlyDelayLines[i].getFeedback().setValue(gain.getValue());
			}
			for (int i = 0; i < NUM_LATE_LINES; i++) {
				lateDelayLines[i].getFeedback().setValue(gain.getValue());
			}
		} else if (o.equals(roomSize)) {
			for (int i = 0; i < NUM_EARLY_LINES; i++) {
				/* 44100 sample per second => 1 ms = ~4 samples */
				int delayTime = (int) (roomSize.getValue() / (2 * Math
						.pow(3, i)));
				earlyDelayLines[i].getDelayLength().setValue(delayTime);
			}
			for (int i = 0; i < NUM_LATE_LINES; i++) {
				int delayTime = (int) (roomSize.getValue() / Math.pow(3, i));
				lateDelayLines[i].getDelayLength().setValue(delayTime);
			}
		}
	}

	@Override
	public void touchpadEvent(int touchX, int touchY, int pressure) {
		// TODO Auto-generated method stub
		
	}
}
