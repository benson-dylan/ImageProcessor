package parallel.group.imageprocessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Blur {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Blur (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage blur() throws Exception {
        System.out.println("Attempting to blur...");
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try {
            Future<?>[] futures = new Future[height];

            for (int y = 0; y < height; y++) {
                final int currentY = y;
                futures[y] = executor.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        ArrayList<Integer> pixels = new ArrayList<Integer>();
                        pixels.add(image.getRGB(x, currentY));
                        if (currentY - 1 > 0)
                            pixels.add(image.getRGB(x, currentY - 1));
                        if (currentY + 1 < height)
                            pixels.add(image.getRGB(x, currentY + 1));
                        if (x + 1 < width) {

                            pixels.add(image.getRGB(x + 1, currentY));
                            if (currentY + 1 < height)
                                pixels.add(image.getRGB(x + 1, currentY + 1));
                            if (currentY - 1 > 0)
                                pixels.add(image.getRGB(x + 1, currentY - 1));

                        }
                        if (x - 1 > 0) {
                            pixels.add(image.getRGB(x - 1, currentY));
                            if (currentY + 1 < height)
                                pixels.add(image.getRGB(x - 1, currentY + 1));
                            if (currentY - 1 > 0)
                                pixels.add(image.getRGB(x - 1, currentY - 1));
                        }
                        int pixelRGBAvg = pixelAvg(pixels);
                        blurredImage.setRGB(x, currentY, pixelRGBAvg);
                    }
                });
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }

        return blurredImage;
    }

    private int pixelAvg(ArrayList<Integer> pixels)
    {
        int pixelRGBAvg = 0;
        for(Integer e : pixels)
        {
            pixelRGBAvg = e;
        }
        pixelRGBAvg /= pixels.size();
        return pixelRGBAvg;
    }
}
