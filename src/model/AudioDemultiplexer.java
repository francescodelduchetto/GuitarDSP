package model;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import controller.Controller;
import effects.Effect;

public class AudioDemultiplexer extends Thread {
	
	private static final int BUFFER_SIZE = 5;

	private AudioInputStream stream;
	
	private Model model;
	
	private Controller controller;
	
	private boolean isStreamStopped;
	
	public AudioDemultiplexer(AudioInputStream stream, Model model, Controller controller) {
		this.stream = stream;
		this.model = model;
		this.controller = controller;
		this.isStreamStopped = false;
	}
	
	@Override
	public final void run() {
		byte[] buffer = new byte[BUFFER_SIZE];
		short[] unpack = new short[4];
		
		int bytesRead = -1;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		controller.streamStarted();
		
		try {
			if (stream != null) {
				if (stream.available() >= buffer.length) 
					bytesRead = stream.read(buffer, 0, buffer.length);
				System.out.println("asd" + bytesRead);
			}
			
//			System.out.println(bytesRead);
			while(bytesRead != -1 && !isStreamStopped) {
				for (int i = 0; i < 4; i++) {
					int mask = (1 << (8 - 2 * i)) - 1;
					int lshift = 2 * i;
					int rshift = 8 - 2 * (i + 1);
					unpack[i] = (short) ((buffer[i] & mask) << lshift + (buffer[i + 1] >> rshift));
				}
				
//				System.out.println("audio = " + unpack[0]);
//				System.out.println("X = " + unpack[1]);
//				System.out.println("Y = " + unpack[2]);
//				System.out.println("pressure = " + unpack[3]);
				
				/* update mixer */
				this.model.getMixer().audioEvent(unpack[0]);
				
				notifyEffects(unpack[1], unpack[2], unpack[3]);
				
				/* Update the graph */
				this.controller.updateGraph(unpack[0]);			
	
				bytesRead = stream.read(buffer, 0, buffer.length);
				System.out.println(bytesRead);
			}
		} catch (IOException exception) {
			controller.showErrorDialog("Error accessing input line");
		}
		
		
		controller.streamStopped();
	}
	
	public void stopStream() {
		this.isStreamStopped = true;
	}
	
	private void notifyEffects(short touchX, short touchY, short pressure) {
		/* Update effects */
		if (touchX != -1) 
			for (Effect effect: this.model.getEffects()) {
				effect.touchpadEvent(touchX, touchY, pressure);
			}
	}

}