package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private final IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
    }

    public Position getPlayerPosition() {
        return model.getPlayerPosition();
    }

    public Position getGoalPosition() {
        return model.getGoalPosition();
    }

    public void setPlayerPosition(Position position) {
        model.setPlayerPosition(position);
    }

    public Solution getSolution() {
        return model.getSolution();
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public void movePlayer(KeyEvent keyEvent) {
        MovementDirection direction;
        switch (keyEvent.getCode()) {
            case NUMPAD2, DIGIT2 -> direction = MovementDirection.DOWN;
            case NUMPAD8, DIGIT8 -> direction = MovementDirection.UP;
            case NUMPAD4, DIGIT4 -> direction = MovementDirection.LEFT;
            case NUMPAD6, DIGIT6 -> direction = MovementDirection.RIGHT;
            case NUMPAD1, DIGIT1 -> direction = MovementDirection.DOWN_LEFT;
            case NUMPAD3, DIGIT3 -> direction = MovementDirection.DOWN_RIGHT;
            case NUMPAD7, DIGIT7 -> direction = MovementDirection.UP_LEFT;
            case NUMPAD9, DIGIT9 -> direction = MovementDirection.UP_RIGHT;
            default -> {
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public void loadMaze(String mazeName) {
        model.loadMaze(mazeName);
    }

    public void saveMaze(String path) {
        model.saveMaze(path);
    }
}