package model;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import controller.Controller;
import effects.Effect;

public class AudioDemultiplexer extends Thread {

	private int debug = 0;
	
	private static final int BUFFER_SIZE = 10;

	private SerialBufferedListener usbListener;

	private Model model;

	private Controller controller;

	private boolean isStreamStopped;

	public AudioDemultiplexer(SerialBufferedListener usbListener, Model model,
			Controller controller) {
		this.usbListener = usbListener;
		this.model = model;
		this.controller = controller;
		this.isStreamStopped = false;
	}

	@Override
	public final void run() {
		int byteRead = -1, prevByte;

		int[] audio = new int[2];
		int[] touchX = new int[2];
		int[] touchY = new int[2];
		int[] pressure = new int[2];
		
		short shortAudio, shortTouchX, shortTouchY, shortPressure;
		

		controller.streamStarted();

		while (true) {
			if (usbListener != null) {
//				System.out.println("ciao-------------------------------------------------");
				byteRead = usbListener.read();
//				System.out.println("ciao-------------------------------------------------");
				
				if (debug == 2) {
					System.out.println("byte letto prima: " + byteRead);
				}
	
				while (byteRead != -1 && !isStreamStopped) {
					prevByte = byteRead;
					byteRead = usbListener.read();
					if (debug == 2) {
//						System.out.println("byte letto dentro: " + byteRead);
					}
					if (byteRead == 255 && prevByte == 255) {
						audio[0] = usbListener.read();
						audio[1] = usbListener.read();
						touchX[0] = usbListener.read();
						touchX[1] = usbListener.read();
						touchY[0] = usbListener.read();
						touchY[1] = usbListener.read();
						pressure[0] = usbListener.read();
						pressure[1] = usbListener.read();
		
						// for (int i = 0; i < 4; i++) {
						// int mask = (1 << (8 - 2 * i)) - 1;
						// int lshift = 2 * i;
						// int rshift = 8 - 2 * (i + 1);
						// unpack[i] = (short) ((buffer[i] & mask) << lshift +
						// (buffer[i + 1] >> rshift));
						// }
						
						shortAudio = (short) ((short) ((audio[0] << 8) + audio[1]) - Short.MAX_VALUE / 2);
						shortTouchX = (short) ((short) ((touchX[0] << 8) + touchX[1]) - Short.MAX_VALUE / 2);
						shortTouchY = (short) ((short) ((touchY[0] << 8) + touchY[1]) - Short.MAX_VALUE / 2);
						shortPressure = (short) ((short) ((pressure[0] << 8) + pressure[1]) - Short.MAX_VALUE / 2);
		
						if (debug == 1) {
							System.out.println("audio = "
									+ shortAudio);
							System.out.println("X = "
									+ shortTouchX);
							System.out.println("Y = "
									+ shortTouchY);
							System.out.println("pressure = "
									+ shortPressure);
						}
						// System.out.println(((audio[0] << 8) + audio[1]));
		
						/* update mixer */
						this.model.getMixer().audioEvent(
								shortAudio);
		
						notifyEffects(shortTouchX,
								shortTouchY,
								shortPressure);
		
						/* Update the graph */
						this.controller
								.updateGraph(shortAudio);
					}
	
					byteRead = usbListener.read();
					// System.out.println("byte letto dopo: " + byteRead);
				}
			}
		}
//		controller.streamStopped();
	}

	public void stopStream() {
		this.isStreamStopped = true;
	}

	private void notifyEffects(short touchX, short touchY, short pressure) {
		/* Update effects */
		if (touchX != -1)
			for (Effect effect : this.model.getEffects()) {
				effect.touchpadEvent(touchX, touchY, pressure);
			}
	}

}