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

    int option_height;
    int option_width;
    Size boardSize;
    MatOfPoint2f corners;


    /*
    static int[] testInt = {0};
    public static void main(String[] args) {    }
     */

    public CameraCalibrator() {
        option_height = 8;
        option_width = 8;
        boardSize = new Size(option_height, option_width);
    }
    public CameraCalibrator(int option_sizeX, int option_sizeY) {
        this.option_height = option_sizeX;
        this.option_width = option_sizeY;
        boardSize = new Size(option_sizeX, option_sizeY);
    }
    public CameraCalibrator(Size boardSize) {
        this.boardSize = boardSize;
        option_height = (int) boardSize.height;
        option_width = (int) boardSize.width;
    }

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
    private boolean findCornersChessboard(Mat image, int sizeX, int sizeY) {
        return Calib3d.findChessboardCorners(image, new Size(sizeX,sizeY), corners,
                Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FAST_CHECK | Calib3d.CALIB_CB_NORMALIZE_IMAGE);
    }

    /**
     * Draws Points on all corners of a chessboard in a image
     *
     * @param image The image with the chessboard-pattern
     * @param height The number of corners x-wise
     * @param width The number of corners y-wise
     */
    public void detectAndDrawCorners(Mat image, int height, int width) {
        boolean found = findCornersChessboard(image, height, width);
        if(found)
            Imgproc.cornerSubPix(image, (Mat) corners, new Size(11, 11), new Size(-1,-1),
                    new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 30, 0.1));
        Calib3d.drawChessboardCorners(image, boardSize, corners, found);
    }




}
