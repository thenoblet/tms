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


@WebServlet(name = "TaskServlet", urlPatterns = {"/tasks", "/tasks/*"})
public class TaskServlet extends HttpServlet {
    private TaskService taskService;
    private SimpleDateFormat dateFormatter;

    @Override
    public void init() throws ServletException {
        taskService = new TaskServiceImpl();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

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

    public void listTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Task> tasks = taskService.getAllTasks();
        request.setAttribute("tasks", tasks);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
        dispatcher.forward(request, response);
    }

    public void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/form.jsp");

        dispatcher.forward(request, response);
    }

    public void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            Task task = taskService.getTask(id);

            if (task != null) {
                request.setAttribute("task", task);

                // Add comma-separated tags string for easier form handling
                if (task.getTags() != null && !task.getTags().isEmpty()) {
                    String tagsString = String.join(",", task.getTags());
                    request.setAttribute("tagsString", tagsString);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/form.jsp");
                dispatcher.forward(request, response);
            } else {
                // Task not found, redirect to list
                response.sendRedirect("tasks");
            }
        } catch (NumberFormatException e) {
            // Invalid ID format
            response.sendRedirect("tasks");
        }
    }
    public void showTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Task task = taskService.getTask(id);
        request.setAttribute("task", task);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/view.jsp");
        dispatcher.forward(request, response);
    }

    public void createTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Task newTask = new Task();
        taskService.createTask(mapParamToTask(newTask, request));

        response.sendRedirect("tasks");
    }

    public void deleteTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        taskService.deleteTask(id);

        response.sendRedirect("tasks");
    }

    public void updateTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Task task = taskService.getTask(id);

        taskService.updateTask(mapParamToTask(task, request));
        response.sendRedirect("tasks");

    }


    public void filterTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Task.Status status = Task.Status.valueOf(request.getParameter("status"));
        List<Task> tasks = taskService.getTasksByStatus(status);

        request.setAttribute("tasks", tasks);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");

        dispatcher.forward(request, response);
    }

    public void sortTasks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            boolean ascending = "asc".equalsIgnoreCase(request.getParameter("order"));
            List<Task> tasks = taskService.getAllTasksSortedByDueDate(ascending);

            // Set both attributes to ensure compatibility
            request.setAttribute("tasks", tasks);
            request.setAttribute("sortedTasks", tasks);

            // Forward to the correct view (dashboard.jsp instead of list.jsp)
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/task/list.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Sorting failed", e);
        }
    }

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
