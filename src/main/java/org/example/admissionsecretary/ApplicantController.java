package org.example.admissionsecretary;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class ApplicantController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<AdmissionList> listComboBox;
    @FXML private TextField egeMathField;
    @FXML private TextField egeRussianField;
    @FXML private TextField egeSubjectField;
    @FXML private TextField achievementsField;
    @FXML private TextField portfolioField;
    @FXML private RadioButton noneRadio;
    @FXML private RadioButton targetRadio;
    @FXML private RadioButton priorityRadio;
    @FXML private Label messageLabel;

    private final ApplicantService service = new ApplicantService();
    private final AdmissionListDAO listDAO = new AdmissionListDAO();

    @FXML
    public void initialize() {
        try {
            List<AdmissionList> lists = listDAO.getAll();
            listComboBox.setItems(FXCollections.observableArrayList(lists));
        } catch (SQLException e) {
            messageLabel.setText("Ошибка загрузки списков: " + e.getMessage());
        }
    }

    @FXML
    private void submitForm() {
        if (fullNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                listComboBox.getValue() == null) {
            messageLabel.setText("Заполните все обязательные поля!");
            return;
        }

        try {
            Applicant a = new Applicant();
            a.setFullName(fullNameField.getText());
            a.setEmail(emailField.getText());
            a.setListId(listComboBox.getValue().getId());
            a.setEgeMath(Integer.parseInt(egeMathField.getText().isEmpty() ? "0" : egeMathField.getText()));
            a.setEgeRussian(Integer.parseInt(egeRussianField.getText().isEmpty() ? "0" : egeRussianField.getText()));
            a.setEgeSubject(Integer.parseInt(egeSubjectField.getText().isEmpty() ? "0" : egeSubjectField.getText()));
            a.setAchievementsScore(Integer.parseInt(achievementsField.getText().isEmpty() ? "0" : achievementsField.getText()));
            a.setPortfolioScore(Integer.parseInt(portfolioField.getText().isEmpty() ? "0" : portfolioField.getText()));
            a.setTarget(targetRadio.isSelected());
            a.setPriority(priorityRadio.isSelected());

            service.addApplicant(a);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Заявление подано успешно! Ваш ID: " + a.getId());
            clearForm();

        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Баллы должны быть числами!");
        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void clearForm() {
        fullNameField.clear();
        emailField.clear();
        egeMathField.clear();
        egeRussianField.clear();
        egeSubjectField.clear();
        achievementsField.clear();
        portfolioField.clear();
        noneRadio.setSelected(true);
        listComboBox.setValue(null);
    }
}