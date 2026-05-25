package app;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import view.DashboardView;
import view.LoginView;
import view.SplashScreen;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        SplashScreen.show(primaryStage);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> showLogin());
        delay.play();
    }

    public void showLogin() {
        LoginView loginView = new LoginView(user -> showDashboard(user));

        Scene loginScene = new Scene(loginView.getView());

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("SIMANESSA - Login");

        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true); 
        
        primaryStage.show();
    }

    public void showDashboard(User user) {
        DashboardView dashboard = new DashboardView(primaryStage, user, this);

        Scene dashboardScene = new Scene(dashboard.getView());

        primaryStage.setScene(dashboardScene);
        primaryStage.setTitle("SIMANESSA - Dashboard");

        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true); 
        
        primaryStage.show();
    }

    public void logoutWithSplash() {
        SplashScreen.show(primaryStage);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> showLogin());
        delay.play();
    }

    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}