package parallel.group.imageprocessor;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Resize {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Resize(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage resize(BufferedImage image, int newWidth, int newHeight) throws Exception {
        System.out.println("Attempting to resize...");

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try {
            Future<?>[] futures = new Future[newHeight];

            for (int y = 0; y < newHeight; y++) {
                final int currentY = y;
                futures[y] = executor.submit(() -> {
                    for (int x = 0; x < newWidth; x++) {
                        int originalX = (int) (x * (double) image.getWidth() / newWidth);
                        int originalY = (int) (currentY * (double) image.getHeight() / newHeight);
                        int pixel = image.getRGB(originalX, originalY);
                        resizedImage.setRGB(x, currentY, pixel);
                    }
                });
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }

        return resizedImage;
    }

    public void printDimensions() {
        System.out.println("Height: " + image.getHeight());
        System.out.println("Width: " + image.getWidth());
    }
}