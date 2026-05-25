package view;

import controller.StudentManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Student;

import java.util.ArrayList;

public class EditStudentView {

    // =========================
    // ATTRIBUTE
    // =========================

    private VBox root =
            new VBox(20);

    private Student student;

    private StudentManager manager =
            new StudentManager();

    private int tugasCount = 0;

    // =========================
    // CONSTRUCTOR
    // =========================

    public EditStudentView(
            Student student
    ) {

        this.student = student;

        buildUI();
    }

    // =========================
    // BUILD UI
    // =========================

    private void buildUI() {

        root.setPadding(
                new Insets(30)
        );

        root.setStyle(
                "-fx-background-color: #0f172a;"
        );

        // =========================
        // TITLE
        // =========================

        Label title =
                new Label(
                        "Edit Data Siswa"
                );

        title.setStyle(
                "-fx-text-fill: white;"
                        +
                        "-fx-font-size: 32px;"
                        +
                        "-fx-font-weight: bold;"
        );

        // =========================
        // FIELD
        // =========================

        TextField nisField =
                createField(
                        student.getNis()
                );

        nisField.setPromptText(
                "NIS"
        );

        TextField namaField =
                createField(
                        student.getNama()
                );

        namaField.setPromptText(
                "Nama"
        );

        TextField kelasField =
                createField(
                        student.getKelas()
                );

        kelasField.setPromptText(
                "Kelas"
        );

        TextField utsField =
                createField(
                        String.valueOf(
                                student.getUts()
                        )
                );

        utsField.setPromptText(
                "UTS"
        );

        TextField uasField =
                createField(
                        String.valueOf(
                                student.getUas()
                        )
                );

        uasField.setPromptText(
                "UAS"
        );

        // =========================
        // TUGAS (MENGGUNAKAN GRIDPANE UNTUK TATA LETAK SEJAJAR)
        // =========================

        GridPane tugasBox =
                new GridPane();
        tugasBox.setHgap(15);
        tugasBox.setVgap(15);
        tugasBox.setAlignment(Pos.CENTER_LEFT);

        ArrayList<TextField> tugasFields =
                new ArrayList<>();

        for (Double tugas :
                student.getTugasList()) {

            final int currentCount = tugasCount;

            TextField tugasField =
                    createField(
                            String.valueOf(
                                    tugas
                            )
                    );

            tugasField.setPromptText(
                    "Nilai Tugas"
            );

            tugasField.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(tugasField, Priority.ALWAYS);

            tugasFields.add(
                    tugasField
            );

            Button hapusButton =
                    new Button("Hapus");

            hapusButton.setStyle(
                    deleteButtonStyle()
            );

            hapusButton.setOnAction(e -> {

                tugasBox.getChildren()
                        .remove(tugasField);
                tugasBox.getChildren()
                        .remove(hapusButton);

                tugasFields.remove(
                        tugasField
                );
            });

            tugasBox.add(tugasField, 0, currentCount);
            tugasBox.add(hapusButton, 1, currentCount);

            tugasCount++;
        }

        // =========================
        // BUTTON TAMBAH TUGAS
        // =========================

        Button tambahTugasButton =
                new Button(
                        "+ Tambah Tugas"
                );

        tambahTugasButton.setStyle(
                buttonStyle()
        );

        tambahTugasButton.setMaxWidth(
                Double.MAX_VALUE
        );

        tambahTugasButton.setOnAction(e -> {

            final int currentCount = tugasCount;

            TextField tugasField =
                    createField("");

            tugasField.setPromptText(
                    "Nilai Tugas Baru"
            );

            tugasField.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(tugasField, Priority.ALWAYS);

            tugasFields.add(
                    tugasField
            );

            Button hapusButton =
                    new Button("Hapus");

            hapusButton.setStyle(
                    deleteButtonStyle()
            );

            hapusButton.setOnAction(ev -> {

                tugasBox.getChildren()
                        .remove(tugasField);
                tugasBox.getChildren()
                        .remove(hapusButton);

                tugasFields.remove(
                        tugasField
                );
            });

            tugasBox.add(tugasField, 0, currentCount);
            tugasBox.add(hapusButton, 1, currentCount);

            tugasCount++;
        });

        // =========================
        // BUTTON SIMPAN
        // =========================

        Button saveButton =
                new Button(
                        "Simpan"
                );

        saveButton.setStyle(
                buttonStyle()
        );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setOnAction(e -> {

            try {

                String nis =
                        nisField.getText();

                String nama =
                        namaField.getText();

                // =========================
                // VALIDASI NIS
                // =========================

                if (!nis.matches("\\d+")) {

                    showError(
                            "NIS harus angka"
                    );

                    return;
                }

                // =========================
                // VALIDASI NAMA
                // =========================

                if (
                        nama.matches(".*\\d.*")
                ) {

                    showError(
                            "Nama tidak boleh angka"
                    );

                    return;
                }

                double uts =
                        Double.parseDouble(
                                utsField.getText()
                        );

                double uas =
                        Double.parseDouble(
                                uasField.getText()
                        );

                // =========================
                // VALIDASI NILAI
                // =========================

                if (

                        uts < 0
                                ||
                                uts > 100
                                ||
                                uas < 0
                                ||
                                uas > 100

                ) {

                    showError(
                            "Nilai harus 0 - 100"
                    );

                    return;
                }

                // TUGAS
                ArrayList<Double> tugasList =
                        new ArrayList<>();

                for (TextField tf :
                        tugasFields) {

                    double nilai =
                            Double.parseDouble(
                                    tf.getText()
                            );

                    if (
                            nilai < 0
                                    ||
                                    nilai > 100
                    ) {

                        showError(
                                "Nilai harus 0 - 100"
                        );

                        return;
                    }

                    tugasList.add(nilai);
                }

                // HITUNG NILAI
                double akhir =
                        manager.calculateFinalScore(

                                uts,
                                uas,
                                tugasList
                        );

                
                // UPDATE STUDENT

                Student updated =
                        new Student(

                                nis,
                                nama,
                                kelasField.getText(),

                                uts,
                                uas,

                                tugasList,

                                akhir
                        );

                manager.updateStudent(
                        student,
                        updated
                );

                manager.refreshAllStudentScores();

         
                // SUCCESS
            

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setContentText(
                        "Data berhasil diupdate"
                );

                alert.showAndWait();

                // CLOSE WINDOW

                Stage stage =
                        (Stage)
                                root.getScene()
                                        .getWindow();

                stage.close();

            } catch (Exception ex) {

                showError(
                        "Input salah"
                );
            }
        });


        // LAYOUT


        root.getChildren().addAll(

                title,

                nisField,
                namaField,
                kelasField,

                utsField,
                uasField,

                tambahTugasButton,

                tugasBox,

                saveButton
        );
    }

    // FIELD STYLE

    private TextField createField(
            String text
    ) {

        TextField field =
                new TextField(text);

        field.setStyle(

                "-fx-background-color: #334155;"
                        +
                        "-fx-text-fill: white;"
                        +
                        "-fx-prompt-text-fill: #94a3b8;"
                        +
                        "-fx-background-radius: 12;"
                        +
                        "-fx-padding: 14;"
        );

        return field;
    }

    // BUTTON STYLE

    private String buttonStyle() {

        return

                "-fx-background-color: #38bdf8;"
                        +
                        "-fx-text-fill: white;"
                        +
                        "-fx-font-size: 15px;"
                        +
                        "-fx-font-weight: bold;"
                        +
                        "-fx-background-radius: 12;"
                        +
                        "-fx-padding: 14;";
    }

    private String deleteButtonStyle() {
        return
                "-fx-background-color: #dc2626;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 16;" +
                        "-fx-cursor: hand;";
    }

    // =========================
    // ERROR

    private void showError(
            String text
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setContentText(
                text
        );

        alert.showAndWait();
    }

    // GET VIEW (PERBAIKAN BACKGROUND PUTIH DISINI)

    public Parent getView() {

        ScrollPane scrollPane =
                new ScrollPane(root);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);


        scrollPane.setStyle(
                "-fx-background: #0f172a;" +
                "-fx-background-color: #0f172a;" +
                "-fx-border-color: #0f172a;"
        );

        return scrollPane;
    }
}