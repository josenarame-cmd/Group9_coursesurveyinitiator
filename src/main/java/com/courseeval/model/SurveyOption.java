package com.courseeval.model;

public class SurveyOption {
    private int optionId;
    private int questionId;
    private String optionText;
    private int orderNum;
    private int responseCount;  // used in results view

    public SurveyOption() {}

    public int getOptionId()               { return optionId; }
    public void setOptionId(int id)        { this.optionId = id; }

    public int getQuestionId()                 { return questionId; }
    public void setQuestionId(int questionId)  { this.questionId = questionId; }

    public String getOptionText()                  { return optionText; }
    public void setOptionText(String optionText)   { this.optionText = optionText; }

    public int getOrderNum()               { return orderNum; }
    public void setOrderNum(int orderNum)  { this.orderNum = orderNum; }

    public int getResponseCount()                    { return responseCount; }
    public void setResponseCount(int responseCount)  { this.responseCount = responseCount; }
}
