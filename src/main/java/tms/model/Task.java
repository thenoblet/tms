package tms.model;

//import java.util.Date;
import java.util.List;

import java.sql.Date;

public class Task {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private Date dueDate;
    private Status status;
    private List<String> tags;

    public enum Status {
        PENDING, COMPLETED, FAILED, IN_PROGRESS
    }

    public Task() {}

    public Task(Long id, String description, String priority, Date dueDate, Status status, List<String> tag) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.tags = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
