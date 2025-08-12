package View.Controllers;

import Model.IModel;
import Model.MyModel;
import View.IView;
import View.MazeDisplayer;
import View.StageManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import ViewModel.MyViewModel;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements IView, Initializable, Observer {
    private MyViewModel viewModel;

    // boolean to keep track of whether maze was loaded from file or not
    private boolean mazeLoadedFromFile = false;

    // boolean to keep track of whether the current maze was saved already or not
    private boolean mazeWasSaved = false;


    @FXML
    private Label statusLabel;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public OptionsButtonController settings = new OptionsButtonController();

    @FXML
    private StackPane displayerPane;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    StringProperty updateGoalRow = new SimpleStringProperty();
    StringProperty updateGoalCol = new SimpleStringProperty();

    public MyViewController() throws IOException {
        IModel model = new MyModel();
        viewModel = new MyViewModel(model);
        setViewModel(viewModel);
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public void setUpdateGoalRow(int updatePlayerRow) {
        this.updateGoalRow.set(updateGoalRow + "");
    }

    public void setUpdateGoalCol(int updatePlayerCol) {
        this.updateGoalRow.set(updateGoalCol + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void onExitClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onGenerateMazeClicked(int rows, int cols) {
        //pass generate to view model
        viewModel.generateMaze(rows, cols);

        mazeLoadedFromFile = false;
        mazeWasSaved = false;

        //show status to user
        statusLabel.setText("Maze generated");
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    public void markMazeAsLoadedFromFile() {
        mazeLoadedFromFile = true;
        mazeWasSaved = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> displayerPane.requestFocus());

        mazeDisplayer.widthProperty().bind(displayerPane.widthProperty());
        mazeDisplayer.heightProperty().bind(displayerPane.heightProperty());

        mazeDisplayer.widthProperty().addListener((obs, oldVal, newVal) -> mazeDisplayer.redrawMaze());
        mazeDisplayer.heightProperty().addListener((obs, oldVal, newVal) -> mazeDisplayer.redrawMaze());
    }

    public void generateMaze(int rows, int cols) {
        viewModel.generateMaze(rows, cols);
    }

    public void solveMaze() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
        viewModel.solveMaze();
    }

    public void setPlayerPosition(int row, int col) {
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void setGoalPosition(int row, int col) {
        mazeDisplayer.setGoalPosition(row, col);
        setUpdateGoalRow(row);
        setUpdateGoalCol(col);
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change) {
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            case "maze loaded" -> mazeLoaded();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    public void applyImages() {
        mazeDisplayer.setImageFileNamePlayer(settings.getCharacterPath());
        mazeDisplayer.setImageFileNameGoal(settings.getGoalPath());
        mazeDisplayer.setImageFileNameWall("/seaWeed.png");
        mazeDisplayer.setImageFileNameBackground("/BikiniBottom.png");
    }

    private void mazeLoaded() {
        mazeDisplayer.drawMaze(viewModel.getMaze().getGrid());
        setPlayerPosition(viewModel.getPlayerPosition().getRowIndex(), viewModel.getPlayerPosition().getColumnIndex());
        setGoalPosition(viewModel.getGoalPosition().getRowIndex(), viewModel.getGoalPosition().getColumnIndex());
    }

    private void mazeGenerated() {
        setGoalPosition(viewModel.getGoalPosition().getRowIndex(), viewModel.getGoalPosition().getColumnIndex());
        applyImages();
        mazeDisplayer.drawMaze(viewModel.getMaze().getGrid());
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
        mazeDisplayer.requestFocus();
    }

    private void playerMoved() {
        int playerRow = viewModel.getPlayerPosition().getRowIndex();
        int playerCol = viewModel.getPlayerPosition().getColumnIndex();
        int goalRow = viewModel.getGoalPosition().getRowIndex();
        int goalCol = viewModel.getGoalPosition().getColumnIndex();

        setPlayerPosition(viewModel.getPlayerPosition().getRowIndex(), viewModel.getPlayerPosition().getColumnIndex());

        if (playerRow == goalRow && playerCol == goalCol) {
            onMazeCompleted();
        }
    }

    private void onMazeCompleted() {
        MusicPlayer.playVictoryMusic(); // Will resume background music after
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/MazeCompletedPopup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(StageManager.mainStage);
            stage.showAndWait();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    @FXML
    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case NUMPAD8, NUMPAD2, NUMPAD4, NUMPAD6, NUMPAD7, NUMPAD9, NUMPAD1, NUMPAD3, DIGIT2, DIGIT4, DIGIT6, DIGIT8,
                 DIGIT1, DIGIT3, DIGIT7, DIGIT9 -> viewModel.movePlayer(event);
        }
    }

    public void onSolveClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
        viewModel.solveMaze();
    }

    public void onSaveClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");

        // Add .maze extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze Files (*.maze)", "*.maze"));

        // Suggest default file name
        fileChooser.setInitialFileName("myMaze.maze");

        // Open save dialog
        File selectedFile = fileChooser.showSaveDialog(((Node) actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Make sure it ends with .maze
            String path = selectedFile.getAbsolutePath();
            if (!path.endsWith(".maze")) {
                path += ".maze";
            }

            // Save using the ViewModel
            viewModel.saveMaze(path);

            mazeWasSaved = true;

            // success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Successful");
            alert.setHeaderText(null);
            alert.setContentText("Maze saved to:\n" + path);
            alert.showAndWait();
        }
    }

    public void onHelpClicked(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs//HelpButton.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Platform.runLater(() -> {
                mazeDisplayer.requestFocus();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onBackClicked(ActionEvent actionEvent) {
        if (!mazeLoadedFromFile && viewModel.getMaze() != null && !mazeWasSaved) {
            // Unsaved generated maze, show confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure you want to exit without saving?");
            alert.setHeaderText(null);
            alert.setContentText("You have an unsaved maze. Are you sure you want to return to the main menu?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yes, cancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == yes) {
                    goToMainMenu(actionEvent);
                }
            });
        } else {
            // Either maze was loaded or nothing to save
            goToMainMenu(actionEvent);
        }
    }

    private void goToMainMenu(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXMLs/MainMenu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 800));
            stage.setTitle("Main Menu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}