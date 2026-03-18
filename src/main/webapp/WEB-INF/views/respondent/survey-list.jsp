<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Available Surveys</h2>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<c:choose>
    <c:when test="${empty surveys}">
        <div class="empty-state">
            <p>No surveys are currently available.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="survey-grid">
            <c:forEach var="s" items="${surveys}">
                <div class="survey-card">
                    <h3>${s.title}</h3>
                    <p class="muted">📚 ${s.courseName}</p>
                    <p>${s.description}</p>
                    <div class="survey-meta">
                        <span class="badge">🔒 ${s.accessType}</span>
                        <c:if test="${not empty s.endDate}">
                            <span class="muted">Closes: ${s.endDate}</span>
                        </c:if>
                    </div>
                    <a href="${pageContext.request.contextPath}/survey/${s.surveyId}/take"
                       class="btn btn-primary btn-block" style="margin-top:12px">Take Survey</a>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp"/>
