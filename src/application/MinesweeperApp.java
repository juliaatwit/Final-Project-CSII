package application;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class MinesweeperApp extends Application {
    private int size = 8; // Default grid size
    private int mines = 10; // Default number of mines

    private Button[][] buttons;
    private boolean[][] minesArray;
    private boolean[][] flagged;
    private int remainingFlags;
    Image flagImage;
    Font font = Font.font("Verdana", FontWeight.EXTRA_BOLD, 25);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        flagImage = new Image("https://pngfre.com/wp-content/uploads/Chicken-25-1024x990.png");
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
                ImageView flagImageView = new ImageView(flagImage);
                flagImageView.setFitHeight(size*3);
                flagImageView.setFitWidth(size*3);
                buttons[x][y].setGraphic(flagImageView); // Display flag
                flagged[x][y] = true;
                remainingFlags--;
                if (Arrays.deepEquals(flagged, minesArray)){
                    winner();
                }
            } else if (flagged[x][y]) {
                buttons[x][y].setGraphic(null); // Remove flag
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
            buttons[x][y].setFont(Font.font( "Arial",FontWeight.EXTRA_BOLD,20));
            buttons[x][y].setBackground(null);

            switch (count) {
                case 1 -> buttons[x][y].setTextFill(Color.DARKRED);
                case 2 -> buttons[x][y].setTextFill(Color.ORANGE);
                case 3 -> buttons[x][y].setTextFill(Color.GOLD);
                case 4 -> buttons[x][y].setTextFill(Color.YELLOWGREEN);
                case 5 -> buttons[x][y].setTextFill(Color.GREEN);
                case 6 -> buttons[x][y].setTextFill(Color.LIGHTSEAGREEN);
                case 7 -> buttons[x][y].setTextFill(Color.CYAN);
                case 8 -> buttons[x][y].setTextFill(Color.DARKCYAN);
            }

            buttons[x][y].setOpacity(100);


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

    private void winner(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner!");
        alert.setHeaderText(null);
        alert.setContentText("You Won, Congrats!");
        alert.showAndWait();
        System.exit(0);
    }
}

//--------------------------------------------------------------------------------------------------------------------
//
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundImage;
//import javafx.scene.layout.BackgroundSize;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//import java.util.Optional;
//import java.util.Random;
//
//public class MinesweeperApp extends Application {
//    private int size = 8; // Default grid size
//    private int mines = 10; // Default number of mines
//
//    private Button[][] buttons;
//    private boolean[][] minesArray;
//    private boolean[][] flagged;
//    private int remainingFlags;
//    Image flagImage;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        flagImage = new Image("https://pngfre.com/wp-content/uploads/Chicken-25-1024x990.png");
//        showCustomizationDialog(); // Get user input for grid size and number of mines
//
//        generateMines();
//
//        GridPane grid = createGrid();
//        addButtonsToGrid(grid);
//
//        Scene scene = new Scene(grid);
//        primaryStage.setTitle("Minesweeper");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void showCustomizationDialog() {
//        TextInputDialog sizeDialog = new TextInputDialog("8");
//        sizeDialog.setTitle("Grid Size");
//        sizeDialog.setHeaderText(null);
//        sizeDialog.setContentText("Enter grid size:");
//
//        Optional<String> sizeResult = sizeDialog.showAndWait();
//        sizeResult.ifPresent(s -> size = Integer.parseInt(s));
//
//        TextInputDialog minesDialog = new TextInputDialog("10");
//        minesDialog.setTitle("Number of Mines");
//        minesDialog.setHeaderText(null);
//        minesDialog.setContentText("Enter number of mines:");
//
//        Optional<String> minesResult = minesDialog.showAndWait();
//        minesResult.ifPresent(m -> mines = Integer.parseInt(m));
//    }
//
//    private void generateMines() {
//        buttons = new Button[size][size];
//        minesArray = new boolean[size][size];
//        flagged = new boolean[size][size];
//        remainingFlags = mines;
//
//        Random random = new Random();
//
//        int count = 0;
//        while (count < mines) {
//            int x = random.nextInt(size);
//            int y = random.nextInt(size);
//
//            if (!minesArray[x][y]) {
//                minesArray[x][y] = true;
//                count++;
//            }
//        }
//    }
//
//    private GridPane createGrid() {
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(5);
//        grid.setVgap(5);
//
//        // Load the image
//        Image backgroundImage = new Image("http://benimatic.com/tfwiki/images/8/89/Forest_Grass.png");
//
//        // Set the background for each cell
//        BackgroundImage backgroundImg = new BackgroundImage(
//                backgroundImage,
//                null,
//                null,
//                null,
//                new BackgroundSize(100, 100, true, true, true, false)
//        );
//
//        Background background = new Background(backgroundImg);
//
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                buttons[i][j] = new Button();
//
//                // Set the background for each cell
//                buttons[i][j].setBackground(background);
//
//                buttons[i][j].setMinSize(40, 40);
//                final int x = i;
//                final int y = j;
//                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
//                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));
//
//                grid.add(buttons[i][j], j, i);
//            }
//        }
//
//        return grid;
//    }
//
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                // Initially, hide mines and flags
//                buttons[i][j].setText("");
//            }
//        }
//    }
//
//    private void handleButtonClick(int x, int y) {
//        if (minesArray[x][y]) {
//            showGameOverAlert();
//        } else {
//            revealCell(x, y);
//        }
//    }
//
//    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
//        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
//            // Right-click to toggle flag
//            toggleFlag(x, y);
//        }
//    }
//
//    private void toggleFlag(int x, int y) {
//        if (!buttons[x][y].isDisabled()) {
//            if (!flagged[x][y] && remainingFlags > 0) {
//                ImageView flagImageView = new ImageView(flagImage);
//                flagImageView.setFitHeight(size*3);
//                flagImageView.setFitWidth(size*3);
//                buttons[x][y].setGraphic(flagImageView); // Display flag
//                flagged[x][y] = true;
//                remainingFlags--;
//            } else if (flagged[x][y]) {
//                buttons[x][y].setGraphic(null); // Remove flag
//                flagged[x][y] = false;
//                remainingFlags++;
//            }
//        }
//    }
//
//    private void revealCell(int x, int y) {
//        buttons[x][y].setDisable(true);
//
//        int count = countNeighboringMines(x, y);
//        if (count > 0) {
//            buttons[x][y].setText(String.valueOf(count));
//        } else {
//            for (int i = -1; i <= 1; i++) {
//                for (int j = -1; j <= 1; j++) {
//                    int newX = x + i;
//                    int newY = y + j;
//                    if (newX >= 0 && newX < size && newY >= 0 && newY < size && !buttons[newX][newY].isDisabled()) {
//                        revealCell(newX, newY);
//                    }
//                }
//            }
//        }
//    }
//
//    private int countNeighboringMines(int x, int y) {
//        int count = 0;
//        for (int i = -1; i <= 1; i++) {
//            for (int j = -1; j <= 1; j++) {
//                int newX = x + i;
//                int newY = y + j;
//                if (newX >= 0 && newX < size && newY >= 0 && newY < size && minesArray[newX][newY]) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    private void showGameOverAlert() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Game Over");
//        alert.setHeaderText(null);
//        alert.setContentText("You hit squashed an egg! Game over.");
//        alert.showAndWait();
//        System.exit(0);
//    }}

//-----------------------------------------------------------------------------------------------------------------------

// V2.2 --- added grass and bolder font
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.image.Image;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundImage;
//import javafx.scene.layout.BackgroundSize;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//import java.util.Optional;
//import java.util.Random;
//
//public class MinesweeperApp extends Application {
//    private int size = 8; // Default grid size
//    private int mines = 10; // Default number of mines
//
//    private Button[][] buttons;
//    private boolean[][] minesArray;
//    private boolean[][] flagged;
//    private int remainingFlags;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        showCustomizationDialog(); // Get user input for grid size and number of mines
//
//        generateMines();
//
//        GridPane grid = createGrid();
//        addButtonsToGrid(grid);
//
//        Scene scene = new Scene(grid);
//        primaryStage.setTitle("Minesweeper");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void showCustomizationDialog() {
//        TextInputDialog sizeDialog = new TextInputDialog("8");
//        sizeDialog.setTitle("Grid Size");
//        sizeDialog.setHeaderText(null);
//        sizeDialog.setContentText("Enter grid size:");
//
//        Optional<String> sizeResult = sizeDialog.showAndWait();
//        sizeResult.ifPresent(s -> size = Integer.parseInt(s));
//
//        TextInputDialog minesDialog = new TextInputDialog("10");
//        minesDialog.setTitle("Number of Mines");
//        minesDialog.setHeaderText(null);
//        minesDialog.setContentText("Enter number of mines:");
//
//        Optional<String> minesResult = minesDialog.showAndWait();
//        minesResult.ifPresent(m -> mines = Integer.parseInt(m));
//    }
//
//    private void generateMines() {
//        buttons = new Button[size][size];
//        minesArray = new boolean[size][size];
//        flagged = new boolean[size][size];
//        remainingFlags = mines;
//
//        Random random = new Random();
//
//        int count = 0;
//        while (count < mines) {
//            int x = random.nextInt(size);
//            int y = random.nextInt(size);
//
//            if (!minesArray[x][y]) {
//                minesArray[x][y] = true;
//                count++;
//            }
//        }
//    }
//
//    private GridPane createGrid() {
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(5);
//        grid.setVgap(5);
//
//        // Load the image
//        Image backgroundImage = new Image("http://benimatic.com/tfwiki/images/8/89/Forest_Grass.png");
//
//        // Set the background for each cell
//        BackgroundImage backgroundImg = new BackgroundImage(
//                backgroundImage,
//                null,
//                null,
//                null,
//                new BackgroundSize(100, 100, true, true, true, false)
//        );
//
//        Background background = new Background(backgroundImg);
//
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                buttons[i][j] = new Button();
//
//                // Set the background for each cell
//                buttons[i][j].setBackground(background);
//
//                buttons[i][j].setMinSize(40, 40);
//                final int x = i;
//                final int y = j;
//                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
//                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));
//
//                grid.add(buttons[i][j], j, i);
//            }
//        }
//
//        return grid;
//    }
//
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                // Initially, hide mines and flags
//                buttons[i][j].setText("");
//            }
//        }
//    }
//
//    private void handleButtonClick(int x, int y) {
//        if (minesArray[x][y]) {
//            showGameOverAlert();
//        } else {
//            revealCell(x, y);
//        }
//    }
//
//    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
//        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
//            // Right-click to toggle flag
//            toggleFlag(x, y);
//        }
//    }
//
//    private void toggleFlag(int x, int y) {
//        if (!buttons[x][y].isDisabled()) {
//            if (!flagged[x][y] && remainingFlags > 0) {
//                buttons[x][y].setText("F"); // Display flag
//                flagged[x][y] = true;
//                remainingFlags--;
//            } else if (flagged[x][y]) {
//                buttons[x][y].setText(""); // Remove flag
//                flagged[x][y] = false;
//                remainingFlags++;
//            }
//        }
//    }
//
////    private void revealCell(int x, int y) {
////        buttons[x][y].setDisable(true);
////
////        int count = countNeighboringMines(x, y);
////        if (count > 0) {
////            buttons[x][y].setText(String.valueOf(count));
////        } else {
////            for (int i = -1; i <= 1; i++) {
////                for (int j = -1; j <= 1; j++) {
////                    int newX = x + i;
////                    int newY = y + j;
////                    if (newX >= 0 && newX < size && newY >= 0 && newY < size && !buttons[newX][newY].isDisabled()) {
////                        revealCell(newX, newY);
////                    }
////                }
////            }
////        }
////    }
//    
//    private void revealCell(int x, int y) {
//        buttons[x][y].setDisable(true);
//
//        int count = countNeighboringMines(x, y);
//        if (count > 0) {
//            buttons[x][y].setText(String.valueOf(count));
//            // Set bold font for numbers
//            buttons[x][y].setStyle("-fx-font-weight: bold;");
//        } else {
//            for (int i = -1; i <= 1; i++) {
//                for (int j = -1; j <= 1; j++) {
//                    int newX = x + i;
//                    int newY = y + j;
//                    if (newX >= 0 && newX < size && newY >= 0 && newY < size && !buttons[newX][newY].isDisabled()) {
//                        revealCell(newX, newY);
//                    }
//                }
//            }
//        }
//    }
//
//    private int countNeighboringMines(int x, int y) {
//        int count = 0;
//        for (int i = -1; i <= 1; i++) {
//            for (int j = -1; j <= 1; j++) {
//                int newX = x + i;
//                int newY = y + j;
//                if (newX >= 0 && newX < size && newY >= 0 && newY < size && minesArray[newX][newY]) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    private void showGameOverAlert() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Game Over");
//        alert.setHeaderText(null);
//        alert.setContentText("You hit a mine! Game over.");
//        alert.showAndWait();
//        System.exit(0);
//    }
//}

//-----------------------------------------------------------------------------------------------------------------

// V2.1 --- added user input & flags 
//package application;
//
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//import java.util.Optional;
//import java.util.Random;
//
//public class MinesweeperApp extends Application {
//    private int size = 8; // Default grid size
//    private int mines = 10; // Default number of mines
//
//    private Button[][] buttons;
//    private boolean[][] minesArray;
//    private boolean[][] flagged;
//    private int remainingFlags;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        showCustomizationDialog(); // Get user input for grid size and number of mines
//
//        generateMines();
//
//        GridPane grid = createGrid();
//        addButtonsToGrid(grid);
//
//        Scene scene = new Scene(grid);
//        primaryStage.setTitle("Minesweeper");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void showCustomizationDialog() {
//        TextInputDialog sizeDialog = new TextInputDialog("8");
//        sizeDialog.setTitle("Grid Size");
//        sizeDialog.setHeaderText(null);
//        sizeDialog.setContentText("Enter grid size:");
//
//        Optional<String> sizeResult = sizeDialog.showAndWait();
//        sizeResult.ifPresent(s -> size = Integer.parseInt(s));
//
//        TextInputDialog minesDialog = new TextInputDialog("10");
//        minesDialog.setTitle("Number of Mines");
//        minesDialog.setHeaderText(null);
//        minesDialog.setContentText("Enter number of mines:");
//
//        Optional<String> minesResult = minesDialog.showAndWait();
//        minesResult.ifPresent(m -> mines = Integer.parseInt(m));
//    }
//
//    private void generateMines() {
//        buttons = new Button[size][size];
//        minesArray = new boolean[size][size];
//        flagged = new boolean[size][size];
//        remainingFlags = mines;
//
//        Random random = new Random();
//
//        int count = 0;
//        while (count < mines) {
//            int x = random.nextInt(size);
//            int y = random.nextInt(size);
//
//            if (!minesArray[x][y]) {
//                minesArray[x][y] = true;
//                count++;
//            }
//        }
//    }
//
//    private GridPane createGrid() {
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(5);
//        grid.setVgap(5);
//
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                buttons[i][j] = new Button();
//                buttons[i][j].setMinSize(40, 40);
//                final int x = i;
//                final int y = j;
//                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
//                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));
//
//                grid.add(buttons[i][j], j, i);
//            }
//        }
//
//        return grid;
//    }
//
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                // Initially, hide mines and flags
//                buttons[i][j].setText("");
//            }
//        }
//    }
//
//    private void handleButtonClick(int x, int y) {
//        if (minesArray[x][y]) {
//            showGameOverAlert();
//        } else {
//            revealCell(x, y);
//        }
//    }
//
//    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
//        if (event.getButton() == MouseButton.SECONDARY) {
//            // Right-click to toggle flag
//            toggleFlag(x, y);
//        }
//    }
//
//    private void toggleFlag(int x, int y) {
//        if (!buttons[x][y].isDisabled()) {
//            if (!flagged[x][y] && remainingFlags > 0) {
//                buttons[x][y].setText("F"); // Display flag
//                flagged[x][y] = true;
//                remainingFlags--;
//            } else if (flagged[x][y]) {
//                buttons[x][y].setText(""); // Remove flag
//                flagged[x][y] = false;
//                remainingFlags++;
//            }
//        }
//    }
//
//    private void revealCell(int x, int y) {
//        buttons[x][y].setDisable(true);
//
//        int count = countNeighboringMines(x, y);
//        if (count > 0) {
//            buttons[x][y].setText(String.valueOf(count));
//        } else {
//            for (int i = -1; i <= 1; i++) {
//                for (int j = -1; j <= 1; j++) {
//                    int newX = x + i;
//                    int newY = y + j;
//                    if (newX >= 0 && newX < size && newY >= 0 && newY < size && !buttons[newX][newY].isDisabled()) {
//                        revealCell(newX, newY);
//                    }
//                }
//            }
//        }
//    }
//
//    private int countNeighboringMines(int x, int y) {
//        int count = 0;
//        for (int i = -1; i <= 1; i++) {
//            for (int j = -1; j <= 1; j++) {
//                int newX = x + i;
//                int newY = y + j;
//                if (newX >= 0 && newX < size && newY >= 0 && newY < size && minesArray[newX][newY]) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    private void showGameOverAlert() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Game Over");
//        alert.setHeaderText(null);
//        alert.setContentText("You hit a mine! Game over.");
//        alert.showAndWait();
//        System.exit(0);
//    }
//}


// NORMAL V1.1 --- Regular Game 
//package application;
//
//import javafx.application.Application;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;
//
//import java.util.Random;
//
//public class MinesweeperApp extends Application {
//    private static final int SIZE = 8;
//    private static final int MINES = 10;
//
//    private Button[][] buttons = new Button[SIZE][SIZE];
//    private boolean[][] mines = new boolean[SIZE][SIZE];
//    private boolean[][] flagged = new boolean[SIZE][SIZE];
//    private int remainingFlags = MINES;
//    
//    
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//    	
//        generateMines();
//
//        GridPane grid = createGrid();
//        addButtonsToGrid(grid);
//
//        Scene scene = new Scene(grid);
//        primaryStage.setTitle("Minesweeper");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void generateMines() {
//        Random random = new Random();
//
//        int count = 0;
//        while (count < MINES) {
//            int x = random.nextInt(SIZE);
//            int y = random.nextInt(SIZE);
//
//            if (!mines[x][y]) {
//                mines[x][y] = true;
//                count++;
//            }
//        }
//    }
//
//    private GridPane createGrid() {
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(5);
//        grid.setVgap(5);
//
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                buttons[i][j] = new Button();
//                buttons[i][j].setMinSize(40, 40);
//                final int x = i;
//                final int y = j;
//                buttons[i][j].setOnAction(e -> handleButtonClick(x, y));
//                buttons[i][j].setOnMouseClicked(e -> handleMouseClick(x, y, e));
//
//                grid.add(buttons[i][j], j, i);
//            }
//        }
//
//        return grid;
//    }
//
//    //VERSION 1
//    private void addButtonsToGrid(GridPane grid) {
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                // Initially, hide mines and flags
//                buttons[i][j].setText("");
//            }
//        }
//    }
//    
//    private void handleButtonClick(int x, int y) {
//        if (mines[x][y]) {
//            showGameOverAlert();
//        } else {
//            revealCell(x, y);
//        }
//    }
//
//    private void handleMouseClick(int x, int y, javafx.scene.input.MouseEvent event) {
//        if (event.getButton() == MouseButton.SECONDARY) {
//            // right-click for toggle flag
//            toggleFlag(x, y);
//        }
//    }
//
//    private void toggleFlag(int x, int y) {
//        if (!buttons[x][y].isDisabled()) {
//            if (!flagged[x][y] && remainingFlags > 0) {
//                buttons[x][y].setText("F"); // show flag
//                flagged[x][y] = true;
//                remainingFlags--;
//            } else if (flagged[x][y]) {
//                buttons[x][y].setText(""); // remove flag
//                flagged[x][y] = false;
//                remainingFlags++;
//            }
//        }
//    }
//
//    // VERSION 1
//    private void revealCell(int x, int y) {
//        buttons[x][y].setDisable(true);
//
//        int count = countNeighboringMines(x, y);
//        if (count > 0) {
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
//    
//
//    private int countNeighboringMines(int x, int y) {
//        int count = 0;
//        for (int i = -1; i <= 1; i++) {
//            for (int j = -1; j <= 1; j++) {
//                int newX = x + i;
//                int newY = y + j;
//                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && mines[newX][newY]) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    private void showGameOverAlert() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Game Over");
//        alert.setHeaderText(null);
//        alert.setContentText("You squashed an egg! Game over.");
//        alert.showAndWait();
//        System.exit(0);
//    }
//}