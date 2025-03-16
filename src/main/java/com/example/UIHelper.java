package com.example;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.*;

/**
 * Helper class for common UI operations.
 */
public class UIHelper {
    
    /**
     * Shows an alert dialog with the specified title and message.
     * 
     * @param title Alert title
     * @param message Alert message
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}