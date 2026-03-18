

// ============================================================
// Course.java
// ============================================================
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int createdBy;
    private Timestamp createdAt;

    public Course() {}

    public int getCourseId()               { return courseId; }
    public void setCourseId(int courseId)  { this.courseId = courseId; }

    public String getCourseCode()                  { return courseCode; }
    public void setCourseCode(String courseCode)   { this.courseCode = courseCode; }

    public String getCourseName()                  { return courseName; }
    public void setCourseName(String courseName)   { this.courseName = courseName; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public int getCreatedBy()                { return createdBy; }
    public void setCreatedBy(int createdBy)  { this.createdBy = createdBy; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }
}
