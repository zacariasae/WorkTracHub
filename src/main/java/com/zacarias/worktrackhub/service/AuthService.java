package com.zacarias.worktrackhub.service;

import com.zacarias.worktrackhub.dao.UserDAO;
import com.zacarias.worktrackhub.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);

        if(user == null) {
            return null;
        }

        if(!user.getActive()) {
            return null;
        }

        if(!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isEmployee() {
        return currentUser != null && currentUser.getRole().equals("EMPLOYEE");
    }

    public boolean isProjectManager() {
        return currentUser != null && currentUser.getRole().equals("PROJECTMANAGER");
    }

    public boolean isSuperAdmin() {
        return currentUser != null && currentUser.getRole().equals("SUPERADMIN");
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}