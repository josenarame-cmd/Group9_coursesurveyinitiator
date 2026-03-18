<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Teacher Dashboard</h2>

<h3>My Courses</h3>
<div class="stats-grid">
    <c:forEach var="c" items="${courses}">
        <div class="stat-card"><h3>${c.courseCode}</h3><p>${c.courseName}</p></div>
    </c:forEach>
</div>

<h3>Surveys for My Courses</h3>
<table class="table">
    <thead><tr><th>Title</th><th>Course</th><th>Status</th><th>Responses</th><th>Actions</th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${surveys}">
        <tr>
            <td>${s.title}</td>
            <td>${s.courseName}</td>
            <td><span class="badge badge-${s.status == 'PUBLISHED' ? 'success' : s.status == 'DRAFT' ? 'warning' : 'secondary'}">${s.status}</span></td>
            <td>–</td>
            <td>
                <a href="${pageContext.request.contextPath}/teacher/surveys/${s.surveyId}/results" class="btn btn-info btn-sm">View Results</a>
                <c:if test="${s.status == 'PUBLISHED'}">
                    <a href="${pageContext.request.contextPath}/survey/${s.surveyId}/take" class="btn btn-secondary btn-sm" target="_blank">Preview</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../common/footer.jsp"/>
