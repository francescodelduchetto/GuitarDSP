package model;

import effects.Effect;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;

import controller.Controller;

public class AudioDemultiplexer extends Thread {

	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	private int debug = 0;
	
	private Model model;

	private Controller controller;

	private boolean isStreamStopped;

	private InputStream input;
	private int bytes_read;

	public AudioDemultiplexer(String portName, Model model, Controller controller) {
		this.model = model;
		this.controller = controller;
		this.isStreamStopped = false;
		System.out.println("################ OPENING #################");

		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
			// open serial port, and use class name for the appName.
			SerialPort serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the stream
			input = serialPort.getInputStream();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public final void run() {
		int[] b = new int[] {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
		};
		controller.streamStarted();
		while (true) {
			try {
				b[10] = b[9];
				b[9] = b[8];
				b[8] = b[7];
				b[7] = b[6];
				b[6] = b[5];
				b[5] = b[4];
				b[4] = b[3];
				b[3] = b[2];
				b[2] = b[1];
				b[1] = b[0];
				b[0] = input.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b[0] == -1) {
				if (debug == 1) System.out.println("Warning: input.read() == -1; non ci sono dati da leggere.");
				continue;
			}
			bytes_read++;
			if (bytes_read % 1000 == 0) {
				System.out.println("(letti: " + bytes_read + ")");
			}

			// Check sync bytes
			if (b[0] != 255 && b[1] == 255 && b[2] == 255) {
				short zero = 1 << 9;
				
				// Parse shorts from bytes
				short audio = (short) ((b[10] << 8 | b[9]) - zero);
				short touch_x = (short) ((b[8] << 8 | b[7]) - zero);
				short touch_y = (short) ((b[6] << 8 | b[5]) - zero);
				short pressure = (short) ((b[4] << 8 | b[3]) - zero);

				if (debug == 1) {
					System.out.println("audio = " + audio);
					System.out.println("X = " + touch_x);
					System.out.println("Y = " + touch_y);
					System.out.println("pressure = " + pressure);
				}

				/* update mixer */
				this.model.getMixer().audioEvent(audio);

				notifyEffects(touch_x, touch_y, pressure);

				/* Update the graph */
				this.controller.updateGraph(audio);
			}
		}
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