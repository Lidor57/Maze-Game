package View.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MazeSizePopupController {
    private Stage mainStage;

    @FXML
    private TextField rowsField;

    @FXML
    private TextField colsField;

    private MyViewController mainController;

    public void setMainController(MyViewController controller) {
        this.mainController = controller;
    }

    @FXML
    private void generateButton() {
        try {
            int rows = Integer.parseInt(rowsField.getText());
            int cols = Integer.parseInt(colsField.getText());

            if (rows <= 0 || cols <= 0) {
                throw new NumberFormatException(); // only positive numbers
            }

            // Load MyView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/MyView.fxml"));
            Parent root = loader.load();

            // Pass maze size to MyViewController
            MyViewController controller = loader.getController();
            controller.generateMaze(rows, cols);

            // Switch scene on same stage
            Stage stage = mainStage;
            stage.setScene(new Scene(root, 800, 800));
            stage.setTitle("Maze Display");
            stage.setResizable(true);
            stage.show();
            closeWindow();

        } catch (NumberFormatException e) {
            showInvalidInputAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInvalidInputAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid Maze Dimensions");
        alert.setContentText("Please enter valid **positive integers** for both rows and columns.");
        alert.showAndWait();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) rowsField.getScene().getWindow();
        stage.close();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}