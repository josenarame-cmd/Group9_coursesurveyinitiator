<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>${survey.surveyId == 0 ? 'Create New Survey' : 'Edit Survey'}</h2>

<div class="card">
    <form action="${pageContext.request.contextPath}/initiator/surveys/save" method="post">
        <input type="hidden" name="surveyId" value="${survey.surveyId}">

        <div class="form-group">
            <label>Survey Title *</label>
            <input type="text" name="title" value="${survey.title}" required placeholder="e.g. End of Term Evaluation – CS101">
        </div>
        <div class="form-group">
            <label>Description</label>
            <textarea name="description" rows="3" placeholder="Brief description of this survey">${survey.description}</textarea>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>Course *</label>
                <select name="courseId" required class="form-control">
                    <option value="">-- Select Course --</option>
                    <c:forEach var="c" items="${courses}">
                        <option value="${c.courseId}" ${survey.courseId == c.courseId ? 'selected' : ''}>${c.courseCode} – ${c.courseName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>Access Type *</label>
                <select name="accessType" class="form-control">
                    <option value="BOTH"      ${survey.accessType == 'BOTH'          ? 'selected' : ''}>Both (Auth + Guest)</option>
                    <option value="AUTHENTICATED" ${survey.accessType == 'AUTHENTICATED' ? 'selected' : ''}>Authenticated Only</option>
                    <option value="GUEST"     ${survey.accessType == 'GUEST'         ? 'selected' : ''}>Guest Only</option>
                </select>
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>Start Date</label>
                <input type="date" name="startDate" value="${survey.startDate}">
            </div>
            <div class="form-group">
                <label>End Date</label>
                <input type="date" name="endDate" value="${survey.endDate}">
            </div>
        </div>

        <button type="submit" class="btn btn-primary">💾 Save &amp; Continue</button>
        <a href="${pageContext.request.contextPath}/initiator/dashboard" class="btn btn-secondary">Cancel</a>
    </form>
</div>

<jsp:include page="../common/footer.jsp"/>
