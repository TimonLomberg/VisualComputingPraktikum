package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Maike Leonie Huster
 *
 * Searches for shapes in an image (square, triangle,circle)
 * using the method findContours
 *
 */

public class Shape {

    /**
     * Searches for specific shapes
     * @param image
     */

    public static Mat shapeDetection(Mat image) {

        Mat blurred = image.clone();
        Imgproc.medianBlur(image, blurred, 9);
        Mat img = new Mat(blurred.size(), CvType.CV_8U);

        Imgproc.cvtColor(image, img, Imgproc.COLOR_RGB2HSV);
        Core.inRange(image, new Scalar(245,245,245), new Scalar(255,255,255), img);   //abends:new Scalar(250,240,120), new Scalar(255,255,190)     new Scalar(200,60,60), new Scalar(250,180,180)


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

        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);  //Imgproc.RETR_LIST / TREE , Imgproc.CHAIN_APPROX_SIMPLE);


        for (MatOfPoint contour : contours) {

            MatOfPoint2f approx = new MatOfPoint2f();
            MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());
            BufferedImage result = Tracking.Mat2BufferedImage(image);

            Imgproc.approxPolyDP(temp, approx, Imgproc.arcLength(temp, true) * 0.04, true);
            Point center = massCenterMatOfPoint2f(approx);
            int colorCount = 0;


            long count = approx.total();
            if (count == 4) {
                //System.out.println(count);
                //System.out.println("Viereck");
                Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 8);

                for (int i = 0; i <= colorCount; i++) {
                    Color color = new Color(result.getRGB((int) center.x, (int) center.y));
                    if (color.getGreen() >= 110 && color.getRed() >= 50 && color.getBlue() == 255) {
                        System.out.println("Viereck");
                        ImageProcessing.haupt("resources/images/wallOfThorns.jpg");
                    }
                }


            } else if (count == 3) {
                System.out.println(count);
                Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 8);

                for (int i = 0; i <= colorCount; i++) {
                    Color color = new Color(result.getRGB((int) center.x, (int) center.y));
                    if (color.getGreen() >= 110 && color.getRed() >= 50 && color.getBlue() == 255) {
                        System.out.println("Dreieck");
                        ImageProcessing.haupt("resources/images/raiseDead.jpg");
                    }

                }

            } else {
                HoughCirclesRun.HoughCircle(image);
                System.out.println(count);
            }


        }

        return image;
    }

    private static Point massCenterMatOfPoint2f(MatOfPoint2f map) {
        final Moments moments = Imgproc.moments(map);
        final Point center = new Point();
        center.x = moments.get_m10() / moments.get_m00();
        center.y = moments.get_m01() / moments.get_m00();
        return center;
    }
}
