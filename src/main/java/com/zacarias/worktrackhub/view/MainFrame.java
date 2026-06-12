package com.zacarias.worktrackhub.view;

import com.zacarias.worktrackhub.model.User;
import com.zacarias.worktrackhub.service.*;
import com.zacarias.worktrackhub.service.UserService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private AuthService authService;
    private ProjectService projectService;
    private TimeEntryService timeEntryService;
    private ReportService reportService;

    private JPanel currentPanel;

    public MainFrame() {
        this.authService = new AuthService();
        this.projectService = new ProjectService();
        this.timeEntryService = new TimeEntryService();
        this.reportService = new ReportService();

        initFrame();
        showLoginPanel();
    }

    private void initFrame() {
        setTitle("WorkTrack Hub");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImageIcon icon = new ImageIcon(getClass().getResource("/workTrackHubLogo.png"));
        setIconImage(icon.getImage());
    }

    private void showLoginPanel() {
        showPanel(new LoginPanel(this, authService));
    }

    public void showPanelForRole(User user) {
        switch(user.getRole()) {
            case "EMPLOYEE":
                showPanel(new TimeEntryPanel(this, authService, projectService, timeEntryService));
                break;
            case "PROJECTMANAGER":
                showPanel(new ReportPanel(this, authService, reportService));
                break;
            case "SUPERADMIN":
                showPanel(new AdminPanel(this, authService, projectService, new UserService()));
                break;
            default:
                showLoginPanel();
        }
    }

    private void showPanel(JPanel panel) {
        if(currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void logout() {
        authService.logout();
        showLoginPanel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}