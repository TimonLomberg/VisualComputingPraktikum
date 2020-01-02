package VisualComputingPraktikum;

// Test

import VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode.VideoProcessing;
import VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP.ShapesMainWindowPP;
import org.opencv.core.Core;

public class MainApp {

    public VideoProcessing videoProcessing;
    public ShapesMainWindowPP shapesRenderer;



    public MainApp () {

        // Load OpenCV libraries and start program
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        videoProcessing = new VideoProcessing();
        videoProcessing.setMainApp(this);


        shapesRenderer = new ShapesMainWindowPP(this);
        shapesRenderer.setMainApp(this);
    }

    public static void main(String args[]) {


        new MainApp();
    }
}
