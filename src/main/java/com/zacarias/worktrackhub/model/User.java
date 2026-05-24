package com.zacarias.worktrackhub.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private boolean active;

    public User() {}

    public User(int id, String name, String email, String passwordHash, String role, boolean active){
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}