<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Course Management</h2>
<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>

<!-- Add/Edit Form -->
<div class="card">
    <h3>Add New Course</h3>
    <form action="${pageContext.request.contextPath}/admin/courses/save" method="post">
        <input type="hidden" name="courseId" value="${course.courseId}">
        <div class="form-row">
            <div class="form-group">
                <label>Course Code</label>
                <input type="text" name="courseCode" value="${course.courseCode}" required placeholder="e.g. CS101">
            </div>
            <div class="form-group">
                <label>Course Name</label>
                <input type="text" name="courseName" value="${course.courseName}" required placeholder="Course title">
            </div>
        </div>
        <div class="form-group">
            <label>Description</label>
            <textarea name="description" rows="3" placeholder="Course description">${course.description}</textarea>
        </div>
        <button type="submit" class="btn btn-primary">💾 Save Course</button>
    </form>
</div>

<!-- Course List -->
<h3>All Courses</h3>
<table class="table">
    <thead>
        <tr><th>Code</th><th>Name</th><th>Description</th><th>Actions</th></tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${courses}">
        <tr>
            <td><strong>${c.courseCode}</strong></td>
            <td>${c.courseName}</td>
            <td>${c.description}</td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/courses/assign/${c.courseId}" class="btn btn-info btn-sm">👤 Assign Teacher</a>
                <a href="${pageContext.request.contextPath}/admin/courses/delete/${c.courseId}"
                   class="btn btn-danger btn-sm"
                   onclick="return confirm('Delete this course?')">🗑 Delete</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../common/footer.jsp"/>
