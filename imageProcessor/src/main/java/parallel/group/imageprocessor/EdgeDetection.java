package parallel.group.imageprocessor;
import java.awt.image.Kernel;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.nio.Buffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EdgeDetection {

    private BufferedImage image;
    private final int NUM_THREADS = 8;
    public EdgeDetection (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage detectEdges()
    {
        BufferedImage grayImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayImage.getGraphics().drawImage(this.image, 0, 0, null);

        int rowsPerRegion = Math.max(1, image.getHeight() / NUM_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        try
        {
            BufferedImage blurredImage = parallelApplyBlur(grayImage, executor, rowsPerRegion);
            BufferedImage gradientImage = computeGradient(blurredImage);
            BufferedImage suppressedImage = parallelNonMaximumSuppression(gradientImage, executor, rowsPerRegion);
            BufferedImage thresholdImage = parallelDoubleThreshold(suppressedImage, executor, rowsPerRegion);
            BufferedImage edges = parallelEdgeTracking(thresholdImage, executor, rowsPerRegion);
            return thresholdImage;
        }
        finally
        {
            executor.shutdown();
            try
            {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }


    }

    private static BufferedImage parallelApplyBlur(BufferedImage image, ExecutorService executor, int rowsPerRegion)
    {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            for (int startY = 0; startY < image.getHeight(); startY += rowsPerRegion) {
                final int regionStartY = startY;
                final int regionEndY = Math.min(regionStartY + rowsPerRegion, image.getHeight());
                executor.submit(() -> {
                    applyBlur(image, result, regionStartY, regionEndY);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void applyBlur(BufferedImage source, BufferedImage destination, int startY, int endY)
    {
        float[] blurKernel = {
                1/16f, 2/16f, 1/16f,
                2/16f, 4/16f, 2/16f,
                1/16f, 2/16f, 1/16f
        };

        Kernel kernel = new Kernel(3, 3, blurKernel);
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                destination.setRGB(x, y, rgb); // Copy pixel value initially
            }
        }
        convolveOp.filter(destination.getSubimage(0, startY, destination.getWidth(), endY - startY),
                destination.getSubimage(0, startY, destination.getWidth(), endY - startY));
    }

//    private BufferedImage parallelComputeGradient(BufferedImage image, ExecutorService executor, int rowsPerRegion) {
//        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
//        try {
//            for (int startY = 0; startY < image.getHeight(); startY += rowsPerRegion) {
//                final int regionStartY = startY;
//                final int regionEndY = Math.min(regionStartY + rowsPerRegion, image.getHeight());
//                executor.submit(() -> {
//                    computeGradient(image, result, regionStartY, regionEndY);
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    private void computeGradient(BufferedImage source, BufferedImage destination, int startY, int endY) {
//        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
//        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
//
//        int width = source.getWidth();
//        int height = source.getHeight();
//
//        for (int y = startY; y < endY; y++) {
//            for (int x = 1; x < width - 1; x++) {
//                int gx = 0, gy = 0;
//
//                for (int i = -1; i <= 1; i++) {
//                    for (int j = -1; j <= 1; j++) {
//                        int pixel = source.getRGB(x + j, y + i);
//                        int gray = getGrayLevel(pixel); // Assuming image type is BufferedImage.TYPE_INT_RGB
//                        gx += sobelX[i + 1][j + 1] * gray;
//                        gy += sobelY[i + 1][j + 1] * gray;
//                    }
//                }
//
//                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
//                //magnitude = Math.min(255, Math.max(0, magnitude)); // Clamp to [0, 255]
//
//                int edgePixel = (magnitude << 16) | (magnitude << 8) | magnitude; // Gray pixel
//                destination.setRGB(x, y, edgePixel);
//            }
//        }
//    }

    private static BufferedImage computeGradient(BufferedImage image)
    {
        int[][] sobelX = {{ -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 }};
        int[][] sobelY = {{ -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 }};

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage gradientImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 1; y < height - 1; y++)
        {
            for (int x = 1; x < width - 1; x++)
            {
                int gx = 0, gy = 0;

                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        gx += (sobelX[i + 1][j + 1] * getGrayLevel(image.getRGB(x + i, y + j)));
                    }
                }

                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        gy += (sobelY[i + 1][j + 1] * getGrayLevel(image.getRGB(x + i, y + j)));
                    }
                }

                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);

                gradientImage.setRGB(x, y, magnitude << 16 | magnitude << 8 | magnitude);
            }
        }

        return gradientImage;
    }

    private BufferedImage parallelNonMaximumSuppression(BufferedImage image, ExecutorService executor, int rowsPerRegion) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            for (int startY = 0; startY < image.getHeight(); startY += rowsPerRegion) {
                final int regionStartY = startY;
                final int regionEndY = Math.min(regionStartY + rowsPerRegion, image.getHeight());
                executor.submit(() -> {
                    nonMaximumSuppression(image, result, regionStartY, regionEndY);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void nonMaximumSuppression(BufferedImage source, BufferedImage destination, int startY, int endY) {
        int width = source.getWidth();
        int height = source.getHeight();

        int padding = (endY - startY) / 2; // Adjust as needed
        int startYWithPadding = Math.max(0, startY - padding);
        int endYWithPadding = Math.min(height - 1, endY + padding);

        for (int y = startYWithPadding; y < endYWithPadding; y++) {
            for (int x = 1; x < width - 1; x++) {
                int pixel = source.getRGB(x, y);
                int magnitude = (pixel >> 16) & 0xFF; // Assuming image type is BufferedImage.TYPE_INT_RGB

                double direction = Math.atan2(
                        ((pixel >> 16) & 0xFF) - ((pixel >> 8) & 0xFF),
                        ((pixel >> 8) & 0xFF) - (pixel & 0xFF)
                );


                if (direction < 0) {
                    direction += 2 * Math.PI;
                }

                int neighbor1, neighbor2;
                if ((direction >= 0 && direction < Math.PI / 8) || (direction >= 15 * Math.PI / 8 && direction < 2 * Math.PI)) {
                    neighbor1 = source.getRGB(x, y - 1);
                    neighbor2 = source.getRGB(x, y + 1);
                } else if (direction >= Math.PI / 8 && direction < 3 * Math.PI / 8) {
                    neighbor1 = source.getRGB(x + 1, y - 1);
                    neighbor2 = source.getRGB(x - 1, y + 1);
                } else if (direction >= 3 * Math.PI / 8 && direction < 5 * Math.PI / 8) {
                    neighbor1 = source.getRGB(x + 1, y);
                    neighbor2 = source.getRGB(x - 1, y);
                } else if (direction >= 5 * Math.PI / 8 && direction < 7 * Math.PI / 8) {
                    neighbor1 = source.getRGB(x - 1, y - 1);
                    neighbor2 = source.getRGB(x + 1, y + 1);
                } else {
                    neighbor1 = source.getRGB(x, y + 1);
                    neighbor2 = source.getRGB(x, y - 1);
                }

                int neighborMagnitude1 = neighbor1 & 0xFF;
                int neighborMagnitude2 = neighbor2 & 0xFF;

                // Perform non-maximum suppression
                if (magnitude >= neighborMagnitude1 && magnitude >= neighborMagnitude2) {
                    destination.setRGB(x, y, magnitude << 16 | magnitude << 8 | magnitude); // Preserve the pixel value
                } else {
                    destination.setRGB(x, y, 0); // Suppress non-maximum pixels
                }
            }
        }
    }

//    private static BufferedImage nonMaximumSuppression(BufferedImage image)
//    {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        BufferedImage suppressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//
//        for (int y = 1; y < height - 1; y++)
//        {
//            for (int x = 1; x < width - 1; x++)
//            {
//                int pixel = image.getRGB(x, y);
//                int magnitude = (pixel >> 16) & 0xFF;
//
//                double direction = Math.atan2(
//                        ((pixel >> 16) & 0xFF) - ((pixel >> 8) & 0xFF),
//                        ((pixel >> 8) & 0xFF) - (pixel & 0xFF)
//                );
//
//                int neighbor1 = 0, neighbor2 = 0;
//                if (direction < Math.PI / 8 || direction >= 7 * Math.PI / 8) {
//                    neighbor1 = image.getRGB(x, y - 1);
//                    neighbor2 = image.getRGB(x, y + 1);
//                } else if (direction >= Math.PI / 8 && direction < 3 * Math.PI / 8) {
//                    neighbor1 = image.getRGB(x - 1, y - 1);
//                    neighbor2 = image.getRGB(x + 1, y + 1);
//                } else if (direction >= 3 * Math.PI / 8 && direction < 5 * Math.PI / 8) {
//                    neighbor1 = image.getRGB(x - 1, y);
//                    neighbor2 = image.getRGB(x + 1, y);
//                } else {
//                    neighbor1 = image.getRGB(x - 1, y + 1);
//                    neighbor2 = image.getRGB(x + 1, y - 1);
//                }
//
//                if (magnitude >= (neighbor1 & 0xFF) && magnitude >= (neighbor2 & 0xFF))
//                {
//                    suppressedImage.setRGB(x, y, magnitude << 16 | magnitude << 8 | magnitude);
//                }
//                else
//                {
//                    suppressedImage.setRGB(x, y, 0);
//                }
//            }
//        }
//
//        return suppressedImage;
//    }

    private BufferedImage parallelDoubleThreshold(BufferedImage image, ExecutorService executor, int rowsPerRegion) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            for (int startY = 0; startY < image.getHeight(); startY += rowsPerRegion) {
                final int regionStartY = startY;
                final int regionEndY = Math.min(regionStartY + rowsPerRegion, image.getHeight());
                executor.submit(() -> {
                    doubleThreshold(image, result, regionStartY, regionEndY);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void doubleThreshold(BufferedImage source, BufferedImage destination, int startY, int endY) {
        int width = source.getWidth();
        int height = source.getHeight();

//        int padding = (endY - startY) / 2; // Adjust as needed
//        int startYWithPadding = Math.max(0, startY - padding);
//        int endYWithPadding = Math.min(height - 1, endY + padding);

        int lowThreshold = 20;
        int highThreshold = 50;

        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRGB(x, y);
                int magnitude = (pixel >> 16) & 0xFF; // Assuming image type is BufferedImage.TYPE_INT_RGB

                if (magnitude >= highThreshold) {
                    destination.setRGB(x, y, 255 << 24 | 255 << 16 | 255 << 8 | 255);
                } else if (magnitude >= lowThreshold) {
                    destination.setRGB(x, y, 255 << 24 | 127 << 16 | 127 << 8 | 127);
                } else {
                    destination.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
                }
            }
        }
    }
//    private static BufferedImage doubleThreshold(BufferedImage image)
//    {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        BufferedImage thresholdImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//
//        int lowThreshold = 20;
//        int highThreshold = 50;
//
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                int pixel = image.getRGB(x, y);
//                int magnitude = (pixel >> 16) & 0xFF;
//
//                if (magnitude >= highThreshold)
//                {
//                    thresholdImage.setRGB(x, y, 255 << 24 | 255 << 16 | 255 << 8 | 255);
//                }
//                else if (magnitude >= lowThreshold)
//                {
//                    thresholdImage.setRGB(x, y, 255 << 24 | 127 << 16 | 127 << 8 | 127);
//                }
//                else
//                    thresholdImage.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
//            }
//        }
//        return thresholdImage;
//    }

    private BufferedImage parallelEdgeTracking(BufferedImage image, ExecutorService executor, int rowsPerRegion) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            for (int startY = 0; startY < image.getHeight(); startY += rowsPerRegion) {
                final int regionStartY = startY;
                final int regionEndY = Math.min(regionStartY + rowsPerRegion, image.getHeight());
                executor.submit(() -> {
                    edgeTracking(image, result, regionStartY, regionEndY);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void edgeTracking(BufferedImage source, BufferedImage destination, int startY, int endY) {
        int width = source.getWidth();
        int height = source.getHeight();

//        int padding = (endY - startY) / 2; // Adjust as needed
//        int startYWithPadding = Math.max(0, startY - padding);
//        int endYWithPadding = Math.min(height - 1, endY + padding);

        int strongEdge = 255 << 24 | 255 << 16 | 255 << 8 | 255;
        int weakEdge = 255 << 24 | 127 << 16 | 127 << 8 | 127;

        System.out.println(strongEdge + " " + weakEdge + " " + 0xFF000000);

        for (int x = 1; x < width - 1; x++)
        {
            for (int y = startY; y < endY; y++)
            {
                int pixel = source.getRGB(x, y);

                if (pixel == strongEdge)
                    destination.setRGB(x, y, strongEdge);
                else if (pixel == weakEdge)
                {
                    if ((source.getRGB(x+1, y-1) == strongEdge) || (source.getRGB(x+1, y) == strongEdge)
                    || (source.getRGB(x+1, y+1) == strongEdge)
                    || (source.getRGB(x, y-1) == strongEdge) || (source.getRGB(x, y+1) == strongEdge)
                    || (source.getRGB(x-1, y-1) == strongEdge) || (source.getRGB(x-1, y) == strongEdge)
                    || (source.getRGB(x-1, y+1) == strongEdge))
                    {
                        destination.setRGB(x, y, strongEdge);
                    }
                    else
                    {
                        destination.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
                    }
                }
            }
        }
    }

//    private static BufferedImage edgeTracking(BufferedImage image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        BufferedImage edges = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//
//        int strongEdge = 255 << 24 | 255 << 16 | 255 << 8 | 255;
//        int weakEdge = 255 << 24 | 127 << 16 | 127 << 8 | 127;
//
//        System.out.println(strongEdge + " " + weakEdge + " " + 0xFF000000);
//
//        for (int x = 1; x < width - 1; x++)
//        {
//            for (int y = 1; y < height - 1; y++)
//            {
//                int pixel = image.getRGB(x, y);
//
//                if (pixel == strongEdge)
//                    edges.setRGB(x, y, strongEdge);
//                else if (pixel == weakEdge)
//                {
//                    if ((image.getRGB(x+1, y-1) == strongEdge) || (image.getRGB(x+1, y) == strongEdge)
//                    || (image.getRGB(x+1, y+1) == strongEdge)
//                    || (image.getRGB(x, y-1) == strongEdge) || (image.getRGB(x, y+1) == strongEdge)
//                    || (image.getRGB(x-1, y-1) == strongEdge) || (image.getRGB(x-1, y) == strongEdge)
//                    || (image.getRGB(x-1, y+1) == strongEdge))
//                    {
//                        edges.setRGB(x, y, strongEdge);
//                    }
//                    else
//                    {
//                        edges.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
//                    }
//                }
//            }
//        }
//
//        return edges;
//    }

    private static int getGrayLevel(int rgb) {
        // Extract the red, green, and blue components
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Compute the gray level using luminance formula
        return (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    }

}
