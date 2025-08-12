package View.Controllers;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainMenuController {
    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView mainImageView;

    @FXML
    public void onNewClicked(ActionEvent actionEvent) {
        try {
            // Load MazeSize.fxml (popup)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/mazeSize.fxml"));
            Parent root = loader.load();

            // Pass reference to main stage
            MazeSizePopupController dialogController = loader.getController();
            dialogController.setMainStage((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("New Maze");
            popup.setScene(new Scene(root));
            popup.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLoadClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze File");

        // Filter for .maze files only
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze Files (*.maze)", "*.maze"));

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Load the main maze view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/MyView.fxml"));
                Parent root = loader.load();

                // Get controller and inject ViewModel
                MyViewController viewController = loader.getController();

                // Create and inject ViewModel
                IModel model = new MyModel();
                MyViewModel viewModel = new MyViewModel(model);
                viewController.setViewModel(viewModel);

                // Load the maze using the ViewModel
                viewModel.loadMaze(selectedFile.getPath());

                //mark loaded
                viewController.markMazeAsLoadedFromFile();
                viewController.applyImages();

                // Change scene
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 800, 800));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void onAboutClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/AboutButton.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExitClicked(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void onOptionsClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/FXMLs/OptionsButton.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Game Settings");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        mainImageView.fitWidthProperty().bind(rootPane.widthProperty());
        mainImageView.fitHeightProperty().bind(rootPane.heightProperty());
    }
}