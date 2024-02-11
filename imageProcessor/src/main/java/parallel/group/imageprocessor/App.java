package parallel.group.imageprocessor;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Border;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import java.io.File;

public class App extends Application {

    private Scene createStartScene(Stage primaryStage)
    {
        int width = 720;
        int height = 720;
        BorderPane rootNode = new BorderPane();

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1000);
        imageView.setFitHeight(600);
        BorderPane.setAlignment(imageView, Pos.CENTER_LEFT);

        rootNode.setCenter(imageView);

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
               Image image = new Image(selectedFile.toURI().toString());
               imageView.setImage(image);
           }
        });
        rootNode.setBottom(getImageButton);

        return new Scene(rootNode, width, height);
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
