<%--
  Created by IntelliJ IDEA.
  User: appia
  Date: 17/05/2025
  Time: 16:44
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h1 class="mb-4">Task Details</h1>

<div class="card">
  <div class="card-header">
    <h5>${task.title}</h5>
  </div>
  <div class="card-body">
    <div class="mb-3">
      <strong>Description:</strong>
      <p>${task.description}</p>
    </div>
    <div class="mb-3">
      <strong>Due Date:</strong>
      <p><fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" /></p>
    </div>
    <div class="mb-3">
      <strong>Status:</strong>
      <span class="badge ${task.status == 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
        ${task.status}
      </span>
    </div>
  </div>
  <div class="card-footer">
    <a href="tasks?action=edit&id=${task.id}" class="btn btn-primary">Edit</a>
    <a href="tasks" class="btn btn-secondary">Back to List</a>
  </div>
</div>

<%@ include file="../common/footer.jsp" %>