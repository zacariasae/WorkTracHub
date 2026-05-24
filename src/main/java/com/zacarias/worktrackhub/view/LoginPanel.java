package com.zacarias.worktrackhub.view;

import com.zacarias.worktrackhub.model.User;
import com.zacarias.worktrackhub.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private AuthService authService;
    private MainFrame mainFrame;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame, AuthService authService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        initComponents();
    }

    private void initComponents() {

        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);

        //Title
        JLabel titleLabel = new JLabel("WorkTrack Hub");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        add(titleLabel, gridBagConstraints);

        //Email
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;

        emailField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getText().isEmpty()) {
                    Graphics2D graphics2D = (Graphics2D) graphics.create();
                    graphics2D.setColor(Color.GRAY);
                    graphics2D.setFont(getFont().deriveFont(Font.ITALIC));
                    graphics2D.drawString("Please enter your email.", 5, getHeight() / 2 + 5);
                    graphics2D.dispose();
                }
            }
        };
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(emailField, gridBagConstraints);
        emailField.addActionListener(e -> handleLogin());

        //Password

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;

        passwordField = new JPasswordField(20) {
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getPassword().length == 0) {
                    Graphics2D graphics2D = (Graphics2D) graphics.create();
                    graphics2D.setColor(Color.GRAY);
                    graphics2D.setFont(getFont().deriveFont(Font.ITALIC));
                    graphics2D.drawString("Please enter your password.", 5, getHeight() / 2 + 5);
                    graphics2D.dispose();
                }
            }
        };
        gridBagConstraints.gridx = 1;
        add(passwordField, gridBagConstraints);
        passwordField.addActionListener(e -> handleLogin());

        //Login button
        loginButton = new JButton("Login");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        add(loginButton, gridBagConstraints);

        //Error
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gridBagConstraints.gridy = 4;
        add(errorLabel, gridBagConstraints);

        //Button click
        loginButton.addActionListener(e -> handleLogin());

    }

    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill all the fields");
            return;
        }

        User user = authService.login(email, password);

        if (user == null) {
            errorLabel.setText("Invalid email or password");
            return;
        }

        mainFrame.showPanelForRole(user);

    }
}