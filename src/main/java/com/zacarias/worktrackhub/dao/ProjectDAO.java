package com.zacarias.worktrackhub.dao;

import com.zacarias.worktrackhub.model.Project;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ProjectDAO {

    private Connection connection;

    public ProjectDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void create (Project project) {

        String sqlInsert = "INSERT INTO projects(name, description, active, user_id) VALUES (?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setInt(3, project.isActive() ? 1 : 0);
            preparedStatement.setInt(4, project.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating project: " + e.getMessage());
        }
    }

    public Project findById(int id) {

        String sqlSelect = "SELECT * FROM projects WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return mapProject(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving project: " + e.getMessage());
        }
        return null;
    }

    public List<Project> findAll() {

        String sqlSelect = "SELECT * FROM projects";
        List<Project> projects = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projects.add(mapProject(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing projects: " + e.getMessage());
        }
        return projects;
    }

    public List<Project> findAllActive() {

        String sqlSelect = "SELECT * FROM projects WHERE active = 1";
        List<Project> projects = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                projects.add(mapProject(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing active projects: " + e.getMessage());
        }
        return projects;
    }

    public void update(Project project) {

        String sqlUpdate = "UPDATE projects SET name = ?, description = ?, active = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setInt(3, project.isActive() ? 1 : 0);
            preparedStatement.setInt(4, project.getUserId());
            preparedStatement.setInt(5, project.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating project: " + e.getMessage());
        }
    }

    public void deactivate(int id) {

        String sqlUpdate = "UPDATE projects SET active = 0 where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error disabling project: " + e.getMessage());
        }
    }

    public void reactivate(int id) {
        String sqlUpdate = "UPDATE projects SET active = 1 where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement((sqlUpdate)))  {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error reactivating project: " + e.getMessage());
        }
    }

    private Project mapProject(ResultSet resultSet) throws SQLException {
        return new Project(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("active") == 1,
                resultSet.getInt("user_id")
        );
    }
}