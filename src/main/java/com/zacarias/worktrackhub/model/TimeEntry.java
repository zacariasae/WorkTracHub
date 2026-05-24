package com.zacarias.worktrackhub.model;

import java.time.LocalDate;

public class TimeEntry {
    private int id;
    private LocalDate date;
    private float hours;
    private String description;
    private String status;
    private int userId;
    private int taskId;

    public TimeEntry() {}

    public TimeEntry(int id, LocalDate date, float hours, String description, String status, int userId, int taskId){
        this.id = id;
        this.date = date;
        this.hours = hours;
        this.description = description;
        this.status = status;
        this.userId = userId;
        this.taskId = taskId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public float getHours() { return hours; }
    public void setHours(float hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
}