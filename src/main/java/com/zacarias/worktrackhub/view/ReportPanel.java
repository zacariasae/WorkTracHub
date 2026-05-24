package com.zacarias.worktrackhub.view;

import com.zacarias.worktrackhub.model.TimeEntry;
import com.zacarias.worktrackhub.service.AuthService;
import com.zacarias.worktrackhub.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportPanel extends JPanel {

    private MainFrame mainFrame;
    private AuthService authService;
    private ReportService reportService;

    private JTextField userIdField;
    private JTextField monthField;
    private JTextField yearField;
    private JButton filterButton;
    private JButton exportButton;
    private JButton logoutButton;
    private JLabel totalHoursLabel;

    private JTable table;
    private DefaultTableModel tableModel;

    private List<TimeEntry> currentTimeEntries;

    public ReportPanel(MainFrame mainFrame, AuthService authService, ReportService reportService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.reportService = reportService;
        initComponents();
        loadAllTimeEntries();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        //Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + authService.getCurrentUser().getName() + " - " + authService.getCurrentUser().getRole());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        filterPanel.add(new JLabel("User ID:"), gridBagConstraints);
        userIdField = new JTextField(10);
        gridBagConstraints.gridx = 1;
        filterPanel.add(userIdField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        filterPanel.add(new JLabel("Month:"), gridBagConstraints);
        monthField = new JTextField(10);
        gridBagConstraints.gridx = 1;
        filterPanel.add(monthField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        filterPanel.add(new JLabel("Year:"), gridBagConstraints);
        yearField = new JTextField(10);
        gridBagConstraints.gridx = 1;
        filterPanel.add(yearField, gridBagConstraints);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        filterButton = new JButton("Filter");
        exportButton = new JButton("CSV Export");

        filterButton.addActionListener(e -> handleFilter());
        exportButton.addActionListener(e -> handleExport());

        buttonPanel.add(filterButton);
        buttonPanel.add(exportButton);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        filterPanel.add(buttonPanel, gridBagConstraints);

        add(filterPanel, BorderLayout.WEST);

        //Table
        String[] columns = {"ID", "Date", "Hours", "Description", "Status", "User", "Task"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Time Entries"));
        add(scrollPane, BorderLayout.CENTER);

        //Total hours Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalHoursLabel = new JLabel("Total Hours: 0.0");
        totalHoursLabel.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(totalHoursLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAllTimeEntries() {
        currentTimeEntries = reportService.getAllTimeEntries();
        refreshTable(currentTimeEntries);
    }

    private void handleFilter() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            int month = Integer.parseInt(monthField.getText().trim());
            int year = Integer.parseInt(yearField.getText().trim());
            currentTimeEntries = reportService.getEntriesByUserAndPeriod(userId, month, year);
            refreshTable(currentTimeEntries);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numbers");
        }
    }

    private void handleExport() {
        if(currentTimeEntries == null || currentTimeEntries.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data to export");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("report.csv"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            reportService.exportToCsv(currentTimeEntries, filePath);
            JOptionPane.showMessageDialog(this, "CSV exported successfully");
        }
    }

    private void refreshTable(List<TimeEntry> timeEntries) {
        tableModel.setRowCount(0);
        float  totalHours = 0;
        if(timeEntries == null) {
            totalHoursLabel.setText("Total hours: 0.00");
            return;
        }
        for (TimeEntry timeEntry : timeEntries) {
            tableModel.addRow(new Object[]{
                    timeEntry.getId(),
                    timeEntry.getDate(),
                    timeEntry.getHours(),
                    timeEntry.getDescription(),
                    timeEntry.getStatus(),
                    timeEntry.getUserId(),
                    timeEntry.getTaskId()
            });
            totalHours += timeEntry.getHours();
        }
        JOptionPane.showMessageDialog(this, "Total hours: " + totalHours);
    }
}