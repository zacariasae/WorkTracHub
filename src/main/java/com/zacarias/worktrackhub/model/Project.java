package com.zacarias.worktrackhub.model;

public class Project {
    private int id;
    private String name;
    private String description;
    private boolean active;
    private int userId;

    public Project() {}

    public Project(int id, String name, String description, boolean active,  int userId){
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId;    }

    @Override
    public String toString() {
        return name;
    }
}