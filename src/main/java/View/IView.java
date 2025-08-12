package View;

import javafx.event.ActionEvent;

public interface IView {

    void generateMaze(int rows, int cols);

    void solveMaze();

    void onHelpClicked(ActionEvent actionEvent);

    void onExitClicked(ActionEvent actionEvent);

    void onGenerateMazeClicked(int rows, int cols);

    void onSolveClicked(ActionEvent actionEvent);

    String getUpdatePlayerRow();

    String getUpdatePlayerCol();

    void setUpdatePlayerRow(int updatePlayerRow);

    void setUpdatePlayerCol(int updatePlayerCol);
}