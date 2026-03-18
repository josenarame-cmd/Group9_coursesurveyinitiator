package com.courseeval.model;

import java.util.List;

public class SurveyQuestion {
    private int questionId;
    private int surveyId;
    private String questionText;
    private String questionType;  // SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT
    private int orderNum;
    private List<SurveyOption> options;

    // New field to hold open-text answers
    private List<String> textResponses;

    public SurveyQuestion() {}

    public int getQuestionId()                 { return questionId; }
    public void setQuestionId(int id)          { this.questionId = id; }

    public int getSurveyId()                   { return surveyId; }
    public void setSurveyId(int surveyId)      { this.surveyId = surveyId; }

    public String getQuestionText()                        { return questionText; }
    public void setQuestionText(String questionText)       { this.questionText = questionText; }

    public String getQuestionType()                        { return questionType; }
    public void setQuestionType(String questionType)       { this.questionType = questionType; }

    public int getOrderNum()               { return orderNum; }
    public void setOrderNum(int orderNum)  { this.orderNum = orderNum; }

    public List<SurveyOption> getOptions()             { return options; }
    public void setOptions(List<SurveyOption> options) { this.options = options; }

    // Getters and setters for text responses
    public List<String> getTextResponses() { return textResponses; }
    public void setTextResponses(List<String> textResponses) { this.textResponses = textResponses; }
}