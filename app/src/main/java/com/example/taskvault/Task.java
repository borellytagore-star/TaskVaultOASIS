package com.example.taskvault;

public class Task {

    private int id;
    private int userId;
    private String taskName;
    private String notes;
    private boolean completed;

    public Task(
            int id,
            int userId,
            String taskName,
            String notes,
            boolean completed) {

        this.id = id;
        this.userId = userId;
        this.taskName = taskName;
        this.notes = notes;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}