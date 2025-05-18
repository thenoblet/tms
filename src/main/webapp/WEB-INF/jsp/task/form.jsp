<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<h1 class="mb-4">${empty task.id ? 'Add New' : 'Edit'} Task</h1>

<c:if test="${not empty error}">
  <div class="alert alert-danger">${error}</div>
</c:if>

<form action="${pageContext.request.contextPath}/tasks" method="post" class="needs-validation" novalidate>
  <input type="hidden" name="id" value="${task.id}">
  <input type="hidden" name="action" value="${empty task.id ? 'create' : 'update'}">

  <div class="mb-3">
    <label for="title" class="form-label">Title *</label>
    <input type="text" class="form-control" id="title" name="title"
           value="${task.title}" required>
    <div class="invalid-feedback">Please provide a title.</div>
  </div>

  <div class="mb-3">
    <label for="description" class="form-label">Description *</label>
    <textarea class="form-control" id="description" name="description"
              rows="3" required>${task.description}</textarea>
    <div class="invalid-feedback">Please provide a description.</div>
  </div>

  <div class="mb-3">
    <label for="priority" class="form-label">Priority *</label>
    <select class="form-select" id="priority" name="priority" required>
      <option value="">Select priority</option>
      <option value="HIGH" ${task.priority == 'HIGH' ? 'selected' : ''}>High</option>
      <option value="MEDIUM" ${task.priority == 'MEDIUM' ? 'selected' : ''}>Medium</option>
      <option value="LOW" ${task.priority == 'LOW' ? 'selected' : ''}>Low</option>
    </select>
    <div class="invalid-feedback">Please select a priority.</div>
  </div>

  <div class="mb-3">
    <label for="dueDate" class="form-label">Due Date *</label>
    <input type="date" class="form-control" id="dueDate" name="dueDate"
           value="<fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />" required>
    <div class="invalid-feedback">Please provide a due date.</div>
  </div>

  <div class="mb-3">
    <label class="form-label">Status *</label>
    <div class="form-check">
      <input class="form-check-input" type="radio" name="status"
             id="statusPending" value="PENDING"
      ${empty task.status || task.status == 'PENDING' ? 'checked' : ''} required>
      <label class="form-check-label" for="statusPending">Pending</label>
    </div>
    <div class="form-check">
      <input class="form-check-input" type="radio" name="status"
             id="statusInProgress" value="IN_PROGRESS"
      ${task.status == 'IN_PROGRESS' ? 'checked' : ''}>
      <label class="form-check-label" for="statusInProgress">In Progress</label>
    </div>
    <div class="form-check">
      <input class="form-check-input" type="radio" name="status"
             id="statusCompleted" value="COMPLETED"
      ${task.status == 'COMPLETED' ? 'checked' : ''}>
      <label class="form-check-label" for="statusCompleted">Completed</label>
      <div class="invalid-feedback">Please select a status.</div>
    </div>
  </div>

  <div class="mb-3">
    <label for="tags" class="form-label">Tags (comma separated)</label>
    <input type="text" class="form-control" id="tags" name="tags"
           value="${not empty tagsString ? tagsString : fn:join(task.tags, ',')}">
    <div class="form-text">Enter tags separated by commas (e.g., "urgent, client, project")</div>
  </div>

  <button type="submit" class="btn btn-primary">Save</button>
  <a href="${pageContext.request.contextPath}/tasks" class="btn btn-secondary ms-2">Cancel</a>
</form>



<%@ include file="../common/footer.jsp" %>