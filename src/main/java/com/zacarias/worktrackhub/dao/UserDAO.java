package com.zacarias.worktrackhub.dao;

import com.zacarias.worktrackhub.model.User;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void create (User user) {

        String sqlInsert = "INSERT INTO users(name, email, passwordhash, role, active) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setInt(5, user.getActive() ? 1 : 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    public User findByEmail (String email) {

        String sqlSelect = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public User findById(int id) {

        String sqlSelect = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving ID: " + e.getMessage());
        }
        return null;
    }

    public List<User> findAll() {

        String sqlSelect = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error listing users: " + e.getMessage());
        }
        return users;
    }

    public void update(User user) {

        String sqlUpdate = "UPDATE users SET name = ?, email = ?, passwordhash = ?, role = ?, active = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setInt(5, user.getActive() ? 1 : 0);
            preparedStatement.setInt(6, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void deactivate(int id) {

        String sqlUpdate = "UPDATE users SET active = 0 where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error disabling user: " + e.getMessage());
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("passwordhash"),
                resultSet.getString("role"),
                resultSet.getInt("active") == 1
        );
    }
}