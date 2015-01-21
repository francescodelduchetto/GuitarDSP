package model;

import java.nio.ByteBuffer;

import javax.sound.sampled.SourceDataLine;

import effects.Effect;

public class Mixer {

	private Model model;

	private InputParameter<Double> attenuation;

	private SourceDataLine lineOut;

	public Mixer(final SourceDataLine lineOut,
			final InputParameter<Double> attenuation, final Model model) {
		this.lineOut = lineOut;
		this.attenuation = attenuation;
		this.model = model;

		lineOut.start();
	}

	public void audioEvent(short audioSample) {

		attenuate(audioSample);

		for (Effect effect : model.getEffects()) {
			effect.process(audioSample, audioSample.length);
		}

		byte[] byteAudioBuffer = {};

		ByteBuffer.wrap(byteAudioBuffer)
				.order(AudioSettings.getAudioSettings().getByteOrder())
				.asShortBuffer().put(audioSample);

		lineOut.write(byteAudioBuffer, 0, byteAudioBuffer.length);
	}

	public void stopMixer() {
		lineOut.drain();
		lineOut.stop();
	}

	private void attenuate(final short[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] *= attenuation.getValue();
		}
	}

	// private boolean isEmpty(final byte[] buffer) {
	// for (byte s : buffer) {
	// if (Byte.valueOf(s).compareTo((byte)0) != 0) {
	// return false;
	// }
	// }
	// return true;
	// }
}
