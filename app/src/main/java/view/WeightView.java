package view;

import controller.StudentManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.WeightConfig;

public class WeightView {

    public static void showWindow() {
        Stage stage = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        Label title = new Label("Pengaturan Bobot");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        Label utsLabel = new Label("Bobot UTS (%)");
        utsLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField utsField = createModernField(String.valueOf(WeightConfig.utsWeight));

        Label uasLabel = new Label("Bobot UAS (%)");
        uasLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField uasField = createModernField(String.valueOf(WeightConfig.uasWeight));

        Label tugasLabel = new Label("Bobot Semua Tugas (%)");
        tugasLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField tugasField = createModernField(String.valueOf(WeightConfig.tugasWeight));

        Button saveButton = new Button("Simpan Bobot");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 14; -fx-cursor: hand;");

        saveButton.setOnAction(e -> {
            try {
                double uts = Double.parseDouble(utsField.getText());
                double uas = Double.parseDouble(uasField.getText());
                double tugas = Double.parseDouble(tugasField.getText());
                
                if (uts + uas + tugas != 100) {
                    AlertHelper.showError("Total bobot UTS, UAS, dan Tugas harus pas 100%");
                    return;
                }

                WeightConfig.utsWeight = uts;
                WeightConfig.uasWeight = uas;
                WeightConfig.tugasWeight = tugas;

                StudentManager manager = new StudentManager();
                manager.refreshAllStudentScores();

                // PERBAIKAN: Menggunakan AlertHelper
                AlertHelper.showInfo("Sukses", "Konfigurasi bobot berhasil diperbarui!");
                stage.close();
            } catch (Exception ex) {
                AlertHelper.showError("Input bobot salah. Pastikan menggunakan format angka.");
            }
        });

        root.getChildren().addAll(title, utsLabel, utsField, uasLabel, uasField, tugasLabel, tugasField, saveButton);
        Scene scene = new Scene(root, 400, 520);
        stage.setScene(scene);
        stage.setTitle("Pengaturan Bobot");
        stage.showAndWait();
    }

    private static TextField createModernField(String text) {
        TextField field = new TextField(text);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 12; -fx-padding: 14;");
        return field;
    }
}