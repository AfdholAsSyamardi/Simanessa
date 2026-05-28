package view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.io.PrintWriter;
import java.util.Optional;

public class AlertHelper {

    private static void applyTheme(Alert alert) {
        try {
            File cssFile = new File("modern_alert.css");
            if (!cssFile.exists()) {
                PrintWriter writer = new PrintWriter(cssFile);
                writer.println(".dialog-pane { -fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 2px; }");
                writer.println(".dialog-pane .content.label { -fx-text-fill: #cbd5e1; -fx-font-size: 14px; }");
                writer.println(".dialog-pane .header-panel { -fx-background-color: #0f172a; }");
                writer.println(".dialog-pane .header-panel .label { -fx-text-fill: #38bdf8; -fx-font-size: 18px; -fx-font-weight: bold; }");
                writer.println(".dialog-pane .button-bar, .dialog-pane > *.button-bar > *.container { -fx-background-color: #1e293b; }");
                writer.println(".dialog-pane .button { -fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; }");
                writer.println(".dialog-pane .button:hover { -fx-background-color: #3b82f6; }");
                writer.close();
            }
            alert.getDialogPane().getStylesheets().add(cssFile.toURI().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        applyTheme(alert);
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        applyTheme(alert);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        applyTheme(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}