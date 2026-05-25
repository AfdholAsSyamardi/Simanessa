package view;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.Guru;
import model.User;
import java.util.function.Consumer;

public class LoginView {

    private StackPane root; 
    private int loginAttempt = 0; 

    public LoginView(Consumer<User> onLoginSuccess) {
        root = new StackPane();
        root.setStyle("-fx-background-color: #F3F4F6;");

        VBox card = new VBox(20);
        card.setMaxSize(400, 550);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // LOGO SIMANESSA DI-RESIZE LEBIH BESAR
        try {
            Image img = new Image(getClass().getResource("/images/Logo.jpg").toExternalForm());
            ImageView logoView = new ImageView(img);
            
            // Perbaikan: Diperbesar dari 180 menjadi 250 karena label tulisan di bawahnya dihapus
            logoView.setFitWidth(250); 
            logoView.setPreserveRatio(true);
            card.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo tidak ditemukan.");
        }


        Label subtitle = new Label("Portal Login Guru");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #4B5563;");

        // FORM USERNAME
        VBox usernameBox = new VBox(8);
        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Masukkan username guru");
        usernameField.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #D1D5DB; -fx-border-radius: 6; -fx-padding: 12;");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        // FORM PASSWORD
        VBox passwordBox = new VBox(8);
        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #D1D5DB; -fx-border-radius: 6; -fx-padding: 12;");
        passwordBox.getChildren().addAll(passLabel, passwordField);

        // TOMBOL LOGIN
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 12; -fx-cursor: hand;");

        // LABEL PERINGATAN (DI BAWAH TOMBOL LOGIN)
        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!username.equals("guru") || !password.equals("123")) {
                loginAttempt++;
                warningLabel.setText("Username atau Password salah!");
            } else {
                loginAttempt = 0;
                warningLabel.setText(""); 
                onLoginSuccess.accept(new Guru(username, password));
            }

            if (loginAttempt >= 3) {
                loginButton.setDisable(true);
                warningLabel.setText("Terlalu banyak percobaan.\nTunggu 15 detik...");
                
                PauseTransition pause = new PauseTransition(Duration.seconds(15));
                pause.setOnFinished(event -> {
                    loginButton.setDisable(false);
                    warningLabel.setText("");
                    loginAttempt = 0;
                });
                pause.play();
            }
        });

        // Menyusun komponen ke dalam card tanpa menyertakan label title teks lagi
        card.getChildren().addAll(subtitle, usernameBox, passwordBox, loginButton, warningLabel);
        root.getChildren().add(card);
    }

    public Parent getView() {
        return root;
    }
}