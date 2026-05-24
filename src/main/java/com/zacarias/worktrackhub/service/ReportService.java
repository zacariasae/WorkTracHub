package com.zacarias.worktrackhub.service;

import com.zacarias.worktrackhub.dao.TimeEntryDAO;
import com.zacarias.worktrackhub.dao.UserDAO;
import com.zacarias.worktrackhub.dao.ProjectDAO;
import com.zacarias.worktrackhub.dao.TaskDAO;
import com.zacarias.worktrackhub.model.TimeEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private TimeEntryDAO timeEntryDAO;
    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;

    public ReportService() {
        timeEntryDAO = new TimeEntryDAO();
        userDAO = new UserDAO();
        projectDAO = new ProjectDAO();
        taskDAO = new TaskDAO();
    }

    public List<TimeEntry> getEntriesByUserAndPeriod(int userId, int month, int year) {
        return timeEntryDAO.findByUserAndPeriod(userId, month, year);
    }

    public List<TimeEntry> getAllTimeEntries() {
        return timeEntryDAO.findAll();
    }

    public float getTotalHoursByUser(int userId) {
        List<TimeEntry> timeEntries = timeEntryDAO.findByUserId(userId);
        float totalHours = 0;
        for (TimeEntry timeEntry : timeEntries) {
            totalHours += timeEntry.getHours();
        }
        return totalHours;
    }

    public float getTotalHoursByUserAndPeriod(int userId, int month, int year) {
        List<TimeEntry> timeEntries = timeEntryDAO.findByUserAndPeriod(userId, month, year);
        float totalHours = 0;
        for (TimeEntry timeEntry : timeEntries) {
            totalHours += timeEntry.getHours();
        }
        return totalHours;
    }

    public void exportToCsv(List<TimeEntry> timeEntries, String filePath) {
        try(FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write("id,date,hours,description,status,user_id,task_id\n");
            for (TimeEntry timeEntry : timeEntries) {
                fileWriter.write(
                        timeEntry.getId() + "," +
                                timeEntry.getDate() + "," +
                                timeEntry.getHours() + "," +
                                timeEntry.getDescription() + "," +
                                timeEntry.getStatus() + "," +
                                timeEntry.getUserId() + "," +
                                timeEntry.getTaskId() + "\n"
                );
            }

            System.out.println("CSV successfully exported to: " + filePath);

        }  catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}