package Model;

import algorithms.mazeGenerators.*;
import algorithms.search.*;

import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);

    Maze getMaze();

    void solveMaze();

    Solution getSolution();

    void updatePlayerLocation(MovementDirection direction);

    Position getPlayerPosition();

    void assignObserver(Observer o);

    Position getGoalPosition();

    void saveMaze(String path);

    void loadMaze(String path);

    void setPlayerPosition(Position position);
}