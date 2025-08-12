package View.Controllers;

import Server.Configurations;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OptionsButtonController {

    @FXML
    private CheckBox musicCheckBox;

    @FXML
    private TextField poolSizeTextField;
    @FXML
    private ComboBox<String> algorithmComboBox;

    @FXML
    private ComboBox<String> mazeGenComboBox;

    @FXML
    private ComboBox<String> characterComboBox;

    private static String characterPath = "/spongebob.png";
    private static String goalPath = "/KrabbyPatty.png";

    @FXML
    public void initialize() {
        algorithmComboBox.getItems().addAll("BFS", "DFS", "BestFirstSearch");
        algorithmComboBox.setValue("BFS");

        characterComboBox.getItems().addAll("SpongeBob", "Patrick", "Squidward", "Mr. Krabs");
        characterComboBox.setValue("SpongeBob");

        mazeGenComboBox.getItems().addAll("Normal", "Simple", "Empty");
        mazeGenComboBox.setValue("Normal");
    }

    @FXML
    private void handleSaveSettings() {
        Configurations config = Configurations.getInstance();
        boolean musicOn = musicCheckBox.isSelected();
        String mazeGenAlgorithm = mazeGenComboBox.getValue();
        String solvingAlgorithm = algorithmComboBox.getValue();
        String character = characterComboBox.getValue();
        String poolSize = poolSizeTextField.getText();

        //handle music
        MusicPlayer.setMusicEnabled(musicOn);

        //handle choices
        setCharacterPath(getImageForCharacter(character));
        setGoalPath(getImageForGoal(character));
        setGeneratingAlgorithm(mazeGenAlgorithm);
        setSolvingAlgorithm(solvingAlgorithm);
        try {
            if (Integer.parseInt(poolSize) > 0) {
                config.setProperty("threadPoolSize", poolSize);
            } else {
                showInvalidPoolSizeAlert();
                return;
            }
        } catch (NumberFormatException e) {
            showInvalidPoolSizeAlert();
            return;
        }

        //close window
        Stage stage = (Stage) musicCheckBox.getScene().getWindow();
        stage.close();
    }

    private void setGeneratingAlgorithm(String mazeGenAlgorithm) {
        Configurations config = Configurations.getInstance();

        String generatorClass;
        switch (mazeGenAlgorithm) {
            case "Normal" -> generatorClass = "MyMazeGenerator";
            case "Simple" -> generatorClass = "SimpleMazeGenerator";
            case "Empty" -> generatorClass = "EmptyMazeGenerator";
            default -> generatorClass = "MyMazeGenerator"; // fallback if unknown
        }

        config.setProperty("mazeGeneratingAlgorithm", generatorClass);
    }

    private void setSolvingAlgorithm(String solvingAlgorithm) {
        Configurations config = Configurations.getInstance();

        String generatorClass;
        switch (solvingAlgorithm) {
            case "BFS" -> generatorClass = "BreadthFirstSearch";
            case "DFS" -> generatorClass = "DepthFirstSearch";
            case "BestFirstSearch" -> generatorClass = "BestFirstSearch";
            default -> generatorClass = "BreadthFirstSearch"; // fallback if unknown
        }

        config.setProperty("mazeSearchingAlgorithm", generatorClass);
    }



    private void showInvalidPoolSizeAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Thread Pool Input");
        alert.setHeaderText("");
        alert.setContentText("Please enter a whole number above 0 in the pool size selection field.");
        alert.setGraphic(null);
        alert.show();
    }

    public String getCharacterPath() {
        return characterPath;
    }

    public void setCharacterPath(String characterPath) {
        OptionsButtonController.characterPath = characterPath;
    }

    public String getGoalPath() {
        return goalPath;
    }

    public void setGoalPath(String goalPath) {
        OptionsButtonController.goalPath = goalPath;
    }

    //image path by character
    private String getImageForCharacter(String characterName) {
        return switch (characterName) {
            case "Patrick" -> "/Patrick.png";
            case "Squidward" -> "/squidward.png";
            case "Mr. Krabs" -> "/mrKrab.png";
            default -> "/spongebob.png";
        };
    }

    //image path by character
    private String getImageForGoal(String characterName) {
        return switch (characterName) {
            case "Patrick" -> "/PatricksHouse.png";
            case "Squidward" -> "/Klarinet.png";
            case "Mr. Krabs" -> "/Dollar.png";
            default -> "/KrabbyPatty.png";
        };
    }
}