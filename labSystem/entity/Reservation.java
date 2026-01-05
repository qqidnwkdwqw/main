package labSystem.entity;
import java.util.Date;

/**
 * 预约实体类
 * 状态：pending(待审核), approved(已批准), rejected(已拒绝),
 *       completed(已完成), cancelled(已取消), expired(已过期)  
 */
public class Reservation {
    private Integer reservationId;
    private Integer userId;            // 预约用户ID
    private Integer deviceId;          // 设备ID
    private String purpose;            // 使用目的
    private Date startTime;            // 开始时间
    private Date endTime;              // 结束时间
    private String status;             // 预约状态
    private String adminNotes;         // 管理员备注
    private String userNotes;          // 用户备注
    private Date actualStartTime;      // 实际开始时间
    private Date actualEndTime;        // 实际结束时间
    private Date createdAt;
    private Date updatedAt;
    
    // === 关联信息（查询时填充） ===
    private String userName;           // 用户名
    private String userRealName;       // 用户真实姓名
    private String deviceCode;         // 设备编号
    private String deviceName;         // 设备名称
    private String deviceLocation;     // 设备位置
    
    // === 状态检查方法 ===
    public boolean isPending() {
        return "pending".equals(status);
    }
    
    public boolean isApproved() {
        return "approved".equals(status);
    }
    
    public boolean isRejected() {
        return "rejected".equals(status);
    }
    
    public boolean isCompleted() {
        return "completed".equals(status);
    }
    
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }
    
    public boolean isExpired() {
        return "expired".equals(status);
    }
    
    public boolean isActive() {
        return isPending() || isApproved();
    }
    
    public boolean canBeReviewed() {
        return isPending();
    }
    
    public boolean canBeCancelled() {
        return isPending() || isApproved();
    }
    
    public boolean canBeCompleted() {
        return isApproved() && new Date().after(startTime);
    }
    
    // === 时间检查方法 ===
    public boolean isInProgress() {
        Date now = new Date();
        return isApproved() && startTime != null && endTime != null &&
               now.after(startTime) && now.before(endTime);
    }
    
    public boolean isUpcoming() {
        Date now = new Date();
        return isApproved() && startTime != null && startTime.after(now);
    }
    
    public boolean isPast() {
        Date now = new Date();
        return endTime != null && endTime.before(now);
    }
    
    public Long getDurationHours() {
        if (startTime == null || endTime == null) return 0L;
        long diff = endTime.getTime() - startTime.getTime();
        return diff / (1000 * 60 * 60);
    }
    
    // === 构造方法 ===
    public Reservation() {
        this.status = "pending";
        this.createdAt = new Date();
    }
    
    public Reservation(Integer userId, Integer deviceId, String purpose, 
                      Date startTime, Date endTime) {
        this();
        this.userId = userId;
        this.deviceId = deviceId;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // === Getter和Setter ===
    public Integer getReservationId() { 
        return reservationId; 
    }
    
    public void setReservationId(Integer reservationId) { 
        this.reservationId = reservationId; 
    }
    
    public Integer getUserId() { 
        return userId; 
    }
    
    public void setUserId(Integer userId) { 
        this.userId = userId; 
    }
    
    public Integer getDeviceId() { 
        return deviceId; 
    }
    
    public void setDeviceId(Integer deviceId) { 
        this.deviceId = deviceId; 
    }
    
    public String getPurpose() { 
        return purpose; 
    }
    
    public void setPurpose(String purpose) { 
        this.purpose = purpose; 
    }
    
    public Date getStartTime() { 
        return startTime; 
    }
    
    public void setStartTime(Date startTime) { 
        this.startTime = startTime; 
    }
    
    public Date getEndTime() { 
        return endTime; 
    }
    
    public void setEndTime(Date endTime) { 
        this.endTime = endTime; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getAdminNotes() { 
        return adminNotes; 
    }
    
    public void setAdminNotes(String adminNotes) { 
        this.adminNotes = adminNotes; 
    }
    
    public String getUserNotes() { 
        return userNotes; 
    }
    
    public void setUserNotes(String userNotes) { 
        this.userNotes = userNotes; 
    }
    
    public Date getActualStartTime() { 
        return actualStartTime; 
    }
    
    public void setActualStartTime(Date actualStartTime) { 
        this.actualStartTime = actualStartTime; 
    }
    
    public Date getActualEndTime() { 
        return actualEndTime; 
    }
    
    public void setActualEndTime(Date actualEndTime) { 
        this.actualEndTime = actualEndTime; 
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
    
    // === 关联信息Getter和Setter ===
    public String getUserName() { 
        return userName; 
    }
    
    public void setUserName(String userName) { 
        this.userName = userName; 
    }
    
    public String getUserRealName() { 
        return userRealName; 
    }
    
    public void setUserRealName(String userRealName) { 
        this.userRealName = userRealName; 
    }
    
    public String getDeviceCode() { 
        return deviceCode; 
    }
    
    public void setDeviceCode(String deviceCode) { 
        this.deviceCode = deviceCode; 
    }
    
    public String getDeviceName() { 
        return deviceName; 
    }
    
    public void setDeviceName(String deviceName) { 
        this.deviceName = deviceName; 
    }
    
    public String getDeviceLocation() { 
        return deviceLocation; 
    }
    
    public void setDeviceLocation(String deviceLocation) { 
        this.deviceLocation = deviceLocation; 
    }
    
    // === 显示方法 ===
    public String getStatusDisplayName() {
        switch (status) {
            case "pending": return "待审核";
            case "approved": return "已批准";
            case "rejected": return "已拒绝";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            case "expired": return "已过期";
            default: return status;
        }
    }
    
    public String getStatusColor() {
        switch (status) {
            case "pending": return "orange";
            case "approved": return "green";
            case "rejected": return "red";
            case "completed": return "blue";
            case "cancelled": return "gray";
            case "expired": return "lightgray";
            default: return "black";
        }
    }
    
    public String getTimeRange() {
        if (startTime == null || endTime == null) return "时间未设置";
        return String.format("%tF %tR - %tR", startTime, startTime, endTime);
    }
    
    public String getFormattedDuration() {
        Long hours = getDurationHours();
        if (hours == 0) return "0小时";
        if (hours < 24) return hours + "小时";
        return String.format("%d天%d小时", hours / 24, hours % 24);
    }
    
    @Override
    public String toString() {
        return String.format("预约#%d: %s - %s [%s]", 
            reservationId, 
            deviceName != null ? deviceName : "设备" + deviceId,
            userRealName != null ? userRealName : "用户" + userId,
            getStatusDisplayName());
    }
    
    // === 工具方法 ===
    public String getBriefInfo() {
        return String.format("%s 预约 %s", 
            userRealName != null ? userRealName : "用户" + userId,
            deviceName != null ? deviceName : "设备" + deviceId);
    }
    
    public boolean hasConflict(Date otherStart, Date otherEnd) {
        if (startTime == null || endTime == null || otherStart == null || otherEnd == null) {
            return false;
        }
        return !(endTime.before(otherStart) || startTime.after(otherEnd));
    }
}

