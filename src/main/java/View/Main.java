package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Server.*;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageManager.mainStage = primaryStage;

        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server mazeSolvingServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

        mazeGeneratingServer.start();
        mazeSolvingServer.start();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLs/MainMenu.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 800, 800));

        primaryStage.setOnCloseRequest(event -> { // make sure pressing 'X' terminates the program
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        View.Controllers.MusicPlayer.startMusic();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            File userConfig = new File("userConfig.properties");
            if (userConfig.exists()) {
                userConfig.delete();
            }
        }));

        launch();
    }
}
