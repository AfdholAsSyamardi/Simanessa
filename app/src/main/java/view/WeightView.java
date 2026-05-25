package view;

import controller.StudentManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.WeightConfig;

public class WeightView {

    public static void showWindow() {

        Stage stage =
                new Stage();

        // Jarak antar elemen diperlebar agar tidak sesak
        VBox root =
                new VBox(15);

        // Padding (ruang kosong di pinggir) diperbesar
        root.setPadding(
                new Insets(30)
        );

        // Background Dark Mode SIMANESSA
        root.setStyle(
                "-fx-background-color: #0f172a;"
        );

        // =========================
        // JUDUL
        // =========================
        Label title = new Label("Pengaturan Bobot");
        title.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        // =========================
        // FORM UTS
        // =========================
        Label utsLabel =
                new Label(
                        "Bobot UTS (%)"
                );
        utsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        TextField utsField =
                createModernField(
                        String.valueOf(
                                WeightConfig.utsWeight
                        )
                );

        // =========================
        // FORM UAS
        // =========================
        Label uasLabel =
                new Label(
                        "Bobot UAS (%)"
                );
        uasLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        TextField uasField =
                createModernField(
                        String.valueOf(
                                WeightConfig.uasWeight
                        )
                );

        // =========================
        // FORM TUGAS
        // =========================
        Label tugasLabel =
                new Label(
                        "Bobot Semua Tugas (%)"
                );
        tugasLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        TextField tugasField =
                createModernField(
                        String.valueOf(
                                WeightConfig.tugasWeight
                        )
                );

        // =========================
        // TOMBOL SIMPAN
        // =========================
        Button saveButton =
                new Button("Simpan");

        saveButton.setMaxWidth(Double.MAX_VALUE); // Memanjangkan tombol
        saveButton.setStyle(
                "-fx-background-color: #1E6FD9;" + // Primary Blue
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 14;" +
                "-fx-cursor: hand;"
        );

        saveButton.setOnAction(e -> {

            try {

                double uts =
                        Double.parseDouble(
                                utsField.getText()
                        );

                double uas =
                        Double.parseDouble(
                                uasField.getText()
                        );

                double tugas =
                        Double.parseDouble(
                                tugasField.getText()
                        );

                double total =
                        uts + uas + tugas;

                if (total != 100) {

                    showError("Total bobot harus 100%");
                    return;
                }

                // UPDATE BOBOT
                WeightConfig.utsWeight =
                        uts;

                WeightConfig.uasWeight =
                        uas;

                WeightConfig.tugasWeight =
                        tugas;

                // ======================
                // FIX BUG NILAI AKHIR
                // ======================

                StudentManager manager =
                        new StudentManager();

                manager.refreshAllStudentScores();

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setContentText(
                        "Bobot berhasil diperbarui"
                );

                alert.showAndWait();

                stage.close();

            } catch (Exception ex) {

                showError("Input bobot salah");
            }
        });

        root.getChildren().addAll(
                title,
                
                utsLabel,
                utsField,

                uasLabel,
                uasField,

                tugasLabel,
                tugasField,

                saveButton
        );

        // Ukuran Scene diperbesar sedikit agar memuat padding dan desain membulat
        Scene scene =
                new Scene(root, 400, 520);

        stage.setScene(scene);

        stage.setTitle(
                "Pengaturan Bobot"
        );

        stage.showAndWait();
    }

    // =========================
    // STYLE UTILITIES
    // =========================
    private static TextField createModernField(String text) {
        TextField field = new TextField(text);
        field.setStyle(
                "-fx-background-color: #334155;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #94a3b8;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 14;"
        );
        return field;
    }

    private static void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.showAndWait();
    }
}