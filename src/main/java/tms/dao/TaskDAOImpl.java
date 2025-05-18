package tms.dao;

import tms.exception.DataAccessException;
import tms.model.Task;
import tms.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Implementation of the TaskDAO interface for database operations related to tasks.
 * Handles CRUD operations for tasks including saving, updating, deleting, and querying tasks.
 * Manages task-tag relationships and supports sorting and filtering operations.
 */
public class TaskDAOImpl implements TaskDAO {

    /**
     * Saves a new task to the database including its tags.
     * Performs the operation within a transaction.
     *
     * @param task the task to be saved
     * @return the saved task with generated ID
     * @throws DataAccessException if database access fails or operation fails
     */
    @Override
    public Task save(Task task) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String taskSql = "INSERT INTO tasks (title, description, priority, due_date, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement taskStmt = conn.prepareStatement(taskSql, Statement.RETURN_GENERATED_KEYS);

            taskStmt.setString(1, task.getTitle());
            taskStmt.setString(2, task.getDescription());
            taskStmt.setString(3, task.getPriority());
            taskStmt.setDate(4, new java.sql.Date(task.getDueDate().getTime()));
            taskStmt.setString(5, task.getStatus().name());

            int affectedRows = taskStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed");
            }

            try (ResultSet generatedKeys = taskStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getLong(1));
                }
            }

            if (task.getTags() != null && !task.getTags().isEmpty()) {
                saveTags(conn, task.getId(), task.getTags());
            }

            conn.commit();
            return task;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DataAccessException("Failed to rollback transaction", ex);
                }
            }
            throw new DataAccessException("Failed to save task", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Saves tags for a task and creates the task-tag relationships.
     *
     * @param conn the database connection to use
     * @param taskId the ID of the task
     * @param tags the list of tags to save
     * @throws SQLException if database access fails
     */
    private void saveTags(Connection conn, Long taskId, List<String> tags) throws SQLException {
        String deleteSql = "DELETE FROM task_tags WHERE task_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setLong(1, taskId);
            deleteStmt.executeUpdate();
        }

        String insertTagSql = "INSERT INTO tags (name) VALUES (?) ON CONFLICT (name) DO NOTHING";
        String insertTaskTagSql = "INSERT INTO task_tags (task_id, tag_id) VALUES (?, (SELECT id FROM tags WHERE name = ?))";

        try (PreparedStatement tagStmt = conn.prepareStatement(insertTagSql);
             PreparedStatement taskTagStmt = conn.prepareStatement(insertTaskTagSql)) {

            for (String tagName : tags) {
                tagStmt.setString(1, tagName);
                tagStmt.executeUpdate();

                taskTagStmt.setLong(1, taskId);
                taskTagStmt.setString(2, tagName);
                taskTagStmt.executeUpdate();
            }
        }
    }

    /**
     * Finds a task by its ID including its associated tags.
     *
     * @param id the ID of the task to find
     * @return the found task or null if not found
     * @throws DataAccessException if database access fails
     */
    @Override
    public Task findById(Long id) {
        String sql = "SELECT t.*, array_agg(tg.name) as tags " +
                "FROM tasks t " +
                "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                "LEFT JOIN tags tg ON tt.tag_id = tg.id " +
                "WHERE t.id = ? " +
                "GROUP BY t.id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultToTask(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find task with id:" + id, e);
        }
    }

    /**
     * Retrieves all tasks from the database including their tags.
     *
     * @return list of all tasks
     * @throws DataAccessException if database access fails
     */
    @Override
    public List<Task> getTasks() {
        String sql = "SELECT t.*, array_agg(tg.name) as tags " +
                "FROM tasks t " +
                "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                "LEFT JOIN tags tg ON tt.tag_id = tg.id " +
                "GROUP BY t.id";

        return getTasksByQuery(sql);
    }

    /**
     * Finds tasks by their status including their tags.
     *
     * @param status the status to filter by
     * @return list of tasks with the specified status
     * @throws DataAccessException if database access fails
     */
    @Override
    public List<Task> findByStatus(Task.Status status) {
        String sql = "SELECT t.*, array_agg(tg.name) as tags " +
                "FROM tasks t " +
                "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                "LEFT JOIN tags tg ON tt.tag_id = tg.id " +
                "WHERE t.status = ? " +
                "GROUP BY t.id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(mapResultToTask(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find tasks by status:" + status, e);
        }
    }

    /**
     * Retrieves all tasks sorted by due date including their tags.
     *
     * @param ascending true for ascending order, false for descending
     * @return list of tasks sorted by due date
     * @throws DataAccessException if database access fails
     */
    @Override
    public List<Task> findAllSortedByDueDate(boolean ascending) {
        String sql = "SELECT t.*, array_agg(tg.name) as tags " +
                "FROM tasks t " +
                "LEFT JOIN task_tags tt ON t.id = tt.task_id " +
                "LEFT JOIN tags tg ON tt.tag_id = tg.id " +
                "GROUP BY t.id " +
                "ORDER BY t.due_date " + (ascending ? "ASC" : "DESC");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Task> tasks = new ArrayList<>();

            while (rs.next()) {
                tasks.add(mapResultToTask(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to sort tasks", e);
        }
    }

    /**
     * Helper method to execute a query and return tasks.
     *
     * @param sql the SQL query to execute
     * @return list of tasks from the query result
     * @throws DataAccessException if database access fails
     */
    private List<Task> getTasksByQuery(String sql) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Task> tasks = new ArrayList<>();

            while (rs.next()) {
                tasks.add(mapResultToTask(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get tasks", e);
        }
    }

    /**
     * Updates an existing task including its tags.
     * Performs the operation within a transaction.
     *
     * @param task the task with updated information
     * @throws DataAccessException if database access fails or operation fails
     */
    @Override
    public void update(Task task) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String taskSql = "UPDATE tasks SET title = ?, description = ?, priority = ?, due_date = ?, status = ? WHERE id = ?";
            try (PreparedStatement taskStmt = conn.prepareStatement(taskSql)) {
                taskStmt.setString(1, task.getTitle());
                taskStmt.setString(2, task.getDescription());
                taskStmt.setString(3, task.getPriority());
                taskStmt.setDate(4, new java.sql.Date(task.getDueDate().getTime()));
                taskStmt.setString(5, task.getStatus().name());
                taskStmt.setLong(6, task.getId());
                taskStmt.executeUpdate();
            }

            if (task.getTags() != null) {
                saveTags(conn, task.getId(), task.getTags());
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DataAccessException("Failed to rollback transaction", ex);
                }
            }
            throw new DataAccessException("Failed to update task", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Deletes a task by its ID.
     * Uses ON DELETE CASCADE for related task_tags records.
     *
     * @param id the ID of the task to delete
     * @throws DataAccessException if database access fails
     */
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete task with id:" + id, e);
        }
    }

    /**
     * Maps a database ResultSet to a Task object.
     *
     * @param rs the ResultSet containing task data
     * @return the populated Task object
     * @throws SQLException if database access fails
     */
    private Task mapResultToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setPriority(rs.getString("priority"));
        task.setDueDate(rs.getDate("due_date"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));

        Array tagsArray = rs.getArray("tags");
        if (tagsArray != null) {
            String[] tagNames = (String[]) tagsArray.getArray();
            if (tagNames != null && tagNames.length > 0) {
                task.setTags(Arrays.asList(tagNames));
            } else {
                task.setTags(Collections.emptyList());
            }
        } else {
            task.setTags(Collections.emptyList());
        }

        return task;
    }
}