package org.example.admissionsecretary;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AdminDAO adminDAO = new AdminDAO();

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (adminDAO.checkCredentials(username, password)) {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("admin-view.fxml")
                );
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setTitle("Панель администратора");
                stage.setScene(new Scene(loader.load(), 800, 600));
                stage.show();
            } else {
                errorLabel.setText("Неверный логин или пароль!");
            }
        } catch (SQLException | IOException e) {
            errorLabel.setText("Ошибка: " + e.getMessage());
        }
    }
}