package tms.model;

import java.util.List;
import java.sql.Date;

/**
 * Represents a task in the Task Management System.
 * Contains task details including title, description, priority, due date,
 * status, and associated tags.
 */
public class Task {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private Date dueDate;
    private Status status;
    private List<String> tags;

    /**
     * Enumeration of possible task statuses.
     * PENDING - Task has been created but not started
     * IN_PROGRESS - Task is currently being worked on
     * COMPLETED - Task has been successfully finished
     * FAILED - Task could not be completed
     */
    public enum Status {
        PENDING, COMPLETED, FAILED, IN_PROGRESS
    }

    /**
     * Default constructor.
     */
    public Task() {}

    /**
     * Constructs a Task with specified parameters.
     *
     * @param id the unique identifier for the task
     * @param description detailed description of the task
     * @param priority priority level of the task
     * @param dueDate date when the task should be completed
     * @param status current status of the task
     * @param tags list of tags associated with the task
     */
    public Task(Long id, String description, String priority, Date dueDate,
                Status status, List<String> tags) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.tags = tags;
    }

    /**
     * Gets the task ID.
     * @return the task's unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the task ID.
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the task title.
     * @return the title of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the task title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the task description.
     * @return the detailed description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the task description.
     * @param description the detailed description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the task priority.
     * @return the priority level
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the task priority.
     * @param priority the priority level to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Gets the task due date.
     * @return the completion target date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the task due date.
     * @param dueDate the completion target date to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the task status.
     * @return current status of the task
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the task status.
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the task tags.
     * @return list of associated tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the task tags.
     * @param tags list of tags to associate with the task
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}