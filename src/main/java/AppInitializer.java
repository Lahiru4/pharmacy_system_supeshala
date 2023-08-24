import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginform.fxml"))));
        primaryStage.resizableProperty();
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("/view/assests/images/WhatsApp_Image_2023-05-11_at_10.01.28-removebg-preview.png"));
        primaryStage.show();


    }
}
