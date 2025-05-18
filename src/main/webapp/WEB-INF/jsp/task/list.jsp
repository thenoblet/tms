<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
<script src="${pageContext.request.contextPath}/js/script.js" defer></script>

<div class="container-fluid dashboard-container">
  <div class="row mb-4">
    <div class="col">
      <h1 class="fw-bold mt-4 mb-4">Task Dashboard</h1>
    </div>
  </div>

  <div class="row mb-4">
    <div class="col">
      <a href="tasks?action=new" class="btn btn-primary px-4" aria-label="Add new task">
        <i class="fas fa-plus-circle me-2"></i>Add New Task
      </a>

      <div class="d-inline-block ms-4 filter-buttons">
        <a href="tasks?action=filter&status=PENDING" class="btn pending-btn" aria-label="Filter pending tasks">Pending Tasks</a>
        <a href="tasks?action=filter&status=COMPLETED" class="btn completed-btn" aria-label="Filter completed tasks">Completed Tasks</a>
        <a href="tasks?action=filter&status=IN_PROGRESS" class="btn progress-btn" aria-label="Filter in progress tasks">In Progress Tasks</a>
        <a href="tasks?action=filter&status=FAILED" class="btn failed-btn" aria-label="Filter failed tasks">Failed Tasks</a>
      </div>

      <div class="d-inline-block ms-4">
        <a href="tasks?action=sort&order=asc" class="btn btn-outline-secondary" aria-label="Sort tasks by due date ascending">
          <i class="fas fa-sort-amount-down-alt me-2"></i>Sort by Due Date (Asc)
        </a>
        <a href="tasks?action=sort&order=desc" class="btn btn-outline-secondary" aria-label="Sort tasks by due date descending">
          <i class="fas fa-sort-amount-down me-2"></i>Sort by Due Date (Desc)
        </a>
      </div>
    </div>
  </div>

  <c:if test="${not empty message}">
    <div class="alert alert-success">${message}</div>
  </c:if>

  <div class="task-board">
    <div class="task-column pending-column">
      <div class="column-header">
        <div class="status-indicator pending"></div>
        <h3>To do <span class="task-count">
          <c:set var="pendingCount" value="0"/>
          <c:forEach var="task" items="${tasks}">
            <c:if test="${task.status == 'PENDING'}">
              <c:set var="pendingCount" value="${pendingCount + 1}"/>
            </c:if>
          </c:forEach>
          ${pendingCount}
        </span></h3>
        <div class="column-actions">
          <button class="btn-add" aria-label="Add task to this column"><i class="fas fa-plus"></i></button>
          <button class="btn-more" aria-label="More options"><i class="fas fa-ellipsis-h"></i></button>
        </div>
      </div>

      <div class="tasks-container">
        <c:forEach var="task" items="${tasks}">
          <c:if test="${task.status == 'PENDING'}">
            <div class="task-card" aria-label="Task: ${task.title}">
              <div class="task-header">
                <div class="task-status ${fn:toLowerCase(task.status)}" onclick="toggleDropdown(this)">
                  <c:set var="statusDisplay" value="Not Started"/>
                  <c:set var="statusClass" value="not-started"/>
                  <c:if test="${not empty task.tags && fn:length(task.tags) > 0}">
                    <c:choose>
                      <c:when test="${task.tags[0] == 'Research'}">
                        <c:set var="statusDisplay" value="In Research"/>
                        <c:set var="statusClass" value="research"/>
                      </c:when>
                      <c:when test="${task.tags[0] == 'On Track'}">
                        <c:set var="statusDisplay" value="On Track"/>
                        <c:set var="statusClass" value="on-track"/>
                      </c:when>
                    </c:choose>
                  </c:if>
                  <div class="status-dot ${statusClass}"></div>
                  <span>${statusDisplay}</span>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit Task
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete Task
                    </a>
                  </div>
                </div>
                <div class="task-actions">
                  <button class="btn-more" aria-label="More options" onclick="toggleDropdown(this)">
                    <i class="fas fa-ellipsis-h"></i>
                  </button>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete
                    </a>
                  </div>
                </div>
              </div>

              <h4 class="task-title">${not empty task.title ? task.title : 'Untitled Task'}</h4>
              <p class="task-description">${not empty task.description ? task.description : 'No description available'}</p>

              <div class="due-date">
                <i class="far fa-calendar-alt" aria-hidden="true"></i>
                <fmt:formatDate value="${task.dueDate}" pattern="dd MMM yyyy" />
              </div>

              <div class="priority-badge ${fn:toLowerCase(task.priority)}">
                  ${task.priority}
              </div>

              <div class="task-footer">
                <div class="task-tags">
                  <c:forEach var="tag" items="${task.tags}">
                    <span class="task-tag" data-tag="${fn:toLowerCase(tag)}">${tag}</span>
                  </c:forEach>
                  <c:if test="${empty task.tags}">
                    <span class="no-tags">No tags</span>
                  </c:if>
                </div>
              </div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </div>

    <div class="task-column progress-column">
      <div class="column-header">
        <div class="status-indicator progress"></div>
        <h3>In Progress <span class="task-count">
          <c:set var="progressCount" value="0"/>
          <c:forEach var="task" items="${tasks}">
            <c:if test="${task.status == 'IN_PROGRESS'}">
              <c:set var="progressCount" value="${progressCount + 1}"/>
            </c:if>
          </c:forEach>
          ${progressCount}
        </span></h3>
        <div class="column-actions">
          <button class="btn-add" aria-label="Add task to this column"><i class="fas fa-plus"></i></button>
          <button class="btn-more" aria-label="More options"><i class="fas fa-ellipsis-h"></i></button>
        </div>
      </div>

      <div class="tasks-container">
        <c:forEach var="task" items="${tasks}">
          <c:if test="${task.status == 'IN_PROGRESS'}">
            <div class="task-card" aria-label="Task: ${task.title}">
              <div class="task-header">
                <div class="task-status ${fn:toLowerCase(task.status)}" onclick="toggleDropdown(this)">
                  <c:set var="statusDisplay" value="In Progress"/>
                  <c:set var="statusClass" value="in-progress"/>
                  <c:if test="${not empty task.tags && fn:length(task.tags) > 0}">
                    <c:choose>
                      <c:when test="${task.tags[0] == 'Research'}">
                        <c:set var="statusDisplay" value="In Research"/>
                        <c:set var="statusClass" value="research"/>
                      </c:when>
                      <c:when test="${task.tags[0] == 'On Track'}">
                        <c:set var="statusDisplay" value="On Track"/>
                        <c:set var="statusClass" value="on-track"/>
                      </c:when>
                    </c:choose>
                  </c:if>
                  <div class="status-dot ${statusClass}"></div>
                  <span>${statusDisplay}</span>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit Task
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete Task
                    </a>
                  </div>
                </div>
                <div class="task-actions">
                  <button class="btn-more" aria-label="More options" onclick="toggleDropdown(this)">
                    <i class="fas fa-ellipsis-h"></i>
                  </button>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete
                    </a>
                  </div>
                </div>
              </div>

              <h4 class="task-title">${not empty task.title ? task.title : 'Untitled Task'}</h4>
              <p class="task-description">${not empty task.description ? task.description : 'No description available'}</p>

              <div class="due-date">
                <i class="far fa-calendar-alt" aria-hidden="true"></i>
                <fmt:formatDate value="${task.dueDate}" pattern="dd MMM yyyy" />
              </div>

              <div class="priority-badge ${fn:toLowerCase(task.priority)}">
                  ${task.priority}
              </div>

              <div class="task-footer">
                <div class="task-tags">
                  <c:forEach var="tag" items="${task.tags}">
                    <span class="task-tag" data-tag="${fn:toLowerCase(tag)}">${tag}</span>
                  </c:forEach>
                  <c:if test="${empty task.tags}">
                    <span class="no-tags">No tags</span>
                  </c:if>
                </div>
              </div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </div>

    <div class="task-column completed-column">
      <div class="column-header">
        <div class="status-indicator completed"></div>
        <h3>Done <span class="task-count">
          <c:set var="completedCount" value="0"/>
          <c:forEach var="task" items="${tasks}">
            <c:if test="${task.status == 'COMPLETED'}">
              <c:set var="completedCount" value="${completedCount + 1}"/>
            </c:if>
          </c:forEach>
          ${completedCount}
        </span></h3>
        <div class="column-actions">
          <button class="btn-add" aria-label="Add task to this column"><i class="fas fa-plus"></i></button>
          <button class="btn-more" aria-label="More options"><i class="fas fa-ellipsis-h"></i></button>
        </div>
      </div>

      <div class="tasks-container">
        <c:forEach var="task" items="${tasks}">
          <c:if test="${task.status == 'COMPLETED'}">
            <div class="task-card" aria-label="Task: ${task.title}">
              <div class="task-header">
                <div class="task-status ${fn:toLowerCase(task.status)}" onclick="toggleDropdown(this)">
                  <div class="status-dot complete"></div>
                  <span>Complete</span>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit Task
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete Task
                    </a>
                  </div>
                </div>
                <div class="task-actions">
                  <button class="btn-more" aria-label="More options" onclick="toggleDropdown(this)">
                    <i class="fas fa-ellipsis-h"></i>
                  </button>
                  <div class="dropdown-menu">
                    <a href="tasks?action=edit&id=${task.id}" class="dropdown-item">
                      <i class="fas fa-edit"></i> Edit
                    </a>
                    <a href="#" class="dropdown-item delete"
                       data-task-id="${task.id}"
                       data-task-title="${fn:escapeXml(task.title)}">
                      <i class="fas fa-trash"></i> Delete
                    </a>
                  </div>
                </div>
              </div>

              <h4 class="task-title">${not empty task.title ? task.title : 'Untitled Task'}</h4>
              <p class="task-description">${not empty task.description ? task.description : 'No description available'}</p>

              <div class="due-date">
                <i class="far fa-calendar-alt" aria-hidden="true"></i>
                <fmt:formatDate value="${task.dueDate}" pattern="dd MMM yyyy" />
              </div>

              <div class="priority-badge ${fn:toLowerCase(task.priority)}">
                  ${task.priority}
              </div>

              <div class="task-footer">
                <div class="task-tags">
                  <c:forEach var="tag" items="${task.tags}">
                    <span class="task-tag" data-tag="${fn:toLowerCase(tag)}">${tag}</span>
                  </c:forEach>
                  <c:if test="${empty task.tags}">
                    <span class="no-tags">No tags</span>
                  </c:if>
                </div>
              </div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </div>

    <div class="add-column">
      <button class="btn-add-column" aria-label="Add new column">
        <i class="fas fa-plus"></i>
      </button>
    </div>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>