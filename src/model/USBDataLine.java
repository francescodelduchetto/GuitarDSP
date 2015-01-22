package model;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class USBDataLine implements TargetDataLine, SerialPortEventListener {
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	private static final Random deleteme = new Random();

	/**
	 * 
	 */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;

	private AudioFormat audioFormat;
	private boolean isOpen;
	private byte[] buffer;
	private int bufferSize;
	private int bufferHead;
	private int bufferTail;
	private long framePosition;
	private String portName;
	private SerialPort serialPort;

	private ArrayList<LineListener> listeners;

	public USBDataLine(String portName, AudioFormat audioFormat) {
		this.portName = portName;
		this.audioFormat = audioFormat;
		this.framePosition = 0;
		this.bufferHead = 0;
		this.bufferTail = 0;
	}

	@Override
	public int available() {
		if (this.isOpen()) {
			if (bufferTail >= bufferHead) {
				System.out.println(bufferTail - bufferHead);
				return bufferTail - bufferHead;
			} else {
				System.out.println(bufferTail + bufferSize - bufferHead);
				return bufferTail + bufferSize - bufferHead;
			}
		} else {
			System.out.println(0);
			return 0;
		}
	}

	@Override
	public void drain() {
		//
	}

	@Override
	public void flush() {
		//
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public AudioFormat getFormat() {
		return this.audioFormat;
	}

	@Override
	public int getFramePosition() {
		// API says the result will wrap around 2^31
		return (int) (this.framePosition % (1 << 31));
	}

	@Override
	public float getLevel() {
		return 1;
	}

	@Override
	public long getLongFramePosition() {
		return this.framePosition;
	}

	@Override
	public long getMicrosecondPosition() {
		return (long) (this.framePosition * this.audioFormat.getFrameRate() * 1000);
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public void start() {
		//
	}

	@Override
	public void stop() {
		//
	}

	@Override
	public void addLineListener(LineListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void close() {
		//
	}

	@Override
	public Control getControl(Type arg0) {
		return null;
	}

	@Override
	public Control[] getControls() {
		return null;
	}

	@Override
	public Line.Info getLineInfo() {
		return null;
	}

	@Override
	public boolean isControlSupported(Type arg0) {
		return false;
	}

	@Override
	public boolean isOpen() {
		return this.isOpen;
	}

	@Override
	public void removeLineListener(LineListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void open() throws LineUnavailableException {
		this.open(AudioSettings.getAudioSettings().getAudioFormat(),
				AudioSettings.getAudioSettings().getBufferLength()*10);
	}

	@Override
	public void open(AudioFormat format) throws LineUnavailableException {
		this.open(format, AudioSettings.getAudioSettings().getBufferLength()*10);
	}

	@Override
	public void open(AudioFormat format, int bufferSize)
			throws LineUnavailableException {
		this.isOpen = true;
		this.bufferSize = bufferSize;
		this.buffer = new byte[bufferSize];

		CommPortIdentifier portId = null;

		try {
			portId = CommPortIdentifier.getPortIdentifier(this.portName);
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(AudioSettings.getAudioSettings()
					.getBitRate(), SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedInputStream(serialPort.getInputStream());
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public int read(byte[] b, int off, int len) {
		int ret = 0;
		for (int i = 0; i < len; i++) {
			b[off + i] = this.buffer[this.bufferHead++];
			if (this.bufferHead == this.bufferSize) {
				this.bufferHead = 0;
			}
			ret++;
		}
		return ret;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				byte[] bytes = new byte[5];
				input.read(bytes);
				if (this.bufferTail % 32 == 0) {
//					System.out.println("DEBUG bufferTail: " + this.bufferTail);
//					System.out.println("DEBUG bufferHead: " + this.bufferHead);
				}
				System.out.println("asd");
				for (byte b: bytes) {
				System.out.println("# " + b);
					this.buffer[this.bufferTail++] = b;
					if (this.bufferTail == this.bufferSize) {
						this.bufferTail = 0;
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		} else if (event.getEventType() == SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
			// etc...
		}
	}

}
