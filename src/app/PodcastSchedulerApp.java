package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PodcastSchedulerApp extends Application {

    private TextField titleField, durationField, timeField;
    private ComboBox<String> typeBox;
    private DatePicker datePicker;
    private ListView<Episode> listView;
    private Label statusLabel;

    private final EpisodeRepository repo = new EpisodeRepository();

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ---------- HEADER ----------
        Label header = new Label("🎙 Podcast Episode Scheduler");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        root.setTop(header);

        // ---------- FORM ----------
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        titleField = new TextField();
        durationField = new TextField();
        datePicker = new DatePicker();
        timeField = new TextField();
        timeField.setPromptText("HH:mm");

        typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Regular", "Bonus");
        typeBox.setValue("Regular");

        Button createBtn = new Button("Create & Schedule");

        form.add(new Label("Title:"), 0, 0);
        form.add(titleField, 1, 0);

        form.add(new Label("Duration (min):"), 0, 1);
        form.add(durationField, 1, 1);

        form.add(new Label("Date:"), 0, 2);
        form.add(datePicker, 1, 2);

        form.add(new Label("Time:"), 0, 3);
        form.add(timeField, 1, 3);

        form.add(new Label("Type:"), 0, 4);
        form.add(typeBox, 1, 4);

        form.add(createBtn, 1, 5);

        root.setLeft(form);

        // ---------- CENTER LIST ----------
        listView = new ListView<>();
        root.setCenter(listView);

        // ---------- BOTTOM CONTROLS ----------
        Button publishBtn = new Button("Publish");
        Button saveBtn = new Button("Save");
        Button loadBtn = new Button("Load");

        publishBtn.disableProperty().bind(
                listView.getSelectionModel().selectedItemProperty().isNull()
        );

        HBox buttons = new HBox(10, publishBtn, saveBtn, loadBtn);
        buttons.setAlignment(Pos.CENTER);

        statusLabel = new Label("Ready.");
        statusLabel.setStyle("-fx-font-weight: bold;");

        VBox bottomBox = new VBox(10, buttons, statusLabel);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        // ---------- ACTIONS ----------
        createBtn.setOnAction(e -> createEpisode());
        publishBtn.setOnAction(e -> publishEpisode());
        saveBtn.setOnAction(e -> saveEpisodes());
        loadBtn.setOnAction(e -> loadEpisodes());

        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        statusLabel.setText(
                                "Selected: " + newVal.getTitle() +
                                " [" + newVal.getTypeLabel() + "]"
                        );
                    }
                }
        );

        // ---------- SCENE ----------
        Scene scene = new Scene(root, 950, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Podcast Scheduler");
        stage.show();
    }

    // ---------- LOGIC METHODS ----------

    private void createEpisode() {
        try {
            String title = titleField.getText().trim();
            int duration = Integer.parseInt(durationField.getText().trim());
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeField.getText().trim());

            Episode ep = repo.createEpisode(typeBox.getValue(), title, duration);
            repo.scheduleEpisode(ep, LocalDateTime.of(date, time));

            refreshList();
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Episode created and scheduled successfully.");

            titleField.clear();
            durationField.clear();
            timeField.clear();
            datePicker.setValue(null);

        } catch (ScheduleConflictException ex) {
            showAlert(Alert.AlertType.ERROR, "Schedule Conflict",
                    "Another episode is already scheduled at this time.");
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Scheduling failed due to conflict.");

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Please check all input fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Failed to create episode.");
        }
    }

    private void publishEpisode() {
        Episode selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.publish(LocalDateTime.now());
            listView.refresh();

            showAlert(Alert.AlertType.INFORMATION, "Published",
                    "Episode published successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Episode published.");
        }
    }

    private void saveEpisodes() {
        try {
            repo.saveToFile("episodes.txt");
            showAlert(Alert.AlertType.INFORMATION, "Saved",
                    "Episodes saved successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Episodes saved to file.");

        } catch (EpisodePersistenceException e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void loadEpisodes() {
        try {
            repo.loadFromFile("episodes.txt");
            refreshList();

            showAlert(Alert.AlertType.INFORMATION, "Loaded",
                    "Episodes loaded successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Episodes loaded.");

        } catch (EpisodePersistenceException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void refreshList() {
        listView.getItems().setAll(repo.getEpisodes());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
