package com.courseeval.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private int roleId;
    private String roleName;
    private String status;       // PENDING, ACTIVE, REJECTED
    private Timestamp createdAt;

    // ---- Constructors ----
    public User() {}

    // ---- Getters & Setters ----
    public int getUserId()               { return userId; }
    public void setUserId(int userId)    { this.userId = userId; }

    public String getUsername()               { return username; }
    public void setUsername(String username)  { this.username = username; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPassword()                { return password; }
    public void setPassword(String password)   { this.password = password; }

    public String getFullName()                { return fullName; }
    public void setFullName(String fullName)   { this.fullName = fullName; }

    public int getRoleId()               { return roleId; }
    public void setRoleId(int roleId)    { this.roleId = roleId; }

    public String getRoleName()                { return roleName; }
    public void setRoleName(String roleName)   { this.roleName = roleName; }

    public String getStatus()              { return status; }
    public void setStatus(String status)   { this.status = status; }

    public Timestamp getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }
}
