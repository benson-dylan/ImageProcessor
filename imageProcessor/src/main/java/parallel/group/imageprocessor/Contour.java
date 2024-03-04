package parallel.group.imageprocessor;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Contour {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Contour (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage contour() throws Exception
    {
        System.out.println("Attempting to find contours...");

        BufferedImage contouredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        try
        {
            // convert BufferedImage to Mat
            Mat src = new Mat(height, widdth, CvType.CV_8UC3);
            bufferedImageToMat(image, src);

            // convert to grayscale
        }

        catch (Exception error)
        {
            System.err.println("Error finding contours: " + error.getMessage());
            throw error;
        }

        finally
        {

        }

        return contouredImage;
    }
}

private List<MatOfPoint> findContours(Mat submat)
{
    
}

private Mat bufferedImageToMat(BufferedImage image)
{
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

    byteArrayOutputStream.flush();

    byte[] byteArray = byteArrayOutputStream.toByteArray();

    byteArrayOutputStream.close();

    mat.put(0, 0, byteArray);
}

private void matToBufferedImage(Mat mat, BufferedImage iamge)
{
    int width = mat.width(), height = mat.height(), channels = mat.channels();

    byte[] sourcePixels = new byte[width * height * channels];

    mat.get(0, 0, sourcePixels);

    bufferedImage.getRaster().setDataElements(0, 0, width, height, sourcePixels);
}