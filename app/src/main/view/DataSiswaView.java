package view;

import controller.StudentManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataSiswaView {

    private BorderPane root =
            new BorderPane();

    private TableView<Student> table =
            new TableView<>();

    private StudentManager manager =
            new StudentManager();

    private ObservableList<Student> data =
            FXCollections.observableArrayList();

    public DataSiswaView() {

        root.setPadding(
                new Insets(30)
        );

        root.setStyle(
                "-fx-background-color: #0f172a;"
        );

        // Menerapkan gaya tabel modern menggunakan CSS yang di-generate otomatis
        applyModernTableStyle();

        refreshTable();

        // =========================
        // TITLE
        // =========================

        Label title =
                new Label(
                        "Data Siswa"
                );

        title.setStyle(
                "-fx-text-fill: white;"
                        +
                        "-fx-font-size: 36px;"
                        +
                        "-fx-font-weight: bold;"
        );

        // =========================
        // SEARCH
        // =========================

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Cari NIS atau Nama"
        );

        searchField.setStyle(getModernFieldStyle());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            ObservableList<Student> filtered =
                    FXCollections.observableArrayList();

            for (Student s :
                    manager.getStudents()) {

                if (

                        s.getNama()
                                .toLowerCase()
                                .contains(
                                        newVal.toLowerCase()
                                )

                                ||

                                s.getNis()
                                        .contains(newVal)

                ) {

                    filtered.add(s);
                }
            }

            table.setItems(filtered);
        });

        // =========================
        // TABLE SETTINGS
        // =========================

        createColumns();

        table.setItems(data);
        
        // Memaksa kolom untuk mengisi lebar tabel tanpa menyisakan ruang kosong di kanan
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // =========================
        // DOUBLE CLICK EDIT
        // =========================

        table.setRowFactory(tv -> {

            TableRow<Student> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (

                        event.getClickCount() == 2
                                &&
                                !row.isEmpty()

                ) {

                    Student student =
                            row.getItem();

                    EditStudentView editView =
                            new EditStudentView(student);

                    Stage stage =
                            new Stage();

                    Scene scene =
                            new Scene(
                                    editView.getView(),
                                    600,
                                    800
                            );

                    stage.setScene(scene);

                    stage.setTitle(
                            "Edit Siswa"
                    );

                    stage.showAndWait();

                    refreshTable();
                    createColumns();
                    table.setItems(data);
                }
            });

            return row;
        });

        VBox top =
                new VBox(25);

        top.getChildren().addAll(
                title,
                searchField
        );

        root.setTop(top);

        // Memberikan margin atas pada tabel agar tidak menempel ke kolom search
        BorderPane.setMargin(table, new Insets(20, 0, 0, 0));
        root.setCenter(table);
    }

    // =========================
    // REFRESH TABLE
    // =========================

    private void refreshTable() {

        data.clear();

        data.addAll(
                manager.getStudents()
        );
    }

    // =========================
    // CREATE COLUMNS
    // =========================

    private void createColumns() {

        table.getColumns().clear();

        // =========================
        // NIS
        // =========================

        TableColumn<Student, String> nisCol =
                new TableColumn<>("NIS");

        nisCol.setCellValueFactory(
                new PropertyValueFactory<>("nis")
        );

        // ========================================================
        // PERBAIKAN BUG SORTING NIS (Custom Comparator)
        // Mengubah teks (String) menjadi angka (Long) secara internal 
        // khusus untuk keperluan pengurutan agar berurut 1, 2, 10, 11, dst.
        // ========================================================
        nisCol.setComparator((nis1, nis2) -> {
            try {
                Long num1 = Long.parseLong(nis1);
                Long num2 = Long.parseLong(nis2);
                return num1.compareTo(num2);
            } catch (NumberFormatException e) {
                // Jika entah bagaimana datanya bukan angka, kembali ke sorting teks biasa
                return nis1.compareTo(nis2);
            }
        });

        table.getColumns().add(nisCol);

        // =========================
        // NAMA
        // =========================

        TableColumn<Student, String> namaCol =
                new TableColumn<>("Nama");

        namaCol.setCellValueFactory(
                new PropertyValueFactory<>("nama")
        );

        table.getColumns().add(namaCol);

        // =========================
        // UTS
        // =========================

        TableColumn<Student, Double> utsCol =
                new TableColumn<>("UTS");

        utsCol.setCellValueFactory(
                new PropertyValueFactory<>("uts")
        );

        table.getColumns().add(utsCol);

        // =========================
        // UAS
        // =========================

        TableColumn<Student, Double> uasCol =
                new TableColumn<>("UAS");

        uasCol.setCellValueFactory(
                new PropertyValueFactory<>("uas")
        );

        table.getColumns().add(uasCol);

        // =========================
        // TUGAS
        // =========================

        int maxTugas = 0;

        for (Student s : data) {

            if (
                    s.getTugasList().size()
                            > maxTugas
            ) {

                maxTugas =
                        s.getTugasList().size();
            }
        }

        for (int i = 0; i < maxTugas; i++) {

            final int index = i;

            TableColumn<Student, String> tugasCol =
                    new TableColumn<>(
                            "Tugas " + (i + 1)
                    );

            tugasCol.setCellValueFactory(cell -> {

                ArrayList<Double> tugas =
                        cell.getValue()
                                .getTugasList();

                if (index < tugas.size()) {

                    return new SimpleStringProperty(

                            String.valueOf(
                                    tugas.get(index)
                            )
                    );
                }

                return new SimpleStringProperty("0");
            });

            // Perbaikan Sorting Tugas (Agar sorting tugas juga numerik)
            tugasCol.setComparator((tugas1, tugas2) -> {
                try {
                    Double num1 = Double.parseDouble(tugas1);
                    Double num2 = Double.parseDouble(tugas2);
                    return num1.compareTo(num2);
                } catch (NumberFormatException e) {
                    return tugas1.compareTo(tugas2);
                }
            });

            table.getColumns().add(
                    tugasCol
            );
        }

        // =========================
        // NILAI AKHIR
        // =========================

        TableColumn<Student, Double> akhirCol =
                new TableColumn<>("Nilai Akhir");

        akhirCol.setCellValueFactory(
                new PropertyValueFactory<>("nilaiAkhir")
        );

        table.getColumns().add(akhirCol);

        // =========================
        // STATUS
        // =========================

        TableColumn<Student, String> statusCol =
                new TableColumn<>("Status");

        statusCol.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        table.getColumns().add(statusCol);

        // =========================
        // HAPUS
        // =========================

        TableColumn<Student, Void> deleteCol =
                new TableColumn<>("Hapus");

        deleteCol.setCellFactory(param ->
                new TableCell<>() {

                    private final Button deleteBtn =
                            new Button("🗑");

                    {

                        deleteBtn.setStyle(

                                "-fx-background-color: #dc2626;"
                                        +
                                        "-fx-text-fill: white;"
                                        +
                                        "-fx-font-weight: bold;"
                                        +
                                        "-fx-background-radius: 8;"
                                        +
                                        "-fx-padding: 6 12;"
                                        +
                                        "-fx-cursor: hand;"
                        );

                        deleteBtn.setOnAction(event -> {

                            Student student =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            Alert confirm =
                                    new Alert(
                                            Alert.AlertType.CONFIRMATION
                                    );

                            confirm.setHeaderText(
                                    "Hapus Siswa"
                            );

                            confirm.setContentText(
                                    "Yakin ingin menghapus siswa ini?"
                            );

                            confirm.showAndWait();

                            if (

                                    confirm.getResult()
                                            == ButtonType.OK

                            ) {

                                manager.deleteStudent(student);
                                refreshTable();
                                createColumns();
                                table.setItems(data);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {
                            
                            setGraphic(deleteBtn);
                            setStyle("-fx-alignment: CENTER;"); // Meratakan tombol ke tengah
                        }
                    }
                });

        table.getColumns().add(deleteCol);
    }

    // =========================
    // INJECT MODERN CSS
    // =========================

    private void applyModernTableStyle() {
        try {
            // Membuat file CSS temporary untuk merombak warna komponen internal TableView
            File cssFile = new File("modern_table.css");
            if (!cssFile.exists()) {
                PrintWriter writer = new PrintWriter(cssFile);
                writer.println(".table-view { -fx-background-color: #1e293b; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-padding: 10px; }");
                writer.println(".table-view .column-header-background { -fx-background-color: transparent; }");
                writer.println(".table-view .column-header, .table-view .filler { -fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: #334155; -fx-size: 50px; }");
                writer.println(".table-view .column-header .label { -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 14px; }");
                writer.println(".table-view .table-row-cell { -fx-background-color: #1e293b; -fx-border-width: 0 0 1 0; -fx-border-color: #334155; -fx-cell-size: 50px; }");
                writer.println(".table-view .table-row-cell:empty { -fx-background-color: #1e293b; -fx-border-color: transparent; }");
                writer.println(".table-view .table-row-cell:hover { -fx-background-color: #334155; }");
                writer.println(".table-view .table-cell { -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; }");
                writer.println(".table-view .scroll-bar:horizontal, .table-view .scroll-bar:vertical { -fx-background-color: transparent; }");
                writer.println(".table-view .scroll-bar:horizontal .thumb, .table-view .scroll-bar:vertical .thumb { -fx-background-color: #475569; -fx-background-radius: 5px; }");
                writer.close();
            }
            root.getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // STYLE UTILITIES
    // =========================

    private String getModernFieldStyle() {
        return
                "-fx-background-color: #334155;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #94a3b8;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 14;";
    }

    // =========================
    // GET VIEW
    // =========================

    public Parent getView() {

        return root;
    }
}