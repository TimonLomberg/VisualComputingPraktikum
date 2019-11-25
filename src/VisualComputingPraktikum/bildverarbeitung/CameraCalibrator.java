package VisualComputingPraktikum.bildverarbeitung;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

enum Modes { CAPTURING, DETECTION, CALIBRATED}

public class CameraCalibrator {

    Modes mode = Modes.CAPTURING;



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




}
