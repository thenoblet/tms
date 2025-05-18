package tms.service;

import tms.dao.TaskDAO;
import tms.dao.TaskDAOImpl;
import tms.exception.ValidationException;
import tms.model.Task;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link TaskService} interface.
 * Provides business logic and validation for task operations,
 * delegating data persistence to the {@link TaskDAO}.
 */
public class TaskServiceImpl implements TaskService {
    private final TaskDAO taskDAO;

    /**
     * Constructs a new TaskServiceImpl with default DAO implementation.
     */
    public TaskServiceImpl() {
        this.taskDAO = new TaskDAOImpl();
    }

    /**
     * Constructs a new TaskServiceImpl with a specified DAO implementation.
     * Primarily used for testing with mock DAOs.
     *
     * @param taskDAO the TaskDAO implementation to use
     */
    public TaskServiceImpl(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /**
     * Creates a new task after validating its fields.
     *
     * @param task the task to create
     * @throws ValidationException if task validation fails
     */
    @Override
    public void createTask(Task task) {
        validateTask(task);
        taskDAO.save(task);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task to retrieve
     * @return the found task, or null if not found
     */
    @Override
    public Task getTask(Long id) {
        return taskDAO.findById(id);
    }

    /**
     * Retrieves all tasks in the system.
     *
     * @return a list of all tasks
     */
    @Override
    public List<Task> getAllTasks() {
        return taskDAO.getTasks();
    }

    /**
     * Retrieves tasks filtered by status.
     *
     * @param status the status to filter by
     * @return a list of tasks with the specified status
     */
    @Override
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskDAO.findByStatus(status);
    }

    /**
     * Retrieves all tasks sorted by due date.
     *
     * @param ascending true for ascending order, false for descending
     * @return a sorted list of tasks
     */
    @Override
    public List<Task> getAllTasksSortedByDueDate(boolean ascending) {
        return taskDAO.findAllSortedByDueDate(ascending);
    }

    /**
     * Updates an existing task after validating its fields.
     *
     * @param task the task with updated information
     * @return the updated task
     * @throws ValidationException if task validation fails or ID is null
     */
    @Override
    public Task updateTask(Task task) {
        validateTask(task);
        if (task.getId() == null) {
            throw new ValidationException("Task ID can't be null for update");
        }
        taskDAO.update(task);
        return task;
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     */
    @Override
    public void deleteTask(Long id) {
        taskDAO.delete(id);
    }

    /**
     * Validates a task's fields according to business rules.
     *
     * @param task the task to validate
     * @throws ValidationException if any validation rule is violated
     */
    public void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new ValidationException("Task title is required");
        }

        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new ValidationException("Task description is required");
        }

        if (task.getPriority() == null || task.getPriority().trim().isEmpty()) {
            throw new ValidationException("Task priority is required");
        }

        if (task.getDueDate() == null) {
            throw new ValidationException("Due date is required");
        }

        if (task.getDueDate().before(new Date())) {
            throw new ValidationException("Due date cannot be in the past");
        }

        if (task.getStatus() == null) {
            task.setStatus(Task.Status.PENDING);
        }
    }
}