package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP.ShapesMainWindowPP;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;


/**
 *
 * @author Maike Leonie Huster
 *
 * Searchs for circles in a gray image and gets the x,y and z value
 *
 */

public class Tracking {


    public static final double RADIUS_OF_MARKER = 2.65;
    public static final double FOCAL_LENGTH = 320.8301965245661; //focal length in pixels
    final int DISTANCE_TO_OBJECT = 109;

    public static double blackCenterX;
    public static double blackCenterY;
    public static double blackCenterZ;

    public static double pinkCenterX;
    public static double pinkCenterY;
    public static double pinkCenterZ;

    public static double redCenterX;
    public static double redCenterY;
    public static double redCenterZ;

    public static double greenCenterX;
    public static double greenCenterY;
    public static double greenCenterZ;



    /**
     *
     * get the x,y,z value for a specific colored circle
     *
     * @param image
     *
     */


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
            double radius = c[2];
            count++;


            for (int i = 0; i <= count; i++) {
                Color colorCircle = new Color(result.getRGB((int) center.x, (int) center.y));
                if (colorCircle.getGreen() > 160 && colorCircle.getRed() == 0 && colorCircle.getBlue() >= 115) {
                    Imgproc.circle(image, center, (int) radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Grün");

                    greenCenterX = center.x;
                    greenCenterY = center.y;
                    greenCenterZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);

                } else if (colorCircle.getRed() > 250 && colorCircle.getBlue() ==0 && colorCircle.getGreen() >= 20) {
                    Imgproc.circle(image, center, (int)radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Rot");

                    redCenterX = center.x;
                    redCenterY = center.y;
                    redCenterZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);


                } else if (colorCircle.getBlue() >= 155 && colorCircle.getRed() == 255 && colorCircle.getGreen() >= 24) {
                    Imgproc.circle(image, center,(int) radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Pink");

                    pinkCenterX = center.x;
                    pinkCenterY = center.y;
                    pinkCenterZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);



                } else if (colorCircle.getBlue() >=50 && colorCircle.getGreen() >= 27 && colorCircle.getRed() >= 45) {
                    Imgproc.circle(image, center, (int)radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Schwarz");



                    blackCenterX = center.x;
                    blackCenterY = center.y;
                    blackCenterZ = distanceMeasure(RADIUS_OF_MARKER, FOCAL_LENGTH, radius);



                }
            }
        }
        return image;
    }

    /**
     *
     * measures the distance between the center of the circle and the camera
     *
     * @param RADIUS_OF_MARKER
     * @param FOCAL_LENGTH
     * @param radius
     *
     */

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

