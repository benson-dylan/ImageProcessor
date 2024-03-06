//package parallel.group.imageprocessor;
//
//import java.awt.image.BufferedImage;
//import java.nio.Buffer;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.Scalar;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.highgui.HighGui;
//import org.opencv.imgproc.Imgproc;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class Contour {
//    private BufferedImage image;
//    private final int NUM_THREADS = 8;
//
//    public Contour (BufferedImage image)
//    {
//        this.image = image;
//    }
//
//    public BufferedImage contour() throws Exception
//    {
//        System.out.println("Attempting to find contours...");
//
//        BufferedImage contouredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//
//        try
//        {
//            // convert BufferedImage to Mat
//            Mat src = new Mat(height, widdth, CvType.CV_8UC3);
//            bufferedImageToMat(image, src);
//
//            // convert to grayscale
//            Mat gray = new Mat();
//            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
//
//            // thresholding
//            Imgproc.threshold(gray, gray, 127, 255, Imgproc.THRESH_BINARY);
//
//            // find contours using parallel processing
//            ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
//            List<Future<List<MatOfPoint>>> tasks = new ArrayList<>();
//
//            int rowsPerTask = gray.rows() / NUM_THREADS;
//
//            for (int i = 0; i < NUM_THREADS; ++i)
//            {
//                int startRow = i * rowsPerTask;
//                int endRow = Math.min(startRow + rowsPerTask, gray.rows());
//                Mat subMat = gray.submat(startRow, endRow);
//                tasks.add(executor.submit(() -> findContoursInRegion(subMat)));
//            }
//
//            List<MatOfPoint> allContours = new ArrayList<>();
//
//            for (Future<List<MatOfPoint>> task : tasks)
//            {
//                allContours.addAll(task.get());
//            }
//
//            // draw contours on the contouredImage
//            for (MatOfPoint contour : allContours)
//            {
//                Imgproc.drawContours(src, Collections.singletonList(contour), -1, new Scalar(0, 255, 0), 2);
//            }
//
//            // convert Mat back to BufferedImage
//            matToBufferedImage(src, contouredImage);
//        }
//
//        catch (Exception error)
//        {
//            System.err.println("Error finding contours: " + error.getMessage());
//            throw error;
//        }
//
//        finally
//        {
//            // release resources
//            src.release();
//            gray.release();
//        }
//
//        return contouredImage;
//    }
//}
//
//private List<MatOfPoint> findContoursInRegion(Mat submat)
//{
//    List<MatOfPoint> contours = new ArrayList<>();
//
//    Imgproc.findContours(subMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//    return contours;
//}
//
//private Mat bufferedImageToMat(BufferedImage image)
//{
//    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
//
//    byteArrayOutputStream.flush();
//
//    byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//    byteArrayOutputStream.close();
//
//    mat.put(0, 0, byteArray);
//}
//
//private void matToBufferedImage(Mat mat, BufferedImage iamge)
//{
//    int width = mat.width(), height = mat.height(), channels = mat.channels();
//
//    byte[] sourcePixels = new byte[width * height * channels];
//
//    mat.get(0, 0, sourcePixels);
//
//    bufferedImage.getRaster().setDataElements(0, 0, width, height, sourcePixels);
//}