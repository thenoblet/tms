package tms.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tms.model.Task;
import tms.service.TaskService;
import tms.service.TaskServiceImpl;
import tms.util.logging.ColorConsoleFormatter;

/**
 * Servlet controller for managing task operations in the Task Management System.
 * Handles all CRUD operations for tasks including listing, creating, updating,
 * deleting, filtering and sorting tasks.
 */
@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks", "/tasks/*"})
public class TaskServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TaskServlet.class.getName());

    static {
        configureLogging();
    }

    /**
     * Configures logging format, console coloring, and file logging.
     */
    private static void configureLogging() {
        try {
            System.setProperty("java.util.logging.SimpleFormatter.format\n",
                    "%1$tY-%m-%d %1$tH:%1$tM:%1$tS.%1$tL %4$s [%2$s] %5$s%6$s%n");

            Logger appLogger = Logger.getLogger("gtp.ems");
            appLogger.setUseParentHandlers(false); // Don't inherit root handlers

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new ColorConsoleFormatter());
            appLogger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("ems-app.log", false);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            appLogger.addHandler(fileHandler);

            appLogger.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
        }
    }

    private TaskService taskService;
    private SimpleDateFormat dateFormatter;

    /**
     * Initializes the servlet and its dependencies.
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() throws ServletException {
        try {
            taskService = new TaskServiceImpl();
            dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            LOGGER.info("TaskServlet initialized successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize TaskServlet: " + e.getMessage());
            throw new ServletException("Initialization failed", e);
        }
    }

    /**
     * Handles HTTP GET requests for task operations.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        String action = request.getParameter("action");
        action = (action == null) ? "list" : action;

        String finalAction = action;
        LOGGER.fine(() -> "Processing GET request with action: " + finalAction);

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteTask(request, response);
                    break;
                case "filter":
                    filterTasks(request, response);
                    break;
                case "sort":
                    sortTasks(request, response);
                    break;
                default:
                    listTasks(request, response);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing GET request for action: " + action, ex);
            throw new ServletException(ex);
        }
    }

    /**
     * Handles HTTP POST requests for task operations.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String action = request.getParameter("action");
        action = (action == null) ? "create" : action;

        String finalAction = action;
        LOGGER.fine(() -> "Processing POST request with action: " + finalAction);

        try {
            switch (action) {
                case "create":
                    createTask(request, response);
                    break;
                case "update":
                    updateTask(request, response);
                    break;
                default:
                    listTasks(request, response);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing POST request for action: " + action, ex);
            throw new ServletException(ex);
        }
    }

    /**
     * Displays the list of all tasks.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void listTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            LOGGER.fine("Listing all tasks");
            List<Task> tasks = taskService.getAllTasks();
            request.setAttribute("tasks", tasks);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
            dispatcher.forward(request, response);
            LOGGER.fine("Successfully listed tasks");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error listing tasks", e);
            throw e;
        }
    }

    /**
     * Shows the form for creating a new task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/form.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Shows the form for editing an existing task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            LOGGER.fine(() -> "Displaying edit form for task ID: " + id);

            Task task = taskService.getTask(id);
            if (task == null) {
                LOGGER.warning(() -> "Task not found with ID: " + id);
                response.sendRedirect("tasks");
                return;
            }

            request.setAttribute("task", task);
            request.setAttribute("isNew", false);

            if (task.getTags() != null && !task.getTags().isEmpty()) {
                String tagsString = String.join(",", task.getTags());
                request.setAttribute("tagsString", tagsString);
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/form.jsp");
            dispatcher.forward(request, response);
            LOGGER.fine(() -> "Edit form displayed successfully for task ID: " + id);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid task ID format: " + request.getParameter("id"));
            response.sendRedirect("tasks");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error displaying edit form", e);
            throw e;
        }
    }

    /**
     * Creates a new task based on form submission.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    public void createTask(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            LOGGER.fine("Creating new task");
            Task newTask = new Task();
            taskService.createTask(mapParamToTask(newTask, request));
            LOGGER.info(() -> "Successfully created new task with title: " + newTask.getTitle());
            response.sendRedirect("tasks");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating task", e);
            throw e;
        }
    }

    /**
     * Deletes an existing task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    public void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            LOGGER.fine(() -> "Deleting task with ID: " + id);
            taskService.deleteTask(id);
            LOGGER.info(() -> "Successfully deleted task with ID: " + id);
            response.sendRedirect("tasks");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting task", e);
            throw e;
        }
    }

    /**
     * Updates an existing task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    public void updateTask(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            LOGGER.fine(() -> "Updating task with ID: " + id);

            Task task = taskService.getTask(id);
            if (task == null) {
                LOGGER.warning(() -> "Task not found for update with ID: " + id);
                response.sendRedirect("tasks");
                return;
            }

            taskService.updateTask(mapParamToTask(task, request));
            LOGGER.info(() -> "Successfully updated task with ID: " + id + " and title: " + task.getTitle());
            response.sendRedirect("tasks");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating task", e);
            throw e;
        }
    }

    /**
     * Filters tasks by status.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void filterTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Task.Status status = Task.Status.valueOf(request.getParameter("status"));
            LOGGER.fine(() -> "Filtering tasks by status: " + status);

            List<Task> tasks = taskService.getTasksByStatus(status);
            request.setAttribute("tasks", tasks);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
            dispatcher.forward(request, response);
            LOGGER.fine(() -> "Successfully filtered tasks by status: " + status);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering tasks", e);
            throw e;
        }
    }

    /**
     * Sorts tasks by due date in ascending or descending order.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     */
    public void sortTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            boolean ascending = "asc".equalsIgnoreCase(request.getParameter("order"));
            LOGGER.fine(() -> "Sorting tasks by due date (" + (ascending ? "ascending" : "descending") + ")");

            List<Task> tasks = taskService.getAllTasksSortedByDueDate(ascending);
            request.setAttribute("tasks", tasks);
            request.setAttribute("sortedTasks", tasks);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
            dispatcher.forward(request, response);
            LOGGER.fine("Successfully sorted tasks by due date");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting tasks", e);
            throw new ServletException("Sorting failed", e);
        }
    }

    /**
     * Maps request parameters to a Task object.
     * @param task the Task object to populate
     * @param request the HttpServletRequest containing parameters
     * @return the populated Task object
     */
    private Task mapParamToTask(Task task, HttpServletRequest request) {
        try {
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                task.setId(Long.parseLong(idParam));
            }

            task.setTitle(request.getParameter("title"));
            task.setDescription(request.getParameter("description"));
            task.setDueDate(Date.valueOf(request.getParameter("dueDate")));
            task.setPriority(request.getParameter("priority"));
            task.setStatus(Task.Status.valueOf(request.getParameter("status")));

            String tagsParam = request.getParameter("tags");
            if (tagsParam != null && !tagsParam.isEmpty()) {
                task.setTags(List.of(tagsParam.split("\\s*,\\s*")));
            } else {
                task.setTags(Collections.emptyList());
            }

            LOGGER.finest(() -> "Mapped parameters to task: " + task);
            return task;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error mapping parameters to task", e);
            throw e;
        }
    }
}
