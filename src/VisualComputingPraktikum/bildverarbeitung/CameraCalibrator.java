package VisualComputingPraktikum.bildverarbeitung;

import org.jetbrains.annotations.NotNull;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum Modes { CAPTURING, DETECTION, CALIBRATED}


/**
 * Class for camera calibration and position estimation
 */
public class CameraCalibrator {

    Modes mode = Modes.CAPTURING;

    private static final Size boardSize = new Size(7,7);
    private static final float squareSize = 100f;
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

    /**
     * Calibrates over the given frames of images of a chessboard and returns the Camera-Matrix,
     * distortion-coefficients, rotation-matrix,
     * and translation-vector
     *
     * @param camSize The screen size of the image in pixel
     * @param frames The frames over which to calibrate as matrices
     * @param sampleSize The number of samples used
     * @param boardSize The size which indicates the number of chessboard-corners horizontally and vertically
     * @param squareSize The size of one square of the chessboard
     * @param objectPoints See cv::calibrateCamera documentation
     * @param imagePoints  See cv::calibrateCamera documentation
     * @param cameraMatrix  See cv::calibrateCamera documentation
     * @param distCoeffs  See cv::calibrateCamera documentation
     * @param rvecs  See cv::calibrateCamera documentation
     * @param tvecs  See cv::calibrateCamera documentation
     */
    public static void calibrate(@NotNull Size camSize, @NotNull ArrayList<Mat> frames, int sampleSize,
                                 @NotNull Size boardSize, float squareSize, List<Mat> objectPoints,
                                 List<Mat> imagePoints, Mat cameraMatrix, Mat distCoeffs,
                                 List<Mat> rvecs, List<Mat> tvecs) {


        MatOfPoint3f objectCorners = new MatOfPoint3f();
        MatOfPoint2f imageCorners  = new MatOfPoint2f();
        objectPoints = calcBordCornerPoints(boardSize, squareSize, objectCorners, sampleSize);

        for(int i = 0;i < sampleSize; i++) {
            findCornersChessboard(frames.get(i), (int) boardSize.width, (int) boardSize.height, imageCorners);
            imagePoints.add(i, imageCorners);
        }
        cameraMatrix = Mat.eye(3,3, CvType.CV_64F);
        distCoeffs = Mat.zeros(8,1, CvType.CV_64F);


        if (!(imagePoints.size() == objectPoints.size())) {
            System.err.println("imagePoints.size() and objectPoints.size() and imagePoints[i].size() must be equal " +
                    "to objectPoints[i].size() for each i.");
        }

         Calib3d.calibrateCamera(objectPoints, imagePoints, camSize, cameraMatrix, distCoeffs, rvecs, tvecs);

        /*
        System.out.print("ObjectPoints are: [");
        for(Mat point : objectPoints) {
            System.out.print("--[");
            for (int j=0;j<point.size().width;j++) {
                for (int y=0;y<point.size().height;y++) {
                    System.out.print(Arrays.toString(point.get(j, y)));
                }
            }
            System.out.print("]--");

        }
        System.out.print("]");

        System.out.print("ImagePoints are: [");
        for(Mat point : imagePoints) {
            System.out.print("--[");
            for (int j=0;j<point.size().width;j++) {
                for (int y=0;y<point.size().height;y++) {
                    System.out.print(Arrays.toString(point.get(j, y)));
                }
            }
            System.out.print("]--");

        }
        System.out.print("]");


            System.out.print("CameraMatrix is: [");
            for (int j=0;j<cameraMatrix.size().width;j++) {
                for (int y=0;y<cameraMatrix.size().height;y++) {
                    System.out.print(Arrays.toString(cameraMatrix.get(j, y)));
                }
            }
            System.out.print("]");

        System.out.print("DistCoeffs are: [");
        for (int j=0;j<distCoeffs.size().width;j++) {
            for (int y=0;y<distCoeffs.size().height;y++) {
                System.out.print(Arrays.toString(distCoeffs.get(j, y)));
            }
        }
        System.out.print("]");

        */


    }

    //TODO work here
    private static List<Mat> calcBordCornerPoints(Size boardSize, float squareSize,
                                                  MatOfPoint3f corners, int sampleSize) {
        for(int i=0; i < boardSize.height; i++) {
            for(int j=0;j < boardSize.width; j++) {
                //corners.push_back( new MatOfPoint3f(new Point3(j*squareSize, i*squareSize, 0f)));
                List<Mat> src = Arrays.asList(corners, new MatOfPoint3f(new Point3(j*squareSize,
                        i*squareSize, 0f)));
                Core.hconcat(src, corners);
            }
        }
        ArrayList<Mat> out = new ArrayList<Mat>(sampleSize);

        for(int i=0;i < sampleSize; i++) {
            out.add(corners.clone());
        }

        return out;
    }

    /**
     * Uses cv::solvePnP to estimate rotation-matrix and translation-matrix from reference points and previously
     * estimated camera-matrix and distortion coefficient.
     * Also converts from computervision-coordinate-space to opengl-coordinate-space.
     *
     * See cv::solvePnP for parameters
     */
    public static void pnp(MatOfPoint3f objectPoints, MatOfPoint2f imagePoints, Mat cameraMatrix,
                           MatOfDouble distCoeffs, Mat rvec, Mat tvec) {
        Calib3d.solvePnP(objectPoints,imagePoints,cameraMatrix,distCoeffs,rvec,tvec);

        Mat rotM = Mat.zeros(3, 3, CvType.CV_64F);
        Calib3d.Rodrigues(rvec, rotM);
        rotM.t();
        Core.multiply(rotM.inv(), tvec, tvec);


        System.out.println("Camera position estimated as " + tvec.dump() + " with rotation " + rotM.dump());
    }


    /**
     * Uses cv::solvePnPGeneric to estimate multiple possibilities of rotation-matrix and translation-matrix
     * from reference points and previously estimated camera-matrix and distortion coefficient.
     * Also converts from computervision-coordinate-space to opengl-coordinate-space.
     *
     * See cv::solvePnPGeneric for parameters
     */
    public static void pnpGeneric(Mat objectPoints, Mat imagePoints, Mat cameraMatrix, MatOfDouble distCoeffs,
                                  List<Mat> rvec, List<Mat> tvec) {

        Calib3d.solvePnPGeneric(objectPoints,imagePoints,cameraMatrix,distCoeffs,rvec,tvec);
        Mat rotM = Mat.zeros(3, 3, CvType.CV_64F);
        Calib3d.Rodrigues(rvec.get(0), rotM);
        rotM.t();
        tvec.set(0,rotM.inv().mul(tvec.get(0)));

        //System.out.println("Camera position estimated as " + tvec + " with rotation " + rotM);
    }




}
