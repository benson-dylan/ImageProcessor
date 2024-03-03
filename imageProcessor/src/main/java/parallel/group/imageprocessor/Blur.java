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
        int threshHold = 100000;
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
                            if(pixels.getFirst() - image.getRGB(x, currentY - 1) < -threshHold || pixels.getFirst() - image.getRGB(x, currentY - 1) > threshHold )
                                pixels.add(image.getRGB(x, currentY - 1));
                        if (currentY + 1 < height)
                            if(pixels.getFirst() - image.getRGB(x, currentY + 1) < -threshHold || pixels.getFirst() - image.getRGB(x, currentY + 1) > threshHold )
                                pixels.add(image.getRGB(x, currentY + 1));
                        if (x + 1 < width) {
                            if(pixels.getFirst() - image.getRGB(x+1, currentY) < -threshHold || pixels.getFirst() - image.getRGB(x+1, currentY) > threshHold )
                                pixels.add(image.getRGB(x + 1, currentY));
                            if (currentY + 1 < height)
                                if(pixels.getFirst() - image.getRGB(x + 1, currentY + 1) < -threshHold || pixels.getFirst() - image.getRGB(x + 1, currentY + 1) > threshHold )
                                    pixels.add(image.getRGB(x + 1, currentY + 1));
                            if (currentY - 1 > 0)
                                if(pixels.getFirst() - image.getRGB(x + 1, currentY - 1) < -threshHold || pixels.getFirst() - image.getRGB(x + 1, currentY - 1) > threshHold )
                                    pixels.add(image.getRGB(x + 1, currentY - 1));

                        }
                        if (x - 1 > 0) {
                            if(pixels.getFirst() - image.getRGB(x-1, currentY) < -threshHold || pixels.getFirst() - image.getRGB(x-1, currentY ) > threshHold )
                                pixels.add(image.getRGB(x - 1, currentY));
                            if (currentY + 1 < height)
                                if(pixels.getFirst() - image.getRGB(x- 1, currentY + 1) < -threshHold || pixels.getFirst() - image.getRGB(x-1, currentY + 1) > threshHold )
                                    pixels.add(image.getRGB(x - 1, currentY + 1));
                            if (currentY - 1 > 0)
                                if(pixels.getFirst() - image.getRGB(x - 1, currentY - 1) < -threshHold || pixels.getFirst() - image.getRGB(x - 1, currentY - 1) > threshHold )
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

    //Colorize, leave commented
//    private int pixelAvg(ArrayList<Integer> pixels)
//    {
//        int pixelRGBAvg = 0;
//        int count = 0;
//        for(Integer e : pixels)
//        {
//            count++;
//            pixelRGBAvg += e;
//        }
//        pixelRGBAvg /= count;
//        return pixelRGBAvg;
//    }

    private int pixelAvg(ArrayList<Integer> pixels)
    {
        int alphaSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        for (Integer pixel : pixels) {
            int alpha = (pixel >> 24) & 0xFF;
            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;
            alphaSum += alpha;
            redSum += red;
            greenSum += green;
            blueSum += blue;
        }
        int count = pixels.size();
        int alphaAvg = alphaSum / count;
        int redAvg = redSum / count;
        int greenAvg = greenSum / count;
        int blueAvg = blueSum / count;
        return (alphaAvg << 24) | (redAvg << 16) | (greenAvg << 8) | blueAvg;
    }
}
