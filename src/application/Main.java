package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.util.Random;

public class Main extends Application {
    private static final int SIZE = 8;
    private static final int MINES = 10;

    private Button[][] buttons = new Button[SIZE][SIZE];
    private boolean[][] mines = new boolean[SIZE][SIZE];
    private boolean[][] flagged = new boolean[SIZE][SIZE];
    private int remainingFlags = MINES;
    
    private Image eggImage;
    private Image brokenEggImage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	// the egg and broken egg images
        eggImage = new Image("https://img.freepik.com/free-vector/egg-cartoon-style_78370-1042.jpg");
        brokenEggImage = new Image("https://img.freepik.com/premium-vector/egg-yolk-vector_688334-6.jpg?size=626&ext=jpg&ga=GA1.1.1880011253.1699747200&semt=ais");

    	
        generateMines();

        GridPane grid = createGrid();
        addButtonsToGrid(grid);

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateMines() {
        Random random = new Random();

        int count = 0;
        while (count < MINES) {
            int x = random.nextInt(SIZE);
            int y = random.nextInt(SIZE);

            if (!mines[x][y]) {
                mines[x][y] = true;
                count++;
            }
        }
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new Button();
                buttons[i][j].setMinSize(40, 40);
                final int x = i;
                final int y = j;
                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));

                grid.add(buttons[i][j], j, i);
            }
        }

        return grid;
    }

    //VERSION 1
    private void addButtonsToGrid(GridPane grid) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Initially, hide mines and flags
                buttons[i][j].setText("");
            }
        }
    }
    
//    //VERSION 2 -- Adds Egg Images 
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                buttons[i][j].setGraphic(new ImageView(eggImage));
//            }
//        }
//    }
    
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                ImageView imageView = new ImageView(eggImage);
//                imageView.setFitWidth(30); // Set the desired width
//                imageView.setFitHeight(30); // Set the desired height
//
//                buttons[i][j] = new Button();
//                buttons[i][j].setMinSize(40, 40);
//                final int x = i;
//                final int y = j;
//                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
//                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));
//                buttons[i][j].setGraphic(imageView);
//
//                grid.add(buttons[i][j], j, i);
//            }
//        }
//    }

    private void handleButtonClick(int x, int y) {
        if (mines[x][y]) {
            showGameOverAlert();
        } else {
            revealCell(x, y);
        }
    }

    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            // right-click for toggle flag
            toggleFlag(x, y);
        }
    }

    private void toggleFlag(int x, int y) {
        if (!buttons[x][y].isDisabled()) {
            if (!flagged[x][y] && remainingFlags > 0) {
                buttons[x][y].setText("F"); // show flag
                flagged[x][y] = true;
                remainingFlags--;
            } else if (flagged[x][y]) {
                buttons[x][y].setText(""); // remove flag
                flagged[x][y] = false;
                remainingFlags++;
            }
        }
    }

    // VERSION 1
    private void revealCell(int x, int y) {
        buttons[x][y].setDisable(true);

        int count = countNeighboringMines(x, y);
        if (count > 0) {
            buttons[x][y].setText(String.valueOf(count));
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = x + i;
                    int newY = y + j;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && !buttons[newX][newY].isDisabled()) {
                        revealCell(newX, newY);
                    }
                }
            }
        }
    }
    
//    //VERSION 2 ---- not really working
//    private void revealCell(int x, int y) {
//        buttons[x][y].setDisable(true);
//
//        int count = countNeighboringMines(x, y);
//        if (count > 0) {
//            buttons[x][y].setGraphic(new ImageView(brokenEggImage));
//            buttons[x][y].setText(String.valueOf(count));
//        } else {
//            for (int i = -1; i <= 1; i++) {
//                for (int j = -1; j <= 1; j++) {
//                    int newX = x + i;
//                    int newY = y + j;
//                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && !buttons[newX][newY].isDisabled()) {
//                        revealCell(newX, newY);
//                    }
//                }
//            }
//        }
//    }

    private int countNeighboringMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && mines[newX][newY]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You squashed an egg! Game over.");
        alert.showAndWait();
        System.exit(0);
    }
}
