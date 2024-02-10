package parallel.group.imageprocessor;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import java.io.File;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = new Stage();
        primaryStage.setTitle("Image Processor");

        int width = 720;
        int height = 720;
        Group rootNode = new Group();
        rootNode.
        Scene scene = new Scene(rootNode ,width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
