<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register – CourseEval</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<div class="auth-card">
    <h1>📋 CourseEval</h1>
    <h2>${empty registerTitle ? 'Teacher Registration' : registerTitle}</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

<%--    <form action="${pageContext.request.contextPath}/register" method="post">--%>
    <form action="${pageContext.request.contextPath}${empty registerAction ? '/register' : registerAction}" method="post">
        <div class="form-group">
            <label>Full Name</label>
            <input type="text" name="fullName" required placeholder="Your full name">
        </div>
        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required placeholder="Choose a username">
        </div>
        <div class="form-group">
            <label>Email</label>
            <input type="email" name="email" required placeholder="Your email">
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="Choose a password" minlength="6">
        </div>
        <button type="submit" class="btn btn-primary btn-block">Register</button>
    </form>
    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/login">Back to Login</a>
    </div>
</div>
</body>
</html>
