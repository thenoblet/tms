package tms.service;

import tms.model.Task;

import java.util.List;

/**
 * Service interface for managing Task entities.
 * Provides CRUD operations and additional task retrieval methods.
 */
public interface TaskService {
    /**
     * Creates a new task in the system.
     *
     * @param task The task to create.
     */
    void createTask(Task task);

    /**
     * Retrieves a task by its ID.
     * @param id The ID of the task to fetch.
     * @return The task if found, otherwise null.
     */
    Task getTask(Long id);

    /**
     * Retrieves all tasks in the system.
     * @return A list of all tasks.
     */
    List<Task> getAllTasks();

    /**
     * Retrieves tasks filtered by status.
     * @param status The status to filter by (e.g., PENDING, COMPLETED).
     * @return A list of tasks matching the given status.
     */
    List<Task> getTasksByStatus(Task.Status status);

    /**
     * Retrieves all tasks sorted by due date.
     * @param ascending If true, sorts in ascending order (earliest first).
     *                 If false, sorts in descending order (latest first).
     * @return A list of tasks sorted by due date.
     */
    List<Task> getAllTasksSortedByDueDate(boolean ascending);

    /**
     * Updates an existing task.
     * @param task The task with updated fields.
     * @return The updated task.
     */
    Task updateTask(Task task);

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     */
    void deleteTask(Long id);
}