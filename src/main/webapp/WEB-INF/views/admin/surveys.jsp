<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>
<h2>All Surveys</h2>
<table class="table">
    <thead><tr><th>Title</th><th>Course</th><th>Creator</th><th>Access</th><th>Status</th><th>Created</th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${surveys}">
        <tr>
            <td>${s.title}</td>
            <td>${s.courseName}</td>
            <td>${s.creatorName}</td>
            <td><span class="badge">${s.accessType}</span></td>
            <td><span class="badge badge-${s.status == 'PUBLISHED' ? 'success' : s.status == 'DRAFT' ? 'warning' : 'secondary'}">${s.status}</span></td>
            <td>${s.createdAt}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<jsp:include page="../common/footer.jsp"/>
