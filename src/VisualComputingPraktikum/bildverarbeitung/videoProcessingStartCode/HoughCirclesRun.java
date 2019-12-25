package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HoughCirclesRun {  //farbe anpassen, größe anpassen


    public static Mat HoughCircle (Mat image) {

        Mat gray = new Mat();
        BufferedImage result = Tracking.Mat2BufferedImage(image);
        int count = 0;

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) gray.rows() / 16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 15); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles


        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(image, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
            count++;

            for (int i = 0; i <= count; i++) {
                Color colorCircle = new Color(result.getRGB((int) center.x, (int) center.y));
                if (colorCircle.getGreen() >= 240 && colorCircle.getRed() >= 80 && colorCircle.getRed() <= 110 && colorCircle.getBlue() == 255) {
                    Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Kreis");
                    ImageProcessing.haupt("resources/images/sleep.jpg");
                }
            }
        }

        return image;


    }
}

