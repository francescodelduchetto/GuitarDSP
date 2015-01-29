package model;

import effects.Effect;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;

import controller.Controller;


public class AudioDemultiplexer extends Thread {

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	private int debug = 1;
	
	private Model model;

	private Controller controller;

	private boolean isStreamStopped;
	
	private SerialPort serialPort;

	private InputStream input;
//	private BufferedReader input;
	private int bytes_read;

	public AudioDemultiplexer(String portName, Model model, Controller controller) {
		this.model = model;
		this.controller = controller;
		this.isStreamStopped = false;
		System.out.println("################ OPENING #################");

		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(2000000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the stream
			input = serialPort.getInputStream();
//			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public final void run() {
//		short[] window = new short[5];
		
		int[] b = new int[] {
			-1, -1, -1, -1, -1
		};
//		int sample = -1;
//		short prev_audio = 512;
		controller.streamStarted();
		while (!isStreamStopped) {
			try {
				b[4] = b[3];
				b[3] = b[2];
				b[2] = b[1];
				b[1] = b[0];
				b[0] = ( input.read());
//				sample = Integer.parseInt( input.readLine());
			} catch (Exception e) {
//				e.printStackTrace();
				continue;
			}
//			if (b[0] == -1) {
//				if (debug == 1) System.out.println("Warning: input.read() == -1; non ci sono dati da leggere.");
//				continue;
//			}
//			bytes_read++;
//			if (bytes_read % 1000 == 0) {
//				System.out.println("(letti: " + bytes_read + ")");
//			}

			// Check sync bytes
			if (b[0] != 255 && b[1] == 255 && b[2] == 255) {
				assert b[3] != 255;
				assert b[4] != 255;
//				
//				if (debug == 1) {
//					System.out.println("b[3] = " + b[3]);
//					System.out.println("b[4] = " + b[4]);
//				}
				
				short zero = 1 << 9;

				// Parse shorts from bytes
				short audio = (short) (b[4] << 8);
//				short audio = (short)sample;
				audio |= b[3];
				
				if (debug == 1) {
					System.out.println("audio before = " + audio);
				}
//				if (Math.abs(audio - prev_audio) > 40) {
//					audio = prev_audio;
//				}
//				prev_audio = audio;
//				
				audio -= zero;
				
				if (debug == 1) {
//					System.out.println("audio between = " + audio);
				}

				audio *= 1 << 6;

//				window[4] = window[3];
//				window[3] = window[2];
//				window[2] = window[1];
//				window[1] = window[0];
//				window[0] = audio;
//				
//				int sum = 0;
//				for (int x: window){
//					sum += x;
//				}
//				audio = (short) (sum / window.length);

				if (debug == 1) {
					System.out.println("audio after = " + audio);
				}

				/* update mixer */
				this.model.getMixer().audioEvent(audio);

				/* Update the graph */
				this.controller.updateGraph(audio);
			}
		}
	}

	public void stopStream() {
		this.isStreamStopped = true;
		this.serialPort.close();
		this.controller.streamStopped();
	}

	private void notifyEffects(short touchX, short touchY, short pressure) {
		/* Update effects */
		if (touchX != -1)
			for (Effect effect : this.model.getEffects()) {
				effect.touchpadEvent(touchX, touchY, pressure);
			}
	}

}