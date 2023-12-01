package application;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Random;

public class Main extends Application {
    private int size = 8; // Default grid size
    private int mines = 10; // Default number of mines

    private Button[][] buttons;
    private boolean[][] minesArray;
    private boolean[][] flagged;
    private int remainingFlags;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showCustomizationDialog(); // Get user input for grid size and number of mines

        generateMines();

        GridPane grid = createGrid();
        addButtonsToGrid(grid);

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCustomizationDialog() {
        TextInputDialog sizeDialog = new TextInputDialog("8");
        sizeDialog.setTitle("Grid Size");
        sizeDialog.setHeaderText(null);
        sizeDialog.setContentText("Enter grid size:");

        Optional<String> sizeResult = sizeDialog.showAndWait();
        sizeResult.ifPresent(s -> size = Integer.parseInt(s));

        TextInputDialog minesDialog = new TextInputDialog("10");
        minesDialog.setTitle("Number of Mines");
        minesDialog.setHeaderText(null);
        minesDialog.setContentText("Enter number of mines:");

        Optional<String> minesResult = minesDialog.showAndWait();
        minesResult.ifPresent(m -> mines = Integer.parseInt(m));
    }

    private void generateMines() {
        buttons = new Button[size][size];
        minesArray = new boolean[size][size];
        flagged = new boolean[size][size];
        remainingFlags = mines;

        Random random = new Random();

        int count = 0;
        while (count < mines) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);

            if (!minesArray[x][y]) {
                minesArray[x][y] = true;
                count++;
            }
        }
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        // Load the image
        Image backgroundImage = new Image("http://benimatic.com/tfwiki/images/8/89/Forest_Grass.png");

        // Set the background for each cell
        BackgroundImage backgroundImg = new BackgroundImage(
                backgroundImage,
                null,
                null,
                null,
                new BackgroundSize(100, 100, true, true, true, false)
        );

        Background background = new Background(backgroundImg);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j] = new Button();

                // Set the background for each cell
                buttons[i][j].setBackground(background);

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

    private void addButtonsToGrid(GridPane grid) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Initially, hide mines and flags
                buttons[i][j].setText("");
            }
        }
    }

    private void handleButtonClick(int x, int y) {
        if (minesArray[x][y]) {
            showGameOverAlert();
        } else {
            revealCell(x, y);
        }
    }

    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
            // Right-click to toggle flag
            toggleFlag(x, y);
        }
    }

    private void toggleFlag(int x, int y) {
        if (!buttons[x][y].isDisabled()) {
            if (!flagged[x][y] && remainingFlags > 0) {
                buttons[x][y].setText("F"); // Display flag
                flagged[x][y] = true;
                remainingFlags--;
            } else if (flagged[x][y]) {
                buttons[x][y].setText(""); // Remove flag
                flagged[x][y] = false;
                remainingFlags++;
            }
        }
    }

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
                    if (newX >= 0 && newX < size && newY >= 0 && newY < size && !buttons[newX][newY].isDisabled()) {
                        revealCell(newX, newY);
                    }
                }
            }
        }
    }

    private int countNeighboringMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < size && newY >= 0 && newY < size && minesArray[newX][newY]) {
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
        alert.setContentText("You hit a mine! Game over.");
        alert.showAndWait();
        System.exit(0);
    }
}