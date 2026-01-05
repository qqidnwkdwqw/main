package labSystem.entity;
import java.util.Date;

/** 
 * 报修实体类
 * 状态：pending(待处理), processing(处理中), resolved(已解决), closed(已关闭)
 * 严重程度：low(低), medium(中), high(高), critical(严重)
 */
public class Repair {
    private Integer repairId;
    private Integer deviceId;          // 设备ID
    private Integer userId;            // 报修用户ID
    private String title;              // 报修标题
    private String description;        // 问题描述
    private String attachmentUrl;      // 附件URL
    private String status;             // 处理状态
    private String severity;           // 严重程度
    private String repairNotes;        // 维修说明
    private Integer resolvedBy;        // 处理人ID
    private Date resolvedAt;           // 解决时间
    private Date createdAt;
    private Date updatedAt;
    
    // === 关联信息 ===
    private String deviceCode;         // 设备编号
    private String deviceName;         // 设备名称
    private String userName;           // 报修人用户名
    private String userRealName;       // 报修人真实姓名
    private String resolverName;       // 处理人姓名
    
    // === 状态检查方法 ===
    public boolean isPending() {
        return "pending".equals(status);
    }
    
    public boolean isProcessing() {
        return "processing".equals(status);
    }
    
    public boolean isResolved() {
        return "resolved".equals(status);
    }
    
    public boolean isClosed() {
        return "closed".equals(status);
    }
    
    public boolean isOpen() {
        return isPending() || isProcessing();
    }
    
    public boolean canBeProcessed() {
        return isPending();
    }
    
    public boolean canBeResolved() {
        return isPending() || isProcessing();
    }
    
    public boolean canBeClosed() {
        return isResolved();
    }
    
    // === 严重程度检查 ===
    public boolean isLowSeverity() {
        return "low".equals(severity);
    }
    
    public boolean isMediumSeverity() {
        return "medium".equals(severity);
    }
    
    public boolean isHighSeverity() {
        return "high".equals(severity);
    }
    
    public boolean isCriticalSeverity() {
        return "critical".equals(severity);
    }
    
    // === 构造方法 ===
    public Repair() {
        this.status = "pending";
        this.severity = "medium";
        this.createdAt = new Date();
    }
    
    public Repair(Integer deviceId, Integer userId, String title, String description) {
        this();
        this.deviceId = deviceId;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }
    
    // === Getter和Setter ===
    public Integer getRepairId() { 
        return repairId; 
    }
    
    public void setRepairId(Integer repairId) { 
        this.repairId = repairId; 
    }
    
    public Integer getDeviceId() { 
        return deviceId; 
    }
    
    public void setDeviceId(Integer deviceId) { 
        this.deviceId = deviceId; 
    }
    
    public Integer getUserId() { 
        return userId; 
    }
    
    public void setUserId(Integer userId) { 
        this.userId = userId; 
    }
    
    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getAttachmentUrl() { 
        return attachmentUrl; 
    }
    
    public void setAttachmentUrl(String attachmentUrl) { 
        this.attachmentUrl = attachmentUrl; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public String getSeverity() { 
        return severity; 
    }
    
    public void setSeverity(String severity) { 
        this.severity = severity; 
    }
    
    public String getRepairNotes() { 
        return repairNotes; 
    }
    
    public void setRepairNotes(String repairNotes) { 
        this.repairNotes = repairNotes; 
    }
    
    public Integer getResolvedBy() { 
        return resolvedBy; 
    }
    
    public void setResolvedBy(Integer resolvedBy) { 
        this.resolvedBy = resolvedBy; 
    }
    
    public Date getResolvedAt() { 
        return resolvedAt; 
    }
    
    public void setResolvedAt(Date resolvedAt) { 
        this.resolvedAt = resolvedAt; 
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
    
    public String getResolverName() { 
        return resolverName; 
    }
    
    public void setResolverName(String resolverName) { 
        this.resolverName = resolverName; 
    }
    
    // === 显示方法 ===
    public String getStatusDisplayName() {
        switch (status) {
            case "pending": return "待处理";
            case "processing": return "处理中";
            case "resolved": return "已解决";
            case "closed": return "已关闭";
            default: return status;
        }
    }
    
    public String getSeverityDisplayName() {
        switch (severity) {
            case "low": return "低";
            case "medium": return "中";
            case "high": return "高";
            case "critical": return "严重";
            default: return severity;
        }
    }
    
    public String getSeverityColor() {
        switch (severity) {
            case "low": return "green";
            case "medium": return "orange";
            case "high": return "red";
            case "critical": return "darkred";
            default: return "black";
        }
    }
    
    public String getStatusColor() {
        switch (status) {
            case "pending": return "orange";
            case "processing": return "blue";
            case "resolved": return "green";
            case "closed": return "gray";
            default: return "black";
        }
    }
    
    public String getTimeInfo() {
        if (resolvedAt != null) {
            long hours = (resolvedAt.getTime() - createdAt.getTime()) / (1000 * 60 * 60);
            if (hours < 24) return hours + "小时解决";
            return String.format("%d天%d小时解决", hours / 24, hours % 24);
        }
        long hours = (new Date().getTime() - createdAt.getTime()) / (1000 * 60 * 60);
        if (hours < 24) return "已提交" + hours + "小时";
        return "已提交" + (hours / 24) + "天";
    }
    
    @Override
    public String toString() {
        return String.format("报修#%d: %s - %s [%s]", 
            repairId, 
            deviceName != null ? deviceName : "设备" + deviceId,
            title,
            getStatusDisplayName());
    }
    
    // === 工具方法 ===
    public String getBriefInfo() {
        return String.format("%s 报修 %s", 
            userRealName != null ? userRealName : "用户" + userId,
            deviceName != null ? deviceName : "设备" + deviceId);
    }
    
    public boolean requiresUrgentAttention() {
        return isHighSeverity() || isCriticalSeverity();
    }
}

