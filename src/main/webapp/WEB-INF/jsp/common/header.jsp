<%--
  Created by IntelliJ IDEA.
  User: appiah
  Date: 17/05/2025
  Time: 16:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty pageTitle ? pageTitle : 'NovaTech Task Manager'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
    <!-- Font Awesome for icons (optional) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="${pageContext.request.contextPath}/js/script.js" defer></script>
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/tasks">NovaTech TMS</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link ${param.action == null ? 'active' : ''}" href="${pageContext.request.contextPath}/tasks">All Tasks</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.action == 'new' ? 'active' : ''}" href="${pageContext.request.contextPath}/tasks?action=new">New Task</a>
                </li>
            </ul>
            <c:if test="${not empty currentUser}">
                <div class="dropdown text-end">
                    <a href="#" class="text-white text-decoration-none dropdown-toggle" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fas fa-user-circle me-1"></i> ${currentUser.firstName}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                    </ul>
                </div>
            </c:if>
        </div>
    </div>
</nav>
<div class="container mt-4 flex-grow-1">

<!-- Display flash messages/alerts if any -->
<c:if test="${not empty message}">
    <div class="alert alert-${messageType} alert-dismissible fade show" role="alert">
            ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>