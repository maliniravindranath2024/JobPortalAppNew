package com.application;

import com.application.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SearchForJobsController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Job> jobTable;
    @FXML
    private TableColumn<Job, Integer> jobIdColumn;
    @FXML
    private TableColumn<Job, String> titleColumn;
    @FXML
    private TableColumn<Job, String> descriptionColumn;
    @FXML
    private TableColumn<Job, String> locationColumn;
    @FXML
    private TableColumn<Job, String> salaryRangeColumn;
    @FXML
    private TableColumn<Job, String> typeColumn;
    @FXML
    private TableColumn<Job, String> skillsRequiredColumn;
    @FXML
    private TableColumn<Job, String> postedAtColumn;
    @FXML
    private TableColumn<Job, String> expiryDateColumn;

    private ObservableList<Job> jobList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("job_id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        salaryRangeColumn.setCellValueFactory(new PropertyValueFactory<>("salary_range"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        skillsRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("skills_required"));
        postedAtColumn.setCellValueFactory(new PropertyValueFactory<>("posted_at"));
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiry_date"));

        jobTable.setItems(jobList);
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "Please enter a search term.");
            return;
        }

        searchJobsInDatabase(searchText);
    }

    private void searchJobsInDatabase(String searchText) {
        jobList.clear();

        String url = "jdbc:mysql://localhost:3306/db_jobportal";
        String user = "root";
        String password = "root";

        String query = "SELECT * FROM jobs WHERE title LIKE '%" + searchText + "%' OR description LIKE '%" + searchText + "%' OR location LIKE '%" + searchText + "%'";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int jobId = resultSet.getInt("job-id");
                int employerId = resultSet.getInt("employer_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String salaryRange = resultSet.getString("salary_range");
                String type = resultSet.getString("type");
                String skillsRequired = resultSet.getString("skills_required");
                String postedAt = resultSet.getString("posted_at");
                String expiryDate = resultSet.getString("expiry_date");

                Job job = new Job(jobId, employerId, title, description, location, salaryRange, type, skillsRequired, postedAt, expiryDate);
                jobList.add(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}