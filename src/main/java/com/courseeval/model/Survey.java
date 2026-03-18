package com.courseeval.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Survey {
    private int surveyId;
    private String title;
    private String description;
    private int courseId;
    private String courseName;
    private int createdBy;
    private String creatorName;
    private String accessType;   // AUTHENTICATED, GUEST, BOTH
    private String status;       // DRAFT, PUBLISHED, CLOSED
    private Date startDate;
    private Date endDate;
    private Timestamp createdAt;
    private List<SurveyQuestion> questions;

    public Survey() {}

    // Auto-generated getters/setters
    public int getSurveyId()               { return surveyId; }
    public void setSurveyId(int id)        { this.surveyId = id; }

    public String getTitle()               { return title; }
    public void setTitle(String title)     { this.title = title; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public int getCourseId()                   { return courseId; }
    public void setCourseId(int courseId)      { this.courseId = courseId; }

    public String getCourseName()                  { return courseName; }
    public void setCourseName(String courseName)   { this.courseName = courseName; }

    public int getCreatedBy()                  { return createdBy; }
    public void setCreatedBy(int createdBy)    { this.createdBy = createdBy; }

    public String getCreatorName()                     { return creatorName; }
    public void setCreatorName(String creatorName)     { this.creatorName = creatorName; }

    public String getAccessType()                    { return accessType; }
    public void setAccessType(String accessType)     { this.accessType = accessType; }

    public String getStatus()              { return status; }
    public void setStatus(String status)   { this.status = status; }

    public Date getStartDate()             { return startDate; }
    public void setStartDate(Date d)       { this.startDate = d; }

    public Date getEndDate()               { return endDate; }
    public void setEndDate(Date d)         { this.endDate = d; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp t)            { this.createdAt = t; }

    public List<SurveyQuestion> getQuestions()           { return questions; }
    public void setQuestions(List<SurveyQuestion> q)    { this.questions = q; }
}
