package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.SourceDataLine;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import controller.Controller;

import effects.Effect;

public class Mixer {

	private Model model;

	private InputParameter<Double> attenuation;

	private SourceDataLine lineOut;
	
	private Collection<Effect> toggledEffects = new HashSet<>();
	
	private Controller controller;

	public Mixer(final SourceDataLine lineOut,
			final InputParameter<Double> attenuation, final Model model,
			final Controller controller) {
		this.lineOut = lineOut;
		this.attenuation = attenuation;
		this.model = model;
		this.controller = controller;
		
		lineOut.start();
	}
	
	public void audioEvent(short audioSample) {
			byte[] byteAudioBuffer = new byte[2];
			
			audioSample = attenuate(audioSample);
			
			for (Effect effect : model.getEffects()) {
				if (!toggledEffects.contains(effect)) {
					audioSample = effect.process(audioSample);
				}
			}

			byteAudioBuffer[1] = (byte) (audioSample >> 8);
			byteAudioBuffer[0] = (byte) (audioSample & 255);
	
			lineOut.write(byteAudioBuffer, 0, byteAudioBuffer.length);

			/* Update the graph */
			this.controller.updateGraph(audioSample);
	}
	
//	public void audioEvent(short audioSample) {
//		count++;
//		audioBuffer[count] = audioSample;
//		if (count == AudioSettings.getAudioSettings()
//		  			.getShortBufferLength()-1) {
//			count = 0;
//
//			this.attenuate(audioBuffer);
//	
//			for (Effect effect : model.getEffects()) {
//				effect.process(audioBuffer, audioBuffer.length);
//			}
//	
//			byte[] byteAudioBuffer = new byte[AudioSettings.getAudioSettings()
//			                       			.getBufferLength()];
//	
//			ByteBuffer.wrap(byteAudioBuffer)
//					.order(AudioSettings.getAudioSettings().getByteOrder())
//					.asShortBuffer().put(audioBuffer);
//	
//			lineOut.write(byteAudioBuffer, 0, byteAudioBuffer.length);
//		}
//	}
	
	public void buttonPressed(int button) {
		java.util.List<Effect> effects = model.getEffects();
		int nEffects = effects.size();
		if (button < nEffects && button >= 0) {
			this.toggleEffect(effects.get(button), button);
		}
	}
	
	public void toggleEffect(Effect effect, int index) {
		if (toggledEffects.contains(effect)) {
			toggledEffects.remove(effect);
			controller.activateEffect(index);
		} else {
			toggledEffects.add(effect);
			controller.deactivateEffect(index);
		}
	}

	public void stopMixer() {
		lineOut.drain();
		lineOut.stop();
	}

	private short attenuate(short sample) {
		return (short) (sample * attenuation.getValue());
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
