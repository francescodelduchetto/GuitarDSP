package main;

import controller.Controller;

/**
 * Main class of the GuitarDSP application.
 * 
 * @author Francesco Del Duchetto
 */
public class MainClass {
	
	/**
	 * It starts the application creating a {@link Controller}.
	 * @param argv
	 */
	public static void main(String[] argv) {
		new Controller();
	}
}
