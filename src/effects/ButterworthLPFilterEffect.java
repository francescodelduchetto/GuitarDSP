package effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import model.AudioSettings;
import model.InputParameter;

/**
 * Perform an high frequency attenuating of the given audio signal.
 * 
 * <p>
 * This is an implementation of a two pole Butterworth low pass filter. The
 * Butterworth is a filter that is designed to have as flat a frequency response
 * as possible in the passband.
 * 
 * <p>
 * It is used to modify the tone in the {@link OverdriveEffect} and for simulate
 * the high frequency decay in the {@link ReverbEffect}.
 * 
 * @author Francesco Del Duchetto
 * 
 */
@Effect.Attributes(name = "Butterworth low pass filter", isShowable = false)
public class ButterworthLPFilterEffect implements Effect {

	/**
	 * Minimun value of the cutoff {@link InputParameter}.
	 */
	private static final Integer MIN_CUTOFF = 0;

	/**
	 * Maximum value of the cutoff {@link InputParameter}.
	 */
	private static final Integer MAX_CUTOFF = 8000;

	/**
	 * Initial value of the cutoff {@link InputParameter}.
	 */
	private static final Integer INITIAL_CUTOFF = 2000;
	
	/**
	 * Instance of {@link InputParameter} that represents the threshold of the
	 * frequency attenuation.
	 */
	private InputParameter<Integer> cutoff = new InputParameter<>("cutoff",
			MIN_CUTOFF, MAX_CUTOFF, INITIAL_CUTOFF);
	/**
	 * Internal use variables.
	 */
	private double[] xv = new double[3];
	private double[] yv = new double[3];
	private double[] ax = new double[3];
	private double[] by = new double[3];

	/**
	 * Construct a Butterworth filter.
	 */
	public ButterworthLPFilterEffect() {
		cutoff.addObserver(this);
		/*
		 * To create the necessary coefficients with the initial value of
		 * cutoff.
		 */
		update(cutoff, null);
	}

	@Override
	public final void process(final short[] audioIn, final int shortsRead) {
		for (int i = 0; i < shortsRead; i++) {
			xv[2] = xv[1];
			xv[1] = xv[0];
			xv[0] = (double) (audioIn[i]) / Short.MAX_VALUE;
			yv[2] = yv[1];
			yv[1] = yv[0];
			yv[0] = ax[0] * xv[0] + ax[1] * xv[1] + ax[2] * xv[2] - by[1]
					* yv[0] - by[2] * yv[1];
			audioIn[i] = (short) (yv[0] * Short.MAX_VALUE);
		}
	}

	@Override
	public final List<InputParameter<? extends Number>> getInputParameters() {
		List<InputParameter<? extends Number>> list = new ArrayList<>();
		list.add(cutoff);
		return list;
	}

	@Override
	public void initialize() {
	}

	@Override
	public final void update(final Observable source, final Object arg) {
		if (source.equals(cutoff)) {
			// Find cutoff frequency in [0..PI]
			double qcRaw = (2 * Math.PI * cutoff.getValue())
					/ AudioSettings.getAudioSettings().getSampleRate();
			double qcWarp = Math.tan(qcRaw); // Warp cutoff frequency
			double sqrt2 = Math.sqrt(2);
			double gain = 0;
			if (cutoff.getValue() != 0) {
				gain = 1 / (1 + sqrt2 / qcWarp + 2 / (qcWarp * qcWarp));
				by[2] = (1 - sqrt2 / qcWarp + 2 / (qcWarp * qcWarp)) * gain;
				by[1] = (2 - 4 / (qcWarp * qcWarp)) * gain;
				by[0] = 1.0;
				ax[0] = 1 * gain;
				ax[1] = 2 * gain;
				ax[2] = 1 * gain;
			}
		}
	}
}
