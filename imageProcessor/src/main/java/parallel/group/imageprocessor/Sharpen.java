package parallel.group.imageprocessor;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Sharpen {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Sharpen (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage sharpenImage(double n)
    {
        System.out.println("Attempting to sharpen...");
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Apply sharpening
        double[][] kernel = {
                {0, -1, 0},
                {-1, n, -1},
                {0, -1, 0}
        };

        try {
            Future<?>[] futures = new Future[height];

            for (int y = 0; y < height; y++) {
                final int Y = y;
                futures[y] = executor.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        int sumR = 0, sumG = 0, sumB = 0;

                        for (int ky = -1; ky <= 1; ky++) {
                            for (int kx = -1; kx <= 1; kx++) {
                                int pixelX = Math.min(Math.max(x + kx, 0), width - 1);
                                int pixelY = Math.min(Math.max(Y + ky, 0), height - 1);
                                Color pixel = new Color(image.getRGB(pixelX, pixelY));
                                double kernelValue = kernel[ky + 1][kx + 1];
                                sumR += (int) (pixel.getRed() * kernelValue);
                                sumG += (int) (pixel.getGreen() * kernelValue);
                                sumB += (int) (pixel.getBlue() * kernelValue);
                            }
                        }

                        sumR = Math.min(Math.max(sumR, 0), 255);
                        sumG = Math.min(Math.max(sumG, 0), 255);
                        sumB = Math.min(Math.max(sumB, 0), 255);

                        int sharpenedPixel = new Color(sumR, sumG, sumB).getRGB();
                        sharpenedImage.setRGB(x, Y, sharpenedPixel);
                    }
                });
            }

            for (Future<?> future : futures) {
                future.get();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return sharpenedImage;
    }

}
