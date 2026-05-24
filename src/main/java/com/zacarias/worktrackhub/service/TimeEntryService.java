package com.zacarias.worktrackhub.service;

import com.zacarias.worktrackhub.dao.TimeEntryDAO;
import com.zacarias.worktrackhub.model.TimeEntry;

import java.time.LocalDate;
import java.util.List;

public class TimeEntryService {

    private TimeEntryDAO timeEntryDAO;

    public TimeEntryService() {
        timeEntryDAO = new TimeEntryDAO();
    }

    public void createTimeEntry(TimeEntry timeEntry) {
        validate(timeEntry);
        timeEntry.setStatus("DRAFT");
        timeEntryDAO.create(timeEntry);
    }

    public TimeEntry getTimeEntryById(int id) {
        return timeEntryDAO.findById(id);
    }

    public List<TimeEntry> getTimeEntriesByUserId(int userId) {
        return timeEntryDAO.findByUserId(userId);
    }

    public List<TimeEntry> getTimeEntriesByUserAndPeriod(int userId, int month, int year) {
        return timeEntryDAO.findByUserAndPeriod(userId, month, year);
    }

    public List<TimeEntry> getAllTimeEntries() {
        return timeEntryDAO.findAll();
    }

    public void updateTimeEntry(TimeEntry timeEntry) {
        TimeEntry oldTimeEntry = timeEntryDAO.findById(timeEntry.getId());

        if(oldTimeEntry == null) {
            throw new IllegalArgumentException("TimeEntry does not exist");
        }
        if(oldTimeEntry.getStatus().equals("SUBMITTED")) {
            throw new IllegalArgumentException("You cannot edit a submitted time entry");
        }

        validate(timeEntry);
        timeEntryDAO.update(timeEntry);
    }

    public void submitTimeEntry(int id) {
        TimeEntry timeEntry = timeEntryDAO.findById(id);

        if(timeEntry == null) {
            throw new IllegalArgumentException("TimeEntry does not exist");
        }
        if(timeEntry.getStatus().equals("SUBMITTED")) {
            throw new IllegalArgumentException("TimeEntry is already submitted");
        }

        timeEntryDAO.updateStatus(id, "SUBMITTED");
    }

    public void recoverTimeEntry(int id) {
        TimeEntry timeEntry = timeEntryDAO.findById(id);

        if(timeEntry == null) {
            throw new IllegalArgumentException("TimeEntry does not exist");
        }
        if(timeEntry.getStatus().equals("DRAFT")) {
            throw new IllegalArgumentException("TimeEntry is already draft");
        }

        timeEntryDAO.updateStatus(id, "DRAFT");
    }

    public void deleteTimeEntry(int id) {
        TimeEntry timeEntry = timeEntryDAO.findById(id);

        if(timeEntry == null) {
            throw new IllegalArgumentException("TimeEntry does not exist");
        }
        if(timeEntry.getStatus().equals("SUBMITTED")) {
            throw new IllegalArgumentException("You cannot delete a submitted time entry");
        }

        timeEntryDAO.delete(id);
    }

    public void validate(TimeEntry timeEntry) {
        if(timeEntry.getDate() == null) {
            throw new IllegalArgumentException("TimeEntry date is required");
        }
        if(timeEntry.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("TimeEntry date is after now");
        }
        if(timeEntry.getHours() <= 0) {
            throw new IllegalArgumentException("Hours must be greater than 0");
        }
        if(timeEntry.getTaskId() <= 0) {
            throw new IllegalArgumentException("Must select a task");
        }
    }
}