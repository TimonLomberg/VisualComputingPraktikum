package VisualComputingPraktikum.bildverarbeitung;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.util.Queue;
import java.util.Vector;

enum Modes { CAPTURING, DETECTION, CALIBRATED}

public class CameraCalibrater {

    Modes mode = Modes.CAPTURING;




    public void method(Queue<Mat> samples) {

/*

        for(int i = 0;;i++) {
            Mat view;
            view = samples.remove();

            if(mode == Modes.CAPTURING && imagePoints.size() >= samples.size()) {
                if( runCalibration(imageSize, cameraMatrix, distCoeffs, imagePoints))
                    mode = Modes.CALIBRATED;
                else
                    mode = Modes.DETECTION;
            }
            if(view.empty())
            {
                if(imagePoints.size() > 0)
                    runCalibration(imageSize, cameraMatrix, distCoeffs, imagePoints);
                break;
            }
            imageSize = view.size();
        }

    }

    public boolean runCalibration(Size imageSize, Mat cameraMatrix, Mat distCoeffs, Vector<Vector<Point>> imagePoints,
                                  Vector<Mat> rvecs, Vector<Mat> tvecs) {
        Calib3d.calibrateCamera();
    }
*/
    }}
