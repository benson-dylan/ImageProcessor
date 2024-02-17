package parallel.group.imageprocessor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Zoom {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Zoom (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage zoom(BufferedImage image, double scaleFactor) throws Exception
    {
        System.out.println("Attempting to zoom...");
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        int newWidth = (int) (originalWidth * scaleFactor);
        int newHeight = (int) (originalHeight * scaleFactor);

        BufferedImage zoomedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try
        {
            Future<?>[] futures = new Future[newHeight];

            for (int y = 0; y < newHeight; y++)
            {
                final int currentY = y;
                futures[y] = executor.submit(() -> {
                    for (int x = 0; x < newWidth; x++)
                    {
                        int originalX = (int) (x * (double) originalWidth / newWidth);
                        int originalY = (int) (currentY * (double) originalHeight / newHeight);
                        int pixel = image.getRGB(originalX, originalY);
                        zoomedImage.setRGB(x, currentY, pixel);
                    }
                });
            }

            for (Future<?> future : futures)
            {
                future.get();
            }
        }
        finally
        {
            executor.shutdown();
        }

        return zoomedImage;
    }

    public void printDimensions()
    {
        System.out.println("Height: " + image.getHeight());
        System.out.println("Width: " + image.getWidth());
    }
}
