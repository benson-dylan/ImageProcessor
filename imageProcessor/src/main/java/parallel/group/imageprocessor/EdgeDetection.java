package parallel.group.imageprocessor;
import java.awt.image.Kernel;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.nio.Buffer;

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

        float[] blurKernel = {
            1/16f, 2/16f, 1/16f,
            2/16f, 4/16f, 2/16f,
            1/16f, 2/16f, 1/16f
        };

        BufferedImage blurredImage = applyBlur(grayImage, blurKernel);
        BufferedImage gradientImage = computeGradient(blurredImage);
        BufferedImage suppressedImage = nonMaximumSuppression(gradientImage);
        BufferedImage thresholdImage = doubleThreshold(suppressedImage);
        BufferedImage edges = edgeTracking(thresholdImage);
        return edges;
    }

    private static BufferedImage applyBlur(BufferedImage image, float[] kernelData)
    {
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return convolveOp.filter(image, null);
    }

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

    private static BufferedImage nonMaximumSuppression(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage suppressedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 1; y < height - 1; y++)
        {
            for (int x = 1; x < width - 1; x++)
            {
                int pixel = image.getRGB(x, y);
                int magnitude = (pixel >> 16) & 0xFF;

                double direction = Math.atan2(
                        ((pixel >> 16) & 0xFF) - ((pixel >> 8) & 0xFF),
                        ((pixel >> 8) & 0xFF) - (pixel & 0xFF)
                );

                int neighbor1 = 0, neighbor2 = 0;
                if (direction < Math.PI / 8 || direction >= 7 * Math.PI / 8) {
                    neighbor1 = image.getRGB(x, y - 1);
                    neighbor2 = image.getRGB(x, y + 1);
                } else if (direction >= Math.PI / 8 && direction < 3 * Math.PI / 8) {
                    neighbor1 = image.getRGB(x - 1, y - 1);
                    neighbor2 = image.getRGB(x + 1, y + 1);
                } else if (direction >= 3 * Math.PI / 8 && direction < 5 * Math.PI / 8) {
                    neighbor1 = image.getRGB(x - 1, y);
                    neighbor2 = image.getRGB(x + 1, y);
                } else {
                    neighbor1 = image.getRGB(x - 1, y + 1);
                    neighbor2 = image.getRGB(x + 1, y - 1);
                }

                if (magnitude >= (neighbor1 & 0xFF) && magnitude >= (neighbor2 & 0xFF))
                {
                    suppressedImage.setRGB(x, y, magnitude << 16 | magnitude << 8 | magnitude);
                }
                else
                {
                    suppressedImage.setRGB(x, y, 0);
                }
            }
        }

        return suppressedImage;
    }
    
    private static BufferedImage doubleThreshold(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage thresholdImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        int lowThreshold = 20;
        int highThreshold = 50;
        
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int pixel = image.getRGB(x, y);
                int magnitude = (pixel >> 16) & 0xFF;
                
                if (magnitude >= highThreshold)
                {
                    thresholdImage.setRGB(x, y, 255 << 24 | 255 << 16 | 255 << 8 | 255);
                }
                else if (magnitude >= lowThreshold)
                {
                    thresholdImage.setRGB(x, y, 255 << 24 | 127 << 16 | 127 << 8 | 127);
                }
                else 
                    thresholdImage.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
            }
        }
        return thresholdImage;
    }

    private static BufferedImage edgeTracking(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage edges = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int strongEdge = 255 << 24 | 255 << 16 | 255 << 8 | 255;
        int weakEdge = 255 << 24 | 127 << 16 | 127 << 8 | 127;

        System.out.println(strongEdge + " " + weakEdge + " " + 0xFF000000);

        for (int x = 1; x < width - 1; x++)
        {
            for (int y = 1; y < height - 1; y++)
            {
                int pixel = image.getRGB(x, y);

                if (pixel == strongEdge)
                    edges.setRGB(x, y, strongEdge);
                else if (pixel == weakEdge)
                {
                    if ((image.getRGB(x+1, y-1) == strongEdge) || (image.getRGB(x+1, y) == strongEdge)
                    || (image.getRGB(x+1, y+1) == strongEdge)
                    || (image.getRGB(x, y-1) == strongEdge) || (image.getRGB(x, y+1) == strongEdge)
                    || (image.getRGB(x-1, y-1) == strongEdge) || (image.getRGB(x-1, y) == strongEdge)
                    || (image.getRGB(x-1, y+1) == strongEdge))
                    {
                        edges.setRGB(x, y, strongEdge);
                    }
                    else
                    {
                        edges.setRGB(x, y, 255 << 24 | 0 << 16 | 0 << 8 | 0);
                    }
                }
            }
        }

        return edges;
    }

    private static int getGrayLevel(int rgb) {
        // Extract the red, green, and blue components
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Compute the gray level using luminance formula
        return (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    }

}