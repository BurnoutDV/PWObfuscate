import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("PCG.fxml"));
        primaryStage.setTitle("Password Color Generator");
        primaryStage.setScene(new Scene(root, 450, 600));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(450);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
