package parallel.group.imageprocessor;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

public class Contour {
    private BufferedImage image;
    private final int NUM_THREADS = 8;

    public Contour (BufferedImage image)
    {
        this.image = image;
    }

    public BufferedImage contour()
    {
        System.out.println("Attempting to find countours...");

        try
        {

        }

        finally
        {

        }
    }
}
