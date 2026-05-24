package com.zacarias.worktrackhub.view;

import com.zacarias.worktrackhub.model.Project;
import com.zacarias.worktrackhub.model.Task;
import com.zacarias.worktrackhub.model.User;
import com.zacarias.worktrackhub.service.AuthService;
import com.zacarias.worktrackhub.service.ProjectService;
import com.zacarias.worktrackhub.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private MainFrame mainFrame;
    private AuthService authService;
    private ProjectService projectService;
    private UserService userService;

    private JButton logoutButton;
    private JTabbedPane tabbedPane;
    private JComboBox projectComboBox;
    private DefaultTableModel taskTableModel;


    public AdminPanel(MainFrame mainFrame, AuthService authService, ProjectService projectService, UserService userService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.projectService = projectService;
        this.userService = userService;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        //Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Admin Panel - " + authService.getCurrentUser().getEmail());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Projects", createProjectsPanel());
        tabbedPane.addTab("Tasks", createTasksPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 1){
                loadProjectsInCombo(projectComboBox);
                loadTasks(taskTableModel);
            }
        });
    }

    //Project Tab
    private JPanel createProjectsPanel() {
        JPanel projectsPanel = new JPanel(new BorderLayout(10, 10));

        //Form
        JPanel formProjectsPanel = new JPanel(new GridBagLayout());
        formProjectsPanel.setBorder(BorderFactory.createTitledBorder("New Project"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        JTextField projectNameField = new JTextField(20);
        JTextField projectDescriptionField = new JTextField(20);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        formProjectsPanel.add(new JLabel("Name:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formProjectsPanel.add(projectNameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        formProjectsPanel.add(new JLabel("Description:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formProjectsPanel.add(projectDescriptionField, gridBagConstraints);

        //Table
        String[] columns = {"ID", "Name", "Description", "Active"};
        DefaultTableModel projectTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        JTable projectTable = new JTable(projectTableModel);
        projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setBorder((BorderFactory.createTitledBorder("Projects")));

        //Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton deactivateButton = new JButton("Deactivate");
        JButton reactivateButton = new JButton("Reactivate");
        reactivateButton.setVisible(false);

        addButton.addActionListener(e -> {
            String name = projectNameField.getText().trim();
            String description = projectDescriptionField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name for this project");
                return;
            }
            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setActive(true);
            project.setUserId(authService.getCurrentUser().getId());
            projectService.createProject(project);
            projectNameField.setText("");
            projectDescriptionField.setText("");
            loadProjects(projectTableModel);
        });

        deactivateButton.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a project");
                return;
            }
            int id = (int) projectTableModel.getValueAt(selectedRow, 0);
            projectService.deactivateProject(id);
            loadProjects(projectTableModel);
        });

        reactivateButton.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a project");
                return;
            }
            int id = (int) projectTableModel.getValueAt(selectedRow, 0);
            projectService.reactivateProject(id);
            loadProjects(projectTableModel);
        });

        projectTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow == -1) {
                deactivateButton.setVisible(true);
                reactivateButton.setVisible(false);
                return;
            }
            String active = (String) projectTableModel.getValueAt(selectedRow, 3);
            if(active.equals("Yes")) {
                deactivateButton.setVisible(true);
                reactivateButton.setVisible(false);
            } else {
                deactivateButton.setVisible(false);
                reactivateButton.setVisible(true);
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(reactivateButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formProjectsPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        projectsPanel.add(topPanel, BorderLayout.NORTH);
        projectsPanel.add(scrollPane, BorderLayout.CENTER);

        loadProjects(projectTableModel);
        return projectsPanel;
    }

    private void loadProjects(DefaultTableModel projectTableModel) {
        projectTableModel.setRowCount(0);
        List<Project> projects = projectService.getAllProjects();
        for(Project project : projects) {
            projectTableModel.addRow(new Object[]{
                    project.getId(),
                    project.getName(),
                    project.getDescription(),
                    project.isActive() ? "Yes" : "No"
            });
        }
    }

    //Taks Tab
    private JPanel createTasksPanel() {
        JPanel tasksPanel = new JPanel(new BorderLayout(10, 10));

        //Form
        JPanel formTasksPanel = new JPanel(new GridBagLayout());
        formTasksPanel.setBorder(BorderFactory.createTitledBorder("New Task"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        JTextField taskNameField = new JTextField(20);
        JTextField taskDescriptionField = new JTextField(20);
        projectComboBox = new JComboBox<>();
        loadProjectsInCombo(projectComboBox);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        formTasksPanel.add(new JLabel("Project:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formTasksPanel.add(projectComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        formTasksPanel.add(new JLabel("Name:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formTasksPanel.add(taskNameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        formTasksPanel.add(new JLabel("Description:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formTasksPanel.add(taskDescriptionField, gridBagConstraints);

        //Table
        String[] columns = {"ID", "Name", "Description", "Active", "Project ID"};
        taskTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        JTable taskTable = new JTable(taskTableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder((BorderFactory.createTitledBorder("Tasks")));

        //Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton deactivateButton = new JButton("Deactivate");
        JButton reactivateButton = new JButton("Reactivate");
        reactivateButton.setVisible(false);

        addButton.addActionListener(e -> {
            Project selectedProject = (Project) projectComboBox.getSelectedItem();
            if (selectedProject == null) {
                JOptionPane.showMessageDialog(null, "Please select a project");
                return;
            }
            String name = taskNameField.getText().trim();
            String description = taskDescriptionField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name for this project");
                return;
            }
            Task task = new Task();
            task.setName(name);
            task.setDescription(description);
            task.setActive(true);
            task.setProjectId(selectedProject.getId());
            projectService.createTask(task);
            taskNameField.setText("");
            taskDescriptionField.setText("");
            loadTasks(taskTableModel);
        });

        deactivateButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a project");
                return;
            }
            int id = (int) taskTableModel.getValueAt(selectedRow, 0);
            projectService.deactivateTask(id);
            loadTasks(taskTableModel);
        });

        reactivateButton.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a task");
                return;
            }
            int id = (int) taskTableModel.getValueAt(selectedRow, 0);
            projectService.reactivateTask(id);
            loadTasks(taskTableModel);
        });

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                deactivateButton.setVisible(true);
                reactivateButton.setVisible(false);
                return;
            }
            String active = (String) taskTableModel.getValueAt(selectedRow, 3);
            if (active.equals("Yes")) {
                deactivateButton.setVisible(true);
                reactivateButton.setVisible(false);
            } else {
                deactivateButton.setVisible(false);
                reactivateButton.setVisible(true);
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(reactivateButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formTasksPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        tasksPanel.add(topPanel, BorderLayout.NORTH);
        tasksPanel.add(scrollPane, BorderLayout.CENTER);

        loadTasks(taskTableModel);
        return tasksPanel;
    }

    private void loadTasks(DefaultTableModel taskTableModel) {
        taskTableModel.setRowCount(0);
        List<Project> projects = projectService.getAllProjects();
        for(Project project : projects) {
            List<Task> tasks = projectService.getTasksByProjectId(project.getId());
            for(Task task : tasks) {
                taskTableModel.addRow(new Object[]{
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.isActive() ? "Yes" : "No",
                        project.getName()
                });
            }
        }
    }

    private void loadProjectsInCombo(JComboBox<Project> projectComboBox) {
        projectComboBox.removeAllItems();
        List<Project> projects = projectService.getAllProjects();
        for(Project project : projects) {
            projectComboBox.addItem(project);
        }
    }

    //Users Tab
    private JPanel createUsersPanel() {
        JPanel usersPanel = new JPanel(new BorderLayout(10, 10));

        //Form
        JPanel formUsersPanel = new JPanel(new GridBagLayout());
        formUsersPanel.setBorder(BorderFactory.createTitledBorder("New User"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        JTextField userNameField = new JTextField(20);
        JTextField userEmailField = new JTextField(20);
        JPasswordField userPasswordField = new JPasswordField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(
                new String[]{ "EMPLOYEE", "PROJECTMANAGER", "SUPERADMIN" }
        );

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        formUsersPanel.add(new JLabel("Name:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formUsersPanel.add(userNameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        formUsersPanel.add(new JLabel("Email:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formUsersPanel.add(userEmailField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        formUsersPanel.add(new JLabel("Password:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formUsersPanel.add(userPasswordField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        formUsersPanel.add(new JLabel("Role:"), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        formUsersPanel.add(roleComboBox, gridBagConstraints);

        //Table
        String[] columns = {"ID", "Name", "Email", "Role", "Active"};
        DefaultTableModel userTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder((BorderFactory.createTitledBorder("Users")));

        //Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add");
        JButton deactivateButton = new JButton("Deactivate");

        addButton.addActionListener(e -> {
            String name = userNameField.getText().trim();
            String email = userEmailField.getText().trim();
            String password = new String(userPasswordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields");
                return;
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(authService.hashPassword(password));
            user.setRole(role);
            user.setActive(true);
            userService.createUser(user);
            userNameField.setText("");
            userEmailField.setText("");
            userPasswordField.setText("");
            loadUsers(userTableModel);
        });

        deactivateButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a user");
                return;
            }
            int id = (int) userTableModel.getValueAt(selectedRow, 0);
            userService.deactivateUser(id);
            loadUsers(userTableModel);
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deactivateButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formUsersPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        usersPanel.add(topPanel, BorderLayout.NORTH);
        usersPanel.add(scrollPane, BorderLayout.CENTER);

        loadUsers(userTableModel);
        return usersPanel;
    }

    private void loadUsers(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for(User user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActive() ? "Yes" : "No"
            });
        }
    }
}