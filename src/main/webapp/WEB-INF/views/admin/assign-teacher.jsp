<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Assign Teacher to: ${course.courseName}</h2>

<div class="card">
    <form action="${pageContext.request.contextPath}/admin/courses/assign" method="post">
        <input type="hidden" name="courseId" value="${course.courseId}">
        <div class="form-group">
            <label>Select Teacher</label>
            <select name="teacherId" class="form-control" required>
                <option value="">-- Select Teacher --</option>
                <c:forEach var="t" items="${allTeachers}">
                    <c:if test="${t.status == 'ACTIVE'}">
                        <option value="${t.userId}">${t.fullName} (${t.username})</option>
                    </c:if>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Assign</button>
        <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">Cancel</a>
    </form>
</div>

<jsp:include page="../common/footer.jsp"/>
