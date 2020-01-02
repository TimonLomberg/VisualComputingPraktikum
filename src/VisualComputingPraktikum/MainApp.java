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

        shapesRenderer = new ShapesMainWindowPP();
        shapesRenderer.setMainApp(this);
        shapesRenderer.setVideoProcessing(videoProcessing);
        videoProcessing = new VideoProcessing();
        videoProcessing.setMainApp(this);



    }

    public static void main(String args[]) {


        new MainApp();
    }
}
