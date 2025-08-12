package View.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MazeCompletedPopupController {
    @FXML
    private Button playAgainButton;

    @FXML
    private Button backToMenuButton;

    @FXML
    private void handlePlayAgain() {
        try {
            Stage popupStage = (Stage) playAgainButton.getScene().getWindow();
            Stage mainStage = (Stage) popupStage.getOwner();
            popupStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/mazeSize.fxml"));
            Parent root = loader.load();

            MazeSizePopupController controller = loader.getController();
            controller.setMainStage(mainStage);
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Generate Maze");
            popupStage.show();
            MusicPlayer.startMusic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBackToMenu() {
        try {
            Stage popupStage = (Stage) backToMenuButton.getScene().getWindow();
            popupStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/MainMenu.fxml"));
            Parent root = loader.load();

            Stage mainStage = (Stage) popupStage.getOwner();

            mainStage.setScene(new Scene(root));
            mainStage.setTitle("Main Menu");
            mainStage.setResizable(true);
            mainStage.show();
            MusicPlayer.startMusic();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
