package com.zacarias.worktrackhub.service;

import com.zacarias.worktrackhub.dao.ProjectDAO;
import com.zacarias.worktrackhub.dao.TaskDAO;
import com.zacarias.worktrackhub.model.Project;
import com.zacarias.worktrackhub.model.Task;

import java.util.List;

public class ProjectService {

    private ProjectDAO projectDAO;
    private TaskDAO taskDAO;

    public ProjectService() {

        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
    }

    public void createProject(Project project) {
        if(project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        projectDAO.create(project);
    }

    public Project getProjectById(int id) {
        return projectDAO.findById(id);
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectDAO.findAllActive();
    }

    public void updateProject(Project project) {
        if(project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        projectDAO.update(project);
    }

    public void deactivateProject(int id) {
        projectDAO.deactivate(id);
    }

    public void reactivateProject(int id) {
        projectDAO.reactivate(id);
    }

    public void createTask(Task task) {
        if(task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        taskDAO.create(task);
    }

    public List<Task> getTasksByProjectId(int id) {
        return taskDAO.findByProjectId(id);
    }

    public List<Task> getActiveTasksByProjectId(int id) {
        return taskDAO.findActiveByProjectId(id);
    }

    public void updateTask(Task task) {
        if(task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        taskDAO.update(task);
    }

    public void deactivateTask(int id) {
        taskDAO.deactivate(id);
    }

    public void reactivateTask(int id) {
        taskDAO.reactivate(id);
    }
}