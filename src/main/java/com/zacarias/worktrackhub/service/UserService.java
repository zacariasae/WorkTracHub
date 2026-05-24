package com.zacarias.worktrackhub.service;

import com.zacarias.worktrackhub.dao.UserDAO;
import com.zacarias.worktrackhub.model.User;

import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public void createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }
        userDAO.create(user);
    }

    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public void updateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        userDAO.update(user);
    }

    public void deactivateUser(int id) {
        userDAO.deactivate(id);
    }
}