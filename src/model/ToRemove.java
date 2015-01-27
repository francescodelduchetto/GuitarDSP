package model;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import controller.Controller;

import effects.Effect;

/**
 * 
 * Thread that process the input audio.
 * 
 * <p>
 * It creates an {@link AudioInputStream} from the sound file given, then gets
 * the bytes from the stream and applies the effects on it, the resulting bytes
 * are given to the {@link SourceDataLine} which writes them to the system
 * mixer.
 * 
 * @author Francesco Del Duchetto
 */
public class ToRemove extends Thread {

	/**
	 * Input line.
	 */
	private AudioInputStream inputStream;

	/**
	 * Sound file.
	 */
	private File soundFile;

	/**
	 * Output line.
	 */
	private SourceDataLine lineOut;

	/**
	 * It is true if the stream is stopped.
	 */
	private volatile boolean isStreamStopped;

	/**
	 * Attenuation of the input sound.
	 */
	private final InputParameter<Double> attenuation;

	/**
	 * Main model of the application.
	 */
	private Model model;

	/**
	 * Main controller of the application.
	 */
	private Controller controller;

	/**
	 * Audio settings.
	 */
	private AudioSettings audioSettings;

	/**
	 * Construct a new {@code Streamer}.
	 * 
	 * @param lineOut
	 *            the output line.
	 * @param soundFile
	 *            the sound file.
	 * @param attenuation
	 *            the attenuation parameter.
	 * @param controller
	 *            the main controller.
	 * @param model
	 *            the main model.
	 */
	public ToRemove(final SourceDataLine lineOut, final File soundFile,
			final InputParameter<Double> attenuation,
			final Controller controller, final Model model) {
		this.soundFile = soundFile;
		this.lineOut = lineOut;
		this.attenuation = attenuation;
		this.model = model;
		this.controller = controller;
		this.audioSettings = AudioSettings.getAudioSettings();
	}

	@Override
	public final void run() {
		isStreamStopped = false;
		// Obtain an AudioInputStream
		try {
			inputStream = AudioSystem.getAudioInputStream(
					audioSettings.getAudioFormat(),
					AudioSystem.getAudioInputStream(soundFile));
		} catch (UnsupportedAudioFileException e1) {
			controller.showErrorDialog("File selected unsupported");
			stopStream();
		} catch (IOException e1) {
			controller.showErrorDialog("File selected unavailable");
			stopStream();
		}
		controller.streamStarted();
		// Start source
		lineOut.start();

		// Create audio buffer
		byte[] audioBuffer = new byte[audioSettings.getBufferLength()];
		short[] shortAudioBuffer = new short[audioSettings
				.getShortBufferLength()];

		int bytesRead = -1;
		int shortsRead = -1;

		try {
			if (inputStream != null) {
				/* Read bytes from the input line */
				bytesRead = inputStream.read(audioBuffer, 0,
						audioSettings.getBufferLength());
				shortsRead = bytesRead / 2;
			}
			while (bytesRead != -1 && !isStreamStopped) {
				/* Convert byte[] to short[] */
				ByteBuffer.wrap(audioBuffer)
						.order(audioSettings.getByteOrder()).asShortBuffer()
						.get(shortAudioBuffer);

				/* attenuate the input gain */
				this.attenuate(shortAudioBuffer);

				/* Apply the effects */
				java.util.List<Effect> effects = model.getEffects();
				for (int i = 0; i < effects.size(); i++) {
					effects.get(i).process(shortAudioBuffer, shortsRead);
				}

				/* update the graph */
//				this.controller.updateGraph(shortAudioBuffer);

				/* Re-convert to byte[] */
				ByteBuffer.wrap(audioBuffer)
						.order(audioSettings.getByteOrder()).asShortBuffer()
						.put(shortAudioBuffer);

				/* Write the bytes to output */
				lineOut.write(audioBuffer, 0, bytesRead);
				bytesRead = inputStream.read(audioBuffer, 0,
						audioSettings.getBufferLength());
				shortsRead = bytesRead / 2;
			}
		} catch (IOException e1) {
			controller.showErrorDialog("Error reading file");
		}

		/*
		 * This new cycle allows the streaming of the residual echoes of the
		 * effects that works with delay
		 */
		while (!isStreamStopped) {
			/* Passes an empty buffer to the effects */
			Arrays.fill(shortAudioBuffer, (short) 0);

			java.util.List<Effect> effects = model.getEffects();
			for (int i = 0; i < effects.size(); i++) {
				effects.get(i).process(shortAudioBuffer,
						audioSettings.getShortBufferLength());
			}

			/* The buffer is empty when the echoes are terminated */
			if (isEmpty(shortAudioBuffer)) {
				break;
			}

			/* update the graph */
//			this.controller.updateGraph(shortAudioBuffer);

			/* Re-convert to byte[] */
			ByteBuffer.wrap(audioBuffer).order(audioSettings.getByteOrder())
					.asShortBuffer().put(shortAudioBuffer);

			lineOut.write(audioBuffer, 0, audioSettings.getBufferLength());
		}
		/* Stops lineOut */
		lineOut.drain();
		lineOut.stop();

		controller.streamStopped();

		/* Closes input line */
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e1) {
				controller.showErrorDialog("File closing error");
			}
		}
	}

	/**
	 * Stop the streaming of the sound.
	 */
	public final void stopStream() {
		this.isStreamStopped = true;
	}

	/**
	 * Attenuate the given buffer according to the attenuation parameter.
	 * 
	 * @param buffer
	 *            the buffer that must be attenuated.
	 */
	private void attenuate(final short[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] *= attenuation.getValue();
		}
	}

	/**
	 * Controls if the buffer given is empty.
	 * 
	 * @param buffer
	 *            the buffer that must be controlled
	 * @return true if the buffer is empty, false if it isn't empty.
	 */
	private boolean isEmpty(final short[] buffer) {
		for (short s : buffer) {
			if (Short.valueOf(s).compareTo((short) 0) != 0) {
				return false;
			}
		}
		return true;
	}
}
