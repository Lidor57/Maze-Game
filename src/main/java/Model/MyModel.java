package Model;

import Client.Client;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import IO.*;
import Client.IClientStrategy;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    private Maze maze;
    private Solution solution;
    private Position playerPosition;
    private Position goalPosition;

    private final String serverIP = "localhost";

    @Override
    public void generateMaze(int rows, int cols) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(rows * cols) + 100 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        playerPosition = new Position(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
                        goalPosition = new Position(maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColumnIndex());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //we will call the server to generate the maze. we're using a client which asks to generate a maze
        setChanged();
        notifyObservers("maze generated");
        movePlayer(playerPosition.getRowIndex(), playerPosition.getColumnIndex());
    }


    public Maze getMaze() {
        return maze;
    }

    @Override
    public void solveMaze() {
        if (maze == null) return;

        int solveMazePort = 5401;
        try (Socket socket = new Socket(serverIP, solveMazePort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(maze);
            out.flush();

            solution = (Solution) in.readObject();

            setChanged();
            notifyObservers("maze solved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        int playerRow = playerPosition.getRowIndex();
        int playerCol = playerPosition.getColumnIndex();
        switch (direction) {
            case UP -> {
                if (playerRow > 0) {
                    movePlayer(playerRow - 1, playerCol);
                }
            }
            case DOWN -> {
                if (playerRow < maze.getGrid().length - 1)
                    movePlayer(playerRow + 1, playerCol);
            }
            case LEFT -> {
                if (playerCol > 0)
                    movePlayer(playerRow, playerCol - 1);
            }
            case RIGHT -> {
                if (playerCol < maze.getGrid()[0].length - 1)
                    movePlayer(playerRow, playerCol + 1);
            }
            case UP_LEFT -> {
                if (playerRow > 0 && playerCol > 0)
                    movePlayer(playerRow - 1, playerCol - 1);
            }
            case UP_RIGHT -> {
                if (playerRow > 0 && playerCol < maze.getGrid()[0].length - 1)
                    movePlayer(playerRow - 1, playerCol + 1);
            }
            case DOWN_LEFT -> {
                if (playerRow < maze.getGrid().length - 1 && playerCol > 0)
                    movePlayer(playerRow + 1, playerCol - 1);
            }
            case DOWN_RIGHT -> {
                if (playerRow < maze.getGrid().length - 1 && playerCol < maze.getGrid()[0].length - 1)
                    movePlayer(playerRow + 1, playerCol + 1);
            }
        }
    }

    @Override
    public Position getPlayerPosition() {
        return playerPosition;
    }

    @Override
    public Position getGoalPosition() {
        return goalPosition;
    }

    @Override
    public void saveMaze(String path) {
        if (maze == null) return;

        try (MyCompressorOutputStream out = new MyCompressorOutputStream(new java.io.FileOutputStream(path))) {
            byte[] mazeBytes = maze.toByteArray(); // Converts the maze to a byte array
            out.write(mazeBytes);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadMaze(String path) {
        try (MyDecompressorInputStream in = new MyDecompressorInputStream(new java.io.FileInputStream(path))) {
            // Allocate enough space for decompressed maze
            byte[] decompressed = new byte[10000]; // adjust size based on expected maze size
            in.read(decompressed);

            maze = new Maze(decompressed);
            playerPosition = maze.getStartPosition();
            goalPosition = maze.getGoalPosition();

            setChanged();
            notifyObservers("maze loaded"); // trigger ViewModel update

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPlayerPosition(Position position) {
        this.playerPosition = position;
    }

    private void movePlayer(int row, int col) {
        if (maze.getGrid()[row][col] == 0) {
            this.playerPosition = new Position(row, col);
            setChanged();
            notifyObservers("player moved");
        }
    }
}