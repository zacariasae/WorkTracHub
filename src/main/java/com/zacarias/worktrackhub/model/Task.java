package com.zacarias.worktrackhub.model;

public class Task {
    private int id;
    private String name;
    private String description;
    private boolean active;
    private int projectId;

    public Task() {}

    public Task(int id, String name, String description, boolean active, int projectId){
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.projectId = projectId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    @Override
    public String toString() {
        return name;
    }
}