package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewJobsController {

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
    @FXML
    private TableColumn<Job, Void> applyColumn;
    @FXML
    private TextField searchField;

    private ObservableList<Job> jobList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        jobIdColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        salaryRangeColumn.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        skillsRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("skillsRequired"));
        postedAtColumn.setCellValueFactory(new PropertyValueFactory<>("postedAt"));
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

        loadJobsFromDatabase();
        jobTable.setItems(jobList);

        addApplyButtonToTable();

        // Add listener to search field
        searchField.addEventHandler(KeyEvent.KEY_RELEASED, event -> filterJobs());
    }

    private void loadJobsFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/db_jobportal";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM jobs")) {

            while (resultSet.next()) {
                int jobId = resultSet.getInt("job_id");
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

    private void addApplyButtonToTable() {
        Callback<TableColumn<Job, Void>, TableCell<Job, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Job, Void> call(final TableColumn<Job, Void> param) {
                final TableCell<Job, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Apply");

                    {
                        btn.setOnAction((event) -> {
                            Job job = getTableView().getItems().get(getIndex());
                            applyForJob(job.getJobId());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        applyColumn.setCellFactory(cellFactory);
    }

    private void applyForJob(int jobId) {
        System.out.println("Apply button clicked for job ID: " + jobId); // Debugging print statement

        String url = "jdbc:mysql://localhost:3306/db_jobportal";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO applications (job_id, job_seeker_id, status) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, getJobSeekerId()); // Replace with actual job seeker ID
            pstmt.setString(3, "Applied");
            int affectedRows = pstmt.executeUpdate();

            System.out.println("ID: " + getJobSeekerId()); // Debugging print statement
            System.out.println("Affected rows: " + affectedRows); // Debugging print statement

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int applicationId = rs.getInt(1);
                    showAlert("Application successful! Application ID: " + applicationId);
                }
            } else {
                showAlert("Application failed. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred: " + e.getMessage());
        }
    }

    private int getJobSeekerId() {
        // Replace with actual logic to get the job seeker ID
        return UserSession.getInstance().getUserId(); // Example job seeker ID
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Application Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void filterJobs() {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Job> filteredList = FXCollections.observableArrayList();

        for (Job job : jobList) {
            if (job.getTitle().toLowerCase().contains(searchText) || job.getDescription().toLowerCase().contains(searchText)) {
                filteredList.add(job);
            }
        }

        jobTable.setItems(filteredList);
    }
}