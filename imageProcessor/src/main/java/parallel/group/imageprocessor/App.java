package parallel.group.imageprocessor;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.Border;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

public class App extends Application {
    private Image selectedImage;
    private ImageView imageView;
    private BorderPane rootNode;
    private int windowHeight;
    private int windowWidth;

    private void initWindow()
    {
        this.windowHeight = 720;
        this.windowWidth = 720;
        this.rootNode = new BorderPane();
    }

    private Scene createStartScene(Stage primaryStage)
    {
        initWindow();
        initImageView();
        this.rootNode.setCenter(imageView);

        Button getImageButton = new Button("Open Image");
        getImageButton.setAlignment(Pos.CENTER_LEFT);
        getImageButton.setMinSize(150, 30);
        getImageButton.setStyle("-fx-alignment: center;");
        getImageButton.setOnAction(event -> {
           FileChooser fileChooser = new FileChooser();
           fileChooser.setTitle("Select Image File");
           fileChooser.getExtensionFilters().addAll(
                   new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp")
           );

           File selectedFile = fileChooser.showOpenDialog(primaryStage);

           if (selectedFile != null)
           {
               this.selectedImage = new Image(selectedFile.toURI().toString());
               setImageView(this.selectedImage);
           }
        });

        Button zoomButton = new Button("Zoom");
        zoomButton.setAlignment(Pos.CENTER_RIGHT);
        zoomButton.setStyle("-fx-alignment: center;");
        zoomButton.setOnAction(event -> zoomPopUp());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(getImageButton, zoomButton);
        rootNode.setBottom(buttonBox);

        return new Scene(rootNode, this.windowWidth, this.windowHeight);
    }

    private void initImageView()
    {
        this.imageView = new ImageView();
        imageView.setPreserveRatio(true);
    }

    private ImageView getImageView()
    {
        return this.imageView;
    }
    private void setImageView(Image image)
    {
        this.imageView.setImage(image);
    }

    private BufferedImage convertToBufferedImg(Image image)
    {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = buffImg.getRaster();

        PixelReader pixelReader = image.getPixelReader();
        int[] pixels =new int[width * height];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                Color color = pixelReader.getColor(x, y);
                int argb = (int) (color.getOpacity() * 255) << 24 |
                           (int) (color.getRed() * 255) << 16 |
                           (int) (color.getGreen() * 255) << 8 |
                           (int) (color.getBlue() * 255);
                pixels[y * width + x] = argb;
            }
        }

        raster.setDataElements(0, 0, width, height, pixels);
        return buffImg;
    }

    private Image convertToJavaFXImg(BufferedImage buffImg)
    {
        WritableImage writableImg = new WritableImage(buffImg.getWidth(), buffImg.getHeight());
        PixelWriter pixelWriter = writableImg.getPixelWriter();

        for (int y = 0; y < buffImg.getHeight(); y++)
        {
            for (int x = 0; x < buffImg.getWidth(); x++)
            {
                int argb = buffImg.getRGB(x, y);
                pixelWriter.setArgb(x, y, argb);
            }
        }

        return writableImg;
    }

    private void setSelectedImage(Image image)
    {
        this.selectedImage = image;
    }

    private void zoomPopUp()
    {
        Stage popUp = new Stage();
        popUp.setTitle("Zoom Image");

        BorderPane pane = new BorderPane();
        pane.setTop(new Label("Enter Zoom Percentage: "));

        TextField zoomPercent = new TextField();
        pane.setCenter(zoomPercent);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> popUp.close());

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            double scale = Double.parseDouble(zoomPercent.getText()) / 100;
            try
            {
                Zoom(scale);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            popUp.close();
        });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(cancelButton, submitButton);
        pane.setBottom(buttonBox);

        Scene scene = new Scene(pane, 300, 150);
        popUp.setScene(scene);
        popUp.show();
    }

    private void Zoom(double scale) throws Exception
    {
        if (this.selectedImage != null)
        {
            BufferedImage image = convertToBufferedImg(this.selectedImage);
            Zoom zoomFunction = new Zoom(image);
            zoomFunction.printDimensions();
            BufferedImage zoomedImg = zoomFunction.zoom(image, scale);
            Image updatedImage = convertToJavaFXImg(zoomedImg);
            setSelectedImage(updatedImage);
            setImageView(updatedImage);
        }
        else
        {
            System.out.println("Image has not been selected.");
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = new Stage();
        primaryStage.setTitle("Image Processor");

        Scene startScene = createStartScene(primaryStage);
        primaryStage.setScene(startScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
