package com.vhaa.translatorrfx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Class Message Utiles.
 *
 * Class to manipulate the messages and register events in the logs.
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class MessageUtils {
    /**
     * Shows a dialog panel with an error
     *
     * @param header
     * @param message
     */
    public static void showError(String header, String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setHeaderText(header);
        dialog.setContentText(message);
        setStyleDialog(dialog);
        dialog.showAndWait();
        log(MessageType.ERROR, message);
    }

    /**
     * Shows a dialog panel with an information message
     *
     * @param header
     * @param message
     */
    public static void showMessage(String header, String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Information");
        if (!message.isEmpty()) {
            dialog.setHeaderText(header);
        }
        dialog.setContentText(message);
        setStyleDialog(dialog);
        dialog.showAndWait();
        log(MessageType.INFO, message);
    }

    /**
     * Show a dialog panel with message of confirmation
     *
     * @param header
     * @param message
     * @return
     */
    public static boolean showMessageConfirmation(String header, String message) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmation");
        if (!header.isEmpty()) {
            dialog.setHeaderText(header);
        }
        dialog.setContentText(message);
        setStyleDialog(dialog);
        Optional<ButtonType> result = dialog.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Set style in dialog
     *
     * @param dialog
     */
    private static void setStyleDialog(Alert dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(MessageUtils.class.getResource("/com/vhaa/cateringfx/main-view.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
    }

    /**
     * Print log in console
     *
     * @param type
     * @param description
     */
    public static void log(MessageType type, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().toString());
        sb.append(" [");
        sb.append(type);
        sb.append("] ");
        sb.append(description);
        switch (type) {
            case ERROR: {
                System.err.println(sb.toString());
                break;
            }
            case INFO: {
                System.out.println(sb.toString());
                break;
            }
            default:
                break;
        }
    }

    public static enum MessageType {
        ERROR,
        INFO;

        private MessageType() {
        }
    }
}
