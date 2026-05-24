package com.zacarias.worktrackhub.dao;

import com.zacarias.worktrackhub.model.Task;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TaskDAO {

    private Connection connection;

    public TaskDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void create (Task task) {

        String sqlInsert = "INSERT INTO tasks(name, description, active, project_id) VALUES (?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setInt(3, task.isActive() ? 1 : 0);
            preparedStatement.setInt(4, task.getProjectId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating task: " + e.getMessage());
        }
    }

    public Task findById(int id) {

        String sqlSelect = "SELECT * FROM tasks WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return mapTask(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving task: " + e.getMessage());
        }
        return null;
    }

    public List<Task> findByProjectId(int projectId) {

        String sqlSelect = "SELECT * FROM tasks WHERE project_id = ?";
        List<Task> tasks = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tasks.add(mapTask(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing tasks: " + e.getMessage());
        }
        return tasks;
    }

    public List<Task> findActiveByProjectId(int projectId) {

        String sqlSelect = "SELECT * FROM tasks WHERE project_id = ? AND active = 1";
        List<Task> tasks = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tasks.add(mapTask(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing tasks: " + e.getMessage());
        }
        return tasks;
    }

    public void update(Task task) {

        String sqlUpdate = "UPDATE tasks SET name = ?, description = ?, active = ?, project_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setInt(3, task.isActive() ? 1 : 0);
            preparedStatement.setInt(4, task.getProjectId());
            preparedStatement.setInt(5, task.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    public void deactivate(int id) {

        String sqlUpdate = "UPDATE tasks SET active = 0 where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error disabling task: " + e.getMessage());
        }
    }

    public void reactivate(int id) {
        String sqlUpdate = "UPDATE tasks SET active = 1 where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error reactivating task: " + e.getMessage());
        }
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        return new Task(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("active") == 1,
                resultSet.getInt("project_id")
        );
    }
}