package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import VisualComputingPraktikum.bildverarbeitung.CameraCalibrator;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Shape {

    public static Mat shapeDetection(Mat image) {

        Mat blurred = image.clone();
        Imgproc.medianBlur(image, blurred, 9);
        Mat img = new Mat(blurred.size(), CvType.CV_8U);

        Imgproc.cvtColor(image, img, Imgproc.COLOR_RGB2HSV);
        Core.inRange(image, new Scalar(100,40,40), new Scalar(240,100,110), img); //100,40,40), new Scalar (240,100,110)  227,6,29  (image, new Scalar(80,100,100), new Scalar(180,255,255), gray)
        //Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGBA2RGB );
        //Core.inRange(image, new Scalar (100,0,0), new Scalar(250,100,250), gray);
        Imgproc.erode(img, img, new Mat());

        Mat thresh = new Mat();

        int thresholdLevel = 1;
        for (int t = 0; t < thresholdLevel; t++) {
            if (t == 0) {
                Imgproc.Canny(img, thresh, 127, 255, 3, true);
                Imgproc.dilate(img, thresh, new Mat(), new Point(-1, -1), 1);
            } else {
                Imgproc.adaptiveThreshold(img, thresh, thresholdLevel,
                        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                        Imgproc.THRESH_BINARY,
                        (image.width() + image.height()) / 200, t);
            }
        }

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);  //Imgproc.RETR_LIST / TREE , Imgproc.CHAIN_APPROX_SIMPLE);


        for (MatOfPoint contour : contours) {

            MatOfPoint2f approx = new MatOfPoint2f();
            MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

            Imgproc.approxPolyDP(temp, approx, Imgproc.arcLength(temp, true) * 0.02, true);

            long count = approx.total();
            if (count == 4) {
                System.out.println(count);
                System.out.println("Viereck");
                Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 8);

                ImageProcessing.haupt("images/wallOfThornes.jpg");

            } else if (count == 3) {
                System.out.println(count);
                System.out.println("Dreieck");
                Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 8);

                ImageProcessing.haupt("images/raiseDead.jpg");


            } else {
                HoughCirclesRun.HoughCircle(image);
                System.out.println(count);

                ImageProcessing.haupt("images/sleep.jpg");
            }

        }

        return thresh;
    }
}
