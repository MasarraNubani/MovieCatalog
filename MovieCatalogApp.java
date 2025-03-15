package com.example.project4;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class MovieCatalogApp extends Application {
    private final String buttonStyle =
            "-fx-background-color: linear-gradient(to bottom, #cbeef3,#d5bdaf);" +
                    "-fx-text-fill: #000000;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 8 15 8 15;" +
                    "-fx-background-radius: 5px;";

    private final String buttonHoverStyle =
            "-fx-background-color: linear-gradient(to bottom, #2c3e50, #2980b9);" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 8 15 8 15;" +
                    "-fx-background-radius: 5px;";



    private MovieCatalog catalog;
    private ObservableList<Movie> movies;
    private TableView<Movie> tableView;

    private int currentHashIndex = 0;
    private int totalHashCells = 0;

    private Label currentIndexLabel;
    private TableView<Movie> navigationTableView;
    private Label treeHeightLabel;

    private Button previousButton;
    private Button nextButton;

    private Label reportLabel;

    private Label highestRatedLabel;
    private Label lowestRatedLabel;

    private Stack<Operation> movieUndoStack = new Stack<>();
    private Stack<Operation> movieRedoStack = new Stack<>();

    private Button undoButton;
    private Button redoButton;

    private static final ButtonType SEARCH_BUTTON = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        catalog = new MovieCatalog();
        movies = FXCollections.observableArrayList();
        tableView = new TableView<>();

        TabPane tabPane = new TabPane();

        StackPane mainStackPane = new StackPane();

        String imagePath1 = "C:/Users/pc/Desktop/datastructure/Red Illustration Welcome To Our Movie Video.png";
        Image image1 = new Image(new File(imagePath1).toURI().toString());
        ImageView imageView1 = new ImageView(image1);
        imageView1.setFitWidth(1280);
        imageView1.setPreserveRatio(true);

        VBox mainVBox = new VBox();
        mainVBox.setSpacing(5);
        mainVBox.setPadding(new Insets(10, 10, 10, 10));

        ToolBar toolBar1 = createToolBar(primaryStage);

        Button startButton = new Button("Start");
        startButton.setStyle(  "-fx-background-color: linear-gradient(to bottom, #2c3e50, #bf4342);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 15 25 15 25;" +
                "-fx-background-radius: 5px;"
               );
        startButton.setOnAction(e -> {
            tabPane.getSelectionModel().select(1);
        });

        mainVBox.getChildren().addAll(toolBar1, new Separator(), startButton);

        mainStackPane.getChildren().addAll(imageView1, mainVBox);

        Tab mainTab = new Tab("All Movies", mainStackPane);
        mainTab.setClosable(false);

        StackPane navigationStackPane = new StackPane();

        String imagePath2 = "C:/Users/pc/Desktop/datastructure/28.jpg";
        Image image2 = new Image(new File(imagePath2).toURI().toString());
        ImageView imageView2 = new ImageView(image2);
        imageView2.setFitWidth(1280);
        imageView2.setPreserveRatio(true);

        VBox navigationVBox = createHashTableNavigationPane(primaryStage);

        navigationStackPane.getChildren().addAll(imageView2, navigationVBox);

        Tab navigationTab = new Tab("Hash Table Navigation", navigationStackPane);
        navigationTab.setClosable(false);

        tabPane.getTabs().addAll(mainTab, navigationTab);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 600);

        primaryStage.setTitle("Movie Catalog Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        File initialFile = new File("movies.txt");
        if (initialFile.exists()) {
            loadMoviesFromFile(initialFile);
        }

        totalHashCells = catalog.getHashTableSize();
    }

    private ToolBar createToolBar(Stage stage) {
        ToolBar toolBar = new ToolBar();

        Button openButton = new Button("Open");
        Button saveButton = new Button("Save");
        Button exitButton = new Button("Exit");

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        toolBar.getItems().addAll(openButton, saveButton, separator, exitButton);

        toolBar.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-padding: 1px;"
        );
//#cbeef3,#d5bdaf)
        String buttonStyle =
                "-fx-background-color: linear-gradient(to bottom,  #1c2541,#000000);" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-background-radius: 5px;";

        String buttonHoverStyle =
                "-fx-background-color: linear-gradient(to bottom, #2c3e50, #2980b9);" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-background-radius: 5px;";

        openButton.setStyle(buttonStyle);
        saveButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        addHoverEffect(openButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(saveButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(exitButton, buttonStyle, buttonHoverStyle);

        openButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Movie File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                loadMoviesFromFile(file);
            }
        });

        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Movie File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                saveMoviesToFile(file);
            }
        });

        exitButton.setOnAction(e -> {
            Platform.exit();
        });

        return toolBar;
    }


    private void addHoverEffect(Button button, String normalStyle, String hoverStyle) {
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }


    private VBox createHashTableNavigationPane(Stage stage) {
        VBox navigationVBox = new VBox();
        navigationVBox.setSpacing(10);
        navigationVBox.setPadding(new Insets(10, 10, 10, 10));

        ToolBar toolBar = createToolBar(stage);
        navigationVBox.getChildren().add(toolBar);
        navigationVBox.getChildren().add(new Separator());

        currentIndexLabel = new Label("Current Hash Index: 0");
        currentIndexLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #d5bdaf;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        navigationTableView = new TableView<>();
        setupNavigationTableView();

        reportLabel = new Label("Top and Least Ranked Movies Report:");
        reportLabel.setWrapText(true);
        reportLabel.setPadding(new Insets(100, 20, 80, 0));
        reportLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #d5bdaf;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        highestRatedLabel = new Label("Highest Rated Movie: N/A");
        lowestRatedLabel = new Label("Lowest Rated Movie: N/A");

        highestRatedLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #99e2b4;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        lowestRatedLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #640d14;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        treeHeightLabel = new Label("AVL Tree Height: 0");
        treeHeightLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #d5bdaf;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        previousButton = new Button("<<");
        nextButton = new Button(">>");

        previousButton.setStyle(buttonStyle);
        nextButton.setStyle(buttonStyle);

        addHoverEffect(previousButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(nextButton, buttonStyle, buttonHoverStyle);

        previousButton.setOnAction(e -> navigateHashTable(-1));
        nextButton.setOnAction(e -> navigateHashTable(1));

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(previousButton, nextButton);

        HBox operationsBox = createOperationsBoxForTab2();

        HBox tableAndReportBox = new HBox();
        tableAndReportBox.setSpacing(20);
        tableAndReportBox.getChildren().addAll(navigationTableView, reportLabel);

        HBox ratingsBox = new HBox();
        ratingsBox.setSpacing(20);
        ratingsBox.getChildren().addAll(highestRatedLabel, lowestRatedLabel);

        HBox.setHgrow(navigationTableView, Priority.ALWAYS);
        navigationTableView.setMaxWidth(Double.MAX_VALUE);
        reportLabel.setMaxWidth(Double.MAX_VALUE);
        reportLabel.setMinWidth(250);

        navigationVBox.getChildren().addAll(
                currentIndexLabel,
                tableAndReportBox,
                ratingsBox,
                treeHeightLabel,
                buttonBox,
                operationsBox
        );

        VBox.setVgrow(navigationTableView, Priority.NEVER);

        navigationVBox.setPrefWidth(900);
        navigationVBox.setPrefHeight(500);

        return navigationVBox;
    }


    private final String comboBoxStyle =
            "-fx-background-color: linear-gradient(to bottom, #89c2d9, #ddbea9);" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;";

    private final String comboBoxHoverStyle =
            "-fx-background-color: linear-gradient(to bottom, #0077b6, #000000);" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;";

    private HBox createOperationsBoxForTab2() {
        HBox operationsBox = new HBox();
        operationsBox.setSpacing(10);

        ComboBox<String> operationsComboBox = new ComboBox<>();
        operationsComboBox.setPromptText("Select Operation");
        operationsComboBox.getItems().addAll(
                "Print Sorted"
        );

        operationsComboBox.setStyle(comboBoxStyle);

        operationsComboBox.setOnMouseEntered(e -> operationsComboBox.setStyle(comboBoxHoverStyle));
        operationsComboBox.setOnMouseExited(e -> operationsComboBox.setStyle(comboBoxStyle));

        Button executeButton = new Button("Execute");
        executeButton.setStyle(comboBoxHoverStyle);
        addHoverEffect(executeButton, comboBoxHoverStyle, comboBoxStyle);
        executeButton.setDisable(true);

        operationsComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            executeButton.setDisable(newVal == null || newVal.isEmpty());
        });

        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        undoButton.setStyle(buttonStyle);
        redoButton.setStyle(buttonStyle);
        addHoverEffect(undoButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(redoButton, buttonStyle, buttonHoverStyle);
        undoButton.setDisable(true);
        redoButton.setDisable(true);

        undoButton.setOnAction(e -> undo());
        redoButton.setOnAction(e -> redo());

        Button addButton = new Button("Add Movie");
        Button updateButton = new Button("Update Movie");
        Button deleteButton = new Button("Delete Movie");
        Button searchButton = new Button("Search Movie");
        Button showAllButton = new Button("Show All");

        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        searchButton.setStyle(buttonStyle);
        showAllButton.setStyle(buttonStyle);

        addHoverEffect(addButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(updateButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(deleteButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(searchButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(showAllButton, buttonStyle, buttonHoverStyle);

        addButton.setOnAction(e -> showAddMovieDialog());
        updateButton.setOnAction(e -> promptAndUpdateMovie());
        deleteButton.setOnAction(e -> promptAndDeleteMovie());
        searchButton.setOnAction(e -> showSearchMovieDialog());
        showAllButton.setOnAction(e -> refreshTableView());

        executeButton.setOnAction(e -> {
            String selectedOperation = operationsComboBox.getSelectionModel().getSelectedItem();
            if (selectedOperation != null) {
                switch (selectedOperation) {
                    case "Print Sorted":
                        showPrintSortedDialogTab2();
                        break;
                    case "Print Top and Least Ranked Movies":
                        showPrintTopLeastDialogTab2();
                        break;
                    default:
                        showErrorDialog("Invalid Operation", "The selected operation is not recognized.");
                }
                operationsComboBox.getSelectionModel().clearSelection();
            }
        });

        operationsBox.getChildren().addAll(
                addButton, updateButton, deleteButton, searchButton, showAllButton,
                operationsComboBox, executeButton,
                undoButton, redoButton
        );

        return operationsBox;
    }

    private void setupNavigationTableView() {
        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.25); // 25%

        titleColumn.setStyle(
                "-fx-background-color: #e3d5ca;" +
                        "-fx-font-family: 'Arial Bold';" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: #000000;"
        );


        titleColumn.setCellFactory(tc -> new TableCell<Movie, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-font-family: 'Segoe UI';" +
                                    "-fx-text-fill: #2c3e50;"
                    );
                }
            }
        });

        TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.35); // 35%

        descriptionColumn.setStyle(
                "-fx-background-color: #e3d5ca;" +
                        "-fx-font-family: 'Arial Bold';" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: #000000;"
        );

        descriptionColumn.setCellFactory(tc -> new TableCell<Movie, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-font-family: 'Segoe UI';" +
                                    "-fx-text-fill: #2c3e50;"
                    );
                }
            }
        });

        TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        yearColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.15); // 15%

        yearColumn.setStyle(
                "-fx-background-color: #e3d5ca;" +
                        "-fx-font-family: 'Arial Bold';" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: #000000;"
        );

        yearColumn.setCellFactory(tc -> new TableCell<Movie, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-font-family: 'Segoe UI';" +
                                    "-fx-text-fill: #2c3e50;"
                    );
                }
            }
        });

        TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingColumn.setMaxWidth(1f * Integer.MAX_VALUE * 0.15); // 15%

        ratingColumn.setStyle(
                "-fx-background-color: #e3d5ca;" +
                        "-fx-font-family: 'Arial Bold';" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: #000000;"
        );

        ratingColumn.setCellFactory(tc -> new TableCell<Movie, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f", item));
                    setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-font-family: 'Segoe UI';" +
                                    "-fx-text-fill: #2c3e50;"
                    );
                }
            }
        });

        navigationTableView.getColumns().addAll(titleColumn, descriptionColumn, yearColumn, ratingColumn);

        navigationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        navigationTableView.setPrefWidth(600);
        navigationTableView.setPrefHeight(300);

        navigationTableView.setMinWidth(400);
        navigationTableView.setMinHeight(200);
        navigationTableView.setMaxWidth(800);
        navigationTableView.setMaxHeight(600);

        navigationTableView.setStyle(
                "-fx-background-color: #ebebeb;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;" +
                        "-fx-border-color: #bdc3c7;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-selection-bar: #2980b9;" +
                        "-fx-selection-bar-non-focused: #2980b9;"
        );

        Label placeholderLabel = new Label("No movies in this hash cell.");
        placeholderLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #7f8c8d;"
        );
        navigationTableView.setPlaceholder(placeholderLabel);

        navigationTableView.setRowFactory(tv -> new TableRow<Movie>() {
            @Override
            protected void updateItem(Movie item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getRating() >= 8.0) {
                        setStyle("-fx-background-color: #dff0d8;");
                    } else {
                        setStyle("");
                    }
                }
            }

            {
                setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && (!isEmpty())) {
                        Movie selectedMovie = getItem();
                        showMovieOptionsStage(selectedMovie);
                    }
                });
            }
        });
    }



    private void navigateHashTable(int direction) {
        int newIndex = currentHashIndex + direction;

        if (newIndex < 0 || newIndex >= totalHashCells) {
            return;
        }

        currentHashIndex = newIndex;
        displayCurrentHashCell();
    }

    private void displayCurrentHashCell() {
       // currentIndexLabel.setText("Current Hash Index: " + currentHashIndex);

        AVLTree currentTree = catalog.getHashTable().get(currentHashIndex);
        List<Movie> moviesInTree = currentTree.getAllMovies();

        if (moviesInTree.isEmpty()) {
            navigationTableView.setItems(FXCollections.observableArrayList());
            reportLabel.setText("Top and Least Ranked Movies Report:\n\n" + " has no movies.");
        } else {
            navigationTableView.setItems(FXCollections.observableArrayList(moviesInTree));

            StringBuilder report = new StringBuilder();
            Movie topMovie = moviesInTree.stream()
                    .max(Comparator.comparingDouble(Movie::getRating))
                    .orElse(null);
            Movie leastMovie = moviesInTree.stream()
                    .min(Comparator.comparingDouble(Movie::getRating))
                    .orElse(null);
           report.append("Hash Index ").append(currentHashIndex).append(":\n");
            if (topMovie != null) {
                report.append("  Top Ranked Movie: ").append(topMovie.getTitle())
                        .append(" (Rating: ").append(topMovie.getRating()).append(")\n");
            }
            if (leastMovie != null) {
                report.append("  Least Ranked Movie: ").append(leastMovie.getTitle())
                        .append(" (Rating: ").append(leastMovie.getRating()).append(")\n");
            }
//            report.append("  AVL Tree Height: ").append(currentTree.getTreeHeight()).append("\n\n");

            reportLabel.setText("Top and Least Ranked Movies Report:\n\n" + report.toString());
        }

        treeHeightLabel.setText("AVL Tree Height: " + currentTree.getTreeHeight());

        previousButton.setDisable(currentHashIndex == 0);
        nextButton.setDisable(currentHashIndex == totalHashCells - 1);
    }


    private void showAddMovieDialog() {
        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Add Movie");
        dialog.setHeaderText("Enter movie details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);

        TextField yearField = new TextField();
        yearField.setPromptText("Release Year");

        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (0.0 - 10.0)");

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Available", "Unavailable", "Archived");
        statusComboBox.setValue("Available");

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionArea,
                new Label("Release Year:"), yearField,
                new Label("Rating:"), ratingField,
                new Label("Status:"), statusComboBox
        );

        dialog.getDialogPane().setContent(vBox);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        ratingField.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(newValue.trim().isEmpty());
        });
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            addButton.setDisable(newVal == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                String yearText = yearField.getText().trim();
                String ratingText = ratingField.getText().trim();
                String status = statusComboBox.getValue();

                if (title.isEmpty() || description.isEmpty() || yearText.isEmpty() || ratingText.isEmpty() || status == null) {
                    showErrorDialog("Invalid Input", "All fields are required.");
                    return null;
                }

                if (catalog.get(title) != null) {
                    showErrorDialog("Duplicate Movie", "A movie with the title \"" + title + "\" already exists.");
                    return null;
                }

                try {
                    int releaseYear = Integer.parseInt(yearText);
                    double rating = Double.parseDouble(ratingText);
                    if (rating < 0.0 || rating > 10.0) {
                        showErrorDialog("Invalid Rating", "Rating must be between 0.0 and 10.0.");
                        return null;
                    }
                    return new Movie(title, description, releaseYear, rating);
                } catch (NumberFormatException e) {
                    showErrorDialog("Invalid Input", "Release Year must be an integer and Rating must be a number.");
                    return null;
                }
            }
            return null;
        });

        Optional<Movie> result = dialog.showAndWait();
        result.ifPresent(movie -> {
            catalog.put(movie);
            totalHashCells = catalog.getHashTableSize();
            refreshTableView();

            Operation operation = new Operation(Operation.OperationType.ADD, Operation.EntityType.MOVIE, movie, null);
            movieUndoStack.push(operation);
            movieRedoStack.clear();

            logOperation("Add Movie", movie, "Added movie " + movie.getTitle());

            updateUndoRedoButtons();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie added successfully.");
        });
    }



    private void showUpdateMovieDialog(Movie movieToUpdate) {
        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Update Movie");
        dialog.setHeaderText("Update details for: " + movieToUpdate.getTitle());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        CheckBox titleCheckBox = new CheckBox("Title");
        CheckBox descriptionCheckBox = new CheckBox("Description");
        CheckBox yearCheckBox = new CheckBox("Release Year");
        CheckBox ratingCheckBox = new CheckBox("Rating");
        CheckBox statusCheckBox = new CheckBox("Status");

        TextField titleField = new TextField(movieToUpdate.getTitle());
        titleField.setPromptText("Title");
        titleField.setDisable(true);

        TextArea descriptionArea = new TextArea(movieToUpdate.getDescription());
        descriptionArea.setPromptText("Description");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setDisable(true);

        TextField yearField = new TextField(String.valueOf(movieToUpdate.getReleaseYear()));
        yearField.setPromptText("Release Year");
        yearField.setDisable(true);

        TextField ratingField = new TextField(String.valueOf(movieToUpdate.getRating()));
        ratingField.setPromptText("Rating (0.0 - 10.0)");
        ratingField.setDisable(true);
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Available", "Unavailable", "Archived");
        statusComboBox.setDisable(true);

        VBox checkBoxVBox = new VBox(10);
        checkBoxVBox.getChildren().addAll(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox);

        VBox fieldsVBox = new VBox(10);
        fieldsVBox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionArea,
                new Label("Release Year:"), yearField,
                new Label("Rating:"), ratingField,
                new Label("Status:"), statusComboBox
        );

        HBox contentHBox = new HBox(20);
        contentHBox.setPadding(new Insets(10));
        contentHBox.getChildren().addAll(checkBoxVBox, fieldsVBox);

        dialog.getDialogPane().setContent(contentHBox);

        titleCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            titleField.setDisable(!isNowSelected);
        });

        descriptionCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            descriptionArea.setDisable(!isNowSelected);
        });

        yearCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            yearField.setDisable(!isNowSelected);
        });

        ratingCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            ratingField.setDisable(!isNowSelected);
        });

        statusCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            statusComboBox.setDisable(!isNowSelected);
        });

        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        titleCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            validateUpdateFieldsSelective(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox,
                    titleField, descriptionArea, yearField, ratingField, statusComboBox, updateButton);
        });
        descriptionCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            validateUpdateFieldsSelective(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox,
                    titleField, descriptionArea, yearField, ratingField, statusComboBox, updateButton);
        });
        yearCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            validateUpdateFieldsSelective(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox,
                    titleField, descriptionArea, yearField, ratingField, statusComboBox, updateButton);
        });
        ratingCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            validateUpdateFieldsSelective(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox,
                    titleField, descriptionArea, yearField, ratingField, statusComboBox, updateButton);
        });
        statusCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            validateUpdateFieldsSelective(titleCheckBox, descriptionCheckBox, yearCheckBox, ratingCheckBox, statusCheckBox,
                    titleField, descriptionArea, yearField, ratingField, statusComboBox, updateButton);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String newTitle = titleField.isDisabled() ? movieToUpdate.getTitle() : titleField.getText().trim();
                String newDescription = descriptionArea.isDisabled() ? movieToUpdate.getDescription() : descriptionArea.getText().trim();
                int newReleaseYear = yearField.isDisabled() ? movieToUpdate.getReleaseYear() : Integer.parseInt(yearField.getText().trim());
                double newRating = ratingField.isDisabled() ? movieToUpdate.getRating() : Double.parseDouble(ratingField.getText().trim());

                if (!newTitle.equalsIgnoreCase(movieToUpdate.getTitle()) && catalog.get(newTitle) != null) {
                    showErrorDialog("Duplicate Movie", "A movie with the title \"" + newTitle + "\" already exists.");
                    return null;
                }

                return new Movie(newTitle, newDescription, newReleaseYear, newRating);
            }
            return null;
        });

        Optional<Movie> updateResult = dialog.showAndWait();
        updateResult.ifPresent(updatedMovie -> {
            Movie oldMovie = new Movie(
                    movieToUpdate.getTitle(),
                    movieToUpdate.getDescription(),
                    movieToUpdate.getReleaseYear(),
                    movieToUpdate.getRating());

            if (!movieToUpdate.getTitle().equalsIgnoreCase(updatedMovie.getTitle())) {
                catalog.erase(movieToUpdate.getTitle());
            }

            catalog.put(updatedMovie);
            totalHashCells = catalog.getHashTableSize();
            refreshTableView();

            Operation operation = new Operation(Operation.OperationType.UPDATE, Operation.EntityType.MOVIE, updatedMovie, oldMovie);
            movieUndoStack.push(operation);
            movieRedoStack.clear();

            logOperation("Update Movie", updatedMovie, "Updated movie " + updatedMovie.getTitle());

            updateUndoRedoButtons();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie updated successfully.");
        });
    }


    private void validateUpdateFieldsSelective(CheckBox titleCheckBox, CheckBox descriptionCheckBox, CheckBox yearCheckBox,
                                               CheckBox ratingCheckBox, CheckBox statusCheckBox,
                                               TextField titleField, TextArea descriptionArea,
                                               TextField yearField, TextField ratingField,
                                               ComboBox<String> statusComboBox, Node updateButton) {
        boolean atLeastOneSelected = titleCheckBox.isSelected() || descriptionCheckBox.isSelected() ||
                yearCheckBox.isSelected() || ratingCheckBox.isSelected() ||
                statusCheckBox.isSelected();

        if (!atLeastOneSelected) {
            updateButton.setDisable(true);
            return;
        }

        boolean valid = true;

        if (titleCheckBox.isSelected()) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                valid = false;
            }
        }

        if (descriptionCheckBox.isSelected()) {
            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) {
                valid = false;
            }
        }

        if (yearCheckBox.isSelected()) {
            String yearText = yearField.getText().trim();
            try {
                Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        if (ratingCheckBox.isSelected()) {
            String ratingText = ratingField.getText().trim();
            try {
                double rating = Double.parseDouble(ratingText);
                if (rating < 0.0 || rating > 10.0) {
                    valid = false;
                }
            } catch (NumberFormatException e) {
                valid = false;
            }
        }

        if (statusCheckBox.isSelected()) {
            String status = statusComboBox.getValue();
            if (status == null || status.isEmpty()) {
                valid = false;
            }
        }

        updateButton.setDisable(!valid);
    }



    private void showDeleteMovieDialog(Movie movieToDelete) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete \"" + movieToDelete.getTitle() + "\"?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> confirmationResult = confirmation.showAndWait();
        if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
            Movie oldMovie = new Movie(
                    movieToDelete.getTitle(),
                    movieToDelete.getDescription(),
                    movieToDelete.getReleaseYear(),
                    movieToDelete.getRating());

            catalog.erase(movieToDelete.getTitle());
            totalHashCells = catalog.getHashTableSize();
            refreshTableView();

            Operation operation = new Operation(Operation.OperationType.DELETE, Operation.EntityType.MOVIE, null, oldMovie);
            movieUndoStack.push(operation);
            movieRedoStack.clear();

            logOperation("Delete Movie", movieToDelete, "Deleted movie " + movieToDelete.getTitle());

            updateUndoRedoButtons();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Movie deleted successfully.");

            if (catalog.getHashTableSize() == currentHashIndex) {
                displayCurrentHashCell();
            }
        }
    }


    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem updateItem = new MenuItem("Update");
        MenuItem deleteItem = new MenuItem("Delete");

        updateItem.setOnAction(e -> {
            Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                showUpdateMovieDialog(selectedMovie);
            }
        });

        deleteItem.setOnAction(e -> {
            Movie selectedMovie = tableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                showDeleteMovieDialog(selectedMovie);
            }
        });

        contextMenu.getItems().addAll(updateItem, deleteItem);
        return contextMenu;
    }


    private void validateUpdateFields(TextField titleField, TextArea descriptionArea, TextField yearField, TextField ratingField, Node updateButton) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String yearText = yearField.getText().trim();
        String ratingText = ratingField.getText().trim();

        boolean disable = title.isEmpty() || description.isEmpty() || yearText.isEmpty() || ratingText.isEmpty();

        if (!disable) {
            try {
                Integer.parseInt(yearText);
                double rating = Double.parseDouble(ratingText);
                if (rating < 0.0 || rating > 10.0) {
                    disable = true;
                }
            } catch (NumberFormatException e) {
                disable = true;
            }
        }

        updateButton.setDisable(disable);
    }


    private void undo() {
        if (!movieUndoStack.isEmpty()) {
            Operation lastOperation = movieUndoStack.pop();
            switch (lastOperation.getType()) {
                case ADD:
                    catalog.erase(lastOperation.getNewData().getTitle());
                    movieRedoStack.push(lastOperation);
                    break;
                case UPDATE:
                    catalog.erase(lastOperation.getNewData().getTitle());
                    catalog.put(lastOperation.getOldData());
                    movieRedoStack.push(lastOperation);
                    break;
                case DELETE:
                    catalog.put(lastOperation.getOldData());
                    movieRedoStack.push(lastOperation);
                    break;
            }
            refreshTableView();
            updateUndoRedoButtons();
            showAlert(Alert.AlertType.INFORMATION, "Undo", "Operation has been undone.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Undo", "No operations to undo.");
        }
    }

    private void redo() {
        if (!movieRedoStack.isEmpty()) {
            Operation lastOperation = movieRedoStack.pop();
            switch (lastOperation.getType()) {
                case ADD:
                    catalog.put(lastOperation.getNewData());
                    movieUndoStack.push(lastOperation);
                    break;
                case UPDATE:
                    catalog.erase(lastOperation.getOldData().getTitle());
                    catalog.put(lastOperation.getNewData());
                    movieUndoStack.push(lastOperation);
                    break;
                case DELETE:
                    catalog.erase(lastOperation.getOldData().getTitle());
                    movieUndoStack.push(lastOperation);
                    break;
            }
            refreshTableView();
            updateUndoRedoButtons();
            showAlert(Alert.AlertType.INFORMATION, "Redo", "Operation has been redone.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Redo", "No operations to redo.");
        }
    }


    private void updateUndoRedoButtons() {
        undoButton.setDisable(movieUndoStack.isEmpty());
        redoButton.setDisable(movieRedoStack.isEmpty());
    }


    public static class Operation {
        public enum OperationType {
            ADD, UPDATE, DELETE
        }

        public enum EntityType {
            MOVIE,
        }

        private OperationType type;
        private EntityType entityType;
        private Movie newData;
        private Movie oldData;

        public Operation(OperationType type, EntityType entityType, Movie newData, Movie oldData) {
            this.type = type;
            this.entityType = entityType;
            this.newData = newData;
            this.oldData = oldData;
        }

        // Getters
        public OperationType getType() {
            return type;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public Movie getNewData() {
            return newData;
        }

        public Movie getOldData() {
            return oldData;
        }
    }


    private void updateHighestAndLowestRatedLabels() {
        List<Movie> allMovies = catalog.getAllMovies();
        if (allMovies.isEmpty()) {
            highestRatedLabel.setText("Highest Rated Movie: N/A");
            lowestRatedLabel.setText("Lowest Rated Movie: N/A");
        } else {
            Movie highestRated = allMovies.stream()
                    .max(Comparator.comparingDouble(Movie::getRating))
                    .orElse(null);
            Movie lowestRated = allMovies.stream()
                    .min(Comparator.comparingDouble(Movie::getRating))
                    .orElse(null);

            highestRatedLabel.setText("Highest Rated Movie: " +
                    (highestRated != null ? highestRated.getTitle() + " (" + highestRated.getRating() + ")" : "N/A"));
            lowestRatedLabel.setText("Lowest Rated Movie: " +
                    (lowestRated != null ? lowestRated.getTitle() + " (" + lowestRated.getRating() + ")" : "N/A"));
        }
    }


    private void refreshTableView() {
        movies.setAll(catalog.getAllMovies());
        totalHashCells = catalog.getHashTableSize();
        displayCurrentHashCell();
        updateHighestAndLowestRatedLabels();
    }


    private void refreshNavigationTableView() {
        displayCurrentHashCell();
    }


    private void logOperation(String operationType, Movie movie, String message) {
        System.out.println(operationType + ": " + message);

    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void showPrintSortedDialogTab2() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Print Sorted Movies (Tab2)");
        dialog.setHeaderText("Choose sorting order for current AVL Tree:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<String> sortOrderCombo = new ComboBox<>();
        sortOrderCombo.getItems().addAll("Ascending", "Descending");
        sortOrderCombo.setValue("Ascending");

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(new Label("Sort Order:"), sortOrderCombo);

        dialog.getDialogPane().setContent(vBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String sortOrder = sortOrderCombo.getValue();
                AVLTree currentTree = catalog.getHashTable().get(currentHashIndex);
                List<Movie> moviesInTree = currentTree.getAllMovies();

                if (sortOrder.equals("Ascending")) {
                    moviesInTree.sort(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
                } else {
                    moviesInTree.sort(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
                }

                navigationTableView.setItems(FXCollections.observableArrayList(moviesInTree));
                showAlert(Alert.AlertType.INFORMATION, "Print Sorted (Tab2)", "Movies in Hash Index " + currentHashIndex + " have been sorted in " + sortOrder + " order.");
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showPrintTopLeastDialogTab2() {
        StringBuilder report = new StringBuilder();

        AVLTree currentTree = catalog.getHashTable().get(currentHashIndex);
        List<Movie> moviesInTree = currentTree.getAllMovies();

        if (!moviesInTree.isEmpty()) {
            Movie topMovie = moviesInTree.stream().max(Comparator.comparingDouble(Movie::getRating)).orElse(null);
            Movie leastMovie = moviesInTree.stream().min(Comparator.comparingDouble(Movie::getRating)).orElse(null);
            report.append("Hash Index ").append(currentHashIndex).append(":\n");
            if (topMovie != null) {
                report.append("  Top Ranked Movie: ").append(topMovie.getTitle())
                        .append(" (Rating: ").append(topMovie.getRating()).append(")\n");
            }
            if (leastMovie != null) {
                report.append("  Least Ranked Movie: ").append(leastMovie.getTitle())
                        .append(" (Rating: ").append(leastMovie.getRating()).append(")\n");
            }
            report.append("  AVL Tree Height: ").append(currentTree.getTreeHeight()).append("\n\n");
        } else {
            report.append("Hash Index ").append(currentHashIndex).append(" has no movies.\n\n");
        }

        reportLabel.setText(report.toString());
    }





    private void loadMoviesFromFile(File file) {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() {
                catalog.loadMoviesFromFile(file.getAbsolutePath());
                return null;
            }

            @Override
            protected void succeeded() {
                refreshTableView();
                displayCurrentHashCell();
                showInfoDialog("Load Successful", "Movies loaded successfully from " + file.getName());
            }

            @Override
            protected void failed() {
                showErrorDialog("Load Failed", "Failed to load movies from " + file.getName());
            }
        };
        new Thread(loadTask).start();
    }


    private void saveMoviesToFile(File file) {
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                catalog.saveMoviesToFile(file.getAbsolutePath());
                return null;
            }

            @Override
            protected void succeeded() {
                showInfoDialog("Save Successful", "Movies saved successfully to " + file.getName());
            }

            @Override
            protected void failed() {
                showErrorDialog("Save Failed", "Failed to save movies to " + file.getName());
            }
        };
        new Thread(saveTask).start();
    }


    private void promptAndUpdateMovie() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Movie");
        dialog.setHeaderText("Enter the Title of the Movie to Update:");
        dialog.setContentText("Title:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            Movie movieToUpdate = catalog.get(title.trim());
            if (movieToUpdate != null) {
                showUpdateMovieDialog(movieToUpdate);
            } else {
                showErrorDialog("Not Found", "No movie found with the title \"" + title.trim() + "\".");
            }
        });
    }


    private void promptAndDeleteMovie() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Movie");
        dialog.setHeaderText("Enter the Title of the Movie to Delete:");
        dialog.setContentText("Title:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            Movie movieToDelete = catalog.get(title.trim());
            if (movieToDelete != null) {
                showDeleteMovieDialog(movieToDelete);
            } else {
                showErrorDialog("Not Found", "No movie found with the title \"" + title.trim() + "\".");
            }
        });
    }
    private void showSearchMovieDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Movie");
        dialog.setHeaderText("Search by Title or Release Year:");

        dialog.getDialogPane().getButtonTypes().addAll(SEARCH_BUTTON, ButtonType.CANCEL);

        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton titleRadio = new RadioButton("Title");
        titleRadio.setToggleGroup(toggleGroup);
        titleRadio.setSelected(true);
        titleRadio.setStyle(radioButtonStyle);
        titleRadio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                titleRadio.setStyle(radioButtonSelectedStyle);
            } else {
                titleRadio.setStyle(radioButtonStyle);
            }
        });

        RadioButton yearRadio = new RadioButton("Release Year");
        yearRadio.setToggleGroup(toggleGroup);
        yearRadio.setStyle(radioButtonStyle);
        yearRadio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                yearRadio.setStyle(radioButtonSelectedStyle);
            } else {
                yearRadio.setStyle(radioButtonStyle);
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");
        searchField.setStyle(textFieldStyle);
        searchField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                searchField.setStyle(textFieldFocusedStyle);
            } else {
                searchField.setStyle(textFieldStyle);
            }
        });

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setStyle(vboxStyle);
        vBox.getChildren().addAll(titleRadio, yearRadio, searchField);

        Button resetButton = new Button("Reset");
        resetButton.setStyle(buttonStyle);
        resetButton.setOnMouseEntered(e -> resetButton.setStyle(buttonHoverStyle));
        resetButton.setOnMouseExited(e -> resetButton.setStyle(buttonStyle));
        resetButton.setOnAction(e -> {
            refreshNavigationTableView();
            dialog.close();
        });

        HBox resetBox = new HBox(resetButton);
        resetBox.setPadding(new Insets(10, 0, 0, 0));
        resetBox.setAlignment(Pos.CENTER);
        resetBox.setStyle(hboxStyle);
        vBox.getChildren().add(resetBox);

       dialog.getDialogPane().setContent(vBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == SEARCH_BUTTON) {
                String searchTerm = searchField.getText().trim();
                if (searchTerm.isEmpty()) {
                    showErrorDialog("Invalid Input", "Search term cannot be empty.");
                    return null;
                }

                List<Movie> allMovies = catalog.getAllMovies();
                List<Movie> searchResults = new ArrayList<>();

                if (titleRadio.isSelected()) {
                    for (Movie movie : allMovies) {
                        if (movie.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                            searchResults.add(movie);
                        }
                    }
                } else if (yearRadio.isSelected()) {
                    try {
                        int searchYear = Integer.parseInt(searchTerm);
                        for (Movie movie : allMovies) {
                            if (movie.getReleaseYear() == searchYear) {
                                searchResults.add(movie);
                            }
                        }
                    } catch (NumberFormatException e) {
                        showErrorDialog("Invalid Input", "Release Year must be an integer.");
                        return null;
                    }
                }

                if (searchResults.isEmpty()) {
                    showInfoDialog("No Results", "No movies found matching the search criteria.");
                    navigationTableView.setItems(FXCollections.observableArrayList());
                } else {
                    navigationTableView.setItems(FXCollections.observableArrayList(searchResults));
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showMovieOptionsStage(Movie selectedMovie) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Manage Movie: " + selectedMovie.getTitle());

        Button updateButton = new Button("Update Movie");
        Button deleteButton = new Button("Delete Movie");
        Button cancelButton = new Button("Cancel");

        updateButton.setPrefWidth(150);
        deleteButton.setPrefWidth(150);
        cancelButton.setPrefWidth(150);

        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        cancelButton.setStyle(buttonStyle);

        addHoverEffect(updateButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(deleteButton, buttonStyle, buttonHoverStyle);
        addHoverEffect(cancelButton, buttonStyle, buttonHoverStyle);

        updateButton.setOnAction(e -> {
            optionsStage.close();
            showUpdateMovieDialog(selectedMovie);
        });

        deleteButton.setOnAction(e -> {
            optionsStage.close();
            showDeleteMovieDialog(selectedMovie);
        });

        cancelButton.setOnAction(e -> optionsStage.close());

        HBox buttonsBox = new HBox(10);
        buttonsBox.setPadding(new Insets(20));
        buttonsBox.getChildren().addAll(updateButton, deleteButton, cancelButton);

        buttonsBox.setStyle(
                "-fx-background-color: #c0c0c0;" +
                        "-fx-alignment: center;"
        );

        Scene scene = new Scene(buttonsBox);
        optionsStage.setScene(scene);

        optionsStage.show();
    }



    private final String radioButtonStyle =
            "-fx-font-size: 14px;" +
                    "-fx-text-fill: #2c3e50;" +
                    "-fx-font-family: 'Segoe UI';";

    private final String radioButtonSelectedStyle =
            "-fx-font-size: 14px;" +
                    "-fx-text-fill: #2980b9;" +
                    "-fx-font-family: 'Segoe UI';";

    private final String textFieldStyle =
            "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-text-fill: #2c3e50;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-border-color: #bdc3c7;" +
                    "-fx-border-width: 1px;" +
                    "-fx-padding: 5px;";

    private final String textFieldFocusedStyle =
            "-fx-font-size: 14px;" +
                    "-fx-font-family: 'Segoe UI';" +
                    "-fx-text-fill: #2c3e50;" +
                    "-fx-background-radius: 5px;" +
                    "-fx-border-color: #2980b9;" +
                    "-fx-border-width: 1px;" +
                    "-fx-padding: 5px;";

    private final String hboxStyle =
            "-fx-background-color: #f0f0f0;" +
                    "-fx-alignment: center;" +
                    "-fx-padding: 20;" +
                    "-fx-spacing: 10;";

    private final String vboxStyle =
            "-fx-spacing: 15;" +
                    "-fx-padding: 20;";


}
