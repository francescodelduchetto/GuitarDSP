package model;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import controller.Controller;

public class Mixer extends Thread {

	private Controller controller;
	
	private Model model;
	
	private InputParameter<Double> attenuation;
	
	private SourceDataLine lineOut;
	
	private AudioInputStream lineIn;
	
	private boolean isStreamStopped;
	
	public Mixer(final SourceDataLine lineOut, final AudioInputStream lineIn,
			final InputParameter<Double> attenuation,
			final Controller controller, final Model model) {
		this.lineOut = lineOut;
		this.lineIn = lineIn;
		this.attenuation = attenuation;
		this.controller = controller;
		this.model = model;
	}
	
	public final void run() {
		this.isStreamStopped = false;
		this.controller.streamStarted();
		
		lineOut.start();

		byte[] audioBuffer = new byte[AudioSettings.getAudioSettings().getBufferLength()];
		
		int bytesRead = -1;
		
		try {
			if (lineIn != null)
				bytesRead = lineIn.read(audioBuffer, 0, audioBuffer.length);
			
			while (bytesRead != -1 && !isStreamStopped) {
				/* Apply attenuation */
				this.attenuate(audioBuffer);
				
				/* Apply effects */
				
				/* Update the graph */
				this.controller.updateGraph(audioBuffer);
				
				/* Write output */
				lineOut.write(audioBuffer, 0, audioBuffer.length);
			}
		} catch (IOException exception) {
			controller.showErrorDialog("Error accessing input line");
		}
		
		lineOut.drain();
		lineOut.stop();
		
		controller.streamStopped();
		
		/* Closes input line */
		if (lineIn != null) {
			try {
				lineIn.close();
			} catch (IOException e1) {
				controller.showErrorDialog("File closing error");
			}
		}
	}
	
	public void stopStream() {
		this.isStreamStopped = true;
	}
	
	private void attenuate(final byte[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] *= attenuation.getValue();
		}
	}
	
	private boolean isEmpty(final byte[] buffer) {
		for (byte s : buffer) {
			if (Byte.valueOf(s).compareTo((byte)0) != 0) {
				return false;
			}
		}
		return true;
	}
	

}
