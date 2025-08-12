package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    private Solution solution;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    //goal position:
    private int goalRow = 0;
    private int goalCol = 0;
    // maze images:
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameBackground = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();

    private Image wallImage, playerImage, goalImage, backgroundImage;

    public MazeDisplayer() {
        widthProperty().addListener((obs, oldW, newW) -> redrawMaze());
        heightProperty().addListener((obs, oldH, newH) -> redrawMaze());
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public String getImageFileNameBackground() {
        return imageFileNameBackground.get();
    }

    public void setImageFileNameBackground(String imageFileNameBackground) {
        this.imageFileNameBackground.set(imageFileNameBackground);
        InputStream stream = getClass().getResourceAsStream(imageFileNameBackground);
        if (stream != null)
            this.backgroundImage = new Image(stream);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
        InputStream stream = getClass().getResourceAsStream(imageFileNameGoal);
        if (stream != null)
            this.goalImage = new Image(stream);
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setGoalPosition(int row, int col) {
        this.goalRow = row;
        this.goalCol = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }


    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
        InputStream stream = getClass().getResourceAsStream(imageFileNameWall);
        if (stream != null)
            this.wallImage = new Image(stream);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
        InputStream stream = getClass().getResourceAsStream(imageFileNamePlayer);
        if (stream != null)
            this.playerImage = new Image(stream);
    }

    public int getGoalCol() {
        return goalCol;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public void drawMaze(int[][] maze) {
        this.maze = maze;
        draw();
    }

    public void redrawMaze() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (maze == null || maze.length == 0 || maze[0].length == 0) return;

        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        if (canvasHeight <= 0 || canvasWidth <= 0) return;
        int rows = maze.length;
        int cols = maze[0].length;

        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        if(backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);
        }

        drawMazeWalls(gc, cellHeight, cellWidth, rows, cols);

        if (solution != null)
            drawSolution(gc, cellHeight, cellWidth);

        drawPlayer(gc, cellHeight, cellWidth);
        drawGoal(gc, cellHeight, cellWidth);
    }

    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        if (solution == null || solution.getSolutionPath().isEmpty()) return;

        graphicsContext.setFill(Color.YELLOW);

        for (AState state : solution.getSolutionPath()) {
            if (state instanceof MazeState) {
                Position pos = ((MazeState) state).getPosition();
                int row = pos.getRowIndex();
                int col = pos.getColumnIndex();

                double x = col * cellWidth;
                double y = row * cellHeight;

                // Draw a filled rectangle for the solution step
                graphicsContext.fillRect(x, y, cellWidth, cellHeight);
            }
        }
    }


    private void drawMazeWalls(GraphicsContext gc, double cellHeight, double cellWidth, int rows, int cols) {
        if (maze == null || maze.length == 0 || maze[0].length == 0) return;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 1) {
                    double x = j * cellWidth;
                    double y = i * cellHeight;

                    if (wallImage != null)
                        gc.drawImage(wallImage, x, y, cellWidth, cellHeight);
                    else {
                        gc.setFill(Color.RED);
                        gc.fillRect(x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext gc, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;

        if (playerImage != null)
            gc.drawImage(playerImage, x, y, cellWidth, cellHeight);
        else {
            gc.setFill(Color.GREEN);
            gc.fillRect(x, y, cellWidth, cellHeight);
        }
    }

    private void drawGoal(GraphicsContext gc, double cellHeight, double cellWidth) {
        double x = getGoalCol() * cellWidth;
        double y = getGoalRow() * cellHeight;

        if (goalImage != null)
            gc.drawImage(goalImage, x, y, cellWidth, cellHeight);
        else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, cellWidth, cellHeight);
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return 0;
    }

    @Override
    public double prefHeight(double width) {
        return 0;
    }
}