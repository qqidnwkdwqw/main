package labSystem.entity;

import java.util.Date;

/**
 * 用户实体类
 * 支持三种角色：admin(管理员), teacher(教师), student(学生)
 */
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String department;
    private String userRole;        // admin, teacher, student
    private String status;          // active, inactive, banned
    private String avatarUrl;
    private Date lastLoginTime;
    private Date createdAt;
    private Date updatedAt;
    
    // === 权限检查方法 ===
    public boolean isAdmin() {
        return "admin".equals(userRole);
    }
    
    public boolean isTeacher() {
        return "teacher".equals(userRole);
    }
    
    public boolean isStudent() {
        return "student".equals(userRole);
    }
    
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public boolean isInactive() {
        return "inactive".equals(status);
    }
    
    public boolean isBanned() {
        return "banned".equals(status);
    }
    
    public boolean canManageDevices() {
        return isAdmin();
    }
    
    public boolean canReviewReservations() {
        return isAdmin();
    }
    
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    // === 构造方法 ===
    public User() {
        this.status = "active";
    }
    
    public User(String username, String password, String realName, String userRole) {
        this();
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.userRole = userRole;
    }
    
    public User(String username, String password, String realName, String email, 
                String phone, String department, String userRole) {
        this(username, password, realName, userRole);
        this.email = email;
        this.phone = phone;
        this.department = department;
    }
    
    // === Getter和Setter ===
    public Integer getUserId() { 
        return userId; 
    }
    
    public void setUserId(Integer userId) { 
        this.userId = userId; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public String getRealName() { 
        return realName; 
    }
    
    public void setRealName(String realName) { 
        this.realName = realName; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    
    public String getDepartment() { 
        return department; 
    }
    
    public void setDepartment(String department) { 
        this.department = department; 
    }
    
    public String getUserRole() { 
        return userRole; 
    }
    
    public void setUserRole(String userRole) { 
        this.userRole = userRole; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getAvatarUrl() { 
        return avatarUrl; 
    }
    
    public void setAvatarUrl(String avatarUrl) { 
        this.avatarUrl = avatarUrl; 
    }
    
    public Date getLastLoginTime() { 
        return lastLoginTime; 
    }
    
    public void setLastLoginTime(Date lastLoginTime) { 
        this.lastLoginTime = lastLoginTime; 
    }
    
    public Date getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Date createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public Date getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(Date updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    // === 显示方法 ===
    public String getRoleDisplayName() {
        switch (userRole) {
            case "admin": return "管理员";
            case "teacher": return "教师";
            case "student": return "学生";
            default: return userRole;
        }
    }
    
    public String getStatusDisplayName() {
        switch (status) {
            case "active": return "正常";
            case "inactive": return "停用";
            case "banned": return "封禁";
            default: return status;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", realName, username, getRoleDisplayName());
    }
    
    // === 工具方法 ===
    public String toSimpleString() {
        return username + " - " + realName;
    }
    
    public String getDisplayInfo() {
        return String.format("%s [%s] - %s", realName, username, department);
    }
}

