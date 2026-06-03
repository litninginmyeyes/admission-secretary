package org.example.admissionsecretary;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.util.List;

public class StatusController {

    @FXML private TextField emailField;
    @FXML private ComboBox<AdmissionList> listComboBox;
    @FXML private Label resultLabel;

    private final ApplicantDAO applicantDAO = new ApplicantDAO();
    private final AdmissionListDAO listDAO = new AdmissionListDAO();

    @FXML
    public void initialize() {
        try {
            List<AdmissionList> lists = listDAO.getAll();
            listComboBox.setItems(FXCollections.observableArrayList(lists));
        } catch (SQLException e) {
            resultLabel.setText("Ошибка загрузки списков: " + e.getMessage());
        }
    }

    @FXML
    private void checkStatus() {
        String email = emailField.getText().trim();
        AdmissionList selectedList = listComboBox.getValue();

        if (email.isEmpty()) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Введите email!");
            return;
        }

        if (selectedList == null) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Выберите специальность!");
            return;
        }

        try {
            List<Applicant> all = applicantDAO.getByListId(selectedList.getId());
            Applicant found = all.stream()
                    .filter(a -> email.equalsIgnoreCase(a.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText("Заявление не найдено.\nПроверьте email и выбранную специальность.");
                return;
            }

            String statusColor = switch (found.getStatus()) {
                case "Проходит"    -> "-fx-text-fill: green;";
                case "Не проходит" -> "-fx-text-fill: red;";
                default            -> "-fx-text-fill: orange;";
            };

            resultLabel.setStyle(statusColor);
            resultLabel.setText(
                    "ФИО: " + found.getFullName() + "\n" +
                            "Специальность: " + selectedList.getSpecialityName() + "\n" +
                            "Сумма баллов: " + found.getTotalScore() + "\n" +
                            "Статус: " + found.getStatus()
            );

        } catch (SQLException e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Ошибка: " + e.getMessage());
        }
    }
}