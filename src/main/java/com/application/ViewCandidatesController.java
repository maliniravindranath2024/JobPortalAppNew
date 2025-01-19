package com.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ViewCandidatesController {

    @FXML
    private ComboBox<String> jobDropdown; // Dropdown showing job names

    @FXML
    private TableView<Jobseeker> applicationTable; // Table displaying candidate details

    @FXML
    private TableColumn<Jobseeker, String> firstnameColumn; // First Name column

    @FXML
    private TableColumn<Jobseeker, String> lastnameColumn; // Last Name column

    @FXML
    private TableColumn<Jobseeker, Integer> experienceColumn; // Experience column

    @FXML
    private TableColumn<Jobseeker, String> resumeColumn; // Resume column

    static Connection connection;

    @FXML
    public void initialize() throws SQLException {
        // Initialize TableView columns
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
        resumeColumn.setCellValueFactory(new PropertyValueFactory<>("resume"));

        firstnameColumn.setPrefWidth(100);
        lastnameColumn.setPrefWidth(100);
        experienceColumn.setPrefWidth(100);
        resumeColumn.setPrefWidth(150);


        connection = DBUtil.getConnection();
        // Load jobs posted by the authenticated user into the dropdown
        loadJobs();

        // Listener for dropdown changes
        jobDropdown.setOnAction(event -> loadApplicationsForSelectedJob());
    }

    private void loadJobs() throws SQLException {
        UserSession session = UserSession.getInstance();
        String role = session.getRole();
        int userId = session.getUserId();

        ObservableList<String> jobsPosted = getJobsByUser(userId);

        // Populate the job dropdown
        jobDropdown.setItems(jobsPosted);
    }

    private void loadApplicationsForSelectedJob() {
        String selectedJob = jobDropdown.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            return; // If no job is selected, do nothing
        }

        // Fetch applications and related job_seeker details for the selected job
        ObservableList<Jobseeker> candidates = getCandidatesForJob(selectedJob);

        for (Jobseeker candidate : candidates) {
            System.out.println("Candidate: " + candidate.getFirstname() + " " + candidate.getLastname());
        }


        // Set data in the TableView
        applicationTable.setItems(candidates);
    }

    public static ObservableList<String> getJobsByUser(int userId) throws SQLException {
        ObservableList<String> jobs = FXCollections.observableArrayList();

        try (
             var preparedStatement = connection.prepareStatement("SELECT job_id, title FROM jobs WHERE employer_id = ?")) {
            preparedStatement.setInt(1, userId);
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                jobs.add(String.valueOf(resultSet.getInt("job_id")));
                //jobs.add(resultSet.getString("title"));
            }
        }

        return jobs;
    }

    public static ObservableList<Jobseeker> getCandidatesForJob(String job_id) {
        // Replace with actual database query logic (example below shows a simulated result)
        ObservableList<Jobseeker> candidates = FXCollections.observableArrayList();

        try (var preparedStatement = connection.prepareStatement("SELECT\n" +
                "       js.first_name AS applicant_firstname,\n" +
                "       js.last_name AS applicant_lastname,\n" +
                "       js.experience_years AS applicant_experience,\n" +
                "       js.resume AS applicant_resume\n" +
                "   FROM\n" +
                "       applications a\n" +
                "   INNER JOIN\n" +
                "       jobseekers js ON a.job_seeker_id = js.job_seeker_id\n" +
                "   WHERE\n" +
                "       a.job_id = ?")) {
            preparedStatement.setInt(1, Integer.parseInt(job_id));
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("applicant_firstname");
                String lastName = resultSet.getString("applicant_lastname");
                int experience = resultSet.getInt("applicant_experience");
                String resume = resultSet.getString("applicant_resume");

                // Create a new Jobseeker object and set the fields
                Jobseeker jobseeker = new Jobseeker();
                jobseeker.setFirstname(firstName);  // Assume Jobseeker has a setFirstname() method
                jobseeker.setLastname(lastName);    // Assume Jobseeker has a setLastname() method
                jobseeker.setExperience(experience); // Assume Jobseeker has a setExperience() method
                jobseeker.setResume(resume);        // Assume Jobseeker has a setResume() method

                // Add the jobseeker object to the candidates list
                candidates.add(jobseeker);
            }

            } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return candidates;
    }


    }