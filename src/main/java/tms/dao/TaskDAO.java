package tms.dao;

import tms.model.Task;

import java.util.List;

/**
 * Data Access Object interface for Task entities.
 * Defines database operations for Task persistence.
 */
public interface TaskDAO {
    /**
     * Persists a new task in the database.
     * @param task The task to be saved.
     * @return The saved task with generated identifier.
     */
    Task save(Task task);

    /**
     * Retrieves a task by its unique identifier.
     * @param id The ID of the task to find.
     * @return The found task or null if not exists.
     */
    Task findById(Long id);

    /**
     * Retrieves all tasks from the database.
     * @return List of all tasks.
     */
    List<Task> getTasks();

    /**
     * Finds tasks by their status.
     * @param status The status to filter by.
     * @return List of tasks with matching status.
     */
    List<Task> findByStatus(Task.Status status);

    /**
     * Retrieves all tasks sorted by due date.
     * @param ascending True for ascending order, false for descending.
     * @return List of tasks sorted by due date.
     */
    List<Task> findAllSortedByDueDate(boolean ascending);

    /**
     * Updates an existing task in the database.
     * @param task The task with updated values.
     */
    void update(Task task);

    /**
     * Deletes a task from the database.
     * @param id The ID of the task to delete.
     */
    void delete(Long id);
}