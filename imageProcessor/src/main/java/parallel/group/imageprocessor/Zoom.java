package parallel.group.imageprocessor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Zoom {
    private BufferedImage image;

    public Zoom (BufferedImage image)
    {
        this.image = image;
    }

    public void printDimensions()
    {
        System.out.println("Height: " + image.getHeight());
        System.out.println("Width: " + image.getWidth());
    }
}
