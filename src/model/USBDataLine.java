package model;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class USBDataLine implements TargetDataLine{

	private String serialPort;
	private AudioFormat audioFormat;
	
	public USBDataLine(String serialPort, AudioFormat audioFormat) {
		this.serialPort = serialPort;
		this.audioFormat = audioFormat;
	}
	
	@Override
	public int available() {
		return 0;
	}

	@Override
	public void drain() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFramePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLongFramePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMicrosecondPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLineListener(LineListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Control getControl(Type arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.sound.sampled.Line.Info getLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isControlSupported(Type arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void open() throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLineListener(LineListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(AudioFormat arg0) throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(AudioFormat arg0, int arg1)
			throws LineUnavailableException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
