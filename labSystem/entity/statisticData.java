package labSystem.entity;

import java.util.Date;
import java.util.List;

/**
 * 统计数据实体类
 * 用于封装各种统计查询结果
 */
public class statisticData {
    
    // === 设备使用统计 ===
    public static class DeviceUsageStats {
        private Integer deviceId;
        private String deviceCode;
        private String deviceName;
        private String categoryName;
        private Integer uniqueUsers;        // 独立用户数
        private Integer totalReservations;  // 总预约数
        private Double totalHours;          // 总使用小时数
        private Double avgHoursPerUse;      // 平均每次使用小时数
        private Double usageRate;           // 使用率（百分比）
        private Double usagePerDay;         // 日均使用次数
        
        // 构造方法
        public DeviceUsageStats() {}
        
        public DeviceUsageStats(Integer deviceId, String deviceCode, String deviceName, 
                               Integer totalReservations, Double totalHours) {
            this.deviceId = deviceId;
            this.deviceCode = deviceCode;
            this.deviceName = deviceName;
            this.totalReservations = totalReservations;
            this.totalHours = totalHours;
        }
        
        // Getter和Setter
        public Integer getDeviceId() { return deviceId; }
        public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }
        
        public String getDeviceCode() { return deviceCode; }
        public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }
        
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public Integer getUniqueUsers() { return uniqueUsers; }
        public void setUniqueUsers(Integer uniqueUsers) { this.uniqueUsers = uniqueUsers; }
        
        public Integer getTotalReservations() { return totalReservations; }
        public void setTotalReservations(Integer totalReservations) { this.totalReservations = totalReservations; }
        
        public Double getTotalHours() { return totalHours; }
        public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }
        
        public Double getAvgHoursPerUse() { return avgHoursPerUse; }
        public void setAvgHoursPerUse(Double avgHoursPerUse) { this.avgHoursPerUse = avgHoursPerUse; }
        
        public Double getUsageRate() { return usageRate; }
        public void setUsageRate(Double usageRate) { this.usageRate = usageRate; }
        
        public Double getUsagePerDay() { return usagePerDay; }
        public void setUsagePerDay(Double usagePerDay) { this.usagePerDay = usagePerDay; }
        
        // 显示方法
        public String getFormattedUsageRate() {
            if (usageRate == null) return "0%";
            return String.format("%.1f%%", usageRate * 100);
        }
        
        public String getFormattedTotalHours() {
            if (totalHours == null) return "0小时";
            if (totalHours < 24) return String.format("%.1f小时", totalHours);
            return String.format("%.1f天", totalHours / 24);
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d次预约，%.1f小时", deviceName, totalReservations, totalHours);
        }
    }
    
    // === 预约趋势统计 ===
    public static class ReservationTrend {
        private String period;          // 时间段：2024-01, 2024-01-15, 周一等
        private Date startDate;
        private Date endDate;
        private Integer totalReservations;  // 总预约数
        private Integer approvedCount;      // 批准数
        private Integer rejectedCount;      // 拒绝数
        private Integer pendingCount;       // 待审核数
        private Double approvalRate;        // 批准率
        private Double avgDuration;         // 平均时长（小时）
        
        // Getter和Setter
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        
        public Date getEndDate(){ return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        
        public Integer getTotalReservations() { return totalReservations; }
        public void setTotalReservations(Integer totalReservations) { this.totalReservations = totalReservations; }
        
        public Integer getApprovedCount() { return approvedCount; }
        public void setApprovedCount(Integer approvedCount) { this.approvedCount = approvedCount; }
        
        public Integer getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Integer rejectedCount) { this.rejectedCount = rejectedCount; }
        
        public Integer getPendingCount() { return pendingCount; }
        public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }
        
        public Double getApprovalRate() { return approvalRate; }
        public void setApprovalRate(Double approvalRate) { this.approvalRate = approvalRate; }
        
        public Double getAvgDuration() { return avgDuration; }
        public void setAvgDuration(Double avgDuration) { this.avgDuration = avgDuration; }
        
        // 计算字段
        public Integer getCompletedCount() {
            return (approvedCount != null ? approvedCount : 0) + 
                   (rejectedCount != null ? rejectedCount : 0);
        }
        
        public String getFormattedApprovalRate() {
            if (approvalRate == null) return "0%";
            return String.format("%.1f%%", approvalRate * 100);
        }
        
        public String getFormattedAvgDuration() {
            if (avgDuration == null) return "0小时";
            return String.format("%.1f小时", avgDuration);
        }
        
        public String getTimeRangeFormatted() {
            if (startDate == null || endDate == null) return period;
            return String.format("%tF 至 %tF", startDate, endDate);
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d预约，批准率%s", 
                period, totalReservations, getFormattedApprovalRate());
        }
    }
    
    // === 报修统计 ===
    public static class RepairStats {
        private String deviceName;
        private Integer deviceId;
        private Integer totalRepairs;       // 总报修数
        private Integer pendingCount;       // 待处理数
        private Integer processingCount;    // 处理中数
        private Integer resolvedCount;      // 已解决数
        private Integer closedCount;        // 已关闭数
        private Double avgResolutionTime;   // 平均解决时间（小时）
        
        // Getter和Setter
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        
        public Integer getDeviceId() { return deviceId; }
        public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }
        
        public Integer getTotalRepairs() { return totalRepairs; }
        public void setTotalRepairs(Integer totalRepairs) { this.totalRepairs = totalRepairs; }
        
        public Integer getPendingCount() { return pendingCount; }
        public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }
        
        public Integer getProcessingCount() { return processingCount; }
        public void setProcessingCount(Integer processingCount) { this.processingCount = processingCount; }
        
        public Integer getResolvedCount() { return resolvedCount; }
        public void setResolvedCount(Integer resolvedCount) { this.resolvedCount = resolvedCount; }
        
        public Integer getClosedCount() { return closedCount; }
        public void setClosedCount(Integer closedCount) { this.closedCount = closedCount; }
        
        public Double getAvgResolutionTime() { return avgResolutionTime; }
        public void setAvgResolutionTime(Double avgResolutionTime) { this.avgResolutionTime = avgResolutionTime; }
        
        // 计算字段
        public Integer getOpenRepairs() {
            return (pendingCount != null ? pendingCount : 0) + 
                   (processingCount != null ? processingCount : 0);
        }
        
        public Integer getCompletedRepairs() {
            return (resolvedCount != null ? resolvedCount : 0) + 
                   (closedCount != null ? closedCount : 0);
        }
        
        public Double getResolutionRate() {
            if (totalRepairs == null || totalRepairs == 0) return 0.0;
            return (double) getCompletedRepairs() / totalRepairs * 100;
        }
        
        public String getFormattedAvgResolutionTime() {
            if (avgResolutionTime == null) return "无数据";
            if (avgResolutionTime < 24) return String.format("%.1f小时", avgResolutionTime);
            return String.format("%.1f天", avgResolutionTime / 24);
        }
        
        public String getFormattedResolutionRate() {
            return String.format("%.1f%%", getResolutionRate());
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d次报修，解决率%s", 
                deviceName, totalRepairs, getFormattedResolutionRate());
        }
    }
    
    // === 用户活跃度统计 ===
    public static class UserActivityStats {
        private Integer userId;
        private String username;
        private String realName;
        private String userRole;
        private String department;
        private Integer devicesUsed;        // 使用设备数
        private Integer totalReservations;  // 总预约数
        private Integer repairReports;      // 报修报告数
        private Date lastReservationTime;   // 最后预约时间
        private Date lastLoginTime;         // 最后登录时间
        
        // Getter和Setter
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        
        public String getUserRole() { return userRole; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public Integer getDevicesUsed() { return devicesUsed; }
        public void setDevicesUsed(Integer devicesUsed) { this.devicesUsed = devicesUsed; }
        
        public Integer getTotalReservations() { return totalReservations; }
        public void setTotalReservations(Integer totalReservations) { this.totalReservations = totalReservations; }
        
        public Integer getRepairReports() { return repairReports; }
        public void setRepairReports(Integer repairReports) { this.repairReports = repairReports; }
        
        public Date getLastReservationTime() { return lastReservationTime; }
        public void setLastReservationTime(Date lastReservationTime) { this.lastReservationTime = lastReservationTime; }
        
        public Date getLastLoginTime() { return lastLoginTime; }
        public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
        
        // 计算字段
        public Integer getTotalActivities() {
            return (totalReservations != null ? totalReservations : 0) + 
                   (repairReports != null ? repairReports : 0);
        }
        
        public String getActivityLevel() {
            int activities = getTotalActivities();
            if (activities == 0) return "无活动";
            if (activities < 5) return "低活跃";
            if (activities < 20) return "中活跃";
            return "高活跃";
        }
        
        public String getRoleDisplayName() {
            if (userRole == null) return "未知";
            switch (userRole) {
                case "admin": return "管理员";
                case "teacher": return "教师";
                case "student": return "学生";
                default: return userRole;
            }
        }
        
        public Long getDaysSinceLastActivity() {
            Date lastActivity = getLastActivity();
            if (lastActivity == null) return null;
            
            long diff = new Date().getTime() - lastActivity.getTime();
            return diff / (1000 * 60 * 60 * 24);
        }
        
        private Date getLastActivity() {
            if (lastReservationTime == null && lastLoginTime == null) return null;
            if (lastReservationTime == null) return lastLoginTime;
            if (lastLoginTime == null) return lastReservationTime;
            return lastReservationTime.after(lastLoginTime) ? lastReservationTime : lastLoginTime;
        }
        
        public String getLastActivityFormatted() {
            Long days = getDaysSinceLastActivity();
            if (days == null) return "从未活动";
            if (days == 0) return "今天";
            if (days == 1) return "昨天";
            if (days < 7) return days + "天前";
            if (days < 30) return (days / 7) + "周前";
            return (days / 30) + "月前";
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d预约，%d报修，%s", 
                realName, totalReservations, repairReports, getActivityLevel());
        }
    }
    
    // === 系统概况统计 ===
    public static class SystemOverview {
        private Integer totalUsers;           // 总用户数
        private Integer activeUsers;          // 活跃用户数
        private Integer totalDevices;         // 总设备数
        private Integer availableDevices;     // 可用设备数
        private Integer todayReservations;    // 今日预约数
        private Integer pendingReservations;  // 待审核预约数
        private Integer openRepairs;          // 待处理报修数
        private Integer recentReservations;   // 近期预约数（7天）
        private Integer recentRepairs;        // 近期报修数（7天）
        private Date generatedAt;             // 统计时间
        
        // 构造方法
        public SystemOverview() {
            this.generatedAt = new Date();
        }
        
        // Getter和Setter
        public Integer getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }
        
        public Integer getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Integer activeUsers) { this.activeUsers = activeUsers; }
        
        public Integer getTotalDevices() { return totalDevices; }
        public void setTotalDevices(Integer totalDevices) { this.totalDevices = totalDevices; }
        
        public Integer getAvailableDevices() { return availableDevices; }
        public void setAvailableDevices(Integer availableDevices) { this.availableDevices = availableDevices; }
        
        public Integer getTodayReservations() { return todayReservations; }
        public void setTodayReservations(Integer todayReservations) { this.todayReservations = todayReservations; }
        
        public Integer getPendingReservations() { return pendingReservations; }
        public void setPendingReservations(Integer pendingReservations) { this.pendingReservations = pendingReservations; }
        
        public Integer getOpenRepairs() { return openRepairs; }
        public void setOpenRepairs(Integer openRepairs) { this.openRepairs = openRepairs; }
        
        public Integer getRecentReservations() { return recentReservations; }
        public void setRecentReservations(Integer recentReservations) { this.recentReservations = recentReservations; }
        
        public Integer getRecentRepairs() { return recentRepairs; }
        public void setRecentRepairs(Integer recentRepairs) { this.recentRepairs = recentRepairs; }
        
        public Date getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
        
        // 计算字段
        public Double getDeviceAvailabilityRate() {
            if (totalDevices == null || totalDevices == 0) return 0.0;
            if (availableDevices == null) return 0.0;
            return (double) availableDevices / totalDevices * 100;
        }
        
        public Double getUserActivityRate() {
            if (totalUsers == null || totalUsers == 0) return 0.0;
            if (activeUsers == null) return 0.0;
            return (double) activeUsers / totalUsers * 100;
        }
        
        public Double getReservationProcessingRate() {
            if (todayReservations == null || todayReservations == 0) return 0.0;
            if (pendingReservations == null) return 100.0;
            return (double) (todayReservations - pendingReservations) / todayReservations * 100;
        }
        
        public String getFormattedDeviceAvailabilityRate() {
            return String.format("%.1f%%", getDeviceAvailabilityRate());
        }
        
        public String getFormattedUserActivityRate() {
            return String.format("%.1f%%", getUserActivityRate());
        }
        
        public String getFormattedReservationProcessingRate() {
            return String.format("%.1f%%", getReservationProcessingRate());
        }
        
        public String getGeneratedTime() {
            return String.format("%tF %tT", generatedAt, generatedAt);
        }
        
        @Override
        public String toString() {
            return String.format("系统概况: %d用户(%.1f%%活跃)，%d设备(%.1f%%可用)，今日%d预约，%d待处理报修", 
                totalUsers, getUserActivityRate(), totalDevices, getDeviceAvailabilityRate(), 
                todayReservations, openRepairs);
        }
    }
    
    // === 时间段统计请求 ===
    public static class TimeRangeRequest {
        private Date startDate;
        private Date endDate;
        private String groupBy;  // day, week, month, year
        private String deviceType;
        private String userRole;
        
        // Getter和Setter
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        
        public Date getEndDate() { return endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        
        public String getGroupBy() { return groupBy; }
        public void setGroupBy(String groupBy) { this.groupBy = groupBy; }
        
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        
        public String getUserRole() { return userRole; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
        
        // 验证方法
        public boolean isValid() {
            return startDate != null && endDate != null && 
                   endDate.after(startDate) && groupBy != null;
        }
        
        public String getTimeRangeFormatted() {
            if (startDate == null || endDate == null) return "时间未设置";
            return String.format("%tF 至 %tF", startDate, endDate);
        }
    }
    
    // === 统计结果包装 ===
    public static class StatisticsResult<T> {
        private boolean success;
        private String message;
        private List<T> data;
        private Integer totalCount;
        private Date generatedAt;
        
        // 构造方法
        public StatisticsResult() {
            this.generatedAt = new Date();
        }
        
        public StatisticsResult(List<T> data) {
            this();
            this.success = true;
            this.data = data;
            this.totalCount = data != null ? data.size() : 0;
        }
        
        public StatisticsResult(boolean success, String message) {
            this();
            this.success = success;
            this.message = message;
        }
        
        // Getter和Setter
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public List<T> getData() { return data; }
        public void setData(List<T> data) { 
            this.data = data; 
            this.totalCount = data != null ? data.size() : 0;
        }
        
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        
        public Date getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
        
        // 工具方法
        public static <T> StatisticsResult<T> success(List<T> data) {
            return new StatisticsResult<>(data);
        }
        
        public static <T> StatisticsResult<T> error(String message) {
            return new StatisticsResult<>(false, message);
        }
        
        public boolean hasData() {
            return data != null && !data.isEmpty();
        }
        
        public String getGeneratedTime() {
            return String.format("%tF %tT", generatedAt, generatedAt);
        }
    }
    
    // === 图表数据点 ===
    public static class ChartDataPoint {
        private String label;      // X轴标签
        private Number value;      // Y轴数值
        private String category;   // 分类（用于多系列图表）
        private String color;      // 颜色
        private Object extraData;  // 额外数据
        
        // 构造方法
        public ChartDataPoint() {}
        
        public ChartDataPoint(String label, Number value) {
            this.label = label;
            this.value = value;
        }
        
        public ChartDataPoint(String label, Number value, String category) {
            this(label, value);
            this.category = category;
        }
        
        // Getter和Setter
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public Number getValue() { return value; }
        public void setValue(Number value) { this.value = value; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        public Object getExtraData() { return extraData; }
        public void setExtraData(Object extraData) { this.extraData = extraData; }
        
        // 工具方法
        public Double getDoubleValue() {
            if (value == null) return 0.0;
            return value.doubleValue();
        }
        
        public Integer getIntegerValue() {
            if (value == null) return 0;
            return value.intValue();
        }
    }
    
    // === 饼图数据 ===
    public static class PieChartData {
        private String name;     // 名称
        private Number value;    // 数值
        private Double percentage; // 百分比
        private String color;    // 颜色
        
        // 构造方法
        public PieChartData() {}
        
        public PieChartData(String name, Number value) {
            this.name = name;
            this.value = value;
        }
        
        // Getter和Setter
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Number getValue() { return value; }
        public void setValue(Number value) { this.value = value; }
        
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        
        // 计算百分比
        public void calculatePercentage(double total) {
            if (total == 0) {
                this.percentage = 0.0;
            } else {
                this.percentage = (value != null ? value.doubleValue() : 0) / total * 100;
            }
        }
        
        public String getFormattedPercentage() {
            if (percentage == null) return "0%";
            return String.format("%.1f%%", percentage);
        }
    }



    
    // 新增：用于承载系统概览数据的属性
    private SystemOverview systemOverview;

    // === 新增的 Getter 和 Setter ===
    public SystemOverview getSystemOverview() {
        return systemOverview;
    }

    public void setSystemOverview(SystemOverview systemOverview) {
        this.systemOverview = systemOverview;
    }
}