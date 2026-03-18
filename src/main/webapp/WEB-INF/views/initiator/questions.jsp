<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp"/>

<h2>Questions – ${survey.title}</h2>

<!-- Add question form -->
<div class="card">
    <h3>Add Question</h3>
    <form action="${pageContext.request.contextPath}/initiator/surveys/${survey.surveyId}/questions/add" method="post" id="addQForm">
        <div class="form-row">
            <div class="form-group" style="flex:3">
                <label>Question Text *</label>
                <input type="text" name="questionText" required placeholder="Enter your question">
            </div>
            <div class="form-group" style="flex:1">
                <label>Type *</label>
                <select name="questionType" id="qType" class="form-control" onchange="toggleOptions(this.value)">
                    <option value="SINGLE_CHOICE">Single Choice</option>
                    <option value="MULTIPLE_CHOICE">Multiple Choice</option>
                    <option value="TEXT">Open Text</option>
                </select>
            </div>
        </div>

        <div id="optionsSection">
            <label>Answer Options</label>
            <div id="optionsList">
                <input type="text" name="options" placeholder="Option 1" class="option-input">
                <input type="text" name="options" placeholder="Option 2" class="option-input">
                <input type="text" name="options" placeholder="Option 3" class="option-input">
                <input type="text" name="options" placeholder="Option 4" class="option-input">
            </div>
            <button type="button" onclick="addOption()" class="btn btn-secondary btn-sm" style="margin-top:8px">+ Add Option</button>
        </div>

        <button type="submit" class="btn btn-primary" style="margin-top:16px">➕ Add Question</button>
    </form>
</div>

<!-- Existing questions -->
<h3>Questions (${questions.size()})</h3>
<c:forEach var="q" items="${questions}" varStatus="st">
    <div class="question-card">
        <div class="question-header">
            <span class="q-num">Q${st.index + 1}</span>
            <span>${q.questionText}</span>
            <span class="badge">${q.questionType}</span>
            <a href="${pageContext.request.contextPath}/initiator/surveys/${survey.surveyId}/questions/${q.questionId}/delete"
               class="btn btn-danger btn-sm" onclick="return confirm('Delete this question?')" style="margin-left:auto">🗑</a>
        </div>
        <c:if test="${not empty q.options}">
            <ul class="options-list">
                <c:forEach var="o" items="${q.options}">
                    <li>${o.optionText}</li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</c:forEach>

<div style="margin-top:24px">
    <a href="${pageContext.request.contextPath}/initiator/dashboard" class="btn btn-secondary">← Back to Dashboard</a>
</div>

<script>
function toggleOptions(type) {
    document.getElementById('optionsSection').style.display = type === 'TEXT' ? 'none' : 'block';
}
function addOption() {
    const input = document.createElement('input');
    input.type = 'text';
    input.name = 'options';
    input.placeholder = 'Option ' + (document.querySelectorAll('.option-input').length + 1);
    input.className = 'option-input';
    document.getElementById('optionsList').appendChild(input);
}
</script>

<jsp:include page="../common/footer.jsp"/>
