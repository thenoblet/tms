package tms.service;

import tms.dao.TaskDAO;
import tms.dao.TaskDAOImpl;
import tms.exception.ValidationException;
import tms.model.Task;

import java.util.Date;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskDAO taskDAO;

    public TaskServiceImpl() {
        this.taskDAO = new TaskDAOImpl();
    }

    public TaskServiceImpl(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public Task createTask(Task task) {
        validateTask(task);

        return taskDAO.save(task);
    }

    @Override
    public Task getTask(Long id) {
        return taskDAO.findById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskDAO.getTasks();
    }

    @Override
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskDAO.findByStatus(status);
    }

    @Override
    public List<Task> getAllTasksSortedByDueDate(boolean ascending) {
        return taskDAO.findAllSortedByDueDate(ascending);
    }

    @Override
    public Task updateTask(Task task) {
        validateTask(task);
        if (task.getId() == null) {
            throw new ValidationException("Task iD can't be null for update");
        }

        taskDAO.update(task);
        return task;

    }

    @Override
    public void deleteTask(Long id) {
        taskDAO.delete(id);
    }

    public  void validateTask(Task task) {
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
