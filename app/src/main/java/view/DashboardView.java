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

public class DashboardView {

    private BorderPane root = new BorderPane();
    private StudentManager manager = new StudentManager();
    private ObservableList<Student> data = FXCollections.observableArrayList();

    private Label totalLabel = new Label("0");
    private Label lulusLabel = new Label("0");
    private Label gagalLabel = new Label("0");
    private Label rataLabel = new Label("0");

    private Main app; 

    public DashboardView(Stage primaryStage, User user, Main app) {
        this.app = app;
        root.setStyle("-fx-background-color: #0f172a;");
        data.addAll(manager.getStudents());

        VBox sidebar = new VBox(15); 
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(260); 
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: transparent #334155 transparent transparent; -fx-border-width: 0 1.5 0 0;");

        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER); 
        try {
            Image img = new Image(getClass().getResource("/images/Logo.jpg").toExternalForm());
            ImageView logoView = new ImageView(img);
            logoView.setFitWidth(180); 
            logoView.setPreserveRatio(true);

            StackPane logoBackground = new StackPane(logoView);
            logoBackground.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
            logoBox.getChildren().add(logoBackground);
        } catch (Exception e) {}

        VBox roleBox = new VBox();
        roleBox.setAlignment(Pos.CENTER_LEFT);
        Label roleLabel = new Label("Logged in as:\n" + user.showRole() + " (" + user.getUsername() + ")");
        roleLabel.setStyle("-fx-text-fill: #93C5FD; -fx-font-size: 14px; -fx-font-weight: bold;");
        roleBox.getChildren().add(roleLabel);
        VBox.setMargin(roleBox, new Insets(15, 0, 20, 0));

        Button dashboardBtn = createSidebarBoxButton("🏠 Dashboard");
        Button siswaBtn = createSidebarBoxButton("📚 Data Siswa");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPadding(new Insets(12, 15, 12, 15));
        
        String logoutNormal = "-fx-background-color: #7F1D1D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand;";
        String logoutHover = "-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8; -fx-cursor: hand;";
        logoutBtn.setStyle(logoutNormal);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHover));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutNormal));

        logoutBtn.setOnAction(e -> {
            // PERBAIKAN: Menggunakan AlertHelper kustom
            if (AlertHelper.showConfirm("Konfirmasi Logout", "Anda yakin ingin keluar dari sistem?")) {
                app.logoutWithSplash(); 
            }
        });

        sidebar.getChildren().addAll(logoBox, roleBox, dashboardBtn, sidebarButtonSpacer(), siswaBtn, spacer, logoutBtn);
        root.setLeft(sidebar);

        VBox content = new VBox(40); 
        content.setAlignment(Pos.CENTER); 
        content.setPadding(new Insets(50));

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("Selamat Datang di SIMANESSA");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 42px; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Sistem Manajemen Nilai Evaluasi Siswa");
        subtitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 18px;");
        
        headerBox.getChildren().addAll(title, subtitle);

        HBox cards = new HBox(30); 
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
                createCard("Total Siswa", totalLabel),
                createCard("Lulus", lulusLabel),
                createCard("Tidak Lulus", gagalLabel),
                createCard("Rata-rata", rataLabel)
        );

        content.getChildren().addAll(headerBox, cards);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true); 
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

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
        
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        
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
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(250); 
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #93c5fd; -fx-font-size: 18px;");

        value.setStyle("-fx-text-fill: white; -fx-font-size: 48px; -fx-font-weight: bold;"); 
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
            if (s.getStatus().equals("Lulus")) lulus++;
            else gagal++;
        }

        double rata = (total > 0) ? (totalNilai / total) : 0;

        totalLabel.setText(String.valueOf(total));
        lulusLabel.setText(String.valueOf(lulus));
        gagalLabel.setText(String.valueOf(gagal));
        rataLabel.setText(String.format("%.2f", rata));
    }

    public Parent getView() {
        return root;
    }
}