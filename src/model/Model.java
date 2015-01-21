package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import controller.Controller;
import effects.ButterworthLPFilterEffect;
import effects.DelayEffect;
import effects.Effect;
import effects.FlangerEffect;
import effects.OverdriveEffect;
import effects.ReverbEffect;

/**
 * GuitarDSP model.
 * 
 * <p>
 * The model asks the {@link AudioSystem} to give an input line from the audio
 * file and then open it, if the line isn't available return a
 * {@code LineUnavailableException}. It also set up the audio settings of the
 * line such as the length of the buffer or the sample rate.
 * 
 * <p>
 * It is able to start and stop a {@link OldStreamer} which acts the streaming of
 * the audio input and applies the effects selected. The effects are also
 * managed by the {@code Model} that provide methods to add and remove an effect
 * and to exchange two effects.
 * 
 * @author Francesco Del Duchetto
 */
public class Model {

	/**
	 * Minimum value of the attenuation {@link InputParameter}.
	 */
	private static final Double MIN_ATTENUATION = 0.0;

	/**
	 * Maximum value of the attenuation {@link InputParameter}.
	 */
	private static final Double MAX_ATTENUATION = 1.0;

	/**
	 * Initial value of the attenuation {@link InputParameter}.
	 */
	private static final Double INITIAL_ATTENUATION = 0.5;

	/**
	 * Attenuation of the audio input.
	 */
	private InputParameter<Double> inputAttenuation = 
			new InputParameter<Double>("input attenuation", MIN_ATTENUATION, 
					MAX_ATTENUATION, INITIAL_ATTENUATION);

	/**
	 * Output line.
	 */
	private SourceDataLine lineOut;

	/**
	 * Input line.
	 */
	private AudioInputStream lineIn;

	/**
	 * Instance of {@code Streamer}.
	 */
	private Mixer mixer;

	/**
	 * List of effects which are currently utilized.
	 */
	private List<Effect> effectsList = new ArrayList<>();

	/**
	 * Main controller.
	 */
	private Controller controller;

	/**
	 * Contains the settings of the audio streaming.
	 */
	private AudioSettings audioSettings;

	/**
	 * Constructor of the {@code Model}.
	 * 
	 * @param controller
	 *            the main {@link Controller} of the application.
	 */
	public Model(final Controller controller) {
		try {
			audioSettings = AudioSettings.getAudioSettings();
			lineOut = AudioSystem.getSourceDataLine(audioSettings
					.getAudioFormat());
			lineOut.open(audioSettings.getAudioFormat(),
					audioSettings.getBufferLength());
		} catch (LineUnavailableException e) {
			controller.showErrorDialog("Output line unavailable");
		}
		this.controller = controller;
		try {
			TargetDataLine usbLine = new USBDataLine("/dev/ttyUSB0", AudioSettings.getAudioSettings().getAudioFormat());
			usbLine.open();
			lineIn = new AudioInputStream(usbLine);
		} catch (Exception exception) {
			controller.showErrorDialog("Input line unavailable");
		}
	}

	/**
	 * @return the {@link List} of available effects.
	 */
	public final List<Class<? extends Effect>> getAvailableEffects() {
		List<Class<? extends Effect>> effects = new ArrayList<>();
		effects.add(DelayEffect.class);
		effects.add(OverdriveEffect.class);
		effects.add(FlangerEffect.class);
		effects.add(ReverbEffect.class);
		effects.add(ButterworthLPFilterEffect.class);
		return effects;
	}

	/**
	 * @return the {@link List} of effects that are currently utilized.
	 */
	public final List<Effect> getEffects() {
		return effectsList;
	}

	/**
	 * Add an effect.
	 * 
	 * @param effect
	 *            that must be added.
	 */
	public final void addEffect(final Effect effect) {
		effectsList.add(effect);
	}

	/**
	 * Remove an effect.
	 * 
	 * @param index
	 *            position of effect that must be deleted.
	 */
	public final void removeEffect(final int index) {
		effectsList.remove(index);
	}

	/**
	 * Exchange the position of two effects.
	 * 
	 * @param index1
	 *            the index of the first effect.
	 * @param index2
	 *            the index of the second effect.
	 */
	public final void exchangeEffects(final int index1, final int index2) {
		Effect ef = effectsList.get(index1);
		effectsList.set(index1, effectsList.get(index2));
		effectsList.set(index2, ef);
	}

	/**
	 * @return the {@link InputParameter}for attenuate the input audio.
	 */
	public final InputParameter<Double> getInputAttenuation() {
		return inputAttenuation;
	}

	/**
	 * Starts a new {@link OldStreamer} which stream the file located at the given
	 * path.
	 * 
	 * @param fileSelected
	 *            the path of the audio file.
	 */
	public final void startStream(final String fileSelected) {
		if (fileSelected != null) {
			this.mixer = new Mixer(lineOut, lineIn,
					inputAttenuation, controller, this);
			this.mixer.start();
		}
	}

	/**
	 * Stop the running {@link OldStreamer} if there is one and reinitialize the
	 * effects.
	 */
	public final void stopStream() {
		if (mixer != null) {
			mixer.stopStream();
		}
		for (Effect e : effectsList) {
			e.initialize();
		}
	}
	
	public Mixer getMixer() {
		return this.mixer;
	}
}