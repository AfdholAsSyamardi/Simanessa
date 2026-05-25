package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplashScreen {

    public static void show(Stage stage) {

        VBox root = new VBox(25); 
        root.setAlignment(Pos.CENTER);

        root.setStyle(
                "-fx-background-color: #FFFFFF;"
        );
        // LOGO SIMANESSA 
        try {
            Image img = new Image(SplashScreen.class.getResource("/images/Logo.jpg").toExternalForm());
            ImageView logoView = new ImageView(img);
            
            logoView.setFitWidth(300); 
            logoView.setPreserveRatio(true);
            root.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan, pastikan file /images/Logo.jpg tersedia.");
        }

        Label logoText = new Label("SIMANESSA");

        logoText.setStyle(
                "-fx-font-size: 50px;"
                        + "-fx-text-fill: #1E6FD9;" 
                        + "-fx-font-weight: bold;"
        );

        Label loading = new Label("Sistem Manajemen Nilai Evaluasi Siswa Secara Akurat");

        loading.setStyle(
                "-fx-text-fill: #4B5563;" 
                        + "-fx-font-size: 18px;"
                        + "-fx-font-style: italic;"
        );

        root.getChildren().addAll(
                logoText,
                loading
        );
        
        // PERBAIKAN: HANYA MENGGUNAKAN ROOT TANPA UKURAN MANUAL
       
        Scene scene = new Scene(root);

        stage.setScene(scene);
        
        // TRIK MEMAKSA WINDOWS FULLSCREEN SAAT SPLASH
        stage.setMaximized(false);
        stage.setMaximized(true); 
        
        stage.show();
    }
}