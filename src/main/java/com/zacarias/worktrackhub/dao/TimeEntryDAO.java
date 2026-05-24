package com.zacarias.worktrackhub.dao;

import com.zacarias.worktrackhub.model.TimeEntry;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class TimeEntryDAO {

    private Connection connection;

    public TimeEntryDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void create (TimeEntry timeEntry) {

        String sqlInsert = "INSERT INTO timeentries(date, hours, description, status, user_id, task_id) VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, timeEntry.getDate().toString());
            preparedStatement.setFloat(2, timeEntry.getHours());
            preparedStatement.setString(3, timeEntry.getDescription());
            preparedStatement.setString(4, timeEntry.getStatus());
            preparedStatement.setInt(5, timeEntry.getUserId());
            preparedStatement.setInt(6, timeEntry.getTaskId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating time entry: " + e.getMessage());
        }
    }

    public TimeEntry findById(int id) {

        String sqlSelect = "SELECT * FROM timeentries WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return mapTimeEntry(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving time entry: " + e.getMessage());
        }
        return null;
    }

    public List<TimeEntry> findByUserId(int userId) {

        String sqlSelect = "SELECT * FROM timeentries WHERE user_id = ?";
        List<TimeEntry> timeEntries = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                timeEntries.add(mapTimeEntry(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing time entries: " + e.getMessage());
        }
        return timeEntries;
    }

    public List<TimeEntry> findByUserAndPeriod(int userId, int month, int year) {

        String sqlSelect = "SELECT * FROM timeentries WHERE user_id = ? AND strfTime('%m', date) = ? AND strfTime('%Y', date) = ?";
        List<TimeEntry> timeEntries = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, String.format("%02d", month));
            preparedStatement.setString(3, String.valueOf(year));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                timeEntries.add(mapTimeEntry(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error filtering time entries: " + e.getMessage());
        }
        return timeEntries;
    }

    public List<TimeEntry> findAll() {

        String sqlSelect = "SELECT * FROM timeentries";
        List<TimeEntry> timeEntries = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                timeEntries.add(mapTimeEntry(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing time entries: " + e.getMessage());
        }
        return timeEntries;
    }

    public void update(TimeEntry timeEntry) {

        String sqlUpdate = "UPDATE timeentries SET date = ?, hours = ?, description = ?, status = ?, task_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, timeEntry.getDate().toString());
            preparedStatement.setFloat(2, timeEntry.getHours());
            preparedStatement.setString(3, timeEntry.getDescription());
            preparedStatement.setString(4, timeEntry.getStatus());
            preparedStatement.setInt(5, timeEntry.getTaskId());
            preparedStatement.setInt(6, timeEntry.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating time entry: " + e.getMessage());
        }
    }

    public void updateStatus(int id, String status) {

        String sqlUpdate = "UPDATE timeentries SET status = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating status: " + e.getMessage());
        }
    }

    public void delete(int id) {

        String sqlDelete = "DELETE FROM timeentries where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
        }
    }

    private TimeEntry mapTimeEntry(ResultSet resultSet) throws SQLException {
        return new TimeEntry(
                resultSet.getInt("id"),
                LocalDate.parse(resultSet.getString("date")),
                resultSet.getFloat("hours"),
                resultSet.getString("description"),
                resultSet.getString("status"),
                resultSet.getInt("user_id"),
                resultSet.getInt("task_id")
        );
    }
}