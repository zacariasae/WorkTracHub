package com.zacarias.worktrackhub.view;

import com.zacarias.worktrackhub.model.Project;
import com.zacarias.worktrackhub.model.Task;
import com.zacarias.worktrackhub.model.TimeEntry;
import com.zacarias.worktrackhub.service.AuthService;
import com.zacarias.worktrackhub.service.ProjectService;
import com.zacarias.worktrackhub.service.TimeEntryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TimeEntryPanel extends JPanel {

    private MainFrame mainFrame;
    private AuthService authService;
    private ProjectService projectService;
    private TimeEntryService timeEntryService;

    //Form
    private JTextField dateField;
    private JTextField hoursField;
    private JTextField descriptionField;
    private JComboBox<Project> projectComboBox;
    private JComboBox<Task> taskComboBox;
    private JButton saveButton;
    private JButton submitButton;
    private JButton recoveryButton;
    private JButton deleteButton;
    private JButton logoutButton;

    //Table
    private JTable table;
    private DefaultTableModel tableModel;

    public TimeEntryPanel(MainFrame mainFrame, AuthService authService, ProjectService projectService, TimeEntryService timeEntryService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.projectService = projectService;
        this.timeEntryService = timeEntryService;
        initComponents();
        loadTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        //Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Bienvenido, " + authService.getCurrentUser().getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("New Time Entry"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        formPanel.add(new JLabel("Date: "), gridBagConstraints);
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().toString());
        gridBagConstraints.gridx = 1;
        formPanel.add(dateField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        formPanel.add(new JLabel("Hours: "), gridBagConstraints);
        hoursField = new JTextField(15);
        gridBagConstraints.gridx = 1;
        formPanel.add(hoursField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        formPanel.add(new JLabel("Project: "), gridBagConstraints);
        projectComboBox = new JComboBox<>();
        projectComboBox.addActionListener(e -> loadTasks());
        gridBagConstraints.gridx = 1;
        formPanel.add(projectComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        formPanel.add(new JLabel("Tasks: "), gridBagConstraints);
        taskComboBox = new JComboBox<>();
        gridBagConstraints.gridx = 1;
        formPanel.add(taskComboBox, gridBagConstraints);

        loadProjects();

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        formPanel.add(new JLabel("Description: "), gridBagConstraints);
        descriptionField = new JTextField(15);
        gridBagConstraints.gridx = 1;
        formPanel.add(descriptionField, gridBagConstraints);

        //Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Save");
        submitButton = new JButton("Submit");
        recoveryButton = new JButton("Recover");
        deleteButton = new JButton("Delete");

        saveButton.addActionListener(e -> handleSave());
        submitButton.addActionListener(e -> handleSubmit());
        recoveryButton.addActionListener(e -> handleRecovery());
        deleteButton.addActionListener(e -> handleDelete());

        buttonPanel.add(saveButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(recoveryButton);
        buttonPanel.add(deleteButton);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        formPanel.add(buttonPanel, gridBagConstraints);

        add(formPanel, BorderLayout.WEST);

        //Table
        String[] columns = {"ID", "Date", "Hours", "Description", "Status", "Task"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder((BorderFactory.createTitledBorder("My Time Entries")));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProjects() {
        projectComboBox.removeAllItems();
        List<Project> projects = projectService.getActiveProjects();
        for (Project project : projects) {
            projectComboBox.addItem(project);
        }
        loadTasks();
    }

    private void loadTasks() {
        taskComboBox.removeAllItems();
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        if (selectedProject != null) {
            List<Task> tasks = projectService.getActiveTasksByProjectId(selectedProject.getId());
            for (Task task : tasks) {
                taskComboBox.addItem(task);
            }
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        int userId = authService.getCurrentUser().getId();
        List<TimeEntry> timeEntries = timeEntryService.getTimeEntriesByUserId(userId);
        for (TimeEntry timeEntry : timeEntries) {
            tableModel.addRow(new Object[]{
                    timeEntry.getId(),
                    timeEntry.getDate(),
                    timeEntry.getHours(),
                    timeEntry.getDescription(),
                    timeEntry.getStatus(),
                    timeEntry.getTaskId()
            });
        }
    }

    private void handleSave() {
        try {
            Task selectedTask = (Task) taskComboBox.getSelectedItem();
            if (selectedTask == null) {
                JOptionPane.showMessageDialog(this, "Select a task", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setDate(LocalDate.parse(dateField.getText().trim()));
            timeEntry.setHours(Float.parseFloat(hoursField.getText().trim()));
            timeEntry.setDescription(descriptionField.getText().trim());
            timeEntry.setUserId(authService.getCurrentUser().getId());
            timeEntry.setTaskId(selectedTask.getId());

            timeEntryService.createTimeEntry(timeEntry);
            JOptionPane.showMessageDialog(this, "Time Entry created (Draft) ", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleSubmit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            timeEntryService.submitTimeEntry(id);
            JOptionPane.showMessageDialog(this, "Time Entry submitted", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleRecovery() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            timeEntryService.recoverTimeEntry(id);
            JOptionPane.showMessageDialog(this, "Time Entry recovered (Draft) ", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            timeEntryService.deleteTimeEntry(id);
            JOptionPane.showMessageDialog(this, "Time Entry deleted (Draft) ", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().toString());
        hoursField.setText("");
        descriptionField.setText("");
    }
}