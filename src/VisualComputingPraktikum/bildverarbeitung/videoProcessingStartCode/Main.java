package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import org.opencv.core.Core;

/**
 * Main class to start the program
 */

/**
 * @author Karsten Lehn
 * @version 21.10.2016
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Load OpenCV libraries and start program
	    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		new VideoProcessing();
	}

}
