package view;

import app.Main;
import controller.StudentManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Student;
import model.User;

import java.util.ArrayList;

public class DashboardView {

    private BorderPane root =
            new BorderPane();

    private StudentManager manager =
            new StudentManager();

    private ObservableList<Student> data =
            FXCollections.observableArrayList();

    private VBox tugasBox =
            new VBox(10);

    private ArrayList<TextField> tugasFields =
            new ArrayList<>();

    private Label totalLabel =
            new Label("0");

    private Label lulusLabel =
            new Label("0");

    private Label gagalLabel =
            new Label("0");

    private Label rataLabel =
            new Label("0");

    private ScrollPane scrollPane;

    private Main app; 

    public DashboardView(Stage primaryStage, User user, Main app) {

        this.app = app;

        root.setStyle(
                "-fx-background-color: #0f172a;"
        );

        data.addAll(
                manager.getStudents()
        );

        // =========================
        // SIDEBAR (DARK SLATE HARMONIS)
        // =========================

        VBox sidebar =
                new VBox(15); 

        sidebar.setPadding(
                new Insets(20)
        );

        sidebar.setPrefWidth(260); 

        sidebar.setStyle(
                "-fx-background-color: #1e293b;" +
                "-fx-border-color: transparent #334155 transparent transparent;" +
                "-fx-border-width: 0 1.5 0 0;"
        );

        // =========================
        // LOGO DAN JUDUL SIDEBAR
        // =========================
        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER); 

        try {
            Image img = new Image(getClass().getResource("/images/Logo.jpg").toExternalForm());
            ImageView logoView = new ImageView(img);
            
            logoView.setFitWidth(180); 
            logoView.setPreserveRatio(true);
            logoBox.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan, pastikan path /images/Logo.jpg benar.");
        }

        Label logoText =
                new Label("SIMANESSA");

        logoText.setStyle(
                "-fx-text-fill: #FFFFFF;" + 
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: 900;"
        );
        logoBox.getChildren().add(logoText);

        // =========================
        // ROLE USER
        // =========================
        VBox roleBox = new VBox();
        roleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label roleLabel = new Label("Logged in as:\n" + user.showRole() + " (" + user.getUsername() + ")");
        roleLabel.setStyle(
                "-fx-text-fill: #93C5FD;" + 
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );
        roleBox.getChildren().add(roleLabel);
        VBox.setMargin(roleBox, new Insets(15, 0, 20, 0));

        // =========================
        // TOMBOL NAVIGASI
        // =========================
        Button dashboardBtn =
                createSidebarBoxButton("🏠 Dashboard");

        Button siswaBtn =
                createSidebarBoxButton("📚 Data Siswa");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // =========================
        // TOMBOL LOGOUT
        // =========================
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPadding(new Insets(12, 15, 12, 15));
        
        String logoutNormal = "-fx-background-color: #7F1D1D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand;";
        String logoutHover = "-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand;";
        
        logoutBtn.setStyle(logoutNormal);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHover));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutNormal));

        logoutBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText("Logout");
            confirm.setContentText("Anda yakin ingin keluar dari sistem?");
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.OK) {
                // Perbaikan: Tidak menutup aplikasi, melainkan masuk alur loading splash screen kembali
                app.logoutWithSplash(); 
            }
        });

        sidebar.getChildren().addAll(
                logoBox, 
                roleBox,
                dashboardBtn,
                sidebarButtonSpacer(), // Memasukkan celah pemisah antar komponen
                siswaBtn,
                spacer, 
                logoutBtn
        );

        root.setLeft(sidebar);

        // =========================
        // CONTENT ASLI 100%
        // =========================

        VBox content =
                new VBox(25);

        content.setPadding(
                new Insets(25)
        );

        Label title =
                new Label(
                        "Dashboard Penilaian"
                );

        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;"
        );

        // CARDS
        HBox cards =
                new HBox(20);

        cards.getChildren().addAll(
                createCard("Total Siswa", totalLabel),
                createCard("Lulus", lulusLabel),
                createCard("Tidak Lulus", gagalLabel),
                createCard("Rata-rata", rataLabel)
        );

        // FORM
        VBox form =
                new VBox(15);

        form.setPadding(
                new Insets(20)
        );

        form.setStyle(
                "-fx-background-color: #1e293b;" +
                        "-fx-background-radius: 20;"
        );

        TextField nisField = createField("NIS");
        TextField namaField = createField("Nama");
        TextField kelasField = createField("Kelas");
        TextField utsField = createField("Nilai UTS");
        TextField uasField = createField("Nilai UAS");

        // BUTTON TAMBAH TUGAS
        Button tambahTugasBtn = new Button("+ Tambah Tugas");
        tambahTugasBtn.setStyle(mainButton());
        tambahTugasBtn.setMaxWidth(Double.MAX_VALUE);
        tambahTugasBtn.setOnAction(event -> {
            TextField tugasField = createField("Nilai Tugas " + (tugasFields.size() + 1));
            tugasFields.add(tugasField);
            tugasBox.getChildren().add(tugasField);
        });

        // BUTTON TAMBAH SISWA
        Button tambahBtn = new Button("Tambah Siswa");
        tambahBtn.setStyle(mainButton());
        tambahBtn.setMaxWidth(Double.MAX_VALUE);
        tambahBtn.setOnAction(event -> {
            try {
                String nis = nisField.getText();
                String nama = namaField.getText();

                if (!nis.matches("\\d+")) {
                    showError("NIS harus angka");
                    return;
                }
                if (nama.matches(".*\\d.*")) {
                    showError("Nama tidak boleh angka");
                    return;
                }
                if (manager.isDuplicate(nis, nama)) {
                    showError("NIS atau Nama sudah ada");
                    return;
                }

                double uts = Double.parseDouble(utsField.getText());
                double uas = Double.parseDouble(uasField.getText());

                if (uts < 0 || uts > 100 || uas < 0 || uas > 100) {
                    showError("Nilai harus 0 - 100");
                    return;
                }

                ArrayList<Double> tugasList = new ArrayList<>();
                for (TextField tf : tugasFields) {
                    String text = tf.getText();
                    if (text.isEmpty()) {
                        tugasList.add(0.0);
                    } else {
                        double nilai = Double.parseDouble(text);
                        if (nilai < 0 || nilai > 100) {
                            showError("Nilai harus 0 - 100");
                            return;
                        }
                        tugasList.add(nilai);
                    }
                }

                double akhir = manager.calculateFinalScore(uts, uas, tugasList);
                Student student = new Student(nis, nama, kelasField.getText(), uts, uas, tugasList, akhir);

                manager.addStudent(student);
                manager.loadData();
                refreshStats();

                nisField.clear();
                namaField.clear();
                kelasField.clear();
                utsField.clear();
                uasField.clear();
                tugasFields.clear();
                tugasBox.getChildren().clear();

            } catch (Exception ex) {
                showError("Input salah");
            }
        });

        // BUTTON BOBOT
        Button bobotBtn = new Button("⚙ Atur Bobot");
        bobotBtn.setStyle(mainButton());
        bobotBtn.setMaxWidth(Double.MAX_VALUE);
        bobotBtn.setOnAction(event -> {
            WeightView.showWindow();
            manager.refreshAllStudentScores();
            refreshStats();
        });

        // BUTTON PDF
        Button pdfBtn = new Button("📄 Export PDF");
        pdfBtn.setStyle(mainButton());
        pdfBtn.setMaxWidth(Double.MAX_VALUE);
        pdfBtn.setOnAction(event -> {
            manager.exportPDF();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("PDF berhasil dibuat");
            alert.showAndWait();
        });

        form.getChildren().addAll(
                nisField, namaField, kelasField,
                utsField, uasField, tambahTugasBtn,
                tugasBox, tambahBtn, bobotBtn, pdfBtn
        );

        content.getChildren().addAll(title, cards, form);

        scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);

        // BUTTON ACTION NAVIGASI
        dashboardBtn.setOnAction(event -> {
            manager.loadData();
            refreshStats();
            root.setCenter(scrollPane);
        });

        siswaBtn.setOnAction(event -> {
            DataSiswaView dataSiswaView = new DataSiswaView();
            root.setCenter(dataSiswaView.getView());
        });

        refreshStats();
    }

    private Button createSidebarBoxButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        
        String normalStyle = 
                "-fx-background-color: transparent;" + 
                "-fx-text-fill: #CBD5E1;" + 
                "-fx-font-size: 15px;" + 
                "-fx-font-weight: bold;" + 
                "-fx-background-radius: 8;" + 
                "-fx-cursor: hand;";
                
        String hoverStyle = 
                "-fx-background-color: #2563EB;" + 
                "-fx-text-fill: white;" + 
                "-fx-font-size: 15px;" + 
                "-fx-font-weight: bold;" + 
                "-fx-background-radius: 8;" + 
                "-fx-cursor: hand;";
        
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        
        return btn;
    }

    private Region sidebarButtonSpacer() {
        Region r = new Region();
        r.setPrefHeight(2);
        return r;
    }

    private VBox createCard(String title, Label value) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 20;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 16px;");

        value.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        card.getChildren().addAll(titleLabel, value);

        return card;
    }

    private void refreshStats() {
        manager.loadData();
        int total = manager.getStudents().size();
        int lulus = 0;
        int gagal = 0;
        double totalNilai = 0;

        for (Student s : manager.getStudents()) {
            totalNilai += s.getNilaiAkhir();
            if (s.getStatus().equals("Lulus")) {
                lulus++;
            } else {
                gagal++;
            }
        }

        double rata = (total > 0) ? (totalNilai / total) : 0;

        totalLabel.setText(String.valueOf(total));
        lulusLabel.setText(String.valueOf(lulus));
        gagalLabel.setText(String.valueOf(gagal));
        rataLabel.setText(String.format("%.2f", rata));
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 12; -fx-padding: 14;");
        return field;
    }

    private String mainButton() {
        return "-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 14; -fx-cursor: hand;";
    }

    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}