package com.zacarias.worktrackhub.dao;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:worktrackhub.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                initDatabase();
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
            }
        }
        return connection;
    }

    private static void initDatabase() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                name                TEXT NOT NULL,
                email               TEXT NOT NULL UNIQUE,
                passwordhash        TEXT NOT NULL,
                role                TEXT NOT NULL CHECK(role IN('EMPLOYEE', 'PROJECTMANAGER', 'SUPERADMIN')),
                active              INTEGER NOT NULL DEFAULT 1
                );""";

        String createProjects = """
                CREATE TABLE IF NOT EXISTS projects (
                id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                name                TEXT NOT NULL,
                description         TEXT NOT NULL,
                active              INTEGER NOT NULL DEFAULT 1,
                user_id             INTEGER NOT NULL,
                FOREIGN KEY         (user_id) REFERENCES users(id)
                );""";

        String createTasks = """
                CREATE TABLE IF NOT EXISTS tasks (
                id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                name                TEXT NOT NULL,
                description         TEXT NOT NULL,
                active              INTEGER DEFAULT 1,
                project_id          INTEGER NOT NULL,
                FOREIGN KEY         (project_id) REFERENCES projects(id)
                );""";

        String createTimeEntries = """
                CREATE TABLE IF NOT EXISTS timeentries (
                id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                date                TEXT NOT NULL,
                hours               REAL NOT NULL CHECK(hours > 0),
                description         TEXT NOT NULL,
                status              TEXT NOT NULL,
                user_id             INTEGER NOT NULL,
                task_id             INTEGER NOT NULL,
                FOREIGN KEY         (user_id) REFERENCES users(id),
                FOREIGN KEY         (task_id) REFERENCES tasks(id)
                );""";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createUsers);
            statement.execute(createProjects);
            statement.execute(createTasks);
            statement.execute(createTimeEntries);
            System.out.println("The database has been initialized successfully.");

            // Seed de usuarios iniciales (solo si la tabla está vacía)
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
                String seed = "INSERT INTO users (name, email, passwordhash, role, active) VALUES " +
                        "('Admin', 'admin@worktrackhub.com', '" + hash + "', 'SUPERADMIN', 1)," +
                        "('Manager', 'manager@worktrackhub.com', '" + hash + "', 'PROJECTMANAGER', 1)," +
                        "('Employee', 'employee@worktrackhub.com', '" + hash + "', 'EMPLOYEE', 1)";
                statement.execute(seed);
                System.out.println("Initial users created (password: admin123).");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing the database: " + e.getMessage());
        }

    }
}