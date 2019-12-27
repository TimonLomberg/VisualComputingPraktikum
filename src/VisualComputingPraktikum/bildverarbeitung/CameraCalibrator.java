package VisualComputingPraktikum.bildverarbeitung;

import org.jetbrains.annotations.NotNull;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

enum Modes { CAPTURING, DETECTION, CALIBRATED}

public class CameraCalibrator {

    Modes mode = Modes.CAPTURING;

    private static final Size boardSize = new Size(7,7);
    private static final float squareSize = 30f;
    /*
    static int[] testInt = {0};
    public static void main(String[] args) {    }
     */


    /**
     * Finds chessboard corners in given image
     *
     * Outputs the found corners into "corners"
     *
     * @param image The input image to find the pattern in
     * @param sizeX The size of the chessboard pattern (X)
     * @param sizeY The size of the chessboard pattern (Y)
     * @return If the method found the pattern
     */
    private static boolean findCornersChessboard(Mat image, int sizeX, int sizeY, MatOfPoint2f corners) {

        return Calib3d.findChessboardCorners(image, new Size(sizeX,sizeY), corners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE);
    }

    /**
     * Draws Points on all corners of a chessboard in a image
     *
     * @param image The image with the chessboard-pattern to draw on
     * @param height The number of corners x-wise
     * @param width The number of corners y-wise
     */
    public static Mat detectAndDrawCorners(Mat image,  int width, int height) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        MatOfPoint2f corners = new MatOfPoint2f();
        boolean found = findCornersChessboard(gray, width, height, corners);
        Mat out = image.clone();
        if(found) {
            System.out.println("Chessboard detected!");
            Imgproc.cornerSubPix(gray, (Mat) corners, new Size(11, 11), new Size(-1,-1),
                    new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 30, 0.1));
        } else {
            System.err.println("No chessboard detected!");
        }
        Calib3d.drawChessboardCorners(out, new Size(width, height), corners, found);
        return out;
    }

    //TODO work here
    public static void calibrate(@NotNull Size camSize, @NotNull ArrayList<Mat> frames,@NotNull int sampleSize,@NotNull Size boardSize,@NotNull float squareSize, List<Mat> objectPoints, List<Mat> imagePoints, Mat cameraMatrix, Mat distCoeffs, List<Mat> rvecs, List<Mat> tvecs) {


        MatOfPoint3f objectCorners = new MatOfPoint3f();
        MatOfPoint2f imageCorners  = new MatOfPoint2f();
        objectPoints = calcBordCornerPoints(boardSize, squareSize, objectCorners, sampleSize);

        for(int i = 0;i < sampleSize; i++) {
            findCornersChessboard(frames.get(i), (int) boardSize.width, (int) boardSize.height, imageCorners);
            imagePoints.add(i, imageCorners);
        }
        cameraMatrix = Mat.eye(3,3, CvType.CV_64F);
        distCoeffs = Mat.zeros(8,1, CvType.CV_64F);
        Calib3d.calibrateCamera(objectPoints, imagePoints, camSize, cameraMatrix, distCoeffs, rvecs, tvecs);
    }

    //TODO work here
    private static List<Mat> calcBordCornerPoints(Size boardSize, float squareSize, MatOfPoint3f corners, int sampleSize) {
        for(int i=0; i < boardSize.height; i++) {
            for(int j=0;j < boardSize.width; j++) {
                corners.push_back( new MatOfPoint3f(new Point3(j*squareSize, i*squareSize, 0f)));
            }
        }
        ArrayList<Mat> out = new ArrayList<Mat>(sampleSize);

        for(int i=0;i < sampleSize; i++) {
            out.add(corners.clone());
        }

        return out;
    }
}
