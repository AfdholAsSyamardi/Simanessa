package view;

import controller.StudentManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
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
    
    private VBox tugasBox = new VBox(10);
    private ArrayList<TextField> tugasFields = new ArrayList<>();

    public DataSiswaView() {
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        applyModernTableStyle();
        refreshTable();

        // HEADER KIRI (JUDUL & SEARCH)
        VBox leftContent = new VBox(20);
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Data Siswa");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Cari NIS atau Nama...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 10 15; -fx-border-color: #334155; -fx-border-radius: 8;");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            ObservableList<Student> filtered = FXCollections.observableArrayList();
            for (Student s : manager.getStudents()) {
                if (s.getNama().toLowerCase().contains(newVal.toLowerCase()) || s.getNis().contains(newVal)) {
                    filtered.add(s);
                }
            }
            
            // PRE-SORT PENCARIAN BERDASARKAN NIS
            FXCollections.sort(filtered, (s1, s2) -> {
                try { return Long.valueOf(s1.getNis()).compareTo(Long.valueOf(s2.getNis())); } 
                catch (NumberFormatException e) { return s1.getNis().compareTo(s2.getNis()); }
            });
            
            table.setItems(filtered);
        });

        headerBox.getChildren().addAll(title, searchField);

        // TABLE SETTINGS
        createColumns();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        leftContent.getChildren().addAll(headerBox, table);
        
        // KANAN (FORM INPUT PANEL)
        VBox rightPanel = new VBox(15);
        rightPanel.setPrefWidth(350);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        BorderPane.setMargin(rightPanel, new Insets(0, 0, 0, 20)); 

        Label formTitle = new Label("Form Input Siswa");
        formTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        VBox.setMargin(formTitle, new Insets(0, 0, 10, 0));

        TextField nisField = createField("NIS Siswa");
        TextField namaField = createField("Nama Lengkap");
        TextField kelasField = createField("Kelas");
        
        HBox nilaiBox = new HBox(10);
        TextField utsField = createField("Nilai UTS");
        TextField uasField = createField("Nilai UAS");
        HBox.setHgrow(utsField, Priority.ALWAYS);
        HBox.setHgrow(uasField, Priority.ALWAYS);
        nilaiBox.getChildren().addAll(utsField, uasField);

        // BUTTONS
        Button tambahTugasBtn = new Button("+ Tambah Tugas");
        tambahTugasBtn.setStyle(secondaryButton());
        tambahTugasBtn.setMaxWidth(Double.MAX_VALUE);
        tambahTugasBtn.setOnAction(event -> {
            TextField tugasField = createField("Nilai Tugas " + (tugasFields.size() + 1));
            tugasFields.add(tugasField);
            tugasBox.getChildren().add(tugasField);
        });

        Button tambahBtn = new Button("Simpan Data Siswa");
        tambahBtn.setStyle(primaryButton()); 
        tambahBtn.setMaxWidth(Double.MAX_VALUE);
        tambahBtn.setOnAction(event -> {
            try {
                String nis = nisField.getText();
                String nama = namaField.getText();

                if (!nis.matches("\\d+")) { AlertHelper.showError("NIS harus angka"); return; }
                if (nama.matches(".*\\d.*")) { AlertHelper.showError("Nama tidak boleh mengandung angka"); return; }
                if (manager.isDuplicate(nis, nama)) { AlertHelper.showError("NIS atau Nama sudah ada"); return; }

                double uts = Double.parseDouble(utsField.getText());
                double uas = Double.parseDouble(uasField.getText());

                if (uts < 0 || uts > 100 || uas < 0 || uas > 100) { AlertHelper.showError("Nilai harus 0 - 100"); return; }

                ArrayList<Double> tugasList = new ArrayList<>();
                for (TextField tf : tugasFields) {
                    String text = tf.getText();
                    if (text.isEmpty()) {
                        tugasList.add(0.0);
                    } else {
                        double nilai = Double.parseDouble(text);
                        if (nilai < 0 || nilai > 100) { AlertHelper.showError("Nilai harus 0 - 100"); return; }
                        tugasList.add(nilai);
                    }
                }

                double akhir = manager.calculateFinalScore(uts, uas, tugasList);
                Student student = new Student(nis, nama, kelasField.getText(), uts, uas, tugasList, akhir);

                manager.addStudent(student);
                manager.loadData();
                
                refreshTable();
                createColumns();
                table.setItems(data);

                // CLEAR FORM
                nisField.clear(); namaField.clear(); kelasField.clear(); utsField.clear(); uasField.clear();
                tugasFields.clear(); tugasBox.getChildren().clear();

            } catch (Exception ex) {
                AlertHelper.showError("Input tidak valid atau ada kolom kosong.");
            }
        });

        Button bobotBtn = new Button("⚙ Atur Bobot Global");
        bobotBtn.setStyle(secondaryButton());
        bobotBtn.setMaxWidth(Double.MAX_VALUE);
        bobotBtn.setOnAction(event -> {
            WeightView.showWindow();
            manager.refreshAllStudentScores();
            refreshTable();
            createColumns();
            table.setItems(data);
        });

        Button pdfBtn = new Button("📄 Export Data ke PDF");
        pdfBtn.setStyle(secondaryButton());
        pdfBtn.setMaxWidth(Double.MAX_VALUE);
        pdfBtn.setOnAction(event -> {
            manager.exportPDF();
            AlertHelper.showInfo("Export Berhasil", "Data berhasil di-export ke dalam file PDF!");
        });

        // MASUKKAN KE PANEL KANAN
        ScrollPane formScroll = new ScrollPane();
        VBox innerForm = new VBox(15);
        innerForm.getChildren().addAll(
                new Label("Data Identitas"), nisField, namaField, kelasField,
                new Label("Nilai Ujian"), nilaiBox,
                new Label("Nilai Tugas"), tambahTugasBtn, tugasBox,
                new Region(), tambahBtn, bobotBtn, pdfBtn 
        );
        innerForm.setStyle("-fx-background-color: transparent;");
        
        for (javafx.scene.Node node : innerForm.getChildren()) {
            if (node instanceof Label) {
                node.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        }

        formScroll.setContent(innerForm);
        formScroll.setFitToWidth(true);
        formScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(formScroll, Priority.ALWAYS);

        rightPanel.getChildren().addAll(formTitle, formScroll);

        root.setCenter(leftContent);
        root.setRight(rightPanel);

        // EVENT DOUBLE CLICK EDIT
        table.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Student student = row.getItem();
                    EditStudentView editView = new EditStudentView(student);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(editView.getView(), 600, 800));
                    stage.setTitle("Edit Siswa");
                    stage.showAndWait();

                    refreshTable();
                    createColumns();
                    table.setItems(data);
                }
            });
            return row;
        });
    }

    private void refreshTable() {
        data.clear();
        data.addAll(manager.getStudents());
        
        // PRE-SORT DATA BERDASARKAN NIS
        FXCollections.sort(data, (s1, s2) -> {
            try { return Long.valueOf(s1.getNis()).compareTo(Long.valueOf(s2.getNis())); } 
            catch (NumberFormatException e) { return s1.getNis().compareTo(s2.getNis()); }
        });
    }

    private void createColumns() {
        table.getColumns().clear();

        TableColumn<Student, String> nisCol = new TableColumn<>("NIS");
        nisCol.setCellValueFactory(new PropertyValueFactory<>("nis"));
        nisCol.setPrefWidth(60);
        
        nisCol.setComparator((nis1, nis2) -> {
            try { return Long.valueOf(nis1).compareTo(Long.valueOf(nis2)); } 
            catch (NumberFormatException e) { return nis1.compareTo(nis2); }
        });

        TableColumn<Student, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaCol.setPrefWidth(130);

        // PERBAIKAN: Menambahkan kembali Kolom Kelas
        TableColumn<Student, String> kelasCol = new TableColumn<>("Kelas");
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        kelasCol.setPrefWidth(70);

        TableColumn<Student, Double> utsCol = new TableColumn<>("UTS");
        utsCol.setCellValueFactory(new PropertyValueFactory<>("uts"));
        utsCol.setPrefWidth(60);

        TableColumn<Student, Double> uasCol = new TableColumn<>("UAS");
        uasCol.setCellValueFactory(new PropertyValueFactory<>("uas"));
        uasCol.setPrefWidth(60);

        // Memasukkan kelasCol ke dalam tabel
        table.getColumns().addAll(nisCol, namaCol, kelasCol, utsCol, uasCol);

        int maxTugas = 0;
        for (Student s : data) {
            if (s.getTugasList().size() > maxTugas) maxTugas = s.getTugasList().size();
        }

        for (int i = 0; i < maxTugas; i++) {
            final int index = i;
            TableColumn<Student, String> tugasCol = new TableColumn<>("Tugas " + (i + 1));
            tugasCol.setPrefWidth(70);
            tugasCol.setCellValueFactory(cell -> {
                ArrayList<Double> tugas = cell.getValue().getTugasList();
                if (index < tugas.size()) return new SimpleStringProperty(String.valueOf(tugas.get(index)));
                return new SimpleStringProperty("-");
            });
            tugasCol.setComparator((t1, t2) -> {
                try { return Double.valueOf(t1).compareTo(Double.valueOf(t2)); } 
                catch (NumberFormatException e) { return t1.compareTo(t2); }
            });
            table.getColumns().add(tugasCol);
        }

        TableColumn<Student, Double> akhirCol = new TableColumn<>("Akhir");
        akhirCol.setCellValueFactory(new PropertyValueFactory<>("nilaiAkhir"));
        akhirCol.setPrefWidth(70);

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(70);

        TableColumn<Student, Void> deleteCol = new TableColumn<>("Aksi");
        deleteCol.setPrefWidth(60);
        deleteCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            {
                deleteBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    if (AlertHelper.showConfirm("Konfirmasi Hapus", "Yakin hapus data siswa ini secara permanen?")) {
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
                if (empty) { setGraphic(null); } 
                else { setGraphic(deleteBtn); setStyle("-fx-alignment: CENTER;"); }
            }
        });

        table.getColumns().addAll(akhirCol, statusCol, deleteCol);
    }

    private void applyModernTableStyle() {
        try {
            File cssFile = new File("modern_table.css");
            if (!cssFile.exists()) {
                PrintWriter writer = new PrintWriter(cssFile);
                writer.println(".table-view { -fx-background-color: #1e293b; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-padding: 10px; }");
                writer.println(".table-view .column-header-background { -fx-background-color: transparent; }");
                writer.println(".table-view .column-header, .table-view .filler { -fx-background-color: transparent; -fx-border-width: 0 0 2 0; -fx-border-color: #334155; -fx-size: 40px; }");
                writer.println(".table-view .column-header .label { -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 13px; }");
                writer.println(".table-view .table-row-cell { -fx-background-color: #1e293b; -fx-border-width: 0 0 1 0; -fx-border-color: #334155; -fx-cell-size: 45px; }");
                writer.println(".table-view .table-row-cell:empty { -fx-background-color: #1e293b; -fx-border-color: transparent; }");
                writer.println(".table-view .table-row-cell:hover { -fx-background-color: #334155; }");
                writer.println(".table-view .table-cell { -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; }");
                writer.println(".table-view .scroll-bar:horizontal, .table-view .scroll-bar:vertical { -fx-background-color: transparent; }");
                writer.println(".table-view .scroll-bar:horizontal .thumb, .table-view .scroll-bar:vertical .thumb { -fx-background-color: #475569; -fx-background-radius: 5px; }");
                writer.close();
            }
            root.getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 12;");
        return field;
    }

    private String primaryButton() {
        return "-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;";
    }
    
    private String secondaryButton() {
        return "-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand; -fx-border-color: #475569; -fx-border-radius: 8;";
    }

    public Parent getView() {
        return root;
    }
}