package VisualComputingPraktikum.bildverarbeitung.videoProcessingStartCode;

import VisualComputingPraktikum.bildverarbeitung.CalibratorV2;
import VisualComputingPraktikum.bildverarbeitung.CameraCalibrator;
import VisualComputingPraktikum.computergrafik.joglStartCodePP.shapesPP.ShapesMainWindowPP;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import VisualComputingPraktikum.bildverarbeitung.HoughCirclesRun;

/**
 * Simple application, that opens a video stream
 * - either from the web cam or a video file -
 * grabs frames, 
 * performs frame-wise image processing using OpenCV,
 * displays the video stream and the processed stream 
 * and saves processed frames to an image file.
 * 
 * @author Karsten Lehn modified by Merijam Gotzes
 * @version 21.10.2016, 01.10.2019
 * 
 */
public class VideoProcessing extends JFrame {
	
    private BufferedImagePanel imgPanel1;
    private BufferedImagePanel imgPanel2;
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));


    private static final int calibSampleSize = 10;
    private static final Size boardSize = new Size(7,7);
	private static final float squareSize = 10f;
	Mat rvecs;
	Mat tvecs;

	CalibratorV2 calibratorer;


    int samplesCollected;
    ArrayList<Mat> collectedFrames;
	
	/**
	 * Create object and perform the processing by calling private member functions.
	 */
	
	public VideoProcessing() {
		imgPanel1 = null;
		imgPanel2 = null;
		samplesCollected = 0;
		collectedFrames = new ArrayList<Mat>(calibSampleSize);

		calibratorer = new CalibratorV2();
		calibratorer.init();
		calibratorer.updateSettings();

		new ShapesMainWindowPP();

		createFrame();
		processShowVideo();

	}

	
	/**
	 * Returns the path and file of the video to be processed.
	 * @return path and file name in one string
	 */
	private String getFilePathName() {
		// Begin: Get file path and name from "getRessource"
		// File name determination using getResource (seems to be buggy)
/*		String filePathName = getClass().getResource("./Landscape.avi").getPath();
		filePathName = filePathName.substring(1);  // remove the bug
*/		// End: Get file path and name from "getRessource"

		
		// Begin: Set relative path and file name 
//		String filePathName = "videos\\Landscape.avi";
		// End: Set relative path and file name 
		
		// Choose file path and file name via a file selector box
        int returnVal = fileChooser.showOpenDialog(this); 
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;  // cancelled
        }
        File selectedFile = fileChooser.getSelectedFile();
        String filePathName = selectedFile.getPath();		
		// End: Choose file path and file name via a file selector box
	    
	    System.out.println("Video file name: " + filePathName);
		return filePathName;
	}
	
	/**
	 * @param imgMat image matrix to be written to a file
	 * @param filename name of the file to be created
	 */
	private void writeImage(Mat imgMat, String filename) {
		String filePathName = "videos/" + filename;
	    Imgcodecs.imwrite(filePathName, imgMat, 
	    		new MatOfInt(Imgcodecs.IMWRITE_PNG_STRATEGY_HUFFMAN_ONLY,
	    					 Imgcodecs.IMWRITE_PNG_STRATEGY_FIXED));
	}
	
	
	/**
	 * Create the JFrame to be displayed, displaying two images.
	 */
	private void createFrame() {

		setTitle("Original and processed video stream");
		
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new FlowLayout());
		
		imgPanel1 = new BufferedImagePanel();
		contentPane.add(imgPanel1);
		imgPanel2 = new BufferedImagePanel();
		contentPane.add(imgPanel2);
				
	       // place the frame at the center of the screen and show
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2 - getWidth()/2, dim.height/2 - getHeight()/2);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Reades a video stream from a file or camera, displays the original frames,
	 * processes the frames and displays the result.
	 */
	private void processShowVideo() {	    

		// BEGIN: Prepare streaming from internal web cam
    	VideoCapture cap = new VideoCapture(0);
		// END: Prepare streaming from internal web cam		

	   // BEGIN: Prepare streaming from video file
       //String filePathName = getFilePathName();
	   //VideoCapture cap = new VideoCapture(filePathName);
 	   // END: Prepare streaming from video file

       Mat frame = new Mat();
	   // Check of file or camera can be opened
	   if(!cap.isOpened()) 
		   throw new CvException("The Video File or the Camera could not be opened!");
        
	   Mat processedImage = new Mat();  
	   cap.read(frame);
	   System.out.println("  First grabbed frame: " + frame);
	   System.out.println("  Matrix columns: " + frame.cols());
	   System.out.println("  Matrix rows: " + frame.rows());
	   System.out.println("  Matrix channels: " + frame.channels());

	   int i = 1;
	   System.out.print("Frame count: (" + i + ")"); 
	   // loop for grabbing frames
	   while (cap.read(frame)) { 	   
		   i++;
    	   if ((i % 100) == 0)
    		   System.out.println(".(" + i + ")");
    	   else
    		   System.out.print("."); 

    	   // display original frame from the video stream
    	   imgPanel1.setImage(Mat2BufferedImage(frame));
	    
    	   // convert the frame to a grayscale image
		   Mat grayImg = new Mat();

		   //processedImage = Shape.shapeDetection(frame);
		   //processedImage = HoughCirclesRun.HoughCircle(frame);
		   processedImage = Tracking.positionCircle(frame);
		   //processedImage = CameraCalibrator.detectAndDrawCorners(frame, 7,7);


			/*if(collectFrames(calibSampleSize, frame, collectedFrames)) {

				boolean success = false;

				List<Mat> objectPoints = new ArrayList<Mat>();
				List<Mat> imagePoints = new ArrayList<Mat>();
				Mat cameraMatrix = new Mat();
				Mat distCoeffs = new Mat();
				List<Mat> rvecs = new ArrayList<Mat>();
				List<Mat> tvecs = new ArrayList<Mat>();
				try {
					CameraCalibrator.calibrate(processedImage.size(), collectedFrames, calibSampleSize, boardSize, squareSize, objectPoints, imagePoints, cameraMatrix, distCoeffs, rvecs, tvecs);
					success = true;
				} catch (Exception e) {
					System.err.println("[Warning]:  Calibration unseccessfull");
					success = false;

				}

				if(success) {*/

					/*
					ArrayList<MatOfPoint3f> objPoints = new ArrayList<MatOfPoint3f>();
					ArrayList<MatOfPoint2f> imgPoints = new ArrayList<MatOfPoint2f>();



					for (int y = 0; y < objectPoints.size();y++) {
						if (objectPoints.get(y) != null) {
							Converters.Mat_to_vector_vector_Point3f(objectPoints.get(y), objPoints);
							Converters.Mat_to_vector_vector_Point2f(imagePoints.get(y), imgPoints);
							if (objPoints.size()!=0) {
								CameraCalibrator.pnp(objPoints.get(y), imgPoints.get(y),cameraMatrix,new MatOfDouble(distCoeffs),rvecs.get(0),tvecs.get(0));
								break;
							}


						}

					}


					 */

					//for (int j = 0; j<objectPoints.get(0))

						/*System.out.println(objectPoints.toString());

					//CameraCalibrator.pnpGeneric(Converters.vector_Mat_to_Mat(objectPoints), Converters.vector_Mat_to_Mat(imagePoints),cameraMatrix,new MatOfDouble(distCoeffs),rvecs,tvecs);

				}


				collectedFrames.clear();
				System.out.println("" + cameraMatrix + " end matrix");
			}*/



		   calibratorer.findAndDrawPoints(frame);
		   calibratorer.takeSnapshot();

		   if(calibratorer.isCalibrated()) {

			   MatOfPoint3f tmpObj = new MatOfPoint3f();
			   for (Mat object : calibratorer.getObjectPoints()) {
			   		ArrayList<Point3> tmp2 = new ArrayList<>();
				   Converters.Mat_to_vector_Point3f(object, tmp2);
				   tmpObj.push_back(Converters.vector_Point3f_to_Mat(tmp2));
			   }
			   MatOfPoint2f tmpImg = new MatOfPoint2f();
			   for (Mat object : calibratorer.getImagePoints()) {
				   ArrayList<Point> tmp2 = new ArrayList<>();
				   Converters.Mat_to_vector_Point2f(object, tmp2);
				   tmpObj.push_back(Converters.vector_Point2f_to_Mat(tmp2));
			   }


				rvecs = new Mat();
			    tvecs = new Mat();

			   CameraCalibrator.pnp(tmpObj, tmpImg, calibratorer.getIntrinsic(),(MatOfDouble) calibratorer.getDistCoeffs(), rvecs, tvecs);
		   }






           // Show processed image
    	   imgPanel2.setImage(Mat2BufferedImage(processedImage));
    	   pack();
        
    	   // Write unprocessed and processed frame successively to files
    	   writeImage(frame, "unprocessedImage.png");
    	   writeImage(processedImage, "processedImage.png");
       } // END for loop
	   System.out.println(".(" + i + ")");
	   cap.release();
	}
    
	/**
	 * Converts an OpenCV matrix into a BufferedImage.
	 * 
	 * Inspired by 
 	 * http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
 	 * Fastest code
	 * 
	 * @param "OpenCV" Matrix to be converted must be a one channel (grayscale) or
	 * three channel (BGR) matrix, i.e. one or three byte(s) per pixel.
	 * @return converted image as BufferedImage
	 * 
	 */
    public BufferedImage Mat2BufferedImage(Mat imgMat){
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

    private boolean collectFrames(int sampleSize, Mat frame, List<Mat> imageList) {
    	if(! (samplesCollected >= sampleSize)) {
    		imageList.add(frame);
			samplesCollected++;
    		System.out.println("Collected " + imageList.size() + " of "+ sampleSize +" frames. ");
    		return false;
		} else {
    		samplesCollected = 0;
    		return true;
		}
	}


}
