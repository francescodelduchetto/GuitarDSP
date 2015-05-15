package effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import model.AudioSettings;
import model.InputParameter;

/**
 * Perform the flanger effect on the input buffer.
 * 
 * <p>
 * Applies to the given buffer a phase modulation that continuously modify the
 * time of delay with an oscillating function.
 * 
 * @author Francesco Del Duchetto
 */
@Effect.Attributes(name = "Flanger")
public class FlangerEffect implements Effect {

	/**
	 * Minimum value of the excursion {@link InputParameter}.
	 */
	private static final Integer MIN_EXCURSION = 0;

	/**
	 * Maximum value of the excursion {@link InputParameter}.
	 */
	private static final Integer MAX_EXCURSION = 500;

	/**
	 * Initial value of the excursion {@link InputParameter}.
	 */
	private static final Integer INITIAL_EXCURSION = 250;

	/**
	 * Controls the depth of the oscillation.
	 */
	private InputParameter<Integer> excursion = new InputParameter<>(
			"excursion", MIN_EXCURSION, MAX_EXCURSION, INITIAL_EXCURSION);

	/**
	 * Minimum value of the frequency {@link InputParameter}.
	 */
	private static final Double MIN_FREQUENCY = 0.0;

	/**
	 * Maximum value of the frequency {@link InputParameter}.
	 */
	private static final Double MAX_FREQUENCY = 1.0;

	/**
	 * Initial value of the frequency {@link InputParameter}.
	 */
	private static final Double INITIAL_FREQUENCY = 0.1;

	/**
	 * Controls the frequency of the oscillating function.
	 */
	private InputParameter<Double> frequency = new InputParameter<>(
			"frequency", MIN_FREQUENCY, MAX_FREQUENCY, INITIAL_FREQUENCY);

	/**
	 * Minimum value of the depth {@link InputParameter}.
	 */
	private static final Double MIN_DEPTH = 0.0;

	/**
	 * Maximum value of the depth {@link InputParameter}.
	 */
	private static final Double MAX_DEPTH = 1.0;

	/**
	 * Initial value of the depth {@link InputParameter}.
	 */
	private static final Double INITIAL_DEPTH = 0.5;

	/**
	 * Controls the amount of out-of-phase sound is added to the original sound.
	 */
	private InputParameter<Double> depth = new InputParameter<>("depth",
			MIN_DEPTH, MAX_DEPTH, INITIAL_DEPTH);

	/**
	 * Stores ideally the position in time of the stream.
	 */
	private long counter = 0;

	/**
	 * Supply the previous buffer given.
	 */
	private short[] previousBuffer = new short[AudioSettings.getAudioSettings()
			.getShortBufferLength()];

	/**
	 * Sample rate.
	 */
	private int sampleRate;

	/**
	 * Construct a {@link FlangerEffect}.
	 */
	public FlangerEffect() {
		Arrays.fill(previousBuffer, (short) 0);
		this.sampleRate = AudioSettings.getAudioSettings().getSampleRate();
	}

	@Override
	public final short process(short audioIn) {
		short newSample = audioIn;
		audioIn = (short) (audioIn * (1 - depth.getValue()) + this
				.getDelayShort(newSample) * depth.getValue());
		insertNewSample(newSample);
		return audioIn;
	}
	
	private void insertNewSample(short sample) {
		for (int i=0; i<previousBuffer.length-1; i++) {
			previousBuffer[i] = previousBuffer[i+1];
		}
		previousBuffer[previousBuffer.length-1] = sample;
	}

	/**
	 * @param audioIn
	 * 		the original buffer.
	 * @param n 
	 * 		the current position index on the original buffer.
	 * @return the sample that must be added to the current sample.
	 */
	private short getDelayShort(final short audioIn) {
		int ind = getOffsetIndex();
//		System.out.println("OffsetIndex: " + ind);
//		System.out.println("index: " + (previousBuffer.length - ind));
		
		return previousBuffer[previousBuffer.length - getOffsetIndex() -1];
	}

	/**
	 * @return the offset from the current position index.
	 */
	private int getOffsetIndex() {
		counter++;
		return (int) (excursion.getValue() * Math.abs(Math.sin(2 * Math.PI
				* counter * frequency.getValue() / sampleRate)));
	}

	@Override
	public final List<InputParameter<? extends Number>> getInputParameters() {
		List<InputParameter<? extends Number>> list = new ArrayList<>();
		list.add(frequency);
		list.add(excursion);
		list.add(depth);
		return list;
	}

	@Override
	public final void initialize() {
		counter = 0;
		Arrays.fill(previousBuffer, (short) 0);
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}
}
