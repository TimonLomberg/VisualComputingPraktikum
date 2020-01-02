package VisualComputingPraktikum;

// Test

import VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP.ShapesMainWindowPP;

public class MainApp {

    VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode.Main videoProcessing;
    ShapesMainWindowPP shapesRenderer;

    public MainApp () {
        videoProcessing = new VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode.Main();
        shapesRenderer = new ShapesMainWindowPP();
    }

    public static void main(String args[]) {
        new MainApp();
    }
}
