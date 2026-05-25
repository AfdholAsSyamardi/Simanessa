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

    private BorderPane root = new BorderPane();

    private TableView<Student> table = new TableView<>();

    private StudentManager manager = new StudentManager();

    private ObservableList<Student> data = FXCollections.observableArrayList();

    public DataSiswaView() {
        root.setPadding(new Insets(30));

        root.setStyle("-fx-background-color: #0f172a;");

        applyModernTableStyle();

        refreshTable();

        Label title = new Label("Data Siswa");

        title.setStyle("-fx-text-fill: white;" + "-fx-font-size: 36px;" + "-fx-font-weight: bold;");

        TextField searchField = new TextField();

        searchField.setPromptText("Cari NIS atau Nama");

        searchField.setStyle(getModernFieldStyle());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            ObservableList<Student> filtered = FXCollections.observableArrayList();

            for (Student s : manager.getStudents()) {
                if (s.getNama().toLowerCase().contains(newVal.toLowerCase()) || s.getNis().contains(newVal)) {
                    filtered.add(s);
                }
            }
            table.setItems(filtered);
        });

        createColumns();

        table.setItems(data);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Student student = row.getItem();
                    EditStudentView editView = new EditStudentView(student);
                    Stage stage = new Stage();
                    Scene scene = new Scene(editView.getView(),600,800);

                    stage.setScene(scene);
                    stage.setTitle("Edit Siswa");
                    stage.showAndWait();

                    refreshTable();
                    createColumns();
                    table.setItems(data);
                }
            });
            return row;
        });

        VBox top = new VBox(25);

        top.getChildren().addAll(title, searchField);

        root.setTop(top);

        BorderPane.setMargin(table, new Insets(20, 0, 0, 0));
        root.setCenter(table);
    }

    private void refreshTable() {
        data.clear();
        data.addAll(manager.getStudents());
    }

    private void createColumns() {
        table.getColumns().clear();

        TableColumn<Student, String> nisCol = new TableColumn<>("NIS");

        nisCol.setCellValueFactory(new PropertyValueFactory<>("nis"));

        nisCol.setComparator((nis1, nis2) -> {
            try {
                Long num1 = Long.parseLong(nis1);
                Long num2 = Long.parseLong(nis2);
                return num1.compareTo(num2);
            } catch (NumberFormatException e) {
                return nis1.compareTo(nis2);
            }
        });

        table.getColumns().add(nisCol);

        TableColumn<Student, String> namaCol = new TableColumn<>("Nama");

        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));

        table.getColumns().add(namaCol);

        TableColumn<Student, Double> utsCol = new TableColumn<>("UTS");

        utsCol.setCellValueFactory(new PropertyValueFactory<>("uts"));

        table.getColumns().add(utsCol);

        TableColumn<Student, Double> uasCol = new TableColumn<>("UAS");

        uasCol.setCellValueFactory(new PropertyValueFactory<>("uas"));

        table.getColumns().add(uasCol);

        int maxTugas = 0;

        for (Student s : data) {
            if (s.getTugasList().size() > maxTugas) {
                maxTugas = s.getTugasList().size();
            }
        }

        for (int i = 0; i < maxTugas; i++) {
            final int index = i;

            TableColumn<Student, String> tugasCol = new TableColumn<>("Tugas " + (i + 1));

            tugasCol.setCellValueFactory(cell -> {
                ArrayList<Double> tugas = cell.getValue().getTugasList();

                if (index < tugas.size()) {
                    return new SimpleStringProperty(String.valueOf(tugas.get(index)));
                }

                return new SimpleStringProperty("0");
            });

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

        TableColumn<Student, Double> akhirCol = new TableColumn<>("Nilai Akhir");

        akhirCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAkhir"));

        table.getColumns().add(akhirCol);

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().add(statusCol);

        TableColumn<Student, Void> deleteCol = new TableColumn<>("Hapus");

        deleteCol.setCellFactory(param ->
                new TableCell<>() {
                    private final Button deleteBtn = new Button("🗑");
                    {deleteBtn.setStyle("-fx-background-color: #dc2626;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 8;" + "-fx-padding: 6 12;"+ "-fx-cursor: hand;");
                        deleteBtn.setOnAction(event -> {Student student = getTableView().getItems().get(getIndex());
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

                            confirm.setHeaderText("Hapus Siswa");

                            confirm.setContentText("Yakin ingin menghapus siswa ini?");

                            confirm.showAndWait();

                            if (confirm.getResult() == ButtonType.OK) {
                                manager.deleteStudent(student);
                                refreshTable();
                                createColumns();
                                table.setItems(data);
                            }
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                                setGraphic(null);
                        } else { 
                            setGraphic(deleteBtn);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    }
                });
        table.getColumns().add(deleteCol);
    }

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

    private String getModernFieldStyle() {
        return "-fx-background-color: #334155;" + "-fx-text-fill: white;" + "-fx-prompt-text-fill: #94a3b8;" + "-fx-background-radius: 12;" + "-fx-padding: 14;";
    }

    public Parent getView() {
        return root;
    }
}