package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewAllUsersController {

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> actionColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Log initialization processing
        System.out.println("Initializing ViewAllUsersController...");

        if (userTableView == null || userIdColumn == null || emailColumn == null || roleColumn == null || passwordColumn == null || actionColumn == null) {
            showAlert("Error", "FXML components not properly initialized. Check FXML bindings.", Alert.AlertType.ERROR);
            return; // Prevent further execution if components are not connected properly.
        }

        try {
            // Bind data to TableView columns
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
            passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

            // Add a delete button to the "Action" column
            addDeleteButtonToTable();

            // Load users from the database
            loadUserData();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to initialize table or load data. Please contact support.", Alert.AlertType.ERROR);
        }
    }

    private void loadUserData() {
        // Initialize database connection and fetch data
        try (Connection connection = DBUtil.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Clear the existing list
            userList.clear();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String password = rs.getString("password");
                userList.add(new User(userId, email, role, password));
            }

            // Populate the TableView with fetched data
            userTableView.setItems(userList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user data. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    private void addDeleteButtonToTable() {
        // Custom cell factory for the delete button
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = param -> new TableCell<User, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    // Get selected user
                    User selectedUser = getTableView().getItems().get(getIndex());
                    if (selectedUser != null) {
                        deleteUser(selectedUser);
                    } else {
                        showAlert("Error", "No user selected for deletion.", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void deleteUser(User user) {
        try (Connection connection = DBUtil.getConnection()) {
            // Check the user's role in the database
            String fetchRoleSql = "SELECT role FROM users WHERE user_id = ?";
            String userRole = "";
            try (PreparedStatement fetchRoleStmt = connection.prepareStatement(fetchRoleSql)) {
                fetchRoleStmt.setInt(1, user.getUserId());
                ResultSet rs = fetchRoleStmt.executeQuery();
                if (rs.next()) {
                    userRole = rs.getString("role");
                }
            }
            // If the role is "recruiter", delete dependent data in jobs and employers tables
            if ("recruiter".equalsIgnoreCase(userRole)) {
                // Delete all job posts made by the recruiter
                String deleteJobsSql = "DELETE FROM jobs WHERE employer_id = ?";
                try (PreparedStatement deleteJobsStmt = connection.prepareStatement(deleteJobsSql)) {
                    deleteJobsStmt.setInt(1, user.getUserId());
                    deleteJobsStmt.executeUpdate();
                }

                String deleteInterviewsSql = "DELETE FROM interviews WHERE employer_id = ?";
                try (PreparedStatement deleteJobsStmt = connection.prepareStatement(deleteInterviewsSql)) {
                    deleteJobsStmt.setInt(1, user.getUserId());
                    deleteJobsStmt.executeUpdate();
                }

                // Delete the recruiter from the employers table
                String deleteEmployersSql = "DELETE FROM employers WHERE employer_id = ?";
                try (PreparedStatement deleteEmployersStmt = connection.prepareStatement(deleteEmployersSql)) {
                    deleteEmployersStmt.setInt(1, user.getUserId());
                    deleteEmployersStmt.executeUpdate();
                }
            }
            else if("job_seeker".equalsIgnoreCase(userRole)) {

                String deleteInterviewsSql = "DELETE FROM interviews WHERE job_seeker_id = ?";
                try (PreparedStatement deleteJobsStmt = connection.prepareStatement(deleteInterviewsSql)) {
                    deleteJobsStmt.setInt(1, user.getUserId());
                    deleteJobsStmt.executeUpdate();
                }

                String deleteApplicationsSql = "DELETE FROM applications WHERE job_seeker_id = ?";
                try (PreparedStatement deleteJobsStmt = connection.prepareStatement(deleteApplicationsSql)) {
                    deleteJobsStmt.setInt(1, user.getUserId());
                    deleteJobsStmt.executeUpdate();
                }

                String deleteJobseekersSql = "DELETE FROM jobseekers WHERE job_seeker_id = ?";
                try (PreparedStatement deleteJobseekerStmt = connection.prepareStatement(deleteJobseekersSql)) {
                    deleteJobseekerStmt.setInt(1, user.getUserId());
                    deleteJobseekerStmt.executeUpdate();
                }
            }

            // Then, delete the user from the users table
            String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserSql)) {
                deleteUserStmt.setInt(1, user.getUserId());
                int rowsAffected = deleteUserStmt.executeUpdate();
                if (rowsAffected > 0) {
                    userList.remove(user);
                    userTableView.refresh(); // Refresh TableView after deletion
                    showAlert("Success", "User deleted successfully.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to delete the user. Please try again.", Alert.AlertType.ERROR);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while deleting the user. Please try again.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}