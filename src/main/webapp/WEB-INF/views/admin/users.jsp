<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>
<h2>System Users</h2>
<table class="table">
    <thead><tr><th>Name</th><th>Username</th><th>Email</th><th>Role</th><th>Status</th><th>Joined</th></tr></thead>
    <tbody>
    <c:forEach var="u" items="${users}">
        <tr>
            <td>${u.fullName}</td>
            <td>${u.username}</td>
            <td>${u.email}</td>
            <td><span class="badge">${u.roleName}</span></td>
            <td><span class="badge badge-${u.status == 'ACTIVE' ? 'success' : u.status == 'PENDING' ? 'warning' : 'danger'}">${u.status}</span></td>
            <td>${u.createdAt}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<jsp:include page="../common/footer.jsp"/>
