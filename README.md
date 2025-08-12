Maze Game

📜 Description
Maze Game is a JavaFX-based application where players can navigate and solve mazes with a graphical interface, complete with background music.

🚀 Running the Game
JavaFX is not bundled inside the JAR. To run the game, you need:

Java 15 or later (Java 16+ supported with extra flags)

JavaFX SDK (version 16)

To run the game, type the following command in CMD (modify based on installation path):
java --module-path "path_to_folder\javafx-sdk-16\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.media ^
     --enable-native-access=javafx.graphics ^
     -jar MazeGame.jar
