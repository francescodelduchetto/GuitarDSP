package effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.InputParameter;

/**
 * Performs a distortion of the audio signal.
 * 
 * <p>
 * This effect modifies the audio throw two parameters:
 * <ul>
 * <li>Drive: allows users to decide their distorted wave shape should be more
 * like a sine-wave or more like a square wave.
 * <li>Cutoff: is controlled by a {@link ButterworthLPFilterEffect} which cut
 * the high frequencies.
 * 
 * @author Francesco Del Duchetto
 */
@Effect.Attributes(name = "Overdrive")
public class OverdriveEffect implements Effect {

	/**
	 * Instance of {@link ButterworthLPFilterEffect}.
	 */
	private static final ButterworthLPFilterEffect LPFILTER = 
			new ButterworthLPFilterEffect();

	/**
	 * Minimum value of the drive {@link InputParameter}.
	 */
	private static final Double MIN_DRIVE = 0.0;

	/**
	 * Maximum value of the drive {@link InputParameter}.
	 */
	private static final Double MAX_DRIVE = 0.99;

	/**
	 * Initial value of the drive {@link InputParameter}.
	 */
	private static final Double INITIAL_DRIVE = 0.5;

	/**
	 * Parameter that controls the distortion level.
	 */
	private InputParameter<Double> drive = new InputParameter<>("drive",
			MIN_DRIVE, MAX_DRIVE, INITIAL_DRIVE);

	/**
	 * Internal-use variables.
	 */
	private double k;
	private double a;
	private double x;

	@Override
	public final short process(short audioIn) {
		if (drive.getValue() != 0) {
			x = (double) (audioIn) / Short.MAX_VALUE;
			a = Math.sin(drive.getValue() * Math.PI / 2);
			k = 2 * a / (1 - a);
			x = (1 + k) * x / (1 + k * Math.abs(x));
			audioIn = (short) (x * Short.MAX_VALUE);
			LPFILTER.process(audioIn);
		}
		return audioIn;
	}

	@Override
	public final List<InputParameter<? extends Number>> getInputParameters() {
		List<InputParameter<? extends Number>> list = new ArrayList<>(
				LPFILTER.getInputParameters());
		list.add(drive);
		return list;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void update(final Observable o, final Object arg) {
	}
}
