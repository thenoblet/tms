package tms.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tms.model.Task;
import tms.service.TaskService;
import tms.service.TaskServiceImpl;

/**
 * Servlet controller for managing task operations in the Task Management System.
 * Handles all CRUD operations for tasks including listing, creating, updating,
 * deleting, filtering and sorting tasks.
 */
@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks", "/tasks/*"})
public class TaskServlet extends HttpServlet {
    private TaskService taskService;
    private SimpleDateFormat dateFormatter;

    /**
     * Initializes the servlet and its dependencies.
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() throws ServletException {
        taskService = new TaskServiceImpl();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Handles HTTP GET requests for task operations.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

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

        if (action == null) {
            action = "create";
        }

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
        List<Task> tasks = taskService.getAllTasks();
        request.setAttribute("tasks", tasks);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
        dispatcher.forward(request, response);
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
            Task task = taskService.getTask(id);

            if (task != null) {
                request.setAttribute("task", task);

                if (task.getTags() != null && !task.getTags().isEmpty()) {
                    String tagsString = String.join(",", task.getTags());
                    request.setAttribute("tagsString", tagsString);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/form.jsp");
                dispatcher.forward(request, response);
            } else {
                response.sendRedirect("tasks");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("tasks");
        }
    }

    /**
     * Creates a new task based on form submission.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void createTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Task newTask = new Task();
        taskService.createTask(mapParamToTask(newTask, request));
        response.sendRedirect("tasks");
    }

    /**
     * Deletes an existing task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        taskService.deleteTask(id);
        response.sendRedirect("tasks");
    }

    /**
     * Updates an existing task.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void updateTask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Task task = taskService.getTask(id);
        taskService.updateTask(mapParamToTask(task, request));
        response.sendRedirect("tasks");
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
        Task.Status status = Task.Status.valueOf(request.getParameter("status"));
        List<Task> tasks = taskService.getTasksByStatus(status);
        request.setAttribute("tasks", tasks);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Sorts tasks by due date in ascending or descending order.
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void sortTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            boolean ascending = "asc".equalsIgnoreCase(request.getParameter("order"));
            List<Task> tasks = taskService.getAllTasksSortedByDueDate(ascending);

            request.setAttribute("tasks", tasks);
            request.setAttribute("sortedTasks", tasks);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
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

        return task;
    }
}