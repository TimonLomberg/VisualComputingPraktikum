package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import aruco.DetectorParameters;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import  org.opencv.core.CvType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Vector;

import static org.opencv.imgproc.Imgproc.contourArea;


/**
 *
 * @author Maike Leonie Huster
 *
 * Searchs for circles in a gray image and gets the x and y value
 *
 */

public class Tracking {


    public static final double RADIUS_OF_MARKER = 2.65;
    public static final double FOCAL_LENGTH = 320.8301965245661; //focal length in pixels
    final int DISTANCE_TO_OBJECT = 109;



    public static Mat positionCircle (Mat image) {

        BufferedImage result = Mat2BufferedImage(image);
        Mat gray = new Mat();
        int count = 0;

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) gray.rows() / 16, 100.0, 30.0, 1, 15);



        for (int x = 0; x < circles.cols(); x++) {


            double[] c = circles.get(0, x);
            Point center = new Point(c[0], c[1]);
            // circle center
            //Imgproc.circle(image, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
            // circle outline
            double radius = c[2]; //Math.round(c[2]);
            //Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
            count++;


            for (int i = 0; i <= count; i++) {
                Color colorCircle = new Color(result.getRGB((int) center.x, (int) center.y));
                if (colorCircle.getGreen() > 160 && colorCircle.getRed() == 0 && colorCircle.getBlue() >= 115) {
                    Imgproc.circle(image, center, (int) radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Grün");

                    double centerZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);

                    MatOfPoint3f objPoints = new MatOfPoint3f();
                    objPoints.push_back( new MatOfPoint3f(new Point3(center.x, center.y, centerZ)));

                } else if (colorCircle.getRed() > 250 && colorCircle.getBlue() ==0 && colorCircle.getGreen() >= 20) {
                    Imgproc.circle(image, center, (int)radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Rot");

                    double centerZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);

                    MatOfPoint3f objPoints = new MatOfPoint3f();
                    objPoints.push_back( new MatOfPoint3f(new Point3(center.x, center.y, centerZ)));

                } else if (colorCircle.getBlue() >= 155 && colorCircle.getRed() == 255 && colorCircle.getGreen() >= 24) {
                    Imgproc.circle(image, center,(int) radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Pink");

                    double centerZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);

                    MatOfPoint3f objPoints = new MatOfPoint3f();
                    objPoints.push_back( new MatOfPoint3f(new Point3(center.x, center.y, centerZ)));

                } else if (colorCircle.getBlue() >=50 && colorCircle.getGreen() >= 27 && colorCircle.getRed() >= 45) {
                    Imgproc.circle(image, center, (int)radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Schwarz");

                    double centerZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);

                    MatOfPoint3f objPoints = new MatOfPoint3f();
                    objPoints.push_back( new MatOfPoint3f(new Point3(center.x, center.y, centerZ)));

                }
            }
        }
        return image;
    }

    public static double distanceMeasure (double RADIUS_OF_MARKER, double FOCAL_LENGTH, double radius){
        double centerZ = ((RADIUS_OF_MARKER *FOCAL_LENGTH) / radius) +5;
        System.out.println("Distance in cm: " + centerZ);

        return  centerZ;
    }


    public static BufferedImage Mat2BufferedImage(Mat imgMat){
        int bufferedImageType = 0;
        switch (imgMat.channels()) {
            case 1:
                bufferedImageType = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                bufferedImageType = BufferedImage.TYPE_3BYTE_BGR;
                break;
            default:
                throw new IllegalArgumentException("Unknown matrix type. Only one byte per pixel (one channel) or three bytes pre pixel (three channels) are allowed.");
        }
        BufferedImage bufferedImage = new BufferedImage(imgMat.cols(), imgMat.rows(), bufferedImageType);
        final byte[] bufferedImageBuffer = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        imgMat.get(0, 0, bufferedImageBuffer);
        return bufferedImage;
    }

    }

