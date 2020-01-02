package VisualComputingPraktikum.bildverarbeitung;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalibratorV2 {


    // the saved chessboard image
    private Mat savedImage;

    public List<Mat> getImagePoints() {
        return imagePoints;
    }

    public List<Mat> getObjectPoints() {
        return objectPoints;
    }

    public Mat getDistCoeffs() {
        return distCoeffs;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    // various variables needed for the calibration
    private List<Mat> imagePoints;
    private List<Mat> objectPoints;
    private MatOfPoint3f obj;
    private MatOfPoint2f imageCorners;
    private int boardsNumber;
    private int numCornersHor;
    private int numCornersVer;
    private int successes;
    private Mat intrinsic;
    private Mat distCoeffs;
    private boolean isCalibrated;



    /**
     * Init all the (global) variables needed in the controller
     */
    public void init()
    {


        this.obj = new MatOfPoint3f();
        this.imageCorners = new MatOfPoint2f();
        this.savedImage = new Mat();

        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.successes = 0;
        this.isCalibrated = false;
    }

    public void updateSettings()
    {
        this.boardsNumber = 10; // set for boardsNumber
        this.numCornersHor = 7; // set for numCornersHor
        this.numCornersVer = 7; // set for numCornersVer
        int numSquares = this.numCornersHor * this.numCornersVer;
        for (int j = 0; j < numSquares; j++)
            obj.push_back(new MatOfPoint3f(new Point3(j / this.numCornersHor, j % this.numCornersVer, 0.0f)));

    }

    public void findAndDrawPoints(Mat frame)
    {
        // init
        Mat grayImage = new Mat();

        // I would perform this operation only before starting the calibration
        // process
        if (this.successes < this.boardsNumber)
        {
            // convert the frame in gray scale
            Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
            // the size of the chessboard
            Size boardSize = new Size(this.numCornersVer, this.numCornersHor);
            // look for the inner chessboard corners
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
            // all the required corners have been found...
            if (found)
            {
                // optimization
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                // save the current frame for further elaborations
                grayImage.copyTo(this.savedImage);
                // show the chessboard inner corners on screen
                Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);


            }

        }
    }


    /**
     * The effective camera calibration, to be performed once in the program
     * execution
     */
    private void calibrateCamera()
    {
        // init needed variables according to OpenCV docs
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        // calibrate!
        Calib3d.calibrateCamera(objectPoints, imagePoints, savedImage.size(), intrinsic, distCoeffs, rvecs, tvecs);
        this.isCalibrated = true;


        //DEBUGGING
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
                    System.out.print(point.get(j, y).toString());
                }
            }
            System.out.print("]--");

        }
        System.out.print("]");

        System.out.print("distCoeffs are: [");

            System.out.print("--[");
            for (int j=0;j<distCoeffs.size().width;j++) {
                for (int y=0;y<distCoeffs.size().height;y++) {
                    System.out.print(Arrays.toString(distCoeffs.get(j, y)));
                }
            }
            System.out.print("]--");


        System.out.print("]");

    }

    /**
     * Take a snapshot to be used for the calibration process
     */
    public void takeSnapshot()
    {
        if (!imageCorners.empty()) {
            if (this.successes < this.boardsNumber)
            {
                // save all the needed values
                this.imagePoints.add(imageCorners);
                imageCorners = new MatOfPoint2f();
                this.objectPoints.add(obj);
                this.successes++;
            }

            // reach the correct number of images needed for the calibration
            if (this.successes == this.boardsNumber)
            {
                this.calibrateCamera();
                this.isCalibrated = true;
            }
            else this.isCalibrated = false;
        }

    }


}
