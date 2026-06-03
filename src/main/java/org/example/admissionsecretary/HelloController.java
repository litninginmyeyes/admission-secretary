package org.example.admissionsecretary;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private void openAdminLogin() throws IOException {
        openWindow("login-view.fxml", "Вход для администратора", 350, 250);
    }

    @FXML
    private void openApplicantForm() throws IOException {
        openWindow("applicant-view.fxml", "Подача заявления", 500, 600);
    }

    @FXML
    private void openStatusCheck() throws IOException {
        openWindow("status-view.fxml", "Проверить статус", 400, 300);
    }

    private void openWindow(String fxml, String title, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource(fxml)
        );
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(loader.load(), width, height));
        stage.show();
    }
}