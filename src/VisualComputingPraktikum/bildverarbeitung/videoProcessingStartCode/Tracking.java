package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 *
 * @author Maike Leonie Huster
 *
 * Searchs for circles in a gray image and gets the x and y value
 *
 */

public class Tracking {


    public static Mat positionCircle (Mat image) {

        int wight = image.width();
        int height = image.height();
        BufferedImage result = Mat2BufferedImage(image);
        Mat gray = new Mat();
        int count = 0;

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, (double) gray.rows() / 16, 100.0, 30.0, 1, 15);



        for (int x = 0; x < circles.cols(); x++) {


            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            //Imgproc.circle(image, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
            // circle outline
            int radius = (int) Math.round(c[2]);
            //Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
            count++;

            for (int i = 0; i <= count; i++) {
                Color colorCircle = new Color(result.getRGB((int) center.x, (int) center.y));
                if (colorCircle.getGreen() > 160 && colorCircle.getRed() == 0 && colorCircle.getBlue() >= 115) {
                    Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Grün");
                } else if (colorCircle.getRed() > 250 && colorCircle.getBlue() ==0 && colorCircle.getGreen() >= 20) {
                    Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Rot");
                } else if (colorCircle.getBlue() >= 155 && colorCircle.getRed() == 255 && colorCircle.getGreen() >= 24) {
                    Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Pink");
                } else if (colorCircle.getBlue() >=50 && colorCircle.getGreen() >= 27 && colorCircle.getRed() >= 45) {
                    Imgproc.circle(image, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                    System.out.println("Schwarz");

                }
            }
        }
        return image;
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
