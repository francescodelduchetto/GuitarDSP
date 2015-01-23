package model;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class SerialBufferedListener extends Thread {
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	private InputStream input;
	private Queue<Byte> queue = new LinkedList<Byte>();
	private int letti, tolti;

	public SerialBufferedListener(String portName) {
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
	public void run() {
		while (true) {
			int b = -1;
			try {
				b = input.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b == -1) {
				System.out.println("Non ci sono più dati da leggere, esco.");
				continue;
			}
			if (queue.size() % 1000 == 0) {
				System.out.println("Buffer size: " + queue.size() + " (letti: " + letti + ", tolti: " + tolti + ")");
			}
			synchronized (queue) {
				queue.add((byte) b);
			}
			letti++;
		}
	}
	
	public int read() {
		synchronized (queue) {
			if (queue.isEmpty()) {
				return -1;
			} else {
				tolti++;
				return (int) (queue.poll() & 0xFF);
			}
		}
	}

}
