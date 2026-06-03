package org.example.admissionsecretary;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class AdminController {

    @FXML private TextField listNameField;
    @FXML private TextField specialityField;
    @FXML private TextField totalPlacesField;
    @FXML private TextField targetPlacesField;
    @FXML private TextField priorityPlacesField;
    @FXML private Label listMessageLabel;

    @FXML private TableView<AdmissionList> listsTable;
    @FXML private TableColumn<AdmissionList, String> listIdCol;
    @FXML private TableColumn<AdmissionList, String> listNameCol;
    @FXML private TableColumn<AdmissionList, String> listSpecialityCol;
    @FXML private TableColumn<AdmissionList, String> listTotalCol;
    @FXML private TableColumn<AdmissionList, String> listTargetCol;
    @FXML private TableColumn<AdmissionList, String> listPriorityCol;

    @FXML private TableView<Applicant> applicantsTable;
    @FXML private TableColumn<Applicant, String> appIdCol;
    @FXML private TableColumn<Applicant, String> appNameCol;
    @FXML private TableColumn<Applicant, String> appEmailCol;
    @FXML private TableColumn<Applicant, String> appTotalCol;
    @FXML private TableColumn<Applicant, String> appTargetCol;
    @FXML private TableColumn<Applicant, String> appPriorityCol;
    @FXML private TableColumn<Applicant, String> appStatusCol;

    @FXML private ComboBox<AdmissionList> filterListComboBox;
    @FXML private Label applicantMessageLabel;
    @FXML private Label statisticsLabel;

    private final AdmissionListDAO listDAO = new AdmissionListDAO();
    private final ApplicantDAO applicantDAO = new ApplicantDAO();
    private final ApplicantService service = new ApplicantService();

    @FXML
    public void initialize() {
        setupListsTable();
        setupApplicantsTable();
        loadLists();
    }

    private void setupListsTable() {
        listIdCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        listNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        listSpecialityCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSpecialityName()));
        listTotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTotalPlaces())));
        listTargetCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTargetPlaces())));
        listPriorityCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getPriorityPlaces())));
    }

    private void setupApplicantsTable() {
        appIdCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        appNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        appEmailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        appTotalCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTotalScore())));
        appTargetCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isTarget() ? "Да" : "Нет"));
        appPriorityCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isPriority() ? "Да" : "Нет"));
        appStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
    }

    private void loadLists() {
        try {
            List<AdmissionList> lists = listDAO.getAll();
            listsTable.setItems(FXCollections.observableArrayList(lists));
            filterListComboBox.setItems(FXCollections.observableArrayList(lists));
        } catch (SQLException e) {
            listMessageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void createList() {
        try {
            if (listNameField.getText().isEmpty() || specialityField.getText().isEmpty()) {
                listMessageLabel.setText("Заполните название и специальность!");
                return;
            }

            AdmissionList list = new AdmissionList();
            list.setName(listNameField.getText());
            list.setSpecialityName(specialityField.getText());
            list.setTotalPlaces(Integer.parseInt(totalPlacesField.getText().isEmpty() ? "0" : totalPlacesField.getText()));
            list.setTargetPlaces(Integer.parseInt(targetPlacesField.getText().isEmpty() ? "0" : targetPlacesField.getText()));
            list.setPriorityPlaces(Integer.parseInt(priorityPlacesField.getText().isEmpty() ? "0" : priorityPlacesField.getText()));

            listDAO.add(list);

            listMessageLabel.setStyle("-fx-text-fill: green;");
            listMessageLabel.setText("Список создан!");
            clearListForm();
            loadLists();

        } catch (NumberFormatException e) {
            listMessageLabel.setStyle("-fx-text-fill: red;");
            listMessageLabel.setText("Количество мест должно быть числом!");
        } catch (SQLException e) {
            listMessageLabel.setStyle("-fx-text-fill: red;");
            listMessageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void deleteList() {
        AdmissionList selected = listsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            listMessageLabel.setText("Выберите список для удаления!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить список \"" + selected.getName() + "\"?\nВсе абитуриенты этого списка тоже удалятся!",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    listDAO.delete(selected.getId());
                    listMessageLabel.setStyle("-fx-text-fill: green;");
                    listMessageLabel.setText("Список удалён!");
                    loadLists();
                } catch (SQLException e) {
                    listMessageLabel.setStyle("-fx-text-fill: red;");
                    listMessageLabel.setText("Ошибка: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void loadApplicants() {
        AdmissionList selected = filterListComboBox.getValue();
        if (selected == null) return;
        try {
            List<Applicant> applicants = service.getRankedApplicants(selected.getId());
            applicantsTable.setItems(FXCollections.observableArrayList(applicants));
        } catch (SQLException e) {
            applicantMessageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void deleteApplicant() {
        Applicant selected = applicantsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            applicantMessageLabel.setText("Выберите абитуриента для удаления!");
            return;
        }

        int listId = selected.getListId();

        try {
            applicantDAO.delete(selected.getId());
            applicantMessageLabel.setStyle("-fx-text-fill: green;");
            applicantMessageLabel.setText("Абитуриент удалён!");
            service.updateStatuses(listId);
            loadApplicants();
        } catch (SQLException e) {
            applicantMessageLabel.setStyle("-fx-text-fill: red;");
            applicantMessageLabel.setText("Ошибка: " + e.getMessage());
        }

    }

    @FXML
    private void showStatistics() {
        AdmissionList selected = filterListComboBox.getValue();
        if (selected == null) {
            applicantMessageLabel.setText("Выберите список!");
            return;
        }
        try {
            String stats = service.getStatistics(selected.getId());
            statisticsLabel.setText(stats);
        } catch (SQLException e) {
            applicantMessageLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void clearListForm() {
        listNameField.clear();
        specialityField.clear();
        totalPlacesField.clear();
        targetPlacesField.clear();
        priorityPlacesField.clear();
    }
}