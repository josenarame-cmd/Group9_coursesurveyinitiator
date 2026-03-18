<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Initiator Dashboard</h2>
<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>

<div style="margin-bottom:16px">
    <a href="${pageContext.request.contextPath}/initiator/surveys/new" class="btn btn-primary">+ New Survey</a>
</div>

<h3>My Surveys</h3>
<table class="table">
    <thead>
        <tr><th>Title</th><th>Course</th><th>Access</th><th>Status</th><th>Actions</th></tr>
    </thead>
    <tbody>
    <c:forEach var="s" items="${surveys}">
        <tr>
            <td>${s.title}</td>
            <td>${s.courseName}</td>
            <td>${s.accessType}</td>
            <td><span class="badge badge-${s.status == 'PUBLISHED' ? 'success' : s.status == 'DRAFT' ? 'warning' : 'secondary'}">${s.status}</span></td>
            <td>
                <a href="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/questions" class="btn btn-info btn-sm">Questions</a>
                <a href="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/edit" class="btn btn-secondary btn-sm">Edit</a>
                <a href="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/results" class="btn btn-secondary btn-sm">Results</a>
                <c:if test="${s.status == 'DRAFT'}">
                    <form action="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/publish" method="post" style="display:inline">
                        <button class="btn btn-success btn-sm">Publish</button>
                    </form>
                </c:if>
                <c:if test="${s.status == 'PUBLISHED'}">
                    <form action="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/close" method="post" style="display:inline">
                        <button class="btn btn-warning btn-sm">Close</button>
                    </form>
                </c:if>
                <a href="${pageContext.request.contextPath}/initiator/surveys/${s.surveyId}/delete"
                   class="btn btn-danger btn-sm"
                   onclick="return confirm('Delete this survey?')">Delete</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<jsp:include page="../common/footer.jsp"/>
