package model;

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
	

	short[] buffer = new short[] {0, 0, 0};

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
		int[] b = new int[] {
			-1, -1, -1, -1, -1, -1
		};
//		int sample = -1;
//		short prev_audio = 512;
		controller.streamStarted();
		while (!isStreamStopped) {
			try {
				b[3] = b[2];
				b[2] = b[1];
				b[1] = b[0];
				b[0] = input.read();
//				System.out.println(b[0]);
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
			if (b[3] == 255 && b[2] == 255 && b[1] != 255) {
				assert b[1] != 255;
				assert b[0] != 255;

				if (debug == 1) {
					System.out.println("b[1] = " + b[1]);
					System.out.println("b[0] = " + b[0]);
				}

				short zero = 1 << 9;
				
				// get button value
				int buttonValue = (b[0] & 0x0F);
				if (buttonValue != 0) {
					this.notifyButtonPressed(buttonValue);
				}

				// Parse shorts from bytes
//				short audio = (short) b[0];
//				audio |= (b[1] & 0x03) << 8;
				
				short audio = (short) (b[0] >> 6);
				audio |= (short) (b[1] << 2);

				if (debug == 2) {
					System.out.println("audio before = " + audio);
				}

				audio -= zero;

				if (debug == 2) {
					System.out.println("audio between = " + audio);
				}

				audio *= 1 << 6;

				if (debug == 2) {
					System.out.println("audio after = " + audio);
				}
				
				audio = (short) ((buffer[0] + buffer[1] + audio) / 3);
				buffer[0] = buffer[1];
				buffer[1] = audio;

				/* update mixer */
				this.model.getMixer().audioEvent(audio);
			}
		}
	}

	public void stopStream() {
		this.isStreamStopped = true;
		this.serialPort.close();
		this.controller.streamStopped();
		System.out.println("################ CLOSED  #################");
	}
	
	private void notifyButtonPressed(int button) {
		System.out.println("Premuto pulsante: " + button);
		this.model.getMixer().buttonPressed(7 - (button - 4)); // valori da 0 a 7
	}

}