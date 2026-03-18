<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login – CourseEval</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<div class="auth-card">
    <h1>📋 CourseEval</h1>
    <h2>Sign In</h2>

    <c:if test="${not empty flashAttributes.error}">
        <div class="alert alert-error">${flashAttributes.error}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required placeholder="Enter username">
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="Enter password">
        </div>
        <button type="submit" class="btn btn-primary btn-block">Login</button>
    </form>

    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/register-student">Register as Student</a> |
        <a href="${pageContext.request.contextPath}/register">Register as Teacher</a> |
        <a href="${pageContext.request.contextPath}/register-initiator">Register as Initiator</a> |
        <a href="${pageContext.request.contextPath}/register-admin">Register as Admin</a><br><br>
        <a href="${pageContext.request.contextPath}/survey/list">Browse Surveys as Guest</a>
    </div>
</div>
</body>
</html>
