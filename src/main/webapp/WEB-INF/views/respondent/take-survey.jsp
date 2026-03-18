<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>${survey.title}</h2>
<p class="muted">Course: <strong>${survey.courseName}</strong></p>
<c:if test="${not empty survey.description}"><p>${survey.description}</p></c:if>

<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<form action="${pageContext.request.contextPath}/survey/${survey.surveyId}/submit" method="post" id="surveyForm">

    <c:if test="${loggedUser == null}">
        <div class="card" style="margin-bottom:20px">
            <div class="form-group">
                <label>Your Email Address * <span class="muted">(required to receive confirmation)</span></label>
                <input type="email" name="guestEmail" required placeholder="you@example.com" class="form-control">
            </div>
        </div>
    </c:if>

    <c:forEach var="q" items="${questions}" varStatus="st">
        <div class="question-card">
            <p class="question-text"><strong>Q${st.index + 1}.</strong> ${q.questionText}
                <c:if test="${q.questionType != 'TEXT'}"><span class="badge" style="font-size:11px">Required</span></c:if>
            </p>

            <c:choose>
                <%-- Single choice (Radio Buttons) --%>
                <c:when test="${q.questionType == 'SINGLE_CHOICE'}">
                    <div class="options-group">
                        <c:forEach var="o" items="${q.options}">
                            <label class="radio-label">
                                <input type="radio" name="q_${q.questionId}" value="${o.optionId}" required>
                                    ${o.optionText}
                            </label>
                        </c:forEach>
                    </div>
                </c:when>

                <%-- Multiple choice (Checkboxes) --%>
                <c:when test="${q.questionType == 'MULTIPLE_CHOICE'}">
                    <div class="options-group">
                        <c:forEach var="o" items="${q.options}">
                            <label class="checkbox-label" style="display: block; margin-bottom: 8px; cursor: pointer;">
                                <input type="checkbox" name="q_${q.questionId}" value="${o.optionId}">
                                    ${o.optionText}
                            </label>
                        </c:forEach>
                    </div>
                </c:when>

                <%-- Open text (Textarea) --%>
                <c:otherwise>
                    <textarea name="q_${q.questionId}" rows="3" class="form-control"
                              placeholder="Your answer…"></textarea>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>

    <div style="margin-top:24px">
        <button type="submit" class="btn btn-primary">📨 Submit Survey</button>
        <a href="${pageContext.request.contextPath}/survey/list" class="btn btn-secondary">Cancel</a>
    </div>
</form>

<jsp:include page="../common/footer.jsp"/>